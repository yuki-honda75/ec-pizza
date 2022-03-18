package com.example.controller;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpSession;

import com.example.domain.Item;
import com.example.domain.LoginUser;
import com.example.domain.Order;
import com.example.domain.OrderItem;
import com.example.domain.OrderTopping;
import com.example.domain.Topping;
import com.example.form.InsertOrderForm;
import com.example.form.UpdateOrderForm;
import com.example.service.ItemService;
import com.example.service.OrderService;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 
 * @author hondayuki
 *
 */
@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private HttpSession session;

    @ModelAttribute
    public InsertOrderForm setUpInsertOrderForm() {
        return new InsertOrderForm();
    }
    @ModelAttribute
    public UpdateOrderForm setUpUpdateOrderForm() {
        return new UpdateOrderForm();
    }

    /**
     * カート追加処理
     * 
     */
    @RequestMapping("/incart")
    public String inCart(InsertOrderForm form, @AuthenticationPrincipal LoginUser loginUser) {
        
        //注文商品情報を作成
        OrderItem orderItem = new OrderItem();
        
        orderItem.setItemId(form.getIntItemId());
        orderItem.setQuantity(form.getIntQuantity());
        Integer size = form.getIntSizeNum();
        if (size == 0) {
            orderItem.setSize('M');
        } else {
            orderItem.setSize('L');
        }
        //商品情報
        Item item = itemService.showDetail(orderItem.getItemId());
        orderItem.setItem(item);
        //トッピング情報
        List<OrderTopping> orderToppingList = new ArrayList<>();
        List<Topping> toppingList = itemService.selectTopping(form.getOrderToppingIdList());
        if (toppingList != null) {
            for (Topping topping : toppingList) {
                OrderTopping orderTopping = new OrderTopping();
                orderTopping.setToppingId(topping.getId());
                orderTopping.setTopping(topping);
                orderToppingList.add(orderTopping);
            }
        }
        orderItem.setOrderToppingList(orderToppingList);
        
        //ログイン状態で分岐
        if (loginUser == null) {
            //ログインしていないとき（セッションに保存
            Order order = (Order) session.getAttribute("order");
            
            List<OrderItem> orderItemList = null;
            if (order == null) {
                order = new Order();
                order.setStatus(0);
                orderItemList = new ArrayList<>();
            } else {
                orderItemList = order.getOrderItemList();
            }
            orderItemList.add(orderItem);
            order.setOrderItemList(orderItemList);
            order.setTotalPrice(order.getCalcTotalPrice() + order.getTax());
            session.setAttribute("order", order);
        } else {
            //ログインしているとき（データベースに保存
            Integer userId = loginUser.getUser().getId();
            Order order = orderService.showCart(userId);
            if (order == null) {
                order = new Order();
                List<OrderItem> orderItemList = new ArrayList<>();
                orderItemList.add(orderItem);
                order.setUserId(userId);
                order.setStatus(0);
                order.setOrderItemList(orderItemList);
                order.setTotalPrice(order.getCalcTotalPrice() + order.getTax());
                orderService.createCart(order);
            } else {
                Integer orderId = order.getId();
                order.getOrderItemList().add(orderItem);
                order.setTotalPrice(order.getCalcTotalPrice() + order.getTax());
                orderService.setTotalPrice(order.getTotalPrice(), orderId);
                orderService.intoCart(orderItem, orderId);
            }
        }
        return "redirect:/item/showList";
    }

    /**
     * カートの中身を表示する
     * 
     * @param loginUser
     * @param model
     * @return
     */
    @RequestMapping("/cartList")
    public String cartList(@AuthenticationPrincipal LoginUser loginUser, Model model) {
        Order order = null;
        if (loginUser == null) {
            order = (Order)session.getAttribute("order");
        } else {
            order = orderService.showCart(loginUser.getUser().getId());
        }
        model.addAttribute("order", order);
        return "cart_list";
    }

    /**
     * 注文確認画面を表示する
     * 
     * @param orderId
     * @param model
     * @return
     */
    @RequestMapping("/confirm")
    public String confirm(UpdateOrderForm form, Integer orderId, Model model) {
        Order order = orderService.cartConfirm(orderId);
        if (form.getDeliveryTime() == null) {
            form.setDeliveryTime("10");
        }
        if (form.getPaymentMethod() == null) {
            form.setPaymentMethod("1");
        }
        
        model.addAttribute("order", order);
        return "order_confirm";
    }

    /**
     * 注文を確定する
     * 
     * @param form
     * @param result
     * @param model
     * @return
     * @throws ParseException
     */
    @RequestMapping("/post")
    public String order(@Validated UpdateOrderForm form, BindingResult result,Model model) throws ParseException {
        //送信時の日付をlong型で取得
		long today = new Date().getTime();
		
		//入力された日付と時刻を文字列として結合したあとにlong型でフォーマット
		long delivaryDateTimeLong = 0;
        
        if (!form.getDeliveryDate().isEmpty()) {
			String delivaryDateTime = form.getDeliveryDate() + " " + form.getDeliveryTime();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh");
			delivaryDateTimeLong = sdf.parse(delivaryDateTime).getTime();
			
			//時刻を比較して時間差を抽出
			long diff = delivaryDateTimeLong - today;
			TimeUnit time = TimeUnit.HOURS;
			long difference = time.convert(diff, TimeUnit.MILLISECONDS);
			
			//時間差が3時間より小さけれればエラー文を格納
			if (difference < 3) {
				result.rejectValue("deliveryDate", null, "今から3時間後の日時をご入力ください");
			}
            
        }
        
        if (result.hasErrors()) {
            return confirm(form, form.getId(), model);
        }

        java.sql.Date sqldDate = new java.sql.Date(today);
        Timestamp deliveryTimestamp = new Timestamp(delivaryDateTimeLong);

        Order order = new Order();
        BeanUtils.copyProperties(form, order);
        order.setOrderDate(sqldDate);
        order.setDeliveryTime(deliveryTimestamp);
        order.setPaymentMethod(form.getIntPaymentmethod());
        if (order.getPaymentMethod() == 1) {
            order.setStatus(1);
        } else if (order.getPaymentMethod() == 2) {
            order.setStatus(2);
        }
        orderService.updateOrder(order);

        return "redirect:/order/finished";
    }

    /**
     * 注文完了画面を表示する
     * 
     * @return
     */
    @RequestMapping("/finished")
    public String finished() {
        return "order_finished";
    }

    /**
     * 注文履歴を表示する
     * 
     * @param loginUser
     * @param model
     * @return
     */
    @RequestMapping("/history")
    public String showHistory(@AuthenticationPrincipal LoginUser loginUser, Model model) {
        
        if (loginUser == null) {
            
        }
        List<Order> orderList = orderService.showHistory(loginUser.getUser().getId());
        model.addAttribute("orderList", orderList);

        return "order_history";
    }

    @RequestMapping("/remove")
    public String removeOrderItem(@AuthenticationPrincipal LoginUser loginUser, Integer orderItemId, int index) {
    	
        if (loginUser == null) {
            Order order = (Order)session.getAttribute("order");
            List<OrderItem> orderItemList = order.getOrderItemList();
            orderItemList.remove(index);
        } else {
            orderService.removeOrderItem(orderItemId);
        }

        return "redirect:/order/cartList";
    }
}

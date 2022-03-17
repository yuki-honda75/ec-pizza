package com.example.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.example.domain.Item;
import com.example.domain.LoginUser;
import com.example.domain.Order;
import com.example.domain.OrderItem;
import com.example.domain.OrderTopping;
import com.example.domain.Topping;
import com.example.form.InsertOrderForm;
import com.example.service.ItemService;
import com.example.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
}

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

    @RequestMapping("/incart")
    public String inCart(InsertOrderForm form, List<Integer> toppingIdList, @AuthenticationPrincipal LoginUser loginUser) {
        Order order = (Order) session.getAttribute("order");
        
        //注文商品情報を作成
        OrderItem orderItem = new OrderItem();
        
        orderItem.setItemId(form.getIntItemId());
        orderItem.setQuantity(form.getIntQuantity());
        orderItem.setSize(form.getCharSize());
        //商品情報
        Item item = itemService.showDetail(orderItem.getItemId());
        orderItem.setItem(item);
        //トッピング情報
        List<OrderTopping> orderToppingList = new ArrayList<>();
        List<Topping> toppingList = itemService.selectTopping(toppingIdList);
        for (Topping topping : toppingList) {
            OrderTopping orderTopping = new OrderTopping();
            orderTopping.setToppingId(topping.getId());
            orderTopping.setTopping(topping);
            orderToppingList.add(orderTopping);
        }
        orderItem.setOrderToppingList(orderToppingList);

        if (loginUser == null) {
            //ログインしていないとき
            
            if (order == null) {
                order = new Order();
                List<OrderItem> orderItemList = new ArrayList<>();
                orderItemList.add(orderItem);
                order.setStatus(0);
                order.setOrderItemList(orderItemList);
                order.setTotalPrice(1);
            } else {
                List<OrderItem> orderItemList = order.getOrderItemList();
                orderItemList.add(orderItem);
                order.setOrderItemList(orderItemList);
                order.setTotalPrice(1);
                
            }
            session.setAttribute("order", order);
        } else {
            //ログインしているとき（データベース
        }
        return "cart_list";
    }
}

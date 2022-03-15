package com.example.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.example.domain.Item;
import com.example.domain.LoginUser;
import com.example.domain.Order;
import com.example.domain.OrderItem;
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
    public String inCart(InsertOrderForm form, List<Integer> toppingId, @AuthenticationPrincipal LoginUser loginUser) {
        Order order = (Order) session.getAttribute("order");
        
        if (loginUser == null) {
            //ログインしていないとき
            OrderItem orderItem = new OrderItem();
            orderItem.setItemId(form.getIntItemId());
            orderItem.setQuantity(form.getIntQuantity());
            orderItem.setSize(form.getCharSize());
            Item item = itemService.showDetail(orderItem.getItemId());
            orderItem.setItem(item);
            if (order == null) {
                order = new Order();
                session.setAttribute("order", order);
            } else {

            }
        }
        return "cart_list";
    }
}

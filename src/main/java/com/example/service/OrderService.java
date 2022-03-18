package com.example.service;

import java.util.List;

import com.example.domain.Order;
import com.example.domain.OrderItem;
import com.example.repository.OrderRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * @author hondayuki
 *
 */
@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    public Order checkOrder(Integer userId) {
        return orderRepository.findExistOrder(userId);
    }

    public void createCart(Order order) {
        orderRepository.insert(order);
    }

    public void intoCart(OrderItem orderItem, Integer orderId) {
        orderRepository.insertOrderItem(orderItem, orderId);
    }

    public void setTotalPrice(Integer totalPrice, Integer orderId) {
        orderRepository.updateTotalPrice(totalPrice, orderId);
    }

    public Order showCart(Integer userId) {
        List<Order> orderList = orderRepository.findByCondision(null, userId, 0);
        if (orderList == null) {
            return null;
        }
        return orderList.get(0);
    }

    public List<Order> showHistory(Integer userId) {
        return orderRepository.findByCondision(null, userId, null);
    }

    public Order cartConfirm(Integer orderId) {
        List<Order> orderList = orderRepository.findByCondision(orderId, null, 0);
        return orderList.get(0);
    }

    public void updateOrder(Order order) {
        orderRepository.update(order);
    }
}

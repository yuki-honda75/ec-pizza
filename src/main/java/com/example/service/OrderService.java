package com.example.service;

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

    public void setTotalPrice(Integer subTotalPrice, Integer orderId) {
        orderRepository.updateTotalPrice(subTotalPrice, orderId);
    }
}

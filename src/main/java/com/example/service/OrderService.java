package com.example.service;

import java.text.SimpleDateFormat;
import java.util.List;

import com.example.domain.Order;
import com.example.domain.OrderItem;
import com.example.repository.OrderRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
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
    @Autowired
    private MailSender mailSender;

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

    /**
     * 
     * 
     * @param order
     */
    public void updateOrder(Order order) {
        orderRepository.update(order);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 hh時");
		String stringPaymentMethod = null;
		if (order.getPaymentMethod() == 1) {
			stringPaymentMethod = "代金引換";
		} else if(order.getPaymentMethod() == 2) {
			stringPaymentMethod = "クレジットカード";
		}
		String text = "ご注文金額:" + String.format("%,d", order.getTotalPrice()) + "円\n"
				+ "お支払い方法:" + stringPaymentMethod + "\n"
				+ "配達日時:" + sdf.format(order.getDeliveryTime()) + "\n"
				+ "ご注文内容:\n";
		for (OrderItem orderItem : order.getOrderItemList()) {
			text += orderItem.getItem().getName() + " " + orderItem.getQuantity() + "個\n";
		}

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("pupi.yh@gmail.com");
        msg.setTo(order.getDestinationEmail());
        msg.setSubject("ご注文を承りました。");
        msg.setText(text);
        try {
			mailSender.send(msg);
		} catch (MailException e) {
			e.printStackTrace();
		}
    }
    public void removeOrderItem(Integer orderItemId) {
        orderRepository.deleteOrderItem(orderItemId);
    }
}

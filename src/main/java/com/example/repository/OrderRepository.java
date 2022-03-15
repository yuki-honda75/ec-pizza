package com.example.repository;

import java.util.ArrayList;
import java.util.List;

import com.example.domain.Item;
import com.example.domain.Order;
import com.example.domain.OrderItem;
import com.example.domain.OrderTopping;
import com.example.domain.Topping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * 
 * @author hondayuki
 *
 */
@Repository
public class OrderRepository {
    @Autowired
    private NamedParameterJdbcTemplate template;

    private static final ResultSetExtractor<List<Order>> ORDER_RESULT_SET_EXTRACTOR = (rs) -> {
        List<Order> orderList = new ArrayList<>();
        List<OrderItem> orderItemList = null;
        List<OrderTopping> orderToppingList = null;
        int preOId = 0;
        int preOIID = 0;
        
        while (rs.next()) {
            int nowOId = rs.getInt("order_id");
            int nowOIId = rs.getInt("order_topping_id");

            if (preOId != nowOId) {
                Order order = new Order();
                order.setId(nowOId);
                order.setUserId(rs.getInt("user_id"));
                order.setStatus(rs.getInt("status"));
                order.setTotalPrice(rs.getInt("total_price"));
                order.setOrderDate(rs.getDate("order_date"));
                order.setDesinationName(rs.getString("desination_name"));
                order.setDesinationEmail(rs.getString("desination_email"));
                order.setDesinationZipcode(rs.getString("desination_zipcode"));
                order.setDesinationAddress(rs.getString("desination_address"));
                order.setDesinationTel(rs.getString("desination_tel"));
                order.setDeliveryTime(rs.getTimestamp("delivery_time"));
                order.setPaymentMethod(rs.getInt("payment_method"));
                orderItemList = new ArrayList<>();
                order.setOrderItemList(orderItemList);
            }

            if (preOIID != nowOIId) {
                OrderItem orderItem = new OrderItem();
                orderItem.setId(nowOIId);
                orderItem.setItemId(rs.getInt("item_id"));
                orderItem.setQuantity(rs.getInt("quantity"));
                orderItem.setSize(rs.getString("size").toCharArray()[0]);
                Item item = new Item();
                item.setName(rs.getString("item_name"));
                item.setDescription(rs.getString("description"));
                item.setPriceM(rs.getInt("price_M"));
                item.setPriceL(rs.getInt("price_L"));
                item.setImagePath(rs.getString("image_path"));
                orderItem.setItem(item);
                orderToppingList = new ArrayList<>();
                orderItem.setOrderToppingList(orderToppingList);

                orderItemList.add(orderItem);
            }

            if (rs.getInt("order_topping_id") > 0) {
                OrderTopping orderTopping = new OrderTopping();
                orderTopping.setId(rs.getInt("order_topping_id"));
                orderTopping.setToppingId(rs.getInt("topping_id"));
                Topping topping = new Topping();
                topping.setName(rs.getString("topping_name"));
                topping.setPriceM(rs.getInt("price_M"));
                topping.setPriceL(rs.getInt("price_L"));
                orderTopping.setTopping(topping);

                orderToppingList.add(orderTopping);
            }

            preOId = nowOId;
            preOIID = nowOIId;
        }

        return orderList;
    };
}

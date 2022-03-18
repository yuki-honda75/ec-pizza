package com.example.repository;

import java.util.ArrayList;
import java.util.List;

import com.example.domain.Item;
import com.example.domain.Order;
import com.example.domain.OrderItem;
import com.example.domain.OrderTopping;
import com.example.domain.Topping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
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
            int nowOIId = rs.getInt("order_item_id");

            if (preOId != nowOId) {
                Order order = new Order();
                order.setId(nowOId);
                order.setUserId(rs.getInt("user_id"));
                order.setStatus(rs.getInt("status"));
                order.setTotalPrice(rs.getInt("total_price"));
                order.setOrderDate(rs.getDate("order_date"));
                order.setDestinationName(rs.getString("destination_name"));
                order.setDestinationEmail(rs.getString("destination_email"));
                order.setDestinationZipcode(rs.getString("destination_zipcode"));
                order.setDestinationAddress(rs.getString("destination_address"));
                order.setDestinationTel(rs.getString("destination_tel"));
                order.setDeliveryTime(rs.getTimestamp("delivery_time"));
                order.setPaymentMethod(rs.getInt("payment_method"));
                orderItemList = new ArrayList<>();
                order.setOrderItemList(orderItemList);
                orderList.add(order);
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
                item.setPriceM(rs.getInt("item_price_M"));
                item.setPriceL(rs.getInt("item_price_L"));
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
                topping.setPriceM(rs.getInt("topping_price_M"));
                topping.setPriceL(rs.getInt("topping_price_L"));
                orderTopping.setTopping(topping);

                orderToppingList.add(orderTopping);
            }
            
            preOId = nowOId;
            preOIID = nowOIId;
        }
        
        return orderList;
    };
    
    private static final RowMapper<Order> ORDER_ROW_MAPPER = new BeanPropertyRowMapper<>(Order.class);

    /**
     * カートの存在チェック
     * 
     * @param userId
     * @return
     */
    public Order findExistOrder(Integer userId) {
        String sql = "SELECT id,total_price FROM orders WHERE user_id=:userId AND status=0";
        SqlParameterSource param = new MapSqlParameterSource().addValue("userId", userId);
        List<Order> orderList = template.query(sql, param, ORDER_ROW_MAPPER);
        
        if (orderList.isEmpty()) {
            return null;
        }
        return orderList.get(0);
    }


    /**
     * 最初のカート作成
     * 
     * @param order
     */
    public void insert(Order order) {
        String sql = "INSERT INTO orders (user_id, status, total_price) VALUES (:userId, :status, :totalPrice) RETURNING id";
        SqlParameterSource param = new MapSqlParameterSource()
                                    .addValue("userId", order.getUserId())
                                    .addValue("status", order.getStatus())
                                    .addValue("totalPrice", order.getTotalPrice());

        Integer orderId = template.queryForObject(sql, param, Integer.class);

        //注文商品の登録
        List<OrderItem> orderItemList = order.getOrderItemList();
        if (!orderItemList.isEmpty()) {
            //ループ処理で注文商品の数だけ
            String orderItemSql = "INSERT INTO order_items (item_id, order_id, quantity, size) VALUES (:itemId,:orderId,:quantity,:size) RETURNING id";
            MapSqlParameterSource param2 = new MapSqlParameterSource();
            for (OrderItem orderItem : orderItemList) {
                param2.addValue("itemId", orderItem.getItemId())
                .addValue("orderId", orderId)
                .addValue("quantity", orderItem.getQuantity())
                .addValue("size", orderItem.getSize());
                Integer orderItemId = template.queryForObject(orderItemSql, param2, Integer.class);

                List<OrderTopping> toppingList = orderItem.getOrderToppingList();
                if (!toppingList.isEmpty()) {
                	String orderToppingSql = "INSERT INTO order_toppings (topping_id, order_item_id) VALUES";
                    for (int i = 0; i < toppingList.size(); i++) {
                        orderToppingSql += " (:toppingId" + i + ",:orderItemId)";
                        param2.addValue("toppingId" + i, toppingList.get(i).getToppingId());
                        if (i < toppingList.size() - 1) {
                            orderToppingSql += ",";
                        }
                    }
                    param2.addValue("orderItemId", orderItemId);
                    template.update(orderToppingSql, param2);
                }
            }
        }
    }

    /**
     * すでに作成済みのカートに入れる
     * 
     * @param orderItem
     * @param orderId
     */
    public void insertOrderItem(OrderItem orderItem, Integer orderId) {

        String orderItemSql = "INSERT INTO order_items (item_id, order_id, quantity, size) VALUES (:itemId,:orderId,:quantity,:size) RETURNING id";
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("itemId", orderItem.getItemId())
        .addValue("orderId", orderId)
        .addValue("quantity", orderItem.getQuantity())
        .addValue("size", orderItem.getSize());
        Integer orderItemId = template.queryForObject(orderItemSql, param, Integer.class);
        
        List<OrderTopping> toppingList = orderItem.getOrderToppingList();
        if (!toppingList.isEmpty()) {
            String orderToppingSql = "INSERT INTO order_toppings (topping_id, order_item_id) VALUES";
            for (int i = 0; i < toppingList.size(); i++) {
                orderToppingSql += " (:toppingId" + i + ",:orderItemId)";
                param.addValue("toppingId" + i, toppingList.get(i).getToppingId());
                if (i < toppingList.size() - 1) {
                    orderToppingSql += ",";
                }
            }
            param.addValue("orderItemId", orderItemId);
            template.update(orderToppingSql, param);
        }
    }

    /**
     * 合計金額の更新
     * 
     * @param totalPrice
     */
    public void updateTotalPrice(Integer totalPrice, Integer orderId) {
        String sql = "UPDATE orders SET total_price=:totalPrice WHERE id=:id";
        SqlParameterSource param = new MapSqlParameterSource().addValue("totalPrice", totalPrice).addValue("id", orderId);

        template.update(sql, param);
    }

    /**
     * ユーザIDと状態から検索
     * 
     * @param userId
     * @param status
     * @return
     */
    public List<Order> findByCondision(Integer orderId, Integer userId, Integer status) {
        String sql = "SELECT"
        		+ " order_id, user_id, status, total_price, order_date, destination_name, destination_email,"
        		+ " destination_zipcode, destination_address, destination_tel, delivery_time, payment_method,"
        		+ " oi.id as order_item_id, item_id, quantity, size,"
        		+ " i.name as item_name, description, i.price_M as item_price_M, i.price_L as item_price_L, image_path,"
        		+ " ot.id as order_topping_id, topping_id,"
        		+ " t.name as topping_name, t.price_M as topping_price_M, t.price_L as topping_price_L"
        		+ " FROM orders as o"
        		+ " LEFT OUTER JOIN order_items as oi ON o.id=oi.order_id"
        		+ " LEFT OUTER JOIN items as i ON i.id=oi.item_id"
        		+ " LEFT OUTER JOIN order_toppings as ot ON oi.id=ot.order_item_id"
        		+ " LEFT OUTER JOIN toppings as t ON t.id=ot.topping_id"
        		+ " WHERE 1=1";
        MapSqlParameterSource param = new MapSqlParameterSource();
        if (orderId != null) {
            sql += " AND o.id=:orderId";
            param.addValue("orderId", orderId);
        }
        if (userId != null) {
            sql += " AND user_id=:userId";
            param.addValue("userId", userId);
        }
        if (status == 0) {
            sql += " AND status=:status";
            param.addValue("status", status);
        } else if(status == null) {
            sql += " AND status!=0";
        }
        List<Order> orderList = template.query(sql, param, ORDER_RESULT_SET_EXTRACTOR);
        
        if (orderList.isEmpty()) {
            return null;
        }
        return orderList;
    }

    public void update(Order order) {
        String sql = "UPDATE orders SET"
        		+ " status=:status,"
        		+ " order_date=:orderDate,"
        		+ " destination_name=:destinationName,"
        		+ " destination_email=:destinationEmail,"
        		+ " destination_zipcode=:destinationZipcode,"
        		+ " destination_address=:destinationAddress,"
        		+ " destination_tel=:destinationTel,"
        		+ " delivery_time=:deliveryTime,"
        		+ " payment_method=:paymentMethod"
        		+ " WHERE id=:id";
        SqlParameterSource param = new BeanPropertySqlParameterSource(order);

        template.update(sql, param);
    }
}

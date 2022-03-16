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
    
    private static final RowMapper<Order> ORDER_ROW_MAPPER = new BeanPropertyRowMapper<>(Order.class);

    /**
     * カートの存在チェック
     * 
     * @param userId
     * @return
     */
    public Order findExistOrder(Integer userId) {
        String sql = "SELECT id FROM orders WHERE user_id=:userId AND status=0";
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
    public void updateTotalPrice(Integer subTotalPrice, Integer orderId) {
        String sql = "UPDATE orders SET total_price=total_price + :totalPrice WHERE id=:id";
        SqlParameterSource param = new MapSqlParameterSource().addValue("totalPrice", subTotalPrice).addValue("id", orderId);

        template.update(sql, param);
    }
}

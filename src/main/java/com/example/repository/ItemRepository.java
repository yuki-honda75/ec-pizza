package com.example.repository;

import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.domain.Item;
import com.example.domain.Topping;

/**
 * 
 * @author hondayuki
 *
 */
@Repository
public class ItemRepository {
	@Autowired
	private NamedParameterJdbcTemplate template;
	
	private static final ResultSetExtractor<List<Item>> ITEM_TOPPING_RESULT_SET_EXTRACTOR = (rs) -> {
		List<Item> itemList = new ArrayList<>();
		List<Topping> toppinglList = null;
		int preId = 0;
		
		while (rs.next()) {
			int nowId = rs.getInt("item_id");
			
			if (preId != nowId) {
				Item item = new Item();
				item.setId(nowId);
				item.setName(rs.getString("item_name"));
				item.setDescription(rs.getString("description"));
				item.setPriceM(rs.getInt("item_price_M"));
				item.setPriceL(rs.getInt("item_price_L"));
				item.setImagePath(rs.getString("image_path"));
				toppinglList = new ArrayList<>();
				item.setToppingList(toppinglList);
			}
			
			if (rs.getInt("topping_id") > 0) {
				Topping topping = new Topping();
				topping.setId(rs.getInt("topping_id"));
				topping.setName(rs.getString("topping_name"));
				topping.setPriceM(rs.getInt("topping_price_M"));
				topping.setPriceL(rs.getInt("topping_price_L"));
				toppinglList.add(topping);
			}
			
			preId = nowId;
		}
		
		return itemList;
	};
	
	private static final RowMapper<Item> ITEM_ROW_MAPPER = new BeanPropertyRowMapper<>(Item.class);
	
	/**
	 * 商品全件取得
	 * 
	 * @return
	 */
	public List<Item> findAll() {
		String sql = "SELECT id,name,description,price_M,price_L,image_path FROM items";
		List<Item> itemList = template.query(sql, ITEM_ROW_MAPPER);
		
		return itemList;
	}

	public List<Item> findByCondition(String name, Integer sortNum) {
		String sql = "SELECT id,name,description,price_M,price_L,image_path FROM items";
		String whereSql = " WHERE 1=1";
		String orderSql = "";
		MapSqlParameterSource param = new MapSqlParameterSource();
		if (!name.isEmpty()) {
			whereSql += " AND name LIKE :name";
			param.addValue("name", "%" + name + "%");
		}
		//0なら安い順、1なら高い順
		if (sortNum != null) {
			if (sortNum == 0) {
				orderSql += " ORDER BY price_L";
			} else if (sortNum == 1) {
				orderSql += " ORDER BY price_L DESC";
			}
		}
		List<Item> itemList = template.query(sql + whereSql + orderSql, param, ITEM_ROW_MAPPER);

		return itemList;
	}
}

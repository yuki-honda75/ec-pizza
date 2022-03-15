package com.example.repository;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
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
	
	private static final RowMapper<Item> ITEM_ROW_MAPPER = new BeanPropertyRowMapper<>(Item.class);

	private static final RowMapper<Topping> TOPPING_ROW_MAPPER = new BeanPropertyRowMapper<>(Topping.class);
	
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

	/**
	 * 商品詳細検索（トッピングも取得
	 * 
	 * @param id
	 * @return
	 */
	public Item load(Integer id) {
		String sql = "SELECT id,name,description,price_M,price_L,image_path FROM items WHERE id=:id";
		SqlParameterSource param = new MapSqlParameterSource().addValue("id", id);
		Item item = template.queryForObject(sql, param, ITEM_ROW_MAPPER);

		String toppingSql = "SELECT id,name,price_M,price_L FROM toppings";
		List<Topping> toppingList = template.query(toppingSql, TOPPING_ROW_MAPPER);
		item.setToppingList(toppingList);

		return item;
	}

	/**
	 * 選択したトッピングを取得する
	 * 
	 * @param toppingIdList
	 * @return
	 */
	public List<Topping> findByIoppingId(List<Integer> toppingIdList) {
		if (toppingIdList.isEmpty()) {
			return null;
		}
		String sql = "SELECT id,name,price_M,price_L FROM toppings";
		String whereSql = " WHERE 1=1";
		MapSqlParameterSource param = new MapSqlParameterSource();
		for (Integer toppingId : toppingIdList) {
			whereSql += " OR id=:id" + toppingId;
			param.addValue("id" + toppingId, toppingId);
		}

		return template.query(sql + whereSql, param, TOPPING_ROW_MAPPER);
	}
}

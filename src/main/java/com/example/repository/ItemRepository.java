package com.example.repository;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
		String sql = "SELECT id,name,description,price_M,price_L,image_path FROM items Order By price_L";
		List<Item> itemList = template.query(sql, ITEM_ROW_MAPPER);
		
		return itemList;
	}

	/**
	 * 商品全件ページ取得
	 * 
	 * @return
	 */
	public Page<Item> findAllPage(Pageable pageable) {
		String sql = "SELECT id,name,description,price_M,price_L,image_path FROM items Order By price_L";
		String sqlLimitOffset = " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

		String sqlCount = "SELECT count(*) FROM items";
		int count = template.queryForObject(sqlCount, new MapSqlParameterSource(), Integer.class);

		List<Item> itemList = template.query(sql + sqlLimitOffset, ITEM_ROW_MAPPER);
		
		return new PageImpl<Item>(itemList, pageable, count);
	}

	public Page<Item> findByCondition(String name, Integer sortNum, Pageable pageable) {
		String sql = "SELECT id,name,description,price_M,price_L,image_path FROM items";
		String whereSql = " WHERE 1=1";
		String orderSql = " ORDER BY price_L";
		MapSqlParameterSource param = new MapSqlParameterSource();
		if (name != null) {
			whereSql += " AND name LIKE :name";
			param.addValue("name", "%" + name + "%");
		}
		//1なら高い順
		if (sortNum != null && sortNum == 1) {
			orderSql += " DESC";
		}
		String sqlLimitOffset = " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

		String sqlCount = "SELECT count(*) FROM items";
		int count = template.queryForObject(sqlCount + whereSql, param, Integer.class);
		List<Item> itemList = template.query(sql + whereSql + orderSql + sqlLimitOffset, param, ITEM_ROW_MAPPER);

		return new PageImpl<Item>(itemList, pageable, count);
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
	public List<Topping> findToppingById(List<Integer> toppingIdList) {
		if (toppingIdList.isEmpty()) {
			return null;
		}
		String sql = "SELECT id,name,price_M,price_L FROM toppings";
		String whereSql = " WHERE 1=1";
		MapSqlParameterSource param = new MapSqlParameterSource();
		int i = 0;
		for (Integer toppingId : toppingIdList) {
			if (i == 0) {
				whereSql += " AND id=:id" + i;
			} else {
				whereSql += " OR id=:id" + i;
			}
			param.addValue("id" + i, toppingId);
			i++;
		}

		return template.query(sql + whereSql, param, TOPPING_ROW_MAPPER);
	}
}

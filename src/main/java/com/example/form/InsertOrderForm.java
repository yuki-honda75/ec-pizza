package com.example.form;

import java.util.List;

import com.example.domain.OrderTopping;

/**
 * 
 * @author hondayuki
 *
 */
public class InsertOrderForm {
	/** アイテムID */
	private String itemId;
	/** 数量 */
    private String quantity;
	/** サイズ */
	private String size;
	/** トッピングリスト */
	private List<OrderTopping> orderToppingList;
	
	public Integer getIntItemId() {
		return Integer.parseInt(itemId);
	}

	public Integer getIntQuantity() {
		return Integer.parseInt(quantity);
	}

	public Character getCharSize() {
		return quantity.toCharArray()[0];
	}

	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public List<OrderTopping> getOrderToppingList() {
		return orderToppingList;
	}
	public void setOrderToppingList(
			List<OrderTopping> orderToppingList) {
		this.orderToppingList = orderToppingList;
	}
	
	@Override
	public String toString() {
		return "InsertOrderForm [itemId=" + itemId
				+ ", quantity=" + quantity + ", size="
				+ size + ", orderToppingList="
				+ orderToppingList + "]";
	}
}

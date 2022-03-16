package com.example.form;

import java.util.List;

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
	private List<Integer> orderToppingIdList;
	
	public Integer getIntItemId() {
		return Integer.parseInt(itemId);
	}

	public Integer getIntQuantity() {
		return Integer.parseInt(quantity);
	}

	public Integer getIntSizeNum() {
		return Integer.parseInt(size);
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
	public List<Integer> getOrderToppingIdList() {
		return orderToppingIdList;
	}
	public void setOrderToppingIdList(
			List<Integer> orderToppingList) {
		this.orderToppingIdList = orderToppingList;
	}
	
	@Override
	public String toString() {
		return "InsertOrderForm [itemId=" + itemId
				+ ", quantity=" + quantity + ", size="
				+ size + ", orderToppingList="
				+ orderToppingIdList + "]";
	}
}

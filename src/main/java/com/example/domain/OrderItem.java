package com.example.domain;

import java.util.List;

/**
 * 
 * @author hondayuki
 *
 */
public class OrderItem {
	/** ID */
	private Integer id;
	/** 商品ID */
	private String itemId;
	/** 注文ID */
	private String orderId;
	/** 数量 */
	private String quantity;
	/** サイズ */
	private Character size;
	/** 商品 */
	private Item item;
	/** 注文トッピングリスト */
	private List<OrderTopping> orderToppingList;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public Character getSize() {
		return size;
	}
	public void setSize(Character size) {
		this.size = size;
	}
	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
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
		return "OrderItem [id=" + id + ", itemId=" + itemId
				+ ", orderId=" + orderId + ", quantity="
				+ quantity + ", size=" + size + ", item="
				+ item + ", orderToppingList="
				+ orderToppingList + "]";
	}
	
}

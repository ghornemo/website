package com.example.model;

import java.sql.Date;
import java.util.ArrayList;


public class Cart {
	public int ID;
	public ArrayList<Item> items = new ArrayList<>();
	public void add(Item i) {
		for(Item item : items) {
			if(item.name.equals(i.name)) {
				item.quantity++;
				return;
			}
		}
		i.quantity = 1;
		items.add(i);
	}

	private Date orderDate;

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}
	
	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public ArrayList<Item> getItems() {
		return items;
	}

	public void setItems(ArrayList<Item> items) {
		this.items = items;
	}

	public void clear() {
		items = new ArrayList<>();
	}
	public float total() {
		float price = 0;
		for(Item item : items)
			price += item.price*item.quantity;
		return price;
	}
}

package com.example.model;

import java.util.ArrayList;


public class Cart {
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

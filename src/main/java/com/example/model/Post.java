package com.example.model;

import java.sql.Date;

public class Post {
	public String body;
	public String user;
	public int Id;
	public Date date;
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public Post() {
		
	}
	public Post(String body, String user, Date date) {
		this.body = body;
		this.user = user;
		this.date = date;
	}
	
	public String getBody() {
		return body;
	}
	public String getUser() {
		return user;
	}
	public Date getDate() {
		return date;
	}
	public void setBody(String data) {
		body = data;
	}
	public void setUser(String data) {
		user = data;
	}

	public void setDate(Date data) {
		date = data;
	}
}

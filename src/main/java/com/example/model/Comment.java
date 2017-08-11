package com.example.model;

import java.sql.Date;

public class Comment {
	   public int id;
	   public Date date;
	   public String name;
	   public String comment;
	   
	   public Comment(int id, Date date, String name, String comment) {
		   this.id = id;
		   this.date = date;
		   this.name = name;
		   this.comment = comment;
	   }
	   
	   public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Comment() {
		   
	   }
}

package com.example.model;

import java.sql.Date;

public class Rating {
	
	
	public short getScore() {
		return score;
	}
	public void setScore(short score) {
		this.score = score;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getReviewer() {
		return reviewer;
	}
	public void setReviewer(String reviewer) {
		this.reviewer = reviewer;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public short score;
	private String comment, email, itemName, reviewer, title;
	private Date time;
	public void print() {
		System.out.println(" --- RATING DATA --- ");
		System.out.println("score: "+score);
		System.out.println("email: "+email);
		System.out.println("itemName: "+itemName);
		System.out.println("reviewer: "+reviewer);
		System.out.println("title: "+title);
		System.out.println("comment: "+comment);
		// TODO Auto-generated method stub
		
	}

}

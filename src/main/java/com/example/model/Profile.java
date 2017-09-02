package com.example.model;

import java.sql.Date;

public class Profile {
	 public String email;
	 public String firstName;
	 public String lastName;
	 public Date memberSince;
	public String getEmail() {
		return email;
	}
	public Date getMemberSince() {
		return memberSince;
	}
	public void setMemberSince(Date memberSince) {
		this.memberSince = memberSince;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getName() {
		return firstName+" "+lastName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}

package com.hd.cedg.lms.model;

import java.io.Serializable;

public class ReportUser implements Serializable {

	private static final long serialVersionUID = -2483435547894385857L;

	private int userId;
	private String firstName;
	private String lastName;
	private String email;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}

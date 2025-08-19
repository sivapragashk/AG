package com.hd.cedg.lms.model;

import java.io.Serializable;

public class WebcastOtherAttendee implements Serializable {

	private static final long serialVersionUID = 6683886970247707909L;

	private String firstName;
	private String lastName;
	private String companyName;

	public WebcastOtherAttendee() {
	}

	public WebcastOtherAttendee(String firstName, String lastName,
			String companyName) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.companyName = companyName;
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

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

}

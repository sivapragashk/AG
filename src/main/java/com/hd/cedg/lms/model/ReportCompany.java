package com.hd.cedg.lms.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ReportCompany implements Serializable, Comparable<ReportCompany> {

	private static final long serialVersionUID = 6015812217736890752L;

	private int companyId;
	private String companyName;
	private String companyType;
	private String allianceProgram;
	private String fabricator;
	private String salesRep;
	private String address;
	private String city;
	private String region;
	private String postCode;
	private List<ReportUser> users;

	public ReportCompany() {
		users = new ArrayList<ReportUser>();
	}

	/**
	 * Makes another company just like the first, but with no users.
	 */
	public ReportCompany(ReportCompany company) {
		companyName = company.getCompanyName();
		companyType = company.getCompanyType();
		allianceProgram = company.getAllianceProgram();
		fabricator = company.getFabricator();
		salesRep = company.getSalesRep();
		address = company.getAddress();
		city = company.getCity();
		region = company.getRegion();
		postCode = company.getPostCode();
		users = new ArrayList<ReportUser>();
	}

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyType() {
		return companyType;
	}

	public void setCompanyType(String companyType) {
		this.companyType = companyType;
	}

	public String getAllianceProgram() {
		return allianceProgram;
	}

	public void setAllianceProgram(String allianceProgram) {
		this.allianceProgram = allianceProgram;
	}

	public String getFabricator() {
		return fabricator;
	}

	public void setFabricator(String fabricator) {
		this.fabricator = fabricator;
	}

	public String getSalesRep() {
		return salesRep;
	}

	public void setSalesRep(String salesRep) {
		this.salesRep = salesRep;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getRegion() {
		return region;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public List<ReportUser> getUsers() {
		return users;
	}

	public void setUsers(List<ReportUser> users) {
		this.users = users;
	}

	public int compareTo(ReportCompany o) {
		int nameCompare = companyName.toLowerCase().compareTo(
				o.getCompanyName().toLowerCase());
		if (nameCompare != 0) {
			return nameCompare;
		}

		int cityCompare = city.toLowerCase().compareTo(
				o.getCity().toLowerCase());
		if (cityCompare != 0) {
			return cityCompare;
		}

		return region.toLowerCase().compareTo(o.getRegion().toLowerCase());
	}

}

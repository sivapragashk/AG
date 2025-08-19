package com.hd.cedg.lms.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LearningUser implements Serializable {

	private static final long serialVersionUID = -7741597802227233976L;

	private boolean active;
	private String authDescription;
	private int points;

	// Basic User Info
	private int userId;
	private String username;
	private String emailAddress;
	private String firstName;
	private String lastName;

	// Address/Location Info
	private String address1;
	private String address2;
	private String city;
	private String region;
	private String postCode;
	private String phone;
	private String cellPhone;
	private String country;

	// Basic Company Info
	private int companyId;
	private String companyName;
	private String companyType;
	private String department;

	// Dealer Specific Info
	private String allianceLevel;
	private String allianceCommitted;
	private int sponsoringFabricatorId;
	private String sponsoringFabricatorName;
	private String sponsoringFabType;
	private int salesRepId;
	private String salesRepName;
	private boolean centurion;
	private boolean isAlligneduser;
	// Fab Specific Info
	private String companyFabType;

	// Training Program Levels
	private String pdpLevel;
	private String pfpLevel;
	private String pipLevel;

	// "Franchises"
	private boolean cdi;
	private String franchise;

	// Certified Motorization Specialist
	private boolean cmsTrainingReady;

	// Trade Alliance
	private boolean tradeAlliance;

	// Service Center
	private boolean serviceCenter;

	// Manager
	private boolean manager;

	// Admin
	private boolean admin;
	private AdminAccessRights adminAccessRights;

	// Access Tags
	private List<String> accessTags;

	
	
	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public LearningUser() {
		adminAccessRights = new AdminAccessRights();
		accessTags = new ArrayList<String>();
	}

	public boolean isAlligneduser() {
		return isAlligneduser;
	}

	public void setAlligneduser(boolean isAlligneduser) {
		this.isAlligneduser = isAlligneduser;
	}

	public String getCellPhone() {
		return cellPhone;
	}

	public void setCellPhone(String cellPhone) {
		this.cellPhone = cellPhone;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getAuthDescription() {
		return authDescription;
	}

	public void setAuthDescription(String authDescription) {
		this.authDescription = authDescription;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
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

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
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

	public void setRegion(String region) {
		this.region = region;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
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

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getAllianceLevel() {
		return allianceLevel;
	}

	public void setAllianceLevel(String allianceLevel) {
		this.allianceLevel = allianceLevel;
	}

	public String getAllianceCommitted() {
		return allianceCommitted;
	}

	public void setAllianceCommitted(String allianceCommitted) {
		this.allianceCommitted = allianceCommitted;
	}

	public int getSponsoringFabricatorId() {
		return sponsoringFabricatorId;
	}

	public void setSponsoringFabricatorId(int sponsoringFabricatorId) {
		this.sponsoringFabricatorId = sponsoringFabricatorId;
	}

	public String getSponsoringFabricatorName() {
		return sponsoringFabricatorName;
	}

	public void setSponsoringFabricatorName(String sponsoringFabricatorName) {
		this.sponsoringFabricatorName = sponsoringFabricatorName;
	}

	public String getSponsoringFabType() {
		return sponsoringFabType;
	}

	public void setSponsoringFabType(String sponsoringFabType) {
		this.sponsoringFabType = sponsoringFabType;
	}

	public int getSalesRepId() {
		return salesRepId;
	}

	public void setSalesRepId(int salesRepId) {
		this.salesRepId = salesRepId;
	}

	public String getSalesRepName() {
		return salesRepName;
	}

	public void setSalesRepName(String salesRepName) {
		this.salesRepName = salesRepName;
	}

	public boolean isCenturion() {
		return centurion;
	}

	public void setCenturion(boolean centurion) {
		this.centurion = centurion;
	}

	public String getCompanyFabType() {
		return companyFabType;
	}

	public void setCompanyFabType(String companyFabType) {
		this.companyFabType = companyFabType;
	}

	public String getPdpLevel() {
		return pdpLevel;
	}

	public void setPdpLevel(String pdpLevel) {
		this.pdpLevel = pdpLevel;
	}

	public String getPfpLevel() {
		return pfpLevel;
	}

	public void setPfpLevel(String pfpLevel) {
		this.pfpLevel = pfpLevel;
	}

	public String getPipLevel() {
		return pipLevel;
	}

	public void setPipLevel(String pipLevel) {
		this.pipLevel = pipLevel;
	}

	public boolean isCdi() {
		return cdi;
	}

	public void setCdi(boolean cdi) {
		this.cdi = cdi;
	}

	public String getFranchise() {
		return franchise;
	}

	public void setFranchise(String franchise) {
		this.franchise = franchise;
	}

	public boolean isCmsTrainingReady() {
		return cmsTrainingReady;
	}

	public void setCmsTrainingReady(boolean cmsTrainingReady) {
		this.cmsTrainingReady = cmsTrainingReady;
	}

	public boolean isTradeAlliance() {
		return tradeAlliance;
	}

	public void setTradeAlliance(boolean tradeAlliance) {
		this.tradeAlliance = tradeAlliance;
	}

	public boolean isServiceCenter() {
		return serviceCenter;
	}

	public void setServiceCenter(boolean serviceCenter) {
		this.serviceCenter = serviceCenter;
	}

	public boolean isManager() {
		return manager;
	}

	public void setManager(boolean manager) {
		this.manager = manager;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public AdminAccessRights getAdminAccessRights() {
		return adminAccessRights;
	}

	public void setAdminAccessRights(AdminAccessRights adminAccessRights) {
		this.adminAccessRights = adminAccessRights;
	}

	public List<String> getAccessTags() {
		return accessTags;
	}

	public void setAccessTags(List<String> accessTags) {
		this.accessTags = accessTags;
	}

}

package com.hd.cedg.lms.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventRegistration implements Serializable {

	private static final long serialVersionUID = 8923075160626590805L;

	public static final int STATUS_REGISTERED = 0;
	public static final int STATUS_ATTENDED = 1;
	public static final int STATUS_NO_SHOW = 2;

	private int registrationId;

	private Date registrationDate;

	private Event event;
	private int registeringUserId;
	private int userId;
	private int companyId;
	private String firstName;
	private String lastName;
	private String companyName;
	private String address;
	private String city;
	private String region;
	private String postCode;
	private String phone;
	private String email;
	private float costOverride;
	private String confirmationNumber;

	private int status;

	private List<Integer> selectedOptionIds;
	private List<Integer> selectedPackageIds;

	private Map<Integer, String> questionAnswers;

	private int paymentMethodId;
	private Map<Integer, String> paymentMethodDetailAnswers;
	private String paymentNote;

	private String companyType;
	private String allianceLevel;
	private String fabricator;
	private String salesRep;

	private String returnUrl;

	private boolean pointsUpdated;
	
	private boolean sendCCRefundEmail; 
	private Map<String, Object> registrationTab;
	 
	public EventRegistration() {
		registrationDate = new Date();
		event = new Event();
		selectedOptionIds = new ArrayList<Integer>();
		selectedPackageIds = new ArrayList<Integer>();
		questionAnswers = new HashMap<Integer, String>();
		paymentMethodDetailAnswers = new HashMap<Integer, String>();
		paymentMethodId = -1;
		costOverride = -1;
		status = STATUS_REGISTERED;
	}

	public EventRegistration(Event event) {
		registrationDate = new Date();
		this.event = event;
		selectedOptionIds = new ArrayList<Integer>();
		selectedPackageIds = new ArrayList<Integer>();
		questionAnswers = new HashMap<Integer, String>();
		paymentMethodDetailAnswers = new HashMap<Integer, String>();
		paymentMethodId = EventPaymentMethod.PAYMENT_ID_NOCHARGE;
		costOverride = -1;
		status = STATUS_REGISTERED;
	}

	public int getRegistrationId() {
		return registrationId;
	}

	public void setRegistrationId(int registrationId) {
		this.registrationId = registrationId;
	}

	public Date getRegistrationDate() {
		return registrationDate;
	}

	public String getFormattedRegistrationDate() {
		if (registrationDate == null) {
			return "";
		} else {
			return new SimpleDateFormat("MM-dd-yyyy").format(registrationDate);
		}
	}

	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	public void setRegistrationDate(String registrationDate) {
		if (registrationDate != null) {
			try {
				this.registrationDate = new SimpleDateFormat("yyyy-MM-dd")
						.parse(registrationDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else {
			registrationDate = null;
		}
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public int getRegisteringUserId() {
		return registeringUserId;
	}

	public void setRegisteringUserId(int registeringUserId) {
		this.registeringUserId = registeringUserId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public float getCostOverride() {
		return costOverride;
	}

	public void setCostOverride(float costOverride) {
		this.costOverride = costOverride;
	}

	public String getConfirmationNumber() {
		return confirmationNumber;
	}

	public void setConfirmationNumber(String confirmationNumber) {
		this.confirmationNumber = confirmationNumber;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public List<Integer> getSelectedOptionIds() {
		return selectedOptionIds;
	}

	public void setSelectedOptionIds(List<Integer> selectedOptionIds) {
		this.selectedOptionIds = selectedOptionIds;
	}

	public List<Integer> getSelectedPackageIds() {
		return selectedPackageIds;
	}

	public void setSelectedPackageIds(List<Integer> selectedPackageIds) {
		this.selectedPackageIds = selectedPackageIds;
	}

	public List<Integer> getUniqueSelectedOptionIds() {
		Map<Integer, Integer> optionMap = new HashMap<Integer, Integer>();
		for (Integer optionId : selectedOptionIds) {
			optionMap.put(optionId, optionId);
		}

		for (Integer packageId : selectedPackageIds) {
			for (EventOptionPackage aPackage : event.getEventOptionPackages()) {
				if (packageId.intValue() == aPackage.getPackageId()) {
					for (Integer optionId : aPackage.getOptionIds()) {
						optionMap.put(optionId, optionId);
					}
				}
			}
		}

		List<Integer> uniqueOptionIds = new ArrayList<Integer>(optionMap
				.keySet());
		Collections.sort(uniqueOptionIds);
		return uniqueOptionIds;
	}

	public Map<Integer, String> getQuestionAnswers() {
		return questionAnswers;
	}

	public void setQuestionAnswers(Map<Integer, String> questionAnswers) {
		this.questionAnswers = questionAnswers;
	}
		
	public Map<String, Object> getRegistrationTabs() {
		return registrationTab;
	}

	public void setRegistrationTabs(Map<String, Object> registrationTab) {
		this.registrationTab = registrationTab;
	}	
	
	public int getPaymentMethodId() {
		return paymentMethodId;
	}

	public void setPaymentMethodId(int paymentMethodId) {
		this.paymentMethodId = paymentMethodId;
	}

	public Map<Integer, String> getPaymentMethodDetailAnswers() {
		return paymentMethodDetailAnswers;
	}

	public void setPaymentMethodDetailAnswers(
			Map<Integer, String> paymentMethodDetailAnswers) {
		this.paymentMethodDetailAnswers = paymentMethodDetailAnswers;
	}

	public String getPaymentNote() {
		return paymentNote;
	}

	public void setPaymentNote(String paymentNote) {
		this.paymentNote = paymentNote;
	}

	public String getCompanyType() {
		return companyType;
	}

	public void setCompanyType(String companyType) {
		this.companyType = companyType;
	}

	public String getAllianceLevel() {
		return allianceLevel;
	}

	public void setAllianceLevel(String allianceLevel) {
		this.allianceLevel = allianceLevel;
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

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	public boolean isPointsUpdated() {
		return pointsUpdated;
	}

	public void setPointsUpdated(boolean pointsUpdated) {
		this.pointsUpdated = pointsUpdated;
	}

	public List<EventQuestion> getQuestionsForCurrentSelections() {
		List<EventQuestion> questions = new ArrayList<EventQuestion>();

		for (EventQuestion question : event.getQuestions()) {
			List<Integer> optionIds = question.getOptionIds();
			List<Integer> packageIds = question.getPackageIds();
			if (optionIds.size() == 0 && packageIds.size() == 0) {
				questions.add(question);
			} else {
				boolean includeQuestion = false;
				for (Integer optionId : optionIds) {
					if (selectedOptionIds.contains(optionId)) {
						includeQuestion = true;
						break;
					}
				}
				if (!includeQuestion) {
					for (Integer packageId : packageIds) {
						if (selectedPackageIds.contains(packageId)) {
							includeQuestion = true;
							break;
						}
					}
				}
				if (includeQuestion) {
					questions.add(question);
				}
			}
		}

		return questions;
	}

	public Cost getTotalCost() {
		if (costOverride >= 0) {
			return new Cost(costOverride);
		}  else {
			return getCalculatedCost();
		}
	}

	public Cost getCalculatedCost() {
		float cost = event.getCost().getCost();

		for (EventOption option : event.getEventOptions()) {
			if (selectedOptionIds.contains(new Integer(option
					.getEventOptionId()))) {
				cost += option.getCost().getCost();
			}
		}

		for (EventOptionPackage aPackage : event.getEventOptionPackages()) {
			if (selectedPackageIds
					.contains(new Integer(aPackage.getPackageId()))) {
				cost += aPackage.getCost().getCost();
			}
		}

		return new Cost(cost);
	}
	
	public boolean isSendCCRefundEmail() {
		return sendCCRefundEmail;
	}

	public void setSendCCRefundEmail(boolean sendCCRefundEmail) {
		this.sendCCRefundEmail = sendCCRefundEmail;
	}
	
	public boolean checkifPaymentNeed(boolean isAligned,float cost,float aligncost){
		if(cost==0 || (isAligned && aligncost==0)){
			return false;
		}else{
			return true;
		}
		
	}
	
	public static boolean checkAlignedUser(String allianceLevel){
		if(allianceLevel.equalsIgnoreCase("Gallery")){
    		return true;
    	}else if(allianceLevel.equalsIgnoreCase("Showcase")){
    		return true;
    	}
    	else if(allianceLevel.equalsIgnoreCase("Priority")){
    		return true;
    	}else if(allianceLevel.equalsIgnoreCase("showcase priority")){
    		return true;
    	}else{
    		return false;
    	}

		
	}
}

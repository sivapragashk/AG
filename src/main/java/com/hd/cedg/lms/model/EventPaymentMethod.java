package com.hd.cedg.lms.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EventPaymentMethod implements Serializable {

	private static final long serialVersionUID = 8073022948314835844L;

	public static final int PAYMENT_ID_CC = 0;
	public static final int PAYMENT_ID_NOCHARGE = -1;
	public static final int PAYMENT_ID_PAID_ON_OTHER_EVENT = -2;

	private int paymentMethodId;

	private String name;
	private boolean adminOnly;
	private List<EventPaymentMethodDetail> paymentMethodDetails;

	public EventPaymentMethod() {
		paymentMethodDetails = new ArrayList<EventPaymentMethodDetail>();
	}

	public int getPaymentMethodId() {
		return paymentMethodId;
	}

	public void setPaymentMethodId(int paymentMethodId) {
		this.paymentMethodId = paymentMethodId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isAdminOnly() {
		return adminOnly;
	}

	public void setAdminOnly(boolean adminOnly) {
		this.adminOnly = adminOnly;
	}

	public List<EventPaymentMethodDetail> getPaymentMethodDetails() {
		return paymentMethodDetails;
	}

	public void setPaymentMethodDetails(
			List<EventPaymentMethodDetail> paymentMethodDetails) {
		this.paymentMethodDetails = paymentMethodDetails;
	}

}

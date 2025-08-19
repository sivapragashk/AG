package com.hd.cedg.lms.model;

import java.io.Serializable;

public class EventPaymentMethodDetail implements Serializable {

	private static final long serialVersionUID = 1962435382749787567L;

	public static final int DETAIL_ID_CC_NUMBER = -1;
	public static final int DETAIL_ID_EXP_DATE = -2;
	public static final int DETAIL_ID_CSC = -3;
	public static final int DETAIL_ID_NAME = -4;
	public static final int DETAIL_ID_ADDRESS = -5;
	public static final int DETAIL_ID_CITY = -6;
	public static final int DETAIL_ID_REGION = -7;
	public static final int DETAIL_ID_POSTCODE = -8;

	private int paymentDetailId;

	private String name;
	private boolean required;

	public int getPaymentDetailId() {
		return paymentDetailId;
	}

	public void setPaymentDetailId(int paymentDetailId) {
		this.paymentDetailId = paymentDetailId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

}

package com.hd.cedg.lms.model;

import java.io.Serializable;

public class EvaluationQuestionOption implements Serializable {

	private static final long serialVersionUID = -6347598165936447916L;

	private String value;
	private String optionText;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getOptionText() {
		return optionText;
	}

	public void setOptionText(String optionText) {
		this.optionText = optionText;
	}

}

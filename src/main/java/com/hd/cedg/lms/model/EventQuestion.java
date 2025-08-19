package com.hd.cedg.lms.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EventQuestion implements Serializable {

	private static final long serialVersionUID = -1481820130248575086L;

	public static final int TYPE_TEXT = 0;
	public static final int TYPE_DROPDOWN = 1;
	public static final int TYPE_CHECKBOXES = 2;

	private int questionId;

	private String text;
	private int type;
	private List<String> answers;
	private boolean required;
	private List<Integer> optionIds;
	private List<Integer> packageIds;

	public EventQuestion() {
		answers = new ArrayList<String>();
		optionIds = new ArrayList<Integer>();
		packageIds = new ArrayList<Integer>();
	}

	public int getQuestionId() {
		return questionId;
	}

	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public List<String> getAnswers() {
		return answers;
	}

	public void setAnswers(List<String> answers) {
		this.answers = answers;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public List<Integer> getOptionIds() {
		return optionIds;
	}

	public void setOptionIds(List<Integer> optionIds) {
		this.optionIds = optionIds;
	}

	public List<Integer> getPackageIds() {
		return packageIds;
	}

	public void setPackageIds(List<Integer> packageIds) {
		this.packageIds = packageIds;
	}

}

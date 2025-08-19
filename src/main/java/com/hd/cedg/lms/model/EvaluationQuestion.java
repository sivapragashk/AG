package com.hd.cedg.lms.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EvaluationQuestion implements Serializable {

	private static final long serialVersionUID = 3668065989026480983L;

	public static final int EVAL_QUESTION_TYPE_FREETEXT = 1;
	public static final int EVAL_QUESTION_TYPE_SINGLESELECT = 2;

	private int questionId;
	private String questionText;
	private int questionType;
	private List<EvaluationQuestionOption> options;

	public EvaluationQuestion() {
		options = new ArrayList<EvaluationQuestionOption>();
	}

	public int getQuestionId() {
		return questionId;
	}

	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public int getQuestionType() {
		return questionType;
	}

	public void setQuestionType(int questionType) {
		this.questionType = questionType;
	}

	public List<EvaluationQuestionOption> getOptions() {
		return options;
	}

	public void setOptions(List<EvaluationQuestionOption> options) {
		this.options = options;
	}

}

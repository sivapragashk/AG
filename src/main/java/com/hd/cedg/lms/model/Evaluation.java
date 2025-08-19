package com.hd.cedg.lms.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Evaluation implements Serializable {

	private static final long serialVersionUID = 4878614060552711208L;

	private Course course;
	private List<EvaluationQuestion> questions;
	private Map<EvaluationQuestion, String> responses;

	public Evaluation() {
		questions = new ArrayList<EvaluationQuestion>();
		responses = new HashMap<EvaluationQuestion, String>();
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public List<EvaluationQuestion> getQuestions() {
		return questions;
	}

	public void setQuestions(List<EvaluationQuestion> questions) {
		this.questions = questions;
	}

	public Map<EvaluationQuestion, String> getResponses() {
		return responses;
	}

	public void setResponses(Map<EvaluationQuestion, String> responses) {
		this.responses = responses;
	}

}

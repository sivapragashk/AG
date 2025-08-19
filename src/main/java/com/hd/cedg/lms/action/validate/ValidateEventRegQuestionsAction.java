package com.hd.cedg.lms.action.validate;

import java.util.Map;

import com.hd.cedg.lms.model.EventQuestion;
import com.hd.cedg.lms.model.EventRegistration;

public class ValidateEventRegQuestionsAction extends AbstractValidateAction {

	protected void validate(Map<String, Object> params,
			Map<String, String> errors) {

		EventRegistration registration = (EventRegistration) params
				.get("registration");

		for (EventQuestion question : registration
				.getQuestionsForCurrentSelections()) {
			int questionId = question.getQuestionId();
			Object answer = params.get("question" + questionId);
			if (question.isRequired()) {
				if (!isAnswered(question.getType(), answer)) {
					errors.put("question" + questionId, "This question is required");
				}
			}
			testMaxLength(answer, questionId, errors);
		}
	}

	private boolean isAnswered(int questionType, Object answer) {
		if (EventQuestion.TYPE_TEXT == questionType) {
			if (answer != null && !"".equals((String)answer)) {
				return true;
			}
		} else if (EventQuestion.TYPE_DROPDOWN == questionType) {
			if (answer != null && !"0".equals((String)answer)) {
				return true;
			}
		} else if (EventQuestion.TYPE_CHECKBOXES == questionType) {
			if (answer != null) {
				return true;
			}
		}
		return false;
	}

	private void testMaxLength(Object answer, int questionId, Map<String, String> errors) {
		if (answer instanceof String) {
			maxLength((String) answer, 400, "question" + questionId, errors);
		} else if (answer instanceof String[]) {
			String fullAnswer = "";
			for (String subAnswer : (String[])answer) {
				fullAnswer += (subAnswer + ", ");
			}
			maxLength(fullAnswer, 400, "question" + questionId, errors);
		}
	}
}

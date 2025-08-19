package com.hd.cedg.lms.action.process;

import java.util.HashMap;
import java.util.Map;

import com.hd.cedg.lms.action.base.BaseControllerAction;
import com.hd.cedg.lms.model.EventQuestion;
import com.hd.cedg.lms.model.EventRegistration;
import com.hd.cedg.lms.model.ExecutionResult;


public class ProcessEventRegQuestionsAction extends BaseControllerAction {

	public ExecutionResult doAction(Map<String, Object> params) {

		EventRegistration registration = (EventRegistration) params
				.get("registration");
		Map<Integer, String> questionAnswers = new HashMap<Integer, String>();

		for (EventQuestion question : registration
				.getQuestionsForCurrentSelections()) {
			int questionId = question.getQuestionId();
			Object answer = params.get("question" + questionId);
			if (isAnswered(question.getType(), answer)) {
				String answerStr = makeStringAnswer(answer);
				questionAnswers.put(new Integer(questionId), answerStr);
			}
		}

		registration.setQuestionAnswers(questionAnswers);

		params.put("registration", registration);

		return null;
	}

	private boolean isAnswered(int questionType, Object answer) {
		if (EventQuestion.TYPE_TEXT == questionType) {
			if (answer != null && !"".equals((String) answer)) {
				return true;
			}
		} else if (EventQuestion.TYPE_DROPDOWN == questionType) {
			if (answer != null && !"0".equals((String) answer)) {
				return true;
			}
		} else if (EventQuestion.TYPE_CHECKBOXES == questionType) {
			if (answer != null) {
				return true;
			}
		}
		return false;
	}

	private String makeStringAnswer(Object answer) {
		if (answer == null) {
			return "";
		} else if (answer instanceof String) {
			return (String) answer;
		} else if (answer instanceof String[]) {
			String fullAnswer = "";
			for (String subAnswer : (String[]) answer) {
				fullAnswer += (subAnswer + ", ");
			}
			fullAnswer = fullAnswer.substring(0, fullAnswer.lastIndexOf(", "));
			return fullAnswer;
		} else {
			return "";
		}
	}

}

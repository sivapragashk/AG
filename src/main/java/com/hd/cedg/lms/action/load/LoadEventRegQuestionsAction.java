package com.hd.cedg.lms.action.load;

import java.util.Map;

import com.hd.cedg.lms.action.BaseLmsControllerAction;
import com.hd.cedg.lms.model.EventRegistration;
import com.hd.cedg.lms.model.ExecutionResult;
import com.hd.cedg.lms.support.DefaultErrorExecutionResult;

public class LoadEventRegQuestionsAction extends BaseLmsControllerAction {

	protected ExecutionResult executeAction(Map<String, Object> params) {
		EventRegistration registration = (EventRegistration) params
				.get("registration");

		if (registration == null) {
			return noFormObject();
		}

		ExecutionResult result = new ExecutionResult();
		result.setPage("event-register/reg-questions.jsp");
		result.addAttribute("errors", params.get("errors"));
		return result;
	}

	private ExecutionResult noFormObject() {
		return new DefaultErrorExecutionResult();
	}
}

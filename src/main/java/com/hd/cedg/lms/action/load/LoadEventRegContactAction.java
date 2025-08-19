package com.hd.cedg.lms.action.load;

import java.util.Map;

import com.hd.cedg.lms.action.BaseLmsControllerAction;
import com.hd.cedg.lms.dao.EventRegistrationDAO;
import com.hd.cedg.lms.model.EventRegistration;
import com.hd.cedg.lms.model.ExecutionResult;
import com.hd.cedg.lms.support.DefaultErrorExecutionResult;

public class LoadEventRegContactAction extends BaseLmsControllerAction {

	private EventRegistrationDAO eventRegistrationDAO;

	public LoadEventRegContactAction() {
		eventRegistrationDAO = new EventRegistrationDAO();
	}

	protected ExecutionResult executeAction(Map<String, Object> params) {
		EventRegistration registration = (EventRegistration) params
				.get("registration");
		if (registration == null) {
			return noFormObject();
		}

		boolean alreadyRegistered = false;
		if (registration.getUserId() > 0) {
			alreadyRegistered = eventRegistrationDAO.exists(registration
					.getEvent().getEventId(), registration.getUserId());
		}

		ExecutionResult result = new ExecutionResult();
		if (!alreadyRegistered) {
			result.setPage("event-register/reg-contact.jsp");
		} else {
			result.setPage("event-register/already-registered.jsp");
		}
		result.addAttribute("errors", params.get("errors"));
		return result;
	}

	private ExecutionResult noFormObject() {
		return new DefaultErrorExecutionResult();
	}
}

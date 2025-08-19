package com.hd.cedg.lms.action.process;

import java.util.Map;

import com.hd.cedg.lms.action.base.BaseControllerAction;
import com.hd.cedg.lms.dao.EventDAO;
import com.hd.cedg.lms.model.Event;
import com.hd.cedg.lms.model.EventRegistration;
import com.hd.cedg.lms.model.ExecutionResult;
import com.hd.cedg.lms.model.LearningUser;

public class ProcessEventRegStartAction extends BaseControllerAction {

	private EventDAO eventDAO;

	public ProcessEventRegStartAction() {
		eventDAO = new EventDAO();
	}

	public ExecutionResult doAction(Map<String, Object> params) {

		String eventIdStr = (String) params.get("id");
		int eventId = 0;
		try {
			eventId = Integer.parseInt(eventIdStr);
		} catch (NumberFormatException e) {
		}

		if (eventId > 0) {
			LearningUser user = (LearningUser) params.get("user");

			String returnUrl = (String) params.get("returnUrl");
			if (returnUrl == null) {
				returnUrl = "";
			}

			Event event = eventDAO.retrieve(eventId);

			boolean eventOpen = event.getStatus() == Event.STATUS_OPEN;
			boolean eventNotFull = !event.isFull();

			if (eventOpen && eventNotFull) {
				EventRegistration registration = new EventRegistration(event);
				registration.setRegisteringUserId(user.getUserId());
				registration.setReturnUrl(returnUrl);
				params.put("registration", registration);
			}
		}

		return null;
	}
}

package com.hd.cedg.lms.action.load;

import java.util.List;

import java.util.Map;
import com.hd.cedg.lms.action.BaseLmsControllerAction;
import com.hd.cedg.lms.dao.EventDAO;
import com.hd.cedg.lms.dao.EventOtherUserDAO;
import com.hd.cedg.lms.model.EventOtherUser;
import com.hd.cedg.lms.model.EventRegistration;
import com.hd.cedg.lms.model.ExecutionResult;
import com.hd.cedg.lms.model.LearningUser;
import com.hd.cedg.lms.support.DefaultErrorExecutionResult;


public class LoadEventRegUserAction extends BaseLmsControllerAction {

	private EventOtherUserDAO eventOtherUserDAO;
	private EventDAO eventDAO;

	public LoadEventRegUserAction() {
		eventOtherUserDAO = new EventOtherUserDAO();
		eventDAO = new EventDAO();
	}

	protected ExecutionResult executeAction(Map<String, Object> params) {
		LearningUser user = (LearningUser) params.get("user");

		EventRegistration registration = (EventRegistration) params
				.get("registration");

		if (registration == null) {
			return noFormObject();
		}

		List<EventOtherUser> otherUsers = eventOtherUserDAO
				.retrieveListByCompanyAndUserId(registration.getCompanyId(),
						user.getUserId());
		List<Integer> regedUserIds = eventDAO
				.retrieveIdsOfRegisteredUsers(registration.getEvent()
						.getEventId());

		ExecutionResult result = new ExecutionResult();
		result.setPage("event-register/reg-user.jsp");
		result.addAttribute("user", user);
		result.addAttribute("otherUsers", otherUsers);
		result.addAttribute("regedUserIds", regedUserIds);
		result.addAttribute("errors", params.get("errors"));
		return result;
	}

	private ExecutionResult noFormObject() {
		return new DefaultErrorExecutionResult();
	}
}

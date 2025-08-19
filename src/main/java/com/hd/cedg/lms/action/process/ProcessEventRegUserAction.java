package com.hd.cedg.lms.action.process;

import java.util.Map;

import com.hd.cedg.lms.action.base.BaseControllerAction;
import com.hd.cedg.lms.model.EventRegistration;
import com.hd.cedg.lms.model.ExecutionResult;

public class ProcessEventRegUserAction extends BaseControllerAction {

	public ExecutionResult doAction(Map<String, Object> params) {

		EventRegistration registration = (EventRegistration) params
				.get("registration");

		String userIdStr = (String) params.get("userId");
		try {
			int userId = Integer.parseInt(userIdStr);
			registration.setUserId(userId);
		} catch (NumberFormatException e) {
		}
		String firstName = (String) params.get("firstName");
		String lastName = (String) params.get("lastName");
		String phone = (String) params.get("phone");
		String email = (String) params.get("email");

		registration.setFirstName(firstName);
		registration.setLastName(lastName);
		registration.setPhone(phone);
		registration.setEmail(email);

		params.put("registration", registration);

		return null;
	}
}

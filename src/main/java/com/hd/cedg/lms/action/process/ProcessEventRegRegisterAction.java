package com.hd.cedg.lms.action.process;

import java.util.Map;

import com.hd.cedg.lms.action.base.BaseControllerAction;
import com.hd.cedg.lms.bizsvc.EventRegistrationBizSvc;
import com.hd.cedg.lms.model.EventRegistration;
import com.hd.cedg.lms.model.ExecutionResult;
import com.hd.cedg.lms.model.PaypalCredentials;

public class ProcessEventRegRegisterAction extends BaseControllerAction {

	private EventRegistrationBizSvc eventRegistrationBizSvc;

	public ProcessEventRegRegisterAction(String mailServer,
			PaypalCredentials paypalCredentials) {
		eventRegistrationBizSvc = new EventRegistrationBizSvc(mailServer,
				paypalCredentials);
	}

	public ExecutionResult doAction(Map<String, Object> params) {

		EventRegistration registration = (EventRegistration) params
				.get("registration");

		boolean isSuccess = eventRegistrationBizSvc.register(registration);

		params.put("registration", registration);

		ExecutionResult result = new ExecutionResult();
		if (isSuccess) {
			result.setStatus(ExecutionResult.SUCCESS);
		} else {
			result.setStatus(ExecutionResult.FAILURE);
		}
		return result;
	}
}

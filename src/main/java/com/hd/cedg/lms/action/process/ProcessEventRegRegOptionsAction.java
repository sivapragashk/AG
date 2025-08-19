package com.hd.cedg.lms.action.process;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hd.cedg.lms.action.base.BaseControllerAction;
import com.hd.cedg.lms.model.EventOption;
import com.hd.cedg.lms.model.EventOptionPackage;
import com.hd.cedg.lms.model.EventRegistration;
import com.hd.cedg.lms.model.ExecutionResult;

public class ProcessEventRegRegOptionsAction extends BaseControllerAction {

	public ExecutionResult doAction(Map<String, Object> params) {

		EventRegistration registration = (EventRegistration) params
				.get("registration");

		List<Integer> optionIds = new ArrayList<Integer>();
		List<Integer> packageIds = new ArrayList<Integer>();

		for (EventOption option : registration.getEvent().getEventOptions()) {
			String key = "option-" + option.getEventOptionId();
			Object value = params.get(key);
			if (value != null) {
				optionIds.add(option.getEventOptionId());
			}
		}

		for (EventOptionPackage aPackage : registration.getEvent()
				.getEventOptionPackages()) {
			String key = "package-" + aPackage.getPackageId();
			Object value = params.get(key);
			if (value != null) {
				packageIds.add(aPackage.getPackageId());
			}
		}

		registration.setSelectedOptionIds(optionIds);
		registration.setSelectedPackageIds(packageIds);

		params.put("registration", registration);

		return null;
	}
}

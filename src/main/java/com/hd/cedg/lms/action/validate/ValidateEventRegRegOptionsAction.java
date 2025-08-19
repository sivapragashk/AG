package com.hd.cedg.lms.action.validate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hd.cedg.lms.model.Event;
import com.hd.cedg.lms.model.EventOption;
import com.hd.cedg.lms.model.EventOptionPackage;
import com.hd.cedg.lms.model.EventRegistration;
import com.hd.cedg.lms.model.EventRuleMutex;
import com.hd.cedg.lms.model.EventRuleRequiredSet;

public class ValidateEventRegRegOptionsAction extends AbstractValidateAction {

	protected void validate(Map<String, Object> params,
			Map<String, String> errors) {

		EventRegistration registration = (EventRegistration) params
				.get("registration");
		Event event = registration.getEvent();

		// Make a list of the selected Options
		List<Integer> selectedOptionIds = new ArrayList<Integer>();
		for (EventOption option : event.getEventOptions()) {
			String key = "option-" + option.getEventOptionId();
			Object value = params.get(key);
			if (value != null) {
				selectedOptionIds.add(option.getEventOptionId());
			}
		}

		// Make a list of the selected Packages
		List<Integer> selectedPackageIds = new ArrayList<Integer>();
		for (EventOptionPackage aPackage : event.getEventOptionPackages()) {
			String key = "package-" + aPackage.getPackageId();
			Object value = params.get(key);
			if (value != null) {
				selectedPackageIds.add(aPackage.getPackageId());
			}
		}

		// Validate Mutexes
		for (EventRuleMutex mutex : event.getEventRuleMutexes()) {
			if (!mutex.isSatisfied(selectedOptionIds, selectedPackageIds)) {
				errors.put("general",
						"This combination of selected options is not allowed.");
			}
		}

		// Validate Required Sets
		for (EventRuleRequiredSet reqSet : event.getEventRuleRequiredSets()) {
			if (!reqSet.isSatisfied(selectedOptionIds, selectedPackageIds)) {
				errors
						.put("general",
								"Must choose at least one option from the required set.");
			}
		}

		// Validate that full Options aren't selected
		for (EventOption option : event.getEventOptions()) {
			if (option.isFull()
					&& selectedOptionIds.contains(option.getEventOptionId())) {
				errors.put("general", "Cannot choose an option that is full");
			}
		}

		// Validate that full Packages aren't selected
		for (EventOptionPackage aPackage : event.getEventOptionPackages()) {
			if (aPackage.isFull(event.getEventOptions())
					&& selectedPackageIds.contains(aPackage.getPackageId())) {
				errors.put("general", "Cannot choose an option that is full");
			}
		}
	}
}

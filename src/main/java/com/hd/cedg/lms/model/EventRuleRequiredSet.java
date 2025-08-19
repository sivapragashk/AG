package com.hd.cedg.lms.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EventRuleRequiredSet implements Serializable {

	private static final long serialVersionUID = -7568009517282900854L;

	private List<Integer> optionIds;
	private List<Integer> packageIds;

	public EventRuleRequiredSet() {
		optionIds = new ArrayList<Integer>();
		packageIds = new ArrayList<Integer>();
	}

	public List<Integer> getOptionIds() {
		return optionIds;
	}

	public void setOptionIds(List<Integer> optionIds) {
		this.optionIds = optionIds;
	}

	public List<Integer> getPackageIds() {
		return packageIds;
	}

	public void setPackageIds(List<Integer> packageIds) {
		this.packageIds = packageIds;
	}

	public boolean isSatisfied(List<Integer> selectedOptionIds,
			List<Integer> selectedPackageIds) {
		for (Integer optionId : selectedOptionIds) {
			if (optionIds.contains(optionId.intValue())) {
				return true;
			}
		}

		for (Integer packageId : selectedPackageIds) {
			if (packageIds.contains(packageId.intValue())) {
				return true;
			}
		}

		return false;
	}
}

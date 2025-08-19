package com.hd.cedg.lms.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EventRuleMutex implements Serializable {

	private static final long serialVersionUID = 9157440715957314628L;

	private List<Integer> optionIds;
	private List<Integer> packageIds;

	public EventRuleMutex() {
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
		int count = 0;

		for (Integer optionId : selectedOptionIds) {
			if (optionIds.contains(optionId.intValue())) {
				count++;
			}
		}

		for (Integer packageId : selectedPackageIds) {
			if (packageIds.contains(packageId.intValue())) {
				count++;
			}
		}

		return count <= 1;
	}
}

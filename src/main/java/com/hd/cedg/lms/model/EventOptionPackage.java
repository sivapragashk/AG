package com.hd.cedg.lms.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EventOptionPackage implements Serializable {

	private static final long serialVersionUID = 4983455251972149917L;

	private int packageId;

	private String title;
	private String description;
	private List<Integer> optionIds;
	private Cost cost;

	public EventOptionPackage() {
		optionIds = new ArrayList<Integer>();
		cost = new Cost();
	}

	public int getPackageId() {
		return packageId;
	}

	public void setPackageId(int packageId) {
		this.packageId = packageId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Integer> getOptionIds() {
		return optionIds;
	}

	public void setOptionIds(List<Integer> optionIds) {
		this.optionIds = optionIds;
	}

	public Cost getCost() {
		return cost;
	}

	public void setCost(Cost cost) {
		this.cost = cost;
	}

	/**
	 * When given a list of EventOption objects, this will find the dates/times
	 * of the package's contents and return a DateRange for this package.
	 * 
	 * @param eventOptions
	 *            A List of EventOption objects that at least contains all of
	 *            the EventsOptions referenced by this package's optionIds.
	 */
	public DateRange getDateRange(List<EventOption> eventOptions) {
		DateRange dateRange = new DateRange();

		for (EventOption option : eventOptions) {
			if (optionIds.contains(option.getEventOptionId())) {
				DateRange opDateRange = option.getDateRange();
				if (opDateRange.getStartDate() != null
						&& (dateRange.getStartDate() == null || opDateRange
								.getStartDate().compareTo(
										dateRange.getStartDate()) < 0)) {
					dateRange.setStartDate(opDateRange.getStartDate());
				}

				if (opDateRange.getStartDate() != null
						&& (dateRange.getEndDate() == null || opDateRange
								.getStartDate().compareTo(
										dateRange.getEndDate()) > 0)) {
					dateRange.setEndDate(opDateRange.getStartDate());
				}

				if (opDateRange.getEndDate() != null
						&& (dateRange.getEndDate() == null || opDateRange
								.getEndDate().compareTo(dateRange.getEndDate()) > 0)) {
					dateRange.setEndDate(opDateRange.getEndDate());
				}
			}
		}

		return dateRange;
	}

	public boolean isFull(List<EventOption> eventOptions) {
		for (EventOption eventOption : eventOptions) {
			if (optionIds.contains(eventOption.getEventOptionId())
					&& eventOption.isFull()) {
				return true;
			}
		}
		return false;
	}
}

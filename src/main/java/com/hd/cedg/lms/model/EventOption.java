package com.hd.cedg.lms.model;

import java.io.Serializable;

public class EventOption implements Serializable {

	private static final long serialVersionUID = 2831252274869978586L;

	private int eventOptionId;

	private String title;
	private String description;
	private int capacity;
	private int currentAttendeeCount;
	private Cost cost;
	private DateRange dateRange;

	public EventOption() {
		cost = new Cost();
		dateRange = new DateRange();
	}

	public int getEventOptionId() {
		return eventOptionId;
	}

	public void setEventOptionId(int eventOptionId) {
		this.eventOptionId = eventOptionId;
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

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getCurrentAttendeeCount() {
		return currentAttendeeCount;
	}

	public void setCurrentAttendeeCount(int currentAttendeeCount) {
		this.currentAttendeeCount = currentAttendeeCount;
	}

	public boolean isFull() {
		if (capacity > 0 && currentAttendeeCount >= capacity) {
			return true;
		} else {
			return false;
		}
	}

	public Cost getCost() {
		return cost;
	}

	public void setCost(Cost cost) {
		this.cost = cost;
	}

	public DateRange getDateRange() {
		return dateRange;
	}

	public void setDateRange(DateRange dateRange) {
		this.dateRange = dateRange;
	}

}

package com.hd.cedg.lms.model;

import java.io.Serializable;

public class CourseEvent implements Serializable {

	private static final long serialVersionUID = -1876906262994222546L;

	private int eventId;
	private boolean emphasize;

	public CourseEvent() {
		emphasize = false;
	}

	public int getEventId() {
		return eventId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	public boolean isEmphasize() {
		return emphasize;
	}

	public void setEmphasize(boolean emphasize) {
		this.emphasize = emphasize;
	}

}

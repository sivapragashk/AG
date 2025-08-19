package com.hd.cedg.lms.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Transcript implements Serializable {

	private static final long serialVersionUID = 3199828231379074427L;

	private static final String DISPLAY_STRING_NO_DATE = "";

	public static final int STATUS_MIDDLE = 1;
	public static final int STATUS_DONE = 2;

	private String learningId;
	private int status;
	private Date startDate;
	private Date endDate;

	public String getLearningId() {
		return learningId;
	}

	public void setLearningId(String learningId) {
		this.learningId = learningId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getStartDate() {
		return startDate;
	}

	public String getDisplayStartDate() {
		if (startDate == null) {
			return DISPLAY_STRING_NO_DATE;
		} else {
			return generateDisplayDateFormat().format(startDate);
		}
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setStartDate(String dateString, String dateFormat) {
		if ("".equals(dateString)) {
			startDate = null;
		} else if (dateString != null) {
			try {
				startDate = new SimpleDateFormat(dateFormat).parse(dateString);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	public Date getEndDate() {
		return endDate;
	}

	public String getDisplayEndDate() {
		if (endDate == null) {
			return DISPLAY_STRING_NO_DATE;
		} else {
			return generateDisplayDateFormat().format(endDate);
		}
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setEndDate(String dateString, String dateFormat) {
		if ("".equals(dateString)) {
			endDate = null;
		} else if (dateString != null) {
			try {
				endDate = new SimpleDateFormat(dateFormat).parse(dateString);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	private DateFormat generateDisplayDateFormat() {
		return new SimpleDateFormat("M/d/yy");
	}

}

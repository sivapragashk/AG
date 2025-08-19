package com.hd.cedg.lms.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

public class DateRange implements Serializable {

	private static final long serialVersionUID = 1683606731958900413L;

	public static final int TIME_PART_HOUR = 0;
	public static final int TIME_PART_MINUTE = 1;
	public static final int TIME_PART_AMPM = 2;

	private Date startDate;
	private Date endDate;
	private Date startTime;
	private Date endTime;

	public Date getStartDate() {
		return startDate;
	}

	public String getFormattedStartDate(String dateFormat) {
		if (startDate != null) {
			return new SimpleDateFormat(dateFormat).format(startDate);
		} else {
			return "";
		}
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setStartDate(String startDate) {
		if (startDate != null) {
			DateFormat dateFormat = generateInternalDateFormat();
			try {
				this.startDate = dateFormat.parse(startDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else {
			startDate = null;
		}
	}

	public Date getEndDate() {
		return endDate;
	}

	public String getFormattedEndDate(String dateFormat) {
		if (endDate != null) {
			return new SimpleDateFormat(dateFormat).format(endDate);
		} else {
			return "";
		}
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setEndDate(String endDate) {
		if (endDate != null) {
			DateFormat dateFormat = generateInternalDateFormat();
			try {
				this.endDate = dateFormat.parse(endDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else {
			endDate = null;
		}
	}

	public Date getStartTime() {
		return startTime;
	}

	public String getStartTime(int timePart) {
		if (startTime != null) {
			if (timePart == TIME_PART_HOUR) {
				return new SimpleDateFormat("hh").format(startTime);
			} else if (timePart == TIME_PART_MINUTE) {
				return new SimpleDateFormat("mm").format(startTime);
			} else if (timePart == TIME_PART_AMPM) {
				return new SimpleDateFormat("a").format(startTime);
			}
		}
		return "";
	}

	public String getFormattedStartTime(String dateFormat) {
		if (startTime != null) {
			return new SimpleDateFormat(dateFormat).format(startTime);
		} else {
			return "";
		}
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public void setStartTime(String startTime) {
		if (startTime != null) {
			DateFormat dateFormat = generateInternalTimeFormat();
			try {
				this.startTime = dateFormat.parse(startTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else {
			this.startTime = null;
		}
	}

	public Date getEndTime() {
		return endTime;
	}

	public String getEndTime(int timePart) {
		if (endTime != null) {
			if (timePart == TIME_PART_HOUR) {
				return new SimpleDateFormat("hh").format(endTime);
			} else if (timePart == TIME_PART_MINUTE) {
				return new SimpleDateFormat("mm").format(endTime);
			} else if (timePart == TIME_PART_AMPM) {
				return new SimpleDateFormat("a").format(endTime);
			}
		}
		return "";
	}

	public String getFormattedEndTime(String dateFormat) {
		if (endTime != null) {
			return new SimpleDateFormat(dateFormat).format(endTime);
		} else {
			return "";
		}
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public void setEndTime(String endTime) {
		if (endTime != null) {
			DateFormat dateFormat = generateInternalTimeFormat();
			try {
				this.endTime = dateFormat.parse(endTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else {
			this.endTime = null;
		}
	}

	public String getFormattedDateRange() {
		if (startDate == null) {
			return "";
		}
		String startMonth = formatMonth(startDate);
		String startDay = formatDay(startDate);
		String startYear = formatYear(startDate);

		if (endDate == null) {
			return startMonth + " " + startDay + ", " + startYear;
		}

		String endMonth = formatMonth(endDate);
		String endDay = formatDay(endDate);
		String endYear = formatYear(endDate);

		if (!startYear.equals(endYear)) {
			// Dec 30, 2008 - Jan 2, 2009
			return startMonth + " " + startDay + ", " + startYear + " - "
					+ endMonth + " " + endDay + ", " + endYear;
		} else if (!startMonth.equals(endMonth)) {
			// June 28 - July 3, 2009
			return startMonth + " " + startDay + " - " + endMonth + " "
					+ endDay + ", " + startYear;
		} else if (!startDay.equals(endDay)) {
			// July 15-17, 2009
			return startMonth + " " + startDay + "-" + endDay + ", "
					+ startYear;
		} else {
			// July 21, 2009
			return startMonth + " " + startDay + ", " + startYear;
		}
	}

	public String getFormattedTime() {
		return formatTime(startTime, endTime, 0);
	}

	public String getFormattedOffsetTime(int offsetHours) {
		return formatTime(startTime, endTime, offsetHours);
	}

	private String formatTime(Date startTime, Date endTime, int offsetHours) {
		DateFormat timeFormat = new SimpleDateFormat("h:mm a");
		if (startTime == null) {
			return "";
		}
		Calendar startCal = new GregorianCalendar();
		startCal.setTime(startTime);
		startCal.add(Calendar.HOUR, offsetHours);
		String startTimeString = timeFormat.format(startCal.getTime());

		if (endTime == null) {
			return startTimeString;
		}

		Calendar endCal = new GregorianCalendar();
		endCal.setTime(endTime);
		endCal.add(Calendar.HOUR, offsetHours);
		String endTimeString = timeFormat.format(endCal.getTime());

		if (startTimeString.equals(endTimeString)) {
			return startTimeString;
		} else {
			return startTimeString + " - " + endTimeString;
		}
	}

	public static String formatYear(Date d) {
		return new SimpleDateFormat("yyyy").format(d);
	}

	public static String formatMonth(Date d) {
		String month = new SimpleDateFormat("MMMM").format(d);
		if (month.length() > 4) {
			month = month.substring(0, 3);
		}
		return month;
	}

	public static String formatDay(Date d) {
		return new SimpleDateFormat("d").format(d);
	}

	public static String formatHour(Date d) {
		return new SimpleDateFormat("h").format(d);
	}

	public static String formatMinute(Date d) {
		return new SimpleDateFormat("mm").format(d);
	}

	public static String formatAMPM(Date d) {
		return new SimpleDateFormat("a").format(d);
	}

	private DateFormat generateInternalDateFormat() {
		return new SimpleDateFormat("yyyy-MM-dd");
	}

	private DateFormat generateInternalTimeFormat() {
		return new SimpleDateFormat("HH:mm:ss");
	}

	public static String getFormattedDate(Date date, String dateFormat) {
		if (date != null) {
			return new SimpleDateFormat(dateFormat).format(date);
		} else {
			return null;
		}
	}

	public boolean isInDaylightSavings() {
		Calendar datetime = new GregorianCalendar();
		datetime.setTime(startDate);

		if (startTime != null) {
			Calendar timeCal = new GregorianCalendar();
			timeCal.setTime(startTime);
			datetime.set(Calendar.HOUR_OF_DAY, timeCal
					.get(Calendar.HOUR_OF_DAY));
			datetime.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
		} else {
			datetime.set(Calendar.HOUR_OF_DAY, 12);
			datetime.set(Calendar.MINUTE, 12);
		}

		TimeZone tz = new SimpleTimeZone(-18000000, "America/Eastern",
				Calendar.MARCH, 8, -Calendar.SUNDAY, 7200000,
				Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 7200000, 3600000);

		return tz.inDaylightTime(datetime.getTime());
	}
}

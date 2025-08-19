package com.hd.cedg.lms.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToDoList implements Serializable, Comparable<ToDoList> {

	private static final long serialVersionUID = -4723680140863962836L;

	private int toDoListId;
	private String title;
	private String imageUrl;
	private List<ToDoListSet> listSets;
	private String certProgram;
	private int certThruYear;
	private int onCompleteEnroll;
	private EmailTemplate enrollEmail;
	private EmailTemplate completeEmail;
	private boolean active;
	private String programType;
	private String toDoListCompletion;
	private Date fromDate;
	private Date toDate;
	private int numberOfCourses;
	private Map<String, String> program = new HashMap<String, String>();

	public ToDoList() {
		listSets = new ArrayList<ToDoListSet>();
		enrollEmail = new EmailTemplate();
		completeEmail = new EmailTemplate();
		active = false;
		program.put("pdp", "Professional Dealer Program");
		program.put("pfp", "Professional Fabricator Program");
		program.put("pip", "Installer Program");
		program.put("ci", "Installer Program");
		program.put("mi", "Installer Program");
	}

	public int getToDoListId() {
		return toDoListId;
	}

	public void setToDoListId(int toDoListId) {
		this.toDoListId = toDoListId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public List<ToDoListSet> getListSets() {
		return listSets;
	}

	public void setListSets(List<ToDoListSet> listSets) {
		this.listSets = listSets;
	}

	public String getCertProgram() {
		return certProgram;
	}

	public void setCertProgram(String certProgram) {
		this.certProgram = certProgram;
	}

	public int getCertThruYear() {
		return certThruYear;
	}

	public void setCertThruYear(int certThruYear) {
		this.certThruYear = certThruYear;
	}

	public int getOnCompleteEnroll() {
		return onCompleteEnroll;
	}

	public void setOnCompleteEnroll(int onCompleteEnroll) {
		this.onCompleteEnroll = onCompleteEnroll;
	}

	public EmailTemplate getEnrollEmail() {
		return enrollEmail;
	}

	public void setEnrollEmail(EmailTemplate enrollEmail) {
		this.enrollEmail = enrollEmail;
	}

	public EmailTemplate getCompleteEmail() {
		return completeEmail;
	}

	public void setCompleteEmail(EmailTemplate completeEmail) {
		this.completeEmail = completeEmail;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getPermissionType() {
		return "ToDoList";
	}

	public String getProgramType() {
		return programType;
	}

	public void setProgramType(String programType) {
		this.programType = programType;
	}

	public String getToDoListCompletion() {
		return toDoListCompletion;
	}

	public void setToDoListCompletion(String toDoListCompletion) {
		this.toDoListCompletion = toDoListCompletion;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public void setFromDate(String fromDate, String dateFormat) {
		if (fromDate != null && fromDate.length() > 0) {
			try {
				this.fromDate = new SimpleDateFormat(dateFormat)
						.parse(fromDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate){
		this.toDate = toDate;
	}
	
	public void setToDate(String toDate, String dateFormat) {
		if (toDate != null && toDate.length() > 0) {
			try {
				this.toDate = new SimpleDateFormat(dateFormat)
						.parse(toDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getFormattedDate(String dateFormat, Date date) {
		if (date != null) {
			return new SimpleDateFormat(dateFormat).format(date);
		} else {
			return "";
		}
	}

	public int getNumberOfCourses() {
		return numberOfCourses;
	}

	public void setNumberOfCourses(int numberOfCourses) {
		this.numberOfCourses = numberOfCourses;
	}

	public int compareTo(ToDoList toDoList) {
		return title.toUpperCase().compareTo(toDoList.getTitle().toUpperCase());
	}

	public String getProgram(){
		return program.get(certProgram);
	}
	
	public int getAverage(Map<String, Transcript> transcriptMap){
		int percentComplete = 0;
		for (ToDoListSet set : getListSets()) {
			percentComplete += set.calculatePercentageComplete(transcriptMap);
		}
		if(percentComplete > 0){
			percentComplete = percentComplete/getListSets().size();
		}
		return percentComplete;
	}
	
	public int getTotalNumOfCourses(){
		int count = 0;
		for (ToDoListSet set : getListSets()) {
			count += set.getLearningIds().size();
		}
		return count;
	}
}

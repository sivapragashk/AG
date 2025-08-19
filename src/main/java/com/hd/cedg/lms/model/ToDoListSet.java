package com.hd.cedg.lms.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ToDoListSet implements Serializable {

	private static final long serialVersionUID = 5445108827577215624L;

	private int id;
	private String title;
	private List<String> learningIds;
	private List<String> alwaysShowIds;

	public ToDoListSet() {
		learningIds = new ArrayList<String>();
		alwaysShowIds = new ArrayList<String>();
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getLearningIds() {
		return learningIds;
	}

	public void setLearningIds(List<String> learningIds) {
		this.learningIds = learningIds;
	}

	public List<String> getAlwaysShowIds() {
		return alwaysShowIds;
	}

	public void setAlwaysShowIds(List<String> alwaysShowIds) {
		this.alwaysShowIds = alwaysShowIds;
	}

	public int calculatePercentageComplete(Map<String, Transcript> transcriptMap) {
		if (learningIds.size() == 0) {
			return 0;
		}

		int complete = 0;
		for (String learningId : learningIds) {
			Transcript transcript = transcriptMap.get(learningId);
			if (transcript != null
					&& transcript.getStatus() == Transcript.STATUS_DONE) {
				complete++;
			}
		}
		return (int) ((((float) complete) / ((float) learningIds.size())) * 100);
	}

	public String getCompletionStatus(Map<String, Transcript> transcriptMap) {
		if (learningIds.size() == 0) {
			return null;
		}

		int complete = 0;
		for (String learningId : learningIds) {
			Transcript transcript = transcriptMap.get(learningId);
			if (transcript != null
					&& transcript.getStatus() == Transcript.STATUS_DONE) {
				complete++;
			}
		}
		return complete + "/" + learningIds.size();
	}
	
	public String getProgress(int percentComplete){
		String progress = "width:";
		if(percentComplete > 0){
			progress += (percentComplete*2)+"px;";
		}else {
			progress += "0px;";
		}
		return progress;
	}
	
}

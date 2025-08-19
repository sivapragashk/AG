package com.hd.cedg.lms.model;

import java.io.Serializable;

public class EkpTranscript implements Serializable {

	private static final long serialVersionUID = 3534079331603077986L;

	private String learningId;
	private String transcriptId;
	private boolean open;

	public String getLearningId() {
		return learningId;
	}

	public void setLearningId(String learningId) {
		this.learningId = learningId;
	}

	public String getTranscriptId() {
		return transcriptId;
	}

	public void setTranscriptId(String transcriptId) {
		this.transcriptId = transcriptId;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

}

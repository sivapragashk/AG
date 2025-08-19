package com.hd.cedg.lms.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EventDisplayGroup implements Serializable {

	private static final long serialVersionUID = 8088699436799623091L;

	private String title;
	private List<EventDisplayGroupItem> items;

	public EventDisplayGroup() {
		title = "";
		items = new ArrayList<EventDisplayGroupItem>();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<EventDisplayGroupItem> getItems() {
		return items;
	}

	public void setItems(List<EventDisplayGroupItem> items) {
		this.items = items;
	}

}

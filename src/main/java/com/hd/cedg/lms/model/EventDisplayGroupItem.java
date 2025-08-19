package com.hd.cedg.lms.model;

import java.io.Serializable;

public class EventDisplayGroupItem implements Serializable {

	private static final long serialVersionUID = 6675929611151645835L;

	private int itemId;
	private int itemType;

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getItemType() {
		return itemType;
	}

	public void setItemType(int itemType) {
		this.itemType = itemType;
	}

}

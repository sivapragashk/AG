package com.hd.cedg.lms.model;

import java.io.Serializable;

public class CoursePerson implements Serializable {

	private static final long serialVersionUID = -1526430420663278818L;

	private String typeName;
	private String name;
	private String title;
	private String imageUrl;
	private String bioText;

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getBioText() {
		return bioText;
	}

	public void setBioText(String bioText) {
		this.bioText = bioText;
	}

}

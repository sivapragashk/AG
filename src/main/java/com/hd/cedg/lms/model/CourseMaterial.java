package com.hd.cedg.lms.model;

import java.io.Serializable;

public class CourseMaterial implements Serializable {

	private static final long serialVersionUID = 2055265097773129327L;

	private String name;
	private String url;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}

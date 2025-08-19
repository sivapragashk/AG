package com.hd.cedg.lms.model;

import java.util.HashMap;
import java.util.Map;

public class ExecutionResult {

	public final static String SUCCESS = "success";
	public final static String FAILURE = "failure";

	private String status;
	private String page;
	private Map<String, Object> attributes;
	private boolean refreshCachedParams;

	public ExecutionResult() {
		attributes = new HashMap<String, Object>();
		refreshCachedParams = false;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public void addAttribute(String key, Object value) {
		attributes.put(key, value);
	}

	public boolean isRefreshCachedParams() {
		return refreshCachedParams;
	}

	public void setRefreshCachedParams(boolean refreshCachedParams) {
		this.refreshCachedParams = refreshCachedParams;
	}

}

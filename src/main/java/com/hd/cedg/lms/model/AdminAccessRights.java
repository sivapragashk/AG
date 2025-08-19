package com.hd.cedg.lms.model;

import java.io.Serializable;

public class AdminAccessRights implements Serializable {

	private static final long serialVersionUID = -1595416890072584116L;

	private boolean adminAdmin;
	private boolean userAdmin;
	private boolean courseAdmin;
	private boolean todoAdmin;
	private boolean ekpAdmin;
	private boolean directAdmin;
	private boolean eventEditAdmin;
	private boolean eventSuperAdmin;

	public AdminAccessRights() {
		adminAdmin = false;
		userAdmin = false;
		courseAdmin = false;
		todoAdmin = false;
		ekpAdmin = false;
		directAdmin = false;
		eventEditAdmin = false;
		eventSuperAdmin = false;
	}

	public boolean isAdmin() {
		return adminAdmin || userAdmin || courseAdmin || todoAdmin || ekpAdmin
				|| directAdmin || eventEditAdmin || eventSuperAdmin;
	}

	public boolean isAdminAdmin() {
		return adminAdmin;
	}

	public void setAdminAdmin(boolean adminAdmin) {
		this.adminAdmin = adminAdmin;
	}

	public boolean isUserAdmin() {
		return userAdmin;
	}

	public void setUserAdmin(boolean userAdmin) {
		this.userAdmin = userAdmin;
	}

	public boolean isCourseAdmin() {
		return courseAdmin;
	}

	public void setCourseAdmin(boolean courseAdmin) {
		this.courseAdmin = courseAdmin;
	}

	public boolean isTodoAdmin() {
		return todoAdmin;
	}

	public void setTodoAdmin(boolean todoAdmin) {
		this.todoAdmin = todoAdmin;
	}

	public boolean isEkpAdmin() {
		return ekpAdmin;
	}

	public void setEkpAdmin(boolean ekpAdmin) {
		this.ekpAdmin = ekpAdmin;
	}

	public boolean isDirectAdmin() {
		return directAdmin;
	}

	public void setDirectAdmin(boolean directAdmin) {
		this.directAdmin = directAdmin;
	}

	public boolean isEventEditAdmin() {
		return eventEditAdmin;
	}

	public void setEventEditAdmin(boolean eventEditAdmin) {
		this.eventEditAdmin = eventEditAdmin;
	}

	public boolean isEventSuperAdmin() {
		return eventSuperAdmin;
	}

	public void setEventSuperAdmin(boolean eventSuperAdmin) {
		this.eventSuperAdmin = eventSuperAdmin;
	}

}

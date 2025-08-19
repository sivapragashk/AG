package com.hd.cedg.lms.model;

import java.util.ArrayList;
import java.util.List;

public class CertificationStatus {

	public CertificationStatus(){
		percentage = new ArrayList<Integer>();
	}
	
	private int todolistId;
	private String name;
	private String type;
	private List<Integer> percentage;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public int getTodolistId() {
		return todolistId;
	}
	public void setTodolistId(int todolistId) {
		this.todolistId = todolistId;
	}
	public List<Integer> getPercentage() {
		return percentage;
	}
	public int getAverage(){
		int average = 0;
		for(int value : percentage){
			average += value;
		}
		return average/percentage.size();
	}
	public void setPercentage(int percentage) {
		this.percentage.add(percentage);
	}
		
}

package com.hd.cedg.lms.support;

import java.util.Map;

import com.hd.cedg.lms.model.ExecutionResult;

public class DefaultErrorExecutionResult extends ExecutionResult {
	
	public DefaultErrorExecutionResult(){
		super();
	}
	
	public DefaultErrorExecutionResult(Map<String, Object> attributes){
		setAttributes(attributes);
	}
	
	public String getPage(){
		return "error.jsp";
	}

}

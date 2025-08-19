package com.hd.cedg.lms.action.base;

import java.util.Map;

import com.hd.cedg.lms.model.ExecutionResult;


public abstract class BaseControllerAction {
	
	public abstract ExecutionResult doAction(Map<String, Object> params);

}

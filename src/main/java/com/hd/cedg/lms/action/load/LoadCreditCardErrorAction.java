package com.hd.cedg.lms.action.load;

import java.util.Map;

import com.hd.cedg.lms.action.BaseLmsControllerAction;
import com.hd.cedg.lms.model.ExecutionResult;


public class LoadCreditCardErrorAction extends BaseLmsControllerAction {

	protected ExecutionResult executeAction(Map<String, Object> params) {
		ExecutionResult result = new ExecutionResult();

		result.setPage("cc-error.jsp");

		return result;
	}
}

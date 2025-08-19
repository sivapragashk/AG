package com.hd.cedg.lms.action.load;

import java.util.Map;

import com.hd.cedg.lms.action.BaseLmsControllerAction;
import com.hd.cedg.lms.model.ExecutionResult;

public class LoadAJAXResponseAction extends BaseLmsControllerAction {

	private String responseText;

	public LoadAJAXResponseAction(String redirectUrl) {
		this.responseText = redirectUrl;
	}

	protected ExecutionResult executeAction(Map<String, Object> params) {
		ExecutionResult result = new ExecutionResult();

		result.setPage("ajax-response.jsp");
		result.addAttribute("responseText", responseText);

		return result;
	}
}

package com.hd.cedg.lms.action.load;

import java.util.Map;

import com.hd.cedg.lms.action.BaseLmsControllerAction;
import com.hd.cedg.lms.model.ExecutionResult;

public class LoadRedirectAction extends BaseLmsControllerAction {

	private String redirectUrl;

	public LoadRedirectAction(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	protected ExecutionResult executeAction(Map<String, Object> params) {
		ExecutionResult result = new ExecutionResult();

		result.setPage("redirect.jsp");
		result.addAttribute("redirectUrl", redirectUrl);

		return result;
	}
}

package com.hd.cedg.lms.action;

import java.util.Map;

import com.hd.cedg.lms.action.base.BaseControllerAction;
import com.hd.cedg.lms.model.ExecutionResult;


public abstract class BaseLmsControllerAction extends BaseControllerAction {

	public ExecutionResult doAction(Map<String, Object> params) {
		ExecutionResult result = executeAction(params);

		String analyticsId = (String) params.get("analyticsId");
		result.addAttribute("analyticsId", analyticsId);

		Object user = params.get("user");
		result.addAttribute("user", user);
		Object allowedToMasquerade = params.get("allowedToMasquerade");
		result.addAttribute("allowedToMasquerade", allowedToMasquerade);
		Object adminUser = params.get("adminUser");
		result.addAttribute("adminUser", adminUser);
		Object isExtMasq = params.get("isExtMasq");
		result.addAttribute("isExtMasq", isExtMasq);
		Boolean lockdown = new Boolean(adminUser != null);
		result.addAttribute("lockdown", lockdown);

		return result;
	}

	protected abstract ExecutionResult executeAction(Map<String, Object> params);

}

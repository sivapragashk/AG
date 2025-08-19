package com.hd.cedg.lms.action.validate;

import java.util.Map;

public class ValidatePfpSignupAction extends AbstractValidateAction {

	protected void validate(Map<String, Object> params,
			Map<String, String> errors) {

		String id = (String) params.get("id");
		String fabDepartment = (String) params.get("dept");

		// Required fields
		notEmpty(id, "general", errors, "Error processing your request.");
		notEmpty(fabDepartment, "general", errors,
				"Error processing your request.");
	}

}

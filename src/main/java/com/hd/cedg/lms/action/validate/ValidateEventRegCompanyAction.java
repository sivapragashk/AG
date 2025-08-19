package com.hd.cedg.lms.action.validate;

import java.util.Map;

public class ValidateEventRegCompanyAction extends AbstractValidateAction {

	protected void validate(Map<String, Object> params,
			Map<String, String> errors) {

		String companyId = (String) params.get("companyId");

		validInt(companyId, true, false, false, "general", errors,
				"Error processing your selection");
	}

}

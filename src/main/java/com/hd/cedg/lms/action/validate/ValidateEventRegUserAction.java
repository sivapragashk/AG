package com.hd.cedg.lms.action.validate;

import java.util.Map;

public class ValidateEventRegUserAction extends AbstractValidateAction {

	protected void validate(Map<String, Object> params,
			Map<String, String> errors) {

		String userId = (String) params.get("userId");

		/*
		 * Trying to register user from a company that is not listed, in this case
		 * UserHDID and CompanyHDID are optional fields.
		 * But company name and user name has to be entered which will later be added to ONYX
		 * by LC Administrator   
		 */
		int companyId = 0;
		String strCompanyId = (String) params.get("companyId");
		try{
			companyId = Integer.parseInt(strCompanyId);
		}catch(Exception ex){ }
		if(companyId > 0) {
			validInt(userId, true, true, false, "general", errors,
					"Error processing your selection");
		}
	}

}

package com.hd.cedg.lms.action.validate;

import java.util.Map;

public class ValidateCoursePaymentAction extends AbstractValidateAction {

	protected void validate(Map<String, Object> params,
			Map<String, String> errors) {

		String id = (String) params.get("id");
		String ccNumber = (String) params.get("cc-number");
		String expDate = (String) params.get("exp-date");
		String csc = (String) params.get("csc");
		String name = (String) params.get("name");
		String address = (String) params.get("address");
		String city = (String) params.get("city");
		String region = (String) params.get("region");
		String postCode = (String) params.get("postcode");

		// Required fields
		notEmpty(id, "general", errors, "Required");
		notEmpty(ccNumber, "cc-number", errors, "Required");
		boolean hasExpDate = notEmpty(expDate, "exp-date", errors, "Required");
		boolean hasCsc = notEmpty(csc, "csc", errors, "Required");
		notEmpty(name, "name", errors, "Required");
		notEmpty(address, "address", errors, "Required");
		notEmpty(city, "city", errors, "Required");
		notEmpty(region, "region", errors, "Required");
		notEmpty(postCode, "postcode", errors, "Required");

		// Expiration Date (with non-digits filtered out must be 4 characters
		if (hasExpDate) {
			String formattedExpDate = expDate.replaceAll("\\D", "");
			if (formattedExpDate.length() != 4) {
				errors.put("exp-date", "Incorrect Format: Must match \"MMYY\"");
			}
		}

		// CSC must be 3 or 4 digits
		if (hasCsc) {
			String formattedCsc = csc.replaceAll("\\D", "");
			if (!csc.equals(formattedCsc)) {
				errors.put("csc", "Invalid Value");
			} else if (csc.length() != 3 && csc.length() != 4) {
				errors.put("csc", "Invalid Value");
			}
		}
	}

}

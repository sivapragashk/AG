package com.hd.cedg.lms.action.validate;

import java.util.Map;

public class ValidateEventRegContactAction extends AbstractValidateAction {

	protected void validate(Map<String, Object> params,
			Map<String, String> errors) {

		String companyName = (String) params.get("companyName");
		String first = (String) params.get("first");
		String last = (String) params.get("last");
		String address = (String) params.get("address");
		String city = (String) params.get("city");
		String region = (String) params.get("region");
		String postcode = (String) params.get("postcode");
		String rawPhone = (String) params.get("phone");
		String phone = rawPhone.replaceAll("\\D", "");
		String email = (String) params.get("email");

		
		notEmpty(companyName, "companyName", errors, "Required");
		notEmpty(first, "first", errors, "Required");
		notEmpty(last, "last", errors, "Required");
		notEmpty(address, "address", errors, "Required");
		notEmpty(city, "city", errors, "Required");
		notEmpty(region, "region", errors, "Required");
		notEmpty(postcode, "postcode", errors, "Required");
		notEmpty(phone, "phone", errors, "Required");
		notEmpty(email, "email", errors, "Required");

		maxLength(companyName, 100, "companyName", errors);
		maxLength(first, 40, "first", errors);
		maxLength(last, 40, "last", errors);
		maxLength(address, 100, "address", errors);
		maxLength(city, 40, "city", errors);
		maxLength(region, 20, "region", errors);
		maxLength(postcode, 20, "postcode", errors);
		maxLength(phone, 40, "phone", errors);
		maxLength(email, 100, "email", errors);
	}

}

package com.hd.cedg.lms.action.validate;

import java.util.Map;

import com.hd.cedg.lms.model.EventPaymentMethod;
import com.hd.cedg.lms.model.EventPaymentMethodDetail;
import com.hd.cedg.lms.model.EventRegistration;

public class ValidateEventRegPaymentAction extends AbstractValidateAction {

	protected void validate(Map<String, Object> params,
			Map<String, String> errors) {

		EventRegistration registration = (EventRegistration) params
				.get("registration");

		String paymentIdStr = (String) params.get("payment");

		boolean hasValidPaymentId = validInt(paymentIdStr, true, true, false,
				"payment", errors, "Invalid payment method");

		if (hasValidPaymentId) {
			int paymentId = -1;
			try {
				paymentId = Integer.parseInt(paymentIdStr);
			} catch (NumberFormatException e) {
			}

			if (paymentId == EventPaymentMethod.PAYMENT_ID_CC) {
				String ccNumber = (String) params.get("cc-number");
				String expDate = (String) params.get("exp-date");
				String csc = (String) params.get("csc");
				String name = (String) params.get("name");
				String address = (String) params.get("address");
				String city = (String) params.get("city");
				String region = (String) params.get("region");
				String postCode = (String) params.get("postcode");

				// Required fields
				notEmpty(ccNumber, "cc-number", errors, "Required");
				boolean hasExpDate = notEmpty(expDate, "exp-date", errors,
						"Required");
				boolean hasCsc = notEmpty(csc, "csc", errors, "Required");
				notEmpty(name, "name", errors, "Required");
				notEmpty(address, "address", errors, "Required");
				notEmpty(city, "city", errors, "Required");
				notEmpty(region, "region", errors, "Required");
				notEmpty(postCode, "postcode", errors, "Required");

				// Expiration Date (with non-digits filtered out must be 4
				// characters
				if (hasExpDate) {
					String formattedExpDate = expDate.replaceAll("\\D", "");
					if (formattedExpDate.length() != 4) {
						errors.put("exp-date",
								"Incorrect Format: Must match \"MMYY\"");
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
			} else {
				boolean legalPaymentId = false;
				for (EventPaymentMethod pay : registration.getEvent()
						.getPaymentMethods()) {
					if (pay.getPaymentMethodId() == paymentId) {
						legalPaymentId = true;
						for (EventPaymentMethodDetail detail : pay
								.getPaymentMethodDetails()) {
							String fieldName = "detail-"
								+ paymentId + "-"
								+ detail.getPaymentDetailId();
							String answer = (String) params.get(fieldName);
							if (detail.isRequired()) {
								notEmpty(answer, fieldName, errors, "Required");
							}
							maxLength(answer, 400, fieldName, errors);
						}
					}
				}
				if (!legalPaymentId) {
					errors.put("payment", "Invalid payment method");
				}
			}
		}
	}
}

package com.hd.cedg.lms.action.process;

import java.util.HashMap;
import java.util.Map;

import com.hd.cedg.lms.action.base.BaseControllerAction;
import com.hd.cedg.lms.model.EventPaymentMethod;
import com.hd.cedg.lms.model.EventPaymentMethodDetail;
import com.hd.cedg.lms.model.EventRegistration;
import com.hd.cedg.lms.model.ExecutionResult;

public class ProcessEventRegPaymentAction extends BaseControllerAction {

	public ExecutionResult doAction(Map<String, Object> params) {

		EventRegistration registration = (EventRegistration) params
				.get("registration");

		// PAYMENT ID
		String paymentIdStr = (String) params.get("payment");
		int paymentId = -1;
		try {
			paymentId = Integer.parseInt(paymentIdStr);
		} catch (NumberFormatException e) {
		}
		registration.setPaymentMethodId(paymentId);

		// PAYMENT DETAILS
		Map<Integer, String> paymentDetailAnswers = new HashMap<Integer, String>();
		if (paymentId == EventPaymentMethod.PAYMENT_ID_CC) {
			String rawCcNumber = (String) params.get("cc-number");
			String ccNumber = rawCcNumber.replaceAll("\\D", "");
			String rawExpDate = (String) params.get("exp-date");
			String expDate = rawExpDate.replaceAll("\\D", "");
			String csc = (String) params.get("csc");
			String name = (String) params.get("name");
			String address = (String) params.get("address");
			String city = (String) params.get("city");
			String region = (String) params.get("region");
			String postCode = (String) params.get("postcode");

			paymentDetailAnswers.put(new Integer(
					EventPaymentMethodDetail.DETAIL_ID_CC_NUMBER), ccNumber);
			paymentDetailAnswers.put(new Integer(
					EventPaymentMethodDetail.DETAIL_ID_EXP_DATE), expDate);
			paymentDetailAnswers.put(new Integer(
					EventPaymentMethodDetail.DETAIL_ID_CSC), csc);
			paymentDetailAnswers.put(new Integer(
					EventPaymentMethodDetail.DETAIL_ID_NAME), name);
			paymentDetailAnswers.put(new Integer(
					EventPaymentMethodDetail.DETAIL_ID_ADDRESS), address);
			paymentDetailAnswers.put(new Integer(
					EventPaymentMethodDetail.DETAIL_ID_CITY), city);
			paymentDetailAnswers.put(new Integer(
					EventPaymentMethodDetail.DETAIL_ID_REGION), region);
			paymentDetailAnswers.put(new Integer(
					EventPaymentMethodDetail.DETAIL_ID_POSTCODE), postCode);
		} else {
			for (EventPaymentMethod pay : registration.getEvent()
					.getPaymentMethods()) {
				if (pay.getPaymentMethodId() == paymentId) {
					for (EventPaymentMethodDetail detail : pay
							.getPaymentMethodDetails()) {
						int detailId = detail.getPaymentDetailId();
						String answer = (String) params.get("detail-"
								+ paymentId + "-" + detailId);
						paymentDetailAnswers.put(new Integer(detailId), answer);
					}
				}
			}
		}
		registration.setPaymentMethodDetailAnswers(paymentDetailAnswers);

		params.put("registration", registration);

		return null;
	}
}

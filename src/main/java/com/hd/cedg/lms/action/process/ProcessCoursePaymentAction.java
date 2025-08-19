package com.hd.cedg.lms.action.process;

import java.util.Map;

import com.hd.cedg.lms.action.base.BaseControllerAction;
import com.hd.cedg.lms.dao.CourseDAO;
import com.hd.cedg.lms.dao.CoursePaymentDAO;
import com.hd.cedg.lms.model.Course;
import com.hd.cedg.lms.model.ExecutionResult;
import com.hd.cedg.lms.model.LearningUser;
import com.hd.cedg.lms.model.PaypalCredentials;
import com.hd.mis.paypal.bizsvc.PaypalTransactionBizSvc;
import com.hd.mis.paypal.model.PaypalTransaction;
import com.hd.mis.paypal.model.PaypalTransactionResponse;

public class ProcessCoursePaymentAction extends BaseControllerAction {

	CourseDAO courseDAO;
	CoursePaymentDAO coursePaymentDAO;
	PaypalCredentials paypalCredentials;

	public ProcessCoursePaymentAction(PaypalCredentials paypalCredentials) {
		courseDAO = new CourseDAO();
		coursePaymentDAO = new CoursePaymentDAO();
		this.paypalCredentials = paypalCredentials;
	}

	public ExecutionResult doAction(Map<String, Object> params) {

		LearningUser user = (LearningUser) params.get("user");

		String learningId = (String) params.get("id");

		Course course = null;
		if (learningId != null) {
			course = courseDAO.retrieve(learningId);
		}

		if (course == null) {
			ExecutionResult result = new ExecutionResult();
			result.setStatus(ExecutionResult.FAILURE);
			return result;
		}

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

		PaypalTransaction paypalTransaction = new PaypalTransaction();

		paypalTransaction.setBillingCcNumber(ccNumber);
		paypalTransaction.setBillingExpDate(expDate);
		paypalTransaction.setBillingCsc(csc);
		paypalTransaction.setBillingName(name);
		paypalTransaction.setBillingAddress(address);
		paypalTransaction.setBillingCity(city);
		paypalTransaction.setBillingRegion(region);
		paypalTransaction.setBillingPostCode(postCode);

		paypalTransaction.setComment1("Learning Center");
		paypalTransaction.setComment2("Course: " + learningId);

		String username = paypalCredentials.getUsername();
		String vendor = paypalCredentials.getVendor();
		String partner = paypalCredentials.getPartner();
		String password = paypalCredentials.getPassword();
		String hostAddress = paypalCredentials.getHostAddress();

		double amount = Double.parseDouble(course.getFormattedCost(false));
		paypalTransaction.setAmount(amount);

		System.out.println();
		System.out
				.println("***   LEARNING CENTER: ATTEMPTING PAYMENT TO PAYPAL ***");
		System.out.println("***   User " + user.getUserId() + "("
				+ user.getFirstName() + " " + user.getLastName()
				+ ") is puchasing course " + learningId);
		System.out.println("***   Host Address: " + hostAddress);

		PaypalTransactionBizSvc paypalTransactionBizSvc = new PaypalTransactionBizSvc(
				username, vendor, partner, password, hostAddress);
		PaypalTransactionResponse paypalTransactionResponse = paypalTransactionBizSvc
				.process(paypalTransaction);
		int resultCode = paypalTransactionResponse.getResultCode();

		System.out.println("***   Transaction ID: "
				+ paypalTransactionResponse.getTransactionId());
		System.out.println("***   Result Code: "
				+ paypalTransactionResponse.getResultCode());
		System.out.println("***   Response Message: "
				+ paypalTransactionResponse.getResponseMessage());
		System.out.println("***   User Message: "
				+ paypalTransactionResponse.getUserMessage());
		System.out.println();

		ExecutionResult result = new ExecutionResult();
		if (resultCode == 0) {
			int userId = user.getUserId();
			coursePaymentDAO.insertPaid(learningId, userId);

			result.setStatus(ExecutionResult.SUCCESS);
		} else {
			result.setStatus(ExecutionResult.FAILURE);
		}

		return result;
	}
}

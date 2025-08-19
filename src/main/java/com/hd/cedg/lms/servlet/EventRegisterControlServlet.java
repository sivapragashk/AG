package com.hd.cedg.lms.servlet;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.hd.cedg.lms.action.load.LoadCreditCardErrorAction;
import com.hd.cedg.lms.action.load.LoadEventRegCompanyAction;
import com.hd.cedg.lms.action.load.LoadEventRegConfirmAction;
import com.hd.cedg.lms.action.load.LoadEventRegContactAction;
import com.hd.cedg.lms.action.load.LoadEventRegEndAction;
import com.hd.cedg.lms.action.load.LoadEventRegPaymentAction;
import com.hd.cedg.lms.action.load.LoadEventRegQuestionsAction;
import com.hd.cedg.lms.action.load.LoadEventRegRegOptionAction;
import com.hd.cedg.lms.action.load.LoadEventRegTabAction;
import com.hd.cedg.lms.action.load.LoadEventRegUserAction;
import com.hd.cedg.lms.action.process.ProcessEventRegCompanyAction;
import com.hd.cedg.lms.action.process.ProcessEventRegConfirmAction;
import com.hd.cedg.lms.action.process.ProcessEventRegContactAction;
import com.hd.cedg.lms.action.process.ProcessEventRegPaymentAction;
import com.hd.cedg.lms.action.process.ProcessEventRegQuestionsAction;
import com.hd.cedg.lms.action.process.ProcessEventRegRegOptionsAction;
import com.hd.cedg.lms.action.process.ProcessEventRegRegisterAction;
import com.hd.cedg.lms.action.process.ProcessEventRegStartAction;
import com.hd.cedg.lms.action.process.ProcessEventRegUserAction;
import com.hd.cedg.lms.action.validate.ValidateEventRegCompanyAction;
import com.hd.cedg.lms.action.validate.ValidateEventRegConfirmAction;
import com.hd.cedg.lms.action.validate.ValidateEventRegContactAction;
import com.hd.cedg.lms.action.validate.ValidateEventRegPaymentAction;
import com.hd.cedg.lms.action.validate.ValidateEventRegQuestionsAction;
import com.hd.cedg.lms.action.validate.ValidateEventRegRegOptionsAction;
import com.hd.cedg.lms.action.validate.ValidateEventRegUserAction;
import com.hd.cedg.lms.dao.ReportDAO;
import com.hd.cedg.lms.model.EventPaymentMethod;
import com.hd.cedg.lms.model.EventRegistration;
import com.hd.cedg.lms.model.ExecutionResult;
import com.hd.cedg.lms.model.LearningUser;
import com.hd.cedg.lms.model.PaypalCredentials;
import com.hd.cedg.lms.model.ReportCompany;
import com.hd.cedg.lms.support.DefaultErrorExecutionResult;


public class EventRegisterControlServlet<MgrReportDefinition> extends FlowControlServlet<Serializable> {

	private static final long serialVersionUID = -4154358532885016036L;
	
	public static final String FLOW_STATE_COMPANY = "company";
	public static final String FLOW_STATE_USER = "user";
	public static final String FLOW_STATE_CONTACT = "contact";
	public static final String FLOW_STATE_REG_OPTIONS = "reg-options";
	public static final String FLOW_STATE_QUESTIONS = "questions";
	public static final String FLOW_STATE_PAYMENT = "payment";
	public static final String FLOW_STATE_CONFIRM = "confirm";
	public static final String FLOW_STATE_END = "end";

	public static final String CONTROLLER_ACTION_NEXT = "next";
	
	
	protected ExecutionResult executeFlow(String action, String state, Map<String, Object> params) {
				
		if ("start".equals(state)) {
			closeStart(params);
			//return viewCompany(params);
			new LoadEventRegTabAction().doAction(params);
			return decideAllowedEnrollMultipleCompanies(params);
		} else if (FLOW_STATE_COMPANY.equals(state)) {
			String closeStatus = closeCompany(params);
			if (ExecutionResult.SUCCESS.equals(closeStatus)) {
				return viewUser(params);
			} else {
				return viewCompany(params);
			}
		} else if (FLOW_STATE_USER.equals(state)) {
			String closeStatus = closeUser(params);
			if (ExecutionResult.SUCCESS.equals(closeStatus)) {
				return viewContact(params);
			} else {
				return viewUser(params);
			}
		} else if (FLOW_STATE_CONTACT.equals(state)) {
			String closeStatus = closeContact(params);
			if (ExecutionResult.SUCCESS.equals(closeStatus)) {
				return decideHasOptions(params);
			} else {
				return viewContact(params);
			}
		} else if (FLOW_STATE_REG_OPTIONS.equals(state)) {
			String closeStatus = closeOptions(params);
			if (ExecutionResult.SUCCESS.equals(closeStatus)) {
				return decideHasQuestions(params);
			} else {
				return viewOptions(params);
			}
		} else if (FLOW_STATE_QUESTIONS.equals(state)) {
			String closeStatus = closeQuestions(params);
			if (ExecutionResult.SUCCESS.equals(closeStatus)) {
				return decideHasCost(params);
			} else {
				return viewQuestions(params);
			}
		} else if (FLOW_STATE_PAYMENT.equals(state)) {
			String closeStatus = closePayment(params);
			if (ExecutionResult.SUCCESS.equals(closeStatus)) {
				return viewConfirm(params);
			} else {
				return viewPayment(params);
			}
		} else if (FLOW_STATE_CONFIRM.equals(state)) {
			String closeStatus = closeConfirm(params);
			if (ExecutionResult.SUCCESS.equals(closeStatus)) {
				String executeStatus = executeRegister(params);
				if (ExecutionResult.SUCCESS.equals(executeStatus)) {
					executePIPMemberCheck(params);
					return viewEnd(params);
				} else {
					return viewCCError(params);
				}
			} else {
				return viewConfirm(params);
			}
		}
		
		return new DefaultErrorExecutionResult();
	}
	
	private String closeStart(Map<String, Object> params) {
		new ProcessEventRegStartAction().doAction(params);
		return ExecutionResult.SUCCESS;
	}

	private String closeCompany(Map<String, Object> params) {
		ExecutionResult valid = new ValidateEventRegCompanyAction()
				.doAction(params);
		new ProcessEventRegCompanyAction().doAction(params);
		return valid.getStatus();
	}

	private String closeUser(Map<String, Object> params) {
		ExecutionResult valid = new ValidateEventRegUserAction()
				.doAction(params);
		new ProcessEventRegUserAction().doAction(params);
		return valid.getStatus();
	}

	private String closeContact(Map<String, Object> params) {
		ExecutionResult valid = new ValidateEventRegContactAction()
				.doAction(params);
		new ProcessEventRegContactAction().doAction(params);
		return valid.getStatus();
	}

	private String closeOptions(Map<String, Object> params) {
		ExecutionResult valid = new ValidateEventRegRegOptionsAction()
				.doAction(params);
		new ProcessEventRegRegOptionsAction().doAction(params);
		return valid.getStatus();
	}

	private String closeQuestions(Map<String, Object> params) {
		ExecutionResult valid = new ValidateEventRegQuestionsAction()
				.doAction(params);
		new ProcessEventRegQuestionsAction().doAction(params);
		return valid.getStatus();
	}

	private String closePayment(Map<String, Object> params) {
		ExecutionResult valid = new ValidateEventRegPaymentAction()
				.doAction(params);
		new ProcessEventRegPaymentAction().doAction(params);
		return valid.getStatus();
	}

	private String closeConfirm(Map<String, Object> params) {
		ExecutionResult valid = new ValidateEventRegConfirmAction()
				.doAction(params);
		new ProcessEventRegConfirmAction().doAction(params);
		return valid.getStatus();
	}

	private void executePIPMemberCheck(Map<String, Object> params){
		//new ProcessPIPMemberCheckAction().doAction(params);
	}
	
	private String executeRegister(Map<String, Object> params) {
		String mailServer = getServletContext().getInitParameter(
				"EMAIL_SERVER_URL");
		PaypalCredentials paypalCredentials = new PaypalCredentials();
		paypalCredentials.setUsername(getServletContext().getInitParameter(
				"PAYPAL_USERNAME"));
		paypalCredentials.setVendor(getServletContext().getInitParameter(
				"PAYPAL_VENDOR"));
		paypalCredentials.setPartner(getServletContext().getInitParameter(
				"PAYPAL_PARTNER"));
		paypalCredentials.setPassword(getServletContext().getInitParameter(
				"PAYPAL_PASSWORD"));
		paypalCredentials.setHostAddress(getServletContext().getInitParameter(
				"PAYPAL_HOSTADDR"));

		ExecutionResult result = new ProcessEventRegRegisterAction(mailServer,
				paypalCredentials).doAction(params);
		return result.getStatus();
	}

	private ExecutionResult viewCompany(Map<String, Object> params) {
		return new LoadEventRegCompanyAction().doAction(params);
	}

	private ExecutionResult viewUser(Map<String, Object> params) {
		return new LoadEventRegUserAction().doAction(params);
	}

	private ExecutionResult viewContact(Map<String, Object> params) {
		return new LoadEventRegContactAction().doAction(params);
	}

	private ExecutionResult viewOptions(Map<String, Object> params) {
		return new LoadEventRegRegOptionAction().doAction(params);
	}

	private ExecutionResult viewQuestions(Map<String, Object> params) {
		return new LoadEventRegQuestionsAction().doAction(params);
	}

	private ExecutionResult viewPayment(Map<String, Object> params) {
		return new LoadEventRegPaymentAction().doAction(params);
	}

	private ExecutionResult viewConfirm(Map<String, Object> params) {
		return new LoadEventRegConfirmAction().doAction(params);
	}

	private ExecutionResult viewEnd(Map<String, Object> params) {
		return new LoadEventRegEndAction().doAction(params);
	}

	private ExecutionResult viewCCError(Map<String, Object> params) {
		return new LoadCreditCardErrorAction().doAction(params);
	}

	private ExecutionResult decideAllowedEnrollMultipleCompanies(
			Map<String, Object> params) {
		EventRegistration registration = (EventRegistration) params
				.get("registration");
		LearningUser user = (LearningUser) params.get("user");
		boolean decide = false;
		if (registration.getEvent().isEnrollManaged()) {
			ReportDAO reportDAO = new ReportDAO();
			List<ReportCompany> reportCompanies = reportDAO
					.retrieveCompaniesForUser(user);
			decide = reportCompanies.size() > 1
					 	|| (reportCompanies.size() == 1 && reportCompanies.get(0)
						 	.getCompanyId() != user.getCompanyId());
		}

		if (decide) {
			return viewCompany(params);
		} else {
			// Need to set static company stuff to the user's values. Probably
			// belongs in a "Process" action, but I'm being lazy
			registration.setCompanyId(user.getCompanyId());
			registration.setCompanyName(user.getCompanyName());
			registration.setCompanyType(user.getCompanyType());
			registration.setAllianceLevel(user.getAllianceLevel());
			registration.setFabricator(user.getSponsoringFabricatorName());
			registration.setSalesRep(user.getSalesRepName());
			registration.setAddress(user.getAddress1());
			registration.setCity(user.getCity());
			registration.setRegion(user.getRegion());
			registration.setPostCode(user.getPostCode());
			params.put("registration", registration);
			return decideAllowedEnrollOthers(params);
		}
	}

	private ExecutionResult decideAllowedEnrollOthers(Map<String, Object> params) {
		EventRegistration registration = (EventRegistration) params
				.get("registration");
		boolean decide = registration.getEvent().isEnrollOthers();

		if (decide) {
			return viewUser(params);
		} else {
			// Need to set static user stuff to the user's values. Probably
			// belongs in a "Process" action, but I'm being lazy
			LearningUser user = (LearningUser) params.get("user");
			registration.setUserId(user.getUserId());
			registration.setFirstName(user.getFirstName());
			registration.setLastName(user.getLastName());
			registration.setPhone(user.getPhone());
			registration.setEmail(user.getEmailAddress());
			params.put("registration", registration);
			return viewContact(params);
		}
	}

	private ExecutionResult decideHasOptions(Map<String, Object> params) {
		EventRegistration registration = (EventRegistration) params
				.get("registration");
		boolean decide = registration.getEvent().getEventDisplayGroups().size() > 0;

		if (decide) {
			return viewOptions(params);
		} else {
			return decideHasQuestions(params);
		}
	}

	private ExecutionResult decideHasQuestions(Map<String, Object> params) {
		EventRegistration registration = (EventRegistration) params
				.get("registration");
		boolean decide = registration.getQuestionsForCurrentSelections().size() > 0;

		if (decide) {
			return viewQuestions(params);
		} else {
			return decideHasCost(params);
		}
	}

	private ExecutionResult decideHasCost(Map<String, Object> params) {
		EventRegistration registration = (EventRegistration) params.get("registration");
		LearningUser user = (LearningUser) params.get("user");
		EventRegistration eventreg= new EventRegistration();
		boolean decide =eventreg.checkifPaymentNeed(user.isAlligneduser(), registration.getTotalCost().getCost(),registration.getEvent().getCost().getAligncost());
		//boolean decide = registration.getTotalCost().getCost() > 0;
		if (decide) {
			return decideNeedsPaymentInput(params);
		} else {
			return viewConfirm(params);
		}
	}

	private ExecutionResult decideNeedsPaymentInput(Map<String, Object> params) {
		EventRegistration registration = (EventRegistration) params
				.get("registration");
		boolean decide = false;
		if (registration.getEvent().isAllowCC()) {
			decide = true;
		} else {
			int size = 0;
			for (EventPaymentMethod pay : registration.getEvent()
					.getPaymentMethods()) {
				if (!pay.isAdminOnly()) {
					size++;
				}
			}
			if (size > 1) {
				decide = true;
			} else if (size == 1) {
				for (EventPaymentMethod pay : registration.getEvent()
						.getPaymentMethods()) {
					if (!pay.isAdminOnly()
							&& pay.getPaymentMethodDetails().size() > 0) {
						decide = true;
						break;
					}
				}
			}
		}

		if (decide) {
			return viewPayment(params);
		} else {
			return viewConfirm(params);
		}
	}

	public String getFormObjectName() {
		return "registration";
	}

}

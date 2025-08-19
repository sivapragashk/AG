package com.hd.cedg.lms.action.load;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.hd.cedg.lms.action.base.BaseControllerAction;
import com.hd.cedg.lms.dao.ReportDAO;
import com.hd.cedg.lms.model.EventRegistration;
import com.hd.cedg.lms.model.ExecutionResult;
import com.hd.cedg.lms.model.LearningUser;
import com.hd.cedg.lms.model.ReportCompany;
import com.hd.cedg.lms.support.DefaultErrorExecutionResult;
import com.hd.cedg.lms.model.EventPaymentMethod;

public class LoadEventRegTabAction extends BaseControllerAction {

	public ExecutionResult doAction(Map<String, Object> params) {
		EventRegistration registration = (EventRegistration) params.get("registration");

		if (registration == null) {
			return noFormObject();
		}
		System.out.println("registrationTab -----> " +registration);
		Map<String, Object> registrationTab = new HashMap<String, Object>();

		LearningUser user = (LearningUser) params.get("user");
		boolean hasCompany = false;
		if (registration.getEvent().isEnrollManaged()) {
			ReportDAO reportDAO = new ReportDAO();
			List<ReportCompany> reportCompanies = reportDAO.retrieveCompaniesForUser(user);
			hasCompany = reportCompanies.size() > 1
					|| (reportCompanies.size() == 1 && reportCompanies.get(0)
							.getCompanyId() != user.getCompanyId());
		}
		registrationTab.put("hasCompany", hasCompany);
		
		boolean hasUser = registration.getEvent().isEnrollOthers();
		registrationTab.put("hasUser", hasUser);

		boolean hasOptions = (registration.getEvent().getEventDisplayGroups().size() > 0);
		registrationTab.put("hasOptions", hasOptions);

		boolean hasQuestions = (registration.getQuestionsForCurrentSelections().size() > 0);
		registrationTab.put("hasQuestions", hasQuestions);

		/* hasPayment */
		boolean hasPayment = false;
		if (registration.getEvent().isAllowCC()) {
			hasPayment = true;
		} else {
			int size = 0;
			for (EventPaymentMethod pay : registration.getEvent().getPaymentMethods()) {
				if (!pay.isAdminOnly()) {
					size++;
				}
			}
			if (size > 1) {
				hasPayment = true;
			} else if (size == 1) {
				for (EventPaymentMethod pay : registration.getEvent().getPaymentMethods()) {
					if (!pay.isAdminOnly() && pay.getPaymentMethodDetails().size() > 0) {
						hasPayment = true;
						break;
					}
				}
			}
		}
		registrationTab.put("hasPayment", hasPayment);
				
		registration.setRegistrationTabs(registrationTab);
		System.out.println("registrationTab seted -----> " +registration.getRegistrationTabs());
		return null;
	}

	private ExecutionResult noFormObject() {
		return new DefaultErrorExecutionResult();
	}
}
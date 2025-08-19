package com.hd.cedg.lms.action.load;

import java.util.List;

import java.util.Map;

import com.hd.cedg.lms.action.BaseLmsControllerAction;
import com.hd.cedg.lms.dao.ReportDAO;
import com.hd.cedg.lms.model.EventRegistration;
import com.hd.cedg.lms.model.ExecutionResult;
import com.hd.cedg.lms.model.LearningUser;
import com.hd.cedg.lms.model.ReportCompany;
import com.hd.cedg.lms.support.DefaultErrorExecutionResult;

public class LoadEventRegCompanyAction extends BaseLmsControllerAction {

	private ReportDAO reportDAO;

	public LoadEventRegCompanyAction() {
		reportDAO = new ReportDAO();
	}

	protected ExecutionResult executeAction(Map<String, Object> params) {
		LearningUser user = (LearningUser) params.get("user");

		EventRegistration registration = (EventRegistration) params
				.get("registration");

		if (registration == null) {
			return noFormObject();
		}

		List<ReportCompany> reportCompanies = reportDAO.retrieveCompaniesForUser(user);

		ExecutionResult result = new ExecutionResult();
		result.setPage("event-register/reg-company.jsp");
		result.addAttribute("user", user);
		result.addAttribute("reportCompanies", reportCompanies);
		result.addAttribute("errors", params.get("errors"));
		return result;
	}

	private ExecutionResult noFormObject() {
		return new DefaultErrorExecutionResult();
	}
}

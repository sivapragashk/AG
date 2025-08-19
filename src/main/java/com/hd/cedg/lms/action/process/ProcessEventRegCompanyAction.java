package com.hd.cedg.lms.action.process;

import java.util.Map;

import com.hd.cedg.lms.action.base.BaseControllerAction;
import com.hd.cedg.lms.model.EventRegistration;
import com.hd.cedg.lms.model.ExecutionResult;

public class ProcessEventRegCompanyAction extends BaseControllerAction {

	public ExecutionResult doAction(Map<String, Object> params) {

		EventRegistration registration = (EventRegistration) params
				.get("registration");

		String companyIdStr = (String) params.get("companyId");
		try {
			int companyId = Integer.parseInt(companyIdStr);
			registration.setCompanyId(companyId);
		} catch (NumberFormatException e) {
		}
		String companyName = (String) params.get("companyName");
		String companyType = (String) params.get("companyType");
		String allianceLevel = (String) params.get("allianceLevel");
		String fabricator = (String) params.get("fabricator");
		String salesRep = (String) params.get("salesRep");
		String address = (String) params.get("address");
		String city = (String) params.get("city");
		String region = (String) params.get("region");
		String postCode = (String) params.get("postCode");

		registration.setCompanyName(companyName);
		registration.setCompanyType(companyType);
		registration.setAllianceLevel(allianceLevel);
		registration.setFabricator(fabricator);
		registration.setSalesRep(salesRep);
		registration.setAddress(address);
		registration.setCity(city);
		registration.setRegion(region);
		registration.setPostCode(postCode);

		params.put("registration", registration);

		return null;
	}
}

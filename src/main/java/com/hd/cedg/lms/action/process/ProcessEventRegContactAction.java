package com.hd.cedg.lms.action.process;

import java.util.Map;

import com.hd.cedg.lms.action.base.BaseControllerAction;
import com.hd.cedg.lms.model.EventRegistration;
import com.hd.cedg.lms.model.ExecutionResult;

public class ProcessEventRegContactAction extends BaseControllerAction {

	public ExecutionResult doAction(Map<String, Object> params) {

		EventRegistration registration = (EventRegistration) params
				.get("registration");

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

		String strCompanyId = (String) params.get("companyId");
		int companyId = 0;
		try{
			companyId = Integer.parseInt(strCompanyId);
		}catch(Exception ex){}
		
		
		String strUserId = (String) params.get("userId");
		int userId = 0;
		try{
			userId = Integer.parseInt(strUserId);
		}catch(Exception ex){}
		
		registration.setCompanyName(companyName);
		if(registration.getCompanyId() == 0){
			registration.setCompanyId(companyId);
		}
		if(registration.getUserId() == 0){
			registration.setUserId(userId);
		}
		registration.setFirstName(first);
		registration.setLastName(last);
		registration.setAddress(address);
		registration.setCity(city);
		registration.setRegion(region);
		registration.setPostCode(postcode);
		registration.setPhone(phone);
		registration.setEmail(email);

		params.put("registration", registration);

		return null;
	}
}

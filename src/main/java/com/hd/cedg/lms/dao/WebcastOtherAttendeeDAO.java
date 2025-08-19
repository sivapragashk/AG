package com.hd.cedg.lms.dao;

import java.util.ArrayList;

import java.util.List;

import com.hd.cedg.lms.model.WebcastOtherAttendee;
import com.hd.mis.data.DataUtils;

public class WebcastOtherAttendeeDAO extends BaseLmsDAO {

	public void save(WebcastOtherAttendee attendee, int userId, int eventId) {
		try {
			String sql = "insert into lc_webcastotherattendee (eventid, userid, firstname, lastname, companyname) values (?, ?, ?, ?, ?)";
			List<Object> params = new ArrayList<Object>();
			params.add(eventId);
			params.add(userId);
			params.add(attendee.getFirstName());
			params.add(attendee.getLastName());
			params.add(attendee.getCompanyName());
			DataUtils.execute(sql, params, LC_DATA_SOURCE);
		} catch (Exception e) {
			System.err.println("Error saving Webcast Other Attendee "
					+ attendee.getFirstName() + " " + attendee.getLastName()
					+ " from " + attendee.getCompanyName()
					+ " to event ID " + eventId + " (entered by user " + userId + "): " + e.getMessage());
			e.printStackTrace();
		}
	}

}

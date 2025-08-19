package com.hd.cedg.lms.dao;

import java.util.ArrayList;
import java.util.List;

import com.hd.mis.data.DataUtils;

public class WebcastLaunchLogDAO extends BaseLmsDAO {

	public void save(int userId, int eventId) {
		try {
			String sql = "insert into lc_webcastlaunchlog (eventid, userid, logdate) values (?, ?, sysdate)";
			List<Object> params = new ArrayList<Object>();
			params.add(eventId);
			params.add(userId);
			DataUtils.execute(sql, params, LC_DATA_SOURCE);
		} catch (Exception e) {
			System.err.println("Error saving Webcast Launch Log for event ID "
					+ eventId + " and user ID " + userId + ": "
					+ e.getMessage());
			e.printStackTrace();
		}
	}

}

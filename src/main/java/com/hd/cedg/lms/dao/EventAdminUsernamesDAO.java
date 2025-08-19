package com.hd.cedg.lms.dao;

import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hd.cedg.lms.model.Event;
import com.hd.mis.data.DataUtils;
import com.hd.mis.data.ResultSetHandler;

public class EventAdminUsernamesDAO extends BaseLmsDAO {

	public List<String> retrieveListByEventId(int eventId) {
		List<String> usernames = new ArrayList<String>();

		try {
			String sql = "select username from lc_eventadmin where eventid = ? order by username";
			List<Object> params = new ArrayList<Object>();
			params.add(eventId);
			usernames = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<List<String>>() {
						public List<String> handleResultSet(ResultSet rs)
								throws Exception {
							List<String> usernames = new ArrayList<String>();

							while (rs.next()) {
								String username = rs.getString("username");

								usernames.add(username);
							}

							return usernames;
						}
					});

		} catch (Exception e) {
			System.err.println("Error retrieving Event Admins Users for event "
					+ eventId + ": " + e.getMessage());
			e.printStackTrace();
		}

		return usernames;
	}

	public Map<Integer, List<String>> retrieveListByList(List<Integer> eventIds) {
		Map<Integer, List<String>> usernamesMap = new HashMap<Integer, List<String>>();

		try {
			String sql = "select eventid, username from lc_eventadmin where eventid in (-1";
			for (int i = 0; i < eventIds.size(); i++) {
				sql += ", ?";
			}
			sql += ") order by username";
			List<Object> params = new ArrayList<Object>();
			for (Integer eventId : eventIds) {
				params.add(eventId);
			}
			usernamesMap = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<Map<Integer, List<String>>>() {
						public Map<Integer, List<String>> handleResultSet(
								ResultSet rs) throws Exception {
							Map<Integer, List<String>> usernamesMap = new HashMap<Integer, List<String>>();

							while (rs.next()) {
								String username = rs.getString("username");

								Integer eventId = new Integer(rs
										.getInt("eventid"));
								List<String> usernames = usernamesMap
										.get(eventId);
								if (usernames == null) {
									usernames = new ArrayList<String>();
									usernamesMap.put(eventId, usernames);
								}
								usernames.add(username);
							}

							return usernamesMap;
						}
					});

		} catch (Exception e) {
			System.err
					.println("Error retrieving Event Admins Users for list of event IDs: "
							+ e.getMessage());
			e.printStackTrace();
		}

		return usernamesMap;
	}

	public void saveByEvent(Event event) {
		try {
			String deleteSql = "delete from lc_eventadmin where eventid = ?";
			List<Object> deleteParams = new ArrayList<Object>();
			deleteParams.add(event.getEventId());
			DataUtils.execute(deleteSql, deleteParams, LC_DATA_SOURCE);

			for (String adminUsername : event.getAdminUsernames()) {
				String insertSql = "insert into lc_eventadmin (eventid, username) values (?, ?)";
				List<Object> insertParams = new ArrayList<Object>();
				insertParams.add(event.getEventId());
				insertParams.add(adminUsername);
				DataUtils.execute(insertSql, insertParams, LC_DATA_SOURCE);
			}
		} catch (Exception e) {
			System.err.println("Error saving Admin Usernames for Event "
					+ event.getEventId() + ": " + e.getMessage());
			e.printStackTrace();
		}
	}
}

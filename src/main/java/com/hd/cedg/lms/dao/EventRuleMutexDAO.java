package com.hd.cedg.lms.dao;

import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hd.cedg.lms.model.Event;
import com.hd.cedg.lms.model.EventRuleMutex;
import com.hd.mis.data.DataUtils;
import com.hd.mis.data.ResultSetHandler;

public class EventRuleMutexDAO extends BaseLmsDAO {

	public List<EventRuleMutex> retrieveListByEventId(int eventId) {
		List<EventRuleMutex> mutexes = new ArrayList<EventRuleMutex>();

		try {
			String sql = "select groupid, optiontypeid, optionid from lc_eventrulemutex where eventid = ? order by groupid, optiontypeid, optionid";
			List<Object> params = new ArrayList<Object>();
			params.add(eventId);
			mutexes = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<List<EventRuleMutex>>() {
						public List<EventRuleMutex> handleResultSet(ResultSet rs)
								throws Exception {
							List<EventRuleMutex> mutexes = new ArrayList<EventRuleMutex>();

							while (rs.next()) {
								int groupId = rs.getInt("groupid");
								int optionTypeId = rs.getInt("optiontypeid");
								int optionId = rs.getInt("optionid");

								while (mutexes.size() <= groupId) {
									mutexes.add(new EventRuleMutex());
								}

								EventRuleMutex mutex = mutexes.get(groupId);
								if (optionTypeId == Event.TYPE_ID_OPTION) {
									mutex.getOptionIds().add(
											new Integer(optionId));
								} else if (optionTypeId == Event.TYPE_ID_PACKAGE) {
									mutex.getPackageIds().add(
											new Integer(optionId));
								}
							}

							return mutexes;
						}
					});
		} catch (Exception e) {
			System.err.println("Error retrieving Event Rule Mutexes for event "
					+ eventId + ": " + e.getMessage());
			e.printStackTrace();
		}

		return mutexes;
	}

	public Map<Integer, List<EventRuleMutex>> retrieveListByList(
			List<Integer> eventIds) {
		Map<Integer, List<EventRuleMutex>> mutexMap = new HashMap<Integer, List<EventRuleMutex>>();

		try {
			String sql = "select eventid, groupid, optiontypeid, optionid from lc_eventrulemutex where eventid in (-1";
			for (int i = 0; i < eventIds.size(); i++) {
				sql += ", ?";
			}
			sql += ") order by groupid, optiontypeid, optionid";
			List<Object> params = new ArrayList<Object>();
			for (Integer eventId : eventIds) {
				params.add(eventId);
			}
			mutexMap = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<Map<Integer, List<EventRuleMutex>>>() {
						public Map<Integer, List<EventRuleMutex>> handleResultSet(
								ResultSet rs) throws Exception {
							Map<Integer, List<EventRuleMutex>> mutexMap = new HashMap<Integer, List<EventRuleMutex>>();

							while (rs.next()) {
								Integer eventId = new Integer(rs
										.getInt("eventid"));
								List<EventRuleMutex> mutexes = mutexMap
										.get(eventId);
								if (mutexes == null) {
									mutexes = new ArrayList<EventRuleMutex>();
									mutexMap.put(eventId, mutexes);
								}

								int groupId = rs.getInt("groupid");
								int optionTypeId = rs.getInt("optiontypeid");
								int optionId = rs.getInt("optionid");

								while (mutexes.size() <= groupId) {
									mutexes.add(new EventRuleMutex());
								}

								EventRuleMutex mutex = mutexes.get(groupId);
								if (optionTypeId == Event.TYPE_ID_OPTION) {
									mutex.getOptionIds().add(
											new Integer(optionId));
								} else if (optionTypeId == Event.TYPE_ID_PACKAGE) {
									mutex.getPackageIds().add(
											new Integer(optionId));
								}
							}

							return mutexMap;
						}
					});
		} catch (Exception e) {
			System.err
					.println("Error retrieving Event Rule Mutexes for list of event IDs: "
							+ e.getMessage());
			e.printStackTrace();
		}

		return mutexMap;
	}

	public void saveByEvent(Event event) {
		try {
			// Delete existing mutexes
			String deleteSql = "delete from lc_eventrulemutex where eventid = ?";
			List<Object> deleteParams = new ArrayList<Object>();
			deleteParams.add(event.getEventId());
			DataUtils.execute(deleteSql, deleteParams, LC_DATA_SOURCE);

			// Insert current mutexes
			for (int groupId = 0; groupId < event.getEventRuleMutexes().size(); groupId++) {
				EventRuleMutex mutex = event.getEventRuleMutexes().get(groupId);
				for (Integer optionId : mutex.getOptionIds()) {
					String insertSql = "insert into lc_eventrulemutex (eventid, groupid, optiontypeid, optionid) values (?, ?, ?, ?)";
					List<Object> insertParams = new ArrayList<Object>();
					insertParams.add(event.getEventId());
					insertParams.add(groupId);
					insertParams.add(Event.TYPE_ID_OPTION);
					insertParams.add(optionId.intValue());
					DataUtils.execute(insertSql, insertParams, LC_DATA_SOURCE);
				}
				for (Integer packageId : mutex.getPackageIds()) {
					String insertSql = "insert into lc_eventrulemutex (eventid, groupid, optiontypeid, optionid) values (?, ?, ?, ?)";
					List<Object> insertParams = new ArrayList<Object>();
					insertParams.add(event.getEventId());
					insertParams.add(groupId);
					insertParams.add(Event.TYPE_ID_PACKAGE);
					insertParams.add(packageId.intValue());
					DataUtils.execute(insertSql, insertParams, LC_DATA_SOURCE);
				}
			}
		} catch (Exception e) {
			System.err.println("Error saving Mutexes for event "
					+ event.getEventId() + ": " + e.getMessage());
			e.printStackTrace();
		}
	}

}

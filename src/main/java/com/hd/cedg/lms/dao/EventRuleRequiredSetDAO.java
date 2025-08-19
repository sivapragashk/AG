package com.hd.cedg.lms.dao;

import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hd.cedg.lms.model.Event;
import com.hd.cedg.lms.model.EventRuleRequiredSet;
import com.hd.mis.data.DataUtils;
import com.hd.mis.data.ResultSetHandler;

public class EventRuleRequiredSetDAO extends BaseLmsDAO {

	public List<EventRuleRequiredSet> retrieveListByEventId(int eventId) {
		List<EventRuleRequiredSet> reqSets = new ArrayList<EventRuleRequiredSet>();

		try {
			String sql = "select groupid, optiontypeid, optionid from lc_eventrulerequiredset where eventid = ? order by groupid, optiontypeid, optionid";
			List<Object> params = new ArrayList<Object>();
			params.add(eventId);
			reqSets = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<List<EventRuleRequiredSet>>() {
						public List<EventRuleRequiredSet> handleResultSet(
								ResultSet rs) throws Exception {
							List<EventRuleRequiredSet> reqSets = new ArrayList<EventRuleRequiredSet>();

							while (rs.next()) {
								int groupId = rs.getInt("groupid");
								int optionTypeId = rs.getInt("optiontypeid");
								int optionId = rs.getInt("optionid");

								while (reqSets.size() <= groupId) {
									reqSets.add(new EventRuleRequiredSet());
								}

								EventRuleRequiredSet reqSet = reqSets
										.get(groupId);
								if (optionTypeId == Event.TYPE_ID_OPTION) {
									reqSet.getOptionIds().add(
											new Integer(optionId));
								} else if (optionTypeId == Event.TYPE_ID_PACKAGE) {
									reqSet.getPackageIds().add(
											new Integer(optionId));
								}
							}

							return reqSets;
						}
					});
		} catch (Exception e) {
			System.err
					.println("Error retrieving Event Rule Required Sets for event "
							+ eventId + ": " + e.getMessage());
			e.printStackTrace();
		}

		return reqSets;
	}

	public Map<Integer, List<EventRuleRequiredSet>> retrieveListByList(
			List<Integer> eventIds) {
		Map<Integer, List<EventRuleRequiredSet>> reqSetMap = new HashMap<Integer, List<EventRuleRequiredSet>>();

		try {
			String sql = "select eventid, groupid, optiontypeid, optionid from lc_eventrulerequiredset where eventid in (-1";
			for (int i = 0; i < eventIds.size(); i++) {
				sql += ", ?";
			}
			sql += ") order by groupid, optiontypeid, optionid";
			List<Object> params = new ArrayList<Object>();
			for (Integer eventId : eventIds) {
				params.add(eventId);
			}
			reqSetMap = DataUtils
					.query(
							sql,
							params,
							LC_DATA_SOURCE,
							new ResultSetHandler<Map<Integer, List<EventRuleRequiredSet>>>() {
								public Map<Integer, List<EventRuleRequiredSet>> handleResultSet(
										ResultSet rs) throws Exception {
									Map<Integer, List<EventRuleRequiredSet>> reqSetMap = new HashMap<Integer, List<EventRuleRequiredSet>>();

									while (rs.next()) {
										Integer eventId = new Integer(rs.getInt("eventid"));
										List<EventRuleRequiredSet> reqSets = reqSetMap.get(eventId);
										if (reqSets == null) {
											reqSets = new ArrayList<EventRuleRequiredSet>();
											reqSetMap.put(eventId, reqSets);
										}

										int groupId = rs.getInt("groupid");
										int optionTypeId = rs
												.getInt("optiontypeid");
										int optionId = rs.getInt("optionid");

										while (reqSets.size() <= groupId) {
											reqSets
													.add(new EventRuleRequiredSet());
										}

										EventRuleRequiredSet reqSet = reqSets
												.get(groupId);
										if (optionTypeId == Event.TYPE_ID_OPTION) {
											reqSet.getOptionIds().add(
													new Integer(optionId));
										} else if (optionTypeId == Event.TYPE_ID_PACKAGE) {
											reqSet.getPackageIds().add(
													new Integer(optionId));
										}
									}

									return reqSetMap;
								}
							});
		} catch (Exception e) {
			System.err
					.println("Error retrieving Event Rule Required Sets for list of event IDs: "
							+ e.getMessage());
			e.printStackTrace();
		}

		return reqSetMap;
	}

	public void saveByEvent(Event event) {
		try {
			// Delete existing reqSets
			String deleteSql = "delete from lc_eventrulerequiredset where eventid = ?";
			List<Object> deleteParams = new ArrayList<Object>();
			deleteParams.add(event.getEventId());
			DataUtils.execute(deleteSql, deleteParams, LC_DATA_SOURCE);

			// Insert current reqSets
			for (int groupId = 0; groupId < event.getEventRuleRequiredSets()
					.size(); groupId++) {
				EventRuleRequiredSet reqSet = event.getEventRuleRequiredSets()
						.get(groupId);
				for (Integer optionId : reqSet.getOptionIds()) {
					String insertSql = "insert into lc_eventrulerequiredset (eventid, groupid, optiontypeid, optionid) values (?, ?, ?, ?)";
					List<Object> insertParams = new ArrayList<Object>();
					insertParams.add(event.getEventId());
					insertParams.add(groupId);
					insertParams.add(Event.TYPE_ID_OPTION);
					insertParams.add(optionId.intValue());
					DataUtils.execute(insertSql, insertParams, LC_DATA_SOURCE);
				}
				for (Integer packageId : reqSet.getPackageIds()) {
					String insertSql = "insert into lc_eventrulerequiredset (eventid, groupid, optiontypeid, optionid) values (?, ?, ?, ?)";
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

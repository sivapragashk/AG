package com.hd.cedg.lms.dao;

import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hd.cedg.lms.model.Event;
import com.hd.cedg.lms.model.EventDisplayGroup;
import com.hd.cedg.lms.model.EventDisplayGroupItem;
import com.hd.cedg.lms.model.EventOption;
import com.hd.cedg.lms.model.EventOptionPackage;
import com.hd.cedg.lms.model.EventQuestion;
import com.hd.cedg.lms.model.EventRuleMutex;
import com.hd.cedg.lms.model.EventRuleRequiredSet;
import com.hd.mis.data.DataUtils;
import com.hd.mis.data.ResultSetHandler;

public class EventOptionDAO extends BaseLmsDAO {

	public List<EventOption> retrieveListByEventId(int eventId) {
		List<EventOption> options = new ArrayList<EventOption>();

		try {
			String sql = "select op.eventoptionid, op.title, op.description, op.capacity, coalesce(regcount.num, 0) currentattendeecount, cost, to_char(op.startdate, 'YYYY-MM-DD') startdate, to_char(op.starttime, 'HH24:MI:SS') starttime, to_char(op.enddate, 'YYYY-MM-DD') enddate, to_char(op.endtime, 'HH24:MI:SS') endtime from lc_eventoption op left join ( select eventoptionid, count(*) num from lc_eventregoption group by eventoptionid ) regcount on regcount.eventoptionid = op.eventoptionid where op.eventid = ? and active = 1";
			List<Object> params = new ArrayList<Object>();
			params.add(eventId);
			options = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<List<EventOption>>() {
						public List<EventOption> handleResultSet(ResultSet rs)
								throws Exception {
							List<EventOption> options = new ArrayList<EventOption>();

							while (rs.next()) {
								EventOption option = new EventOption();

								option.setEventOptionId(rs
										.getInt("eventoptionid"));
								option.setTitle(rs.getString("title"));
								option.setDescription(rs
										.getString("description"));
								option.setCapacity(rs.getInt("capacity"));
								option.setCurrentAttendeeCount(rs
										.getInt("currentattendeecount"));
								option.getCost().setCost(rs.getFloat("cost"));

								option.getDateRange().setStartDate(
										rs.getString("startdate"));
								option.getDateRange().setStartTime(
										rs.getString("starttime"));
								option.getDateRange().setEndDate(
										rs.getString("enddate"));
								option.getDateRange().setEndTime(
										rs.getString("endtime"));

								options.add(option);
							}

							return options;
						}
					});
		} catch (Exception e) {
			System.err.println("Error retrieving Event Options for event "
					+ eventId + ": " + e.getMessage());
			e.printStackTrace();
		}

		return options;
	}

	public Map<Integer, List<EventOption>> retrieveListByList(
			List<Integer> eventIds) {
		Map<Integer, List<EventOption>> optionMap = new HashMap<Integer, List<EventOption>>();

		try {
			String sql = "select op.eventid, op.eventoptionid, op.title, op.description, op.capacity, coalesce(regcount.num, 0) currentattendeecount, cost, to_char(op.startdate, 'YYYY-MM-DD') startdate, to_char(op.starttime, 'HH24:MI:SS') starttime, to_char(op.enddate, 'YYYY-MM-DD') enddate, to_char(op.endtime, 'HH24:MI:SS') endtime from lc_eventoption op left join ( select eventoptionid, count(*) num from lc_eventregoption group by eventoptionid ) regcount on regcount.eventoptionid = op.eventoptionid where op.eventid in (-1";
			for (int i = 0; i < eventIds.size(); i++) {
				sql += ", ?";
			}
			sql += ") and active = 1";
			List<Object> params = new ArrayList<Object>();
			for (Integer eventId : eventIds) {
				params.add(eventId);
			}
			optionMap = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<Map<Integer, List<EventOption>>>() {
						public Map<Integer, List<EventOption>> handleResultSet(
								ResultSet rs) throws Exception {
							Map<Integer, List<EventOption>> optionMap = new HashMap<Integer, List<EventOption>>();

							while (rs.next()) {
								EventOption option = new EventOption();

								option.setEventOptionId(rs
										.getInt("eventoptionid"));
								option.setTitle(rs.getString("title"));
								option.setDescription(rs
										.getString("description"));
								option.setCapacity(rs.getInt("capacity"));
								option.setCurrentAttendeeCount(rs
										.getInt("currentattendeecount"));
								option.getCost().setCost(rs.getFloat("cost"));

								option.getDateRange().setStartDate(
										rs.getString("startdate"));
								option.getDateRange().setStartTime(
										rs.getString("starttime"));
								option.getDateRange().setEndDate(
										rs.getString("enddate"));
								option.getDateRange().setEndTime(
										rs.getString("endtime"));

								Integer eventId = new Integer(rs
										.getInt("eventid"));
								List<EventOption> options = optionMap
										.get(eventId);
								if (options == null) {
									options = new ArrayList<EventOption>();
									optionMap.put(eventId, options);
								}
								options.add(option);
							}

							return optionMap;
						}
					});
		} catch (Exception e) {
			System.err
					.println("Error retrieving Event Options for list of event IDs: "
							+ e.getMessage());
			e.printStackTrace();
		}

		return optionMap;
	}

	public void saveByEvent(Event event) {
		for (EventOption option : event.getEventOptions()) {
			if (option.getEventOptionId() > 0) {
				// Update
				try {
					String sql = "update lc_eventoption set title=?, description=?, capacity=?, cost=?, startdate=to_date(?, 'MM/DD/YYYY'), starttime=to_date(?, 'HH24:MI:SS'), enddate=to_date(?, 'MM/DD/YYYY'), endtime=to_date(?, 'HH24:MI:SS') where eventoptionid = ?";
					List<Object> params = new ArrayList<Object>();
					params.add(option.getTitle());
					params.add(option.getDescription());
					params.add(option.getCapacity());
					params.add(option.getCost().getCost());
					params.add(option.getDateRange().getFormattedStartDate(
							"MM/dd/yyyy"));
					params.add(option.getDateRange().getFormattedStartTime(
							"HH:mm:ss"));
					params.add(option.getDateRange().getFormattedEndDate(
							"MM/dd/yyyy"));
					params.add(option.getDateRange().getFormattedEndTime(
							"HH:mm:ss"));
					params.add(option.getEventOptionId());
					DataUtils.execute(sql, params, LC_DATA_SOURCE);
				} catch (Exception e) {
					System.err
							.println("Error updating Event Option "
									+ option.getEventOptionId() + ": "
									+ e.getMessage());
					e.printStackTrace();
				}
			} else {
				// Generate optionId
				int optionId = generateEventOptionId();

				if (optionId > 0) {
					int oldOptionId = option.getEventOptionId();
					option.setEventOptionId(optionId);

					// Insert
					try {
						String sql = "insert into lc_eventoption (eventoptionid, eventid, title, description, capacity, cost, startdate, starttime, enddate, endtime, active) values (?, ?, ?, ?, ?, ?, to_date(?, 'MM/DD/YYYY'), to_date(?, 'HH24:MI:SS'), to_date(?, 'MM/DD/YYYY'), to_date(?, 'HH24:MI:SS'), 1)";
						List<Object> params = new ArrayList<Object>();
						params.add(option.getEventOptionId());
						params.add(event.getEventId());
						params.add(option.getTitle());
						params.add(option.getDescription());
						params.add(option.getCapacity());
						params.add(option.getCost().getCost());
						params.add(option.getDateRange().getFormattedStartDate(
								"MM/dd/yyyy"));
						params.add(option.getDateRange().getFormattedStartTime(
								"HH:mm:ss"));
						params.add(option.getDateRange().getFormattedEndDate(
								"MM/dd/yyyy"));
						params.add(option.getDateRange().getFormattedEndTime(
								"HH:mm:ss"));
						DataUtils.execute(sql, params, LC_DATA_SOURCE);
					} catch (Exception e) {
						System.err.println("Error inserting Event Option: "
								+ e.getMessage());
						e.printStackTrace();
					}

					// Update references to the temp optionId in Packages
					for (EventOptionPackage aPackage : event
							.getEventOptionPackages()) {
						int index = aPackage.getOptionIds().indexOf(
								new Integer(oldOptionId));
						if (index != -1) {
							aPackage.getOptionIds().set(index,
									new Integer(optionId));
						}
					}
					// Update references to the temp optionId in Groups
					for (EventDisplayGroup group : event
							.getEventDisplayGroups()) {
						for (EventDisplayGroupItem item : group.getItems()) {
							if (item.getItemType() == Event.TYPE_ID_OPTION
									&& item.getItemId() == oldOptionId) {
								item.setItemId(optionId);
							}
						}
					}
					// Update references to the temp optionId in Mutexes
					for (EventRuleMutex mutex : event.getEventRuleMutexes()) {
						int index = mutex.getOptionIds().indexOf(
								new Integer(oldOptionId));
						if (index != -1) {
							mutex.getOptionIds().set(index,
									new Integer(optionId));
						}
					}
					// Update references to the temp optionId in ReqSets
					for (EventRuleRequiredSet reqSet : event
							.getEventRuleRequiredSets()) {
						int index = reqSet.getOptionIds().indexOf(
								new Integer(oldOptionId));
						if (index != -1) {
							reqSet.getOptionIds().set(index,
									new Integer(optionId));
						}
					}
					// Update references to the temp optionId in Questions
					for (EventQuestion question : event.getQuestions()) {
						int index = question.getOptionIds().indexOf(
								new Integer(oldOptionId));
						if (index != -1) {
							question.getOptionIds().set(index,
									new Integer(optionId));
						}
					}
				}
			}
		}
	}

	private int generateEventOptionId() {
		int optionId = 0;
		try {
			String sql = "select lc_eventoption_seq.nextval optionid from dual";
			List<Object> params = new ArrayList<Object>();
			Integer id = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<Integer>() {
						public Integer handleResultSet(ResultSet rs)
								throws Exception {
							if (rs.next()) {
								return new Integer(rs.getInt("optionid"));
							}
							return null;
						}
					});
			if (id != null) {
				optionId = id.intValue();
			}
		} catch (Exception e) {
			System.err.println("Error generating a new Option ID: "
					+ e.getMessage());
			e.printStackTrace();
		}
		return optionId;
	}

}

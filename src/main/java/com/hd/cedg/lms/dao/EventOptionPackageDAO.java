package com.hd.cedg.lms.dao;

import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hd.cedg.lms.model.Event;
import com.hd.cedg.lms.model.EventDisplayGroup;
import com.hd.cedg.lms.model.EventDisplayGroupItem;
import com.hd.cedg.lms.model.EventOptionPackage;
import com.hd.cedg.lms.model.EventQuestion;
import com.hd.cedg.lms.model.EventRuleMutex;
import com.hd.cedg.lms.model.EventRuleRequiredSet;
import com.hd.mis.data.DataUtils;
import com.hd.mis.data.ResultSetHandler;

public class EventOptionPackageDAO extends BaseLmsDAO {

	public List<EventOptionPackage> retrieveListByEventId(int eventId) {
		List<EventOptionPackage> packages = new ArrayList<EventOptionPackage>();

		try {
			String sql = "select packageid, title, description, cost from lc_eventoptionpackage where eventid = ?";
			List<Object> params = new ArrayList<Object>();
			params.add(eventId);
			packages = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<List<EventOptionPackage>>() {
						public List<EventOptionPackage> handleResultSet(
								ResultSet rs) throws Exception {
							List<EventOptionPackage> packages = new ArrayList<EventOptionPackage>();

							while (rs.next()) {
								EventOptionPackage aPackage = new EventOptionPackage();

								aPackage.setPackageId(rs.getInt("packageid"));
								aPackage.setTitle(rs.getString("title"));
								aPackage.setDescription(rs
										.getString("description"));
								aPackage.getCost().setCost(rs.getFloat("cost"));

								packages.add(aPackage);
							}

							return packages;
						}
					});

			// optionIds
			Map<Integer, List<Integer>> optionIdMap = retrieveOptionIdsByEventId(eventId);
			for (EventOptionPackage aPackage : packages) {
				List<Integer> optionIds = optionIdMap.get(aPackage
						.getPackageId());
				if (optionIds != null) {
					aPackage.setOptionIds(optionIds);
				}
			}

		} catch (Exception e) {
			System.err
					.println("Error retrieving Event Options Packages for event "
							+ eventId + ": " + e.getMessage());
			e.printStackTrace();
		}

		return packages;
	}

	public Map<Integer, List<EventOptionPackage>> retrieveListByList(
			List<Integer> eventIds) {
		Map<Integer, List<EventOptionPackage>> packageMap = new HashMap<Integer, List<EventOptionPackage>>();

		try {
			String sql = "select eventid, packageid, title, description, cost from lc_eventoptionpackage where eventid in (-1";
			for (int i = 0; i < eventIds.size(); i++) {
				sql += ", ?";
			}
			sql += ")";
			List<Object> params = new ArrayList<Object>();
			for (Integer eventId : eventIds) {
				params.add(eventId);
			}
			packageMap = DataUtils
					.query(
							sql,
							params,
							LC_DATA_SOURCE,
							new ResultSetHandler<Map<Integer, List<EventOptionPackage>>>() {
								public Map<Integer, List<EventOptionPackage>> handleResultSet(
										ResultSet rs) throws Exception {
									Map<Integer, List<EventOptionPackage>> packageMap = new HashMap<Integer, List<EventOptionPackage>>();

									while (rs.next()) {
										EventOptionPackage aPackage = new EventOptionPackage();

										aPackage.setPackageId(rs
												.getInt("packageid"));
										aPackage
												.setTitle(rs.getString("title"));
										aPackage.setDescription(rs
												.getString("description"));
										aPackage.getCost().setCost(
												rs.getFloat("cost"));

										Integer eventId = new Integer(rs
												.getInt("eventid"));
										List<EventOptionPackage> packages = packageMap
												.get(eventId);
										if (packages == null) {
											packages = new ArrayList<EventOptionPackage>();
											packageMap.put(eventId, packages);
										}
										packages.add(aPackage);
									}

									return packageMap;
								}
							});

			// optionIds
			Map<Integer, List<Integer>> optionIdMap = retrieveOptionIdMapByEventIds(eventIds);
			for (Integer eventId : packageMap.keySet()) {
				List<EventOptionPackage> packages = packageMap.get(eventId);
				for (EventOptionPackage aPackage : packages) {
					List<Integer> optionIds = optionIdMap.get(aPackage
							.getPackageId());
					if (optionIds != null) {
						aPackage.setOptionIds(optionIds);
					}
				}
			}

		} catch (Exception e) {
			System.err
					.println("Error retrieving Event Options Packages for list of event IDs: "
							+ e.getMessage());
			e.printStackTrace();
		}

		return packageMap;
	}

	public void saveByEvent(Event event) {
		for (EventOptionPackage aPackage : event.getEventOptionPackages()) {
			if (aPackage.getPackageId() > 0) {
				// Update
				try {
					String sql = "update lc_eventoptionpackage set title=?, description=?, cost=? where packageid = ?";
					List<Object> params = new ArrayList<Object>();
					params.add(aPackage.getTitle());
					params.add(aPackage.getDescription());
					params.add(aPackage.getCost().getCost());
					params.add(aPackage.getPackageId());
					DataUtils.execute(sql, params, LC_DATA_SOURCE);
				} catch (Exception e) {
					System.err.println("Error updating Event Option Package "
							+ aPackage.getPackageId() + ": " + e.getMessage());
					e.printStackTrace();
				}
				// Update package items
				savePackageItems(aPackage);
			} else {
				// Generate packageId
				int packageId = generatePackageId();

				if (packageId > 0) {
					int oldPackageId = aPackage.getPackageId();
					aPackage.setPackageId(packageId);

					// Insert
					try {
						String sql = "insert into lc_eventoptionpackage (packageid, eventid, title, description, cost) values (?, ?, ?, ?, ?)";
						List<Object> params = new ArrayList<Object>();
						params.add(aPackage.getPackageId());
						params.add(event.getEventId());
						params.add(aPackage.getTitle());
						params.add(aPackage.getDescription());
						params.add(aPackage.getCost().getCost());
						DataUtils.execute(sql, params, LC_DATA_SOURCE);
					} catch (Exception e) {
						System.err
								.println("Error inserting Event Option Package: "
										+ e.getMessage());
						e.printStackTrace();
					}

					// Update package contents
					savePackageItems(aPackage);

					// Update references to the temp packageId in Groups
					for (EventDisplayGroup group : event
							.getEventDisplayGroups()) {
						for (EventDisplayGroupItem item : group.getItems()) {
							if (item.getItemType() == Event.TYPE_ID_PACKAGE
									&& item.getItemId() == oldPackageId) {
								item.setItemId(packageId);
							}
						}
					}
					// Update references to the temp packageId in Mutexes
					for (EventRuleMutex mutex : event.getEventRuleMutexes()) {
						int index = mutex.getPackageIds().indexOf(
								new Integer(oldPackageId));
						if (index != -1) {
							mutex.getPackageIds().set(index,
									new Integer(packageId));
						}
					}
					// Update references to the temp packageId in ReqSets
					for (EventRuleRequiredSet reqSet : event
							.getEventRuleRequiredSets()) {
						int index = reqSet.getPackageIds().indexOf(
								new Integer(oldPackageId));
						if (index != -1) {
							reqSet.getPackageIds().set(index,
									new Integer(packageId));
						}
					}
					// Update references to the temp packageId in Questions
					for (EventQuestion question : event.getQuestions()) {
						int index = question.getPackageIds().indexOf(
								new Integer(oldPackageId));
						if (index != -1) {
							question.getPackageIds().set(index,
									new Integer(packageId));
						}
					}
				}
			}
		}
	}

	private Map<Integer, List<Integer>> retrieveOptionIdsByEventId(int eventId) {
		Map<Integer, List<Integer>> optionIdMap = new HashMap<Integer, List<Integer>>();

		try {
			String sql = "select i.packageid, i.eventoptionid from lc_eventoptionpackageitem i inner join lc_eventoptionpackage p on p.packageid = i.packageid where p.eventid = ?";
			List<Object> params = new ArrayList<Object>();
			params.add(eventId);
			optionIdMap = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<Map<Integer, List<Integer>>>() {
						public Map<Integer, List<Integer>> handleResultSet(
								ResultSet rs) throws Exception {
							Map<Integer, List<Integer>> optionIdMap = new HashMap<Integer, List<Integer>>();

							while (rs.next()) {
								int packageId = rs.getInt("packageid");
								List<Integer> optionIds = optionIdMap
										.get(new Integer(packageId));
								if (optionIds == null) {
									optionIds = new ArrayList<Integer>();
									optionIdMap.put(new Integer(packageId),
											optionIds);
								}
								optionIds.add(new Integer(rs
										.getInt("eventoptionid")));
							}

							return optionIdMap;
						}
					});
		} catch (Exception e) {
			System.err.println("Error retrieving Package Option IDs for Event "
					+ eventId + ": " + e.getMessage());
			e.printStackTrace();
		}

		return optionIdMap;
	}

	private Map<Integer, List<Integer>> retrieveOptionIdMapByEventIds(
			List<Integer> eventIds) {
		Map<Integer, List<Integer>> optionIdMap = new HashMap<Integer, List<Integer>>();

		try {
			String sql = "select i.packageid, i.eventoptionid from lc_eventoptionpackageitem i inner join lc_eventoptionpackage p on p.packageid = i.packageid where p.eventid in (-1";
			for (int i = 0; i < eventIds.size(); i++) {
				sql += ", ?";
			}
			sql += ")";
			List<Object> params = new ArrayList<Object>();
			for (Integer eventId : eventIds) {
				params.add(eventId);
			}
			optionIdMap = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<Map<Integer, List<Integer>>>() {
						public Map<Integer, List<Integer>> handleResultSet(
								ResultSet rs) throws Exception {
							Map<Integer, List<Integer>> optionIdMap = new HashMap<Integer, List<Integer>>();

							while (rs.next()) {
								int packageId = rs.getInt("packageid");
								List<Integer> optionIds = optionIdMap
										.get(new Integer(packageId));
								if (optionIds == null) {
									optionIds = new ArrayList<Integer>();
									optionIdMap.put(new Integer(packageId),
											optionIds);
								}
								optionIds.add(new Integer(rs
										.getInt("eventoptionid")));
							}

							return optionIdMap;
						}
					});
		} catch (Exception e) {
			System.err.println("Error retrieving all Package Option IDs: "
					+ e.getMessage());
			e.printStackTrace();
		}

		return optionIdMap;
	}

	private void savePackageItems(EventOptionPackage aPackage) {
		try {
			// Delete existing contents
			String deleteSql = "delete from lc_eventoptionpackageitem where packageid = ?";
			List<Object> deleteParams = new ArrayList<Object>();
			deleteParams.add(aPackage.getPackageId());
			DataUtils.execute(deleteSql, deleteParams, LC_DATA_SOURCE);

			// Insert current contents
			for (Integer optionId : aPackage.getOptionIds()) {
				String insertSql = "insert into lc_eventoptionpackageitem (packageid, eventoptionid) values (?, ?)";
				List<Object> insertParams = new ArrayList<Object>();
				insertParams.add(aPackage.getPackageId());
				insertParams.add(optionId.intValue());
				DataUtils.execute(insertSql, insertParams, LC_DATA_SOURCE);
			}
		} catch (Exception e) {
			System.err.println("Error saving contents of Package "
					+ aPackage.getPackageId() + " during Update: "
					+ e.getMessage());
			e.printStackTrace();
		}

	}

	private int generatePackageId() {
		int packageId = 0;
		try {
			String sql = "select lc_eventoptionpackage_seq.nextval packageid from dual";
			List<Object> params = new ArrayList<Object>();
			Integer id = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<Integer>() {
						public Integer handleResultSet(ResultSet rs)
								throws Exception {
							if (rs.next()) {
								return new Integer(rs.getInt("packageid"));
							}
							return null;
						}
					});
			if (id != null) {
				packageId = id.intValue();
			}
		} catch (Exception e) {
			System.err.println("Error generating a new Package ID: "
					+ e.getMessage());
			e.printStackTrace();
		}
		return packageId;
	}

}

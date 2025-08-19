package com.hd.cedg.lms.dao;

import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hd.cedg.lms.model.Event;
import com.hd.cedg.lms.model.EventDisplayGroup;
import com.hd.cedg.lms.model.EventDisplayGroupItem;
import com.hd.mis.data.DataUtils;
import com.hd.mis.data.ResultSetHandler;

public class EventDisplayGroupDAO extends BaseLmsDAO {

	public List<EventDisplayGroup> retrieveListByEventId(int eventId) {
		List<EventDisplayGroup> groups = new ArrayList<EventDisplayGroup>();

		try {
			String sql = "select title from lc_eventdisplaygroup where eventid = ? order by sortorder";
			List<Object> params = new ArrayList<Object>();
			params.add(eventId);
			groups = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<List<EventDisplayGroup>>() {
						public List<EventDisplayGroup> handleResultSet(
								ResultSet rs) throws Exception {
							List<EventDisplayGroup> groups = new ArrayList<EventDisplayGroup>();

							while (rs.next()) {
								EventDisplayGroup group = new EventDisplayGroup();
								group.setTitle(rs.getString("title"));
								groups.add(group);
							}

							return groups;
						}
					});

			// optionIds
			List<List<EventDisplayGroupItem>> itemsList = retrieveItemsByEventId(eventId);
			for (int i = 0; i < groups.size(); i++) {
				EventDisplayGroup group = groups.get(i);
				if (itemsList.size() > i) {
					List<EventDisplayGroupItem> items = itemsList.get(i);
					if (items != null) {
						group.setItems(items);
					}
				}
			}

		} catch (Exception e) {
			System.err
					.println("Error retrieving Event Display Groups for event "
							+ eventId + ": " + e.getMessage());
			e.printStackTrace();
		}

		return groups;
	}

	public Map<Integer, List<EventDisplayGroup>> retrieveListByList(
			List<Integer> eventIds) {
		Map<Integer, List<EventDisplayGroup>> groupsMap = new HashMap<Integer, List<EventDisplayGroup>>();

		try {
			String sql = "select eventid, title from lc_eventdisplaygroup where eventid in (-1";
			for (int i = 0; i < eventIds.size(); i++) {
				sql += ", ?";
			}
			sql += ") order by eventid, sortorder";
			List<Object> params = new ArrayList<Object>();
			for (Integer eventId : eventIds) {
				params.add(eventId);
			}
			groupsMap = DataUtils
					.query(
							sql,
							params,
							LC_DATA_SOURCE,
							new ResultSetHandler<Map<Integer, List<EventDisplayGroup>>>() {
								public Map<Integer, List<EventDisplayGroup>> handleResultSet(
										ResultSet rs) throws Exception {
									Map<Integer, List<EventDisplayGroup>> groupsMap = new HashMap<Integer, List<EventDisplayGroup>>();

									while (rs.next()) {
										EventDisplayGroup group = new EventDisplayGroup();
										group.setTitle(rs.getString("title"));

										Integer eventId = new Integer(rs
												.getInt("eventid"));
										List<EventDisplayGroup> groups = groupsMap
												.get(eventId);
										if (groups == null) {
											groups = new ArrayList<EventDisplayGroup>();
											groupsMap.put(eventId, groups);
										}
										groups.add(group);
									}

									return groupsMap;
								}
							});

			// optionIds
			Map<Integer, List<List<EventDisplayGroupItem>>> itemsListMap = retrieveItemsByEventIds(eventIds);
			for (Integer eventId : groupsMap.keySet()) {
				List<EventDisplayGroup> groups = groupsMap.get(eventId);
				List<List<EventDisplayGroupItem>> itemsList = itemsListMap
						.get(eventId);
				for (int i = 0; i < groups.size(); i++) {
					EventDisplayGroup group = groups.get(i);
					if (itemsList.size() > i) {
						List<EventDisplayGroupItem> items = itemsList.get(i);
						if (items != null) {
							group.setItems(items);
						}
					}
				}
			}

		} catch (Exception e) {
			System.err
					.println("Error retrieving Event Display Groups for list of event IDs: "
							+ e.getMessage());
			e.printStackTrace();
		}

		return groupsMap;
	}

	public void saveByEvent(Event event) {
		try {
			// Delete existing display groups
			String deleteSql = "delete from lc_eventdisplaygroup where eventid = ?";
			List<Object> deleteParams = new ArrayList<Object>();
			deleteParams.add(event.getEventId());
			DataUtils.execute(deleteSql, deleteParams, LC_DATA_SOURCE);

			// Delete existing display group items
			String deleteItemSql = "delete from lc_eventdisplaygroupitem where eventid = ?";
			List<Object> deleteItemParams = new ArrayList<Object>();
			deleteItemParams.add(event.getEventId());
			DataUtils.execute(deleteItemSql, deleteItemParams, LC_DATA_SOURCE);

			// Insert current display groups
			for (int i = 0; i < event.getEventDisplayGroups().size(); i++) {
				EventDisplayGroup group = event.getEventDisplayGroups().get(i);
				String insertSql = "insert into lc_eventdisplaygroup (eventid, title, sortorder) values (?, ?, ?)";
				List<Object> insertParams = new ArrayList<Object>();
				insertParams.add(event.getEventId());
				insertParams.add(group.getTitle());
				insertParams.add(i);
				DataUtils.execute(insertSql, insertParams, LC_DATA_SOURCE);

				// Update group items
				saveDisplayGroupItems(group, event.getEventId(), i);
			}
		} catch (Exception e) {
			System.err.println("Error saving Display Group for event "
					+ event.getEventId() + ": " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void saveDisplayGroupItems(EventDisplayGroup group, int eventId,
			int groupSortOrder) {
		try {
			// Insert current display groups
			for (int i = 0; i < group.getItems().size(); i++) {
				EventDisplayGroupItem item = group.getItems().get(i);
				String sql = "insert into lc_eventdisplaygroupitem (eventid, groupsortorder, itemid, itemtypeid, itemsortorder) values (?, ?, ?, ?, ?)";
				List<Object> params = new ArrayList<Object>();
				params.add(eventId);
				params.add(groupSortOrder);
				params.add(item.getItemId());
				params.add(item.getItemType());
				params.add(i);
				DataUtils.execute(sql, params, LC_DATA_SOURCE);
			}
		} catch (Exception e) {
			System.err.println("Error saving Display Group Item for event "
					+ eventId + ", group " + groupSortOrder + ": "
					+ e.getMessage());
			e.printStackTrace();
		}
	}

	private List<List<EventDisplayGroupItem>> retrieveItemsByEventId(int eventId) {
		List<List<EventDisplayGroupItem>> itemsList = new ArrayList<List<EventDisplayGroupItem>>();

		try {
			String sql = "select groupsortorder, itemid, itemtypeid from lc_eventdisplaygroupitem where eventid = ? order by groupsortorder, itemsortorder";
			List<Object> params = new ArrayList<Object>();
			params.add(eventId);
			itemsList = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<List<List<EventDisplayGroupItem>>>() {
						public List<List<EventDisplayGroupItem>> handleResultSet(
								ResultSet rs) throws Exception {
							List<List<EventDisplayGroupItem>> itemsList = new ArrayList<List<EventDisplayGroupItem>>();

							while (rs.next()) {
								int groupSortOrder = rs
										.getInt("groupsortorder");
								int itemId = rs.getInt("itemid");
								int itemTypeId = rs.getInt("itemtypeid");

								while (itemsList.size() <= groupSortOrder) {
									itemsList
											.add(new ArrayList<EventDisplayGroupItem>());
								}

								List<EventDisplayGroupItem> items = itemsList
										.get(groupSortOrder);

								EventDisplayGroupItem item = new EventDisplayGroupItem();
								item.setItemId(itemId);
								item.setItemType(itemTypeId);
								items.add(item);
							}

							return itemsList;
						}
					});
		} catch (Exception e) {
			System.err
					.println("Error retrieving List of Lists of Display Group Items for Event "
							+ eventId + ": " + e.getMessage());
			e.printStackTrace();
		}

		return itemsList;
	}

	private Map<Integer, List<List<EventDisplayGroupItem>>> retrieveItemsByEventIds(
			List<Integer> eventIds) {
		Map<Integer, List<List<EventDisplayGroupItem>>> itemsListMap = new HashMap<Integer, List<List<EventDisplayGroupItem>>>();

		try {
			String sql = "select eventid, groupsortorder, itemid, itemtypeid from lc_eventdisplaygroupitem where eventid in (-1";
			for (int i = 0; i < eventIds.size(); i++) {
				sql += ", ?";
			}
			sql += ") order by eventid, groupsortorder, itemsortorder";
			List<Object> params = new ArrayList<Object>();
			for (Integer eventId : eventIds) {
				params.add(eventId);
			}
			itemsListMap = DataUtils
					.query(
							sql,
							params,
							LC_DATA_SOURCE,
							new ResultSetHandler<Map<Integer, List<List<EventDisplayGroupItem>>>>() {
								public Map<Integer, List<List<EventDisplayGroupItem>>> handleResultSet(
										ResultSet rs) throws Exception {
									Map<Integer, List<List<EventDisplayGroupItem>>> itemsListMap = new HashMap<Integer, List<List<EventDisplayGroupItem>>>();

									while (rs.next()) {
										int groupSortOrder = rs
												.getInt("groupsortorder");
										int itemId = rs.getInt("itemid");
										int itemTypeId = rs
												.getInt("itemtypeid");

										Integer eventId = new Integer(rs.getInt("eventid"));
										List<List<EventDisplayGroupItem>> itemsList = itemsListMap.get(eventId);
										if (itemsList == null) {
											itemsList = new ArrayList<List<EventDisplayGroupItem>>();
											itemsListMap.put(eventId, itemsList);
										}
										
										while (itemsList.size() <= groupSortOrder) {
											itemsList
													.add(new ArrayList<EventDisplayGroupItem>());
										}

										List<EventDisplayGroupItem> items = itemsList
												.get(groupSortOrder);

										EventDisplayGroupItem item = new EventDisplayGroupItem();
										item.setItemId(itemId);
										item.setItemType(itemTypeId);
										items.add(item);
									}

									return itemsListMap;
								}
							});
		} catch (Exception e) {
			System.err
					.println("Error retrieving List of Lists of Display Group Items for list of event IDs: "
							+ e.getMessage());
			e.printStackTrace();
		}

		return itemsListMap;
	}
}

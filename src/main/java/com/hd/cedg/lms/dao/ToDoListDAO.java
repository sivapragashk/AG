package com.hd.cedg.lms.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.hd.cedg.lms.model.Course;
import com.hd.cedg.lms.model.LearningUser;
import com.hd.cedg.lms.model.ToDoList;
import com.hd.cedg.lms.model.ToDoListSet;
import com.hd.cedg.lms.model.ToDoListTranscript;
import com.hd.mis.data.DataUtils;
import com.hd.mis.data.ResultSetHandler;

public class ToDoListDAO extends BaseLmsDAO{

	private CourseDAO courseDAO;

	public ToDoListDAO() {
		courseDAO = new CourseDAO();
	}
	
	public ToDoList retrieveForUser(int toDoListId, LearningUser user, int toDoListSetId) {
		return retrieveForUser(toDoListId, user.getUserId(), user.getAccessTags(), toDoListSetId);
	}
	public ToDoList retrieveForUser(int toDoListId, int userId, List<String> accessTags, int toDoListSetId) {
		// There must be an existing ToDoListTranscript entry for this user
		if (existsTranscript(toDoListId, userId)) {
			// Retrieve the ToDoList
			ToDoList toDoList = retrieve(toDoListId, toDoListSetId);

			// Retrieve its Courses
			Map<String, Course> courseMap = courseDAO.retrieveMapByToDoList(toDoList);

			// Filter
			filterCourses(toDoList, accessTags, courseMap);

			return toDoList;
		} else {
			return null;
		}
	}
	
	private void filterCourses(ToDoList toDoList, List<String> accessTags,
			Map<String, Course> courseMap) {

		List<ToDoListSet> filteredSets = new ArrayList<ToDoListSet>();

		for (ToDoListSet listSet : toDoList.getListSets()) {

			ToDoListSet filteredSet = new ToDoListSet();
			filteredSet.setTitle(listSet.getTitle());
			filteredSet.setId(listSet.getId());

			for (String learningId : listSet.getLearningIds()) {
				Course course = courseMap.get(learningId);
				if (course != null && course.isPermissible(accessTags)) {
					filteredSet.getLearningIds().add(learningId);
				} else if (course != null
						&& listSet.getAlwaysShowIds().contains(learningId)) {
					filteredSet.getLearningIds().add(learningId);
					filteredSet.getAlwaysShowIds().add(learningId);
				} else {
				}
			}

			if (filteredSet.getLearningIds().size() > 0) {
				filteredSets.add(filteredSet);
			}
		}

		toDoList.setListSets(filteredSets);

	}
	
	public ToDoList retrieve(int toDoListId, int toDoListSetId) {
		ToDoList toDoList = null;

		try {
			String sql = "select todolistid, name, imageurl, enrollsendemail, enrollfromaddress, enrollbccaddresses, enrollsubject, enrolltextmessage, enrollhtmlmessage, completesendemail, completefromaddress, completebccaddresses, completesubject, completetextmessage, completehtmlmessage, certprogram, coalesce(certthruyear, 0) certthruyear, oncompleteenroll, active, programtype, completioncheck, fromdate, todate, numofcourses from lc_todolist where todolistid = ?";
			List<Object> params = new ArrayList<Object>();
			params.add(toDoListId);
			toDoList = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<ToDoList>() {
						public ToDoList handleResultSet(ResultSet rs)
								throws Exception {
							ToDoList toDoList = null;
							if (rs.next()) {
								toDoList = new ToDoList();
								toDoList.setToDoListId(rs.getInt("todolistid"));
								toDoList.setTitle(rs.getString("name"));
								toDoList.setImageUrl(rs.getString("imageurl"));
								toDoList.setCertProgram(rs
										.getString("certprogram"));
								toDoList.setCertThruYear(rs
										.getInt("certthruyear"));
								toDoList.setOnCompleteEnroll(rs
										.getInt("oncompleteenroll"));
								toDoList.getEnrollEmail().setSendEmail(
										rs.getInt("enrollsendemail") != 0);
								toDoList.getEnrollEmail().setFromAddress(
										rs.getString("enrollfromaddress"));
								toDoList.getEnrollEmail().setBccAddresses(
										rs.getString("enrollbccaddresses"));
								toDoList.getEnrollEmail().setSubject(
										rs.getString("enrollsubject"));
								toDoList.getEnrollEmail().setTextMessage(
										rs.getString("enrolltextmessage"));
								toDoList.getEnrollEmail().setHtmlMessage(
										rs.getString("enrollhtmlmessage"));
								toDoList.getCompleteEmail().setSendEmail(
										rs.getInt("completesendemail") != 0);
								toDoList.getCompleteEmail().setFromAddress(
										rs.getString("completefromaddress"));
								toDoList.getCompleteEmail().setBccAddresses(
										rs.getString("completebccaddresses"));
								toDoList.getCompleteEmail().setSubject(
										rs.getString("completesubject"));
								toDoList.getCompleteEmail().setTextMessage(
										rs.getString("completetextmessage"));
								toDoList.getCompleteEmail().setHtmlMessage(
										rs.getString("completehtmlmessage"));
								toDoList.setActive(rs.getInt("active") != 0);
								toDoList.setProgramType(rs.getString("programtype"));
								toDoList.setToDoListCompletion(rs.getString("completioncheck"));
								toDoList.setFromDate(rs.getString("fromDate"), "MM/dd/yyyy");
								toDoList.setToDate(rs.getString("toDate"), "MM/dd/yyyy");
								toDoList.setNumberOfCourses(rs.getInt("numofcourses"));
							}
							return toDoList;
						}
					});

			if (toDoList != null) {
				List<ToDoListSet> listSets = retrieveListSets(toDoListId, toDoListSetId);
				toDoList.setListSets(listSets);
			}
		} catch (Exception e) {
			System.err.println("Error retrieving To Do List " + toDoListId
					+ ": " + e.getMessage());
			e.printStackTrace();
		}

		return toDoList;
	}
	
	private List<ToDoListSet> retrieveListSets(int toDoListId, int toDoListSetId) {
		List<ToDoListSet> listSets = new ArrayList<ToDoListSet>();
		
		try {
			String sql = "select ls.setorder, ls.name, item.learningid, item.setitemorder, item.alwaysshow from lc_todolistset ls inner join lc_todolistsetitem item on item.todolistid = ls.todolistid and item.setorder = ls.setorder where ls.todolistid = ? and ? in (0, ls.setorder) order by ls.setorder, item.setitemorder";
			List<Object> params = new ArrayList<Object>();
			params.add(toDoListId);
			params.add(toDoListSetId);
			listSets = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<List<ToDoListSet>>() {
						public List<ToDoListSet> handleResultSet(ResultSet rs)
								throws Exception {
							List<ToDoListSet> listSets = new ArrayList<ToDoListSet>();
							while (rs.next()) {
								int setId = rs.getInt("setOrder") - 1;
								while (setId >= listSets.size()) {
									listSets.add(new ToDoListSet());
								}
								ToDoListSet listSet = listSets.get(setId);
								listSet.setTitle(rs.getString("name"));
								listSet.setId(rs.getInt("setOrder"));
								List<String> learningIds = listSet
										.getLearningIds();
								int itemId = rs.getInt("setitemorder") - 1;
								while (itemId >= learningIds.size()) {
									learningIds.add("");
								}
								String learningId = rs.getString("learningid");
								learningIds.set(itemId, learningId);
								if (rs.getInt("alwaysshow") == 1) {
									listSet.getAlwaysShowIds().add(learningId);
								}
							}
							return listSets;
						}
					});
		} catch (Exception e) {
			System.err
					.println("Error retrieving To Do List Sets for To Do List "
							+ toDoListId + ": " + e.getMessage());
			e.printStackTrace();
		}

		return listSets;
	}
	
	/**
	 * Checks for the existence of a transcript. If the user ID is 0, returns
	 * true automatically.
	 */
	public boolean existsTranscript(int toDoListId, int userId) {

		if (userId == 0) {
			return true;
		}

		try {
			String sql = "select userid from lc_todolisttranscript where todolistid = ? and userid = ?";
			List<Object> params = new ArrayList<Object>();
			params.add(toDoListId);
			params.add(userId);
			boolean exists = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<Boolean>() {
						public Boolean handleResultSet(ResultSet rs)
								throws Exception {
							return rs.next();
						}
					}).booleanValue();
			return exists;
		} catch (Exception e) {
			System.err
					.println("Error looking up existence of ToDoListTranscript for toDoListId "
							+ toDoListId
							+ " and user "
							+ userId
							+ ": "
							+ e.getMessage());
			e.printStackTrace();
		}

		return false;
	}
	
	public int retrieveMaxCertYear(int userId, String certProgram) {
		int maxCertYear = 0;
		try {
			String sql = "select max(year) year from lc_usercert where userid = ? and program = ?";
			List<Object> params = new ArrayList<Object>();
			params.add(userId);
			params.add(certProgram);
			maxCertYear = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<Integer>() {
						public Integer handleResultSet(ResultSet rs)
								throws Exception {
							if (rs.next()) {
								return new Integer(rs.getInt("year"));
							} else {
								return new Integer(0);
							}
						}
					}).intValue();
		} catch (Exception e) {
			System.err
					.println("Error retrieving max certification year for user "
							+ userId
							+ " and program "
							+ certProgram
							+ ": "
							+ e.getMessage());
			e.printStackTrace();
		}
		return maxCertYear;
	}
	
	/**
	 * Retrieves the active To Do Lists for a given user. If the user's ID is 0,
	 * return all To Do Lists.
	 */
	public List<ToDoList> retrieveAllActiveForUser(LearningUser user) {
		List<Integer> toDoListIds = new ArrayList<Integer>();
		if (user.getUserId() > 0) {
			try {
				String sql = "select todolistid from lc_todolisttranscript where userid = ? and iscomplete = 0";
				List<Object> params = new ArrayList<Object>();
				params.add(user.getUserId());
				toDoListIds = DataUtils.query(sql, params, LC_DATA_SOURCE,
						new ResultSetHandler<List<Integer>>() {
							public List<Integer> handleResultSet(ResultSet rs)
									throws Exception {
								List<Integer> toDoListIds = new ArrayList<Integer>();
								while (rs.next()) {
									int toDoListId = rs.getInt("todolistid");
									toDoListIds.add(new Integer(toDoListId));
								}
								return toDoListIds;
							}
						});
			} catch (Exception e) {
				System.err
						.println("Error retrieving all active To Do Lists for user "
								+ user.getUserId() + ": " + e.getMessage());
				e.printStackTrace();
			}
		} else {
			try {
				String sql = "select todolistid from lc_todolist order by todolistid";
				List<Object> params = new ArrayList<Object>();
				toDoListIds = DataUtils.query(sql, params, LC_DATA_SOURCE,
						new ResultSetHandler<List<Integer>>() {
							public List<Integer> handleResultSet(ResultSet rs)
									throws Exception {
								List<Integer> toDoListIds = new ArrayList<Integer>();
								while (rs.next()) {
									int toDoListId = rs.getInt("todolistid");
									toDoListIds.add(new Integer(toDoListId));
								}
								return toDoListIds;
							}
						});
			} catch (Exception e) {
				System.err
						.println("Error retrieving all active To Do Lists for user 0: "
								+ e.getMessage());
				e.printStackTrace();
			}
		}

		List<ToDoList> toDoLists = new ArrayList<ToDoList>();
		for (Integer toDoListId : toDoListIds) {
			ToDoList toDoList = retrieveForUser(toDoListId.intValue(), user, 0);
			toDoLists.add(toDoList);
		}

		return toDoLists;
	}

	
		
	public List<ToDoList> retrieveAll() {
		List<ToDoList> toDoLists = new ArrayList<ToDoList>();

		try {
			String sql = "select todolistid, name, imageurl, enrollsendemail, enrollfromaddress, enrollbccaddresses, enrollsubject, enrolltextmessage, enrollhtmlmessage, completesendemail, completefromaddress, completebccaddresses, completesubject, completetextmessage, completehtmlmessage, certprogram, coalesce(certthruyear, 0) certthruyear, oncompleteenroll, active, programtype, completioncheck, fromdate, todate, numofcourses from lc_todolist order by todolistid";
			List<Object> params = new ArrayList<Object>();
			toDoLists = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<List<ToDoList>>() {
						public List<ToDoList> handleResultSet(ResultSet rs)
								throws Exception {
							List<ToDoList> toDoLists = new ArrayList<ToDoList>();
							while (rs.next()) {
								ToDoList toDoList = new ToDoList();
								toDoList.setToDoListId(rs.getInt("todolistid"));
								toDoList.setTitle(rs.getString("name"));
								toDoList.setImageUrl(rs.getString("imageurl"));
								toDoList.setCertProgram(rs
										.getString("certprogram"));
								toDoList.setCertThruYear(rs
										.getInt("certthruyear"));
								toDoList.setOnCompleteEnroll(rs
										.getInt("oncompleteenroll"));
								toDoList.getEnrollEmail().setSendEmail(
										rs.getInt("enrollsendemail") != 0);
								toDoList.getEnrollEmail().setFromAddress(
										rs.getString("enrollfromaddress"));
								toDoList.getEnrollEmail().setBccAddresses(
										rs.getString("enrollbccaddresses"));
								toDoList.getEnrollEmail().setSubject(
										rs.getString("enrollsubject"));
								toDoList.getEnrollEmail().setTextMessage(
										rs.getString("enrolltextmessage"));
								toDoList.getEnrollEmail().setHtmlMessage(
										rs.getString("enrollhtmlmessage"));
								toDoList.getCompleteEmail().setSendEmail(
										rs.getInt("completesendemail") != 0);
								toDoList.getCompleteEmail().setFromAddress(
										rs.getString("completefromaddress"));
								toDoList.getCompleteEmail().setBccAddresses(
										rs.getString("completebccaddresses"));
								toDoList.getCompleteEmail().setSubject(
										rs.getString("completesubject"));
								toDoList.getCompleteEmail().setTextMessage(
										rs.getString("completetextmessage"));
								toDoList.getCompleteEmail().setHtmlMessage(
										rs.getString("completehtmlmessage"));
								toDoList.setActive(rs.getInt("active") != 0);
								toDoList.setProgramType(
										rs.getString("programtype"));
								toDoList.setToDoListCompletion(rs
										.getString("completioncheck"));
								toDoList.setFromDate(rs.getString("fromDate"),
										"MM/dd/yyyy");
								toDoList.setToDate(rs.getString("toDate"),
										"MM/dd/yyyy");
								toDoList.setNumberOfCourses(rs
										.getInt("numofcourses"));
								toDoLists.add(toDoList);
							}
							return toDoLists;
						}
					});

			for (ToDoList toDoList : toDoLists) {
				List<ToDoListSet> listSets = retrieveListSets(toDoList
						.getToDoListId(), 0);
				toDoList.setListSets(listSets);
			}
		} catch (Exception e) {
			System.err.println("Error retrieving all To Do Lists: "
					+ e.getMessage());
			e.printStackTrace();
		}

		return toDoLists;
	}

	public void save(ToDoList toDoList) {
		if (toDoList.getToDoListId() > 0) {
			update(toDoList);
		} else {
			insert(toDoList);
		}
	}

	private void insert(ToDoList toDoList) {
		// Need a ToDoList ID
		int toDoListId = generateToDoListId();

		if (toDoListId > 0) {
			try {
				String sql = "insert into lc_todolist (todolistid, name, imageurl, enrollsendemail, enrollfromaddress, enrollbccaddresses, enrollsubject, enrolltextmessage, enrollhtmlmessage, completesendemail, completefromaddress, completebccaddresses, completesubject, completetextmessage, completehtmlmessage, certprogram, certthruyear, oncompleteenroll, active, programtype, completioncheck, fromdate, todate, numofcourses) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
				List<Object> params = new ArrayList<Object>();
				params.add(toDoListId);
				params.add(toDoList.getTitle());
				params.add(toDoList.getImageUrl());
				params.add(toDoList.getEnrollEmail().isSendEmail() ? 1 : 0);
				params.add(toDoList.getEnrollEmail().getFromAddress());
				params.add(toDoList.getEnrollEmail().getBccAddresses());
				params.add(toDoList.getEnrollEmail().getSubject());
				params.add(toDoList.getEnrollEmail().getTextMessage());
				params.add(toDoList.getEnrollEmail().getHtmlMessage());
				params.add(toDoList.getCompleteEmail().isSendEmail() ? 1 : 0);
				params.add(toDoList.getCompleteEmail().getFromAddress());
				params.add(toDoList.getCompleteEmail().getBccAddresses());
				params.add(toDoList.getCompleteEmail().getSubject());
				params.add(toDoList.getCompleteEmail().getTextMessage());
				params.add(toDoList.getCompleteEmail().getHtmlMessage());
				params.add(toDoList.getCertProgram());
				params.add(toDoList.getCertThruYear());
				params.add(toDoList.getOnCompleteEnroll());
				params.add(toDoList.isActive() ? 1 : 0);
				params.add(toDoList.getProgramType());
				params.add(toDoList.getToDoListCompletion());
				params.add(toDoList.getFormattedDate("MM/dd/yyyy", toDoList.getFromDate()));
				params.add(toDoList.getFormattedDate("MM/dd/yyyy", toDoList.getToDate()));
				params.add(toDoList.getNumberOfCourses());
				DataUtils.execute(sql, params, LC_DATA_SOURCE);
			} catch (Exception e) {
				System.err.println("Error inserting To Do List: "
						+ e.getMessage());
				e.printStackTrace();
			}

			// Insert any List Sets
			insertListSets(toDoListId, toDoList.getListSets());
		} else {
			System.err
					.println("Did not generate a valid toDoListId - Cannot insert To Do List");
		}
	}

	private void update(ToDoList toDoList) {
		try {
			String sql = "update lc_todolist set name = ?, imageurl = ?, enrollsendemail = ?, enrollfromaddress = ?, enrollbccaddresses = ?, enrollsubject = ?, enrolltextmessage = ?, enrollhtmlmessage = ?, completesendemail = ?, completefromaddress = ?, completebccaddresses = ?, completesubject = ?, completetextmessage = ?, completehtmlmessage = ?, certprogram = ?, certthruyear = ?, oncompleteenroll = ?, active = ?, programtype = ?, completioncheck = ?, fromdate = ?, todate = ?, numofcourses = ? where todolistid = ?";
			List<Object> params = new ArrayList<Object>();
			params.add(toDoList.getTitle());
			params.add(toDoList.getImageUrl());
			params.add(toDoList.getEnrollEmail().isSendEmail() ? 1 : 0);
			params.add(toDoList.getEnrollEmail().getFromAddress());
			params.add(toDoList.getEnrollEmail().getBccAddresses());
			params.add(toDoList.getEnrollEmail().getSubject());
			params.add(toDoList.getEnrollEmail().getTextMessage());
			params.add(toDoList.getEnrollEmail().getHtmlMessage());
			params.add(toDoList.getCompleteEmail().isSendEmail() ? 1 : 0);
			params.add(toDoList.getCompleteEmail().getFromAddress());
			params.add(toDoList.getCompleteEmail().getBccAddresses());
			params.add(toDoList.getCompleteEmail().getSubject());
			params.add(toDoList.getCompleteEmail().getTextMessage());
			params.add(toDoList.getCompleteEmail().getHtmlMessage());
			params.add(toDoList.getCertProgram());
			params.add(toDoList.getCertThruYear());
			params.add(toDoList.getOnCompleteEnroll());
			params.add(toDoList.isActive() ? 1 : 0);
			params.add(toDoList.getProgramType());
			params.add(toDoList.getToDoListCompletion());
			params.add(toDoList.getFormattedDate("MM/dd/yyyy", toDoList.getFromDate()));
			params.add(toDoList.getFormattedDate("MM/dd/yyyy", toDoList.getToDate()));
			params.add(toDoList.getNumberOfCourses());
			params.add(toDoList.getToDoListId());
			DataUtils.execute(sql, params, LC_DATA_SOURCE);
		} catch (Exception e) {
			System.err.println("Error updating ToDoList "
					+ toDoList.getToDoListId() + ": " + e.getMessage());
			e.printStackTrace();
		}

		try {
			// Remove all ListSets and ListSetItems
			String sql = "delete from lc_todolistset where todolistid = ?";
			List<Object> params = new ArrayList<Object>();
			params.add(toDoList.getToDoListId());
			DataUtils.execute(sql, params, LC_DATA_SOURCE);

			String sql2 = "delete from lc_todolistsetitem where todolistid = ?";
			List<Object> params2 = new ArrayList<Object>();
			params2.add(toDoList.getToDoListId());
			DataUtils.execute(sql2, params2, LC_DATA_SOURCE);

			insertListSets(toDoList.getToDoListId(), toDoList.getListSets());
		} catch (Exception e) {
			System.err
					.println("Error removing List Sets and List Set Items from ToDoList "
							+ toDoList.getToDoListId()
							+ " during Update: "
							+ e.getMessage());
			e.printStackTrace();
		}
	}

	private int generateToDoListId() {
		int toDoListId = 0;
		try {
			String sql = "select lc_todolist_seq.nextval todolistid from dual";
			List<Object> params = new ArrayList<Object>();
			Integer id = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<Integer>() {
						public Integer handleResultSet(ResultSet rs)
								throws Exception {
							if (rs.next()) {
								return new Integer(rs.getInt("todolistid"));
							}
							return null;
						}
					});
			if (id != null) {
				toDoListId = id.intValue();
			}
		} catch (Exception e) {
			System.err.println("Error generating a new To Do List ID: "
					+ e.getMessage());
			e.printStackTrace();
		}
		return toDoListId;
	}

	private void insertListSets(int toDoListId, List<ToDoListSet> listSets) {
		for (int i = 0; i < listSets.size(); i++) {
			ToDoListSet listSet = listSets.get(i);
			try {
				String sql = "insert into lc_todolistset (todolistid, setorder, name) values (?, ?, ?)";
				List<Object> params = new ArrayList<Object>();
				params.add(toDoListId);
				params.add(i + 1);
				params.add(listSet.getTitle());
				DataUtils.execute(sql, params, LC_DATA_SOURCE);
			} catch (Exception e) {
				System.err.println("Error inserting ToDoListSet: "
						+ e.getMessage());
				e.printStackTrace();
			}
			for (int j = 0; j < listSet.getLearningIds().size(); j++) {
				String learningId = listSet.getLearningIds().get(j);
				try {
					String sql = "insert into lc_todolistsetitem (todolistid, setorder, learningid, setitemorder, alwaysshow) values (?, ?, ?, ?, ?)";
					List<Object> params = new ArrayList<Object>();
					params.add(toDoListId);
					params.add(i + 1);
					params.add(learningId);
					params.add(j + 1);
					params
							.add(listSet.getAlwaysShowIds()
									.contains(learningId) ? 1 : 0);
					DataUtils.execute(sql, params, LC_DATA_SOURCE);
				} catch (Exception e) {
					System.err.println("Error inserting ToDoListSet item: "
							+ e.getMessage());
					e.printStackTrace();
				}
			}
		}

	}

	public List<ToDoListTranscript> retrieveTranscripts(int userId) {
		List<ToDoListTranscript> transcripts = new ArrayList<ToDoListTranscript>();

		try {
			String sql = "select todolistid, iscomplete, to_char(startdate, 'MM-DD-YYYY') startdate, to_char(enddate, 'MM-DD-YYYY') enddate from lc_todolisttranscript where userid = ?";
			List<Object> params = new ArrayList<Object>();
			params.add(userId);
			transcripts = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<List<ToDoListTranscript>>() {
						public List<ToDoListTranscript> handleResultSet(
								ResultSet rs) throws Exception {
							List<ToDoListTranscript> transcripts = new ArrayList<ToDoListTranscript>();
							while (rs.next()) {
								ToDoListTranscript transcript = new ToDoListTranscript();
								transcript.setToDoListId(rs
										.getInt("todolistid"));
								boolean isComplete = rs.getInt("iscomplete") == 0;
								if (isComplete) {
									transcript
											.setStatus(ToDoListTranscript.STATUS_MIDDLE);
								} else {
									transcript
											.setStatus(ToDoListTranscript.STATUS_DONE);
								}
								transcript.setStartDate(rs
										.getString("startdate"), "MM-dd-yyyy");
								transcript.setEndDate(rs.getString("enddate"),
										"MM-dd-yyyy");
								transcripts.add(transcript);
							}
							return transcripts;
						}
					});
		} catch (Exception e) {
			System.err
					.println("Error retrieving List of To Do List Transcripts for "
							+ userId + ": " + e.getMessage());
			e.printStackTrace();
		}

		return transcripts;
	}

	public List<ToDoListTranscript> retrieveCurrentTranscripts(int userId) {
		List<ToDoListTranscript> allTranscripts = retrieveTranscripts(userId);
		List<ToDoListTranscript> currentTranscripts = new ArrayList<ToDoListTranscript>();
		for (ToDoListTranscript transcript : allTranscripts) {
			if (transcript.getStatus() == ToDoListTranscript.STATUS_MIDDLE) {
				currentTranscripts.add(transcript);
			}
		}
		return currentTranscripts;
	}

	public List<ToDoListTranscript> retrieveCompleteTranscripts(int userId) {
		List<ToDoListTranscript> allTranscripts = retrieveTranscripts(userId);
		List<ToDoListTranscript> currentTranscripts = new ArrayList<ToDoListTranscript>();
		for (ToDoListTranscript transcript : allTranscripts) {
			if (transcript.getStatus() == ToDoListTranscript.STATUS_DONE) {
				currentTranscripts.add(transcript);
			}
		}
		return currentTranscripts;
	}

	
	/**
	 * Checks for the existence of any active transcripts for the user. The user
	 * must also be active.
	 */
	public boolean existsAnyActiveTranscript(int userId) {
		try {
			String sql = "select tran.userid from lc_todolisttranscript tran inner join lc_user u on u.userid = tran.userid and u.active = 1 where tran.userid = ? and tran.iscomplete = 0";
			List<Object> params = new ArrayList<Object>();
			params.add(userId);
			boolean exists = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<Boolean>() {
						public Boolean handleResultSet(ResultSet rs)
								throws Exception {
							return rs.next();
						}
					}).booleanValue();
			return exists;
		} catch (Exception e) {
			System.err
					.println("Error looking up existence of any ToDoListTranscript for user "
							+ userId + ": " + e.getMessage());
			e.printStackTrace();
		}

		return false;
	}

	public void insertTranscript(int toDoListId, int userId) {
		if (!existsTranscript(toDoListId, userId)) {
			try {
				String sql = "insert into lc_todolisttranscript (userid, todolistid, startdate, enddate, iscomplete) values (?, ?, sysdate, null, 0)";
				List<Object> params = new ArrayList<Object>();
				params.add(userId);
				params.add(toDoListId);
				DataUtils.execute(sql, params, LC_DATA_SOURCE);
			} catch (Exception e) {
				System.err
						.println("Error inserting new ToDoListTranscript for toDoListId "
								+ toDoListId
								+ " and user "
								+ userId
								+ ": "
								+ e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public void closeTranscript(ToDoList toDoList, int userId) {
		try {
			String sql = "update lc_todolisttranscript set enddate = sysdate, iscomplete = 1 where userid = ? and todolistid = ? and iscomplete = 0";
			List<Object> params = new ArrayList<Object>();
			params.add(userId);
			params.add(toDoList.getToDoListId());
			DataUtils.execute(sql, params, LC_DATA_SOURCE);
		} catch (Exception e) {
			System.err
					.println("Error closing ToDoListTranscript for toDoListId "
							+ toDoList.getToDoListId() + " and user " + userId
							+ ": " + e.getMessage());
			e.printStackTrace();
		}

		if (toDoList.getCertProgram() != null
				&& !"".equals(toDoList.getCertProgram())
				&& toDoList.getCertThruYear() > 0) {
			int currentCertYear = retrieveCurrentCertYear();
			if (currentCertYear > 0) {
				for (int year = currentCertYear; year <= toDoList
						.getCertThruYear(); year++) {
					try {
						String sql = "insert into lc_usercert (userid, program, year) values (?, ?, ?)";
						List<Object> params = new ArrayList<Object>();
						params.add(userId);
						params.add(toDoList.getCertProgram());
						params.add(year);
						DataUtils.execute(sql, params, LC_DATA_SOURCE);
					} catch (Exception e) {
						System.err
								.println("Error adding Certification Record for User "
										+ userId
										+ ", Program \""
										+ toDoList.getCertProgram()
										+ ", and year "
										+ year
										+ ": "
										+ e.getMessage());
						e.printStackTrace();
					}
				}
			}
		}
	}

	private int retrieveCurrentCertYear() {
		int year = 0;

		try {
			String sql = "select year from lc_currentcertyear";
			List<Object> params = new ArrayList<Object>();
			year = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<Integer>() {
						public Integer handleResultSet(ResultSet rs)
								throws Exception {
							if (rs.next()) {
								return rs.getInt("year");
							} else {
								return 0;
							}
						}
					}).intValue();
		} catch (Exception e) {
			System.err.println("Error retrieving Current Cert Year: "
					+ e.getMessage());
			e.printStackTrace();
		}

		return year;
	}

	public void deleteTranscript(int userId, int toDoListId) {
		try {
			String sql = "delete from lc_todolisttranscript where userid = ? and todolistid = ?";
			List<Object> params = new ArrayList<Object>();
			params.add(userId);
			params.add(toDoListId);
			DataUtils.execute(sql, params, LC_DATA_SOURCE);
		} catch (Exception e) {
			System.err.println("Error deleting To Do List transcript for User "
					+ userId + " and To Do List " + toDoListId + ": "
					+ e.getMessage());
			e.printStackTrace();
		}
	}

	public void saveTranscriptAccessTags(List<String> accessTags, int userId) {
		// Remove existing
		try {
			String sql = "delete from lc_todolisttranscriptaccesstag where userid = ?";
			List<Object> params = new ArrayList<Object>();
			params.add(userId);
			DataUtils.execute(sql, params, LC_DATA_SOURCE);
		} catch (Exception e) {
			System.err
					.println("Error removing To-Do List Transcript Access Tags as part of Save for user "
							+ userId + " : " + e.getMessage());
			e.printStackTrace();
		}

		// Insert new
		for (String accessTag : accessTags) {
			try {
				String sql = "insert into lc_todolisttranscriptaccesstag (userid, accesstagname) values (?, ?)";
				List<Object> params = new ArrayList<Object>();
				params.add(userId);
				params.add(accessTag);
				DataUtils.execute(sql, params, LC_DATA_SOURCE);
			} catch (Exception e) {
				System.err
						.println("Error inserting To-Do List Transcript Access Tags as part of Save for user "
								+ userId + " : " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public List<Object[]> retrieveAllUsernamesAndIdsWithTranscripts() {
		List<Object[]> usernameAndIds = new ArrayList<Object[]>();
		try {
			String sql = "select distinct u.username, u.userid from lc_user u inner join lc_todolisttranscript tran on tran.userid = u.userid and tran.iscomplete = 0";
			List<Object> params = new ArrayList<Object>();
			usernameAndIds = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<List<Object[]>>() {
						public List<Object[]> handleResultSet(ResultSet rs)
								throws Exception {
							List<Object[]> usernameAndIds = new ArrayList<Object[]>();
							while (rs.next()) {
								Object[] usernameAndId = new Object[2];
								usernameAndId[0] = rs.getString("username");
								usernameAndId[1] = rs.getInt("userid");
								usernameAndIds.add(usernameAndId);
							}
							return usernameAndIds;
						}
					});
		} catch (Exception e) {
			System.err
					.println("Error retrieving list of Usernames & User IDs of all users with To-Do Lists: "
							+ e.getMessage());
			e.printStackTrace();
		}
		return usernameAndIds;
	}
	
	
}

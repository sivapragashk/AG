package com.hd.cedg.lms.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import com.hd.cedg.lms.model.Event;
import com.hd.mis.data.DataUtils;
import com.hd.mis.data.ResultSetHandler;

public class EventDAO extends BaseLmsDAO{

	private EventOptionDAO eventOptionDAO;
	private EventOptionPackageDAO eventOptionPackageDAO;
	private EventRuleMutexDAO eventRuleMutexDAO;
	private EventRuleRequiredSetDAO eventRuleRequiredSetDAO;
	private EventDisplayGroupDAO eventDisplayGroupDAO;
	private EventPaymentMethodDAO eventPaymentMethodDAO;
	private EventQuestionDAO eventQuestionDAO;
	private EventAdminUsernamesDAO eventAdminUsernamesDAO;
		
	public EventDAO() {
		eventOptionDAO = new EventOptionDAO();
		eventOptionPackageDAO = new EventOptionPackageDAO();
		eventRuleMutexDAO = new EventRuleMutexDAO();
		eventRuleRequiredSetDAO = new EventRuleRequiredSetDAO();
		eventDisplayGroupDAO = new EventDisplayGroupDAO();
		eventPaymentMethodDAO = new EventPaymentMethodDAO();
		eventQuestionDAO = new EventQuestionDAO();
		eventAdminUsernamesDAO = new EventAdminUsernamesDAO();
	}
	
	public Event retrieve(int eventId) {
		Event event = null;

		try {
			String sql = "select ev.eventid, ev.title, ev.imageurl, ev.status, to_char(ev.startdate, 'YYYY-MM-DD') startdate, to_char(ev.starttime, 'HH24:MI:SS') starttime, to_char(ev.enddate, 'YYYY-MM-DD') enddate, to_char(ev.endtime, 'HH24:MI:SS') endtime, ev.enrollothers, ev.enrollmanaged, ev.capacity, coalesce(regcount.num, 0) currentattendeecount, ev.helpphone, ev.helpemail, ev.inperson, ev.venuename, ev.venueaddress1, ev.venueaddress2, ev.venuecity, ev.venueregion, ev.venuepostcode, ev.venuephone, ev.venueoverride, ev.virtuallive, ev.virtualtype, ev.virtualhelpphone, ev.mediasitepeidsilv, ev.mediasitepeidclas, ev.cost, ev.allowcc, ev.confemailfrom, ev.confemailbcc, ev.confemailsubject, ev.confemailhtml, ev.confemailtext, ev.remindemailfrom, ev.remindemailbcc, ev.remindemailsubject, ev.remindemailhtml, ev.remindemailtext, ev.instprogram, ev.points, ev.description from lc_event ev left join ( select eventid, count(*) num from lc_eventregistration where active = 1 group by eventid ) regcount on regcount.eventid = ev.eventid where ev.eventid = ? and ev.active = 1";
			List<Object> params = new ArrayList<Object>();
			params.add(eventId);
			event = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<Event>() {
						public Event handleResultSet(ResultSet rs)
								throws Exception {
							if (rs.next()) {
								Event event = new Event();

								event.setEventId(rs.getInt("eventid"));
								event.setTitle(rs.getString("title"));
								event.setImageUrl(rs.getString("imageurl"));
								event.setStatus(rs.getInt("status"));

								event.getDateRange().setStartDate(
										rs.getString("startdate"));
								event.getDateRange().setStartTime(
										rs.getString("starttime"));
								event.getDateRange().setEndDate(
										rs.getString("enddate"));
								event.getDateRange().setEndTime(
										rs.getString("endtime"));

								event
										.setEnrollOthers(rs
												.getInt("enrollothers") != 0);
								event.setEnrollManaged(rs
										.getInt("enrollmanaged") != 0);
								event.setCapacity(rs.getInt("capacity"));
								event.setCurrentAttendeeCount(rs
										.getInt("currentattendeecount"));

								event.setHelpPhone(rs.getString("helpphone"));
								event.setHelpEmail(rs.getString("helpemail"));

								event.setInPerson(rs.getInt("inperson"));
								event.setInstProgram(rs.getString("instprogram"));
								event.setPoints(rs.getInt("points"));
								event.setDescription(rs.getString("description"));
								event.getVenue().setName(
										rs.getString("venuename"));
								event.getVenue().setAddress1(
										rs.getString("venueaddress1"));
								event.getVenue().setAddress2(
										rs.getString("venueaddress2"));
								event.getVenue().setCity(
										rs.getString("venuecity"));
								event.getVenue().setRegion(
										rs.getString("venueregion"));
								event.getVenue().setPostCode(
										rs.getString("venuepostcode"));
								event.getVenue().setPhone(
										rs.getString("venuephone"));
								event.getVenue().setOverride(
										rs.getString("venueoverride"));

								event
										.setVirtualLive(rs
												.getInt("virtuallive") != 0);
								event.setVirtualType(rs.getInt("virtualtype"));
								event.setVirtualHelpPhone(rs
										.getString("virtualhelpphone"));
								event.setMediaSitePeidSilverlight(rs
										.getString("mediasitepeidsilv"));
								event.setMediaSitePeidClassic(rs
										.getString("mediasitepeidclas"));

								event.getCost().setCost(rs.getFloat("cost"));
								event.setAllowCC(rs.getInt("allowcc") != 0);

								event.setConfEmailFrom(rs
										.getString("confemailfrom"));
								event.setConfEmailBcc(rs
										.getString("confemailbcc"));
								event.setConfEmailSubject(rs
										.getString("confemailsubject"));
								event.setConfEmailHtml(rs
										.getString("confemailhtml"));
								event.setConfEmailText(rs
										.getString("confemailtext"));

								event.setRemindEmailFrom(rs
										.getString("remindemailfrom"));
								event.setRemindEmailBcc(rs
										.getString("remindemailbcc"));
								event.setRemindEmailSubject(rs
										.getString("remindemailsubject"));
								event.setRemindEmailHtml(rs
										.getString("remindemailhtml"));
								event.setRemindEmailText(rs
										.getString("remindemailtext"));

								return event;
							} else {
								return null;
							}

						}
					});

			if (event != null) {
				// eventOptions
				event.setEventOptions(eventOptionDAO
						.retrieveListByEventId(eventId));

				// eventOptionPackages
				event.setEventOptionPackages(eventOptionPackageDAO
						.retrieveListByEventId(eventId));

				// eventRuleMutexes
				event.setEventRuleMutexes(eventRuleMutexDAO
						.retrieveListByEventId(eventId));

				// eventRuleRequiredSets
				event.setEventRuleRequiredSets(eventRuleRequiredSetDAO
						.retrieveListByEventId(eventId));

				// eventDisplayGroups
				event.setEventDisplayGroups(eventDisplayGroupDAO
						.retrieveListByEventId(eventId));

				// paymentMethods
				event.setPaymentMethods(eventPaymentMethodDAO
						.retrieveListByEventId(eventId));

				// questions
				event.setQuestions(eventQuestionDAO
						.retrieveListByEventId(eventId));

				// admins
				event.setAdminUsernames(eventAdminUsernamesDAO
						.retrieveListByEventId(eventId));
			}
		} catch (Exception e) {
			System.err.println("Error retrieving Event " + eventId + ": "
					+ e.getMessage());
			e.printStackTrace();
		}

		return event;
	}
	
	public Event retrieveByRegistrationId(int registrationId) {
		int eventId = 0;
		try {
			String sql = "select eventid from lc_eventregistration where registrationid = ?";
			List<Object> params = new ArrayList<Object>();
			params.add(registrationId);
			eventId = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<Integer>() {
						public Integer handleResultSet(ResultSet rs)
								throws Exception {
							if (rs.next()) {
								return new Integer(rs.getInt("eventid"));
							} else {
								return new Integer(0);
							}
						}
					}).intValue();
		} catch (Exception e) {
			System.err.println("Error retrieving Event ID for registration ID "
					+ registrationId + ": " + e.getMessage());
			e.printStackTrace();
		}

		if (eventId > 0) {
			return retrieve(eventId);
		} else {
			return null;
		}
	}
	
	public List<Integer> retrieveIdsOfRegisteredUsers(int eventId) {
		List<Integer> regedUserIds = new ArrayList<Integer>();

		try {
			String sql = "select distinct userid from lc_eventregistration where eventid = ? and active = 1 order by userid";
			List<Object> params = new ArrayList<Object>();
			params.add(eventId);
			regedUserIds = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<List<Integer>>() {
						public List<Integer> handleResultSet(ResultSet rs)
								throws Exception {
							List<Integer> regedUserIds = new ArrayList<Integer>();
							while (rs.next()) {
								int userId = rs.getInt("userid");
								regedUserIds.add(new Integer(userId));
							}
							return regedUserIds;
						}
					});
		} catch (Exception e) {
			System.err
					.println("Error retrieving list of registered user IDs for event "
							+ eventId + ": " + e.getMessage());
			e.printStackTrace();
		}

		return regedUserIds;
	}

}

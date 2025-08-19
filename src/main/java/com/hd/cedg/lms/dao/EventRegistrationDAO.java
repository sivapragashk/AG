package com.hd.cedg.lms.dao;

import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hd.cedg.lms.model.Event;
import com.hd.cedg.lms.model.EventPaymentMethod;
import com.hd.cedg.lms.model.EventRegistration;
import com.hd.mis.data.DataUtils;
import com.hd.mis.data.ResultSetHandler;

public class EventRegistrationDAO extends BaseLmsDAO {

	private EventDAO eventDAO;

	public EventRegistrationDAO() {
		eventDAO = new EventDAO();
	}

	public EventRegistration retrieve(int registrationId) {
		EventRegistration registration = null;

		try {
			String sql = "select registrationid, to_char(registrationdate, 'YYYY-MM-DD') registrationdate, registeringuserid, userid, companyid, firstname, lastname, companyname, address, city, region, postcode, phone, email, totalcost, confirmationnumber, status, paymentmethodid, companytype, alliancelevel, sponsoringfabricator, sponsoringsalesrep, paymentnochargereason from lc_eventregistration where registrationid = ? and active = 1";
			List<Object> params = new ArrayList<Object>();
			params.add(registrationId);
			registration = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<EventRegistration>() {
						public EventRegistration handleResultSet(ResultSet rs)
								throws Exception {
							EventRegistration registration = new EventRegistration();
							if (rs.next()) {
								registration.setRegistrationId(rs
										.getInt("registrationid"));
								registration.setRegistrationDate(rs
										.getString("registrationdate"));
								registration.setRegisteringUserId(rs
										.getInt("registeringuserid"));
								registration.setUserId(rs.getInt("userid"));
								registration.setCompanyId(rs
										.getInt("companyid"));
								registration.setFirstName(rs
										.getString("firstname"));
								registration.setLastName(rs
										.getString("lastname"));
								registration.setCompanyName(rs
										.getString("companyname"));
								registration
										.setAddress(rs.getString("address"));
								registration.setCity(rs.getString("city"));
								registration.setRegion(rs.getString("region"));
								registration.setPostCode(rs
										.getString("postcode"));
								registration.setPhone(rs.getString("phone"));
								registration.setEmail(rs.getString("email"));
								// Set Cost Override as total cost? Calculate
								// the real cost later and change if necessary?
								registration.setCostOverride(rs
										.getFloat("totalcost"));
								registration.setConfirmationNumber(rs
										.getString("confirmationnumber"));
								registration.setStatus(rs.getInt("status"));
								registration.setPaymentMethodId(rs
										.getInt("paymentmethodid"));
								registration.setCompanyType(rs
										.getString("companytype"));
								registration.setAllianceLevel(rs
										.getString("alliancelevel"));
								registration.setFabricator(rs
										.getString("sponsoringfabricator"));
								registration.setSalesRep(rs
										.getString("sponsoringsalesrep"));
								registration.setPaymentNote(rs
										.getString("paymentnochargereason"));
							}
							return registration;
						}
					});

			// Assign event
			Event event = eventDAO.retrieveByRegistrationId(registrationId);
			registration.setEvent(event);

			// Retrieve optionIds
			List<Integer> optionIds = retrieveRegOptions(registrationId);
			registration.setSelectedOptionIds(optionIds);

			// Retrieve questionAnswer
			Map<Integer, String> questionAnswers = retrieveQuestionAnswers(registrationId);
			registration.setQuestionAnswers(questionAnswers);

			// Retrieve paymentMethodDetails
			Map<Integer, String> paymentMethodDetailAnswers = retrievePaymentDetails(registrationId);
			registration
					.setPaymentMethodDetailAnswers(paymentMethodDetailAnswers);

		} catch (Exception e) {
			System.err.println("Error retrieving registration with ID "
					+ registrationId + ": " + e.getMessage());
			e.printStackTrace();
		}

		return registration;
	}

	public EventRegistration retrieveByEventIdAndUserId(int eventId, int userId) {
		int registrationId = 0;

		try {
			String sql = "select registrationid from lc_eventregistration where eventid = ? and userid = ? and active = 1";
			List<Object> params = new ArrayList<Object>();
			params.add(eventId);
			params.add(userId);
			registrationId = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<Integer>() {
						public Integer handleResultSet(ResultSet rs)
								throws Exception {
							if (rs.next()) {
								return new Integer(rs.getInt("registrationid"));
							} else {
								return new Integer(0);
							}
						}
					}).intValue();
		} catch (Exception e) {
			System.err.println("Error retrieving Registration ID for event "
					+ eventId + " and user " + userId + ": " + e.getMessage());
			e.printStackTrace();
		}

		if (registrationId > 0) {
			return retrieve(registrationId);
		} else {
			return null;
		}
	}

	public List<EventRegistration> retrieveByEventId(int eventId) {
		List<EventRegistration> registrations = new ArrayList<EventRegistration>();

		Event event = eventDAO.retrieve(eventId);

		try {
			String sql = "select registrationid, to_char(registrationdate, 'YYYY-MM-DD') registrationdate, registeringuserid, userid, companyid, firstname, lastname, companyname, address, city, region, postcode, phone, email, totalcost, confirmationnumber, status, paymentmethodid, companytype, alliancelevel, sponsoringfabricator, sponsoringsalesrep, paymentnochargereason, pointsupdated from lc_eventregistration where eventid = ? and active = 1 order by companyname, firstname, lastname";
			List<Object> params = new ArrayList<Object>();
			params.add(eventId);
			registrations = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<List<EventRegistration>>() {
						public List<EventRegistration> handleResultSet(
								ResultSet rs) throws Exception {
							List<EventRegistration> registrations = new ArrayList<EventRegistration>();
							while (rs.next()) {
								EventRegistration registration = new EventRegistration();
								registration.setRegistrationId(rs
										.getInt("registrationid"));
								registration.setRegistrationDate(rs
										.getString("registrationdate"));
								registration.setRegisteringUserId(rs
										.getInt("registeringuserid"));
								registration.setUserId(rs.getInt("userid"));
								registration.setCompanyId(rs
										.getInt("companyid"));
								registration.setFirstName(rs
										.getString("firstname"));
								registration.setLastName(rs
										.getString("lastname"));
								registration.setCompanyName(rs
										.getString("companyname"));
								registration
										.setAddress(rs.getString("address"));
								registration.setCity(rs.getString("city"));
								registration.setRegion(rs.getString("region"));
								registration.setPostCode(rs
										.getString("postcode"));
								registration.setPhone(rs.getString("phone"));
								registration.setEmail(rs.getString("email"));
								// Set Cost Override as total cost? Calculate
								// the real cost later and change if necessary?
								registration.setCostOverride(rs
										.getFloat("totalcost"));
								registration.setConfirmationNumber(rs
										.getString("confirmationnumber"));
								registration.setStatus(rs.getInt("status"));
								registration.setPaymentMethodId(rs
										.getInt("paymentmethodid"));
								registration.setCompanyType(rs
										.getString("companytype"));
								registration.setAllianceLevel(rs
										.getString("alliancelevel"));
								registration.setFabricator(rs
										.getString("sponsoringfabricator"));
								registration.setSalesRep(rs
										.getString("sponsoringsalesrep"));
								registration.setPaymentNote(rs
										.getString("paymentnochargereason"));
								registration.setPointsUpdated(rs
										.getInt("pointsupdated")==1?true:false);
								registrations.add(registration);
							}
							return registrations;
						}
					});

			// Assign event
			for (EventRegistration registration : registrations) {
				registration.setEvent(event);
			}

			List<Integer> registrationIds = new ArrayList<Integer>();
			for (EventRegistration registration : registrations) {
				registrationIds.add(new Integer(registration
						.getRegistrationId()));
			}

			Map<Integer, List<Integer>> optionIdsMap = retrieveRegOptionsByRegistrationIds(registrationIds);
			Map<Integer, Map<Integer, String>> questionAnswersMap = retrieveQuestionAnswersByRegistrationIds(registrationIds);
			Map<Integer, Map<Integer, String>> paymentMethodDetailAnswersMap = retrievePaymentDetailsByRegistrationIds(registrationIds);

			for (EventRegistration registration : registrations) {
				Integer registrationId = new Integer(registration
						.getRegistrationId());

				// Retrieve optionIds
				List<Integer> optionIds = optionIdsMap.get(registrationId);
				if (optionIds == null) {
					optionIds = new ArrayList<Integer>();
				}
				registration.setSelectedOptionIds(optionIds);

				// Retrieve questionAnswer
				Map<Integer, String> questionAnswers = questionAnswersMap
						.get(registrationId);
				if (questionAnswers == null) {
					questionAnswers = new HashMap<Integer, String>();
				}
				registration.setQuestionAnswers(questionAnswers);

				// Retrieve paymentMethodDetails
				Map<Integer, String> paymentMethodDetailAnswers = paymentMethodDetailAnswersMap
						.get(registrationId);
				if (paymentMethodDetailAnswers == null) {
					paymentMethodDetailAnswers = new HashMap<Integer, String>();
				}
				registration
						.setPaymentMethodDetailAnswers(paymentMethodDetailAnswers);
			}
		} catch (Exception e) {
			System.err
					.println("Error retrieving Event Registrations for Event "
							+ eventId + ": " + e.getMessage());
			e.printStackTrace();
		}

		return registrations;
	}

	public boolean exists(int eventId, int userId) {
		boolean exists = false;

		try {
			String sql = "select registrationid from lc_eventregistration where eventid = ? and userid = ? and active = 1";
			List<Object> params = new ArrayList<Object>();
			params.add(eventId);
			params.add(userId);
			exists = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<Boolean>() {
						public Boolean handleResultSet(ResultSet rs)
								throws Exception {
							return new Boolean(rs.next());
						}
					}).booleanValue();
		} catch (Exception e) {
			System.err
					.println("Error determining if a registration exists for user "
							+ userId
							+ " and event "
							+ eventId
							+ ": "
							+ e.getMessage());
			e.printStackTrace();
		}

		return exists;
	}

	public List<Integer> existsList(List<Integer> eventIds, int userId) {
		List<Integer> exists = new ArrayList<Integer>();

		try {
			String sql = "select eventid from lc_eventregistration where userid = ? and eventid in (-1";
			for (int i = 0; i < eventIds.size(); i++) {
				sql += ", ?";
			}
			sql += ") and active = 1";
			List<Object> params = new ArrayList<Object>();
			params.add(userId);
			for (Integer eventId : eventIds) {
				params.add(eventId);
			}
			exists = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<List<Integer>>() {
						public List<Integer> handleResultSet(ResultSet rs)
								throws Exception {
							List<Integer> exists = new ArrayList<Integer>();
							while (rs.next()) {
								int eventId = rs.getInt("eventid");
								exists.add(new Integer(eventId));
							}
							return exists;
						}
					});
		} catch (Exception e) {
			System.err
					.println("Error determining if registrations exist for user "
							+ userId
							+ " for a set of events: "
							+ e.getMessage());
			e.printStackTrace();
		}

		return exists;
	}

	public EventRegistration save(EventRegistration registration) {
		if (registration.getRegistrationId() == 0) {
			return insert(registration);
		} else {
			return update(registration);
		}
	}

	private EventRegistration insert(EventRegistration registration) {
		int registrationId = generateRegistrationId();

		if (registrationId != 0) {
			try {
				String sql = "insert into lc_eventregistration (registrationid, eventid, registeringuserid, registrationdate, userid, companyid, firstname, lastname, companyname, address, city, region, postcode, phone, email, companytype, alliancelevel, sponsoringfabricator, sponsoringsalesrep, paymentmethodid, paymentnochargereason, totalcost, confirmationnumber, status, active) values (?, ?, ?, sysdate, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1)";
				List<Object> params = new ArrayList<Object>();
				params.add(registrationId);
				params.add(registration.getEvent().getEventId());
				params.add(registration.getRegisteringUserId());
				params.add(registration.getUserId());
				params.add(registration.getCompanyId());
				params.add(registration.getFirstName());
				params.add(registration.getLastName());
				params.add(registration.getCompanyName());
				params.add(registration.getAddress());
				params.add(registration.getCity());
				params.add(registration.getRegion());
				params.add(registration.getPostCode());
				params.add(registration.getPhone());
				params.add(registration.getEmail());
				params.add(registration.getCompanyType());
				params.add(registration.getAllianceLevel());
				params.add(registration.getFabricator());
				params.add(registration.getSalesRep());
				params.add(registration.getPaymentMethodId());
				params.add(registration.getPaymentNote());
				params.add(registration.getTotalCost().getCost());
				params.add(registration.getConfirmationNumber());
				params.add(registration.getStatus());
				DataUtils.execute(sql, params, LC_DATA_SOURCE);

				saveRegOptions(registration.getUniqueSelectedOptionIds(),
						registrationId);
				if (registration.getPaymentMethodId() != EventPaymentMethod.PAYMENT_ID_CC) {
					savePaymentDetails(registration
							.getPaymentMethodDetailAnswers(), registrationId);
				}
				saveQuestionAnswers(registration.getQuestionAnswers(),
						registrationId);
			} catch (Exception e) {
				System.err.println("Error inserting registration for user "
						+ registration.getUserId() + " and event "
						+ registration.getEvent().getTitle() + ": "
						+ e.getMessage());
				e.printStackTrace();
			}
		}

		return retrieve(registrationId);
	}

	private int generateRegistrationId() {
		int registrationId = 0;
		try {
			String sql = "select lc_eventregistration_seq.nextval id from dual";
			List<Object> params = new ArrayList<Object>();

			Integer id = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<Integer>() {
						public Integer handleResultSet(ResultSet rs)
								throws Exception {
							if (rs.next()) {
								return new Integer(rs.getInt("id"));
							} else {
								return new Integer(0);
							}
						}
					});
			registrationId = id.intValue();
		} catch (Exception e) {
			System.out.println("Error in generating Registration ID: " + e);
			e.printStackTrace();
		}
		return registrationId;
	}

	private EventRegistration update(EventRegistration registration) {
		try {
			String sql = "update lc_eventregistration set eventid=?, registeringuserid=?, userid=?, companyid=?, firstname=?, lastname=?, companyname=?, address=?, city=?, region=?, postcode=?, phone=?, email=?, companytype=?, alliancelevel=?, sponsoringfabricator=?, sponsoringsalesrep=?, paymentmethodid=?, paymentnochargereason=?, totalcost=?, confirmationnumber=?, status=? where registrationid = ?";
			List<Object> params = new ArrayList<Object>();
			params.add(registration.getEvent().getEventId());
			params.add(registration.getRegisteringUserId());
			params.add(registration.getUserId());
			params.add(registration.getCompanyId());
			params.add(registration.getFirstName());
			params.add(registration.getLastName());
			params.add(registration.getCompanyName());
			params.add(registration.getAddress());
			params.add(registration.getCity());
			params.add(registration.getRegion());
			params.add(registration.getPostCode());
			params.add(registration.getPhone());
			params.add(registration.getEmail());
			params.add(registration.getCompanyType());
			params.add(registration.getAllianceLevel());
			params.add(registration.getFabricator());
			params.add(registration.getSalesRep());
			params.add(registration.getPaymentMethodId());
			params.add(registration.getPaymentNote());
			params.add(registration.getTotalCost().getCost());
			params.add(registration.getConfirmationNumber());
			params.add(registration.getStatus());
			params.add(registration.getRegistrationId());
			DataUtils.execute(sql, params, LC_DATA_SOURCE);

			saveRegOptions(registration.getUniqueSelectedOptionIds(),
					registration.getRegistrationId());
			if (registration.getPaymentMethodId() != EventPaymentMethod.PAYMENT_ID_CC) {
				savePaymentDetails(
						registration.getPaymentMethodDetailAnswers(),
						registration.getRegistrationId());
			}
			saveQuestionAnswers(registration.getQuestionAnswers(), registration
					.getRegistrationId());
		} catch (Exception e) {
			System.err.println("Error updating registration "
					+ registration.getRegistrationId() + ": " + e.getMessage());
			e.printStackTrace();
		}
		return retrieve(registration.getRegistrationId());
	}

	public void delete(int registrationId) {
		try {
			String sql = "update lc_eventregistration set active = 0 where registrationid = ?";
			List<Object> params = new ArrayList<Object>();
			params.add(registrationId);
			DataUtils.execute(sql, params, LC_DATA_SOURCE);
		} catch (Exception e) {
			System.err.println("Error deleting EventRegistration with ID "
					+ registrationId + ": " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void updatePointStatus(List<Integer> registrationIdList) {
		try {
			String sql = "update lc_eventregistration set pointsupdated = 1 where registrationid in ( ";
			sql += createParamPlaceholderString(registrationIdList);
			sql += " )";
			List<Object> params = new ArrayList<Object>();
			for (Integer registrationId : registrationIdList) {
				params.add(registrationId);
			}
			DataUtils.execute(sql, params, LC_DATA_SOURCE);
		} catch (Exception e) {
			System.err.println("Error updating point status in EventRegistration with Ids "
					+ registrationIdList + ": " + e.getMessage());
			e.printStackTrace();
		}
	}

	private List<Integer> retrieveRegOptions(int registrationId) {
		List<Integer> optionIds = new ArrayList<Integer>();

		try {
			String sql = "select eventoptionid from lc_eventregoption where registrationid = ?";
			List<Object> params = new ArrayList<Object>();
			params.add(registrationId);
			optionIds = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<List<Integer>>() {
						public List<Integer> handleResultSet(ResultSet rs)
								throws Exception {
							List<Integer> optionIds = new ArrayList<Integer>();
							while (rs.next()) {
								Integer optionId = new Integer(rs
										.getInt("eventoptionid"));
								optionIds.add(optionId);
							}
							return optionIds;
						}
					});
		} catch (Exception e) {
			System.err
					.println("Error retrieving Reg Options for Event Registration "
							+ registrationId + ": " + e.getMessage());
			e.printStackTrace();
		}

		return optionIds;
	}

	private Map<Integer, List<Integer>> retrieveRegOptionsByRegistrationIds(
			List<Integer> registrationIds) {
		Map<Integer, List<Integer>> optionIdsMap = new HashMap<Integer, List<Integer>>();

		try {
			String sql = "select registrationid, eventoptionid from lc_eventregoption where registrationid in (-1";
			for (int i = 0; i < registrationIds.size(); i++) {
				sql += ", ?";
			}
			sql += ")";
			List<Object> params = new ArrayList<Object>();
			for (Integer registrationId : registrationIds) {
				params.add(registrationId);
			}
			optionIdsMap = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<Map<Integer, List<Integer>>>() {
						public Map<Integer, List<Integer>> handleResultSet(
								ResultSet rs) throws Exception {
							Map<Integer, List<Integer>> optionIdsMap = new HashMap<Integer, List<Integer>>();
							while (rs.next()) {
								Integer optionId = new Integer(rs
										.getInt("eventoptionid"));
								Integer registrationId = new Integer(rs
										.getInt("registrationid"));
								List<Integer> optionIds = optionIdsMap
										.get(registrationId);
								if (optionIds == null) {
									optionIds = new ArrayList<Integer>();
									optionIdsMap.put(registrationId, optionIds);
								}
								optionIds.add(optionId);
							}
							return optionIdsMap;
						}
					});
		} catch (Exception e) {
			System.err
					.println("Error retrieving Reg Options for list of registration IDs: "
							+ e.getMessage());
			e.printStackTrace();
		}

		return optionIdsMap;
	}

	private void saveRegOptions(List<Integer> optionIds, int registrationId) {
		// Delete Existing
		try {
			String sql = "delete from lc_eventregoption where registrationid = ?";
			List<Object> params = new ArrayList<Object>();
			params.add(registrationId);
			DataUtils.execute(sql, params, LC_DATA_SOURCE);
		} catch (Exception e) {
			System.err.println("Error deleting RegOptions from registration "
					+ registrationId + ": " + e.getMessage());
			e.printStackTrace();
		}

		// Add new
		for (Integer optionId : optionIds) {
			try {
				String sql = "insert into lc_eventregoption (registrationid, eventoptionid) values (?, ?)";
				List<Object> params = new ArrayList<Object>();
				params.add(registrationId);
				params.add(optionId);
				DataUtils.execute(sql, params, LC_DATA_SOURCE);
			} catch (Exception e) {
				System.err.println("Error adding Selected Option \"" + optionId
						+ " for registration " + registrationId + ": "
						+ e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private Map<Integer, String> retrieveQuestionAnswers(int registrationId) {
		Map<Integer, String> questionAnswers = new HashMap<Integer, String>();

		try {
			String sql = "select questionid, answertext from lc_eventregquestionanswer where registrationid = ?";
			List<Object> params = new ArrayList<Object>();
			params.add(registrationId);
			questionAnswers = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<Map<Integer, String>>() {
						public Map<Integer, String> handleResultSet(ResultSet rs)
								throws Exception {
							Map<Integer, String> questionAnswers = new HashMap<Integer, String>();

							while (rs.next()) {
								Integer questionId = new Integer(rs
										.getInt("questionid"));
								String answerText = rs.getString("answertext");
								questionAnswers.put(questionId, answerText);
							}

							return questionAnswers;
						}
					});
		} catch (Exception e) {
			System.err
					.println("Error retrieve Question Answers for Event Registration "
							+ registrationId + ": " + e.getMessage());
			e.printStackTrace();
		}

		return questionAnswers;
	}

	private Map<Integer, Map<Integer, String>> retrieveQuestionAnswersByRegistrationIds(
			List<Integer> registrationIds) {
		Map<Integer, Map<Integer, String>> questionAnswersMap = new HashMap<Integer, Map<Integer, String>>();

		try {
			String sql = "select registrationid, questionid, answertext from lc_eventregquestionanswer where registrationid in (-1";
			for (int i = 0; i < registrationIds.size(); i++) {
				sql += ", ?";
			}
			sql += ")";
			List<Object> params = new ArrayList<Object>();
			for (Integer registrationId : registrationIds) {
				params.add(registrationId);
			}
			questionAnswersMap = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<Map<Integer, Map<Integer, String>>>() {
						public Map<Integer, Map<Integer, String>> handleResultSet(
								ResultSet rs) throws Exception {
							Map<Integer, Map<Integer, String>> questionAnswersMap = new HashMap<Integer, Map<Integer, String>>();

							while (rs.next()) {
								Integer questionId = new Integer(rs
										.getInt("questionid"));
								String answerText = rs.getString("answertext");
								Integer registrationId = new Integer(rs
										.getInt("registrationid"));
								Map<Integer, String> questionAnswers = questionAnswersMap
										.get(registrationId);
								if (questionAnswers == null) {
									questionAnswers = new HashMap<Integer, String>();
									questionAnswersMap.put(registrationId,
											questionAnswers);
								}
								questionAnswers.put(questionId, answerText);
							}

							return questionAnswersMap;
						}
					});
		} catch (Exception e) {
			System.err
					.println("Error retrieve Question Answers for list of registration Ids: "
							+ e.getMessage());
			e.printStackTrace();
		}

		return questionAnswersMap;
	}

	private void saveQuestionAnswers(Map<Integer, String> questionAnswers,
			int registrationId) {
		// Delete Existing
		try {
			String sql = "delete from lc_eventregquestionanswer where registrationid = ?";
			List<Object> params = new ArrayList<Object>();
			params.add(registrationId);
			DataUtils.execute(sql, params, LC_DATA_SOURCE);
		} catch (Exception e) {
			System.err
					.println("Error deleting QuestionAnswers from registration "
							+ registrationId + ": " + e.getMessage());
			e.printStackTrace();
		}

		// Add new
		for (Integer questionId : questionAnswers.keySet()) {
			String answerText = questionAnswers.get(questionId);
			if (answerText != null && !"".equals(answerText)) {
				try {
					String sql = "insert into lc_eventregquestionanswer (registrationid, questionid, answertext) values (?, ?, ?)";
					List<Object> params = new ArrayList<Object>();
					params.add(registrationId);
					params.add(questionId.intValue());
					params.add(answerText);
					DataUtils.execute(sql, params, LC_DATA_SOURCE);
				} catch (Exception e) {
					System.err.println("Error adding Answer \"" + answerText
							+ "\" to Question ID " + questionId
							+ " for registration " + registrationId + ": "
							+ e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	private Map<Integer, String> retrievePaymentDetails(int registrationId) {
		Map<Integer, String> paymentMethodDetailIds = new HashMap<Integer, String>();

		try {
			String sql = "select paymentmethoddetailid, answertext from lc_eventregpaymentdetailanswer where registrationid = ?";
			List<Object> params = new ArrayList<Object>();
			params.add(registrationId);
			paymentMethodDetailIds = DataUtils.query(sql, params,
					LC_DATA_SOURCE,
					new ResultSetHandler<Map<Integer, String>>() {
						public Map<Integer, String> handleResultSet(ResultSet rs)
								throws Exception {
							Map<Integer, String> paymentMethodDetailIds = new HashMap<Integer, String>();
							while (rs.next()) {
								Integer detailId = new Integer(rs
										.getInt("paymentmethoddetailid"));
								String answerText = rs.getString("answertext");
								paymentMethodDetailIds
										.put(detailId, answerText);
							}
							return paymentMethodDetailIds;
						}
					});
		} catch (Exception e) {
			System.err
					.println("Error retrieving Payment Details for Event Registration "
							+ registrationId + ": " + e.getMessage());
			e.printStackTrace();
		}

		return paymentMethodDetailIds;
	}

	private Map<Integer, Map<Integer, String>> retrievePaymentDetailsByRegistrationIds(
			List<Integer> registrationIds) {
		Map<Integer, Map<Integer, String>> paymentMethodDetailIdsMap = new HashMap<Integer, Map<Integer, String>>();

		try {
			String sql = "select registrationid, paymentmethoddetailid, answertext from lc_eventregpaymentdetailanswer where registrationid in (-1";
			for (int i = 0; i < registrationIds.size(); i++) {
				sql += ", ?";
			}
			sql += ")";
			List<Object> params = new ArrayList<Object>();
			for (Integer registrationId : registrationIds) {
				params.add(registrationId);
			}
			paymentMethodDetailIdsMap = DataUtils.query(sql, params,
					LC_DATA_SOURCE,
					new ResultSetHandler<Map<Integer, Map<Integer, String>>>() {
						public Map<Integer, Map<Integer, String>> handleResultSet(
								ResultSet rs) throws Exception {
							Map<Integer, Map<Integer, String>> paymentMethodDetailIdsMap = new HashMap<Integer, Map<Integer, String>>();
							while (rs.next()) {
								Integer detailId = new Integer(rs
										.getInt("paymentmethoddetailid"));
								String answerText = rs.getString("answertext");

								Integer registrationId = new Integer(rs
										.getInt("registrationid"));
								Map<Integer, String> paymentMethodDetailIds = paymentMethodDetailIdsMap
										.get(registrationId);
								if (paymentMethodDetailIds == null) {
									paymentMethodDetailIds = new HashMap<Integer, String>();
									paymentMethodDetailIdsMap.put(
											registrationId,
											paymentMethodDetailIds);
								}
								paymentMethodDetailIds
										.put(detailId, answerText);
							}
							return paymentMethodDetailIdsMap;
						}
					});
		} catch (Exception e) {
			System.err
					.println("Error retrieving Payment Details for list of registration IDs: "
							+ e.getMessage());
			e.printStackTrace();
		}

		return paymentMethodDetailIdsMap;
	}

	private void savePaymentDetails(
			Map<Integer, String> paymentMethodDetailAnswers, int registrationId) {
		// Delete Existing
		try {
			String sql = "delete from lc_eventregpaymentdetailanswer where registrationid = ?";
			List<Object> params = new ArrayList<Object>();
			params.add(registrationId);
			DataUtils.execute(sql, params, LC_DATA_SOURCE);
		} catch (Exception e) {
			System.err
					.println("Error deleting PaymentDetails from registration "
							+ registrationId + ": " + e.getMessage());
			e.printStackTrace();
		}

		// Add new
		for (Integer detailId : paymentMethodDetailAnswers.keySet()) {
			String answerText = paymentMethodDetailAnswers.get(detailId);
			if (answerText != null && !"".equals(answerText)) {
				try {
					String sql = "insert into lc_eventregpaymentdetailanswer (registrationid, paymentmethoddetailid, answertext) values (?, ?, ?)";
					List<Object> params = new ArrayList<Object>();
					params.add(registrationId);
					params.add(detailId.intValue());
					params.add(answerText);
					DataUtils.execute(sql, params, LC_DATA_SOURCE);
				} catch (Exception e) {
					System.err.println("Error adding Answer \"" + answerText
							+ "\" to Payment Method Detail ID " + detailId
							+ " for registration " + registrationId + ": "
							+ e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}
	
	private String createParamPlaceholderString(List<Integer> list) {
		if (list == null || list.size() == 0) {
			return "-1";
		} else {
			String paramString = "";
			for (int i = 0; i < list.size(); i++) {
				paramString += "?";
				if (i < (list.size() - 1)) {
					paramString += ", ";
				}
			}
			return paramString;
		}
	}
}

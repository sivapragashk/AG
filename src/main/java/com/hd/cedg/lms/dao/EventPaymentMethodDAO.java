package com.hd.cedg.lms.dao;

import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hd.cedg.lms.model.Event;
import com.hd.cedg.lms.model.EventPaymentMethod;
import com.hd.cedg.lms.model.EventPaymentMethodDetail;
import com.hd.mis.data.DataUtils;
import com.hd.mis.data.ResultSetHandler;

public class EventPaymentMethodDAO extends BaseLmsDAO {

	public List<EventPaymentMethod> retrieveListByEventId(int eventId) {
		List<EventPaymentMethod> payments = new ArrayList<EventPaymentMethod>();

		try {
			String sql = "select paymentmethodid, name, adminonly from lc_eventpaymentmethod where eventid = ? and active = 1 order by sortorder";
			List<Object> params = new ArrayList<Object>();
			params.add(eventId);
			payments = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<List<EventPaymentMethod>>() {
						public List<EventPaymentMethod> handleResultSet(
								ResultSet rs) throws Exception {
							List<EventPaymentMethod> payments = new ArrayList<EventPaymentMethod>();

							while (rs.next()) {
								EventPaymentMethod payment = new EventPaymentMethod();

								payment.setPaymentMethodId(rs
										.getInt("paymentmethodid"));
								payment.setName(rs.getString("name"));
								payment
										.setAdminOnly(rs.getInt("adminonly") != 0);

								payments.add(payment);
							}

							return payments;
						}
					});

			Map<Integer, List<EventPaymentMethodDetail>> detailsMap = retrieveDetailsByEventId(eventId);
			for (EventPaymentMethod payment : payments) {
				List<EventPaymentMethodDetail> details = detailsMap.get(payment
						.getPaymentMethodId());
				if (details != null) {
					payment.setPaymentMethodDetails(details);
				}
			}

		} catch (Exception e) {
			System.err
					.println("Error retrieving Event Payment Methods for event "
							+ eventId + ": " + e.getMessage());
			e.printStackTrace();
		}

		return payments;
	}

	public Map<Integer, List<EventPaymentMethod>> retrieveListByList(
			List<Integer> eventIds) {
		Map<Integer, List<EventPaymentMethod>> paymentsMap = new HashMap<Integer, List<EventPaymentMethod>>();

		try {
			String sql = "select eventid, paymentmethodid, name, adminonly from lc_eventpaymentmethod where eventid in (-1";
			for (int i = 0; i < eventIds.size(); i++) {
				sql += ", ?";
			}
			sql += ") and active = 1 order by sortorder";
			List<Object> params = new ArrayList<Object>();
			for (Integer eventId : eventIds) {
				params.add(eventId);
			}
			paymentsMap = DataUtils
					.query(
							sql,
							params,
							LC_DATA_SOURCE,
							new ResultSetHandler<Map<Integer, List<EventPaymentMethod>>>() {
								public Map<Integer, List<EventPaymentMethod>> handleResultSet(
										ResultSet rs) throws Exception {
									Map<Integer, List<EventPaymentMethod>> paymentsMap = new HashMap<Integer, List<EventPaymentMethod>>();

									while (rs.next()) {
										EventPaymentMethod payment = new EventPaymentMethod();

										payment.setPaymentMethodId(rs
												.getInt("paymentmethodid"));
										payment.setName(rs.getString("name"));
										payment.setAdminOnly(rs
												.getInt("adminonly") != 0);

										Integer eventId = new Integer(rs
												.getInt("eventid"));
										List<EventPaymentMethod> payments = paymentsMap
												.get(eventId);
										if (payments == null) {
											payments = new ArrayList<EventPaymentMethod>();
											paymentsMap.put(eventId, payments);
										}
										payments.add(payment);
									}

									return paymentsMap;
								}
							});

			Map<Integer, List<EventPaymentMethodDetail>> detailsMap = retrieveDetailsByEventIds(eventIds);
			for (Integer eventId : paymentsMap.keySet()) {
				List<EventPaymentMethod> payments = paymentsMap.get(eventId);
				for (EventPaymentMethod payment : payments) {
					List<EventPaymentMethodDetail> details = detailsMap
							.get(payment.getPaymentMethodId());
					if (details != null) {
						payment.setPaymentMethodDetails(details);
					}
				}
			}

		} catch (Exception e) {
			System.err
					.println("Error retrieving Event Payment Methods for list of event IDs: "
							+ e.getMessage());
			e.printStackTrace();
		}

		return paymentsMap;
	}

	public void saveByEvent(Event event) {
		for (int paymentIndex = 0; paymentIndex < event.getPaymentMethods()
				.size(); paymentIndex++) {
			EventPaymentMethod payment = event.getPaymentMethods().get(
					paymentIndex);
			if (payment.getPaymentMethodId() > 0) {
				// Update
				try {
					String sql = "update lc_eventpaymentmethod set name=?, adminonly=?, sortorder=? where paymentmethodid = ?";
					List<Object> params = new ArrayList<Object>();
					params.add(payment.getName());
					params.add(payment.isAdminOnly() ? 1 : 0);
					params.add(paymentIndex);
					params.add(payment.getPaymentMethodId());
					DataUtils.execute(sql, params, LC_DATA_SOURCE);

					// Save details
					savePaymentDetails(payment);
				} catch (Exception e) {
					System.err.println("Error updating Payment Method "
							+ payment.getPaymentMethodId() + ": "
							+ e.getMessage());
					e.printStackTrace();
				}
			} else {
				// Generate paymentId
				int paymentId = generatePaymentId();

				if (paymentId > 0) {
					payment.setPaymentMethodId(paymentId);

					// Insert
					try {
						String sql = "insert into lc_eventpaymentmethod (paymentmethodid, eventid, name, adminonly, sortorder, active) values (?, ?, ?, ?, ?, 1)";
						List<Object> params = new ArrayList<Object>();
						params.add(payment.getPaymentMethodId());
						params.add(event.getEventId());
						params.add(payment.getName());
						params.add(payment.isAdminOnly() ? 1 : 0);
						params.add(paymentIndex);
						DataUtils.execute(sql, params, LC_DATA_SOURCE);

						// Save details
						savePaymentDetails(payment);
					} catch (Exception e) {
						System.err.println("Error inserting Event Question: "
								+ e.getMessage());
						e.printStackTrace();
					}
				}
			}
		}
	}

	private Map<Integer, List<EventPaymentMethodDetail>> retrieveDetailsByEventId(
			int eventId) {
		List<Integer> eventIds = new ArrayList<Integer>();
		eventIds.add(new Integer(eventId));
		return retrieveDetailsByEventIds(eventIds);
	}

	private Map<Integer, List<EventPaymentMethodDetail>> retrieveDetailsByEventIds(
			List<Integer> eventIds) {
		Map<Integer, List<EventPaymentMethodDetail>> detailsMap = new HashMap<Integer, List<EventPaymentMethodDetail>>();
		try {
			String sql = "select det.paymentmethoddetailid, det.paymentmethodid, det.name, det.required from lc_eventpaymentmethoddetail det inner join lc_eventpaymentmethod pay on pay.paymentmethodid = det.paymentmethodid and pay.active = 1 and pay.eventid in (-1";
			for (int i = 0; i < eventIds.size(); i++) {
				sql += ", ?";
			}
			sql += ") where det.active = 1 order by pay.sortorder, det.sortorder";
			List<Object> params = new ArrayList<Object>();
			for (Integer eventId : eventIds) {
				params.add(eventId);
			}
			detailsMap = DataUtils
					.query(
							sql,
							params,
							LC_DATA_SOURCE,
							new ResultSetHandler<Map<Integer, List<EventPaymentMethodDetail>>>() {
								public Map<Integer, List<EventPaymentMethodDetail>> handleResultSet(
										ResultSet rs) throws Exception {
									Map<Integer, List<EventPaymentMethodDetail>> detailsMap = new HashMap<Integer, List<EventPaymentMethodDetail>>();

									while (rs.next()) {
										int paymentMethodId = rs
												.getInt("paymentmethodid");
										List<EventPaymentMethodDetail> details = detailsMap
												.get(new Integer(
														paymentMethodId));
										if (details == null) {
											details = new ArrayList<EventPaymentMethodDetail>();
											detailsMap.put(new Integer(
													paymentMethodId), details);
										}

										EventPaymentMethodDetail detail = new EventPaymentMethodDetail();
										detail
												.setPaymentDetailId(rs
														.getInt("paymentmethoddetailid"));
										detail.setName(rs.getString("name"));
										detail.setRequired(rs
												.getInt("required") != 0);
										details.add(detail);
									}

									return detailsMap;
								}
							});
		} catch (Exception e) {
			System.err
					.println("Error retrieving Map of Payment Method Details for list of event IDs: "
							+ e.getMessage());
			e.printStackTrace();
		}

		return detailsMap;
	}

	private void savePaymentDetails(EventPaymentMethod payment) {
		try {
			// Deactivate all
			String deleteSql = "update lc_eventpaymentmethoddetail set active = 0, sortorder = -1 where paymentmethodid = ?";
			List<Object> deleteParams = new ArrayList<Object>();
			deleteParams.add(payment.getPaymentMethodId());
			DataUtils.execute(deleteSql, deleteParams, LC_DATA_SOURCE);

			for (int i = 0; i < payment.getPaymentMethodDetails().size(); i++) {
				EventPaymentMethodDetail detail = payment
						.getPaymentMethodDetails().get(i);
				if (detail.getPaymentDetailId() > 0) {
					// Update (including reactivating)
					String updateSql = "update lc_eventpaymentmethoddetail set name=?, required=?, sortorder=?, active=1 where paymentmethoddetailid = ?";
					List<Object> updateParams = new ArrayList<Object>();
					updateParams.add(detail.getName());
					updateParams.add(detail.isRequired() ? 1 : 0);
					updateParams.add(i);
					updateParams.add(detail.getPaymentDetailId());
					DataUtils.execute(updateSql, updateParams, LC_DATA_SOURCE);
				} else {
					// Insert
					int detailId = generatePaymentDetailId();
					detail.setPaymentDetailId(detailId);

					String insertSql = "insert into lc_eventpaymentmethoddetail (paymentmethoddetailid, paymentmethodid, name, required, sortorder, active) values (?, ?, ?, ?, ?, 1)";
					List<Object> insertParams = new ArrayList<Object>();
					insertParams.add(detail.getPaymentDetailId());
					insertParams.add(payment.getPaymentMethodId());
					insertParams.add(detail.getName());
					insertParams.add(detail.isRequired() ? 1 : 0);
					insertParams.add(i);
					DataUtils.execute(insertSql, insertParams, LC_DATA_SOURCE);
				}
			}
		} catch (Exception e) {
			System.err
					.println("Error saveing Event Payment Details for Payment Method "
							+ payment.getPaymentMethodId()
							+ ": "
							+ e.getMessage());
			e.printStackTrace();
		}
	}

	private int generatePaymentId() {
		int paymentId = 0;
		try {
			String sql = "select lc_eventpaymentmethod_seq.nextval paymentid from dual";
			List<Object> params = new ArrayList<Object>();
			Integer id = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<Integer>() {
						public Integer handleResultSet(ResultSet rs)
								throws Exception {
							if (rs.next()) {
								return new Integer(rs.getInt("paymentid"));
							}
							return null;
						}
					});
			if (id != null) {
				paymentId = id.intValue();
			}
		} catch (Exception e) {
			System.err.println("Error generating a new Payment ID: "
					+ e.getMessage());
			e.printStackTrace();
		}
		return paymentId;
	}

	private int generatePaymentDetailId() {
		int detailId = 0;
		try {
			String sql = "select lc_eventpaymentmethoddet_seq.nextval detailid from dual";
			List<Object> params = new ArrayList<Object>();
			Integer id = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<Integer>() {
						public Integer handleResultSet(ResultSet rs)
								throws Exception {
							if (rs.next()) {
								return new Integer(rs.getInt("detailid"));
							}
							return null;
						}
					});
			if (id != null) {
				detailId = id.intValue();
			}
		} catch (Exception e) {
			System.err.println("Error generating a new Payment Detail ID: "
					+ e.getMessage());
			e.printStackTrace();
		}
		return detailId;
	}
}

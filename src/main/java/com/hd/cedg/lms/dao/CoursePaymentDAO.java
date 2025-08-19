package com.hd.cedg.lms.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.hd.mis.data.DataUtils;
import com.hd.mis.data.ResultSetHandler;

public class CoursePaymentDAO extends BaseLmsDAO {

	public boolean isPaid(String learningId, int userId) {
		boolean isPaid = false;
		try {
			String sql = "select userid, learningid from lc_coursepayment where userid = ? and learningid = ?";
			List<Object> params = new ArrayList<Object>();
			params.add(userId);
			params.add(learningId);
			isPaid = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<Boolean>() {
						public Boolean handleResultSet(ResultSet rs)
								throws Exception {
							return rs.next();
						}
					}).booleanValue();
		} catch (Exception e) {
			System.err.println("Error checking payment for Course "
					+ learningId + " for user " + userId + ": "
					+ e.getMessage());
			e.printStackTrace();
		}
		return isPaid;
	}

	public void insertPaid(String learningId, int userId) {
		try {
			String sql = "insert into lc_coursepayment (userid, learningid) values (?, ?)";
			List<Object> params = new ArrayList<Object>();
			params.add(userId);
			params.add(learningId);
			DataUtils.execute(sql, params, LC_DATA_SOURCE);
		} catch (Exception e) {
			System.err.println("Error inserting payment for Course "
					+ learningId + " for user " + userId + ": "
					+ e.getMessage());
			e.printStackTrace();
		}
	}

}

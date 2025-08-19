package com.hd.cedg.lms.dao;

import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;

import com.hd.cedg.lms.model.EventOtherUser;
import com.hd.mis.data.DataUtils;
import com.hd.mis.data.ResultSetHandler;

public class EventOtherUserDAO extends BaseLmsDAO {

	public List<EventOtherUser> retrieveListByCompanyAndUserId(int companyId, int userId) {
		List<EventOtherUser> otherUsers = new ArrayList<EventOtherUser>();

		try {
			String sql = "SELECT tlc.iIndividualId userid, tlc.vchFirstName firstname, tlc.vchLastName lastname, tlc.vchPhoneNumber phone, tlc.vchEmailAddress email FROM compass.TheLearningCenterUserView tlc (nolock) WHERE tlc.iCompanyId = ? AND tlc.iIndividualId != ? AND tlc.iCompanyId != 0 AND tlc.individaulStatus = 'Active' AND tlc.companyType != 'Customers' ORDER BY tlc.vchFirstName, tlc.vchLastName, tlc.iIndividualId";
			List<Object> params = new ArrayList<Object>();
			params.add(companyId);
			params.add(userId);
			otherUsers = DataUtils.query(sql, params, ONYX_READ_DATA_SOURCE,
					new ResultSetHandler<List<EventOtherUser>>() {
						public List<EventOtherUser> handleResultSet(ResultSet rs)
								throws Exception {
							List<EventOtherUser> otherUsers = new ArrayList<EventOtherUser>();

							while (rs.next()) {
								EventOtherUser otherUser = new EventOtherUser();

								otherUser.setUserId(rs.getInt("userid"));
								otherUser.setFirstName(rs.getString("firstname"));
								otherUser.setLastName(rs.getString("lastname"));
								otherUser.setPhone(rs.getString("phone"));
								otherUser.setEmail(rs.getString("email"));

								otherUsers.add(otherUser);
							}

							return otherUsers;
						}
					});
		} catch (Exception e) {
			System.err.println("Error retrieving Event Other Users for user ID "
					+ userId + ": " + e.getMessage());
			e.printStackTrace();
		}

		return otherUsers;
	}
}

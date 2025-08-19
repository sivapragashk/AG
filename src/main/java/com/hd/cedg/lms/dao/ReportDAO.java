package com.hd.cedg.lms.dao;

import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hd.cedg.lms.model.LearningUser;
import com.hd.cedg.lms.model.ReportCompany;
import com.hd.cedg.lms.model.ReportUser;
import com.hd.mis.data.DataUtils;
import com.hd.mis.data.ResultSetHandler;

public class ReportDAO extends BaseLmsDAO {

	private static final String ONYX_REP_DATA_SOURCE = "jdbc/OnyxReadTLCDS";

	public List<ReportCompany> retrieveCompaniesForUser(LearningUser user) {
		return retrieveCompaniesForUser(user, 0, 0);
	}

	public List<ReportCompany> retrieveCompaniesForUser(LearningUser user,
			int companyId, int userId) {
		List<ReportCompany> companies = new ArrayList<ReportCompany>();

		// Retrieve the user's reporting access from Onyx
		Map<String, List<String>> permissionsMap = getManagerPermissions(user
				.getUserId());

		// Retrieve all users per those access levels
		try {
			String sql = "select u.userid, u.firstname, u.lastname, u.emailaddress, u.companyname, u.companytype, coalesce(u.alliancelevel, ' ') alliancelevel, coalesce(u.sponsoringfabricatorname, ' ') fabricator, coalesce(u.salesrepname, ' ') salesrep, u.address1, u.city, u.region, u.postcode, u.companyid from lc_user u where u.active = 1 and ? in (0, u.companyid) and ? in (0, u.userid) and ( u.companyid in (";
			sql += createParamPlaceholderString(permissionsMap
					.get("companyReview"));
			sql += ") or u.sponsoringfabricatorid in (";
			sql += createParamPlaceholderString(permissionsMap.get("fabReview"));
			sql += ") or ( u.sponsoringfabricatorid in (";
			sql += createParamPlaceholderString(permissionsMap
					.get("allianceReview"));
			sql += ") and u.alliancelevel in ('Priority', 'Showcase Priority', 'Gallery')) or ( u.companyid in (";
			sql += createParamPlaceholderString(permissionsMap
					.get("salesReview"));
			sql += ") and u.department = 'Sales') or (u.companyid in (";
			sql += createParamPlaceholderString(permissionsMap.get("csReview"));
			sql += ") and u.department = 'Customer Service')) order by u.companytype, u.alliancelevel, u.companyname, u.lastname, u.firstname";

			List<Object> params = new ArrayList<Object>();
			params.add(companyId);
			params.add(userId);
			for (String id : permissionsMap.get("companyReview")) {
				params.add(id);
			}
			for (String id : permissionsMap.get("fabReview")) {
				params.add(id);
			}
			for (String id : permissionsMap.get("allianceReview")) {
				params.add(id);
			}
			for (String id : permissionsMap.get("salesReview")) {
				params.add(id);
			}
			for (String id : permissionsMap.get("csReview")) {
				params.add(id);
			}

			companies = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<List<ReportCompany>>() {
						public List<ReportCompany> handleResultSet(ResultSet rs)
								throws Exception {
							Map<Integer, ReportCompany> companyMap = new HashMap<Integer, ReportCompany>();
							while (rs.next()) {
								int companyId = rs.getInt("companyid");
								ReportCompany company = companyMap
										.get(new Integer(companyId));
								if (company == null) {
									company = new ReportCompany();
									company.setCompanyId(companyId);
									company.setCompanyName(rs
											.getString("companyname"));
									company.setCompanyType(rs
											.getString("companytype"));
									company.setAllianceProgram(rs
											.getString("alliancelevel"));
									company.setFabricator(rs
											.getString("fabricator"));
									company.setSalesRep(rs
											.getString("salesrep"));
									company.setAddress(rs.getString("address1"));
									company.setCity(rs.getString("city"));
									company.setRegion(rs.getString("region"));
									company.setPostCode(rs.getString("postcode"));
									companyMap.put(new Integer(companyId),
											company);
								}
								ReportUser user = new ReportUser();
								user.setUserId(rs.getInt("userid"));
								user.setFirstName(rs.getString("firstname"));
								user.setLastName(rs.getString("lastname"));
								user.setEmail(rs.getString("emailaddress"));
								company.getUsers().add(user);
							}
							List<ReportCompany> companies = new ArrayList<ReportCompany>(
									companyMap.values());
							Collections.sort(companies);
							return companies;
						}
					});
		} catch (Exception e) {
			System.out
					.println("Error retrieving List of ReportCompany objects for user "
							+ user.getUserId() + ": " + e.getMessage());
			e.printStackTrace();
		}

		return companies;
	}

	/**
	 * 
	 * @param userId
	 * @return A map of Permission Name -> [ID, Detail] (Note that detail is
	 *         only currently used for deptReview
	 */
	private Map<String, List<String>> getManagerPermissions(int userId) {
		Map<String, List<String>> permissionsMap = new HashMap<String, List<String>>();

		try {
			String sql = "DECLARE @indId int SELECT @indId = ? SELECT 'companyReview' permissionName, iOwnerId id FROM Contact WHERE iContactId = @indId AND iContactTypeId IN (99, 94, 118, 114) AND tiRecordStatus = 1 UNION SELECT 'fabReview' permissionName, iOwnerId id FROM Contact AS Con WHERE Con.iContactId = @indId AND Con.iContactTypeId IN (99, 118, 114, 115, 116, 117) AND Con.tiRecordStatus = 1 AND EXISTS ( SELECT * FROM Contact AS FabCon WHERE FabCon.iContactId = Con.iOwnerId AND iContactTypeId IN (67) AND tiRecordStatus = 1) UNION SELECT 'csReview' permissionName, iOwnerId id FROM Contact WHERE iContactId = @indId AND iContactTypeId IN (117) AND tiRecordStatus = 1 UNION SELECT 'salesReview' permissionName, iOwnerId id FROM Contact WHERE iContactId = @indId AND iContactTypeId IN (115) AND tiRecordStatus = 1 UNION SELECT 'allianceReview' permissionName, iOwnerId id FROM Contact WHERE iContactId = @indId AND iContactTypeId IN (103) AND tiRecordStatus = 1";
			List<Object> params = new ArrayList<Object>();
			params.add(userId);
			permissionsMap = DataUtils.query(sql, params, ONYX_REP_DATA_SOURCE,
					new ResultSetHandler<Map<String, List<String>>>() {
						public Map<String, List<String>> handleResultSet(
								ResultSet rs) throws Exception {
							List<String> companyReview = new ArrayList<String>();
							List<String> fabReview = new ArrayList<String>();
							List<String> allianceReview = new ArrayList<String>();
							List<String> salesReview = new ArrayList<String>();
							List<String> csReview = new ArrayList<String>();

							while (rs.next()) {
								String permissionName = rs
										.getString("permissionName");
								String id = rs.getString("id");
								if ("companyReview".equals(permissionName)) {
									companyReview.add(id);
								} else if ("fabReview".equals(permissionName)) {
									fabReview.add(id);
								} else if ("allianceReview"
										.equals(permissionName)) {
									allianceReview.add(id);
								} else if ("salesReview".equals(permissionName)) {
									salesReview.add(id);
								} else if ("csReview".equals(permissionName)) {
									csReview.add(id);
								}
							}

							Map<String, List<String>> permissionsMap = new HashMap<String, List<String>>();
							permissionsMap.put("companyReview", companyReview);
							permissionsMap.put("fabReview", fabReview);
							permissionsMap.put("allianceReview", allianceReview);
							permissionsMap.put("salesReview", salesReview);
							permissionsMap.put("csReview", csReview);
							return permissionsMap;
						}
					});
		} catch (Exception e) {
			System.err.println("Error retrieving Manager Permissions for user "
					+ userId + ": " + e);
			e.printStackTrace();
		}

		return permissionsMap;
	}

	private String createParamPlaceholderString(List<String> list) {
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

package com.hd.cedg.lms.dao;

import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hd.cedg.lms.model.AdminAccessRights;
import com.hd.mis.data.DataUtils;
import com.hd.mis.data.ResultSetHandler;

public class AdminDAO extends BaseLmsDAO {

	public void saveEventAdmins(List<String> usernames) {
		try {
			List<Integer> userIds = retrieveUserIdByUserName(usernames);
			if(userIds != null && userIds.size() > 0){
				Map<Integer, AdminAccessRights> adminAccessRightsMap = getAdminAccessRights(userIds);
				List<String> sqllist = new ArrayList<String>();
				for(int userId : userIds){
					if(adminAccessRightsMap.containsKey(userId)){
						AdminAccessRights adminAccessRights = adminAccessRightsMap.get(userId);
						if(adminAccessRights.isAdmin() && adminAccessRights.isEventEditAdmin()){
							continue;
						}else{
							sqllist.add("update lc_admin set admin=1, eventedit=1 where userid="+userId);
						}
					}else{
						String sql = "insert into lc_admin (userid, admin, userdata, course, todo, ekp, direct, eventedit, eventsuper) values ( " + userId + ", 1, 0, 0, 0, 0, 0, 1, 0 )";
						sqllist.add(sql);
					}
				}
				
				DataUtils.executeSql(sqllist, LC_DATA_SOURCE);
			}
		} catch (Exception e) {
			System.err.println("Error saving Event Admins for Event "
					+ usernames + ": " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private Map<Integer, AdminAccessRights> getAdminAccessRights(List<Integer> userIds) {
		Map<Integer, AdminAccessRights> adminAccessRightsMap = new HashMap<Integer, AdminAccessRights>();

		try {
			String sql = "select userid, admin, userdata, course, todo, ekp, direct, eventedit, eventsuper from lc_admin where userid in ( ";
			sql += createParamPlaceholderString(userIds.size());
			sql += " )";
			List<Object> params = new ArrayList<Object>();
			for (Integer userId : userIds) {
				params.add(userId);
			}		
			
			adminAccessRightsMap = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<Map<Integer, AdminAccessRights>>() {
						public Map<Integer, AdminAccessRights> handleResultSet(ResultSet rs)
								throws Exception {
							Map<Integer, AdminAccessRights> adminAccessRightsMap = new HashMap<Integer, AdminAccessRights>();
							
							while (rs.next()) {
								AdminAccessRights adminAccessRights = new AdminAccessRights();
								adminAccessRights.setAdminAdmin(rs
										.getInt("admin") != 0);
								adminAccessRights.setUserAdmin(rs
										.getInt("userdata") != 0);
								adminAccessRights.setCourseAdmin(rs
										.getInt("course") != 0);
								adminAccessRights.setTodoAdmin(rs
										.getInt("todo") != 0);
								adminAccessRights
										.setEkpAdmin(rs.getInt("ekp") != 0);
								adminAccessRights.setDirectAdmin(rs
										.getInt("direct") != 0);
								adminAccessRights.setEventEditAdmin(rs
										.getInt("eventedit") != 0);
								adminAccessRights.setEventSuperAdmin(rs
										.getInt("eventsuper") != 0);
								adminAccessRightsMap.put(rs.getInt("userid"), adminAccessRights);
							}
							return adminAccessRightsMap;
						}
					});
		} catch (Exception e) {
			System.err
					.println("Error retrieving Admin User Access rights for users "
							+ userIds + ": " + e.getMessage());
			e.printStackTrace();
		}

		return adminAccessRightsMap;
	}
	
	private List<Integer> retrieveUserIdByUserName(List<String> usernames) {
		List<Integer> userIds = new ArrayList<Integer>();
		try {
			String sql = "SELECT userid FROM lc_user WHERE upper(username) in ( ";
			sql += createParamPlaceholderString(usernames.size());
			sql += " )";
			List<Object> params = new ArrayList<Object>();
			for (String username : usernames) {
				params.add(username.toUpperCase());
			}	
			
			userIds = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<List<Integer>>() {
						public List<Integer> handleResultSet(ResultSet rs)
								throws Exception {
							List<Integer> userIds = new ArrayList<Integer>();
							while (rs.next()) {
								userIds.add(rs.getInt("userid"));
							}
							return userIds;
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}

		return userIds;
	}
	
	private String createParamPlaceholderString(int placeholders) {
		if (placeholders == 0) {
			return "-1";
		} else {
			String paramString = "";
			for (int i = 0; i < placeholders; i++) {
				paramString += "?";
				if (i < (placeholders - 1)) {
					paramString += ", ";
				}
			}
			return paramString;
		}
	}
}

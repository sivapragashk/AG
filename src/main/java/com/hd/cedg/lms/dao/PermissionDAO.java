package com.hd.cedg.lms.dao;

import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;
import com.hd.mis.data.DataUtils;
import com.hd.mis.data.ResultSetHandler;

public class PermissionDAO extends BaseLmsDAO {

	public List<String> retrieveAccessTags(String permissionType, int relatedId) {
		List<String> accessTags = new ArrayList<String>();

		try {
			String sql = "select accesstagname from lc_accesstag where permissiontype = ? and relatedid = ?";
			List<Object> params = new ArrayList<Object>();
			params.add(permissionType);
			params.add(relatedId);
			accessTags = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<List<String>>() {
						public List<String> handleResultSet(ResultSet rs)
								throws Exception {
							List<String> accessTags = new ArrayList<String>();
							while (rs.next()) {
								String accessTag = rs.getString("accesstagname");
								accessTags.add(accessTag);
							}
							return accessTags;
						}
					});
		} catch (Exception e) {
			System.err.println("Error retrieving Access Tags for \""
					+ permissionType + "\" and ID " + relatedId + ": "
					+ e.getMessage());
			e.printStackTrace();
		}

		return accessTags;
	}
	
	public List<Integer> retrieveDockTags(String permissionType, int relatedId) {
		List<Integer> accessTags = new ArrayList<Integer>();

		try {
			String sql = "select dockid from lc_docktag where permissiontype = ? and relatedid = ?";
			List<Object> params = new ArrayList<Object>();
			params.add(permissionType);
			params.add(relatedId);
			accessTags = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<List<Integer>>() {
						public List<Integer> handleResultSet(ResultSet rs)
								throws Exception {
							List<Integer> dockTags = new ArrayList<Integer>();
							while (rs.next()) {
								dockTags.add(rs.getInt("dockid"));
							}
							return dockTags;
						}
					});
		} catch (Exception e) {
			System.err.println("Error retrieving Access Tags for \""
					+ permissionType + "\" and ID " + relatedId + ": "
					+ e.getMessage());
			e.printStackTrace();
		}

		return accessTags;
	}

	
	
	

}

package com.hd.cedg.lms.dao;

import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.hd.cedg.lms.model.CertificationStatus;
import com.hd.cedg.lms.model.LearningUser;
import com.hd.mis.data.DataUtils;
import com.hd.mis.data.ResultSetHandler;

public class CertificationDAO extends BaseLmsDAO {

	public Map<String, CertificationStatus> retrieveCertificationStatus(LearningUser user){
		Map<String, CertificationStatus> certificationlevels = retrieveCertifiedDetails(user.getUserId());
		certificationlevels.putAll(retrieveInProgressCertDetails(user.getUserId()));
		CertificationStatus certificationStatus = new CertificationStatus();
		certificationStatus.setName(user.getPipLevel());
		certificationlevels.put("installerProgram", certificationStatus);
		return certificationlevels;
	}
	
	private Map<String, CertificationStatus> retrieveCertifiedDetails(int userId){
		
		Map<String, CertificationStatus> certDetails = new HashMap<String, CertificationStatus>();

		try {
			String sql = "select todolistid, name, certprogram from lc_todolist where todolistid in (select max(todo.todolistid) todolistid from lc_user u inner join lc_todolisttranscript tran on tran.userid = u.userid and tran.iscomplete = 1 and u.active = 1 and u.userid = ? inner join lc_todolist todo on todo.todolistid = tran.todolistid and todo.certprogram in ('pdp', 'pfp') group by todo.certprogram)";
			List<Object> params = new ArrayList<Object>();
			params.add(userId);
			certDetails = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<Map<String, CertificationStatus>>() {
						public Map<String, CertificationStatus> handleResultSet(ResultSet rs)
								throws Exception {
							Map<String, CertificationStatus> certDetails = new HashMap<String, CertificationStatus>();
							while (rs.next()) {
								String program = rs.getString("certprogram");
								CertificationStatus certStatus = certDetails.get(program);
								if (certStatus == null) {
									certStatus = new CertificationStatus();
									certDetails.put(program, certStatus);
								}
								certStatus.setTodolistId(rs.getInt("todolistid"));
								certStatus.setName(rs.getString("name"));
							}
							return certDetails;
						}
					});
		} catch (Exception e) {
			System.err.println("CertificateDAO - Error retrieving Certified Details: "
					+ e.getMessage());
			e.printStackTrace();
		}

		return certDetails;
		
	}
	
	private Map<String, CertificationStatus> retrieveInProgressCertDetails(int userId){
		Map<String, CertificationStatus> certsInProgress = new HashMap<String, CertificationStatus>();
		try {
			String sql = "select u.userid, todo.todolistid, todo.name, todo.certprogram, sum(case when hist.status = 'DONE' then 1 else 0 end) done, count(*) total from lc_user u " + 
						 "inner join lc_todolisttranscript tran on tran.userid = u.userid and tran.iscomplete = 0 and u.active = 1 and u.userid = ? "+
						 "inner join lc_todolist todo on todo.todolistid = tran.todolistid and todo.certprogram in ('pdp', 'pfp') and todo.active = 1 " + 
						 "inner join lc_todolistset todoset on todoset.todolistid = todo.todolistid " +
						 "inner join lc_todolistsetitem item on item.setorder = todoset.setorder and item.todolistid = todo.todolistid " + 
						 "inner join lc_course crs on crs.learningid = item.learningid " +
						 "left join lc_history hist on hist.userid = u.userid and hist.learningid = item.learningid " +
						 "group by u.userid, todo.todolistid,todo.name, todoset.setorder, todo.certprogram order by u.userid, todo.todolistid, todoset.setorder";

			List<Object> params = new ArrayList<Object>();
			params.add(userId);
			certsInProgress = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<Map<String, CertificationStatus>>() {
						public Map<String, CertificationStatus> handleResultSet(ResultSet rs)
								throws Exception {
							Map<String, CertificationStatus> certsInProgress = new HashMap<String, CertificationStatus>();
							while (rs.next()) {
								String program = rs.getString("certprogram");
								CertificationStatus certStatus = certsInProgress.get(program);
								if (certStatus == null) {
									certStatus = new CertificationStatus();
									certsInProgress.put(program, certStatus);
								}
								int done = rs.getInt("done");
								int total = rs.getInt("total");
								if (total > 0) {
									if(certStatus.getName() == null){
										certStatus.setName(rs.getString("name"));
										certStatus.setTodolistId(rs.getInt("todolistid"));
									}
									certStatus.setPercentage(new Integer((100 * done) / total));
								}
							}
							return certsInProgress;
						}
					});
		} catch (Exception e) {
			System.err.println("CertificateDAO - Error retrieving InProgress Certification Details: "
					+ e.getMessage());
			e.printStackTrace();
		}

		return certsInProgress;
	}
	
}

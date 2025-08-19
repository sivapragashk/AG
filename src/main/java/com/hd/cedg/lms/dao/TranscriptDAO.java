package com.hd.cedg.lms.dao;

import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hd.cedg.lms.model.EkpTranscript;
import com.hd.cedg.lms.model.Transcript;
import com.hd.mis.data.DataUtils;
import com.hd.mis.data.ResultSetHandler;

public class TranscriptDAO extends BaseLmsDAO{

	public List<Transcript> retrieveAll(int userId) {
		List<Transcript> transcripts = new ArrayList<Transcript>();

		try {
			String sql = "select learningid, status, to_char(startdate, 'MM-DD-YYYY') startdate, to_char(enddate, 'MM-DD-YYYY') enddate from lc_history where userid = ?";
			List<Object> params = new ArrayList<Object>();
			params.add(userId);
			transcripts = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<List<Transcript>>() {
						public List<Transcript> handleResultSet(ResultSet rs)
								throws Exception {
							List<Transcript> transcripts = new ArrayList<Transcript>();
							while (rs.next()) {
								Transcript transcript = new Transcript();
								transcript.setLearningId(rs
										.getString("learningid"));
								if ("DONE".equals(rs.getString("status"))) {
									transcript
											.setStatus(Transcript.STATUS_DONE);
								} else {
									transcript
											.setStatus(Transcript.STATUS_MIDDLE);
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
			System.err.println("Error retrieving all Transcripts for user "
					+ userId + ": " + e.getMessage());
			e.printStackTrace();
		}

		return transcripts;
	}

	public Map<String, Transcript> retrieveAllMap(int userId) {
		List<Transcript> transcripts = retrieveAll(userId);
		Map<String, Transcript> transcriptMap = new HashMap<String, Transcript>();
		for (Transcript transcript : transcripts) {
			transcriptMap.put(transcript.getLearningId(), transcript);
		}
		return transcriptMap;
	}
	
	public Transcript retrieve(String learningId, int userId) {
		Transcript transcript = null;

		try {
			String sql = "select learningid, status, to_char(startdate, 'MM-DD-YYYY') startdate, to_char(enddate, 'MM-DD-YYYY') enddate from lc_history where userid = ? and learningid = ?";
			List<Object> params = new ArrayList<Object>();
			params.add(userId);
			params.add(learningId);
			transcript = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<Transcript>() {
						public Transcript handleResultSet(ResultSet rs)
								throws Exception {
							Transcript transcript = null;
							if (rs.next()) {
								transcript = new Transcript();
								transcript.setLearningId(rs
										.getString("learningid"));
								if ("DONE".equals(rs.getString("status"))) {
									transcript
											.setStatus(Transcript.STATUS_DONE);
								} else {
									transcript
											.setStatus(Transcript.STATUS_MIDDLE);
								}
								transcript.setStartDate(rs
										.getString("startdate"), "MM-dd-yyyy");
								transcript.setEndDate(rs.getString("enddate"),
										"MM-dd-yyyy");
							}
							return transcript;
						}
					});
		} catch (Exception e) {
			System.err.println("Error retrieving Transcript for user " + userId
					+ " and course " + learningId + ":" + e.getMessage());
			e.printStackTrace();
		}

		return transcript;
	}
	
	public EkpTranscript retrieveEkpTranscript(String learningId, int userId) {
		EkpTranscript ekpTranscript = null;

		try {
			String sql = "select tran.learningid, tran.transcriptid, tran.isopen from transcript tran where tran.learningid = ? and tran.userid = ? and not exists (select * from transcript where learningid = tran.learningid and userid = tran.userid and transcriptid > tran.transcriptid)";
			List<Object> params = new ArrayList<Object>();
			params.add(learningId);
			params.add("" + userId);
			ekpTranscript = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<EkpTranscript>() {
						public EkpTranscript handleResultSet(ResultSet rs)
								throws Exception {
							EkpTranscript ekpTranscript = null;
							if (rs.next()) {
								ekpTranscript = new EkpTranscript();
								ekpTranscript.setLearningId(rs
										.getString("learningid"));
								ekpTranscript.setTranscriptId(rs
										.getString("transcriptid"));
								ekpTranscript.setOpen("Y".equals(rs
										.getString("isopen")));
							}
							return ekpTranscript;
						}
					});
		} catch (Exception e) {
			System.err
					.println("Error retrieving EKP Transcript record for Learning ID "
							+ learningId
							+ " and User + "
							+ userId
							+ ": "
							+ e.getMessage());
			e.printStackTrace();
		}

		return ekpTranscript;
	}
	
	public void insertDirectRecord(String learningId, int userId) {
		try {
			String sql = "insert into lc_directhistory (historyid, userid, learningid, launchdate) values (lc_directhistory_seq.nextval, ?, ?, sysdate)";
			List<Object> params = new ArrayList<Object>();
			params.add(userId);
			params.add(learningId);
			DataUtils.execute(sql, params, LC_DATA_SOURCE);
		} catch (Exception e) {
			System.err
					.println("Error inserting Direct Launch record for Learning ID "
							+ learningId
							+ " and User + "
							+ userId
							+ ": "
							+ e.getMessage());
			e.printStackTrace();
		}
	}

}

package com.hd.cedg.lms.dao;

import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hd.cedg.lms.model.Event;
import com.hd.cedg.lms.model.EventQuestion;
import com.hd.mis.data.DataUtils;
import com.hd.mis.data.ResultSetHandler;

public class EventQuestionDAO extends BaseLmsDAO {

	public List<EventQuestion> retrieveListByEventId(int eventId) {
		List<EventQuestion> questions = new ArrayList<EventQuestion>();

		try {
			String sql = "select questionid, text, type, required from lc_eventquestion where eventid = ? and active = 1 order by sortorder";
			List<Object> params = new ArrayList<Object>();
			params.add(eventId);
			questions = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<List<EventQuestion>>() {
						public List<EventQuestion> handleResultSet(ResultSet rs)
								throws Exception {
							List<EventQuestion> questions = new ArrayList<EventQuestion>();

							while (rs.next()) {
								EventQuestion question = new EventQuestion();

								question.setQuestionId(rs.getInt("questionid"));
								question.setText(rs.getString("text"));
								question.setType(rs.getInt("type"));
								question
										.setRequired(rs.getInt("required") != 0);

								questions.add(question);
							}

							return questions;
						}
					});

			Map<Integer, List<String>> answersMap = retrieveAnswersByEventId(eventId);
			for (EventQuestion question : questions) {
				List<String> answers = answersMap.get(question.getQuestionId());
				if (answers != null) {
					question.setAnswers(answers);
				}
			}

			Map<Integer, List<Integer>> optionIdsMap = retrieveOptionIdsByEventId(eventId);
			for (EventQuestion question : questions) {
				List<Integer> optionIds = optionIdsMap.get(question
						.getQuestionId());
				if (optionIds != null) {
					question.setOptionIds(optionIds);
				}
			}

			Map<Integer, List<Integer>> packageIdsMap = retrievePackageIdsByEventId(eventId);
			for (EventQuestion question : questions) {
				List<Integer> packageIds = packageIdsMap.get(question
						.getQuestionId());
				if (packageIds != null) {
					question.setPackageIds(packageIds);
				}
			}

		} catch (Exception e) {
			System.err.println("Error retrieving Event Questions for event "
					+ eventId + ": " + e.getMessage());
			e.printStackTrace();
		}

		return questions;
	}

	public Map<Integer, List<EventQuestion>> retrieveListByList(
			List<Integer> eventIds) {
		Map<Integer, List<EventQuestion>> questionMap = new HashMap<Integer, List<EventQuestion>>();

		try {
			String sql = "select eventid, questionid, text, type, required from lc_eventquestion where eventid in (-1";
			for (int i = 0; i < eventIds.size(); i++) {
				sql += ", ?";
			}
			sql += ") and active = 1 order by sortorder";
			List<Object> params = new ArrayList<Object>();
			for (Integer eventId : eventIds) {
				params.add(eventId);
			}
			questionMap = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<Map<Integer, List<EventQuestion>>>() {
						public Map<Integer, List<EventQuestion>> handleResultSet(
								ResultSet rs) throws Exception {
							Map<Integer, List<EventQuestion>> questionMap = new HashMap<Integer, List<EventQuestion>>();

							while (rs.next()) {
								EventQuestion question = new EventQuestion();

								question.setQuestionId(rs.getInt("questionid"));
								question.setText(rs.getString("text"));
								question.setType(rs.getInt("type"));
								question
										.setRequired(rs.getInt("required") != 0);

								Integer eventId = new Integer(rs
										.getInt("eventid"));
								List<EventQuestion> questions = questionMap
										.get(eventId);
								if (questions == null) {
									questions = new ArrayList<EventQuestion>();
									questionMap.put(eventId, questions);
								}
								questions.add(question);
							}

							return questionMap;
						}
					});

			Map<Integer, List<String>> answersMap = retrieveAnswersByEventIds(eventIds);
			Map<Integer, List<Integer>> optionIdsMap = retrieveOptionIdsByEventIds(eventIds);
			Map<Integer, List<Integer>> packageIdsMap = retrievePackageIdsByEventIds(eventIds);
			for (Integer eventId : questionMap.keySet()) {
				List<EventQuestion> questions = questionMap.get(eventId);
				for (EventQuestion question : questions) {
					List<String> answers = answersMap.get(question
							.getQuestionId());
					if (answers != null) {
						question.setAnswers(answers);
					}
				}
				for (EventQuestion question : questions) {
					List<Integer> optionIds = optionIdsMap.get(question
							.getQuestionId());
					if (optionIds != null) {
						question.setOptionIds(optionIds);
					}
				}
				for (EventQuestion question : questions) {
					List<Integer> packageIds = packageIdsMap.get(question
							.getQuestionId());
					if (packageIds != null) {
						question.setPackageIds(packageIds);
					}
				}
			}

		} catch (Exception e) {
			System.err
					.println("Error retrieving Event Questions for list of event IDs: "
							+ e.getMessage());
			e.printStackTrace();
		}

		return questionMap;
	}

	public void saveByEvent(Event event) {
		for (int questionIndex = 0; questionIndex < event.getQuestions().size(); questionIndex++) {
			EventQuestion question = event.getQuestions().get(questionIndex);
			if (question.getQuestionId() > 0) {
				// Update
				try {
					String sql = "update lc_eventquestion set text=?, type=?, required=?, sortorder=? where questionid = ?";
					List<Object> params = new ArrayList<Object>();
					params.add(question.getText());
					params.add(question.getType());
					params.add(question.isRequired() ? 1 : 0);
					params.add(questionIndex);
					params.add(question.getQuestionId());
					DataUtils.execute(sql, params, LC_DATA_SOURCE);

					// Save answers
					saveQuestionAnswers(question);

					// Save option associations
					saveQuestionOptions(question);
				} catch (Exception e) {
					System.err.println("Error updating Question "
							+ question.getQuestionId() + ": " + e.getMessage());
					e.printStackTrace();
				}
			} else {
				// Generate questionId
				int questionId = generateQuestionId();

				if (questionId > 0) {
					question.setQuestionId(questionId);

					// Insert
					try {
						String sql = "insert into lc_eventquestion (questionid, eventid, text, type, required, sortorder, active) values (?, ?, ?, ?, ?, ?, 1)";
						List<Object> params = new ArrayList<Object>();
						params.add(question.getQuestionId());
						params.add(event.getEventId());
						params.add(question.getText());
						params.add(question.getType());
						params.add(question.isRequired() ? 1 : 0);
						params.add(questionIndex);
						DataUtils.execute(sql, params, LC_DATA_SOURCE);

						// Save answers
						saveQuestionAnswers(question);

						// Save option associations
						saveQuestionOptions(question);
					} catch (Exception e) {
						System.err.println("Error inserting Event Question: "
								+ e.getMessage());
						e.printStackTrace();
					}
				}
			}
		}
	}

	private Map<Integer, List<String>> retrieveAnswersByEventId(int eventId) {
		List<Integer> eventIds = new ArrayList<Integer>();
		eventIds.add(new Integer(eventId));
		return retrieveAnswersByEventIds(eventIds);
	}

	private Map<Integer, List<String>> retrieveAnswersByEventIds(
			List<Integer> eventIds) {
		Map<Integer, List<String>> answersMap = new HashMap<Integer, List<String>>();

		try {
			String sql = "select ans.questionid, ans.text from lc_eventquestionanswer ans inner join lc_eventquestion qu on qu.questionid = ans.questionid and qu.active = 1 and qu.eventid in (-1";
			for (int i = 0; i < eventIds.size(); i++) {
				sql += ", ?";
			}
			sql += ") order by ans.sortorder";
			List<Object> params = new ArrayList<Object>();
			for (Integer eventId : eventIds) {
				params.add(eventId);
			}
			answersMap = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<Map<Integer, List<String>>>() {
						public Map<Integer, List<String>> handleResultSet(
								ResultSet rs) throws Exception {
							Map<Integer, List<String>> answersMap = new HashMap<Integer, List<String>>();

							while (rs.next()) {
								int questionId = rs.getInt("questionid");
								List<String> answers = answersMap
										.get(new Integer(questionId));
								if (answers == null) {
									answers = new ArrayList<String>();
									answersMap.put(new Integer(questionId),
											answers);
								}

								answers.add(rs.getString("text"));
							}

							return answersMap;
						}
					});
		} catch (Exception e) {
			System.err
					.println("Error retrieving Map of Question Answers for list of event IDs: "
							+ e.getMessage());
			e.printStackTrace();
		}

		return answersMap;
	}

	private Map<Integer, List<Integer>> retrieveOptionIdsByEventId(int eventId) {
		List<Integer> eventIds = new ArrayList<Integer>();
		eventIds.add(new Integer(eventId));
		return retrieveOptionIdsByEventIds(eventIds);
	}

	private Map<Integer, List<Integer>> retrieveOptionIdsByEventIds(
			List<Integer> eventIds) {
		Map<Integer, List<Integer>> optionIdsMap = new HashMap<Integer, List<Integer>>();

		try {
			String sql = "select op.questionid, op.optionid from lc_eventquestionoption op inner join lc_eventquestion qu on qu.questionid = op.questionid and qu.active = 1 and qu.eventid in (-1";
			for (int i = 0; i < eventIds.size(); i++) {
				sql += ", ?";
			}
			sql += ") where op.optiontypeid = ?";
			List<Object> params = new ArrayList<Object>();
			for (Integer eventId : eventIds) {
				params.add(eventId);
			}
			params.add(Event.TYPE_ID_OPTION);
			optionIdsMap = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<Map<Integer, List<Integer>>>() {
						public Map<Integer, List<Integer>> handleResultSet(
								ResultSet rs) throws Exception {
							Map<Integer, List<Integer>> optionIdsMap = new HashMap<Integer, List<Integer>>();

							while (rs.next()) {
								int questionId = rs.getInt("questionid");
								List<Integer> optionIds = optionIdsMap
										.get(new Integer(questionId));
								if (optionIds == null) {
									optionIds = new ArrayList<Integer>();
									optionIdsMap.put(new Integer(questionId),
											optionIds);
								}

								optionIds
										.add(new Integer(rs.getInt("optionid")));
							}

							return optionIdsMap;
						}
					});
		} catch (Exception e) {
			System.err
					.println("Error retrieving Map of Question Option IDs for list of event IDs: "
							+ e.getMessage());
			e.printStackTrace();
		}

		return optionIdsMap;
	}

	private Map<Integer, List<Integer>> retrievePackageIdsByEventId(int eventId) {
		List<Integer> eventIds = new ArrayList<Integer>();
		eventIds.add(new Integer(eventId));
		return retrievePackageIdsByEventIds(eventIds);
	}

	private Map<Integer, List<Integer>> retrievePackageIdsByEventIds(
			List<Integer> eventIds) {
		Map<Integer, List<Integer>> packageIdsMap = new HashMap<Integer, List<Integer>>();

		try {
			String sql = "select op.questionid, op.optionid from lc_eventquestionoption op inner join lc_eventquestion qu on qu.questionid = op.questionid and qu.active = 1 and qu.eventid in (-1";
			for (int i = 0; i < eventIds.size(); i++) {
				sql += ", ?";
			}
			sql += ") where op.optiontypeid = ?";
			List<Object> params = new ArrayList<Object>();
			for (Integer eventId : eventIds) {
				params.add(eventId);
			}
			params.add(Event.TYPE_ID_PACKAGE);
			packageIdsMap = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<Map<Integer, List<Integer>>>() {
						public Map<Integer, List<Integer>> handleResultSet(
								ResultSet rs) throws Exception {
							Map<Integer, List<Integer>> packageIdsMap = new HashMap<Integer, List<Integer>>();

							while (rs.next()) {
								int questionId = rs.getInt("questionid");
								List<Integer> packageIds = packageIdsMap
										.get(new Integer(questionId));
								if (packageIds == null) {
									packageIds = new ArrayList<Integer>();
									packageIdsMap.put(new Integer(questionId),
											packageIds);
								}

								packageIds.add(new Integer(rs
										.getInt("optionid")));
							}

							return packageIdsMap;
						}
					});
		} catch (Exception e) {
			System.err
					.println("Error retrieving Map of Question Package IDs for list of event IDs: "
							+ e.getMessage());
			e.printStackTrace();
		}

		return packageIdsMap;
	}

	private void saveQuestionAnswers(EventQuestion question) {
		try {
			// Delete existing question answers
			String deleteSql = "delete from lc_eventquestionanswer where questionid = ?";
			List<Object> deleteParams = new ArrayList<Object>();
			deleteParams.add(question.getQuestionId());
			DataUtils.execute(deleteSql, deleteParams, LC_DATA_SOURCE);

			// Insert current question answers
			for (int answerIndex = 0; answerIndex < question.getAnswers()
					.size(); answerIndex++) {
				String answer = question.getAnswers().get(answerIndex);
				String insertSql = "insert into lc_eventquestionanswer (questionid, text, sortorder) values (?, ?, ?)";
				List<Object> insertParams = new ArrayList<Object>();
				insertParams.add(question.getQuestionId());
				insertParams.add(answer);
				insertParams.add(answerIndex);
				DataUtils.execute(insertSql, insertParams, LC_DATA_SOURCE);
			}
		} catch (Exception e) {
			System.err.println("Error saving Answers for question "
					+ question.getQuestionId() + ": " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void saveQuestionOptions(EventQuestion question) {
		try {
			// Delete existing question options
			String deleteSql = "delete from lc_eventquestionoption where questionid = ?";
			List<Object> deleteParams = new ArrayList<Object>();
			deleteParams.add(question.getQuestionId());
			DataUtils.execute(deleteSql, deleteParams, LC_DATA_SOURCE);

			// Insert current question options
			for (Integer optionId : question.getOptionIds()) {
				String insertSql = "insert into lc_eventquestionoption (questionid, optiontypeid, optionid) values (?, ?, ?)";
				List<Object> insertParams = new ArrayList<Object>();
				insertParams.add(question.getQuestionId());
				insertParams.add(Event.TYPE_ID_OPTION);
				insertParams.add(optionId.intValue());
				DataUtils.execute(insertSql, insertParams, LC_DATA_SOURCE);
			}
			for (Integer packageId : question.getPackageIds()) {
				String insertSql = "insert into lc_eventquestionoption (questionid, optiontypeid, optionid) values (?, ?, ?)";
				List<Object> insertParams = new ArrayList<Object>();
				insertParams.add(question.getQuestionId());
				insertParams.add(Event.TYPE_ID_PACKAGE);
				insertParams.add(packageId.intValue());
				DataUtils.execute(insertSql, insertParams, LC_DATA_SOURCE);
			}
		} catch (Exception e) {
			System.err.println("Error saving Options for question "
					+ question.getQuestionId() + ": " + e.getMessage());
			e.printStackTrace();
		}
	}

	private int generateQuestionId() {
		int questionId = 0;
		try {
			String sql = "select lc_eventquestion_seq.nextval questionid from dual";
			List<Object> params = new ArrayList<Object>();
			Integer id = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<Integer>() {
						public Integer handleResultSet(ResultSet rs)
								throws Exception {
							if (rs.next()) {
								return new Integer(rs.getInt("questionid"));
							}
							return null;
						}
					});
			if (id != null) {
				questionId = id.intValue();
			}
		} catch (Exception e) {
			System.err.println("Error generating a new Question ID: "
					+ e.getMessage());
			e.printStackTrace();
		}
		return questionId;
	}
}

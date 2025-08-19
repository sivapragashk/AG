package com.hd.cedg.lms.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hd.cedg.lms.model.Course;
import com.hd.cedg.lms.model.Evaluation;
import com.hd.cedg.lms.model.EvaluationQuestion;
import com.hd.cedg.lms.model.EvaluationQuestionOption;
import com.hd.mis.data.DataUtils;
import com.hd.mis.data.ResultSetHandler;

public class EvaluationDAO extends BaseLmsDAO {

	public List<EvaluationQuestion> retrieveQuestions(Course course) {
		List<EvaluationQuestion> questions = new ArrayList<EvaluationQuestion>();

		try {
			String sql = "select questionid, questiontext, questiontype from lc_evaluationquestion order by sortorder";
			List<Object> params = new ArrayList<Object>();
			questions = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<List<EvaluationQuestion>>() {
						public List<EvaluationQuestion> handleResultSet(
								ResultSet rs) throws Exception {
							List<EvaluationQuestion> questions = new ArrayList<EvaluationQuestion>();
							while (rs.next()) {
								EvaluationQuestion question = new EvaluationQuestion();

								question.setQuestionId(rs.getInt("questionid"));
								question.setQuestionText(rs
										.getString("questiontext"));
								String questionTypeStr = rs
										.getString("questiontype");
								if ("singleselect"
										.equalsIgnoreCase(questionTypeStr)) {
									question
											.setQuestionType(EvaluationQuestion.EVAL_QUESTION_TYPE_SINGLESELECT);
								} else if ("freetext"
										.equalsIgnoreCase(questionTypeStr)) {
									question
											.setQuestionType(EvaluationQuestion.EVAL_QUESTION_TYPE_FREETEXT);
								}

								questions.add(question);
							}

							Map<Integer, List<EvaluationQuestionOption>> optionMap = retrieveQuestionOptionMap();
							for (EvaluationQuestion question : questions) {
								List<EvaluationQuestionOption> options = optionMap
										.get(new Integer(question
												.getQuestionId()));
								if (options != null) {
									question.setOptions(options);
								}
							}

							return questions;
						}
					});
		} catch (Exception e) {
			System.err.println("Error retrieving Evaluation Questions: "
					+ e.getMessage());
			e.printStackTrace();
		}

		for (EvaluationQuestion question : questions) {
			String text = question.getQuestionText();
			text = text.replaceAll("<<DISPLAYTYPE>>", course.getDisplayType());
			question.setQuestionText(text);
		}

		return questions;
	}

	private Map<Integer, List<EvaluationQuestionOption>> retrieveQuestionOptionMap() {
		Map<Integer, List<EvaluationQuestionOption>> optionMap = new HashMap<Integer, List<EvaluationQuestionOption>>();

		try {
			String sql = "select questionid, optiontext, optionvalue from lc_evaluationquestionoption order by questionid, sortorder";
			List<Object> params = new ArrayList<Object>();
			optionMap = DataUtils
					.query(
							sql,
							params,
							LC_DATA_SOURCE,
							new ResultSetHandler<Map<Integer, List<EvaluationQuestionOption>>>() {
								public Map<Integer, List<EvaluationQuestionOption>> handleResultSet(
										ResultSet rs) throws Exception {
									Map<Integer, List<EvaluationQuestionOption>> optionMap = new HashMap<Integer, List<EvaluationQuestionOption>>();

									while (rs.next()) {
										Integer questionId = new Integer(rs
												.getInt("questionid"));
										List<EvaluationQuestionOption> options = optionMap
												.get(questionId);
										if (options == null) {
											options = new ArrayList<EvaluationQuestionOption>();
											optionMap.put(questionId, options);
										}
										EvaluationQuestionOption option = new EvaluationQuestionOption();
										option.setOptionText(rs
												.getString("optiontext"));
										option.setValue(rs
												.getString("optionvalue"));
										options.add(option);
									}

									return optionMap;
								}
							});
		} catch (Exception e) {
			System.err
					.println("Error retrieving Map of Evaluation Question Options: "
							+ e.getMessage());
			e.printStackTrace();
		}

		return optionMap;
	}

	public void save(Evaluation evaluation, int userId) {
		for (EvaluationQuestion question : evaluation.getQuestions()) {
			try {
				String sql = "insert into lc_evaluationresponse (userid, learningid, questionid, responsevalue, responsedate) values (?, ?, ?, ?, sysdate)";
				List<Object> params = new ArrayList<Object>();
				params.add(userId);
				params.add(evaluation.getCourse().getLearningId());
				params.add(question.getQuestionId());
				String response = evaluation.getResponses().get(question);
				params.add(response != null ? response : "");
				DataUtils.execute(sql, params, LC_DATA_SOURCE);
			} catch (Exception e) {
				System.err.println("Error saving Evaluation Response: "
						+ e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public boolean hasEvaluated(int userId, String learningId) {
		boolean hasEvaluated = true;

		try {
			String sql = "select * from lc_evaluationresponse where userid = ? and learningid = ?";
			List<Object> params = new ArrayList<Object>();
			params.add(userId);
			params.add(learningId);
			hasEvaluated = DataUtils.query(sql, params, LC_DATA_SOURCE,
					new ResultSetHandler<Boolean>() {
						public Boolean handleResultSet(ResultSet rs)
								throws Exception {
							return rs.next();
						}
					}).booleanValue();
		} catch (Exception e) {
			System.err.println("Error looking for Evaluation of " + learningId
					+ " for user " + userId + ": " + e.getMessage());
			e.printStackTrace();
		}

		return hasEvaluated;
	}
}

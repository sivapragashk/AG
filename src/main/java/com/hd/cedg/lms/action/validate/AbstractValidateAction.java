package com.hd.cedg.lms.action.validate;

import java.util.HashMap;
import java.util.Map;

import com.hd.cedg.lms.action.base.BaseControllerAction;
import com.hd.cedg.lms.model.ExecutionResult;

public abstract class AbstractValidateAction extends BaseControllerAction {

	protected abstract void validate(Map<String, Object> params,
			Map<String, String> errors);

	public ExecutionResult doAction(Map<String, Object> params) {
		Map<String, String> errors = new HashMap<String, String>();

		validate(params, errors);

		params.put("errors", errors);

		ExecutionResult result = new ExecutionResult();
		if (errors.size() == 0) {
			result.setStatus(ExecutionResult.SUCCESS);
		} else {
			result.setStatus(ExecutionResult.FAILURE);
		}
		return result;
	}

	protected boolean notEmpty(String field, String fieldName,
			Map<String, String> errors, String errorValue) {
		if (field == null || "".equals(field)) {
			errors.put(fieldName, errorValue);
			return false;
		} else {
			return true;
		}
	}

	protected void maxLength(String field, int length, String fieldName,
			Map<String, String> errors) {
		if (field != null && field.length() > length) {
			errors.put(fieldName, "Max Length is " + length);
		}
	}

	protected boolean validInt(String field, boolean positive, boolean zero,
			boolean negative, String fieldName, Map<String, String> errors,
			String errorValue) {
		if (notEmpty(field, fieldName, errors, errorValue)) {
			try {
				int value = Integer.parseInt(field);
				if (!positive && value > 0) {
					errors.put(fieldName, errorValue);
					return false;
				}
				if (!zero && value == 0) {
					errors.put(fieldName, errorValue);
					return false;
				}
				if (!negative && value < 0) {
					errors.put(fieldName, errorValue);
					return false;
				}
				return true;
			} catch (NumberFormatException e) {
				errors.put(fieldName, errorValue);
				return false;
			}
		} else {
			return false;
		}
	}
}

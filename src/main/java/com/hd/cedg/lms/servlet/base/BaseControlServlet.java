package com.hd.cedg.lms.servlet.base;

import java.util.HashMap;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hd.cedg.lms.model.ExecutionResult;


public abstract class BaseControlServlet extends HttpServlet {

	private static final long serialVersionUID = 4008587740122940795L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException {
		defaultAction(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException {
		defaultAction(request, response);
	}

	@SuppressWarnings("unchecked")
	public void defaultAction(HttpServletRequest request,
			HttpServletResponse response) {

		// Get Action
		String action = request.getParameter("controllerAction");

		// Get Params
		Set<Entry<String, String[]>> entries = request.getParameterMap()
				.entrySet();
		Map<String, Object> params = new HashMap<String, Object>();
		for (Entry<String, String[]> entry : entries) {
			String key = entry.getKey();
			if (entry.getValue().length == 1) {
				String value = entry.getValue()[0];
				params.put(key, value);
			} else {
				String[] values = entry.getValue();
				params.put(key, values);
			}
		}

		// Get User
		Object user = getUser(request, params);
		params.put("user", user);
		
		// Get additional parameters
		Map<String, Object> addlParams = getAdditionalParams(request, params);
		for (String key : addlParams.keySet()) {
			Object value = addlParams.get(key);
			params.put(key, value);
		}

		
		// Execute the controller
		ExecutionResult executionResult = execute(action, params);

		
		// Direct to result's page
		String destPage = "/WEB-INF/jsp/" + executionResult.getPage();
		RequestDispatcher dispatcher = getServletContext()
				.getRequestDispatcher(destPage);

		// Add attributes to request
		Map<String, Object> attributes = executionResult.getAttributes();
		for (String key : attributes.keySet()) {
			Object value = attributes.get(key);
			request.setAttribute(key, value);
		}

		// Forward
		try {
			dispatcher.forward(request, response);
		} catch (Exception e) {
			System.out.println("Could not forward the request to '"
					+ executionResult.getPage() + "': " + e);
		}
	}

	protected abstract ExecutionResult execute(String action,
			Map<String, Object> params);

	protected abstract Object getUser(HttpServletRequest request,
			Map<String, Object> params);

	protected abstract Map<String, Object> getAdditionalParams(
			HttpServletRequest request, Map<String, Object> params);

	
}

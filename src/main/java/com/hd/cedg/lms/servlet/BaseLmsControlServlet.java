package com.hd.cedg.lms.servlet;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import com.hd.cedg.lms.bizsvc.LearningUserBizSvc;
import com.hd.cedg.lms.bizsvc.ToDoListActivityBizSvc;
import com.hd.cedg.lms.model.LearningUser;
import com.hd.cedg.lms.servlet.base.BaseControlServlet;


public abstract class BaseLmsControlServlet extends BaseControlServlet {

	private static final long serialVersionUID = 2955474262616302985L;

	protected Object getUser(HttpServletRequest request,
			Map<String, Object> params) {

		String username = getUsername(request);
		HttpSession session = request.getSession();
		LearningUser user = (LearningUser) session.getAttribute("user");
		if (user == null) {
			// Retrieve LearningUser (try twice because of VPN bug... )
			String onyxWriteStr = getServletContext().getInitParameter("ONYX_WRITE");
			boolean onyxWrite = Boolean.parseBoolean(onyxWriteStr);
			String hdCatalogXmlRpcUrl = getServletContext().getInitParameter("HDCATALOG_XMLRPC_URL");
			String hdCatalogXmlRpcAccessKey = getServletContext().getInitParameter("HDCATALOG_XMLRPC_ACCESS_KEY");
			String hdCatalogTranslateDefAccessTags = getServletContext().getInitParameter("HDCATALOG_TRANSLATE_DEF_ACCESSTAGS");
			String emailServerUrl = getServletContext().getInitParameter("EMAIL_SERVER_URL");

			LearningUserBizSvc learningUserBizSvc = new LearningUserBizSvc(onyxWrite, hdCatalogXmlRpcUrl, hdCatalogXmlRpcAccessKey, hdCatalogTranslateDefAccessTags);
			ToDoListActivityBizSvc toDoListActivityBizSvc = new ToDoListActivityBizSvc(emailServerUrl, onyxWrite, hdCatalogXmlRpcUrl,hdCatalogXmlRpcAccessKey, hdCatalogTranslateDefAccessTags);
			user = learningUserBizSvc.retrieveSyncByUsername(username);
			if (user == null) {
				user = learningUserBizSvc.retrieveSyncByUsername(username);
			}
			
			// Check if any ToDoLists are newly completed - this also fires off
			// any emails or flag changes that need to happen as a result.
			boolean toDoListsCompleted = toDoListActivityBizSvc
					.checkForCompletion(user);
			if (toDoListsCompleted) {
				// Reload user
				user = learningUserBizSvc.retrieveSyncByUsername(username);
			}
			
			// Sync the access tags for the user
			toDoListActivityBizSvc.syncUserAccessTags(user);

			
			session.setAttribute("user", user);
			
		}
		System.out.println("call 4 ________________>" +user.getUsername() );
		return user;

	}

	private String getUsername(HttpServletRequest request) {
		// Get Username from request
		String usernameEncoded = request.getRemoteUser();
		String username = "";
		try {
			username = URLDecoder.decode(usernameEncoded, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		username = username.trim();
		return username;
	}

	

	

	protected boolean checkAuthorization(Object user) {
		LearningUser learningUser = (LearningUser) user;

		if (learningUser != null && learningUser.isActive()) {
			return true;
		} else {
			return false;
		}
	}
	
	protected Map<String, Object> getAdditionalParams(
			HttpServletRequest request, Map<String, Object> params) {
		HttpSession session = request.getSession();

		Map<String, Object> addlParams = new HashMap<String, Object>();

		// Onyx Write flag
		String onyxWriteStr = (String) session.getAttribute("onyxWrite");
		if (onyxWriteStr == null) {
			onyxWriteStr = getServletContext().getInitParameter("ONYX_WRITE");
			session.setAttribute("onyxWrite", onyxWriteStr);
		}
		addlParams.put("onyxWrite", onyxWriteStr);

		// XML RPC flags
		String hdCatalogXmlRpcUrl = (String) session
				.getAttribute("hdCatalogXmlRpcUrl");
		if (hdCatalogXmlRpcUrl == null) {
			hdCatalogXmlRpcUrl = getServletContext().getInitParameter(
					"HDCATALOG_XMLRPC_URL");
			session.setAttribute("hdCatalogXmlRpcUrl", hdCatalogXmlRpcUrl);
		}
		addlParams.put("hdCatalogXmlRpcUrl", hdCatalogXmlRpcUrl);

		String hdCatalogXmlRpcAccessKey = (String) session
				.getAttribute("hdCatalogXmlRpcAccessKey");
		if (hdCatalogXmlRpcAccessKey == null) {
			hdCatalogXmlRpcAccessKey = getServletContext().getInitParameter(
					"HDCATALOG_XMLRPC_ACCESS_KEY");
			session.setAttribute("hdCatalogXmlRpcAccessKey",
					hdCatalogXmlRpcAccessKey);
		}
		addlParams.put("hdCatalogXmlRpcAccessKey", hdCatalogXmlRpcAccessKey);

		String hdCatalogTranslateDefAccessTags = (String) session
				.getAttribute("hdCatalogTranslateDefAccessTags");
		if (hdCatalogTranslateDefAccessTags == null) {
			hdCatalogTranslateDefAccessTags = getServletContext()
					.getInitParameter("HDCATALOG_TRANSLATE_DEF_ACCESSTAGS");
			session.setAttribute("hdCatalogTranslateDefAccessTags",
					hdCatalogTranslateDefAccessTags);
		}
		addlParams.put("hdCatalogTranslateDefAccessTags",
				hdCatalogTranslateDefAccessTags);

		// Email Server URL
		String emailServerUrl = (String) session.getAttribute("emailServerUrl");
		if (emailServerUrl == null) {
			emailServerUrl = getServletContext().getInitParameter(
					"EMAIL_SERVER_URL");
			session.setAttribute("emailServerUrl", emailServerUrl);
		}
		addlParams.put("emailServerUrl", emailServerUrl);
		addlParams.put("nonPIPMemberAccessEmail", getServletContext().getInitParameter("NON_PIP_MEMBER_ACCESS_EMAIL"));

		// EKP Server URL
		String ekpServerUrl = (String) session.getAttribute("ekpServerUrl");
		if (ekpServerUrl == null) {
			ekpServerUrl = getServletContext().getInitParameter(
					"EKP_SERVER_URL");
			session.setAttribute("ekpServerUrl", ekpServerUrl);
		}
		addlParams.put("ekpServerUrl", ekpServerUrl);

		return addlParams;
	}
	
}

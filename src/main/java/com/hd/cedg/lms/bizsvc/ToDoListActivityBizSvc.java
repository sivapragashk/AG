package com.hd.cedg.lms.bizsvc;

import java.util.Date;

import java.util.List;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import com.hd.cedg.lms.dao.ToDoListDAO;
import com.hd.cedg.lms.dao.TranscriptDAO;
import com.hd.cedg.lms.model.EmailTemplate;
import com.hd.cedg.lms.model.LearningUser;
import com.hd.cedg.lms.model.ToDoList;
import com.hd.cedg.lms.model.ToDoListSet;
import com.hd.cedg.lms.model.ToDoListTranscript;
import com.hd.cedg.lms.model.Transcript;
import com.hd.mis.mailer.EmailHandlerBizSvc;
import com.hd.mis.mailer.EmailObject;

public class ToDoListActivityBizSvc {

	private ToDoListDAO toDoListDAO;
	private TranscriptDAO transcriptDAO;
	private LearningUserBizSvc learningUserBizSvc;
	private static final String CI = "ci";
	private static final String MI = "mi";
	private static final String PROGRAM_TYPE = "re-certification";

	private String emailServerUrl;

	public ToDoListActivityBizSvc(String emailServerUrl, boolean onyxWrite,
			String hdCatalogXmlRpcUrl, String hdCatalogXmlRpcAccessKey,
			String hdCatalogTranslateDefAccessTags) {
		toDoListDAO = new ToDoListDAO();
		transcriptDAO = new TranscriptDAO();
		learningUserBizSvc = new LearningUserBizSvc(onyxWrite,
				hdCatalogXmlRpcUrl, hdCatalogXmlRpcAccessKey,
				hdCatalogTranslateDefAccessTags);
		this.emailServerUrl = emailServerUrl;
	}

	public void enroll(int toDoListId, LearningUser user) {

		ToDoList toDoList = toDoListDAO.retrieve(toDoListId, 0);

		// Make a new ToDoListTranscript
		toDoListDAO.insertTranscript(toDoListId, user.getUserId());

		// Send Enroll email (if applicable)
		if (toDoList.getEnrollEmail().isSendEmail()) {
			//sendEmail(user, toDoList.getEnrollEmail());
		}

	}

	public void unenroll(int toDoListId, int userId) {
		toDoListDAO.deleteTranscript(userId, toDoListId);
	}

	public void syncUserAccessTags(LearningUser user) {
		if (toDoListDAO.existsAnyActiveTranscript(user.getUserId())) {
			List<String> accessTags = user.getAccessTags();
			toDoListDAO.saveTranscriptAccessTags(accessTags, user.getUserId());
		}
	}

	public void syncAllUsersToDoActivity() {
		List<Object[]> usernameAndIds = toDoListDAO
				.retrieveAllUsernamesAndIdsWithTranscripts();

		for (int i = 0; i < usernameAndIds.size(); i++) {
			if (i % 500 == 0) {
				System.out.println("***   syncAllUsersToDoActivity - " + i
						+ " - " + new Date().toString());
			}
			Object[] usernameAndId = usernameAndIds.get(i);
			String username = (String) usernameAndId[0];
			int userId = ((Integer) usernameAndId[1]).intValue();
			List<String> accessTags = learningUserBizSvc
					.retrieveAccessTags(username);
			toDoListDAO.saveTranscriptAccessTags(accessTags, userId);

			// It happens that almost everybody has at least one access tag, so
			// I'm adding this "size() > 0" check. Worst case, we end up with
			// somebody never getting checked nightly, but since this is a
			// nice-to-have, double-check feature, I'm not going to worry if it
			// skips somebody.
			if (accessTags.size() > 0 & userId != 0) {
				boolean closedSomething = checkForCompletion(userId, accessTags);
				if (closedSomething) {
					System.out.println("***      Closed To-Do List(s) for "
							+ username + " (" + userId + ")");
				}
			}
		}
	}

	public boolean checkForCompletion(LearningUser user) {
		return checkForCompletion(user.getUserId(), user.getAccessTags());
	}

	public boolean checkForCompletion(int userId, List<String> accessTags) {
		boolean closedSomething = false;
		Map<String, Transcript> transcriptMap = transcriptDAO
				.retrieveAllMap(userId);
		List<ToDoListTranscript> toDoTranscripts = toDoListDAO
				.retrieveCurrentTranscripts(userId);

		for (ToDoListTranscript toDoTranscript : toDoTranscripts) {
			int toDoListId = toDoTranscript.getToDoListId();

			// Retrieve it
			ToDoList toDoList = toDoListDAO.retrieveForUser(toDoListId, userId,
					accessTags, 0);

			// Check if it's done
			boolean isDone = true;
			boolean notEmpty = false;
			for (ToDoListSet set : toDoList.getListSets()) {
				for (String learningId : set.getLearningIds()) {
					notEmpty = true;
					Transcript transcript = transcriptMap.get(learningId);
					if (transcript == null
							|| transcript.getStatus() != Transcript.STATUS_DONE) {
						isDone = false;
					}
				}
			}

			// If so, run complete(), set return value to "true"
			if (isDone && notEmpty) {
				complete(toDoList, userId);
				closedSomething = true;
			}
		}
		return closedSomething;
	}

	public void markComplete(int toDoListId, LearningUser user) {
		ToDoList toDoList = toDoListDAO.retrieve(toDoListId, 0);
		complete(toDoList, user.getUserId());
	}

	private void complete(ToDoList toDoList, int userId) {
		// Close the ToDoListTranscript - Also adds any Cert Year entries
		toDoListDAO.closeTranscript(toDoList, userId);

		// When user finishes a re-certification to-do list for CI or MI a milestone needs to be written for RENEW
		if(toDoList.getCertProgram() != null && (toDoList.getCertProgram().equals(CI) || toDoList.getCertProgram().equals(MI)) && 
				(toDoList.getProgramType() != null && toDoList.getProgramType().equals(PROGRAM_TYPE))){
			String certProgram = toDoList.getCertProgram() + "renew";
			learningUserBizSvc.updateInstallerProgram(userId, certProgram);
		}

		// Send Complete email (if applicable)
		if (toDoList.getCompleteEmail().isSendEmail()) {
			LearningUser user = learningUserBizSvc.retrieveSync(userId);
			sendEmail(user, toDoList.getCompleteEmail());
		}

		// Auto-enroll in another To Do List?
		if (toDoList.getOnCompleteEnroll() > 0) {
			LearningUser user = learningUserBizSvc.retrieveSync(userId);
			enroll(toDoList.getOnCompleteEnroll(), user);
		}
	}

	private void sendEmail(LearningUser user, EmailTemplate emailTemplate) {

		if (emailServerUrl != null) {
			EmailObject email = new EmailObject();

			// To Address
			try {
				InternetAddress toAddress = new InternetAddress(user
						.getEmailAddress());
				email.addToAddress(toAddress);
			} catch (AddressException e) {
				System.err.println("Invalid \"To\" Address: "
						+ user.getEmailAddress());
				e.printStackTrace();
			}

			// From Address
			try {
				email.setFromAddress(new InternetAddress(emailTemplate
						.getFromAddress()));
			} catch (AddressException e) {
				e.printStackTrace();
			}

			// BCC Address(es)
			if (emailTemplate.getBccAddresses() != null) {
				try {
					InternetAddress[] bccAddresses = InternetAddress.parse(
							emailTemplate.getBccAddresses(), false);
					for (InternetAddress bcc : bccAddresses) {
						email.addBccAddress(bcc);
					}
				} catch (AddressException e) {
					System.err.println("Invalid \"BCC\" Addresses: "
							+ emailTemplate.getBccAddresses());
					e.printStackTrace();
				}
			}

			// Content
			email.setSubject(emailTemplate.getSubject());
			String text = mergeTemplateData(emailTemplate.getTextMessage(),
					user);
			email.setText(text);
			String html = mergeTemplateData(emailTemplate.getHtmlMessage(),
					user);
			email.setHtml(html);

			EmailHandlerBizSvc emailHandlerBizSvc = new EmailHandlerBizSvc();
			emailHandlerBizSvc.setSmtpServerName(emailServerUrl);

			boolean emailSent = emailHandlerBizSvc.sendMail(email);
			if (!emailSent) {
				System.err.println("Email failed to send to "
						+ user.getEmailAddress() + " - Subject: "
						+ emailTemplate.getSubject());
			}
		} else {
			System.out.println();
			System.out.println("Learning Center not configured for email:");
			System.out.println("To:      " + user.getEmailAddress());
			System.out.println("Subject: " + emailTemplate.getSubject());
			String text = mergeTemplateData(emailTemplate.getTextMessage(),
					user);
			System.out.println(text);
			System.out.println();
		}
	}

	private String mergeTemplateData(String template, LearningUser user) {
		if (template == null) {
			return null;
		} else {
			String message = template.replaceAll("<<FirstName>>", user
					.getFirstName());
			message = message.replaceAll("<<LastName>>", user.getLastName());
			message = message.replaceAll("<<CompanyName>>", user
					.getCompanyName());
			return message;
		}
	}
	
	
}

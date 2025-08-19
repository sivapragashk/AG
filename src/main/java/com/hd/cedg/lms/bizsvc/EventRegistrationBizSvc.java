package com.hd.cedg.lms.bizsvc;

import java.util.List;

import java.util.Map;
import java.util.Random;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import com.hd.cedg.lms.dao.EventRegistrationDAO;
import com.hd.cedg.lms.model.DateRange;
import com.hd.cedg.lms.model.Event;
import com.hd.cedg.lms.model.EventDisplayGroup;
import com.hd.cedg.lms.model.EventDisplayGroupItem;
import com.hd.cedg.lms.model.EventOption;
import com.hd.cedg.lms.model.EventOptionPackage;
import com.hd.cedg.lms.model.EventPaymentMethod;
import com.hd.cedg.lms.model.EventPaymentMethodDetail;
import com.hd.cedg.lms.model.EventRegistration;
import com.hd.cedg.lms.model.PaypalCredentials;
import com.hd.mis.mailer.EmailHandlerBizSvc;
import com.hd.mis.mailer.EmailObject;
import com.hd.mis.paypal.bizsvc.PaypalTransactionBizSvc;
import com.hd.mis.paypal.model.PaypalTransaction;
import com.hd.mis.paypal.model.PaypalTransactionResponse;
import com.hd.mis.paypal.servlet.PaymentFormServlet;

public class EventRegistrationBizSvc {

	private EventRegistrationDAO eventRegistrationDAO;
	private String mailServer;
	private PaypalCredentials paypalCredentials;

	public EventRegistrationBizSvc(String mailServer,
			PaypalCredentials paypalCredentials) {
		eventRegistrationDAO = new EventRegistrationDAO();
		this.mailServer = mailServer;
		this.paypalCredentials = paypalCredentials;
	}

	public boolean register(EventRegistration registration) {
		String confirmationNumber = "";

		// Charge Credit Card (if applicable)
		if (registration.getPaymentMethodId() == EventPaymentMethod.PAYMENT_ID_CC) {
			confirmationNumber = chargeCreditCard(registration);
			if ("".equals(confirmationNumber)) {
				return false;
			}
		}

		// Assign Confirmation Number
		if (confirmationNumber.length() == 0) {
			confirmationNumber = generateConfirmationNumber();
		}
		registration.setConfirmationNumber(confirmationNumber);

		// Save to the DB
		eventRegistrationDAO.save(registration);

		// Send Confirmation Email (if Event is open)
		if (registration.getEvent().getStatus() == Event.STATUS_OPEN) {
			sendConfirmationEmail(registration, 1);
		}

		return true;
	}

	public boolean register(List<EventRegistration> registrations) {
		if (registrations.size() > 0) {
			String confirmationNumber = "";

			// Charge Credit Card (if applicable)
			if (registrations.get(0).getPaymentMethodId() == EventPaymentMethod.PAYMENT_ID_CC) {
				confirmationNumber = chargeCreditCard(registrations);
				if ("".equals(confirmationNumber)) {
					return false;
				}
			}

			for (EventRegistration registration : registrations) {
				// Assign Confirmation Number
				if (confirmationNumber.length() == 0) {
					registration
							.setConfirmationNumber(generateConfirmationNumber());
				} else {
					registration.setConfirmationNumber(confirmationNumber);
				}

				// Save to the DB
				eventRegistrationDAO.save(registration);

				// Send Confirmation Email (if Event is open)
				if (registration.getEvent().getStatus() == Event.STATUS_OPEN) {
					sendConfirmationEmail(registration, registrations.size());
				}
			}

			return true;
		} else {
			return false;
		}
	}

	private String chargeCreditCard(EventRegistration registration) {
		PaypalTransaction paypalTransaction = new PaypalTransaction();

		Map<Integer, String> detailMap = registration
				.getPaymentMethodDetailAnswers();

		paypalTransaction.setBillingCcNumber(detailMap
				.get(EventPaymentMethodDetail.DETAIL_ID_CC_NUMBER));
		paypalTransaction.setBillingExpDate(detailMap
				.get(EventPaymentMethodDetail.DETAIL_ID_EXP_DATE));
		paypalTransaction.setBillingCsc(detailMap
				.get(EventPaymentMethodDetail.DETAIL_ID_CSC));
		paypalTransaction.setBillingName(detailMap
				.get(EventPaymentMethodDetail.DETAIL_ID_NAME));
		paypalTransaction.setBillingAddress(detailMap
				.get(EventPaymentMethodDetail.DETAIL_ID_ADDRESS));
		paypalTransaction.setBillingCity(detailMap
				.get(EventPaymentMethodDetail.DETAIL_ID_CITY));
		paypalTransaction.setBillingRegion(detailMap
				.get(EventPaymentMethodDetail.DETAIL_ID_REGION));
		paypalTransaction.setBillingPostCode(detailMap
				.get(EventPaymentMethodDetail.DETAIL_ID_POSTCODE));

		paypalTransaction.setComment1("Learning Center: Event");
		String eventLabel = registration.getEvent().getTitle()
				+ ": "
				+ registration.getEvent().getVenue().getCity()
				+ ", "
				+ registration.getEvent().getVenue().getRegion()
				+ ",  "
				+ registration.getEvent().getDateRange()
						.getFormattedDateRange();
		if (eventLabel.length() > 128) {
			eventLabel = eventLabel.substring(0, 128);
		}
		paypalTransaction.setComment2(eventLabel);

		String username = paypalCredentials.getUsername();
		String vendor = paypalCredentials.getVendor();
		String partner = paypalCredentials.getPartner();
		String password = paypalCredentials.getPassword();
		String hostAddress = paypalCredentials.getHostAddress();

		double amount = Double.parseDouble(registration.getTotalCost()
				.getFormattedCost(false));
		paypalTransaction.setAmount(amount);

		System.out.println();
		System.out
				.println("***   LEARNING CENTER: ATTEMPTING PAYMENT TO PAYPAL ***");
		System.out.println("***   User " + registration.getRegisteringUserId()
				+ " is registering \"" + registration.getFirstName() + " "
				+ registration.getLastName() + "\" ("
				+ registration.getUserId() + ") for " + eventLabel);
		System.out.println("***   Host Address: " + hostAddress);

		PaypalTransactionBizSvc paypalTransactionBizSvc = new PaypalTransactionBizSvc(
				username, vendor, partner, password, hostAddress);
		PaypalTransactionResponse paypalTransactionResponse = paypalTransactionBizSvc
				.process(paypalTransaction);
		int resultCode = paypalTransactionResponse.getResultCode();

		System.out.println("***   Transaction ID: "
				+ paypalTransactionResponse.getTransactionId());
		System.out.println("***   Result Code: "
				+ paypalTransactionResponse.getResultCode());
		System.out.println("***   Response Message: "
				+ paypalTransactionResponse.getResponseMessage());
		System.out.println("***   User Message: "
				+ paypalTransactionResponse.getUserMessage());
		System.out.println();

		if (resultCode == 0) {
			return paypalTransactionResponse.getTransactionId();
		} else {
			return "";
		}
	}

	private String chargeCreditCard(List<EventRegistration> registrations) {
		for (EventRegistration registration : registrations) {
			if (registration.getPaymentMethodId() != EventPaymentMethod.PAYMENT_ID_CC) {
				return "";
			}
		}

		PaypalTransaction paypalTransaction = new PaypalTransaction();

		EventRegistration firstReg = registrations.get(0);

		Map<Integer, String> detailMap = firstReg
				.getPaymentMethodDetailAnswers();

		paypalTransaction.setBillingCcNumber(detailMap
				.get(EventPaymentMethodDetail.DETAIL_ID_CC_NUMBER));
		paypalTransaction.setBillingExpDate(detailMap
				.get(EventPaymentMethodDetail.DETAIL_ID_EXP_DATE));
		paypalTransaction.setBillingCsc(detailMap
				.get(EventPaymentMethodDetail.DETAIL_ID_CSC));
		paypalTransaction.setBillingName(detailMap
				.get(EventPaymentMethodDetail.DETAIL_ID_NAME));
		paypalTransaction.setBillingAddress(detailMap
				.get(EventPaymentMethodDetail.DETAIL_ID_ADDRESS));
		paypalTransaction.setBillingCity(detailMap
				.get(EventPaymentMethodDetail.DETAIL_ID_CITY));
		paypalTransaction.setBillingRegion(detailMap
				.get(EventPaymentMethodDetail.DETAIL_ID_REGION));
		paypalTransaction.setBillingPostCode(detailMap
				.get(EventPaymentMethodDetail.DETAIL_ID_POSTCODE));

		paypalTransaction.setComment1("Learning Center: Event");
		String eventLabel = firstReg.getEvent().getTitle() + ": "
				+ firstReg.getEvent().getVenue().getCity() + ", "
				+ firstReg.getEvent().getVenue().getRegion() + ",  "
				+ firstReg.getEvent().getDateRange().getFormattedDateRange();
		if (eventLabel.length() > 128) {
			eventLabel = eventLabel.substring(0, 128);
		}
		paypalTransaction.setComment2(eventLabel);

		String username = paypalCredentials.getUsername();
		String vendor = paypalCredentials.getVendor();
		String partner = paypalCredentials.getPartner();
		String password = paypalCredentials.getPassword();
		String hostAddress = paypalCredentials.getHostAddress();

		double amount = 0.0;
		for (EventRegistration registration : registrations) {
			amount += Double.parseDouble(registration.getTotalCost()
					.getFormattedCost(false));
		}
		paypalTransaction.setAmount(amount);

		System.out.println();
		System.out
				.println("***   LEARNING CENTER: ATTEMPTING PAYMENT TO PAYPAL ***");
		if (registrations.size() == 1) {
			System.out.println("***   User " + firstReg.getRegisteringUserId()
					+ " is registering \"" + firstReg.getFirstName() + " "
					+ firstReg.getLastName() + "\" (" + firstReg.getUserId()
					+ ") for " + eventLabel);
		} else {
			String userIds = "";
			for (EventRegistration registration : registrations) {
				if (registration != registrations.get(0)) {
					userIds += ", ";
				}
				userIds += registration.getUserId();
			}
			System.out.println("***   User " + firstReg.getRegisteringUserId()
					+ " is registering " + registrations.size() + " users ("
					+ userIds + ") for " + eventLabel);
		}
		System.out.println("***   Host Address: " + hostAddress);

		PaypalTransactionBizSvc paypalTransactionBizSvc = new PaypalTransactionBizSvc(
				username, vendor, partner, password, hostAddress);
		PaypalTransactionResponse paypalTransactionResponse = paypalTransactionBizSvc
				.process(paypalTransaction);
		int resultCode = paypalTransactionResponse.getResultCode();

		System.out.println("***   Transaction ID: "
				+ paypalTransactionResponse.getTransactionId());
		System.out.println("***   Result Code: "
				+ paypalTransactionResponse.getResultCode());
		System.out.println("***   Response Message: "
				+ paypalTransactionResponse.getResponseMessage());
		System.out.println("***   User Message: "
				+ paypalTransactionResponse.getUserMessage());
		System.out.println();

		if (resultCode == 0) {
			return paypalTransactionResponse.getTransactionId();
		} else {
			return "";
		}
	}

	private String generateConfirmationNumber() {
		Random r = new Random();
		String confirmationNumber = "HD";
		for (int i = 0; i < 10; i++) {
			confirmationNumber += r.nextInt(10);
		}
		return confirmationNumber;
	}

	private void sendConfirmationEmail(EventRegistration registration,
			int numInBatch) {
		if (mailServer != null && !"".equals(mailServer)) {
			if ((registration.getEvent().getConfEmailText() == null || ""
					.equals(registration.getEvent().getConfEmailText()))
					&& (registration.getEvent().getConfEmailHtml() == null || ""
							.equals(registration.getEvent().getConfEmailHtml()))) {
				return;
			}

			EmailObject email = new EmailObject();

			// To Address
			try {
				InternetAddress toAddress = new InternetAddress(registration
						.getEmail());
				email.addToAddress(toAddress);
			} catch (AddressException e) {
				System.err.println("Invalid \"To\" Address: "
						+ registration.getEmail());
				e.printStackTrace();
			}

			// From Address
			try {
				// learning@hunterdouglas.com
				email.setFromAddress(new InternetAddress(registration
						.getEvent().getConfEmailFrom()));
			} catch (AddressException e) {
				e.printStackTrace();
				try {
					email.setFromAddress(new InternetAddress(
							"learning@hunterdouglas.com"));
				} catch (AddressException e2) {
				}
			}

			// BCC Address(es)
			if (registration.getEvent().getConfEmailBcc() != null) {
				try {
					InternetAddress[] bccAddresses = InternetAddress.parse(
							registration.getEvent().getConfEmailBcc(), false);
					for (InternetAddress bcc : bccAddresses) {
						email.addBccAddress(bcc);
					}
				} catch (AddressException e1) {
					System.err.println("Invalid \"BCC\" Addresses: "
							+ registration.getEvent().getConfEmailBcc());
					e1.printStackTrace();
				}
			}

			// Content
			email.setSubject(registration.getEvent().getConfEmailSubject());
			if (registration.getEvent().getConfEmailText() != null
					&& !"".equals(registration.getEvent().getConfEmailText())) {
				String text = generateText(registration, numInBatch);
				email.setText(text);
			}
			if (registration.getEvent().getConfEmailHtml() != null
					&& !"".equals(registration.getEvent().getConfEmailHtml())) {
				String html = generateHtml(registration, numInBatch);
				email.setHtml(html);
			}

			EmailHandlerBizSvc emailHandlerBizSvc = new EmailHandlerBizSvc();
			// webext01.hunterdouglas.com
			emailHandlerBizSvc.setSmtpServerName(mailServer);

			boolean emailSent = emailHandlerBizSvc.sendMail(email);
			if (!emailSent) {
				System.err.println("Confirmation Email failed to send to "
						+ registration.getEmail() + " - Registration ID "
						+ registration.getRegistrationId());
			}
		} else {
			System.out.println();
			System.out.println("Learning Center not configured for email:");
			System.out.println("To:      " + registration.getEmail());
			System.out.println("Subject: "
					+ registration.getEvent().getConfEmailSubject());
			System.out.println(generateText(registration, numInBatch));
			System.out.println();
		}
	}

	private String generateText(EventRegistration registration, int numInBatch) {
		Event event = registration.getEvent();
		String text = "<div style=\"font-family:Helvetica,Arial,Sans-serif;font-size:8pt;color:#404040;\">Dear " + registration.getFirstName() + ",</div>";
						
		text += event.getConfEmailText() + "\n\n\n";
		text += "<div style=\"font-family:Helvetica,Arial,Sans-serif;font-size:8pt;color:#404040;\">";
		text += "Registration Summary:\n\n";
		text += "Attendee Name: "+registration.getFirstName() + " " +registration.getLastName() + "\n";
		text += "Attendee Company: "+registration.getCompanyName() + "\n\n";
		if (event.getInPerson() == Event.IN_PERSON || event.getInPerson() == Event.IN_PERSON_TRAINING) {
			text += event.getTitle() + "\n";
			text += event.getVenue().getName() + "\n";
			text += event.getVenue().getAddress1() + "\n";
			if (event.getVenue().getAddress2() != null) {
				text += event.getVenue().getAddress2() + "\n";
			}
			text += event.getVenue().getCity() + ", "
					+ event.getVenue().getRegion() + " "
					+ event.getVenue().getPostCode() + "\n\n";
		}
		if (!"".equals(event.getDateRange().getFormattedDateRange())) {
			text += "Date: " + event.getDateRange().getFormattedDateRange()
					+ "\n";
		}
		if (!"".equals(event.getDateRange().getFormattedTime())) {
			String timeString = "";
			if (event.getInPerson() == Event.IN_PERSON || event.getInPerson() == Event.IN_PERSON_TRAINING) {
				timeString = event.getDateRange().getFormattedTime();
			} else {
				boolean inDs = event.getDateRange().isInDaylightSavings();
				timeString = event.getDateRange().getFormattedTime()
						+ (inDs ? " EDT" : " EST") + "\n      ("
						+ event.getDateRange().getFormattedOffsetTime(-3)
						+ (inDs ? " PDT" : " PST") + ", "
						+ event.getDateRange().getFormattedOffsetTime(-2)
						+ (inDs ? " MDT" : " MST") + ", "
						+ event.getDateRange().getFormattedOffsetTime(-1)
						+ (inDs ? " CDT" : " CST") + ")";
			}
			text += "Time: " + timeString + "\n";
		}
		text += "\n";
		if (registration.getSelectedOptionIds().size() > 0
				|| registration.getSelectedPackageIds().size() > 0) {
			text += "You have been registered for:\n\n";
			for (EventDisplayGroup group : event.getEventDisplayGroups()) {
				for (EventDisplayGroupItem item : group.getItems()) {
					String name = "";
					String date = "";
					String time = "";
					if (item.getItemType() == Event.TYPE_ID_OPTION) {
						if (registration.getSelectedOptionIds().contains(
								item.getItemId())) {
							for (EventOption option : event.getEventOptions()) {
								if (option.getEventOptionId() == item
										.getItemId()) {
									name = option.getTitle();
									date = option.getDateRange()
											.getFormattedDateRange();
									time = option.getDateRange()
											.getFormattedTime();
									break;
								}
							}
						}
					} else if (item.getItemType() == Event.TYPE_ID_PACKAGE) {
						if (registration.getSelectedPackageIds().contains(
								item.getItemId())) {
							for (EventOptionPackage aPackage : event
									.getEventOptionPackages()) {
								if (aPackage.getPackageId() == item.getItemId()) {
									name = aPackage.getTitle();
									DateRange dateRange = aPackage
											.getDateRange(event
													.getEventOptions());
									date = dateRange.getFormattedDateRange();
									time = dateRange.getFormattedTime();
									break;
								}
							}
						}
					}
					if (!"".equals(name)) {
						text += name + "\n";
						if (!"".equals(date)) {
							text += date + "\n";
						}
						if (!"".equals(time)) {
							text += time + "\n";
						}
					}
				}
			}
			text += "\n";
		}
		if (registration.getPaymentMethodId() == EventPaymentMethod.PAYMENT_ID_NOCHARGE) {
			text += "Total Cost: $0.00\n";
		}else{
			text += "Total Cost: "+  registration.getTotalCost().getFormattedCost() + "\n";
		}
		text += "Confirmation #: " + registration.getConfirmationNumber()
				+ "\n";
		if (numInBatch > 1) {
			text += "(Note: Your order contained "
					+ numInBatch
					+ " registrations, which were processed as a single charge.)\n";
		}
		text += "</div>";
		return text;
	}

	private String generateHtml(EventRegistration registration, int numInBatch) {
		Event event = registration.getEvent();
		String html = "<div style=\"font-family:Helvetica,Arial,Sans-serif;font-size:8pt;color:#404040;\">Dear " + registration.getFirstName() + ",</div>";
		html += "<p>" + event.getConfEmailHtml() + "</p>";
		html += "<br>";
		html += "<div style=\"font-family:Helvetica,Arial,Sans-serif;font-size:8pt;color:#404040;\">";
		html += "<strong>Registration Summary:</strong><br><br>";
		html += "Attendee Name: "+registration.getFirstName() + " " +registration.getLastName() + "<br>";
		html += "Attendee Company: "+registration.getCompanyName() + "<br><br>";
		if (event.getInPerson() == Event.IN_PERSON || event.getInPerson() == Event.IN_PERSON_TRAINING) {
			html += event.getTitle() + "<br>";
			html += event.getVenue().getName() + "<br>";
			html += event.getVenue().getAddress1() + "<br>";
			if (event.getVenue().getAddress2() != null) {
				html += event.getVenue().getAddress2() + "<br>";
			}
			html += event.getVenue().getCity() + ", "
					+ event.getVenue().getRegion() + " "
					+ event.getVenue().getPostCode() + "<br><br>";
		}
		if (!"".equals(event.getDateRange().getFormattedDateRange())) {
			html += "Date: " + event.getDateRange().getFormattedDateRange()
					+ "<br>";
		}
		if (!"".equals(event.getDateRange().getFormattedTime())) {
			String timeString = "";
			if (event.getInPerson() == Event.IN_PERSON || event.getInPerson() == Event.IN_PERSON_TRAINING) {
				timeString = event.getDateRange().getFormattedTime();
			} else {
				boolean inDs = event.getDateRange().isInDaylightSavings();
				timeString = event.getDateRange().getFormattedTime()
						+ (inDs ? " EDT" : " EST") + "<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;("
						+ event.getDateRange().getFormattedOffsetTime(-3)
						+ (inDs ? " PDT" : " PST") + ", "
						+ event.getDateRange().getFormattedOffsetTime(-2)
						+ (inDs ? " MDT" : " MST") + ", "
						+ event.getDateRange().getFormattedOffsetTime(-1)
						+ (inDs ? " CDT" : " CST") + ")";
			}
			html += "Time: " + timeString + "<br>";
		}
		html += "</p>";
		if (registration.getSelectedOptionIds().size() > 0
				|| registration.getSelectedPackageIds().size() > 0) {
			html += "<p>You have been registered for:</p>";
			html += "<table style=\"border: 1px solid black\" cellpadding=\"8\">";
			for (EventDisplayGroup group : event.getEventDisplayGroups()) {
				for (EventDisplayGroupItem item : group.getItems()) {
					String name = "";
					String date = "";
					String time = "";
					if (item.getItemType() == Event.TYPE_ID_OPTION) {
						if (registration.getSelectedOptionIds().contains(
								item.getItemId())) {
							for (EventOption option : event.getEventOptions()) {
								if (option.getEventOptionId() == item
										.getItemId()) {
									name = option.getTitle();
									date = option.getDateRange()
											.getFormattedDateRange();
									time = option.getDateRange()
											.getFormattedTime();
									break;
								}
							}
						}
					} else if (item.getItemType() == Event.TYPE_ID_PACKAGE) {
						if (registration.getSelectedPackageIds().contains(
								item.getItemId())) {
							for (EventOptionPackage aPackage : event
									.getEventOptionPackages()) {
								if (aPackage.getPackageId() == item.getItemId()) {
									name = aPackage.getTitle();
									DateRange dateRange = aPackage
											.getDateRange(event
													.getEventOptions());
									date = dateRange.getFormattedDateRange();
									time = dateRange.getFormattedTime();
									break;
								}
							}
						}
					}
					if (!"".equals(name)) {
						html += "<tr>";
						html += "<td>" + name + "</td>";
						html += "<td nowrap>" + date + "</td>";
						html += "<td nowrap>" + time + "</td>";
						html += "</tr>";
					}
				}
			}
			html += "</table>";
		}
		html += "<p>";
		//if (registration.getTotalCost().getCost() != 0) {
			html += "Total Cost: "
					+ registration.getTotalCost().getFormattedCost() + "<br>";
		//}
		html += "Confirmation #: " + registration.getConfirmationNumber();
		if (numInBatch > 1) {
			html += "<br>(Note: Your order contained "
					+ numInBatch
					+ " registrations, which were processed as a single charge.)";
		}
		html += "</p>";
		html += "</div>";
		return html;
	}
}

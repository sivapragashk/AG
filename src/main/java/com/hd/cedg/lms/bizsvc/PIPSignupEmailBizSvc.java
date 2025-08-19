package com.hd.cedg.lms.bizsvc;

import java.util.Map;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import com.hd.cedg.lms.model.LearningUser;
import com.hd.mis.mailer.EmailHandlerBizSvc;
import com.hd.mis.mailer.EmailObject;

public class PIPSignupEmailBizSvc {

	private String mailServer;
	private String nonPIPMemberAccessEmail;
	LearningUser user;
	public PIPSignupEmailBizSvc(Map<String, Object> params){
		mailServer = (String) params.get("emailServerUrl");
		nonPIPMemberAccessEmail = (String) params.get("nonPIPMemberAccessEmail");
		user = (LearningUser) params.get("user");
	}
	public void sendPIPSignUpEmails(){
		sendWelcomeEmailOnSignup();
		nonMemberAccessEmailToAdmin(true);
	}
	public void sendPIPSignUpEmailsOnCIRegistration(String toemail){
		sendWelcomeEmailOnCIRegistration(toemail);
		nonMemberAccessEmailToAdmin(false);
	}
	private void sendWelcomeEmailOnCIRegistration(String toemail) {
		EmailObject email = new EmailObject();
		try{
			email.addToAddress(new InternetAddress(toemail));
		}catch(Exception ex){
			System.out.println("Invalid \"To\" Address: "+toemail+" - "+ex);
		}
		try {
			email.setFromAddress(new InternetAddress("installer.program@hunterdouglas.com"));
		} catch (AddressException e) {
			System.out.println("Invalid \"From\" Address: installer.program@hunterdouglas.com - "+e);
		}
		email.setSubject("Welcome to the Hunter Douglas Professional Installer Program - CI registration");
		StringBuffer htmlmessage = new StringBuffer();
		htmlmessage.append("<span style=\"font-family: Arial; font-size: 10pt; \">Hello,<br/><br/>");
		htmlmessage.append("You recently registered for the Hunter Douglas Certified Installer Training Program.  By registering for this event, you are now enrolled in our Professional Installer Program (PIP)! ");
		htmlmessage.append("We are excited to have you join our network of installers and hope you take advantage of all the valuable resources Hunter Douglas has to offer. ");
		htmlmessage.append("A major benefit of PIP membership is our website, PIP Online. To access the website, go to <a href=\"https://my.hunterdouglas.com\">https://my.hunterdouglas.com.</a><br/><br/>");
		htmlmessage.append("<span style=\"padding-left:40px;\">As a member of PIP, you will have the ability to:</span><br/>");
		htmlmessage.append("<ul><li>Update your profile information</li>");
		htmlmessage.append("<li>Provide quality information about our products by using the Installation Registrations system</li>");
		htmlmessage.append("<li>View a large library of training videos</li>");
		htmlmessage.append("<li>Receive Technically Speaking - electronic newsletter</li></ul>");
		htmlmessage.append("Attached to this email you will find more information regarding the program and learn about your three step process to becoming a Hunter Douglas Master Installer.  ");
		htmlmessage.append("If you have any questions, please e-mail us at <a href=\"mailto:installer.program@hunterdouglas.com\">installer.program@hunterdouglas.com.</a>  We are excited to have you as part of our award winning Professional Installer Program and hope that you explore all the resources PIP has to offer!<br/><br/>");
		htmlmessage.append("Regards,<br/>");
		htmlmessage.append("The Professional Installer Team");
		htmlmessage.append("</span>");
		email.setText(null);
		email.setHtml(htmlmessage.toString());
		email.setAttachment("/home/ecom/lc/documents/Professional_to_MasterInstaller.pdf");
		email.setFilename("Professional_to_MasterInstaller.pdf");
		sendEmail(email);
	}
	
	private void sendWelcomeEmailOnSignup() {
		EmailObject email = new EmailObject();
		try{
			email.addToAddress(new InternetAddress(user.getEmailAddress()));
		}catch(Exception ex){
			System.out.println("Invalid \"To\" Address: "+user.getEmailAddress()+" - "+ex);
		}
		try {
			email.setFromAddress(new InternetAddress("installer.program@hunterdouglas.com"));
		} catch (AddressException e) {
			System.out.println("Invalid \"From\" Address: installer.program@hunterdouglas.com - "+e);
		}
		email.setSubject("Welcome to the Hunter Douglas Professional Installer Program");
		StringBuffer htmlmessage = new StringBuffer();
		htmlmessage.append("<span style=\"font-family: Arial; font-size: 10pt; \">Hello,<br/><br/>");
		htmlmessage.append("Welcome to the Professional Installer Program (PIP)!  We are excited that you have joined our team and hope you take advantage of all the valuable resources Hunter Douglas has to offer.  A major benefit of PIP membership is our website, PIP Online.  To access the website, go to <a href=\"https://my.hunterdouglas.com\">https://my.hunterdouglas.com.</a><br/><br/>");
		htmlmessage.append("As a member of PIP, you will have the ability to:<br/>");
		htmlmessage.append("<ul><li>Update your profile information</li>");
		htmlmessage.append("<li>Provide quality information about our products by using the Installation Registrations system</li>");
		htmlmessage.append("<li>View a large library of training videos</li>");
		htmlmessage.append("<li>Receive Technically Speaking - electronic newsletter</li></ul>");
		htmlmessage.append("Attached to this email you will find more information regarding the program and learn about your three step process to becoming a Hunter Douglas Master Installer.  If you have any questions, please e-mail us at <a href=\"mailto:installer.program@hunterdouglas.com\">installer.program@hunterdouglas.com.</a>  We are excited to have you as part of our award winning Professional Installer Program and hope that you explore all the resources PIP has to offer!<br/><br/>");
		htmlmessage.append("Regards,<br/>");
		htmlmessage.append("The Professional Installer Team");
		htmlmessage.append("</span>");
		email.setText(null);
		email.setHtml(htmlmessage.toString());
		email.setAttachment("/home/ecom/lc/documents/Professional_to_MasterInstaller.pdf");
		email.setFilename("Professional_to_MasterInstaller.pdf");
		sendEmail(email);
	}
	
	private void nonMemberAccessEmailToAdmin(boolean pipSignup) {
		EmailObject email = new EmailObject();
		try{
			email.addToAddress(new InternetAddress(nonPIPMemberAccessEmail));
		}catch(Exception ex){
			System.out.println("Invalid \"To\" Address: "+nonPIPMemberAccessEmail+" - "+ex);
		}
		try {
			email.setFromAddress(new InternetAddress(user.getEmailAddress()));
		} catch (AddressException e) {
			System.out.println("Invalid \"From\" Address: "+user.getEmailAddress()+" - "+e);
		}
		if(pipSignup){
			email.setSubject("PIP Access for non PIP Member");
		}else{
			email.setSubject("PIP Access for non PIP Member - CI registration");
		}
		StringBuffer htmlmessage = new StringBuffer();
		htmlmessage.append("<span style=\"font-family: VERDANA; font-size: 9pt; \"><b>User Details</b><br/><br/>");
		htmlmessage.append("OnyxID:"+user.getUserId()+"<br/>");
		htmlmessage.append("Username:"+user.getFirstName()+ "&nbsp;"+ user.getLastName()+"<br/>");
		htmlmessage.append("Company:"+user.getCompanyName()+"<br/>");
		htmlmessage.append("Address:"+user.getAddress1()+"<br/>");
		htmlmessage.append("        "+user.getCity()+"&nbsp;"+user.getRegion()+"&nbsp;&nbsp;"+getFormatedPostalCode(user.getPostCode())+"<br/>");
		htmlmessage.append("Phone Number:"+getFormatedPhoneNumber(user.getPhone())+"<br/>");
		htmlmessage.append("Cell Phone:"+getFormatedPhoneNumber(user.getCellPhone())+"<br/>");
		htmlmessage.append("<a href=\"mailto:"+user.getEmailAddress()+"\">Email:"+user.getEmailAddress()+"</a><br/><br/>");
		htmlmessage.append("</span>");
		
		email.setText(null);
		email.setHtml(htmlmessage.toString());
		sendEmail(email);
	}

	private void sendEmail(EmailObject email) {
		if (mailServer != null) {
			EmailHandlerBizSvc emailHandlerBizSvc = new EmailHandlerBizSvc();
			emailHandlerBizSvc.setSmtpServerName(mailServer);
			boolean emailSent = emailHandlerBizSvc.sendMail(email);
			if (!emailSent) {
				System.err.println("Failed to send email - Subject: "	+ email.getSubject());
			}
		} else {
			System.out.println();
			System.out.println("Learning Center not configured for email:");
			System.out.println("Subject: " + email.getSubject());
			System.out.println();
		}
	}
	
	public static String getFormatedPhoneNumber(String phoneNumber)
	{
		String displayPhoneNumber = "";
		if (phoneNumber != null)
	   	{
	   	  if (phoneNumber.length() < 10)
	   		  displayPhoneNumber = phoneNumber;
	   	  else if (phoneNumber.length() == 10)
	   		  displayPhoneNumber = phoneNumber.substring(0, 3) + "-" + phoneNumber.substring(3, 6) + "-" + phoneNumber.substring(6);
	   	  else
	   		  displayPhoneNumber = phoneNumber.substring(0, 3) + "-" + phoneNumber.substring(3, 6) + "-" + phoneNumber.substring(6, 10) + "  ext. " + phoneNumber.substring(10);
	   	 }
		return displayPhoneNumber;
	}
	
	public static String getFormatedPostalCode(String postalCode)
	{
		String displayPostalCode = "";
		if (postalCode != null)
        {
          if (postalCode.length() != 9)
          	displayPostalCode = postalCode;
          else
          	displayPostalCode = postalCode.substring(0, 5) + "-" + postalCode.substring(5);
        }
		return displayPostalCode;
	}
}

<%@ page import="java.util.List,
                 java.util.Map,
                 com.hd.cedg.lms.model.LearningUser,
                 com.hd.cedg.lms.model.Event,
                 com.hd.cedg.lms.model.EventRegistration,
                 com.hd.cedg.lms.servlet.EventRegisterControlServlet,
                 com.hd.cedg.lms.model.EventPaymentMethod;"
         errorPage="../error.jsp" buffer="300kb"%><%
LearningUser user = (LearningUser) request.getAttribute("user");        
EventRegistration registration = (EventRegistration) request.getAttribute("registration");
Event event = registration.getEvent();

String formObjectSerialized = (String)request.getAttribute(EventRegisterControlServlet.FORM_OBJECT_NAME);
Map<String, String> errors = (Map<String, String>)request.getAttribute("errors");

Map<String, Object> registrationTab = registration.getRegistrationTabs();
%>

<jsp:include page="/common/header.jsp" flush="true" />
<link href="style/certifications.css" type="text/css" rel="stylesheet">
<script type="text/javascript">
function submitRegister() {
	document.register.submit();
}
</script>

<div id="navigations">
	<div id="standard-nav">
		<a href="#" onclick="#"><span id="home"></span></a>
		<a href="#" onclick="#"><span id="certifications"></span></a>
	</div>	
	<div id="cert-levels">
		<a href="#" onclick="#" id="cert">
			<br/><span id="certificate">Installer Program</span><br/>
			<span id="level"><%=user.getPipLevel()%></span>
		</a>
	</div>
</div>
<div id="cert-details">
	<div id="left-nav"></div>
		<div id="nav-tabs">
				<div id="selected-tab">Company</div>
				<div id="selected-tab">User</div>
				<div id="selected-tab">Attendee</div>
				<% if(Boolean.TRUE.equals(registrationTab.get("hasOptions"))){  %>
					  <div id="tabs">Options</div>					   
				<% } %>
				<% if(Boolean.TRUE.equals(registrationTab.get("hasQuestions"))){  %>
				<div id="tabs">Additional Info</div>					   
				<% } %>	
				<% if(Boolean.TRUE.equals(registrationTab.get("hasPayment"))){  %>
				<div id="tabs">Payment</div>					   
				<% } %>				
				<div id="tabs">Confirm</div>
				
			</div>
	
</div>


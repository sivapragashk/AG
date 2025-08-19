<%@ page import="java.util.List,
                 java.util.Map,
                 com.hd.cedg.lms.model.LearningUser,
                 com.hd.cedg.lms.model.Event,
                 com.hd.cedg.lms.model.EventDisplayGroup,
                 com.hd.cedg.lms.model.EventDisplayGroupItem,
                 com.hd.cedg.lms.model.EventOption,
                 com.hd.cedg.lms.model.EventOptionPackage,
                 com.hd.cedg.lms.model.EventRegistration,
                 com.hd.cedg.lms.servlet.EventRegisterControlServlet"
         errorPage="../error.jsp" buffer="300kb"%><%
LearningUser user = (LearningUser) request.getAttribute("user");
EventRegistration registration = (EventRegistration) request.getAttribute("registration");
Event event = registration.getEvent();

String formObjectSerialized = (String)request.getAttribute(EventRegisterControlServlet.FORM_OBJECT_NAME);
Map<String, String> errors = (Map<String, String>)request.getAttribute("errors");
boolean hasOptions = (event.getEventDisplayGroups().size() > 0) ? true : false;
String next = "Next";
boolean hasQuestions = (registration.getQuestionsForCurrentSelections().size() > 0 ) ? true : false;
/* hasPayment */
boolean hasPayment = false;
if (registration.getEvent().isAllowCC()) {
	hasPayment = true;
} else {
	int size = 0;
	for (EventPaymentMethod pay : registration.getEvent().getPaymentMethods()) {
		if (!pay.isAdminOnly()) {
			size++;
		}
	}
	if (size > 1) {
		hasPayment = true;
	} else if (size == 1) {
		for (EventPaymentMethod pay : registration.getEvent().getPaymentMethods()) {
			if (!pay.isAdminOnly() && pay.getPaymentMethodDetails().size() > 0) {
				hasPayment = true;
				break;
			}
		}
	}
}
%>

<%@page import="com.hd.cedg.lms.model.EventOption"%>
<%@page import="com.hd.cedg.lms.model.EventPaymentMethod"%>
<%@page import="com.hd.cedg.lms.model.EventPaymentMethodDetail"%>
<%@page import="java.util.ArrayList"%>

<jsp:include page="/common/header.jsp" flush="true" />
<link href="style/certifications.css" type="text/css" rel="stylesheet">

<%
String program = "CI";
if(request.getParameter("program") != null){
	program = request.getParameter("program").toUpperCase();
}
%>

		<div id="navigations">
			<div id="standard-nav">
				<a href="home.jsp" onclick="#"><span id="home" /></a>
				<a href="certification-home.jsp" onclick="#"><span id="certifications" /></a>
			</div>	
			<div id="cert-levels">
				<a href="#" onclick="#" id="cert">
					<br/><span id="certificate"><%=program%></span><br/>
					<span id="level">Learn More</span>
				</a>
			</div>
		</div>
		<div id="cert-details">
			<div id="left-nav"></div>
			<div id="nav-tabs">
				<div id="selected-tab">Company</div>
				<div id="selected-tab">User</div>
				<div id="selected-tab">Attendee</div>
				<% if(hasOptions){  %>
					  <div id="selected-tab">Options</div>					   
				<% } %>
				<% if(hasQuestions){  %>
				<div id="selected-tab">Additional Info</div>					   
				<% } %>	
				<% if(hasPayment){  %>
				<div id="selected-tab">Payment</div>					   
				<% } %>				
				<div id="selected-tab">Confirm</div>
				
			</div>
			<div id="right-nav">
				<div id="program">
				<div id="reg-header">Registration Complete</div>				
			<div id="reg-summary">
				<%
				if (event.getImageUrl() != null && !"".equals(event.getImageUrl())) {
				%>
						<img src="<%=event.getImageUrl() %>"/>
				<%
				}
				%>
						<h4><%=event.getTitle() %></h4>
				<table>
				<%
				if (event.getInPerson() == Event.IN_PERSON || event.getInPerson() == Event.IN_PERSON_TRAINING) {
				%>
							<tr>
								<th>Where</th>
								<td><%=event.getVenue().getName() %><br/>
								<%=event.getVenue().getAddress1() %><br/>
				<%
				if (event.getVenue().getAddress2() != null && !"".equals(event.getVenue().getAddress2())) {
				%>
								<%=event.getVenue().getAddress2() %><br/>
				<%
				}
				%>
								<%=event.getVenue().getCity() %>, <%=event.getVenue().getRegion() %> <%=event.getVenue().getPostCode() %><br/></td>
							</tr>
				<%
				}
				%>
							<tr>
								<th>When</th>
								<td><%=event.getDateRange().getFormattedDateRange() %><br/>
								<%=event.getDateRange().getFormattedTime() %><%=event.getInPerson() == Event.VIRTUAL?(event.getDateRange().isInDaylightSavings()?" EDT":" EST"):"" %></td>
							</tr>
							<tr>
								<th>Who</th>
								<td><%=registration.getFirstName() %> <%=registration.getLastName() %></td>
							</tr>
				<%
				if (registration.getTotalCost().getCost() > 0) {
					List<String> costs = new ArrayList<String>();
					if (event.getCost().getCost() > 0) {
						costs.add(event.getCost().getFormattedCost());
					}
					for (EventDisplayGroup group : event.getEventDisplayGroups()) {
						for (EventDisplayGroupItem item : group.getItems()) {
							if (item.getItemType() == Event.TYPE_ID_OPTION && registration.getSelectedOptionIds().contains(item.getItemId())) {
								for (EventOption option : event.getEventOptions()) {
									if (item.getItemId() == option.getEventOptionId()
											&& option.getCost().getCost() > 0) {
										boolean isFirst = costs.size() == 0;
										if (isFirst) {
											costs.add(option.getCost().getFormattedCost());
										} else {
											costs.add("+ " + option.getCost().getFormattedCost(false));
										}
									}
								}
							}
							if (item.getItemType() == Event.TYPE_ID_PACKAGE && registration.getSelectedPackageIds().contains(item.getItemId())) {
								for (EventOptionPackage aPackage : event.getEventOptionPackages()) {
									if (item.getItemId() == aPackage.getPackageId()
											&& aPackage.getCost().getCost() > 0) {
										boolean isFirst = costs.size() == 0;
										costs.add(aPackage.getCost().getFormattedCost(isFirst));
									}
								}
							}
						}
					}
					if (costs.size() > 0) {
				%>
							<tr>
								<th>Cost</th>
				<%
						if (costs.size() == 1) {
				%>
								<td><%=registration.getTotalCost().getFormattedCost() %></td>
				<%
						} else {
				%>
								<td>
									<ul class="cost-summary">
				<%
							for (String cost : costs) {
				%>
										<li><%=cost %></li>
				<%
				}
				%>
										<li><em><%=registration.getTotalCost().getFormattedCost() %></em></li>
									</div>
								</td>
				<%
						}
				%>
							</tr>
				<%
					}
				}
				%>
				</table>
				
			</div>
			<div id="reg-info">
				<div id="message">
					<h3>
						<span>Your Registration is Complete <br><br></span>						
					</h3>
					<span>Your registration to <em>"<%=event.getTitle() %>"</em> has been processed.  We have sent a confirmation email to:</span>
					<span class="important"><%=registration.getEmail() %></span>
					<span>Keep this email for your records to serve as your receipt.</span>
				</div>
				<div id="page-buttons">
				<%
				if (!"".equals(registration.getReturnUrl())) {
					String returnLinkText = event.isEnrollOthers()?"Done Registering":"Back to Event Description";
				%>
							<a href="<%=registration.getReturnUrl() %>" id="next" class="large">
								<span><%=returnLinkText %>&nbsp;&nbsp;&#x25BA;</span>
							</a>
				<%
				}
				if (!"".equals(registration.getReturnUrl()) && event.isEnrollOthers()) {
				%>
							<span class="extra-text">or</span>
				<%
				}
				if (event.isEnrollOthers()) {
				%>
							<a href="javascript: document.registerAnother.submit();" id="next" class="large">
								<span>Register Another Person &nbsp;&nbsp;&#x25BA;</span>
							</a>
							<form action="event-register?id=<%=event.getEventId() %>" name="registerAnother" method="POST">
								<input type="hidden" name="returnUrl" value="<%=registration.getReturnUrl() %>" />
							</form> 
				<%
				}
				%>
				</div>
			</div>
			<form name="register" action="" method="post">
				<input type="hidden" name="controllerAction" value="<%=EventRegisterControlServlet.CONTROLLER_ACTION_NEXT %>">
				<input type="hidden" name="flowState" value="<%=EventRegisterControlServlet.FLOW_STATE_CONFIRM %>">
				<input type="hidden" name="<%=EventRegisterControlServlet.FORM_OBJECT_NAME %>" value="<%=formObjectSerialized %>">
			</form>	
					
					
				</div>
				
			</div>
		</div>
	</div>

</div>

<%-- <jsp:include page="/common/footer.jsp" flush="true" /> --%>
<%@ page import="java.util.List,
                 java.util.Map,
                 com.hd.cedg.lms.model.LearningUser,
                 com.hd.cedg.lms.model.Event,
                 com.hd.cedg.lms.model.EventOtherUser,
                 com.hd.cedg.lms.model.EventRegistration,
                 com.hd.cedg.lms.servlet.EventRegisterControlServlet"
         errorPage="../error.jsp" buffer="300kb"%>
<%
LearningUser user = (LearningUser) request.getAttribute("user");
List<EventOtherUser> otherUsers = (List<EventOtherUser>) request.getAttribute("otherUsers");
List<Integer> regedUserIds = (List<Integer>) request.getAttribute("regedUserIds");
EventRegistration registration = (EventRegistration) request.getAttribute("registration");
Event event = registration.getEvent();

String formObjectSerialized = (String)request.getAttribute(EventRegisterControlServlet.FORM_OBJECT_NAME);
Map<String, String> errors = (Map<String, String>)request.getAttribute("errors");
Map<String, Object> registrationTab = registration.getRegistrationTabs();
%>

<jsp:include page="/common/header.jsp" flush="true" />
<link href="style/certifications.css" type="text/css" rel="stylesheet">

<script type="text/javascript">
$(document).ready(function() {
	$("table.selection-table").find("a").mouseover(hoverOn);
	$("table.selection-table").find("a").mouseleave(hoverOff);
	$("table.selection-table").find("a").mouseup(function(){$(this).mouseleave();});
	$("table.selection-table").find("a").click(submitRegister);
});
function hoverOn() {
	$(this).closest("tr").children("td").addClass("highlight");
}
function hoverOff() {
	$(this).closest("tr").children("td").removeClass("highlight");
}
function submitRegister() {
	document.register.userId.value = $(this).closest("tr").children("td").children("input[name=userId]").attr("value");
	document.register.firstName.value = $(this).closest("tr").children("td").children("input[name=firstName]").attr("value");
	document.register.lastName.value = $(this).closest("tr").children("td").children("input[name=lastName]").attr("value");
	document.register.phone.value = $(this).closest("tr").children("td").children("input[name=phone]").attr("value");
	document.register.email.value = $(this).closest("tr").children("td").children("input[name=email]").attr("value");
	document.register.submit();
}
</script>
<%
String regpage = "certification-home.jsp";
if(session.getAttribute("reg-page") != null){
	regpage = session.getAttribute("reg-page").toString();
}
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
				<div id="tabs">Attendee</div>
				<div id="tabs">Additional Info</div>
				<div id="tabs">Payment</div>
				<div id="tabs">Confirm</div>
				
			</div>
			<div id="right-nav">
			<div id="program">
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
					<%
					
					if (registration.getTotalCost().getCost() > 0) {
					%>
								<tr>
									<th>Cost</th>
									<td><%=registration.getTotalCost().getFormattedCost() %></td>
								</tr>
					<%
					}
					%>
				</table>
				<%
				if (!"".equals(registration.getReturnUrl())) {
				%>
						<a href="<%=registration.getReturnUrl() %>" id="cancel-button">
							<span>&#x25C4;&nbsp;Cancel Registration</span>
						</a>
				<%
				}
				%>
			</div>
			<div id="reg-info">
				<h4><span>Who are you registering for this event?</span></h4>
				<table class="selection-table">
					<%
					int userId = user.getUserId();
					if (user.getCompanyId() == registration.getCompanyId()) {
						if (!regedUserIds.contains(new Integer(userId))) {
					%>
								<tr class="primary">
									<td><a href="javascript: void(0);"><span><%=user.getFirstName() %> <%=user.getLastName() %></span></a><input type="hidden" name="userId" value="<%=userId %>" /><input type="hidden" name="firstName" value="<%=user.getFirstName() %>" /><input type="hidden" name="lastName" value="<%=user.getLastName() %>" /><input type="hidden" name="phone" value="<%=user.getPhone()!=null?user.getPhone():"" %>" /><input type="hidden" name="email" value="<%=user.getEmailAddress() %>" /></td>
									<td><a href="javascript: void(0);"><span class="arrow">&rarr;</span></a></td>
								</tr>
					<%
						} else {
					%>
								<tr class="primary alreadyreged">
									<td><span><%=user.getFirstName() %> <%=user.getLastName() %> (Already Registered)</span></td>
									<td><span class="arrow">&nbsp;</span></td>
								</tr>
					<%
						}
					}
					%>
								<tr class="special">
									<td><a href="javascript: void(0);"><span>Individual not listed</span></a><input type="hidden" name="userId" value="0" /><input type="hidden" name="firstName" value="" /><input type="hidden" name="lastName" value="" /><input type="hidden" name="phone" value="" /><input type="hidden" name="email" value="" /></td>
									<td><a href="javascript: void(0);"><span class="arrow">&rarr;</span></a></td>
								</tr>
		
					<%
					for (EventOtherUser otherUser : otherUsers) {
						int otherUserId = otherUser.getUserId();
						if (!regedUserIds.contains(new Integer(otherUserId))) {
					%>
								<tr>
									<td><a href="javascript: void(0);"><span><%=otherUser.getFirstName() %> <%=otherUser.getLastName() %></span></a><input type="hidden" name="userId" value="<%=otherUserId %>" /><input type="hidden" name="firstName" value="<%=otherUser.getFirstName() %>" /><input type="hidden" name="lastName" value="<%=otherUser.getLastName() %>" /><input type="hidden" name="phone" value="<%=otherUser.getPhone()!=null?otherUser.getPhone():"" %>" /><input type="hidden" name="email" value="<%=otherUser.getEmail()!=null?otherUser.getEmail():"" %>" /></td>
									<td><a href="javascript: void(0);"><span class="arrow">&rarr;</span></a></td>
								</tr>
					<%
						} else {
					%>
								<tr class="alreadyreged">
									<td><span><%=otherUser.getFirstName() %> <%=otherUser.getLastName() %> (Already Registered)</span></td>
									<td><span class="arrow">&nbsp;</span></td>
								</tr>
					<%
						}
					}
					%>
					
				</table>
				
						<% if((Boolean.TRUE.equals(registrationTab.get("hasCompany")))) { %>
						<div id="pre-page-buttons">
							<a href="javascript: history.back();" id="previous">
								<span>&#x25C4;&nbsp; Company Information </span>
							</a>
						</div>
						<% } %>
						
			</div>	
			<form name="register" action="" method="post">
				<input type="hidden" name="userId" value="">
				<input type="hidden" name="firstName" value="">
				<input type="hidden" name="lastName" value="">
				<input type="hidden" name="phone" value="">
				<input type="hidden" name="email" value="">
				<input type="hidden" name="controllerAction" value="<%=EventRegisterControlServlet.CONTROLLER_ACTION_NEXT %>">
				<input type="hidden" name="flowState" value="<%=EventRegisterControlServlet.FLOW_STATE_USER %>">
				<input type="hidden" name="<%=EventRegisterControlServlet.FORM_OBJECT_NAME %>" value="<%=formObjectSerialized %>">
			</form>
			<%
				boolean hasHelpPhone = event.getHelpPhone() != null && !"".equals(event.getHelpPhone());
				boolean hasHelpEmail = event.getHelpEmail() != null && !"".equals(event.getHelpEmail());
				if (hasHelpPhone && hasHelpEmail) {
				%>
					<div id="reg-help">
						<span>Trouble registering? Call <em><%=event.getHelpPhone() %></em> or email <a href="mailto:<%=event.getHelpEmail() %>"><%=event.getHelpEmail() %></a> for support.</span>
					</div>
				<%
				} else if (hasHelpPhone) {
						%>
					<div id="reg-help">
						<span>Trouble registering? Call <em><%=event.getHelpPhone() %></em> for support.</span>
					</div>
				<%
				} else if (hasHelpEmail) {
				%>
					<div id="reg-help">
						<span>Trouble registering? Email <a href="mailto:<%=event.getHelpEmail() %>"><%=event.getHelpEmail() %></a> for support.</span>
					</div>
				<%
				}
				%>

				</div>
				
			</div>
		</div>
	</div>

</div>

<%-- <jsp:include page="/common/footer.jsp" flush="true" /> --%>
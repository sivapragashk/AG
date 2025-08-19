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

boolean newCompany = registration.getCompanyId()==0?true:false;
String companyName = registration.getCompanyName()!=null?registration.getCompanyName():"";
String first = registration.getFirstName()!=null?registration.getFirstName():"";
String last = registration.getLastName()!=null?registration.getLastName():"";
String address = registration.getAddress()!=null?registration.getAddress():"";
String city = registration.getCity()!=null?registration.getCity():"";
String region = registration.getRegion()!=null?registration.getRegion():"";
String postcode = registration.getPostCode()!=null?registration.getPostCode():"";
String phone = registration.getPhone()!=null?registration.getPhone():"";
String email = registration.getEmail()!=null?registration.getEmail():"";
Map<String, Object> registrationTab = registration.getRegistrationTabs();

System.out.println("-------------" + registrationTab);

boolean hasOptions = (event.getEventDisplayGroups().size() > 0) ? true : false;
String next = "Next";
String previous = "Previous"; 
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

if (phone != null) {
	if (phone.length() == 10) {
		phone = phone.substring(0, 3) + "-" + phone.substring(3, 6) + "-" + phone.substring(6);
	} else if (phone.length() > 10) {
		phone = phone.substring(0, 3) + "-" + phone.substring(3, 6) + "-" + phone.substring(6, 10) + " " + phone.substring(10);
	}
}
%>

<jsp:include page="/common/header.jsp" flush="true" />
<link href="style/certifications.css" type="text/css" rel="stylesheet">

<script type="text/javascript">
	function submitRegister() {
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
				<div id="selected-tab">Attendee</div>
				<% if(hasOptions){  %>
					  <div id="tabs">Options</div>					   
				<% } %>
				<% if(hasQuestions){  %>
				<div id="tabs">Additional Info</div>					   
				<% } %>	
				<% if(hasPayment){  %>
				<div id="tabs">Payment</div>					   
				<% } %>				
				<div id="tabs">Confirm</div>
				
			</div>
			<div id="right-nav">
				<div id="program">
					<div id="reg-header">Attendee Information</div>
					<div id="reg-summary">
						<%
						if (event.getImageUrl() != null && !"".equals(event.getImageUrl())) {
						%>
								<img src="<%=event.getImageUrl() %>"/>
						<%
						}
						%>
						<h4><%=event.getTitle()%></h4>
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
					<form name="register" action="" method="post">
					<div id="reg-info">
						<table class="data-entry">
							<%if(!newCompany){%>
								<tr>
									<th>
										<span>Company</span>
									</th>
									<td>
										<span><%=companyName %></span>
										<input type="hidden" name="companyName" value="<%=companyName %>" />
									</td>
								</tr>
							<%}else{%>
								<tr>
									<th>
										<span>Company</span>
									</th>
									<td>
										<input type="text" value="" name="companyName" class="text">
			<%
			if (errors != null && errors.get("companyName") != null) {
			%>
									<span class="error"><%=errors.get("companyName") %></span>
			<%
			}
			%>							
									</td>
								</tr>
							
							
			<%				}
			if (registration.getUserId() != 0) {
			%>
							<tr>
								<th>
									<span>Attendee Name</span>
								</th>
								<td>
									<span><%=first %> <%=last %></span>
									<input type="hidden" name="first" value="<%=first %>" />
									<input type="hidden" name="last" value="<%=last %>" />
			<%
			if (errors != null && errors.get("first") != null) {
			%>
									<span class="error"><%=errors.get("first") %></span>
			<%
			}
			if (errors != null && errors.get("last") != null) {
			%>
									<span class="error"><%=errors.get("last") %></span>
			<%
			}
			%>
								</td>
							</tr>
			<%
			} else {
			%>
							<tr>
								<th>
									<span>Attendee First Name</span>
								</th>
								<td>
									<input type="text" class="text" name="first" value="<%=first %>" />
			<%
			if (errors != null && errors.get("first") != null) {
			%>
									<span class="error"><%=errors.get("first") %></span>
			<%
			}
			%>
								</td>
							</tr>
							<tr>
								<th>
									<span>Attendee Last Name</span>
								</th>
								<td>
									<input type="text" class="text" name="last" value="<%=last %>" />
			<%
			if (errors != null && errors.get("last") != null) {
			%>
									<span class="error"><%=errors.get("last") %></span>
			<%
			}
			%>
								</td>
							</tr>
			<%
			}
			%>
						</table>
						<table class="data-entry">
							<tr>
								<th>
									<span>Address</span>
								</th>
								<td>
									<input type="text" class="text" name="address" value="<%=address %>" />
			<%
			if (errors != null && errors.get("address") != null) {
			%>
									<span class="error"><%=errors.get("address") %></span>
			<%
			}
			%>
								</td>
							</tr>
							<tr>
								<th>
									<span>City</span>
								</th>
								<td>
									<input type="text" class="text" name="city" value="<%=city %>" />
			<%
			if (errors != null && errors.get("city") != null) {
			%>
									<span class="error"><%=errors.get("city") %></span>
			<%
			}
			%>
								</td>
							</tr>
							<tr>
								<th>
									<span>State/Province</span>
								</th>
								<td>
									<input type="text" class="text-short" name="region" value="<%=region %>" />
			<%
			if (errors != null && errors.get("region") != null) {
			%>
									<span class="error"><%=errors.get("region") %></span>
			<%
			}
			%>
								</td>
							</tr>
							<tr>
								<th>
									<span>Postal Code</span>
								</th>
								<td>
									<input type="text" class="text-short" name="postcode" value="<%=postcode %>" />
			<%
			if (errors != null && errors.get("postcode") != null) {
			%>
									<span class="error"><%=errors.get("postcode") %></span>
			<%
			}
			%>
								</td>
							</tr>
						</table>
						<table class="data-entry">
							<tr>
								<th>
									<span>Phone</span>
								</th>
								<td>
									<input type="text" class="text" name="phone" value="<%=phone %>" />
			<%
			if (errors != null && errors.get("phone") != null) {
			%>
									<span class="error"><%=errors.get("phone") %></span>
			<%
			}
			%>
								</td>
							</tr>
							<tr>
								<th>
									<span>Email</span>
								</th>
								<td>
									<input type="text" class="text" name="email" value="<%=email %>" />
			<%
			if (errors != null && errors.get("email") != null) {
			%>
									<span class="error"><%=errors.get("email") %></span>
			<%
			}
			%>
								</td>
							</tr>
						</table>
						<div id="page-buttons">
							<a href="javascript: submitRegister();" id="next">
								<span><%= next = (Boolean.TRUE.equals(registrationTab.get("hasOptions"))) ? "Options" : (Boolean.TRUE.equals(registrationTab.get("hasQuestions")))? "Additional Information" : (Boolean.TRUE.equals(registrationTab.get("hasPayment"))) ? "Payment Information" : "Conform" %> &nbsp;&nbsp;&#x25BA;</span>
							</a>
						</div>
						<% if((Boolean.TRUE.equals(registrationTab.get("hasUser"))) || (Boolean.TRUE.equals(registrationTab.get("hasCompany")))) { %>
						<div id="pre-page-buttons">
							<a href="javascript:  history.back();" id="previous">
								<span>&#x25C4;&nbsp; <%= previous = (Boolean.TRUE.equals(registrationTab.get("hasUser"))) ? "User Information" : "Company Information" %> </span>
							</a>
						</div>
						<% } %>
					</div>
					<input type="hidden" name="controllerAction" value="<%=EventRegisterControlServlet.CONTROLLER_ACTION_NEXT %>">
					<input type="hidden" name="flowState" value="<%=EventRegisterControlServlet.FLOW_STATE_CONTACT %>">
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
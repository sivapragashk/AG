<%@ page import="java.util.List,
                 java.util.ArrayList,
                 java.util.Map,
                 com.hd.cedg.lms.model.LearningUser,
                 com.hd.cedg.lms.model.Event,
                 com.hd.cedg.lms.model.EventOption,
                 com.hd.cedg.lms.model.EventOptionPackage,
                 com.hd.cedg.lms.model.EventDisplayGroup,
                 com.hd.cedg.lms.model.EventDisplayGroupItem,
                 com.hd.cedg.lms.model.EventQuestion,
                 com.hd.cedg.lms.model.EventRegistration,
                 com.hd.cedg.lms.servlet.EventRegisterControlServlet,
                 com.hd.cedg.lms.model.EventPaymentMethod;"
         errorPage="../error.jsp" buffer="300kb"%><%
LearningUser user = (LearningUser) request.getAttribute("user");
EventRegistration registration = (EventRegistration) request.getAttribute("registration");
Event event = registration.getEvent();

String formObjectSerialized = (String)request.getAttribute(EventRegisterControlServlet.FORM_OBJECT_NAME);
Map<String, String> errors = (Map<String, String>)request.getAttribute("errors");

boolean allRequired = true;
boolean someRequired = false;

for (EventQuestion question : registration.getQuestionsForCurrentSelections()) {
	if (question.isRequired()) {
		someRequired = true;
	} else {
		allRequired = false;
	}
}
Map<String, Object> registrationTab = registration.getRegistrationTabs();

boolean hasOptions = (event.getEventDisplayGroups().size() > 0) ? true : false;
/* hasPayment */
String next = "Next";
String previous = "Previous"; 
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
					  <div id="selected-tab">Options</div>					   
				<% } %>
				<div id="selected-tab">Additional Info</div>
				<% if(hasPayment){  %>
				<div id="tabs">Payment</div>					   
				<% } %>				
				<div id="tabs">Confirm</div>
				
			</div>
			<div id="right-nav">
				<div id="program">
				<div id="reg-header">Additional Information</div>
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
										</ul>
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
				<table class="data-entry questions">
			    
				<%
				for (EventQuestion question : registration.getQuestionsForCurrentSelections()) {
					int qid = question.getQuestionId();
					boolean hasLongText = question.getText().length() >= 100;
					if (hasLongText) {
				%>
								<tr>
									<th class="large" colspan="2">
										<span><%=question.getText() %><%=question.isRequired()&&!allRequired?"&nbsp;*":"" %></span>
									</th>
								</tr>
				<%
					}
				%>
								<tr>
				<%
					if (hasLongText) {
				%>
									<td></td>
				<%
					} else {
				%>
									<th>
										<span><%=question.getText() %><%=question.isRequired()&&!allRequired?"&nbsp;*":"" %></span>
									</th>
				<%
					}
				%>
									<td>
				<%
					String ans = registration.getQuestionAnswers().get(new Integer(qid));
					if (question.getType() == EventQuestion.TYPE_TEXT) {
				%>
										<input type="text" class="text" id="question<%=qid %>" name="question<%=qid %>" value="<%=ans!=null?ans:"" %>" />
				<%
					} else if (question.getType() == EventQuestion.TYPE_DROPDOWN) {
				%>
										<select id="question<%=qid %>" name="question<%=qid %>">
											<option value="0">Select One...</option>
				<%
						for (String answer : question.getAnswers()) {
				%>
											<option value="<%=answer %>"<%=answer.equals(ans)?" selected=\"selected\"":"" %>><%=answer %></option>
				<%
						}
				%>
										</select>
				<%
					} else if (question.getType() == EventQuestion.TYPE_CHECKBOXES) {
						for (String answer : question.getAnswers()) {
							boolean checked = false;
							if (ans != null) {
								if (ans.equals(answer)) {
									checked = true;
								} else if (ans.startsWith(answer + ", ")) {
									checked = true;
								} else if (ans.endsWith(", " + answer)) {
									checked = true;
								} else if (ans.contains(", " + answer + ", ")) {
									checked = true;
								}
							}
				%>
										<div class="radio-option">
											<input type="checkbox" name="question<%=qid %>" value="<%=answer %>"<%=checked?" checked=\"checked\"":"" %>></input>
											<span><%=answer %></span>
										</div>
				<%
						}
					}
					if (errors != null && errors.get("question" + qid) != null) {
				%>
										<span class="error"><%=errors.get("question" + qid) %></span>
				<%
					}
				%>
									</td>
								</tr>
				<%
				}
				%>
			</table>
				<%
				if (allRequired) {
				%>
							<div>
								<span class="note">
								<span>All questions are required.</span>
								</span>
							</div>
				<%
				} else if (someRequired) {
				%>
							<div>
								<span class="required-note">
									<span>Questions followed by <em>*</em> are required.</span>
								</span>
							</div>
				<%
				}
				%>
					<div id="page-buttons">
						<a href="javascript: submitRegister();" id="next">
							<span><%= next = (Boolean.TRUE.equals(registrationTab.get("hasPayment"))) ? "Payment Information" : "Conform" %> &nbsp;&nbsp;&#x25BA;</span>
						</a>
					</div>
					
					<div id="pre-page-buttons">
						<a href="javascript:  history.back();" id="previous">
							<span>&#x25C4;&nbsp; <%= previous = (Boolean.TRUE.equals(registrationTab.get("hasOptions"))) ? "Options" : "Attendee Information" %> </span>
						</a>
					</div>
				</div>
				<input type="hidden" name="controllerAction" value="<%=EventRegisterControlServlet.CONTROLLER_ACTION_NEXT %>">
				<input type="hidden" name="flowState" value="<%=EventRegisterControlServlet.FLOW_STATE_QUESTIONS %>">
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
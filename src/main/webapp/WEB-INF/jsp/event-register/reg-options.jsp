<%@ page import="java.util.List,
                 java.util.Map,
                 com.hd.cedg.lms.model.LearningUser,
                 com.hd.cedg.lms.model.Event,
                 com.hd.cedg.lms.model.EventOption,
                 com.hd.cedg.lms.model.EventOptionPackage,
                 com.hd.cedg.lms.model.EventDisplayGroup,
                 com.hd.cedg.lms.model.EventDisplayGroupItem,
                 com.hd.cedg.lms.model.EventRuleMutex,
                 com.hd.cedg.lms.model.EventRuleRequiredSet,
                 com.hd.cedg.lms.model.EventRegistration,
                 com.hd.cedg.lms.servlet.EventRegisterControlServlet,
                 com.hd.cedg.lms.model.EventPaymentMethod;"
         errorPage="../error.jsp" buffer="300kb"%><%
LearningUser user = (LearningUser) request.getAttribute("user");
         user.setAlligneduser(EventRegistration.checkAlignedUser(user.getAllianceLevel()));
EventRegistration registration = (EventRegistration) request.getAttribute("registration");
Event event = registration.getEvent();
String formObjectSerialized = (String)request.getAttribute(EventRegisterControlServlet.FORM_OBJECT_NAME);
Map<String, String> errors = (Map<String, String>)request.getAttribute("errors");
Map<String, Object> registrationTab = registration.getRegistrationTabs();

boolean hasQuestions = (registration.getQuestionsForCurrentSelections().size() > 0 ) ? true : false;
String next = "Next";
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

<jsp:include page="/common/header.jsp" flush="true" />
<link href="style/certifications.css" type="text/css" rel="stylesheet">

<script type="text/javascript">
function submitRegister() {
	document.register.submit();
}
$(document).ready(function() {
	assessOptions();
	$("li.option-entry").children("input.checkbox").click(function(){
		assessOptions();
	});
});
function assessOptions() {
	$("li.option-entry").removeClass("disabled");
	$("span.mutex-explanation").remove();

	var mutexGroups = [
	    	[]
<%
for (EventRuleMutex mutex : event.getEventRuleMutexes()) {
%>
		,["zzz"
<%
	for (Integer optionId : mutex.getOptionIds()){
%>
			, "option-<%=optionId.intValue() %>"	
<%
	}
	for (Integer packageId : mutex.getPackageIds()){
%>
			, "package-<%=packageId.intValue() %>"	
<%
	}
%>
		]
<%
}
%>
	];
	for (var i=0; i < mutexGroups.length; i++) {
		var group = mutexGroups[i];
		for (var j=0; j < group.length; j++) {
			$("input#" + group[j] + ":checked").each(function(){
				for (var k=0; k < group.length; k++) {
					if (k != j) {
						$("li#line-" + group[k]).addClass("disabled");
						if ($("li#line-" + group[k]).children("span.mutex-explanation").size() == 0) {
							$("li#line-" + group[k]).append("<span class=\"mutex-explanation\">Cannot be selected with the other options you have chosen.</span>")
						}
					}
				}
			});
		}
	}
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
				<div id="selected-tab">Options</div>	
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
				<div id="reg-header">Options</div>
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
				if (registration.getTotalCost().getCost() > 0 || user.isAlligneduser()&& event.getCost().getAligncost() > 0) {
					%>
								<tr>
									<th>Cost</th>
									<%if(user.isAlligneduser()&& event.getCost().getAligncost() > 0 || user.isAlligneduser() && registration.getTotalCost().getCost() > 0){%>
									<td><%=event.getCost().getFormattedAlignCost() %></td>
									<%}else{ %>
									<td><%=registration.getTotalCost().getFormattedCost() %></td>
					<%				}%>
									
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
				<%
				if (errors != null && errors.get("general") != null) {
				%>
							<span class="error"><%=errors.get("general") %></span>
				<%
				}
				%>
							<h3>Select from the options below:</h3>
				<%
				for (EventDisplayGroup group : event.getEventDisplayGroups()) {
				%>
							<h4><span><%=group.getTitle() %></span></h4>
							<ul class="large-selection-list">
				<%
					for (EventDisplayGroupItem item : group.getItems()) {
						if (item.getItemType() == Event.TYPE_ID_OPTION) {
							EventOption option = null;
							for (EventOption op : event.getEventOptions()) {
								if (op.getEventOptionId() == item.getItemId()) {
									option = op;
									break;
								}
							}
							if (option != null) {
				%>
								<li id="line-option-<%=option.getEventOptionId() %>" class="option-entry<%=option.isFull()?" full":"" %>">
									<input class="checkbox" type="checkbox" name="option-<%=option.getEventOptionId() %>" id="option-<%=option.getEventOptionId() %>" />
									<span class="title"><%=option.getTitle() %></span>
									<table class="item-info">
										<tr>
				<%
								if (option.getCost().getCost() > 0) {
				%>
											<th>Cost</th>
				<%
								}
								if (!"".equals(option.getDateRange().getFormattedDateRange())) {
				%>
											<th>Date</th>
				<%
								}
								if (!"".equals(option.getDateRange().getFormattedTime())) {
				%>
											<th>Time</th>
				<%
								}
				%>
										</tr>
										<tr>
				<%
								if (option.getCost().getCost() > 0) {
				%>
											<td class="cost"><%=option.getCost().getFormattedCost() %></td>
				<%
								}
								if (!"".equals(option.getDateRange().getFormattedDateRange())) {
				%>
											<td class="date"><%=option.getDateRange().getFormattedDateRange() %></td>
				<%
								}
								if (!"".equals(option.getDateRange().getFormattedTime())) {
				%>
											<td class="time"><%=option.getDateRange().getFormattedTime() %></td>
				<%
								}
				%>
										</tr>
									</table>
				<%
								if (option.getDescription() != null && !"".equals(option.getDescription())) {
				%>
									<span class="description"><h5>Description</h5> <%=option.getDescription() %></span>
				<%
								}
								if (option.isFull()) {
				%>
									<span class="full-explanation">This option is full</span>
				<%
								}
				%>
									<div></div>
								</li>
				<%
							}
						} else if (item.getItemType() == Event.TYPE_ID_PACKAGE) {
							EventOptionPackage aPackage = null;
							for (EventOptionPackage pack : event.getEventOptionPackages()) {
								if (pack.getPackageId() == item.getItemId()) {
									aPackage = pack;
									break;
								}
							}
							if (aPackage != null) {
				%>
								<li id="line-package-<%=aPackage.getPackageId() %>" class="option-entry<%=aPackage.isFull(event.getEventOptions())?" full":"" %>">
									<input class="checkbox" type="checkbox" name="package-<%=aPackage.getPackageId() %>" id="package-<%=aPackage.getPackageId() %>" />
									<span class="title"><%=aPackage.getTitle() %></span>
									<table class="item-info">
										<tr>
				<%
								if (aPackage.getCost().getCost() > 0) {
				%>
											<th>Cost</th>
				<%
								}
								if (!"".equals(aPackage.getDateRange(event.getEventOptions()).getFormattedDateRange())) {
				%>
											<th>Date</th>
				<%
								}
								if (!"".equals(aPackage.getDateRange(event.getEventOptions()).getFormattedTime())) {
				%>
											<th>Time</th>
				<%
								}
				%>
										</tr>
										<tr>
				<%
								if (aPackage.getCost().getCost() > 0) {
				%>
											<td class="cost"><%=aPackage.getCost().getFormattedCost() %></td>
				<%
								}
								if (!"".equals(aPackage.getDateRange(event.getEventOptions()).getFormattedDateRange())) {
				%>
											<td class="date"><%=aPackage.getDateRange(event.getEventOptions()).getFormattedDateRange() %></td>
				<%
								}
								if (!"".equals(aPackage.getDateRange(event.getEventOptions()).getFormattedTime())) {
				%>
											<td class="time"><%=aPackage.getDateRange(event.getEventOptions()).getFormattedTime() %></td>
				<%
								}
				%>
										</tr>
									</table>
				<%
								if (aPackage.getDescription() != null && !"".equals(aPackage.getDescription())) {
				%>
									<span class="description"><h5>Description</h5> <%=aPackage.getDescription() %></span>
				<%
								}
								if (aPackage.isFull(event.getEventOptions())) {
				%>
									<span class="full-explanation">This option is full</span>
				<%
								}
				%>
									<div></div>
								</li>
				<%	
							}
						}
					}
				%>
							</ul>
				<%
				}
				for (EventRuleRequiredSet reqSet : event.getEventRuleRequiredSets()) {
				%>
							<div class="required-set">
								<span>You must select one of the following:</span>
								<ul>
				<%
					for (EventDisplayGroup group : event.getEventDisplayGroups()) {
						for (EventDisplayGroupItem item : group.getItems()) {
							if (item.getItemType() == Event.TYPE_ID_OPTION && reqSet.getOptionIds().contains(item.getItemId())) {
								for (EventOption option : event.getEventOptions()) {
									if (option.getEventOptionId() == item.getItemId()) {
				%>
									<li><%=option.getTitle() %></li>
				<%
									}
								}
							} else if (item.getItemType() == Event.TYPE_ID_PACKAGE && reqSet.getPackageIds().contains(item.getItemId())) {
								for (EventOptionPackage aPackage : event.getEventOptionPackages()) {
									if (aPackage.getPackageId() == item.getItemId()) {
				%>
									<li><%=aPackage.getTitle() %></li>
				<%
									}
								}
							}
						}
					}
				%>
								</ul>
							</div>
				<%
				}
				%>
						<div id="page-buttons">
							<a href="javascript: submitRegister();" id="next">
								<span><%= next = (Boolean.TRUE.equals(registrationTab.get("hasQuestions"))) ? "Additional Information" : (Boolean.TRUE.equals(registrationTab.get("hasPayment"))) ? "Payment Information" : "Conform" %> &nbsp;&nbsp;&#x25BA;</span>
							</a>
						</div>
					
						<div id="pre-page-buttons">
							<a href="javascript:  history.back();" id="previous">
								<span>&#x25C4;&nbsp; Attendee Information </span>
							</a>
						</div>
					
						</div>
						<input type="hidden" name="controllerAction" value="<%=EventRegisterControlServlet.CONTROLLER_ACTION_NEXT %>">
						<input type="hidden" name="flowState" value="<%=EventRegisterControlServlet.FLOW_STATE_REG_OPTIONS %>">
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

<%-- <jsp:include page="../common/event-footer.jsp" flush="true" /> --%>
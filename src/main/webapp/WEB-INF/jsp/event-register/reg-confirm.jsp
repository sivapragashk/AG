<%@ page import="java.util.List,
                 java.util.Map,
                 com.hd.cedg.lms.model.LearningUser,
                 com.hd.cedg.lms.model.Event,
                 com.hd.cedg.lms.model.EventDisplayGroup,
                 com.hd.cedg.lms.model.EventDisplayGroupItem,
                 com.hd.cedg.lms.model.EventOption,
                 com.hd.cedg.lms.model.EventOptionPackage,
                 com.hd.cedg.lms.model.EventQuestion,
                 com.hd.cedg.lms.model.EventRegistration,
                 com.hd.cedg.lms.servlet.EventRegisterControlServlet"
         errorPage="../error.jsp" buffer="300kb"%><%
LearningUser user = (LearningUser) request.getAttribute("user");
EventRegistration registration = (EventRegistration) request.getAttribute("registration");
Event event = registration.getEvent();

String formObjectSerialized = (String)request.getAttribute(EventRegisterControlServlet.FORM_OBJECT_NAME);
Map<String, String> errors = (Map<String, String>)request.getAttribute("errors");

String phone = registration.getPhone();
if (phone != null) {
	if (phone.length() == 10) {
		phone = phone.substring(0, 3) + "-" + phone.substring(3, 6) + "-" + phone.substring(6);
	} else if (phone.length() > 10) {
		phone = phone.substring(0, 3) + "-" + phone.substring(3, 6) + "-" + phone.substring(6, 10) + " " + phone.substring(10);
	}
}
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

<script type="text/javascript">
$(document).ready(function() {
	$("#please-wait").hide();
});
function submitRegister() {
	$("div#page-buttons").hide();
	$("#please-wait").show();
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
				<div id="reg-header">Review</div>
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
				<div id="message">
					<span>Please review your information.  <em>Your registration is <strong>not</strong> complete until you click "Submit" below.</em></span>
				</div>
		<%
		if (registration.getTotalCost().getCost() > 0) {
		%>
				<table class="data-entry">
					<tr>
						<th class="table-label" colspan="2">Total Cost</th>
					</tr>
					<tr>
						<th><span>Amount</span></th>
						<td>
							<span><%=registration.getTotalCost().getFormattedCost() %></span>
						</td>
					</tr>
				</table>
		<%
		}
		%>
				<table class="data-entry">
					<tr>
						<th class="table-label" colspan="2">Attendee Information</th>
					</tr>
					<tr>
						<th>
							<span>Attendee Name</span>
						</th>
						<td>
							<span><%=registration.getFirstName() %> <%=registration.getLastName() %></span>
						</td>
					</tr>
					<tr>
						<th>
							<span>Company</span>
						</th>
						<td>
							<span><%=registration.getCompanyName() %></span>
						</td>
					</tr>
					<tr>
						<th>
							<span>Address</span>
						</th>
						<td>
							<span><%=registration.getAddress() %><br/><%=registration.getCity() %>, <%=registration.getRegion() %> <%=registration.getPostCode() %></span>
						</td>
					</tr>
					<tr>
						<th>
							<span>Phone</span>
						</th>
						<td>
							<span><%=phone %></span>
						</td>
					</tr>
					<tr>
						<th>
							<span>Email</span>
						</th>
						<td>
							<span><%=registration.getEmail() %></span>
						</td>
					</tr>
		<%
		int numOptionsSelected = registration.getSelectedOptionIds().size() + registration.getSelectedPackageIds().size(); 
		if ( numOptionsSelected > 0) {
		%>
					<tr>
						<th>
							<span>Options Selected</span>
						</th>
						<td>
		<%
			if (numOptionsSelected > 1) {
		%>
							<ul>
		<%
			}
			for (EventDisplayGroup group : event.getEventDisplayGroups()) {
				for (EventDisplayGroupItem item : group.getItems()) {
					String name = "";
					if (item.getItemType() == Event.TYPE_ID_OPTION) {
						if (registration.getSelectedOptionIds().contains(item.getItemId())) {
							for (EventOption option : event.getEventOptions()) {
								if (option.getEventOptionId() == item.getItemId()) {
									name = option.getTitle();
									break;
								}
							}
						}
					} else if (item.getItemType() == Event.TYPE_ID_PACKAGE) {
						if (registration.getSelectedPackageIds().contains(item.getItemId())) {
							for (EventOptionPackage aPackage : event.getEventOptionPackages()) {
								if (aPackage.getPackageId() == item.getItemId()) {
									name = aPackage.getTitle();
									break;
								}
							}
						}
					}
					if (!"".equals(name)) {
						if (numOptionsSelected > 1) {
		%>
								<li>
									<span><%=name %></span>
								</li>
		<%
						} else {
		%>
								<span><%=name %></span>
		<%
						}
					}
				}
			}
			if (numOptionsSelected > 1) {
		%>
							</ul>
		<%
			}
		%>
						</td>
					</tr>
		<%
		}
		for (EventQuestion question : event.getQuestions()) {
			String answer = registration.getQuestionAnswers().get(question.getQuestionId());
			if (answer != null && !"".equals(answer)) {
				boolean hasLongText = question.getText().length() >= 100;
				if (hasLongText) {
		%>
					<tr>
						<th class="large" colspan="2">
							<span><%=question.getText() %></span>
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
							<span><%=question.getText() %></span>
						</th>
		<%
				}
		%>
						<td>
							<span><%=answer %></span>
						</td>
					</tr>
		<%
			}
		}
		%>
				</table>
		<%
		if (registration.getPaymentMethodId() >= 0) {
		%>
				<table class="data-entry">
					<tr>
						<th class="table-label" colspan="2">Payment Information</th>
					</tr>
		<%
			int paymentId = registration.getPaymentMethodId();
			if (paymentId == EventPaymentMethod.PAYMENT_ID_CC) {
				Map<Integer, String> detailAnswers = registration.getPaymentMethodDetailAnswers();
				String ccNumber = detailAnswers.get(EventPaymentMethodDetail.DETAIL_ID_CC_NUMBER);
				String expDate = detailAnswers.get(EventPaymentMethodDetail.DETAIL_ID_EXP_DATE);
				String csc = detailAnswers.get(EventPaymentMethodDetail.DETAIL_ID_CSC);
				String name = detailAnswers.get(EventPaymentMethodDetail.DETAIL_ID_NAME);
				String address = detailAnswers.get(EventPaymentMethodDetail.DETAIL_ID_ADDRESS);
				String city = detailAnswers.get(EventPaymentMethodDetail.DETAIL_ID_CITY);
				String region = detailAnswers.get(EventPaymentMethodDetail.DETAIL_ID_REGION);
				String postcode = detailAnswers.get(EventPaymentMethodDetail.DETAIL_ID_POSTCODE);
		%>
					<tr>
						<th>
							<span>Payment Type</span>
						</th>
						<td>
							<span>Credit Card</span>
						</td>
					</tr>
					<tr>
						<th>
							<span>Credit Card Number</span>
						</th>
						<td>
		<%
		if (ccNumber.length() == 16) {
		%>
							<span>XXXX XXXX XXXX <%=ccNumber.substring(12) %></span>
		<%	
		} else {
			char[] mask = new char[ccNumber.length()-4];
			for (int i = 0; i < mask.length; i++) {
				mask[i] = 'X';
			}
		%>
							<span><%=new String(mask) %> <%=ccNumber.substring(ccNumber.length()-4) %></span>
		<%	
		}
		%>
						</td>
					</tr>
					<tr>
						<th>
							<span>Expiration Date (MMYY)</span>
						</th>
						<td>
							<span><%=expDate!=null?expDate:"" %></span>
						</td>
					</tr>
					<tr>
						<th>
							<span>Card Security Code</span>
						</th>
						<td>
							<span><%=csc!=null?csc:"" %></span>
						</td>
					</tr>
					<tr>
						<th>
							<span>Cardholder Name</span>
						</th>
						<td>
							<span><%=name!=null?name:"" %></span>
						</td>
					</tr>
					<tr>
						<th>
							<span>Billing Address</span>
						</th>
						<td>
							<span><%=address!=null?(address+"<br/>"):"" %><%=city!=null?(city+", "):"" %><%=region!=null?(region+" "):"" %><%=postcode!=null?postcode:"" %></span>
						</td>
					</tr>
		<%	} else {
				for (EventPaymentMethod pay : event.getPaymentMethods()) {
					if (pay.getPaymentMethodId() == paymentId) {
		%>
					<tr>
						<th>
							<span>Payment Type</span>
						</th>
						<td>
							<span><%=pay.getName() %></span>
						</td>
					</tr>
		<%
						for (EventPaymentMethodDetail detail : pay.getPaymentMethodDetails()) {
							int detailId = detail.getPaymentDetailId();
							String answer = registration.getPaymentMethodDetailAnswers().get(new Integer(detailId));
							if (answer != null && !"".equals(answer))  {
		%>
					<tr>
						<th>
							<span><%=detail.getName() %></span>
						</th>
						<td>
							<span><%=answer %></span>
						</td>
					</tr>
		<%
							}
						}
					}
				}
			}
		%>
				</table>
		<%
		}
		%>
				<div id="page-buttons">
					<a href="javascript: submitRegister();" id="next">
						<span>Submit&nbsp;&nbsp;&#x25BA;</span>
					</a>
				</div>
				<div id="pre-page-buttons">
					<a href="javascript:  history.back();" id="previous">
						<span>&#x25C4;&nbsp; Payment Information </span>
					</a>
				</div>
				<div id="please-wait">
					<span>Processing<br>Please Wait</span>
				</div>
			</div>
			<form name="register" action="" method="post">
				<input type="hidden" name="controllerAction" value="<%=EventRegisterControlServlet.CONTROLLER_ACTION_NEXT %>">
				<input type="hidden" name="flowState" value="<%=EventRegisterControlServlet.FLOW_STATE_CONFIRM %>">
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
<%@ page import="java.util.List,
                 java.util.ArrayList,
                 java.util.Map,
                 com.hd.cedg.lms.model.LearningUser,
                 com.hd.cedg.lms.model.Event,
                 com.hd.cedg.lms.model.EventOption,
                 com.hd.cedg.lms.model.EventOptionPackage,
                 com.hd.cedg.lms.model.EventDisplayGroup,
                 com.hd.cedg.lms.model.EventDisplayGroupItem,
                 com.hd.cedg.lms.model.EventPaymentMethod,
                 com.hd.cedg.lms.model.EventPaymentMethodDetail,
                 com.hd.cedg.lms.model.EventRegistration,
                 com.hd.cedg.lms.servlet.EventRegisterControlServlet"
         errorPage="../error.jsp" buffer="300kb"%><%
LearningUser user = (LearningUser) request.getAttribute("user");
EventRegistration registration = (EventRegistration) request.getAttribute("registration");
Event event = registration.getEvent();

String formObjectSerialized = (String)request.getAttribute(EventRegisterControlServlet.FORM_OBJECT_NAME);
Map<String, String> errors = (Map<String, String>)request.getAttribute("errors");

int numOptions = 0;
if (event.isAllowCC()) {
	numOptions++;
}
for (EventPaymentMethod pay : event.getPaymentMethods()) {
	if (!pay.isAdminOnly()) {
		numOptions++;
	}
}
%>

<jsp:include page="/common/header.jsp" flush="true" />
<link href="style/certifications.css" type="text/css" rel="stylesheet">

<script type="text/javascript">
function submitRegister() {
	document.register.submit();
}
function openCscExamples() {
	$("#csc-examples").slideDown();
}
function closeCscExamples() {
	$("#csc-examples").slideUp();
}
$(document).ready(function() {
	$(".expandable").hide();
<%
if (numOptions > 1) {
%>
	openDetails(0);
	$("ul#payment-type-selector").children("li").children("input.checkbox").click(function(){
		openDetails(200);
	});
<%
}
%>
});
<%
if (numOptions > 1) {
%>
function openDetails(speed) {
	var id = "";
	$("ul#payment-type-selector").children("li").children("input.checkbox:checked").each(function(){
		id = this.value;
	});
	var detailIds = ["<%=EventPaymentMethod.PAYMENT_ID_CC %>"
<%
	for (EventPaymentMethod pay : event.getPaymentMethods()) {
		if (!pay.isAdminOnly()) {
%>
	             	, "<%=pay.getPaymentMethodId() %>"
<%
		}
	}
%>
	             	];
	for (var i = 0; i < detailIds.length; i++) {
		var detailId = detailIds[i];
		if (id == detailId) {
			$("#details-" + detailId).fadeIn(speed);
		} else {
			$("#details-" + detailId).fadeOut(0);
		}
	}
}
<%
}
%>

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
				<div id="selected-tab">Questions</div>
				<div id="selected-tab">Payment</div>
				<div id="tabs">Confirm</div>
				
			</div>
			<div id="right-nav">
				<div id="program">
					<div id="reg-header">Payment Information</div>
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
			<form name="register" action="" method="post">
			<div id="reg-info">
			<%
			if (errors != null && errors.get("payment") != null) {
			%>
						<span class="error"><%=errors.get("payment") %></span>
			<%
			}
			if (numOptions > 1) {
			%>
						<h4><span>Payment Method</span></h4>
						<ul id="payment-type-selector" class="large-selection-list">
			<%
				if (event.isAllowCC()) {
			%>
							<li>
								<input class="checkbox" type="radio" name="payment" value="<%=EventPaymentMethod.PAYMENT_ID_CC %>"<%=registration.getPaymentMethodId()==EventPaymentMethod.PAYMENT_ID_CC?" checked=\"checked\"":"" %> />
								<span class="title">Credit Card</span>
							</li>
			<%
				}
				for (EventPaymentMethod pay : event.getPaymentMethods()) {
					if (!pay.isAdminOnly()) {
			%>
							<li>
								<input class="checkbox" type="radio" name="payment" value="<%=pay.getPaymentMethodId() %>"<%=registration.getPaymentMethodId()==pay.getPaymentMethodId()?" checked=\"checked\"":"" %> />
								<span class="title"><%=pay.getName() %></span>
							</li>
			<%
					}
				}
			%>
						</ul>
			<%
			} else {
			%>
								<input type="hidden" name="payment" value="<%=event.isAllowCC()?0:event.getPaymentMethods().get(0).getPaymentMethodId() %>" />
			<%
			}
			if (event.isAllowCC()) {
				String ccNumber = registration.getPaymentMethodDetailAnswers().get(EventPaymentMethodDetail.DETAIL_ID_CC_NUMBER);
				String expDate = registration.getPaymentMethodDetailAnswers().get(EventPaymentMethodDetail.DETAIL_ID_EXP_DATE);
				String csc = registration.getPaymentMethodDetailAnswers().get(EventPaymentMethodDetail.DETAIL_ID_CSC);
				String name = registration.getPaymentMethodDetailAnswers().get(EventPaymentMethodDetail.DETAIL_ID_NAME);
				String address = registration.getPaymentMethodDetailAnswers().get(EventPaymentMethodDetail.DETAIL_ID_ADDRESS);
				String city = registration.getPaymentMethodDetailAnswers().get(EventPaymentMethodDetail.DETAIL_ID_CITY);
				String region = registration.getPaymentMethodDetailAnswers().get(EventPaymentMethodDetail.DETAIL_ID_REGION);
				String postcode = registration.getPaymentMethodDetailAnswers().get(EventPaymentMethodDetail.DETAIL_ID_POSTCODE);
			%>
						<div id="details-<%=EventPaymentMethod.PAYMENT_ID_CC %>">
							<table class="data-entry">
								<tr>
									<th class="table-label" colspan="2">Credit Card Details</th>
								</tr>
								<tr>
									<th>
										<span>Credit Card Number</span>
									</th>
									<td>
										<input type="text" class="text" name="cc-number" value="<%=ccNumber!=null?ccNumber:"" %>" />
										<span class="field-example">Ex: 5111-1234-5678-9000 &nbsp; or &nbsp; 3411-123456-78900</span>
			<%
				if (errors != null && errors.get("cc-number") != null) {
			%>
										<span class="error"><%=errors.get("cc-number") %></span>
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
										<input type="text" class="text-short" name="exp-date" value="<%=expDate!=null?expDate:"" %>" />
										<span class="field-example">Ex: 0311</span>
			<%
				if (errors != null && errors.get("exp-date") != null) {
			%>
										<span class="error"><%=errors.get("exp-date") %></span>
			<%
				}
			%>
									</td>
								</tr>
								<tr>
									<th>
										<span>Card Security Code</span>
									</th>
									<td>
										<input type="text" class="text-short" name="csc" value="<%=csc!=null?csc:"" %>" />
										<span class="field-example"><a href="javascript: openCscExamples();">What's This?</a></span>
			<%
				if (errors != null && errors.get("csc") != null) {
			%>
										<span class="error"><%=errors.get("csc") %></span>
			<%
				}
			%>
									</td>
								</tr>
								<tr>
									<th>
										<span>Cardholder Name</span>
									</th>
									<td>
										<input type="text" class="text" name="name" value="<%=name!=null?name:(registration.getFirstName() + " " + registration.getLastName()) %>" />
			<%
				if (errors != null && errors.get("name") != null) {
			%>
										<span class="error"><%=errors.get("name") %></span>
			<%
				}
			%>
									</td>
								</tr>
								<tr>
									<th>
										<span>Billing Address</span>
									</th>
									<td>
										<input type="text" class="text" name="address" value="<%=address!=null?address:registration.getAddress() %>" />
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
										<input type="text" class="text" name="city" value="<%=city!=null?city:registration.getCity() %>" />
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
										<input type="text" class="text-short" name="region" value="<%=region!=null?region:registration.getRegion() %>" />
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
										<input type="text" class="text-short" name="postcode" value="<%=postcode!=null?postcode:registration.getPostCode() %>" />
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
							<span class="note">Note: You will not be charged until you click the Submit button on the Review page at the end of this process.</span>
							<span class="security-note"><em>Security Guarantee:</em> We support Secure Socket Layer (SSL) security features while processing your online transactions.  Credit card payments processed by <img src="image/paypal.jpg" style="position: relative; top: 4px;"/></span>
						</div>
			<%
			}
			for (EventPaymentMethod pay : event.getPaymentMethods()) {
				if (!pay.isAdminOnly() && pay.getPaymentMethodDetails().size() > 0) {
			%>
						<div id="details-<%=pay.getPaymentMethodId() %>">
							<table class="data-entry">
								<tr>
									<th class="table-label" colspan="2"><%=pay.getName() %> Details</th>
								</tr>
			<%
					boolean hasRequired = false;
					for (EventPaymentMethodDetail detail : pay.getPaymentMethodDetails()) {
						String detailValue = registration.getPaymentMethodDetailAnswers().get(detail.getPaymentDetailId());
						if (detail.isRequired()) {
							hasRequired = true;
						}
			%>
								<tr>
									<th>
				
										<span><%=detail.getName() %><%=detail.isRequired()?"&nbsp;*":"" %></span>
									</th>
									<td>
										<input type="text" class="text" name="detail-<%=pay.getPaymentMethodId() %>-<%=detail.getPaymentDetailId() %>" value="<%=detailValue!=null?detailValue:"" %>" />
			<%
						if (errors != null && errors.get("detail-" + pay.getPaymentMethodId() + "-" + detail.getPaymentDetailId()) != null) {
			%>
										<span class="error"><%=errors.get("detail-" + pay.getPaymentMethodId() + "-" + detail.getPaymentDetailId()) %></span>
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
			if (hasRequired) {
			%>
							<span class="required-note">
								<span>Questions followed by <em>*</em> are required.</span>
							</span>
			<%
			}
			%>
						</div>
			<%
				}
			}
			%>
						<div id="page-buttons">
							<a href="javascript: submitRegister();" id="next">
								<span>Confirm&nbsp;&nbsp;&#x25BA;</span>
							</a>
						</div>
						
						<div id="pre-page-buttons">
							<a href="javascript:  history.back();" id="previous">
								<span>&#x25C4;&nbsp; Additional Information </span>
							</a>
					    </div>
					</div>
					
					<div id="csc-examples" class="expandable">
						<a href="javascript: closeCscExamples();"><span>Close</span></a>
						<div style="text-align: center">
							<img src="image/card-security-codes.jpg" />
						</div>
					</div>
					<input type="hidden" name="controllerAction" value="<%=EventRegisterControlServlet.CONTROLLER_ACTION_NEXT %>">
					<input type="hidden" name="flowState" value="<%=EventRegisterControlServlet.FLOW_STATE_PAYMENT %>">
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
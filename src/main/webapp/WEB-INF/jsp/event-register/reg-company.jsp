<%@ page import="java.util.List,
                 java.util.Map,
                 com.hd.cedg.lms.model.LearningUser,
                 com.hd.cedg.lms.model.Event,
                 com.hd.cedg.lms.model.ReportCompany,
                 com.hd.cedg.lms.model.EventRegistration,
                 com.hd.cedg.lms.servlet.EventRegisterControlServlet" errorPage="../error.jsp" buffer="300kb"%>
<%
LearningUser user = (LearningUser) request.getAttribute("user");
List<ReportCompany> reportCompanies = (List<ReportCompany>) request.getAttribute("reportCompanies");
EventRegistration registration = (EventRegistration) request.getAttribute("registration");
Event event = registration.getEvent();

String formObjectSerialized = (String)request.getAttribute(EventRegisterControlServlet.FORM_OBJECT_NAME);
Map<String, String> errors = (Map<String, String>)request.getAttribute("errors");
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
	var companyid = $(this).closest("tr").children("td").children("input[name=companyId]").attr("value");
	document.register.companyId.value = companyid;
	if(companyid == 0){
		document.register.flowState.value = '<%=EventRegisterControlServlet.FLOW_STATE_USER%>';
	}else{
		document.register.companyName.value = $(this).closest("tr").children("td").children("input[name=companyName]").attr("value");
		document.register.companyType.value = $(this).closest("tr").children("td").children("input[name=companyType]").attr("value");
		document.register.allianceLevel.value = $(this).closest("tr").children("td").children("input[name=allianceLevel]").attr("value");
		document.register.fabricator.value = $(this).closest("tr").children("td").children("input[name=fabricator]").attr("value");
		document.register.salesRep.value = $(this).closest("tr").children("td").children("input[name=salesRep]").attr("value");
		document.register.address.value = $(this).closest("tr").children("td").children("input[name=address]").attr("value");
		document.register.city.value = $(this).closest("tr").children("td").children("input[name=city]").attr("value");
		document.register.region.value = $(this).closest("tr").children("td").children("input[name=region]").attr("value");
		document.register.postCode.value = $(this).closest("tr").children("td").children("input[name=postCode]").attr("value");
	}
	document.register.submit();
}
</script>
<%
	String regpage = "certification-home.jsp";
	String program = "CI";
	if (request.getParameter("program") != null) {
		program = request.getParameter("program").toUpperCase();
	}
	if (session.getAttribute("reg-page") != null) {
		regpage = session.getAttribute("reg-page").toString();
	}
%>

<!-- <div id="container">
	<div id="header">
		<div id="hdlogo"></div>
	</div>
	<div id="main-content"> -->
		<div id="navigations">
			<div id="standard-nav">
				<a href="home.jsp" onclick="#"><span id="home" /></a>
				<a href="certification-home.jsp" onclick="#"><span id="certifications" /></a>
			</div>	
			<div id="cert-levels">
				<a href="#" onclick="#" id="cert">
					<br/><span id="certificate"><%=program%></span><br/>
					<span id="level">Learn More</span>  <!-- ??? -->
				</a>
			</div>
		</div>
		<div id="cert-details">
			<div id="left-nav"></div>
			<div id="nav-tabs">
				<div id="selected-tab">Company</div>
				<div id="tabs">User</div>
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
					<img src="<%=event.getImageUrl()%>" />
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
					<div id="reg-info">
						<h4><span>The person you're registering works for which company?</span></h4>
						<table class="selection-table">
							<tr class="primary">
								<td><a href="javascript: void(0);"><span><%=user.getCompanyName().length()>30 ? (user.getCompanyName().substring(0, 27) + "...") : user.getCompanyName() %></span></a><input type="hidden" name="companyId" value="<%=user.getCompanyId() %>" /><input type="hidden" name="companyName" value="<%=user.getCompanyName() %>" /><input type="hidden" name="companyType" value="<%=user.getCompanyType() %>" /><input type="hidden" name="allianceLevel" value="<%=user.getAllianceLevel()!=null?user.getAllianceLevel():"" %>" /><input type="hidden" name="fabricator" value="<%=user.getSponsoringFabricatorName()!=null?user.getSponsoringFabricatorName():"" %>" /><input type="hidden" name="salesRep" value="<%=user.getSalesRepName()!=null?user.getSalesRepName():"" %>" /><input type="hidden" name="address" value="<%=user.getAddress1()!=null?user.getAddress1():"" %>" /><input type="hidden" name="city" value="<%=user.getCity()!=null?user.getCity():"" %>" /><input type="hidden" name="region" value="<%=user.getRegion()!=null?user.getRegion():"" %>" /><input type="hidden" name="postCode" value="<%=user.getPostCode()!=null?user.getPostCode():"" %>" /></td>
								<td><a href="javascript: void(0);"><span class="minor"><%=user.getCity() %></span></a></td>
								<td><a href="javascript: void(0);"><span class="minor"><%=user.getRegion() %></span></a></td>
								<td><a href="javascript: void(0);"><span class="arrow">&rarr;</span></a></td>
							</tr>
							<tr class="special">
								<td colspan="3"><a href="javascript: void(0);"><span>Company not listed</span></a><input type="hidden" name="companyId" value="0" /><input type="hidden" name="companyName" value="" /><input type="hidden" name="companyType" value="" /><input type="hidden" name="allianceLevel" value="" /><input type="hidden" name="fabricator" value="" /><input type="hidden" name="salesRep" value="" /><input type="hidden" name="address" value="" /><input type="hidden" name="city" value="" /><input type="hidden" name="region" value="" /><input type="hidden" name="postCode" value="" /></td>
								<td><a href="javascript: void(0);"><span class="arrow">&rarr;</span></a></td>
							</tr>
						
							<%
							for (ReportCompany reportCompany : reportCompanies) {
								int companyId = reportCompany.getCompanyId();
								if (user.getCompanyId() != companyId) {
							%>
										<tr>
											<td><a href="javascript: void(0);"><span><%=reportCompany.getCompanyName().length()>30 ? (reportCompany.getCompanyName().substring(0, 27) + "...") : reportCompany.getCompanyName() %></span></a><input type="hidden" name="companyId" value="<%=reportCompany.getCompanyId() %>" /><input type="hidden" name="companyName" value="<%=reportCompany.getCompanyName() %>" /><input type="hidden" name="companyType" value="<%=reportCompany.getCompanyType() %>" /><input type="hidden" name="allianceLevel" value="<%=reportCompany.getAllianceProgram()!=null?reportCompany.getAllianceProgram():"" %>" /><input type="hidden" name="fabricator" value="<%=reportCompany.getFabricator()!=null?reportCompany.getFabricator():"" %>" /><input type="hidden" name="salesRep" value="<%=reportCompany.getSalesRep()!=null?reportCompany.getSalesRep():"" %>" /><input type="hidden" name="address" value="<%=reportCompany.getAddress()!=null?reportCompany.getAddress():"" %>" /><input type="hidden" name="city" value="<%=reportCompany.getCity()!=null?reportCompany.getCity():"" %>" /><input type="hidden" name="region" value="<%=reportCompany.getRegion()!=null?reportCompany.getRegion():"" %>" /><input type="hidden" name="postCode" value="<%=reportCompany.getPostCode()!=null?reportCompany.getPostCode():"" %>" /></td>
											<td><a href="javascript: void(0);"><span class="minor"><%=reportCompany.getCity() %></span></a></td>
											<td><a href="javascript: void(0);"><span class="minor"><%=reportCompany.getRegion() %></span></a></td>
											<td><a href="javascript: void(0);"><span class="arrow">&rarr;</span></a></td>
										</tr>
							<%
								}
							}
							%>
						</table>
					</div>	
						<form name="register" action="" method="post">
							<input type="hidden" name="companyId" value="">
							<input type="hidden" name="companyName" value="">
							<input type="hidden" name="companyType" value="">
							<input type="hidden" name="allianceLevel" value="">
							<input type="hidden" name="fabricator" value="">
							<input type="hidden" name="salesRep" value="">
							<input type="hidden" name="address" value="">
							<input type="hidden" name="city" value="">
							<input type="hidden" name="region" value="">
							<input type="hidden" name="postCode" value="">
							<input type="hidden" name="controllerAction" value="<%=EventRegisterControlServlet.CONTROLLER_ACTION_NEXT %>">
							<input type="hidden" name="flowState" value="<%=EventRegisterControlServlet.FLOW_STATE_COMPANY %>">
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
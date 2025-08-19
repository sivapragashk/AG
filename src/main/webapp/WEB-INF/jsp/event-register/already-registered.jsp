<%@ page import="java.util.List,
                 java.util.Map,
                 com.hd.cedg.lms.model.LearningUser,
                 com.hd.cedg.lms.model.Event,
                 com.hd.cedg.lms.model.EventRegistration,
                 com.hd.cedg.lms.servlet.EventRegisterControlServlet"
         errorPage="../error.jsp" buffer="300kb"%><%
LearningUser user = (LearningUser) request.getAttribute("user");
LearningUser theUser = (LearningUser) request.getAttribute("theUser");
EventRegistration registration = (EventRegistration) request.getAttribute("registration");
Event event = registration.getEvent();

boolean isSelf = theUser != null && user.getUserId() == theUser.getUserId();
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
						<tr>
							<th>When</th>
							<td><%=event.getDateRange().getFormattedDateRange() %><br/>
							<%=event.getDateRange().getFormattedTime() %></td>
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
				
			</div>
			
			<div id="reg-info">
				<div id="message">
					<h3>
						<span><%=isSelf?"You're already registered!":"Already Registered!" %> <br><br></span>
					</h3>
					<span>We already have a registration record for <%=isSelf?"you":"this person" %>.  If you think this is incorrect, please contact <a href="help">Learning Center Customer Care</a>.</span>
				</div>
				<div id="page-buttons">
			<%
			if (!"".equals(registration.getReturnUrl())) {
			%>
						<a href="<%=registration.getReturnUrl() %>" id="next" class="large">
							<span>Return to Event Description&nbsp;&nbsp;&#x25BA;</span>
						</a>
			<%
			}
			%>
				</div>
			</div>
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
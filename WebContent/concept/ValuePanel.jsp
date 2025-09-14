<%
 String hostName=request.getServerName();
int port = request.getServerPort();

String sitePath =  "http://"+hostName;
String staticPath;
if(port != 80){
	sitePath += ":"+port;
}
sitePath += "/concepts";
staticPath = sitePath+"/static/";

%>
<%
	if(request.getParameter("midValue") == null){
%>
	<input type="text" size="4" class="midValue" id="primitiveConceptAttrValue" maxlength="2" />
<%
	} else {
%>
	<input type="text" size="4" class="midValue" id="primitiveConceptAttrValue" maxlength="2" value='<%=request.getParameter("midValue")%>'/>
	
<%
	}
%>
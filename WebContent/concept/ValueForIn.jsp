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
	if(request.getParameter("minValue") == null){
%>
	<input class="f_minValue" type="text" size="4"	id="primitiveConceptSAttrValue" maxlength="200" value=''/> -
	<input class="f_maxValue" type="text" size="4"	id="primitiveConceptEAttrValue" maxlength="200" value=''/>
<%
	} else {
%>
	<input class="f_minValue" type="text" size="4"	id="primitiveConceptSAttrValue" maxlength="200" value='<%=request.getParameter("minValue")%>'/> -
	<input class="f_maxValue" type="text" size="4"	id="primitiveConceptEAttrValue" maxlength="200" value='<%=request.getParameter("maxValue")%>'/>
<%
	}
%>
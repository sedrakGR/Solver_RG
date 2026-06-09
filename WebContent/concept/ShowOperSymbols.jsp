<%
	if(request.getParameter("operSymbs") == null) {
%>
		<select id="primitiveConceptAttrOper" class="primitiveConceptAttrOperStyle">
   			<option><%= "=" %></option>
			<option><%= "!=" %></option>
			<option><%="<"%></option>
			<option><%=">"%></option>
			<option><%= "<=" %></option>
			<option><%=">="%></option>
			<option><%= "IN" %></option>
			<option><%= "IS" %></option>
			<option><%= "NOT IS" %></option>
		</select>
<%
	} else {
%>
		<select id="primitiveConceptAttrOper" class="primitiveConceptAttrOperStyle">
        	<option><%= request.getParameter("operSymbs") %>
   			<option><%="=" %></option>
			<option><%="!=" %></option>
			<option><%="<"%></option>
			<option><%=">"%></option>
			<option><%="<=" %></option>
			<option><%=">="%></option>
			<option><%="IN" %></option>
			<option><%="IS" %></option>
			<option><%="NOT IS" %></option>
		</select>
<%
	}
%>
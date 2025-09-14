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


<div id="mainActionDiologContainer" class="mainActionDiologContainerStyle">
  <div id="actionSubDialogPanel" class="actionSubDialogPanelStyle"></div>
  <div id="actionDialogPanel" class="actionDialogPanelStyle">
   <center>
    <fieldset style="width:330px; height:170px">
    <legend>
      <b style="font-size:17px; text-align:left">Please Select Attribute Name</b><br /><br />
    </legend>
      <font style="font-size: 15px; text-decoration: underline;">Attribute Name</font><br/>	
      <select id="attrName" style="width:200px"> 
      	   <option></option>                  
      </select><br /><br />
      
      <div class="parentBunchClassPanelStyle" id="parentBunchClassPanel">
      	<select id="subAttrName" style="width:200px">
      		
      	</select>
      </div>
      
      <input type="button" value="Ok" id="okActionButton" class="dialogButtons"/>
      <input type="button" value="Cancel" id="cancelActionButton" class="dialogButtons" />
    
    </fieldset>
    </center>
   </div> 
</div>  
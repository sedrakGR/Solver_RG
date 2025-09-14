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

<div id="conceptsContentMainPanel" class="conceptsContentMainPanelStyle">

	<div id="dialogPlace" class="diologPlaceStyle"></div>
	<div id="conceptsDisplayPanel" class="conceptsDisplayPanelStyle"></div>
	
	<div id="conceptsControlPanel" class="conceptsControlPanelStyle">
		<div id="conceptsControlButtonsPanel" class="conceptsControlButtonsPanelStyle">
			<input type="button" value="New Abstract" id="newConceptCreator" />
			<input type="button" value="Save Abstract" id="saveConcept" />
		</div>		
		
		
		
		<div id="setAccordionPanel" class="sitAccordionPanelStyle">				
				<div class="sitConceptsTree" id="setConceptsTree">
					<div class="setConceptsTreeHeader" id="setConceptsTreeHeader">Abstract Tree</div>								
					<div id="conceptsListPanel" class="setListBody">			
					</div>
					<div class="actionListConc" id="actionListConc">
						<div class="setListHeader" id="actionListHeaderConc">Actions</div>
						<div class="setListBody" style="display:none" id="actionListBodyConc">
						</div>
					</div>	
					<div class="setList" id="setListConc">
						<div class="setListHeader" id="setListHeaderConc">Set List</div>
						<div class="setListBody" style="display:none" id="setListBodyConc">
						</div>
					</div>								
				</div>					
			</div>
	</div>
	
</div>
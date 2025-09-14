
<div class="actionContainer" id="actionContainer">
	
	<div class="actionTitleBar">
	<div class="closeNuc closeicon">
	</div>
	<div class="helpNuc helpicon">
	</div>
	</div>
	
	<div class="actionHeader actionHeaderStyle">
		<div class="actionNameField actionNameFieldStyle"  contenteditable="true" id="actionNameField">
			New Action
		</div>		
	</div>
		
<div id="preActionPanel">
	<div class="preActionAttrPanel preActionAttrPanelStyle" id="preActionAttrPanel">	  	
		<div class="preActionAttrChecker">
			<input type="checkbox" class="checker"> 
		</div>
		<div class="preActionAttrName" contenteditable="true">
			New Attribute
		</div>
		<div class="preActionAttrParentConc" id="preActionAttrParentConc">
			<select class="preActionParent"></select>
		</div>
		<div class="preActionSubAttrDisplay">
			<button disabled="disabled">+</button>
		</div>
		<div class="preActionSubAttrStyle">		
		</div>
	</div>
</div>

<div id="postActionPanel">
	<div class="postActionAttrPanel postActionAttrPanelStyle" id="postActionAttrPanel">	  	
		<div class="postActionAttrChecker">
			<input type="checkbox" class="checker"> 
		</div>
		<div class="postActionAttrName" contenteditable="true">
			New Attribute
		</div>
		<div class="postAttrOper">
			<select>
				<option><%="=" %></option>
			</select>
		</div>
		<div class="postActionAttrValuePanel" id="postActionAttrValuePanel">
			<input type="text" id="postActionValue" class="postActionValue">
		</div>
		<div class="postActionSubAttrStyle">		
		</div>
	</div>
</div>
		
	<div class="actionAttrContainer actionAttrContainerStyle" id="actionAttrContainer">
		<div id="preCondiotionContainer">
			<div class="preHeaderPanel">
				<div class="precondition">Precondition</div>
				<div class="preActionControlButtonsPanel">
					<input type="button" value="Add" class="preActionControlButton" id="addActionPreAttr" />
					<input type="button" value="Remove" class="preactionControlButton" id="removeActionPreAttr" />
				</div>
			</div>
			<div id="preAttr"></div>
		</div>
		
		<div id="postCondiotionContainer">
			<div class="postHeaderPanel">
				<div class="postcondition">Postcondition</div>
				<div class="postActionControlButtonsPanel">
					<input type="button" value="Add" class="postActionControlButton" id="addActionPostAttr" />
					<input type="button" value="Remove" class="postActionControlButton" id="removeActionPostAttr" />
				</div>
			</div>
			<div id="postAttr"></div>
		</div>
					
	</div>  
</div>
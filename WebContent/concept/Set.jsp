
<div class="setContainer" id="setContainer">
	
	<div class="setTitleBar">
	<div class="closeNuc closeicon">
	</div>
	<div class="helpNuc helpicon">
	</div>
	</div>
	
	<div class="setHeaderPanelStyle" id="setHeaderPanel">
		<div class="setNameField setNameFieldStyle" id="setNameField" contenteditable="true">New Set</div>
		<div class="setAttrAddButtonPanelStyle">	
			<input type="button" id="addSetAttrs" value="add" />
			<input type="button" id="removeSetAttrs" value="remove" />	
		</div>
	</div>
	
<div id="setPanel1" style="width: none; height: none;">
	<div class="setAttrPanel setAttrPanelStyle" id="setAttrPanel1">
		<div class="setAttrCheckerPanel setAttrCheckerStyle">
			<input type="checkbox" class="setAttrChecker">
		</div>
		<div class="setAttrName setAttrNameStyle" contenteditable="true">
			New Attribute
		</div>
		<div class="setAttrParent">
			<select class="setAttrParentSelect"></select>
		</div>
		<div class="setSubAttrDispCont">
			<button disabled="disabled">+</button>
		</div>
		<div class="setSubAttrContainer setSubAttrContainerStyle">								
		</div>
	</div>
</div>

<div id="setPanel" style="width: none; height: none;">
	<div class="countLineStyle setAddAttrPanel setAddAttrPanelStyle" id="setAddAttrPanel">
		<div class="setAddAttrCheckerPanel setAddAttrCheckerStyle">
			<input type="checkbox" class="setAddAttrChecker">
		</div>
		<div class="setAddAttrName addAttrtLineNameFieldStyle"  contenteditable="true">New Addit Attr</div>
		<div class="addAttrOperPanel">
			<select id="addAttrOper">
				<option><%="="%></option>
				<option><%="!="%></option>
				<option><%=">"%></option>
				<option><%="<"%></option>
				<option><%=">="%></option>
				<option><%="<="%></option>
			</select>
		</div>
		<div class="addAttrsValueField">
			<input type="text" size="2" class="txt"> 
		</div>		
	</div>
</div>
	<div class="setAttrContainerStyle" id="setAttrContainer">
		<div class="setAttrStyle" id="setAttr"></div>
		<div class="setAdditAttrStyle" id="setAdditAttr">
			<div class="setAdditionalAttrHeaderStyle" id="setAdditionalAttrHeader">
				<div class="setAdditionalAttrStyle" id="setAdditionalAttr">
					Additional Attrs
				</div>
				<div class="setAdditionalButtonPanelStyle" id="setAdditionalButtonPanel">
					<input type="button" id="addSetAddAttrs" value="add" />
					<input type="button" id="removeSetAddAttrs" value="remove" />
				</div>
			</div>
			
			<div id="setAddAttrsCont">
				<div class="countLineStyle" id="minCount">
					<div></div>
					<div class="countLineNameFieldStyle">minCount</div>
					<div class="countLineOperPanel">
						<select id="minCountLineOper">
							<option><%="="%></option>
							<option><%="!="%></option>
							<option><%=">"%></option>
							<option><%="<"%></option>
							<option><%=">="%></option>
							<option><%="<="%></option>
						</select>
					</div>
					<div class="countLineValueField">
						<input type="text" size="2" id="minCountValue" class="txt"> 
					</div>
				</div>
				<div class="countLineStyle" id="minCount">
					<div></div>
					<div class="countLineNameFieldStyle">maxCount</div>
					<div class="countLineOperPanel">
						<select id="maxCountLineOper">
							<option><%="="%></option>
							<option><%="!="%></option>
							<option><%=">"%></option>
							<option><%="<"%></option>
							<option><%=">="%></option>
							<option><%="<="%></option>
						</select>
					</div>
					<div class="countLineValueField">
						<input type="text" size="2" id="maxCountValue" class="txt"> 
					</div>
				</div>
			</div>
		</div>
	</div>
	
</div>
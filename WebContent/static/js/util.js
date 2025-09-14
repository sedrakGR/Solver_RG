function getCompositeAttrPanel() {
	var panel = $('.compositeAttr').html();
	return panel;
}

function getParentFullName(initial, parent_prefix, attr_name, parent) {
	if(initial == true) {
		if(parent_prefix != "") {
			return parent_prefix + "." + attr_name;
		}
		return attr_name;
	}
	return parent;
}

function fillCompositeAttrIntoDiv(element, json, initial, parent_prefix) {	

	var panel = getcompositeChildAttrSkeleton();

	element.append('<div class="tempAttrChild"></div>');
	$.each(json.compConceptAttrs, function(j) {
		var attr_json = json.compConceptAttrs[j];

		element.find('.tempAttrChild').append('<div class="temp"></div>');
		var temp = element.find('.tempAttrChild').find('.temp');
		temp.append(panel);

		temp.find('.composConcChildAttrName').empty().append(attr_json.name);
		
		var parent = getParentFullName(initial, parent_prefix, attr_json.name, attr_json.parent);
		
		temp.find('.composConcChildAttrParent').prepend('<option value="'+attr_json.type+'">'+parent+'</option>');
		
		if(attr_json.negated == "1") {
			temp.find('.composConcChildAttrNegater').empty().append('<input class="negated" type="text" value="1">');
		}

		var attr_element = temp.find('.compChildAttrChildren');
		switch (attr_json.type)
		{
			case 'p':
			case 'n':
				fillNucleusConceptIntoDiv(attr_element, attr_json);
				break;
			case 's':
				fillSetAttrIntoDiv(attr_element, attr_json, initial, parent);
				break;
			case 'c':
				fillCompositeIndexAttrs(temp.find('#compChildIndexAttributes').empty(), attr_json);
				fillCompositeAttrIntoDiv(attr_element, attr_json, initial, parent);
				break;
		}

		var tmp = temp.html();
		element.append(tmp);
		temp.remove();
	});
	element.find('.tempAttrChild').remove();
	
}

function fillSetAttrIntoDiv(external_div, json, initial, parent_prefix) {
	external_div.append(getSetBodySkeleton());
	fillSetAttrIntoDivInternal(external_div, json, initial, parent_prefix);
}

function fillSetAttrIntoDivInternal(set_body, json, initial, parent_prefix) {
	var setAttrs = $('.setElementAttrPanel').html();
	var element = set_body.find('#setAttr');
	set_body.append('<div class="tempAttrChild"></div>');
	
	$.each(json.attribute.compConceptAttrs, function(j) {
		set_body.find('.tempAttrChild').append('<div class="temp"></div>');
		var temp = set_body.find('.tempAttrChild').find('.temp');
		temp.append(setAttrs);
		var attr_json = json.attribute.compConceptAttrs[j];
		var parent = getParentFullName(initial, parent_prefix, attr_json.name, attr_json.parent);
		temp.find('.setAttrName').empty().append(attr_json.name);
		temp.find('.setAttrParentSelect').prepend('<option value="'+attr_json.type+'">'+parent+'</option>');
		
		var attr_element = temp.find('.setAttrChildren');
		switch (attr_json.type)
		{
			case 'p':
			case 'n':
				fillNucleusConceptIntoDiv(attr_element, attr_json);
				break;
			case 's':
				fillSetAttrIntoDiv(attr_element, attr_json, initial, parent);
				break;
			case 'c':
				var attr_index_element = set_body.find('#setChildAttributeIndex');
				fillCompositeIndexAttrs(attr_index_element.empty(), attr_json);
				fillCompositeAttrIntoDiv(attr_element, attr_json, initial, parent);
				break;
		}
		var tmp = temp.html();
		element.append(tmp);
		temp.remove();
	});
	var attributes = set_body.find('#setAttrContainer').find('#setAddAttrsCont');
	
	var count = json.setManAttr[0];
	var min_json = count.value.minValue;
	var max_json = count.value.maxValue;

	attributes.find('#minCountLineOper').prepend('<option>'+'<'+'</option>');
	attributes.find('#minCount').find('.countLineValueField').html('<input value="'+ min_json +'" size="2" id="minCountValue" type="text">');
	
	attributes.find('#maxCountLineOper').prepend('<option>'+'='+'</option>');
	attributes.find('#maxCount').find('.countLineValueField').html('<input value="'+ max_json +'" size="2" id="maxCountValue" type="text">');
	
/*
TODO THIS SHALL BE CAREFULLY FIXED IF EVER ADDED!	
	var setAddAttrs = $('.setAddAttrPanel').html();	
	var addAttrs = set_body.find('#setAdditAttr');
	var div = null;
	$.each(json.setAddAttr, function(i) {
		alert($.toJSON(json.setAddAttr));
		div = set_body.find('.tempAttrChild').children("#setAdditAttr").find('#setAddAttrsCont');
		var name = json.setAddAttr[i].name;
		if (name == "minCount" || name == "maxCount") {
			return;
		}
		else {
			div.append('<div class="temp"></div>');
			var setAddAttrElement = div.find('.temp');
			setAddAttrElement.append(setAddAttrs);
			var attr_json = json.setAddAttr[i];
			
			setAddAttrElement.find('.setAddAttrName').empty();
			setAddAttrElement.find('.setAddAttrName').html(attr_json.name);
			
			var oper = attr_json.oper;
			setAddAttrElement.find('#addAttrOper').prepend('<option>' + oper + '</option>');
			
			setAddAttrElement.find('.addAttrsValueField').html('<input value="' + attr_json.value + '"  size="2" class="txt" type="text">');
			
			var tempAttrs = setAddAttrElement.html();
			div.append(tempAttrs);
			setAddAttrElement.remove();
		}
	});
	set_body.append(div);
*/
	set_body.find('.tempAttrChild').remove();
}

function fillNucleusConceptIntoDiv(element, json) {

	var primIndex = getPrimitiveIndexSkeleton();
	var primValue = getPrimitiveValueSkeleton();
	$.each(json.nucleusConceptIndexAttrs, function(i){

		element.append('<div id="temp" style="display:block;"></div>');
		element.find('#temp').append(primIndex);
		$('#temp').find('.primAttrNameField').empty();
		$('#temp').find('.primAttrNameField').html(json.nucleusConceptIndexAttrs[i].name);
								
		if (json.nucleusConceptIndexAttrs[i].oper == 'IN') {
			$('#temp select').prepend('<option>'+json.nucleusConceptIndexAttrs[i].oper+'</option>');
			$('#temp').find('div#Interval').show();
			$('#temp').find('div#Interval').empty();
			$('#temp').find('div#nonInterval').hide();
			$('#temp').find('#Interval').append('<input class="f_minValue" type="text" value="' + json.nucleusConceptIndexAttrs[i].value.minValue + '" maxlength="200"> - ');
			$('#temp').find('#Interval').append('<input class="f_maxValue" type="text" value="' + json.nucleusConceptIndexAttrs[i].value.maxValue + '" maxlength="200">');
									
		}
		else {
			$('#temp select').prepend('<option>'+json.nucleusConceptIndexAttrs[i].oper+'</option>');
			$('#temp').find('div#Interval').hide();
			$('#temp').find('div#nonInterval').show();
			$('#temp').find('div#nonInterval').empty();
			$('#temp').find('#nonInterval').append('<input class="midValue" type="text" value="' + json.nucleusConceptIndexAttrs[i].value + '" maxlength="200">');
		}
		$('#temp').find('.primAttrNameField').addClass('static');
		var temp = $('#temp').html();
		element.append(temp);
		$('#temp').remove();
	});

	$.each(json.nucleusConceptValueAttrs, function(i){
			element.append('<div id="temp" style="display:block;"></div>');
			element.find('#temp').append(primValue);
			$('#temp').find('.primAttrNameField').empty();
			$('#temp').find('.primAttrNameField').html(json.nucleusConceptValueAttrs[i].name);
			
			if (json.nucleusConceptValueAttrs[i].oper == 'IN') {
				$('#temp select').prepend('<option>'+json.nucleusConceptValueAttrs[i].oper+'</option>');
				$('#temp').find('div#Interval').show();
				$('#temp').find('div#Interval').empty();
				$('#temp').find('div#nonInterval').hide();
				$('#temp').find('#Interval').append('<input class="f_minValue" type="text" value="' + json.nucleusConceptValueAttrs[i].value.minValue + '" maxlength="200"> - ');
				$('#temp').find('#Interval').append('<input class="f_maxValue" type="text" value="' + json.nucleusConceptValueAttrs[i].value.maxValue + '" maxlength="200">');
				
			}
			else {
				$('#temp select').prepend('<option>'+json.nucleusConceptValueAttrs[i].oper+'</option>');
				$('#temp').find('div#Interval').hide();
				$('#temp').find('div#nonInterval').show();
				$('#temp').find('div#nonInterval').empty();
				$('#temp').find('#nonInterval').append('<input class="midValue" type="text" value="' + json.nucleusConceptValueAttrs[i].value + '" maxlength="200">');
			}
			$('#temp').find('.primAttrNameField').addClass('static');
			var temp = $('#temp').html();
			element.append(temp);
			$('#temp').remove();
	});
}

function fillCompositeIndexAttrs(element, json){
	var indexPanel = getCompositeIndexSkeleton();
    $.each(json.compositeConceptIndexAttrs, function(i){
        element.append('<div id="ind_temp" style="display:block;"></div>');
        element.find('#ind_temp').append(indexPanel);
        $('#ind_temp').find('.f_attrName').empty();
        $('#ind_temp').find('.f_attrName').html(json.compositeConceptIndexAttrs[i].name);
		var oper = json.compositeConceptIndexAttrs[i].oper;
        if (oper == 'IN') {
			$('#ind_temp select').prepend('<option>'+oper+'</option>');
			$('#ind_temp').find('div#Interval').show();
			$('#ind_temp').find('div#Interval').empty();
			$('#ind_temp').find('div#nonInterval').hide();
			$('#ind_temp').find('#Interval').append('<input class="f_minValue" type="text" value="' + json.compositeConceptIndexAttrs[i].value.minValue + '" > - ');
			$('#ind_temp').find('#Interval').append('<input class="f_maxValue" type="text" value="' + json.compositeConceptIndexAttrs[i].value.maxValue + '" >');
									
		}
		else {
			$('#ind_temp select').prepend('<option>'+oper+'</option>');
			$('#ind_temp').find('div#Interval').hide();
			$('#ind_temp').find('div#nonInterval').show();
			$('#ind_temp').find('div#nonInterval').empty();
			$('#ind_temp').find('#nonInterval').append('<input class="midValue" type="text" value="' + json.compositeConceptIndexAttrs[i].value + '" >');
		}
		$('#ind_temp').find('.primAttrNameField').addClass('static');
        var ind_temp = $('#ind_temp').html();
        element.append(ind_temp);
        $('#ind_temp').remove();
    });
}

function getSetBodySkeleton() {
	var skeleton = $('.setPanel').find('#setAttrContainer');
	return skeleton;
}
function getPrimitiveIndexSkeleton() {
	var skeleton = $('.primitiveIndex').html();
	return skeleton;
}
function getPrimitiveValueSkeleton() {
	var skeleton = $('.primitiveValue').html();
	return skeleton;
}
function getcompositeChildAttrSkeleton() {
	var skeleton = $('.compositeChildAttr').html();
	return skeleton;
}
function getCompositeIndexSkeleton() {
	var skeleton = $('.compositeIndex').html();
	return skeleton;
}

function extractChildComposConc(divname, attr) {
	var compConceptChildAttrs = [];
	var type;
	var compChildAttrsArray;
	var childcompositeName;
	var compChildAttrParent;
	$(divname).find(attr).children().each(function(index){
		childcompositeName = $(this).children(":first").find('.composConcChildAttrName').text();
		compChildAttrParent = $(this).children(":first").find('.composConcChildAttrParent option:selected').text();
		type = $(this).children(":first").find('.composConcChildAttrParent option:selected').val();
		
		var isNegated = $(this).children(":first").find('.composConcChildAttrNegater').find('.negated').val();
		
		switch (type) {
		case 'n':
		case 'p':
			compChildAttrsArray = extractNucPrimCompConcWithNegation(type, childcompositeName, compChildAttrParent, $(this), isNegated);
			break;
		case 's':
			compChildAttrsArray = extractAttrSetWithNegation($(this), childcompositeName, compChildAttrParent, isNegated);
			break;
		case 'c':
			var indexElement = $(this).find('#compChildIndexAttributes');
			compChildAttrsArray = {
				'name': childcompositeName,
				'parent': compChildAttrParent,
				'type': type,
				'negated': isNegated,
				'compositeConceptIndexAttrs': extractIndexAttr(indexElement),
				'compConceptAttrs': extractChildComposConc($(this), '.compChildAttrChildren')
			};
			break;
		}
		compConceptChildAttrs[index] = compChildAttrsArray;
	});
	
	return compConceptChildAttrs;
}

function extractAttrSet(divname, className, parentName) {
	var negated = "";
	return extractAttrSetWithNegation(divname, className, parentName, negated);
}

function extractAttrSetWithNegation(divname, className, parentName, negated) {
	var setConceptAttrs = [];
	var errorOccured=false;
	var dummyNegation = "";
	divname.find('#setAttr').children().each(function(index) {
		var setAttrName = $(this).children(".setParentAttribute").children('.setAttrName').text();
		var setAttrParent = $(this).children(".setParentAttribute").find('.setAttrParent option:selected').text();
		
		if (setAttrParent == '--Select Parent--') {
			errorOccured = true;
			return false;
		}
		else {
			var type = $(this).find('.setAttrParentSelect option:selected').val();
			var setAttrsArray;
			switch (type) {
			case 'p':
			case 'n':
				setAttrsArray = extractNucPrimCompConcWithNegation(type, setAttrName, setAttrParent, $(this), dummyNegation);
				break;
			case 's':
				setAttrsArray = extractAttrSetWithNegation($(this).children('.setAttrChildren'), setAttrName, setAttrParent, dummyNegation);
				break;
			case 'c':
				var indexElement = $(this).find('#setChildAttributeIndex');
				
				setAttrsArray = {
							'name': setAttrName,
							'parent': setAttrParent,
							'type': type,
							'negated': dummyNegation,
							'compositeConceptIndexAttrs': extractIndexAttr(indexElement),
							'compConceptAttrs': extractChildComposConc($(this), '.setAttrChildren')
				};
				break;
			}
			setConceptAttrs[index] = setAttrsArray;
		}
	});
	if (!errorOccured) {
		
		var set_object = {
			'cr1': '0',
			'name': 'element',
			'parent': '',
			'compositeConceptIndexAttrs': [],
			'compConceptAttrs': setConceptAttrs
		};
		
		var object = {
			'name': className,
			'parent': parentName,
			'type': 's',
			'negated': negated,
			'attribute': set_object,
			'setManAttr': extractSetManAttribues(divname),
			'setAddAttr': extractSetAttributes(divname)
		};

		alert("json is : \n\n" + $.toJSON(object));
		return object;
	}
	else {
		alert('Please select the attribute parents');
		return false;
	}
}

function extractSetManAttribues(divname)
{
	var setManAttr = [];
	var minCountVal = divname.children("#setAttrContainer").children("#setAdditAttr").find('#minCountValue').val();
	var maxCountVal = divname.children("#setAttrContainer").children("#setAdditAttr").find('#maxCountValue').val();
		
	var countAttr = {
		"name": "count",
		"oper": "IN",
		"value": { "minValue": minCountVal,
				   "maxValue": maxCountVal
		}
	};
	setManAttr[0] = countAttr;
	return setManAttr;
}

function extractSetAttributes(divname){
	var setAddAttr = [];
	divname.children("#setAttrContainer").children("#setAdditAttr").find('#setAddAttrsCont').children('#setAddAttrPanel').each(function(index){
		var name = $(this).find('.setAddAttrName').text();
		var oper = $(this).find('#addAttrOper option:selected').text();
		var value = $(this).find('.txt').val();
			
		var addAttr = {
			"name": name,
			"oper": oper,
			"value": value
		};
			
		setAddAttr[index] = addAttr;
	});
	return setAddAttr;
}

function extractNucPrimCompConc(type, compAttrName, compAttrParent, divname){
	var negated = ""; // False, not negated by default.
	return extractNucPrimCompConcWithNegation(type, compAttrName, compAttrParent, divname, negated);
}

function extractNucPrimCompConcWithNegation(type, compAttrName, compAttrParent, divname, negated){
	var nucleusConceptIndexAttrs;
	var nucleusConceptValueAttrs;
	
	nucleusConceptIndexAttrs = extractIndexAttr(divname);
	nucleusConceptValueAttrs = extractValueAttr(divname);
	
	var compAttrsArray = {
		'name': compAttrName,
		'parent': compAttrParent,
		'type': type,
		'negated': negated,
		'nucleusConceptIndexAttrs': nucleusConceptIndexAttrs,
		'nucleusConceptValueAttrs': nucleusConceptValueAttrs
	};
	return compAttrsArray;
}

function extractIndexAttr(divname){
	var conceptIndexAttrs = [];
	$(divname).find('.f_bunchAttrPanel').each(function(i){
		var attrName = $(this).find('.f_attrName').text();
		var attrOper = $(this).find('#nucComboOper option:selected').text();
		var midvalue = $(this).find('.midValue').val();
		var maxvalue = $(this).find('.f_maxValue').val();
		var minvalue = $(this).find('.f_minValue').val();
		
		var attrArr;
		if (midvalue == '' || midvalue == null) {
			attrArr = {
				'name': attrName,
				'oper': attrOper,
				'value': {
					'minValue': minvalue,
					'maxValue': maxvalue
				}
			};
		}
		else {
			attrArr = {
				'name': attrName,
				'oper': attrOper,
				'value': midvalue
			};
		}
		conceptIndexAttrs[i] = attrArr;
	});
	return conceptIndexAttrs;
}

function extractValueAttr(divname){
	var conceptValueAttrs = [];	
	$(divname).find('.v_bunchAttrPanel').each(function(i){
		var attrName = $(this).find('.f_attrName').text();
		var attrOper = $(this).find('#nucComboOper option:selected').text();
		var midvalue = $(this).find('.midValue').val();
		var maxvalue = $(this).find('.f_maxValue').val();
		var minvalue = $(this).find('.f_minValue').val();
		
		var attrArr;
		if (midvalue == '' || midvalue == null) {
			attrArr = {
				'name': attrName,
				'oper': attrOper,
				'value': {
					'minValue': minvalue,
					'maxValue': maxvalue
				}
			};
		}
		else {
			attrArr = {
				'name': attrName,
				'oper': attrOper,
				'value': midvalue
			};
		}
		conceptValueAttrs[i] = attrArr;
	});
	return conceptValueAttrs;
}

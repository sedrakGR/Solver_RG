/**
 * 
 */

$('.actionTitleBar').live({

	  "mousedown": function(e) {
		inActiveWin();
		$(this).parent().draggable({containment: 'parent', refreshPositions: true}).trigger(e); 
	  	$(this).parent().addClass('actionClassActive');
      },
	  "mouseup": function() {
	  	$('#conceptsDisplayPanel').children().removeClass('ui-draggable');
	  }
});

$('#actionContainer').live({
	  "focus": function() {
		inActiveWin();
		$(this).addClass('actionClassActive');
	  }
});

//add panel
$('#addActionPreAttr').live('click', function(){
		var children = $('#preAttr').children().length;
		if (children < 10) {
		var actionattr = $('.preConditionPanel').html();
		$('.actionClassActive').find('#preAttr').append(actionattr);
		$('.actionClassActive').find('#preAttr').children().show();
		}
		else { alert('Error: Maximum attributes reached'); return false;}
});

//add post panel
$('#addActionPostAttr').live('click', function(){
		var children = $('#postAttr').children().length;
		if (children < 10) {
		var actionattr = $('.postConditionPanel').html();
		$('.actionClassActive').find('#postAttr').append(actionattr);
		$('.actionClassActive').find('#postAttr').children().show();
		}
		else { alert('Error: Maximum attributes reached'); return false;}
});

//remove PreAction
$('#removeActionPreAttr').live('click', function(){ 
	$('.actionClassActive').find('#preAttr').children().each(function(){
     if ($(this).find('input:checked.checker').length == 1) {
           $(this).remove(); 
     }
});
});

//remove PostAction
$('#removeActionPostAttr').live('click', function(){ 
	$('.actionClassActive').find('#postAttr').children().each(function(){
     if ($(this).find('input:checked.checker').length == 1) {
           $(this).remove(); 
     }
});
});

//edit set field name
$('.actionNameField').live({
   'click': function(){
   	var ispresent = $(this).children().hasClass('tempName');
   	if ($(this).text().trim() == 'New Action' && !ispresent) {
   		$(this).empty();
   		$(this).append('<input type="text" class="tempName" style="width: 100px; background: transparent;" maxlength="50"/>');
   		$('.tempName').focus();
   	} else { $(this).focusout(); return false; }
   },
   'dblclick': function(){
		var ispresent = $(this).children().hasClass('tempName');
		if ($(this).text().trim() != 'New Action' && !ispresent) {
			$(this).empty();
			$(this).append('<input type="text" class="tempName" style="width: 100px; background: transparent;" maxlength="50"/>');
			$('.tempName').focus();
		}
		else { $(this).focusout(); return false; }
	},
   'focusout': function() {
   		if ($('.tempName').val().length == 0) {
		$(this).append('New Action');
		$('.tempName').remove();
		}	else { 
		var name = $(this).find('.tempName').val();
		$(this).append(name);
		$('.tempName').remove();
		return false; }
   }
});

//edit preaction attribute names
$('.preActionAttrName').live({
   'click': function(){
   	var ispresent = $(this).children().hasClass('tempName');
   	if ($(this).text().trim() == 'New attribute' && !ispresent) {
   		$(this).empty();
   		$(this).append('<input type="text" class="tempName" style="width: 100px; background: transparent;" maxlength="13"/>');
   		$('.tempName').focus();
   	} else { return false; }
   },
   'dblclick': function(){
		var ispresent = $(this).children().hasClass('tempName');
		if ($(this).text().trim() != 'New attribute' && !ispresent) {
			$(this).empty();
			$(this).append('<input type="text" class="tempName" style="width: 100px; background: transparent;" maxlength="13"/>');
			$('.tempName').focus();
		}
		else { return false; }
	},
   'focusout': function() {
   		var name = $(this).find('.tempName').val();
   		if (name == '' || name == null) {
		$(this).append('New attribute');
		$('.tempName').remove();
		}	else { 
		$(this).append(name);
		$('.tempName').remove();
		return false; }
   }
});

//edit post action names 
$('.postActionAttrName').live({
   'click': function(){
   	var ispresent = $(this).children().hasClass('tempName');
   	if ($(this).text().trim() == 'New attribute' && !ispresent) {
   		$(this).empty();
   		$(this).append('<input type="text" class="tempName" style="width: 100px; background: transparent;" maxlength="50"/>');
   		$('.tempName').focus();
   	} else { return false; }
   },
   'dblclick': function(){
		var ispresent = $(this).children().hasClass('tempName');
		if ($(this).text().trim() != 'New attribute' && !ispresent) {
			$(this).empty();
			$(this).append('<input type="text" class="tempName" style="width: 100px; background: transparent;" maxlength="50"/>');
			$('.tempName').focus();
		}
		else { return false; }
	},
   'focusout': function() {
   		var name = $(this).find('.tempName').val();
   		if (name == '' || name == null) {
		$(this).append('New attribute');
		$('.tempName').remove();
		}	else { 
		$(this).append(name);
		$('.tempName').remove();
		return false; }
   }
});

$('.saveAct').live('click', function(event) {
		saveAction();
});


/// Parent select ///

$('#preActionAttrPanel').live({
      "mousedown": function() {
		$('.actionClassActive').find('#preAttr').children().removeClass('actionAttrActive');
        $(this).addClass('actionAttrActive');
      }
});

$('.preActionParent').live('change',function(event) {
	var value = $('.actionAttrActive').find('.preActionParent option:selected').val();
	var name = $('.actionAttrActive').find('.preActionParent option:selected').text();
	if (name != '--Select Parent--') {
		switch (value) {
			
			case 'p':
				var url = 'viewPrimitive.action';
				$.ajax({
					url: url,
					type: 'POST',
					data: {
						primitiveName: name
					},
					datatype: 'json',
					success: function(json){
						$('.actionAttrActive').find('.actionChildAttribute').empty();
						var escaped = json.trim().replace(/'/g, '"');
						var JSON = $.parseJSON(escaped);
						fillNucleusinActionWindow(JSON);
					}
				});
				break;
			
			case 'n':
				var url = 'viewNucleus.action';
				$.ajax({
					url: url,
					type: 'POST',
					data: {
						nucleusName: name
					},
					datatype: 'json',
					success: function(json){
						$('.actionAttrActive').find('.actionChildAttribute').empty();
						var escaped = json.trim().replace(/'/g, '"');
						var JSON = $.parseJSON(escaped);
						fillNucleusinActionWindow(JSON);
					}
				});
				break;
			
			case 'c':
				var url = 'viewComposite.action';
				$.ajax({
					url: url,
					type: 'POST',
					data: {
						compositeName: name
					},
					datatype: 'json',
					success: function(json){
						$('.actionAttrActive').find('.actionChildAttribute').empty();
						var escaped = json.trim().replace(/'/g, '"');
						var JSON = $.parseJSON(escaped);
						fillInActionAttrWindow(JSON, name);
					}
				});
				break;
		}
	} else { $('.actionAttrActive').find('.actionChildAttribute').empty(); return false;}
});

$('.showActionAttrChild').live('click', function(){
	if (!($('.actionAttrActive').find('.actionChildAttribute').is(':empty'))) {
		if ($('.actionAttrActive').find('.actionChildAttribute').is(':visible')) {
			$('.actionAttrActive').find('.actionChildAttribute').hide();
			$('.actionAttrActive').find('.showActionAttrChild').removeAttr('style');
		}
		else {
			$('.actionAttrActive').find('.actionChildAttribute').show();
			$('.actionAttrActive').find('.showActionAttrChild').css('background-position', 'bottom center');
		}
	} else {return false;}
});

//close Action window
$('.closeAct').live('click', function(event){
	  	$('#conceptsDisplayPanel').children().draggable('destroy');
		$('.actionClassActive').fadeOut(300, function() { $(this).remove(); });
});

// List
$('.ActionTab').live({
	'mousedown': function() {
		$.each($('#conceptsListPanel').children(), function() {
			$(this).find('.ActionNameInTableClose').hide();
		});
		$(this).parent().find('.ActionNameInTableClose').show();
	},
	'click': function() {
		var name = $(this).text();
		var url = 'viewAction.action';
		$.ajax({
			url: url,
			type: 'POST',
			datatype: 'json',
			data: {actionName: name},
			success: function(json) {
				var escaped = json.trim().replace(/'/g, '"');
				var JSONarray = $.parseJSON(escaped);
				fillInActionWindow(JSONarray);
			}
		});
	}
});

$('.ActionNameInTableClose').live({
	'click': function() {
		if (confirm('Are you sure you want to delete this Action?')) {
			var name = $(this).parent().find('.ActionTab').text();
			var url = 'removeAction.action';
			$.ajax({
				url: url,
				type: 'POST',
				datatype: 'JSON',
				data: {
					actionName: name
				},
				success: function(json){
					$('.actionClassActive').remove();
					initConcepts();
					initActions();
				}
			});
		}
		else {
			return false;
		}
	}
});

// functions

function fillNucleusinActionWindow(json) {
	
	var element = $('.actionAttrActive').find('.actionChildAttribute');
	fillNucleusConceptIntoDiv(element, json);
}

function fillInActionAttrWindow(json, parent_prefix) {
	var element = $('.actionAttrActive').find('.actionChildAttribute');
	var index_attr = $('.actionAttrActive').find('#actionChildAttributeIndex').empty();
	index_attr.empty();
	fillCompositeIndexAttrs(index_attr, json);
	
	var initial = true;
	fillCompositeAttrIntoDiv(element, json, initial, parent_prefix);
}

function extractPostCondition() {
	var postConditionAttr = [];
	var attrArray;
	$('.actionClassActive').find('#postAttr').children().each(function(i) {
		var postActionName = $(this).find('.postActionAttrName').text();
		var oper = '=';
		var postActionValue = $(this).find('.postActionValue').val();
		
		attrArray = {
			'right': postActionValue,
			'oper': oper,
			'left': postActionName
		};
		postConditionAttr[i] = attrArray;
	});
	return postConditionAttr;
}

function saveAction(){
	alert ("Save Action called");
	var dummyNegation = "";
	if ($('#conceptsDisplayPanel').children('.actionClassActive').size() == 1) {
		var className = $('.actionClassActive').find('.actionNameField').text().trim();
		var parentName = '';
		var sideIndicator = $('.actionClassActive').find('#sideIndicatorAttr').val().trim();
		
		var preCondAttrs = [];
		var errorOccured = false;
		$('.actionClassActive').find('#preAttr').children().each(function(index){
			var actionAttrName = $(this).find('.preActionAttrName').text();
			var actionAttrParent = $(this).find('.preActionParent option:selected').text();
			
			if (actionAttrParent == '--Select Parent--') {
				errorOccured = true;
				return false;
			}
			else {
				var type = $(this).find('.preActionParent option:selected').val();

				var actionAttrsArray;
				switch (type) {
				case 'p':
				case 'n':
				actionAttrsArray = extractNucPrimCompConcWithNegation(type, actionAttrName, actionAttrParent, $(this), dummyNegation);
				break;
				case 's':
				alert("There is no support for Sets");
				break;
				case 'c':
				var indexElement = $(this).find('#actionChildAttributeIndex');
				
				actionAttrsArray = {
							'name': actionAttrName,
							'parent': actionAttrParent,
							'type': type,
							'negated': dummyNegation,
							'compositeConceptIndexAttrs': extractIndexAttr(indexElement),
							'compConceptAttrs': extractChildComposConc($(this), '.actionChildAttribute')
						};
					break;
				}
				preCondAttrs[index] = actionAttrsArray;
			}
		});
		if (!errorOccured) {
			var precod_object = {
				'cr1': '0',
				'name': "precond",
				'parent': '',
				'compositeConceptIndexAttrs': [],
				'compConceptAttrs': preCondAttrs
			};
			
			var object = {
				'name': className,
				'sideIndicator' : sideIndicator,
				'precondition': precod_object,
				'expressions': extractPostCondition()
			};
		
			var json = $.toJSON(object);
			alert(json);
			var url = 'saveAction.action';
			$.ajax({
				url: url,
				type: 'POST',
				datatype: 'json',
				data: {
					actionData: json
				},
				success: function(data){
					$('.actionClassActive').remove();
					initConcepts();
					initActions();
				}
			});
		}
		else {
			alert('Please select the attribute parents');
			return false;
		}
	}
	else {
		alert('ERROR: No active Window, Please create an action first or select a window.');
	}
}

function fillInActionWindow(json) {
	var actionPanel = getActionPanel();
	var actionAttr = $('.preConditionPanel').html();
	var actionExpr = $('.postConditionPanel').html();
	
	var actionName = json.name;
	
	inActiveWin();
	$('#conceptsDisplayPanel').append('<div id="actionContainer" class="actionContainer actionClassActive"></div>');
	$('.actionClassActive').append(actionPanel);
	$('.actionClassActive').find('.actionNameField').empty().append(actionName);
	
	$('input[id="sideIndicatorAttr"]').val(json.sideIndicator);

//	var index_element = $('.actionClassActive').find('#compIndexAttributes');
//	fillCompositeIndexAttrs(index_element.empty(), json);
	var initial = false;
	var parent_prefix = "";

	$.each(json.precondition.compConceptAttrs, function(j) {
		
		var attr_json = json.precondition.compConceptAttrs[j];
		$('.actionClassActive').find('#preAttr').append('<div class="temp"></div>');
		var element = $('.actionClassActive').find('#preAttr').find('.temp');
		element.append(actionAttr);
		
		var parent = getParentFullName(initial, parent_prefix, attr_json.name, attr_json.parent);
		
		element.find('.preActionAttrName').empty().append(attr_json.name);
		element.find('.preActionParent').prepend('<option value="'+attr_json.type+'">'+parent+'</option>');
		
		var attr_element = element.find('.actionChildAttribute');
		switch (attr_json.type)
		{
			case 'p':
			case 'n':
				fillNucleusConceptIntoDiv(attr_element, attr_json);
				break;
			case 's':
				alert("There is no support for Sets for now");
				break;
			case 'c':
				var attr_index_element = element.find('#actionChildAttributeIndex');
				fillCompositeIndexAttrs(attr_index_element.empty(), attr_json);
				fillCompositeAttrIntoDiv(attr_element, attr_json, initial, parent);
				break;
		}
		
		var temp = $('.temp').html();
		//alert(temp);
		$('.actionClassActive').find('#preAttr').append(temp);
		$('.temp').remove();
	});
	
	$.each(json.expressions, function(i) {
		$('.actionClassActive').find('#postAttr').append('<div class="temp"></div>');
		$('.actionClassActive').find('.temp').append(actionExpr);
		var attr_json = json.expressions;
		var element = $('.actionClassActive').find('#postAttr').find('.temp');
		
		element.find('.postActionAttrName').empty().append(attr_json[i].left);
		element.find('.postActionAttrValuePanel').empty().append('<input id="postActionValue" class="postActionValue" value="'+ attr_json[i].right +'" type="text">');
		
		var temp = $('.temp').html();
		$('.actionClassActive').find('#postAttr').append(temp);
		$('.temp').remove();
	});
	
}


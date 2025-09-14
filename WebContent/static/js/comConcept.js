/**
 * 
 */

$('.compTitleBar').live({

	  "mousedown": function(e) {
        inActiveWin();
		$(this).parent().draggable('enable');
		$(this).parent().draggable({containment: 'parent', refreshPositions: true}).trigger(e); 
	  	$(this).parent().addClass('compClassActive');
      },
	  "mouseup": function() {
	  	$('#conceptsDisplayPanel').children().draggable('disable');
	  }
});
$('#compositeConceptContainer').live({
	  "focus": function() {
	    inActiveWin();
		$(this).addClass('compClassActive');
	  }
});

// add attributes
$('#addCompConcAttr').live('click', function(){
		var children = $('#attrContainer').children().length;
		if (children < 10) {
		var compattr = getCompositeAttrPanel();
		$('.compClassActive').find('#attrContainer').append(compattr);
		$('.compClassActive').find('#attrContainer').children().show();
		}
		else { alert('Error: Maximum attributes reached'); return false;}
});

$('#addCompIndexAttr').live('click', function(){
		var children = $('#compIndexAttributes').children().length;
		if (children < 10) {
		var compattr = $('.compositeIndex').html();
		$('.compClassActive').find('#compIndexAttributes').append(compattr);
		$('.compClassActive').find('#compIndexAttributes').children().show();
		}
		else { alert('Error: Maximum attributes reached'); return false;}
});

$('#bunchAttrPanel').live({
      "mousedown": function() {
		resetAttrs();
        $(this).addClass('bunchAttrSelected');
      }
});

$('#compIndexAttrPanel').live({
      "mousedown": function() {
		resetAttrs();
        $(this).addClass('bunchAttrSelected');
      }
});



// remove attributes
$('#removeCompConcAttr').live('click', function(){ 
	$('.compClassActive').find('#attrContainer').children().each(function(){
        if ($(this).find('input:checked.checker').length == 1) {
              $(this).remove(); 
        }
  });
	$('.compClassActive').find('#postAttrCont').children().each(function(){
        if ($(this).find('input:checked.checker').length == 1) {
              $(this).remove(); 
        }
  });
});

$('#concAddButtonInAction input').live('click', function(){
	var children = $('#postAttrCont').children().length;
	if (children < 10) {
	var compattr = getCompositeAttrPanel();
	$('.compClassActive').find('#postAttrCont').append(compattr);
	$('.compClassActive').find('#postAttrCont').children().show();
	}
	else { alert('Error: Maximum attributes reached'); return false;}
});

////// edit names //////
$('.composConcNameField').live({
	'click': function(event){
		var ispresent = $(this).children().hasClass('tempName');
		if ($(this).text().trim() == 'New Composite Abstract' && !ispresent) {
			$(this).empty();
			$(this).append('<input type="text" class="tempName" style="background: transparent;"/>');
			$('.tempName').focus();
		}
		else { return false; }
	},
	'dblclick': function(){
		var ispresent = $(this).children().hasClass('tempName');
		if ($(this).text().trim() != 'New Composite Abstract' && !ispresent) {
			$(this).empty();
			$(this).append('<input type="text" class="tempName" style="background: transparent;"/>');
			$('.tempName').focus();
		}
		else { $(this).focusout(); return false; }
	},
	'focusout': function(){
		var name = $(this).find('.tempName').val();
   		if (name == '' || name == null) {
		$(this).append('New Composite Abstract');
		$('.tempName').remove();
		}	else { 
		$(this).append(name);
		$('.tempName').remove();
		return false; }
	}
});
$('.composConcAttrName').live({
   'click': function(){
   	var ispresent = $(this).children().hasClass('tempName');
   	if ($(this).text().trim() == 'New attribute' && !ispresent) {
   		$(this).empty();
   		$(this).append('<input type="text" class="tempName" style="width: 100px; background: transparent;"/>');
   		$('.tempName').focus();
   	} else { return false; }
   },
   'dblclick': function(){
		var ispresent = $(this).children().hasClass('tempName');
		if ($(this).text().trim() != 'New attribute' && !ispresent) {
			$(this).empty();
			$(this).append('<input type="text" class="tempName" style="width: 100px; background: transparent;"/>');
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
$('.nameField').live({
   'click': function(){
   	var ispresent = $(this).children().hasClass('tempName');
   	if ($(this).text().trim() == 'New action' && !ispresent) {
   		$(this).empty();
   		$(this).append('<input type="text" class="tempName" style="width: 100px; background: transparent;"/>');
   		$('.tempName').focus();
   	} else { $(this).focusout(); return false; }
   },
   'dblclick': function(){
		var ispresent = $(this).children().hasClass('tempName');
		if ($(this).text().trim() != 'New action' && !ispresent) {
			$(this).empty();
			$(this).append('<input type="text" class="tempName" style="width: 100px; background: transparent;"/>');
			$('.tempName').focus();
		}
		else { $(this).focusout(); return false; }
	},
   'focusout': function() {
   		var name = $(this).find('.tempName').val();
   		if (name == '' || name == null) {
		$(this).append('New action');
		$('.tempName').remove();
		} else { 
		var name = $(this).find('.tempName').val();
		$(this).append(name);
		$('.tempName').remove();
		return false; }
   }
});
/////////// Save Composite Abstract //////////

$('.saveCom').live('click', function(event) {
		saveComposite();
});

$('#bunchAttrPanel').live({
      "mousedown": function() {
	  	resetAttrs();
        $(this).addClass('bunchAttrSelected');
      }
});

////////// Parent Select ////////////
$('#composConcAttrPanel').live({
      "mousedown": function() {
		$('.compClassActive').find('#attrContainer').children().removeClass('composConcAttrActive');
		$('.setClassActive').find('#setAttr').children().removeClass('composConcAttrActive');
		$(this).addClass('composConcAttrActive');
      }
});

$('#composConcChildAttrPanel').live({
      "mousedown": function() {
		$(this).parent().children().removeClass('composConcChildAttrActive');
		$(this).addClass('composConcChildAttrActive');
      }
});

$('.composParent').live('change',function(event) {
	var name = $('.compClassActive').find('.composParent option:selected').text();
	$('.compClassActive').find('#attrContainer').empty();
	$('.compClassActive').find('#compIndexAttributes').empty();
	
	if (name == '--Select Parent--') {
		return false;
	}

	var url = 'viewComposite.action';
	$.ajax({
		url: url,
		type: 'POST',
		data: {
			compositeName: name
		},
		datatype: 'json',
		success: function(json){
			var escaped = json.trim().replace(/'/g, '"');
			var JSON = $.parseJSON(escaped);
			var initial = true;
			fillInComposite(JSON, initial, name);
		}
	});
});

$('.composConcAttrParent').live('change',function(event) {
	var element = $(this).parents(".attrCont").attr("id");
	if (element == "attrContainer") {
		var value = $('.composConcAttrActive').find('.composConcAttrParent option:selected').val();
		var name = $('.composConcAttrActive').find('.composConcAttrParent option:selected').text();
		$('.composConcAttrActive').find('.compAttrChildren').empty();
		$('.composConcAttrActive').find('.composConcIndexAttributes').empty();
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
							var escaped = json.trim().replace(/'/g, '"');
							var JSON = $.parseJSON(escaped);
							fillNucleusinCompWindow(JSON);
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
							var escaped = json.trim().replace(/'/g, '"');
							var JSON = $.parseJSON(escaped);
							fillNucleusinCompWindow(JSON);
						}
					});
					break;
				case 's':
					var url = 'viewSet.action';
					$.ajax({
						url: url,
						type: 'POST',
						data: {
							setName: name
						},
						datatype: 'json',
						success: function(json){
							var escaped = json.trim().replace(/'/g, '"');
							var JSON = $.parseJSON(escaped);
							fillInSetinCompWindow(JSON, name);
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
							var escaped = json.trim().replace(/'/g, '"');
							var JSON = $.parseJSON(escaped);
							fillInCompositeAttrWindow(JSON, name);
						}
					});
					break;
			}
		}
		else {
			$('.composConcActive').find('.compAttrChildren').empty();
			return false;
		}
	} else { return false;}
});

$('.showAttrChild').live('click', function(){
	if (!($('.composConcAttrActive').find('.compAttrChildren').is(':empty'))) {
		if ($('.composConcAttrActive').find('#compAttrChildren').is(':visible')) {
			$('.composConcAttrActive').find('#compAttrChildren').hide();
			$(this).removeClass('minus');
		}
		else {
			$('.composConcAttrActive').find('#compAttrChildren').show();
			$(this).addClass('minus');
		}
	} else {return false;}
});

$('.showChildAttrChild').live('click', function(){
	var element = $(this).parent().parent().parent();
	if ((element.find('.compChildAttrChildren').is(':empty'))) 
	{ return false;}
	else{
		if (element.find('.compChildAttrChildren:first').is(':visible')) {
			element.find('.compChildAttrChildren:first').hide();
			$(this).removeClass('minus');
		}
		else {
			element.find('.compChildAttrChildren:first').show();
			$(this).addClass('minus');
		}
	}
});

/// list ///

$('.CompositeConceptTab').live({
	'mousedown': function() {
		$.each($('#conceptsListPanel').children(), function() {
			$(this).find('.compositeClassNameInTableClose').hide();
		});
		$(this).parent().find('.compositeClassNameInTableClose').show();
	},
	'click': function() {
		var name = $(this).text();
		var url = 'viewComposite.action';
		$.ajax({
			url: url,
			type: 'POST',
			datatype: 'json',
			data: {compositeName: name},
			success: function(json) {
				var escaped = json.trim().replace(/'/g, '"');
				var JSONarray = $.parseJSON(escaped);
				fillInCompositeWindow(JSONarray);
			}
		});
	}
});

$('.compositeClassNameInTableClose').live({
	'click': function() {
		if (confirm('Are you sure you want to delete this Composite Abstract?')) {
			var name = $(this).parent().find('.CompositeConceptTab').text();
			var url = 'removeComposite.action';
			$.ajax({
				url: url,
				type: 'POST',
				datatype: 'JSON',
				data: {
					compositeName: name
				},
				success: function(json){
					$('.compClassActive').remove();
					initConcepts();
				}
			});
		}
		else {
			return false;
		}
	}
});
// Close Composite concept window

$('.closeCom').live('click', function(event){
	  	$('#conceptsDisplayPanel').children().draggable('destroy');
		$('.compClassActive').fadeOut(function() { $(this).remove();});
});

// functions

function fillInCompositeWindow(json) {
	var compositePanel = getCompositePanel();
	inActiveWin();
	$('#conceptsDisplayPanel').append('<div id="compositeConceptContainer" class="compositeConceptContainer compositeConceptContainerStyle compClassActive"></div>');
	$('.compClassActive').append(compositePanel);
	
	var compositeName = json.name;
	$('.compClassActive').find('.composConcNameField').empty().append(compositeName);
	if(json.cr1 == '1') {
		$('input[name="cr1"]').attr('checked','checked');
	}	
	var initial = false;
	var parent_prefix = "";
	fillInComposite(json, initial, parent_prefix);
}

function fillInComposite(json, initial, parent_prefix) {
	var compositeAttrPanel = $('.compositeAttr').html();
	$('.compClassActive').find('.composParent').append('<option>'+ json.parent +'</option>');

	var index_element = $('.compClassActive').find('#compIndexAttributes');
	fillCompositeIndexAttrs(index_element.empty(), json);
	
	var attr_container = $('.compClassActive').find('#attrContainer');
	attr_container.empty();

	$.each(json.compConceptAttrs, function(j) {
		
		var attr_json = json.compConceptAttrs[j];
		attr_container.append('<div class="temp"></div>');
		var element = attr_container.find('.temp');
		element.append(compositeAttrPanel);
		
		var parent = getParentFullName(initial, parent_prefix, attr_json.name, attr_json.parent);
		if(attr_json.negated == "1") {
			element.find('.composConcAttrNegater').empty().append('<input class="negated" type="text" value="1">');
		}
		element.find('.composConcAttrName').empty().append(attr_json.name);
		element.find('.composConcAttrParent').prepend('<option value="'+attr_json.type+'">'+parent+'</option>');

		var attr_element = element.find('.compAttrChildren');
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
				var attr_index_element = element.find('#composConcIndexAttributes');
				fillCompositeIndexAttrs(attr_index_element.empty(), attr_json);
				fillCompositeAttrIntoDiv(attr_element, attr_json, initial, parent);
				break;
				
		}
		var temp = $('.temp').html();
		$('.compClassActive').find('#attrContainer').append(temp);		
		$('.temp').remove();
	});
} 

function fillInCompositeAttrWindow(json, parent_prefix) {
	var element = $('.composConcAttrActive').find('.compAttrChildren');
	
	fillCompositeIndexAttrs($('.composConcAttrActive').find('#composConcIndexAttributes').empty(), json);
	var initial = true;
	fillCompositeAttrIntoDiv(element, json, initial, parent_prefix);
}

function fillInSetinCompWindow(json, parent_prefix) {
	var element = $('.composConcAttrActive').find('.compAttrChildren');
	var initial = true;
	fillSetAttrIntoDiv(element, json, initial, parent_prefix + ".element");
}

function fillNucleusinCompWindow(json) {
	var element = $('.composConcAttrActive').find('.compAttrChildren');
	fillNucleusConceptIntoDiv(element, json);
}
function saveComposite(){
	if ($('#conceptsDisplayPanel').children('.compClassActive').size() == 1) {
		var className = $('.compClassActive').find('.composConcNameField').text().trim();
		var parentName = $('.compClassActive').find('.composParent option:selected').text();
		if(parentName == '--Select Parent--')
		{
			parentName = className;
		}
		
		var compConceptAttrs = [];
		var errorOccured = false;
		$('.compClassActive').find('#attrContainer').children().each(function(index) {
			var compAttrName = $(this).find('.composConcAttrName').text();
			var compAttrParent = $(this).find('.composConcAttrParent option:selected').text();
			
			var isNegated = $(this).find('.composConcAttrNegater').find('.negated').val();
			
			if (compAttrParent == '--Select Parent--') {
				errorOccured = true;
				return false;
			}
			else {
				var type = $(this).find('.composConcAttrParent option:selected').val();

				var compAttrsArray;
				switch (type) {
				case 'p':
				case 'n':
				compAttrsArray = extractNucPrimCompConcWithNegation(type, compAttrName, compAttrParent, $(this), isNegated);
				break;
				case 's':
					compAttrsArray = extractAttrSetWithNegation($(this).find('.compAttrChildren'), compAttrName, compAttrParent, isNegated);
					break;
				case 'c':
				var indexElement = $(this).find('#composConcIndexAttributes');
				
				compAttrsArray = {
							'name': compAttrName,
							'parent': compAttrParent,
							'type': type,
							'negated' : isNegated,
							'compositeConceptIndexAttrs': extractIndexAttr(indexElement),
							'compConceptAttrs': extractChildComposConc($(this), '.compAttrChildren')
						};
					break;
				}
				compConceptAttrs[index] = compAttrsArray;
			}
		});
		var indexElement = $('.compClassActive').find('#compIndexAttributes');
		
		var cr1 = '0';
		if($('input[name="cr1"]').is(':checked')){
			cr1 = '1';
		}
		
		if (!errorOccured) {
			var object = {
				    'cr1': cr1,
				    'name': className,
				    'parent': parentName,
				    'compositeConceptIndexAttrs': extractIndexAttr(indexElement),
				    'compConceptAttrs': compConceptAttrs
				    };
		
			var json = $.toJSON(object);
			var url = 'saveComposite.action';
			$.ajax({
				url: url,
				type: 'POST',
				datatype: 'json',
				data: {
					compositeName: json
				},
				success: function(data){
					$('.compClassActive').remove();
					initConcepts();
				}
			});
		}
		else {
			alert('Please select the attribute parents');
			return false;
		}
	}
	else {
		alert('ERROR: No active Window, Please create an abstract first or select a window.');
	}
}


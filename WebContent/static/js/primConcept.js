/**
 * 
 */

$('.primTitleBar').live({

	  "mousedown": function(e) {
        inActiveWin();
		$(this).parent().draggable({containment: 'parent', refreshPositions: true}).trigger(e); 
	  	$(this).parent().addClass('primClassActive');
      },
	  "mouseup": function() {
	  	$('#conceptsDisplayPanel').children().removeClass('ui-draggable');
	  }
});
$('#primitiveConceptContainer').live({
	  "focus": function() {
	  	inActiveWin();
		$(this).addClass('primClassActive');
	  }
});

////////////////// ADD ATTRIBUTES ///////////////////
$('#addPrimIndexAttr').live('click', function(event) {
			var attrCount = $('.primClassActive').find('#indexAttrContainer').children().size();
			if (attrCount < 4) {
				var primIndex = getPrimitiveIndexSkeleton();
				$('.primClassActive').find('#indexAttrContainer').append(primIndex);

			} else { alert('Error: Maximum Index attributes reached.'); return false;}
});

$('#addPrimValueAttr').live('click', function(event) {
			var valueCount = $('.primClassActive').find('#valueAttrContainer').children().size();
			if (valueCount < 1) {
				var primValue = getPrimitiveValueSkeleton();
				$('.primClassActive').find('#valueAttrContainer').append(primValue);
			}
			else { alert('Error: Maximum value attributes reached.');return false;}
});

$('#bunchAttrPanel').live({
      "mousedown": function() {
	  	$('.primClassActive').find('#indexAttrContainer').children().removeClass('bunchAttrSelected');
		$('.primClassActive').find('#valueAttrContainer').children().removeClass('bunchAttrSelected');
        $(this).addClass('bunchAttrSelected');
      }
});


/*$('.bunchAttrSelected select').live('change',function(event) {
	var value = $('.bunchAttrSelected select option:selected').text();
	$('.bunchAttrSelected').find('.midValue').val('');
	$('.bunchAttrSelected').find('.f_maxValue').val('');
	$('.bunchAttrSelected').find('.f_minValue').val('');
	if(value == 'IN') {
		$('.bunchAttrSelected').find('div#Interval').show();
		$('.bunchAttrSelected').find('div#nonInterval').hide();
	} else {
		$('.bunchAttrSelected').find('div#Interval').hide();
		$('.bunchAttrSelected').find('div#nonInterval').show();
	}

});*/

////////////////// SAVE ATTRIBUTES ///////////////////
$('.savePrim').live('click', function(event) {
	if ($('#conceptsDisplayPanel').children('.primClassActive').size() == 1) {
		var className = $('.primClassActive').find('.primitiveConceptNameField').text().trim();
		var parentName = $('.primClassActive').find('.primitiveParent option:selected').text();
		if (parentName == '--Select Parent--') {
			alert('Please select a parent');
			return false;
		}
		else {
			var nucleusConceptIndexAttrs = [];
			var nucleusConceptValueAttrs = [];
			var object;
			var arr;
			$('.primClassActive').find('.f_bunchAttrPanel').each(function(index)
					{
						var attrArr;
						var attrName = $(this).find('.primAttrNameField').text();
						var attrOper = $(this).find('#nucComboOper option:selected').text();
						var midvalue = $(this).find('.midValue').val();
						var maxvalue = $(this).find('.f_maxValue').val();
						var minvalue = $(this).find('.f_minValue').val();
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
						nucleusConceptIndexAttrs[index] = attrArr;
					});
					
			$('.primClassActive').find('.v_bunchAttrPanel').each(function(index){
				var attrArr;
				var attrName = $(this).find('.primAttrNameField').text();
				var attrOper = $(this).find('#nucComboOper option:selected').text();
				var midvalue = $(this).find('.midValue').val();
				var maxvalue = $(this).find('.f_maxValue').val();
				var minvalue = $(this).find('.f_minValue').val();
				var classifierId = $(this).find('.f_classifier').val() || '';
				// Force-commit the multi-select state, then read the joined value
				// from the hidden mirror field. See nucConcept.js for rationale.
				if (typeof commitSymbolSelection === 'function') commitSymbolSelection($(this));
				var symbolValue = $(this).find('.f_symbolValueCommitted').val() || '';
				if (attrOper === 'IS' || attrOper === 'NOT IS') {
					attrArr = {
						'name': attrName,
						'oper': attrOper,
						'value': symbolValue || midvalue || '',
						'classifier': classifierId
					};
				} else if (midvalue == '' || midvalue == null) {
					attrArr = {
						'name': attrName,
						'oper': attrOper,
						'value': {
							'minValue': minvalue,
							'maxValue': maxvalue
						}
					};
					if (classifierId) { attrArr.classifier = classifierId; }
				}
				else {
					attrArr = {
						'name': attrName,
						'oper': attrOper,
						'value': midvalue
					};
					if (classifierId) { attrArr.classifier = classifierId; }
				}
				nucleusConceptValueAttrs[index] = attrArr;
			});
			object = {
				'name': className,
				'parent': parentName,
				'nucleusConceptIndexAttrs': nucleusConceptIndexAttrs,
				'nucleusConceptValueAttrs': nucleusConceptValueAttrs
			};
			var json = $.toJSON(object);
			var url = 'savePrimitive.action';
			$.ajax({
				url: url,
				type: 'POST',
				datatype: 'json',
				data: {
					prim: json
				},
				success: function(data){
					$('.primClassActive').remove();
					initConcepts();
				}
			});
		}
	} else {
		alert('ERROR: No active Window, Please create an abstract first or select a window.');
	}
});

////////// Parent Select ////////////

$('.primitiveParent').live('change',function(event) {
	var value = $('.primClassActive').find('.primitiveParent option:selected').text();
	var url = "viewNucleus.action";
	$.ajax({
		url: url,
		type: 'POST',
		data: {nucleusName: value},
		success: function(json){
		$('.primClassActive').find('.indexAttrContainer').empty();
		$('.primClassActive').find('.valueAttrContainer').empty();
		var escaped = json.replace(/'/g, '"');
		var thisJSON = $.parseJSON(escaped);
		fillNucleusinPrimWindow(thisJSON);
		}
	});

});

///////////////////// LIST ///////////////////////////

$('.treeParentNameInTable').live({
	"click": function() {
		if ($(this).parent().find('.treeChildNameInTable').is(':visible')) {
			$(this).parent().find('.treeChildNameInTable').slideUp();
			$(this).removeClass('childrenShown');
			$(this).find('.arrowLeft').removeAttr('style');
		}
		else {
			$(this).addClass('treeParentActiveNameInTable');
			$(this).parent().find('.treeChildNameInTable').slideDown();
			$(this).addClass('childrenShown');
			$(this).find('.arrowLeft').css('background-position', 'left top');
		}
	}
});

$('.treeChildNameInTable').live({
	'mouseover': function() {
		$(this).addClass('activeChild');
		$(this).parent().find('.primClassNameInTableClose').show();
	},
	'mouseout': function() {
		$(this).removeClass('activeChild');
		$(this).parent().find('.primClassNameInTableClose').hide();
	}
});

$('.childName').live({
	'click': function() {
		var name = $(this).text();
		var url = 'viewPrimitive.action';
		$.ajax({
			url: url,
			type: 'POST',
			data: {primitiveName: name},
			datatype: 'json',
			success: function(json) {
				var escaped = json.replace(/'/g, '"');
				var JSON = $.parseJSON(escaped);
				fillPrimitiveWindow(JSON);
			}
		});
	}
});
$('.primClassNameInTableClose').live({
	"click": function(){
		$('#conceptDisplayPanel').children().remove();
		var primitive = $('.activeChild').find('.childName').text();
		if(confirm('Are you sure you want to delete this Primitive Abstract?')) {
			var url = "removePrimitive.action";
			$.ajax({
				url: url,
				type: 'POST',
				datatype: 'json',
				data: {primitiveName: primitive},
				success: function(json) {
				$('.primClassActive').remove();
				initConcepts();
				}
			});
		}
	}
});

///////////////////// EDIT NAMES ///////////////////////

$('.primAttrNameField').live({
   'click': function(){
   	var isstatic = $(this).hasClass('static');
   	var ispresent = $(this).children().hasClass('tempName');
   	if ($(this).text().trim() == 'New attribute' && !ispresent && !isstatic) {
   		$(this).empty();
   		$(this).append('<input type="text" class="tempName" style="width: 100px; background: transparent;"/>');
   		$('.tempName').focus();
   	}
   },
   'dblclick': function(){
   		var isstatic = $(this).hasClass('static');
		var ispresent = $(this).children().hasClass('tempName');
		if ($(this).text().trim() != 'New attribute' && !ispresent && !isstatic) {
			$(this).empty();
			$(this).append('<input type="text" class="tempName" style="width: 100px; background: transparent;"/>');
			$('.tempName').focus();
		}
		else {
			return false;
		}
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

$('.primitiveConceptNameField').live({
	'click': function(event){
		var isstatic = $(this).hasClass('static');
		var ispresent = $(this).children().hasClass('tempName');
		if ($(this).text().trim() == 'New Primitive Abstract' && !ispresent && !isstatic) {
			$(this).empty();
			$(this).append('<input type="text" class="tempName" style="background: transparent;"/>');
			$('.tempName').focus();
		}
		else {
			return false;
		}
	},
	'dblclick': function(){
		var isstatic = $(this).hasClass('static');
		var ispresent = $(this).children().hasClass('tempName');
		if ($(this).text().trim() != 'New Primitive Abstract' && !ispresent && !isstatic) {
			$(this).empty();
			$(this).append('<input type="text" class="tempName" style="background: transparent;"/>');
			$('.tempName').focus();
		}
		else {
			return false;
		}
	},
	'focusout': function(){
		var name = $(this).find('.tempName').val();
   		if (name == '' || name == null) {
		$(this).append('New Primitive Abstract');
		$('.tempName').remove();
		}	else { 
		$(this).append(name);
		$('.tempName').remove();
		return false; }
	}
});

///////////////////////// CLOSE ATTRIBUTES AND CLASS //////////////////////


$('.closePrim').live('click', function(event){
	  	$('#conceptsDisplayPanel').children().draggable('destroy');
		$('.primClassActive').fadeOut(300, function() { $(this).remove(); });
});

$('.bunchAttrRemoveButtonPanel').live('click', function(event){
		$('.bunchAttrSelected').fadeOut(100, function() { $(this).remove(); });
});
/////////////////////// HELP /////////////////////////////

$('.helpPrim').live({
	'click': function(event){
		if ($('.helpContent').is(":hidden")) {
			$('#conceptsDisplayPanel').find('.helpContent').fadeIn('slow').animate({
				top: "50px"
			}, {
				queue: false,
				duration: 350
			});
			$.ajax({
				url: 'help/nucleus.html',
				type: 'POST',
				success: function(html){
					$('.helpContent').append(html);
				}
			});
		}
		return false;
	},
	'mouseout': function(event){
	$('#conceptsDisplayPanel').find('.helpContent').fadeOut('fast');
	$('.helpContent').empty();
	}
});

/////// Functions ///////////

function fillNucleusinPrimWindow(json) {
	var primIndex = getPrimitiveIndexSkeleton();
	var primValue = getPrimitiveValueSkeleton();
	$('.primClassActive').hide();	
$.each(json.nucleusConceptIndexAttrs, function(i){

		$('.primClassActive').append('<div id="temp" style="display:block;"></div>');
		$('.primClassActive').find('#temp').append(primIndex);
		$('#temp').find('.primAttrNameField').empty();
		$('#temp').find('.primAttrNameField').html(json.nucleusConceptIndexAttrs[i].name);
								
		if (json.nucleusConceptIndexAttrs[i].oper == 'IN') {
			$('#temp select').prepend('<option>'+json.nucleusConceptIndexAttrs[i].oper+'</option>');
			$('#temp').find('div#Interval').show();
			$('#temp').find('div#Interval').empty();
			$('#temp').find('div#nonInterval').hide();
			$('#temp').find('#Interval').append('<input class="f_minValue" type="text" value="' + json.nucleusConceptIndexAttrs[i].value.minValue + '" > - ');
			$('#temp').find('#Interval').append('<input class="f_maxValue" type="text" value="' + json.nucleusConceptIndexAttrs[i].value.maxValue + '" >');
									
		}
		else {
			$('#temp select').prepend('<option>'+json.nucleusConceptIndexAttrs[i].oper+'</option>');
			$('#temp').find('div#Interval').hide();
			$('#temp').find('div#nonInterval').show();
			$('#temp').find('div#nonInterval').empty();
			$('#temp').find('#nonInterval').append('<input class="midValue" type="text" value="' + json.nucleusConceptIndexAttrs[i].value + '" >');
		}
		$('#temp').find('.primAttrNameField').addClass('static');
		var temp = $('#temp').html();
		$('.primClassActive').find('.indexAttrContainer').append(temp);
		$('#temp').remove();
});
							
$.each(json.nucleusConceptValueAttrs, function(i){
			var attr = json.nucleusConceptValueAttrs[i];
			$('.primClassActive').append('<div id="temp" style="display:block;"></div>');
			$('.primClassActive').find('#temp').append(primValue);
			$('#temp').find('.primAttrNameField').empty();
			$('#temp').find('.primAttrNameField').html(attr.name);

			// Always target the operator select explicitly, NOT $('#temp select')
			// which would also hit the new classifier picker.
			var operSelect = $('#temp').find('#nucComboOper');
			operSelect.prepend('<option>'+attr.oper+'</option>');

			// If the parent declared a classifier, propagate it into this row.
			var inheritedClassifier = attr.classifier || '';

			if (attr.oper == 'IN') {
				$('#temp').find('div#Interval').show();
				$('#temp').find('div#Interval').empty();
				$('#temp').find('div#nonInterval').hide();
				$('#temp').find('#Interval').append('<input class="f_minValue" type="text" value="' + attr.value.minValue + '" > - ');
				$('#temp').find('#Interval').append('<input class="f_maxValue" type="text" value="' + attr.value.maxValue + '" >');
			}
			else if (attr.oper == 'IS' || attr.oper == 'NOT IS') {
				// Leave nonInterval intact (it owns midValue + f_symbolList + buttons).
				$('#temp').find('div#Interval').hide();
				$('#temp').find('div#nonInterval').show();
				$('#temp').find('.midValue').hide();
				$('#temp').find('.f_symbolList, .f_symbolAll, .f_symbolNone').show();
			}
			else {
				$('#temp').find('div#Interval').hide();
				$('#temp').find('div#nonInterval').show();
				$('#temp').find('.midValue').val(attr.value).show();
				$('#temp').find('.f_symbolList, .f_symbolAll, .f_symbolNone').hide();
			}
			$('#temp').find('.primAttrNameField').addClass('static');
			var temp = $('#temp').html();
			$('.primClassActive').find('.valueAttrContainer').append(temp);
			$('#temp').remove();

			// After insertion, drive the classifier dropdown so symbol options populate.
			if (inheritedClassifier) {
				var freshRow = $('.primClassActive').find('.valueAttrContainer .v_bunchAttrPanel').last();
				var picker = freshRow.find('.f_classifier');
				// If the picker has options yet, just set it; otherwise wait for the page-load fetch.
				if (picker.find('option').length > 1) {
					picker.val(inheritedClassifier).trigger('change');
				} else if (typeof loadSolverClassifiers === 'function') {
					loadSolverClassifiers(function(list) {
						for (var k = 0; k < list.length; ++k) {
							var c = list[k];
							if (c.pluggable === false) continue;
							picker.append('<option value="' + c.classifierId + '">' + (c.displayName || c.classifierId) + '</option>');
						}
						picker.data('populated', true).val(inheritedClassifier).trigger('change');
					});
				}
			}
});
	$('.primClassActive').fadeIn('fast');

}

function fillPrimitiveWindow(json) {
			var primPanel = getPrimitivePanel();
			var name = json.name;
			var jsonName = name.replace(/\s+/g, '');
			jsonName = name.replace(/[^a-zA-Z 0-9]+/g,'');
			var children = $('#conceptsDisplayPanel > div').size();
			var ispresent = $('#conceptsDisplayPanel').children().hasClass(jsonName);
			if (!ispresent) {
				if (children < 4) {
							$('#conceptsDisplayPanel').children('#primitiveConceptContainer').animate({
								top: '+=10',
								left: '+=10'
							});
							$('#conceptsDisplayPanel').append('<div id="primitiveConceptContainer" class="primitiveConceptContainerStyle ' + jsonName + '"></div>');
							$('.' + jsonName).hide();	
							$('.' + jsonName).append(primPanel);
							
							$('.' + jsonName).find('.primitiveConceptNameField').empty();
							$('.' + jsonName).find('.primitiveConceptNameField').html(json.name);
							$('.' + jsonName).find('.primitiveConceptNameField').addClass('static');
							$('.' + jsonName).find('.primitiveParent').append('<option>'+ json.parent +'</option>');				
							var primIndex = getPrimitiveIndexSkeleton();
							var primValue = getPrimitiveValueSkeleton();
							$.each(json.nucleusConceptIndexAttrs, function(i){
										$('.' + jsonName).append('<div id="temp" style="display:block;"></div>');
										$('.' + jsonName).find('#temp').append(primIndex);
										$('#temp').find('.primAttrNameField').empty();
										$('#temp').find('.primAttrNameField').html(json.nucleusConceptIndexAttrs[i].name);
										
										if (json.nucleusConceptIndexAttrs[i].oper == 'IN') {
											$('#temp select').prepend('<option>'+json.nucleusConceptIndexAttrs[i].oper+'</option>');
											$('#temp').find('div#Interval').show();
											$('#temp').find('div#Interval').empty();
											$('#temp').find('div#nonInterval').hide();
											$('#temp').find('#Interval').append('<input class="f_minValue" type="text" value="' + json.nucleusConceptIndexAttrs[i].value.minValue + '" > - ');
											$('#temp').find('#Interval').append('<input class="f_maxValue" type="text" value="' + json.nucleusConceptIndexAttrs[i].value.maxValue + '" >');	
										}
										else {
											$('#temp select').prepend('<option>'+json.nucleusConceptIndexAttrs[i].oper+'</option>');
											$('#temp').find('div#Interval').hide();
											$('#temp').find('div#nonInterval').show();
											$('#temp').find('div#nonInterval').empty();
											$('#temp').find('#nonInterval').append('<input class="midValue" type="text" value="' + json.nucleusConceptIndexAttrs[i].value + '" >');
										}
										$('#temp').find('.primAttrNameField').addClass('static');
										var temp = $('#temp').html();
										$('.' + jsonName).find('.indexAttrContainer').append(temp);
										$('#temp').remove();
							});
							
							$.each(json.nucleusConceptValueAttrs, function(i){
										var attr = json.nucleusConceptValueAttrs[i];
										$('.' + jsonName).append('<div id="temp" style="display:block;"></div>');
										$('.' + jsonName).find('#temp').append(primValue);
										$('#temp').find('.primAttrNameField').empty();
										$('#temp').find('.primAttrNameField').html(attr.name);

										// Target the operator select explicitly — never $('#temp select'),
										// which would also affect the new classifier picker.
										$('#temp').find('#nucComboOper').prepend('<option>'+attr.oper+'</option>');

										if (attr.oper == 'IN') {
											$('#temp').find('div#Interval').show();
											$('#temp').find('div#Interval').empty();
											$('#temp').find('div#nonInterval').hide();
											$('#temp').find('#Interval').append('<input class="f_minValue" type="text" value="' + attr.value.minValue + '" > - ');
											$('#temp').find('#Interval').append('<input class="f_maxValue" type="text" value="' + attr.value.maxValue + '" >');
										}
										else if (attr.oper == 'IS' || attr.oper == 'NOT IS') {
											$('#temp').find('div#Interval').hide();
											$('#temp').find('div#nonInterval').show();
											$('#temp').find('.midValue').hide();
											$('#temp').find('.f_symbolList, .f_symbolAll, .f_symbolNone, .f_symbolApply, .f_symbolPreview').show();
											$('#temp').find('.f_symbolValueCommitted').val(attr.value || '').attr('data-saved', attr.value || '');
										}
										else {
											$('#temp').find('div#Interval').hide();
											$('#temp').find('div#nonInterval').show();
											$('#temp').find('.midValue').val(attr.value).show();
											$('#temp').find('.f_symbolList, .f_symbolAll, .f_symbolNone, .f_symbolApply, .f_symbolPreview').hide();
										}
										$('#temp').find('.primAttrNameField').addClass('static');
										var temp = $('#temp').html();
										$('.' + jsonName).find('.valueAttrContainer').append(temp);
										$('#temp').remove();

										// Wire up the classifier dropdown of the just-inserted row to pre-select
										// the saved classifier and re-select the saved symbols.
										if (attr.classifier) {
											var freshRow = $('.' + jsonName).find('.valueAttrContainer .v_bunchAttrPanel').last();
											var picker = freshRow.find('.f_classifier');
											var savedSymbolsCsv = (attr.oper === 'IS' || attr.oper === 'NOT IS') ? (attr.value || '') : '';
											var savedSymbols = savedSymbolsCsv.split(',').map(function(s){ return s.trim(); }).filter(function(s){ return s.length; });
											var applySaved = function() {
												// After checkboxes are populated, tick exactly the saved ones.
												setTimeout(function() {
													freshRow.find('.f_symbolBox').each(function() {
														var v = $(this).val();
														$(this).prop('checked', savedSymbols.indexOf(v) >= 0);
													});
													if (typeof commitSymbolSelection === 'function') commitSymbolSelection(freshRow);
												}, 50);
											};
											if (picker.find('option').length > 1) {
												picker.val(attr.classifier).trigger('change');
												applySaved();
											} else if (typeof loadSolverClassifiers === 'function') {
												loadSolverClassifiers(function(list) {
													for (var k = 0; k < list.length; ++k) {
														var c = list[k];
														if (c.pluggable === false) continue;
														picker.append('<option value="' + c.classifierId + '">' + (c.displayName || c.classifierId) + '</option>');
													}
													picker.data('populated', true).val(attr.classifier).trigger('change');
													applySaved();
												});
											}
										}
							});
							$('#conceptsDisplayPanel').children().removeClass('primClassActive');
							$('.' + jsonName).addClass('primClassActive');
							$('.' + jsonName).fadeIn('fast');	
				}
				else {
					alert('Maximum concepts reached.');
				}
			} else {
				return false;
		}
}

function getPrimitiveIndexSkeleton() {
	var skeleton = $('.primitiveIndex').html();
	return skeleton;
}
function getPrimitiveValueSkeleton() {
	var skeleton = $('.primitiveValue').html();
	return skeleton;
}


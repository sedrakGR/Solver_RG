/*
 * Written by Vartges Lylozian
 * April 21st, 2011
 * 

*/


$('#newBunchClassCreator').live('click', function(event) {
			var children = $('#perceptDisplayPanel').children('#bunchClassContainer').size();
			if (children < 4) {
				$('#perceptDisplayPanel').children('.bunchClassContainer').removeClass('bunchClassActive');
				$('#perceptDisplayPanel').children('.bunchClassContainer').animate({
					top: '+=10',
					left: '+=10'
				});
				var panel = getNucleusPanel();
				$('#perceptDisplayPanel').append('<div id="bunchClassContainer" class="bunchClassContainer bunchClassContainerStyle bunchClassActive"></div>');
				$('.bunchClassActive').append(panel);
			} else {
				alert('Maximum concepts reached.');
			}
	  });


$('.nucTitleBar').live({

	  "mousedown": function(e) {
        $('#perceptDisplayPanel').children().removeClass('bunchClassActive');
		$(this).parent().draggable({containment: 'parent', refreshPositions: true}).trigger(e); 
	  	$(this).parent().addClass('bunchClassActive');
      },
	  "mouseup": function() {
	  	$('#perceptDisplayPanel').children().removeClass('ui-draggable');
	  },

});

$('#bunchClassContainer').live({
	  "focus": function() {
	  	$('#perceptDisplayPanel').children().removeClass('bunchClassActive');
		$(this).addClass('bunchClassActive');
	  }
});

////////////////// ADD ATTRIBUTES ///////////////////
$('#addIndexAttr').live('click', function(event) {
			var attrCount = $('.bunchClassActive').find('#indexAttrContainer').children().size();
			if (attrCount < 4) {
			var indexPanel = getIndexSkeleton();
			$('.bunchClassActive').find('#indexAttrContainer').append(indexPanel);
			
			} else { alert('Error: Maximum Index attributes reached.'); return false;}
});

$('#addValueAttr').live('click', function(event) {
			var valueCount = $('.bunchClassActive').find('#valueAttrContainer').children().size();
			if (valueCount < 1) {
			var valuePanel = getValueSkeleton();
			$('.bunchClassActive').find('#valueAttrContainer').append(valuePanel);
			}
			else { alert('Error: Maximum value attributes reached.');return false;}
});

$('#bunchAttrPanel').live({
      "mousedown": function() {
		resetNucAttrs();
        $(this).addClass('bunchAttrSelected');
      }
});


$('.bunchAttrSelected #nucComboOper').live('change', function(event) {
	var value = $(this).find('option:selected').text();
	var panel = $(this).closest('.bunchAttrSelected');
	panel.find('.midValue').val('');
	panel.find('.f_maxValue').val('');
	panel.find('.f_minValue').val('');
	var symbolBits = '.f_symbolList, .f_symbolAll, .f_symbolNone, .f_symbolApply, .f_symbolPreview';
	if (value == 'IN') {
		panel.find('div#Interval').show();
		panel.find('div#nonInterval').hide();
		panel.find('.midValue').show();
		panel.find(symbolBits).hide();
	} else if (value == 'IS' || value == 'NOT IS') {
		panel.find('div#Interval').hide();
		panel.find('div#nonInterval').show();
		panel.find('.midValue').hide();
		panel.find(symbolBits).show();
		commitSymbolSelection(panel);
	} else {
		panel.find('div#Interval').hide();
		panel.find('div#nonInterval').show();
		panel.find('.midValue').show();
		panel.find(symbolBits).hide();
	}
});

// --- NN classifier picker for value attributes ---
//
// We cache the registry's classifier list once per page load in
// window.solverClassifiers so that newly-added value-attribute rows can be
// populated synchronously, without waiting for the user to click the row.
window.solverClassifiers = window.solverClassifiers || null;
function loadSolverClassifiers(cb) {
	if (window.solverClassifiers) { if (cb) cb(window.solverClassifiers); return; }
	$.ajax({
		url: 'listClassifiers.action', type: 'GET', datatype: 'json',
		success: function(resp) {
			var list = (resp && resp.json && resp.json.classifiers) || (resp && resp.classifiers) || [];
			window.solverClassifiers = list;
			if (cb) cb(list);
		}
	});
}
function populateClassifierPicker(picker, suggestedId) {
	if (!picker || picker.length === 0) return;
	if (picker.data('populated')) {
		if (suggestedId) picker.val(suggestedId).trigger('change');
		return;
	}
	loadSolverClassifiers(function(list) {
		picker.data('populated', true);
		for (var i = 0; i < list.length; ++i) {
			var c = list[i];
			if (c.pluggable === false) continue; // skip relation/regression for now
			picker.append('<option value="' + c.classifierId + '">' + (c.displayName || c.classifierId) + '</option>');
		}
		if (suggestedId) picker.val(suggestedId).trigger('change');
	});
}

// Kick off the registry fetch as soon as the page is ready so the cache is hot.
$(function() { loadSolverClassifiers(); });

// Fire on every newly added value-attribute row, immediately, with no click required.
$('#addValueAttr').live('click', function() {
	setTimeout(function() {
		$('.bunchClassActive').find('.v_bunchAttrPanel').each(function() {
			var picker = $(this).find('.f_classifier');
			if (picker.length && !picker.data('populated')) {
				populateClassifierPicker(picker, getSuggestedClassifierForActiveNucleus());
			}
		});
	}, 0);
});

// Also populate on focus (mousedown) — this covers nuclei loaded from the DB
// where the row already exists when the page renders.
$('.v_bunchAttrPanel').live('mousedown', function() {
	var picker = $(this).find('.f_classifier');
	if (picker.length && !picker.data('populated')) {
		populateClassifierPicker(picker, getSuggestedClassifierForActiveNucleus());
	}
});

// Look at the active nucleus's name and find a registered classifier whose
// suggestedPrimitiveTypeName matches it (case-insensitive). Returns the
// classifierId or "" if none.
function getSuggestedClassifierForActiveNucleus() {
	var list = window.solverClassifiers;
	if (!list) return '';
	var name = $('.bunchClassActive').find('.bunchClassNameField').text().trim();
	if (!name) return '';
	for (var i = 0; i < list.length; ++i) {
		var c = list[i];
		if (c.pluggable === false) continue;
		if (c.suggestedPrimitiveTypeName &&
			c.suggestedPrimitiveTypeName.toLowerCase() === name.toLowerCase()) {
			return c.classifierId;
		}
	}
	return '';
}

// When the nucleus name changes, if a suggested classifier matches and the
// value-attribute picker is still on "(no classifier)", auto-select it.
$('.bunchClassNameField').live('blur', function() {
	var suggested = getSuggestedClassifierForActiveNucleus();
	if (!suggested) return;
	$('.bunchClassActive').find('.v_bunchAttrPanel').each(function() {
		var picker = $(this).find('.f_classifier');
		if (picker.length && (!picker.val() || picker.val() === '')) {
			if (!picker.data('populated')) {
				populateClassifierPicker(picker, suggested);
			} else {
				picker.val(suggested).trigger('change');
			}
		}
	});
});

// When a classifier is picked, populate the symbol dropdown from its classNames.
// In a NUCLEUS row we default to all classes selected — that's the natural meaning
// of "this nucleus's value attribute can be any of these classifier outputs".
// In a PRIMITIVE row (.primClassActive) we leave selection empty so the user pins
// to specific class(es).
$('.f_classifier').live('change', function() {
	var panel = $(this).closest('.v_bunchAttrPanel');
	var classifierId = $(this).val();
	var symbolSelect = panel.find('.f_symbolValue');
	var allBtn = panel.find('.f_symbolAll');
	var noneBtn = panel.find('.f_symbolNone');
	symbolSelect.empty();
	if (!classifierId) {
		symbolSelect.hide(); allBtn.hide(); noneBtn.hide();
		panel.find('.midValue').show();
		return;
	}
	$.ajax({
		url: 'getVocabulary.action',
		type: 'GET',
		datatype: 'json',
		data: { classifierId: classifierId },
		success: function(resp) {
			var vocab = (resp && resp.json) || resp;
			var names = (vocab && vocab.classNames) || [];
			var displayName = (vocab && vocab.displayName) || classifierId;
			var isNucleus = panel.closest('.bunchClassActive').length > 0;
			if (window.console && console.log) {
				console.log('[Solver] classifier "' + displayName + '" ('
					+ classifierId + ') has ' + names.length + ' class(es): '
					+ names.join(', '));
			}
			// Visible banner that survives until the next classifier change.
			var banner = panel.find('.f_classifierBanner');
			if (banner.length === 0) {
				banner = $('<div class="f_classifierBanner" style="clear:both; padding:3px 6px; margin-top:4px; background:#eef6ff; color:#0a3b6e; border:1px solid #b6cfe6; font-size:11px; border-radius:3px;"></div>');
				panel.find('#nonInterval').after(banner);
			}
			banner.text('Classifier "' + displayName + '" — ' + names.length
				+ ' class' + (names.length === 1 ? '' : 'es') + ': '
				+ names.join(', '));

			// Build a checkbox list, not a native multi-select. Linux Chromium's
			// native <select multiple> was rendering as a 1-row tall widget regardless
			// of size attribute, hiding all but the first option. Checkboxes are fully
			// CSS-controlled and immune to OS/GTK theming.
			var list = panel.find('.f_symbolList');
			list.empty();
			for (var i = 0; i < names.length; ++i) {
				var name = names[i];
				var nameEscaped = $('<div>').text(name).html();
				var checked = isNucleus ? ' checked="checked"' : '';
				var rowHtml = '<label style="display:block; line-height:1.6em; cursor:pointer; color:#111;">'
					+ '<input type="checkbox" class="f_symbolBox" value="' + nameEscaped + '"' + checked + ' style="margin-right:6px; vertical-align:middle;"/>'
					+ '<span>' + nameEscaped + '</span></label>';
				list.append(rowHtml);
			}

			var oper = panel.find('#nucComboOper option:selected').text();
			if (oper === 'IS' || oper === 'NOT IS') {
				list.show();
				panel.find('.f_symbolAll, .f_symbolNone, .f_symbolApply, .f_symbolPreview').show();
				panel.find('.midValue').hide();
			}
			// Commit the initial (possibly all-checked) state.
			commitSymbolSelection(panel);
		}
	});
});

// Helper: commit the current multi-select selection into the hidden
// f_symbolValueCommitted input on the same row, and update the preview span.
function commitSymbolSelection(panel) {
	var list = panel.find('.f_symbolList');
	var totalBoxes = list.find('.f_symbolBox').length;
	var selectedNames = list.find('.f_symbolBox:checked').map(function() { return $(this).val(); }).get();
	var joined = selectedNames.join(', ');
	panel.find('.f_symbolValueCommitted').val(joined);
	var preview = panel.find('.f_symbolPreview');
	if (totalBoxes === 0) {
		preview.text('(no classifier picked)').show();
	} else if (selectedNames.length === 0) {
		preview.text('(nothing selected of ' + totalBoxes + ' classes)').show();
	} else {
		preview.text('= ' + joined + '  (' + selectedNames.length + '/' + totalBoxes + ' classes)').show();
	}
}

// Auto-commit whenever any class checkbox toggles.
$('.f_symbolBox').live('change', function() {
	commitSymbolSelection($(this).closest('.v_bunchAttrPanel'));
});

// Convenience buttons next to the checkbox list.
$('.f_symbolAll').live('click', function() {
	var panel = $(this).closest('.v_bunchAttrPanel');
	panel.find('.f_symbolBox').prop('checked', true);
	commitSymbolSelection(panel);
});
$('.f_symbolNone').live('click', function() {
	var panel = $(this).closest('.v_bunchAttrPanel');
	panel.find('.f_symbolBox').prop('checked', false);
	commitSymbolSelection(panel);
});
// Manual "apply" — for users who want to be sure their picks are committed
// before clicking save. Also triggers the change handler above.
$('.f_symbolApply').live('click', function() {
	commitSymbolSelection($(this).closest('.v_bunchAttrPanel'));
});
////////////////// SAVE ATTRIBUTES ///////////////////
$('.saveNuc').live('click', function(event) {
	if ($('#perceptDisplayPanel').children('.bunchClassActive').size() == 1) {
		var isKey;
		var className = $('.bunchClassActive').find('.bunchClassNameField').text().trim();
		if ($('.bunchClassActive').find('.nucKey').hasClass('isNucKey')) {
			isKey = '1';
		} else { isKey = '0';}
		var nucleusConceptIndexAttrs = [];
		var nucleusConceptValueAttrs = [];
		var object;
		var arr;
		var _saveAborted = false;
		$('.bunchClassActive').find('.f_bunchAttrPanel').each(function(index){
			var attrArr;
			var attrName = $(this).find('.bunchAttrNameField').text();
			var attrOper = $(this).find('#nucComboOper option:selected').text();
			var midvalue = $(this).find('.midValue').val();
			var maxvalue = $(this).find('.f_maxValue').val();
			var minvalue = $(this).find('.f_minValue').val();
			if (midvalue == '' || midvalue == null) {
				attrArr = {
					'name': attrName,
					'oper': attrOper,
					'value': { 'minValue': minvalue,
							   'maxValue': maxvalue,
					}
				}
			}
			else {
				attrArr = {
					'name': attrName,
					'oper': attrOper,
					'value': midvalue,
				}
			}
			nucleusConceptIndexAttrs[index] = attrArr;
		});
		
		$('.bunchClassActive').find('.v_bunchAttrPanel').each(function(index){
			var attrArr;
			var attrName = $(this).find('.bunchAttrNameField').text();
			var attrOper = $(this).find('#nucComboOper option:selected').text();
			var midvalue = $(this).find('.midValue').val();
			var maxvalue = $(this).find('.f_maxValue').val();
			var minvalue = $(this).find('.f_minValue').val();
			var classifierId = $(this).find('.f_classifier').val() || '';
			// Force-commit the current multi-select state, then read the joined
			// value from the hidden mirror field. This is independent of jQuery's
			// .val() / $.isArray / $.toJSON quirks for <select multiple>.
			commitSymbolSelection($(this));
			var symbolValue = $(this).find('.f_symbolValueCommitted').val() || '';

			if (attrOper === 'IS' || attrOper === 'NOT IS') {
				if (!symbolValue) {
					alert("Operator '" + attrOper + "' on attribute '" + attrName +
						"' requires at least one class to be selected. Use the 'all' button to select the whole range, or pick specific classes with Ctrl/Cmd-click.");
					_saveAborted = true;
					return false; // break out of .each
				}
				if (!classifierId) {
					alert("Operator '" + attrOper + "' on attribute '" + attrName +
						"' requires a classifier to be picked.");
					_saveAborted = true;
					return false;
				}
				attrArr = {
					'name': attrName,
					'oper': attrOper,
					'value': symbolValue,
					'classifier': classifierId
				};
			} else if (midvalue == '' || midvalue == null) {
				attrArr = {
					'name': attrName,
					'oper': attrOper,
					'value': { 'minValue': minvalue,
							   'maxValue': maxvalue,
					}
				};
				if (classifierId) { attrArr.classifier = classifierId; }
			}
			else {
				attrArr = {
					'name': attrName,
					'oper': attrOper,
					'value': midvalue,
				};
				if (classifierId) { attrArr.classifier = classifierId; }
			}
			nucleusConceptValueAttrs[index] = attrArr;
		});
		if (_saveAborted) { return false; }
		object = {
			'name': className,
			'parent': '',
			'is_key': isKey,
			'nucleusConceptIndexAttrs': nucleusConceptIndexAttrs,
			'nucleusConceptValueAttrs': nucleusConceptValueAttrs,
		};
		var json = $.toJSON(object);
		//alert(json);
		var url = 'saveNucleus.action';
		$.ajax({
			url: url,
			type: 'POST',
			datatype: 'json',
			data: {nucleusName: json},
			success: function(json) {
				if (json) {
					$('#bunchClassListPanel').empty();
					initNucleus();
					$('#perceptDisplayPanel').children().draggable('destroy');
					$('.bunchClassActive').fadeOut().remove();
				} else {return false;}
			}
		});
	} else {
		alert('ERROR: No active Window, Please create an abstract first or select a window.');
	}
});

$('.nucKey').live({
	'click': function(){
		if ($(this).hasClass('isNucKey')) {
			$(this).removeClass('isNucKey');
			$(this).removeAttr('style');
		} else {
			$(this).addClass('isNucKey');
			$(this).css('background-position', '-62px -19px');
		}
	}
});


///////////////////// EDIT NAMES ///////////////////////

$('.bunchAttrNameField').live({
   'click': function(){
   	var ispresent = $(this).children().hasClass('tempName');
   	if ($(this).text().trim() == 'New attribute' && !ispresent) {
   		$(this).empty();
   		$(this).append('<input type="text" class="tempName" style="width: 100px; background: transparent;"/>');
   		$('.tempName').focus();
   	}
   },
   'dblclick': function(){
		var ispresent = $(this).children().hasClass('tempName');
		if ($(this).text().trim() != 'New Nucleus Abstract' && !ispresent) {
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

$('.bunchClassNameField').live({
	'click': function(event){
		var ispresent = $(this).children().hasClass('tempName');
		if ($(this).text().trim() == 'New Nucleus Abstract' && !ispresent) {
			$(this).empty();
			$(this).append('<input type="text" class="tempName" style="color: white; background: transparent;"/>');
			$('.tempName').focus();
		}
		else {
			return false;
		}
	},
	'dblclick': function(){
		var ispresent = $(this).children().hasClass('tempName');
		if ($(this).text().trim() != 'New Nucleus Abstract' && !ispresent) {
			$(this).empty();
			$(this).append('<input type="text" class="tempName" style="color: white; background: transparent;"/>');
			$('.tempName').focus();
		}
		else {
			return false;
		}
	},
	'focusout': function(){
		var name = $(this).find('.tempName').val();
   		if (name == '' || name == null) {
		$(this).append('New Nucleus Abstract');
		$('.tempName').remove();
		}	else { 
		$(this).append(name);
		$('.tempName').remove();
		return false; }
	}
});

///////////////////////// LIST ////////////////////////

$('.bunchClassNameInTable').live({
	"mouseover": function() {
		$(this).addClass('bunchClassActiveNameInTable');
		$('.bunchClassActiveNameInTable').find('.bunchClassNameInTableClose').show();
	},
	"mouseout": function() {
		$('.bunchClassActiveNameInTable').find('.bunchClassNameInTableClose').hide();
		$(this).removeClass('bunchClassActiveNameInTable');
	},
});
$('.nucleusName').live({
	"click": function(){
		$(this).parent().find('.bunchClassNameInTableloader').css('display', 'block');
		var nucleusName = $(this).text();
		loadNucleus(nucleusName);
		$(this).parent().find('.bunchClassNameInTableloader').hide();
	}
});
$('.bunchClassNameInTableClose').live({
	"click": function(){
		var nucleus = $('.bunchClassActiveNameInTable').text();
		if(confirm('Are you sure you want to delete this Nucleus Abstract?')) {
			var url = "removeNucleus.action";
			$.ajax({
				url: url,
				type: 'POST',
				datatype: 'json',
				data: {nucleusName: nucleus},
				success: function(json) {
				$('#bunchClassListPanel').empty();
				if (json) {
					var escaped = json.replace(/'/g, '"');
					var JSONarray = $.parseJSON(escaped);
					fillInitNucleusList(JSONarray);
					initSituation();
				} else {return false;}
				}
			});
		}
	}
});

///////////////////////// CLOSE ATTRIBUTES AND CLASS //////////////////////


$('.bunchClassActive .closeNuc').live('click', function(){
	  	$('#perceptDisplayPanel').children().draggable('destroy');
		$('.bunchClassActive').fadeOut(300, function() { $(this).remove(); });
});

$('.bunchAttrRemoveButtonPanel').live('click', function(){
		$('.bunchAttrSelected').remove();
});
/////////////////////// HELP /////////////////////////////

$('.helpNuc').live({
	'click': function(event){
		if ($('.helpContent').is(":hidden")) {
			$('#perceptDisplayPanel').find('.helpContent').fadeIn('slow').animate({
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
	$('#perceptDisplayPanel').find('.helpContent').fadeOut('fast');
	$('.helpContent').empty();
	}
});

///////////////// FUNCTIONS ///////////////////

function loadNucleus(nucleus) {
	var url = "viewNucleus.action";
	$.ajax({
			url: url,
			type: 'POST',
			dataType: 'html',
			data: {nucleusName: nucleus},
			success: function(json) {
			var escaped = json.replace(/'/g, '"');
			var JSON = $.parseJSON(escaped);
			fillNucleusWindow(JSON);
			}
		});
		
 return false;

}

function fillNucleusWindow(json) {
			var name = json.name;
			var jsonName = name.replace(/\s+/g, '');
			var panel = getNucleusPanel();
			var children = $('#perceptDisplayPanel').children('#bunchClassContainer').size();
			var ispresent = $('#perceptDisplayPanel').children().hasClass(jsonName);
			if (!ispresent) {
				if (children < 4) {
					$('#perceptDisplayPanel').children('.bunchClassContainer').animate({
						top: '+=10',
						left: '+=10'
					});
							$('#perceptDisplayPanel').append('<div id="bunchClassContainer" class="bunchClassContainer bunchClassContainerStyle ' + jsonName + '"></div>');
							$('.' + jsonName).hide();	
							$('.' + jsonName).append(panel);
							
							if (json.is_key == '1') {
								$('.' + jsonName).find('.nucKey').addClass('isNucKey');
								$('.' + jsonName).find('.nucKey').css('background-position', '-62px -19px');
							}
							
							$('.' + jsonName).find('.bunchClassNameField').empty();
							$('.' + jsonName).find('.bunchClassNameField').html(json.name);
							var IndexPanel = getIndexSkeleton();
							var ValuePanel = getValueSkeleton();
							$.each(json.nucleusConceptIndexAttrs, function(i){
										$('.' + jsonName).append('<div id="temp" style="display:block;"></div>');
										$('.' + jsonName).find('#temp').append(IndexPanel);
										$('#temp').find('.bunchAttrNameField').empty();
										$('#temp').find('.bunchAttrNameField').html(json.nucleusConceptIndexAttrs[i].name);
										
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
										var temp = $('#temp').html();
										$('.' + jsonName).find('.indexAttrContainer').append(temp);
										$('#temp').remove();
							});
							
							$.each(json.nucleusConceptValueAttrs, function(i){
										$('.' + jsonName).append('<div id="temp" style="display:block;"></div>');
										$('.' + jsonName).find('#temp').append(ValuePanel);
										$('#temp').find('.bunchAttrNameField').empty();
										$('#temp').find('.bunchAttrNameField').html(json.nucleusConceptValueAttrs[i].name);
										
										if (json.nucleusConceptValueAttrs[i].oper == 'IN') {
											$('#temp select').prepend('<option>'+json.nucleusConceptValueAttrs[i].oper+'</option>');
											$('#temp').find('div#Interval').show();
											$('#temp').find('div#Interval').empty();
											$('#temp').find('div#nonInterval').hide();
											$('#temp').find('#Interval').append('<input class="f_minValue" type="text" value="' + json.nucleusConceptValueAttrs[i].value.minValue + '" > - ');
											$('#temp').find('#Interval').append('<input class="f_maxValue" type="text" value="' + json.nucleusConceptValueAttrs[i].value.maxValue + '" >');
											
										}
										else {
											$('#temp select').prepend('<option>'+json.nucleusConceptValueAttrs[i].oper+'</option>');
											$('#temp').find('div#Interval').hide();
											$('#temp').find('div#nonInterval').show();
											$('#temp').find('div#nonInterval').empty();
											$('#temp').find('#nonInterval').append('<input class="midValue" type="text" value="' + json.nucleusConceptValueAttrs[i].value + '" >');
										}
										var temp = $('#temp').html();
										$('.' + jsonName).find('.valueAttrContainer').append(temp);
										$('#temp').remove();

							});
							$('#perceptDisplayPanel').children().removeClass('bunchClassActive');
							$('.' + jsonName).addClass('bunchClassActive')
							$('.' + jsonName).fadeIn('fast');	
				}
				else {
					alert('Maximum concepts reached.');
				}
			} else {
				return false;
		}
}

function toString(array) {
	var blkstr = [];
	$.each(array, function(idx2,val2) {                    
     var str = idx2 + ":" + val2;
     blkstr.push(str);
	});
	blkstr.join(", ");
	return blkstr;
}

function getIndexSkeleton() {
	var skeleton = $('.nucleusIndex').html();
	return skeleton;
}
function getValueSkeleton() {
	var skeleton = $('.nucleusValue').html();
	return skeleton;
}
function getNucleusPanel() {
	var panel = $('.classPanel').find('.bunchClassContainer').html();
	return panel;
}


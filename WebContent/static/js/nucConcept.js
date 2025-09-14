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


$('.bunchAttrSelected select').live('change',function(event) {
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
			nucleusConceptValueAttrs[index] = attrArr;
		});
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


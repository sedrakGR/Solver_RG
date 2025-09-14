/**
 * 
 */

$('.setTitleBar').live({

	  "mousedown": function(e) {
		inActiveWin();
		$(this).parent().draggable({containment: 'parent', refreshPositions: true}).trigger(e); 
	  	$(this).parent().addClass('setClassActive');
      },
	  "mouseup": function() {
	  	$('#conceptsDisplayPanel').children().removeClass('ui-draggable');
	  }
});
$('#setContainer').live({
	  "focus": function() {
	    inActiveWin();
		$(this).addClass('setClassActive');
	  }
});


//add panel
$('#addSetAttrs').live('click', function(){
		var children = $('#setAttr').children().length;
		if (children < 10) {
		var setattr = $('.setElementAttrPanel').html();
		$('.setClassActive').find('#setAttr').append(setattr);
		$('.setClassActive').find('#setAttr').children().show();
		}
		else { alert('Error: Maximum attributes reached'); return false;}
});

//add additional panel
$('#addSetAddAttrs').live('click', function(){
		var children = $('#setAttr').children().length;
		if (children < 10) {
		var setattr = $('.setAddAttrPanel').html();
		$('.setClassActive').find('#setAddAttrsCont').append(setattr);
		}
		else { alert('Error: Maximum attributes reached'); return false;}
});

//remove panel
$('#removeSetAttrs').live('click', function(){ 
	$('.setClassActive').find('#setAttr').children().each(function(){
     if ($(this).find('input:checked.setAttrChecker').length == 1) {
           $(this).remove(); 
     }
});
});

//remove additional panel
$('#removeSetAddAttrs').live('click', function(){ 
	$('.setClassActive').find('#setAdditAttr').children('#setAddAttrPanel').each(function(){
     if ($(this).find('input:checked.setAddAttrChecker').length == 1) {
           $(this).remove(); 
     }
});
});

//edit set field name
$('.setNameField').live({
   'click': function(){
   	var ispresent = $(this).children().hasClass('tempName');
   	if ($(this).text().trim() == 'New Set' && !ispresent) {
   		$(this).empty();
   		$(this).append('<input type="text" class="tempName" style="width: 100px; background: transparent;" maxlength="50"/>');
   		$('.tempName').focus();
   	} else { $(this).focusout(); return false;}
   },
   'dblclick': function(){
		var ispresent = $(this).children().hasClass('tempName');
		if ($(this).text().trim() != 'New Set' && !ispresent) {
			$(this).empty();
			$(this).append('<input type="text" class="tempName" style="width: 100px; background: transparent;" maxlength="50"/>');
			$('.tempName').focus();
		}
		else {
			$(this).focusout();
			return false;
		}
	},
   'focusout': function() {
   		var name = $(this).find('.tempName').val();
   		if (name == '' || name == null) {
		$(this).append('New Set');
		$('.tempName').remove();
		}	else { 
		$(this).append(name);
		$('.tempName').remove();
		return false; }
   }
});

//edit additional panel names
$('.setAddAttrName').live({
   'click': function(){
   	var ispresent = $(this).children().hasClass('tempName');
   	if ($(this).text().trim() == 'New addit attr' && !ispresent) {
   		$(this).empty();
   		$(this).append('<input type="text" class="tempName" style="width: 100px; background: transparent;" maxlength="13"/>');
   		$('.tempName').focus();
   	} else { $(this).focusout(); return false; }
   },
   'dblclick': function(){
		var ispresent = $(this).children().hasClass('tempName');
		if ($(this).text().trim() != 'New addit attr' && !ispresent) {
			$(this).empty();
			$(this).append('<input type="text" class="tempName" style="width: 100px; background: transparent;" maxlength="13"/>');
			$('.tempName').focus();
		}
		else { $(this).focusout(); return false; }
	},
   'focusout': function() {
   		   		var name = $(this).find('.tempName').val();
   		if (name == '' || name == null) {
		$(this).append('New addit attr');
		$('.tempName').remove();
		}	else { 
		$(this).append(name);
		$('.tempName').remove();
		return false; }
   }
});

//edit panel names 
$('.setAttrName').live({
   'click': function(){
   	var ispresent = $(this).children().hasClass('tempName');
   	if ($(this).text().trim() == 'New attribute' && !ispresent) {
   		$(this).empty();
   		$(this).append('<input type="text" class="tempName" style="width: 100px; background: transparent;" maxlength="13"/>');
   		$('.tempName').focus();
   	} else {$(this).focusout(); return false;}
   },
   'dblclick': function(){
		var ispresent = $(this).children().hasClass('tempName');
		if ($(this).text().trim() != 'New attribute' && !ispresent) {
			$(this).empty();
			$(this).append('<input type="text" class="tempName" style="width: 100px; background: transparent;" maxlength="13"/>');
			$('.tempName').focus();
		}
		else { $(this).focusout(); return false; }
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

$('.saveSet').live('click', function() {
		saveSet();
});
// parent select ///

$('#setAttrPanel').live({
      "mousedown": function() {
		$('.setClassActive').find('#setAttr').children().removeClass('setAttrActive');
        $(this).addClass('setAttrActive');
      }
});

$('.setAttrParentSelect').live('change',function(event) {
	var value = $('.setAttrActive').find('.setAttrParentSelect option:selected').val();
	var name = $('.setAttrActive').find('.setAttrParentSelect option:selected').text();
	if (name != '--Select Parent--') {
		switch (value) {
			case 'n':
				var url = "viewNucleus.action";
				$.ajax({
					url: url,
					type: 'POST',
					data: {
						nucleusName: name
					},
					success: function(json){
						$('.setAttrActive').find('.setAttrChildren').empty();
						var escaped = json.replace(/'/g, '"');
						var thisJSON = $.parseJSON(escaped);
						fillNucleusinSetWindow(thisJSON);
					}
				});
				break;
				
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
						$('.setAttrActive').find('.setAttrChildren').empty();
						var escaped = json.replace(/'/g, '"');
						var JSON = $.parseJSON(escaped);
						fillNucleusinSetWindow(JSON);
					}
				});
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
						$('.setAttrActive').find('.setAttrChildren').empty();
						var escaped = json.trim().replace(/'/g, '"');
						var JSON = $.parseJSON(escaped);
						fillSetInAttrWindow(JSON, name);
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
						$('.setAttrActive').find('.setAttrChildren').empty();
						var escaped = json.trim().replace(/'/g, '"');
						var JSON = $.parseJSON(escaped);
						fillInSetAttrWindow(JSON, name);
					}
				});
				break;
		}
	} else { $('.setAttrActive').find('.setAttrChildren').empty(); return false;}
});

$('.showSetAttrChild').live('click', function(){
	if (!($('.setAttrActive').find('.setAttrChildren').is(':empty'))) {
		if ($('.setAttrActive').find('.setAttrChildren').is(':visible')) {
			$('.setAttrActive').find('.setAttrChildren').hide();
			$('.setAttrActive').find('.showSetAttrChild').removeAttr('style');
		}
		else {
			$('.setAttrActive').find('.setAttrChildren').show();
			$('.setAttrActive').find('.showSetAttrChild').css('background-position', 'bottom center');
		}
	} else {return false;}
});

//Close Set window

$('.closeSet').live('click', function(event){
	  	$('#conceptsDisplayPanel').children().draggable('destroy');
		$('.setClassActive').fadeOut(300, function(){$(this).remove();});
});

// List
$('.setTab').live({
	'mousedown': function() {
		$.each($('#conceptsListPanel').children(), function() {
			$(this).find('.setNameInTableClose').hide();
		});
		$(this).parent().find('.setNameInTableClose').show();
	},
	'click': function() {
		var name = $(this).text();
		var url = 'viewSet.action';
		$.ajax({
			url: url,
			type: 'POST',
			datatype: 'json',
			data: {setName: name},
			success: function(json) {
				var escaped = json.trim().replace(/'/g, '"');
				var JSONarray = $.parseJSON(escaped);
				fillInSetWindow(JSONarray);
			}
		});
	}
});

$('.setNameInTableClose').live({
	'click': function() {
		if (confirm('Are you sure you want to delete this Set?')) {
			var name = $(this).parent().find('.setTab').text();
			var url = 'removeSet.action';
			$.ajax({
				url: url,
				type: 'POST',
				datatype: 'JSON',
				data: {
					setName: name
				},
				success: function(json){
					$('.setClassActive').remove();
					initConcepts();
				}
			});
		}
		else {
			return false;
		}
	}
});

//functions

function fillNucleusinSetWindow(json) {
	var element = $('.setAttrActive').find('.setAttrChildren');
	fillNucleusConceptIntoDiv(element, json);
}

function fillInSetAttrWindow(json, parent_prefix) {
	var element = $('.setAttrActive').find('.setAttrChildren');
	var index_attr = $('.setAttrActive').find('#setChildAttributeIndex').empty();
	index_attr.empty();
	fillCompositeIndexAttrs(index_attr, json);
	var initial = true;
	fillCompositeAttrIntoDiv(element, json, initial, parent_prefix);
}

function fillSetInAttrWindow(json, parent_prefix) {
	var element = $('.setAttrActive').find('.setAttrChildren');
	var initial = true;
	fillSetAttrIntoDiv(element, json, initial, parent_prefix + ".element");
}

function saveSet(){
	if ($('#conceptsDisplayPanel').children('.setClassActive').size() == 1) {
		var className = $('.setClassActive').find('.setNameField').text().trim();
		var parentName = '';
		
		var object = extractAttrSet($('.setClassActive'),className, parentName);
		var json = $.toJSON(object);
		var url = 'saveSet.action';
		$.ajax({
			url: url,
			type: 'POST',
			datatype: 'json',
			data: {
				setName: json
			},
			success: function(data){
				$('.setClassActive').remove();
				initSets();
				initConcepts();
			}
		});
	}
	else {
		alert('ERROR: No active Window, Please create an abstract first or select a window.');
	}
}

function fillInSetWindow(json) {
	var setPanel = getSetPanel();
	var setName = json.name;
	inActiveWin();
	$('#conceptsDisplayPanel').append('<div id="setContainer" class="setContainer setClassActive"></div>');
	$('.setClassActive').append(setPanel);
	$('.setClassActive').find('.setNameField').empty().append(setName);
	
	var element = $('.setClassActive');
	var initial = false;
	var parent_prefix = "";
	fillSetAttrIntoDivInternal(element, json, initial, parent_prefix);
}

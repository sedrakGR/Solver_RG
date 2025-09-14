//
// Sedrak
// edited by Vartges
// May 6th, 2011


//
// Opening Dialog box when clicking on 'new concept' button
//
$('#newConceptCreator').live('click', function(event) {
	var count = $('#conceptsDisplayPanel').children().length;
	if (count < 4) {
		$.ajax({
			url: 'concept/ConceptDiolog.jsp',
			type: 'POST',
			success: function(html){
				$('#dialogPlace').append(html);
			}
		});
	} else {
		alert('Error: Maximum windows reached');
		return false;
	}
});


/*$("#conceptType").live('change', function(event) {
	$('#parentBunchClassPanel').empty();
	var selected = $('#conceptType option:selected').text();
	if (selected == 'Primitive Abstract') {
		$.ajax({
			url : 'concept/ParentBunchClass.jsp',
			type: 'POST',
			success : function(html) {
		    	$("#parentBunchClassPanel").append(html);	
			}
		});
	}
});*/

$("#CancelButton").live('click', function(event) {
	$("#mainDiologContainer").remove();
});

$("#OkButton").live('click', function(event) {
	//$("#conceptsDisplayPanel").empty();
	var selected = $('#conceptType option:selected').text();
	inActiveWin();
	switch (selected) {
	
	case 'Primitive Abstract': 
		$("#conceptsDisplayPanel").find(".primitiveParent").empty();
		$("#conceptsDisplayPanel").find(".indexAttrContainer").empty();
		$("#conceptsDisplayPanel").find(".valueAttrContainer").empty();
		
		var primPanel = getPrimitivePanel();
		$("#conceptsDisplayPanel").append('<div id="primitiveConceptContainer" class="primitiveConceptContainerStyle primClassActive"></div>');
		$(".primClassActive").append(primPanel);
		loadPrimitiveParents();
		break;
		
	case 'Composite Abstract':
		$("#conceptsDisplayPanel").find(".composParent").empty();

		var panel = getCompositePanel();
		$("#conceptsDisplayPanel").append('<div id="compositeConceptContainer" class="compositeConceptContainer compositeConceptContainerStyle compClassActive"></div>');
		$(".compClassActive").append(panel);
		loadCompositeParents();
		loadConceptParents('.composConcAttrParent');	
		break;
	
	case 'Set': 
		var panel = getSetPanel();
		$("#conceptsDisplayPanel").append('<div class="setContainer setClassActive" id="setContainer"></div>');
		$(".setClassActive").append(panel);	
		loadConceptParents('.setAttrParentSelect');	
		break;
		
	case 'Action': 
		var panel = getActionPanel();
		$("#conceptsDisplayPanel").append('<div class="actionContainer actionClassActive" id="actionContainer"></div>');
		$(".actionClassActive").append(panel);	
		$(".actionClassActive").find('.preActionParent').empty();
		loadConceptParents('.preActionParent');	
	break;
}
	$('#dialogPlace').empty();
});

//functions 
function loadPrimitiveParents() {
		var url = 'initPrimitive.action';
		$.ajax({
			url: url,
			type: 'POST',
			datatype: 'json',
			success: function(json) {
				$("#conceptsDisplayPanel").find(".primitiveParent").append('<option>--Select Parent--</option>');
				if (json) {
					var escaped = json.replace(/'/g, '"');
					var JSONarray = $.parseJSON(escaped);
					JSONarray.PrimitiveList.pop();
					$.each(JSONarray.PrimitiveList, function(i) {
						$("#conceptsDisplayPanel").find(".primitiveParent").append('<option>'+JSONarray.PrimitiveList[i].name+'</option>');
					});
				} else {return false;}
			}
		});
}

function loadCompositeParents() {
		var url = 'initCompositeParent.action';
		$.ajax({
			url: url,
			type: 'POST',
			datatype: 'json',
			success: function(json) {
				$("#conceptsDisplayPanel").find(".composParent").append('<option>--Select Parent--</option>');
				if (json) {
					var escaped = json.replace(/'/g, '"');
					var JSONarray = $.parseJSON(escaped);
					JSONarray.ConceptList.pop();
					$.each(JSONarray.ConceptList, function(i) {
						$("#conceptsDisplayPanel").find(".composParent").append('<option value=">'+JSONarray.ConceptList[i].type +'">'+JSONarray.ConceptList[i].name +'</option>');
					});
				} else {return false;}
			}
		});
}

function loadConceptParents(divname) {
		var url = 'initComposite.action';
		$.ajax({
			url: url,
			type: 'POST',
			datatype: 'json',
			success: function(json) {
				resetConceptCombo(divname);
				$("#conceptsContentMainPanel").find(divname).append('<option>--Select Parent--</option>');
				if (json) {
					var escaped = json.replace(/'/g, '"');
					var JSONarray = $.parseJSON(escaped);
					JSONarray.ConceptList.pop();
					$.each(JSONarray.ConceptList, function(i) {
						$("#conceptsContentMainPanel").find(divname).append('<option value="'+ JSONarray.ConceptList[i].type +'">'+JSONarray.ConceptList[i].name+'</option>');
					});
				} else {return false;}
			}
		});
}


function getPrimitivePanel() {
	var panel = $('.primitivePanel').find('#primitiveConceptContainer').html();
	return panel;
}
function getCompositePanel() {
	var panel = $('.compositePanel').find('#compositeConceptContainer').html();
	return panel;
}
function getSetPanel() {
	var panel = $('.setPanel').find('#setContainer').html();
	return panel;
}
function getActionPanel() {
	var panel = $('.actionPanel').find('#actionContainer').html();
	return panel;
}
function inActiveWin() {
		resetAttrs();
		$('#conceptsDisplayPanel').children().removeClass('compClassActive');
		$('#conceptsDisplayPanel').children().removeClass('primClassActive');
		$('#conceptsDisplayPanel').children().removeClass('setClassActive');
		$('#conceptsDisplayPanel').children().removeClass('actionClassActive');
}
function resetConceptCombo(div) {
 	$("#conceptsContentMainPanel").find(div).empty();
 }
 
 function resetNucAttrs() {
	    $('.bunchClassActive').find('#indexAttrContainer').children().removeClass('bunchAttrSelected');
		$('.bunchClassActive').find('#valueAttrContainer').children().removeClass('bunchAttrSelected');
}
function resetCompAttrs() {
		$('.compClassActive').find('#compIndexAttributes').children().removeClass('bunchAttrSelected');
		$('.compClassActive').find('.compAttrChildren').children().removeClass('bunchAttrSelected');
		$('.compClassActive').find('.composConcIndexAttributes').children().removeClass('bunchAttrSelected');
		$('.compClassActive').find('.compChildAttrChildren').children().removeClass('bunchAttrSelected');
		$('.compClassActive').find('#compChildIndexAttributes').children().removeClass('bunchAttrSelected');
}
function resetSetAttrs() {
		$('.setClassActive').find('.setAttrChildren').children().removeClass('bunchAttrSelected');
		$('.setClassActive').find('#setChildAttributeIndex').children().removeClass('bunchAttrSelected');
		$('.setClassActive').find('.compChildAttrChildren').children().removeClass('bunchAttrSelected');
		$('.compClassActive').find('#compChildIndexAttributes').children().removeClass('bunchAttrSelected');
}

function resetActionAttrs() {
		$('.actionClassActive').find('.actionChildAttribute').children().removeClass('bunchAttrSelected');
		$('.actionClassActive').find('#actionChildAttributeIndex').children().removeClass('bunchAttrSelected');
		$('.actionClassActive').find('.compChildAttrChildren').children().removeClass('bunchAttrSelected');
		$('.actionClassActive').find('#compChildIndexAttributes').children().removeClass('bunchAttrSelected');
}
function resetAttrs() {
	resetCompAttrs();
	resetSetAttrs();
	resetActionAttrs();
}
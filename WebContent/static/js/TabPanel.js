	var array = {
		'tab1' : 'windows/homeContent.jsp',
		'tab2' : 'windows/nucContent.html',
		'tab3' : 'windows/conContent.html',
		'tab4' : 'windows/chess.html',
		'tab5' : 'windows/marketContent.html'
	};
	var array1 = {
		'tab1' : 'SSRGT &trade;',
		'tab2' : 'SSRGT &trade; | Nucleus Abstract',
		'tab3' : 'SSRGT &trade; | Abstract',
		'tab4' : 'SSRGT &trade; | Chess',
		'tab5' : 'SSRGT &trade; | Market',
		'tab6' : 'SSRGT &trade; | Graph of Abstracts'
	};
	
$(document).ready(function(){
		init();
		$('div.tabs').mouseover(function(event) {	
		$('div.tabs').removeClass('tabHoverClass');
		$(this).addClass('tabHoverClass');
		}).mouseout(function(){
			$('div.tabs').removeClass('tabHoverClass');
		});
	$('div.tabs').click(function(event){
		$('div.tabs').removeClass('tabSelectedClass');
		var id = $(this).attr('id');
		loadPage(id);
		$(this).addClass('tabSelectedClass');
	});
});

function init() {
	$('#contentGroup').showLoading();
	$('#contentGroup').empty();
	dbManage();
	var url = 'windows/homeContent.jsp';
	$.ajax({
		url: url,
		type: 'POST',
		success: function(html)	{
			$('#contentGroup').append(html);
			$('#tab1').addClass('tabSelectedClass');
		}
	});
	setTimeout("$('#contentGroup').hideLoading()", 1000 );
}

function loadPage(id){
	$('#contentGroup').showLoading();
	$('#contentGroup').empty();
	switch (id) {
	
	case 'tab1':		
		$.ajax({
			url : 'windows/homeContent.jsp',
			type: 'POST',
			success : function(html) {
				dbManage();
		    	$("#contentGroup").append(html);	
			}
		});
		
		break;
		
	case 'tab2':
		$.ajax({
			url : 'windows/nucContent.html',
			type: 'POST',
			success : function(html) {
		    	$("#contentGroup").append(html);
				initNucleus();
			}
		});
		break;
	case 'tab3': 
		$.ajax({
			url : 'windows/conContent.html',
			type: 'POST',
			success : function(html) {
		    	$("#contentGroup").append(html);
				if ($.browser.msie) {
					$('#contentGroup').find(".sitAccordionContainer").accordion({
						animated: false
					});
				}
				else {
					$('#contentGroup').find(".sitAccordionContainer").accordion();
				}
			initConcepts();
			initActions();
			initSets();
			}
		});
		break;
	
	case 'tab4': 
		$.ajax({
			url : 'windows/chess.html',
			type: 'POST',
			success : function(html) {
		    	$("#contentGroup").append(html);
		    	$('#plansList').initPlans()
				initChess();
			}
		});
		break;
	case 'tab5': 
		$.ajax({
			url : 'windows/marketContent.jsp',
			type: 'POST',
			success : function(html) {
		    	$("#contentGroup").append(html);	
			}
		});
		break;
	case 'tab6': 
		window.location.replace("graph_window.jsp");
		break;
	/************************************************ Taron ******************************************************/
	case 'tab7':
		
		$.ajax({
			url : 'windows/plansContent.html',
			type: 'POST',
			success : function(html) {
		    	$("#contentGroup").append(html);
		    	if ($.browser.msie) {
					$('#contentGroup').find(".sitAccordionContainer").accordion({
						animated: false
					});
				}
				else {
					$('#contentGroup').find(".sitAccordionContainer").accordion();
				}
		    	$('#goalsListPanel').initGoals();
		    	$('#plansListPanel').initPlans();
		    	$('#plansWrapperListPanel').initPlanWrappers();


		    	$(".sortable").sortable();
		        $(".sortable").disableSelection();
			}
		});
		break;
		/************************************************ End of Taron ******************************************************/
	}
	
	jQuery.each(array1, function(key, value){
		if (id == key) {
			$('title').empty();
			$('title').append(value);
		}
	});
	setTimeout("$('#contentGroup').hideLoading()", 1000 );
}



function initNucleus() {
	$('#bunchClassListPanel').showLoading();
	var url = 'initNucleus.action';
		$.ajax({
			url: url,
			type: 'POST',
			datatype: 'json',
			success: function(json) {
				$('#bunchClassListPanel').empty();
				if (json) {
					var escaped = json.replace(/'/g, '"');
					var JSONarray = $.parseJSON(escaped);
					fillInitNucleusList(JSONarray);
				} else {return false;}
			}
		});
		$('#bunchClassListPanel').hideLoading();
}

function initConcepts() {
	$('#setAccordionPanel').showLoading();
	$('.primListWrapper').empty();
	$('.compListWrapper').empty();
	var url = 'initComposite.action';
		$.ajax({
			url: url,
			type: 'POST',
			datatype: 'json',
			success: function(json) {
				if (json) {
					var escaped = json.replace(/'/g, '"');
					var JSONarray = $.parseJSON(escaped);
					console.info(JSONarray)
					fillInitConceptList(JSONarray);
				} else { return false;}
			}
		});
	$('#setAccordionPanel').hideLoading();
}

/* this needs to be deleted becouse of new generic functions
function initGoals() {
	$('#setAccordionPanel').showLoading();
	$('#goalsListPanel').empty();

	var url = 'initGoal.action';
		$.ajax({
			url: url,
			type: 'POST',
			datatype: 'json',
			success: function(json) {
				if (json) {
					//console.log(json , 'json@ initGoal ACTION');
					var escaped = json.replace(/'/g, '"');
					var JSONarray = $.parseJSON(escaped);
					fillInitGoalList(JSONarray);
				} else {
					return false;}
			}
		});
	$('#setAccordionPanel').hideLoading();
}
function fillInitGoalList(jsonArray) {
	$.each(jsonArray.GoalsList, function(i) {
		$('#goalsListPanel').append('<div class="treeNameInTable treeNameInTableStyle"><div class="GoalTab">'
			+jsonArray.GoalsList[i].name+'</div><span class="goalRemoveBtn">x</span></div>');
	});
}


function initPlans() {
	$('#setAccordionPanel').showLoading();
	$('#plansListPanel').empty();
	var url = 'initPlan.action'; //'initComposite.action';
		$.ajax({
			url: url,
			type: 'POST',
			datatype: 'json',
			success: function(json) {
				if (json) {
					var escaped = json.replace(/'/g, '"');
					var JSONarray = $.parseJSON(escaped);
					fillInitPlanList(JSONarray);
				} else {
					return false;
				}

			}
		});
	$('#setAccordionPanel').hideLoading();
}
function fillInitPlanList(jsonArray) {	
	$.each(jsonArray.PlansList, function(i) {
		$('#plansListPanel').append('<div class="treeNameInTable treeNameInTableStyle"><div class="PlanTab">'+jsonArray.PlansList[i].name+'</div><span class="planRemoveBtn" >x</span></div>');
	});
}
*/
/************************************************ End of Taron ******************************************************/
function initActions() {
	var url = 'initAction.action';
		$.ajax({
			url: url,
			type: 'POST',
			datatype: 'json',
			success: function(json) {
				if (json) {
					var escaped = json.replace(/'/g, '"');
					var JSONarray = $.parseJSON(escaped);
					fillInitActionList(JSONarray);
				} else {return false;}
			}
		});
}
function initSets() {
	$('#setListBodyConc').empty();
	var url = 'initSet.action';
		$.ajax({
			url: url,
			type: 'POST',
			datatype: 'json',
			success: function(json) {
				if (json) {
					var escaped = json.replace(/'/g, '"');
					var JSONarray = $.parseJSON(escaped);
					fillInitConceptList(JSONarray);
				} else {return false;}
			}
		});
}
/*function initPrimitive() {
	$('#setAccordionPanel').showLoading();
	var url = 'initPrimitive.action';
		$.ajax({
			url: url,
			type: 'POST',
			datatype: 'json',
			success: function(json) {
				if (json) {
					var escaped = json.replace(/'/g, '"');
					var JSONarray = $.parseJSON(escaped);
					fillInitConceptList(JSONarray, 'p');
				} else { return false;}
			}
		});
	$('#setAccordionPanel').hideLoading();
}*/

function initSituation() {
	var url = 'initNucleus.action';
		$.ajax({
			url: url,
			type: 'POST',
			datatype: 'json',
			success: function(json) {
				if (json) {
					$('#bunchListBody').empty();
					var escaped = json.replace(/'/g, '"');
					var JSONarray = $.parseJSON(escaped);
					FillNucleusinSitList(JSONarray);
				} else {return false;}
			}
		});
}

function fillInitNucleusList(json) {
	json.NucleusList.pop();
	$.each(json.NucleusList, function(i) {
		$('#bunchClassListPanel').append('<div class="bunchClassNameInTable bunchClassNameInTableStyle"><div class="nucleusName">'+json.NucleusList[i].name+'</div><div class="bunchClassNameInTableloader"></div><div class="bunchClassNameInTableClose"></div></div>');
	});
}

function FillNucleusinSitList(json) {
	json.NucleusList.pop();
	$.each(json.NucleusList, function(i) {
		$('#bunchListBody').append('<div class="bunchClassNameInTable bunchClassNameInTableStyle"><div class="nucleusName">'+json.NucleusList[i].name+'</div><div class="bunchClassNameInTableloader"></div><div class="bunchClassNameInTableClose"></div></div>');
	});
}

function fillPrimitiveList(parentName, className) {
	$('#conceptsListPanel').append('<div class="familyContainer"><div class="treeParentNameInTable treeParentNameInTableStyle">'+ parentName +'<div class="arrowLeft"></div></div><div class="treeChildNameInTable treeChildNameInTableStyle"><div class="childName">'+ className +'</div><div class="primClassNameInTableClose"></div></div></div>');
}

function fillInitConceptList(json) {
	json.ConceptList.pop();
	$.each(json.ConceptList, function(i) {
		var type = json.ConceptList[i].type;
		switch(type) {
		case 'p':
		$('.primListWrapper').append('<div class="familyContainer"><div class="treeParentNameInTable treeParentNameInTableStyle viewPrimitiveConcept">'+ json.ConceptList[i].parent +'<div class="arrowLeft"></div></div><div class="treeChildNameInTable treeChildNameInTableStyle"><div class="childName">'+ json.ConceptList[i].name +'</div><div class="primClassNameInTableClose"></div></div></div>');
		break;
		case 'c':
		$('.compListWrapper').append('<div class="treeNameInTable treeNameInTableStyle"><div class="CompositeConceptTab">'+ json.ConceptList[i].name +'</div><div class="compositeClassNameInTableClose"></div></div>');
		break;
		case 's':
		$('#setListBodyConc').append('<div class="treeNameInTable treeNameInTableStyle"><div class="setTab">'+ json.ConceptList[i].name +'</div><div class="setNameInTableClose"></div></div>');
		break;
		}
	});
}

function fillInitActionList(json) {
	$('#actionListBodyConc').empty();
	json.actionList.pop();
	$.each(json.actionList, function(i) {
		$('#actionListBodyConc').append('<div class="treeNameInTable treeNameInTableStyle"><div class="ActionTab">'+ json.actionList[i].name +'</div><div class="ActionNameInTableClose"></div></div>')
	});
}

function dbManage(){
	$('#dump').live('click', function() { 
		var url = 'dump.action';
		$.ajax({
			url: url,
			type: 'GET',
			success : function(html) {				
		    	alert(url);				
			}
		});
	});
	$('#undump').live('click', function() { 
		var url = 'undump.action';
		$.ajax({
			url: url,
			type: 'GET',
			success : function(html) {				
		    	alert(url);				
			}
		});
	});
	$('#initializedb').live('click', function() { 
		var url = 'initializedb.action';
		$.ajax({
			url: url,
			type: 'GET',
			success : function(html) {				
		    	alert(url);				
			}
		});
	});
	$('#resetdb').live('click', function() { 
		var url = 'resetdb.action';
		$.ajax({
			url: url,
			type: 'GET',
			success : function(html) {				
		    	alert(url);				
			}
		});
	});
}
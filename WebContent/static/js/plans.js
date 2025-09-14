$(document).ready(function(){
	count = 1;
	countForPlanWrapper = 1;

	$(".goalRemoveBtn").live("click", function(){
		var goalName = $(this).prev("div.GoalTab").text();
		removeGoal(goalName);
	});
	$(".planRemoveBtn").live("click", function(){
		var planName = $(this).prev("div.PlanTab").text();
		removePlan(planName);
	});

	$(".planWrapperRemoveBtn").live("click", function(){
		var planWrapperName = $(this).prev("div.PlanWrapperTab").text();
		removePlanWrapper(planWrapperName);
	});

// New Goal , Plan and PlanWrapper buttons
	$("#newGoalBtn").live("click", function(){
		count = 1;
		$('.goalAddUpdateBlock').removeClass('visible').addClass('hidden');
		console.log('new Goal button pressed');
		showGoalForm(false);
	});
	$("#newPlanBtn").live("click", function(){
		$('.planAddUpdateBlock').removeClass('visible').addClass('hidden');
		showPlanForm(false);
	});
	
	$("#newPlanWrapperBtn").live("click", function(){
		countForPlanWrapper = 1;
		console.log('new plan wrapper button pressed');
		$('.planWrapperAddUpdateBlock').removeClass('visible').addClass('hidden');
		showPlanWrapperForm(false);
	});
	
	$('#addEvalAttrGoal').live('click', function(event) {
		$('.goalAddUpdateBlock').find('#evalAttrContainerGoal').children().size();
		var indexPanel = '<select id="goalCriteriaType_' + count + '" name="criteriaType_' + count + '">' +
				'<option value=""></option></select>' +
				'<input type="number" name="criteriaPriorety_' + count
				+ '" id="goalCriteriaPriorety_' + count + '" placeholder="Insert Priorety">' +
				'<input type="text" name="criteriaExpression_' + count
				+ '" id="goalCriteriaExpression_' + count + '" placeholder="Insert Expression">';
		$('.goalAddUpdateBlock').find('#evalAttrContainerGoal').append(indexPanel);
		getCriteriaPropertiesForGoal(count);
		count ++;
	});
	
	$('#addEvalAttrPlanWrapper').live('click', function(event) {
		$('.planWrapperAddUpdateBlock').find('#evalAttrContaineraddEvalAttrPlanWrapper').children().size();
		var indexPanel = '<select id="planWrapperCriteriaType_' + countForPlanWrapper + '" name="criteriaType_' + countForPlanWrapper + '">' +
				'<option value=""></option></select>' +
				'<input type="number" name="criteriaPriorety_' + countForPlanWrapper
				+ '" id="planWrapperCriteriaPriorety_' + countForPlanWrapper + '" placeholder="Insert Priorety">' +
				'<input type="text" name="criteriaExpression_' + countForPlanWrapper
				+ '" id="planWrapperCriteriaExpression_' + countForPlanWrapper + '" placeholder="Insert Expression">';
		$('.planWrapperAddUpdateBlock').find('#evalAttrContainerPlanWrapper').append(indexPanel);
		getCriteriaPropertiesForPlanWrapper(countForPlanWrapper);
		countForPlanWrapper ++;
	});
	
	

	
// Update Goal/Plan
	$(".GoalTab").live("click", function(){
		$('.goalAddUpdateBlock').removeClass('visible').addClass('hidden');
		var goalName = $(this).text();
		showGoalForm(goalName);
	});
	$(".PlanTab").live("click", function(){
		$('.planAddUpdateBlock').removeClass('visible').addClass('hidden');
		var planName = $(this).text();
		showPlanForm(planName);
	});
	
	$(".PlanWrapperTab").live("click", function(){
		$('.planWrapperAddUpdateBlock').removeClass('visible').addClass('hidden');
		var planWrapperName = $(this).text();
		showPlanWrapperForm(planWrapperName);
	});
	
	
	
// Close goal/plan form block
	$("#goalAddUpdateForm  #CancelButtonGoal").live("click", function(){
		$('.goalAddUpdateBlock').removeClass('visible').addClass('hidden');
	});
	$("#planAddUpdateForm  #CancelButtonPlan").live("click", function(){
		$('.planAddUpdateBlock').removeClass('visible').addClass('hidden');
	});
	
	$("#planWrapperAddUpdateForm  #CancelButtonPlanWrapper").live("click", function(){
		$('.planWrapperAddUpdateBlock').removeClass('visible').addClass('hidden');
	});


// Save goal/plan
	$("#goalAddUpdateForm  #SaveButtonGoal").live("click", function(e){
		if($('#goalFormHead').text() == 'New goal'){
			var goalName = false;
		}
		else{
			var goalName = $('#goalFormHead').text();
		}

		if(validate()){
			saveGoal(goalName);
		}
		e.preventDefault();
	});
	$("#planAddUpdateForm  #SaveButtonPlan").live("click", function(e){
		if($('#planFormHead').text() == 'New plan'){
			var planName = false;
		}
		else{
			var planName = $('#planFormHead').text();
		}
		savePlan(planName);
		e.preventDefault();
	});
	
	$("#planWrapperAddUpdateForm  #SaveButtonPlanWrapper").live("click", function(e){
		if($('#planWrapperFormHead').text() == 'New plan wrapper'){
			var planWrapperName = false;
		}
		else{
			var planWrapperName = $('#planWrapperFormHead').text();
		}
		savePlanWrapper(planWrapperName);
		e.preventDefault();
	});
	
	
	
	$('#planAddUpdateForm #AddGoal').live('click', function(){
		var goalUl = $('#planAddUpdateForm #planGoals ul li:first').clone();
		$('#planAddUpdateForm #planGoals ul').append(goalUl);
	});
	$('#planAddUpdateForm .removeGoalFromPlan').live('click', function(){
		$(this).parents("li").remove();
	});
	
	
	$('#planWrapperAddUpdateForm .removePlanFromPlanWrapper').live('click', function(){
		$(this).parents("li").remove();
	});
	
});


function validate(){
	var a = $('#goalAddUpdateForm input[name="name"]').val();
	var b = $('#goalAddUpdateForm select[name="preCondition"]').val();
	var c = $('#goalAddUpdateForm select[name="postCondition"]').val();
	var d = $('#goalAddUpdateForm input[name="depth"]').val();
	
	
	if(a && b && c && d) {
		return true;
	} else {
		alert("lracreq * @");
	}
	
	
	
};

function saveGoal(goalName){
	console.log('save Goal function');
	var dataObjArray = [];

		if(goalName == false){
			var mainObj = {
				'name'			:	$('#goalAddUpdateForm input[name="name"]').val(),
				'preCondition'	:	$('#goalAddUpdateForm select[name="preCondition"]').val(),
				'postCondition'	:	$('#goalAddUpdateForm select[name="postCondition"]').val(),
				'depth'			:	$('#goalAddUpdateForm input[name="depth"]').val(),
				'primary'		:	$('#goalAddUpdateForm input[name="isGoalFinal"]').is(':checked')
			};
			for(var i = 1; i < count ; i++){
					var data = {
						'priority'       :   $('#goalAddUpdateForm input[name="criteriaPriorety_' + i + '"]').val(),
						'type'	         :   $('#goalAddUpdateForm select[name="criteriaType_' + i + '"]').val(),
						'criteria'     :   $('#goalAddUpdateForm input[name="criteriaExpression_' + i + '"]').val(),
						
					};
					dataObjArray.push(data);
			}
		} else {

			 var mainObj = {
				'goalName'		:	goalName,
				'name'			:	$('#goalAddUpdateForm input[name="name"]').val(),
				'preCondition'	:	$('#goalAddUpdateForm select[name="preCondition"]').val(),
				'postCondition'	:	$('#goalAddUpdateForm select[name="postCondition"]').val(),
				'depth'			:	$('#goalAddUpdateForm input[name="depth"]').val(),
				'primary'		:	$('#goalAddUpdateForm input[name="isGoalFinal"]').val()
			};

			for(var i = 1 ; i < count; i++) {
					var data = {
						'priority': $('#goalAddUpdateForm input[name="criteriaPriorety_' + i + '"]').val(),
						'type': $('#goalAddUpdateForm select[name="criteriaType_' + i + '"]').val(),
						'criteria': $('#goalAddUpdateForm input[name="criteriaExpression_' + i + '"]').val(),
						
					};
					dataObjArray.push(data);
				}
		}

		count = 1;
	var data = $.extend(mainObj, {
		"evaluator": dataObjArray  
	});
	
	var dataJson= JSON.stringify(data);
	//e.g. {"name":"aaaaa", "primary" : "true", "preCondition":"comp1", "postCondition":"comp2", "depth":"8",
	// "evaluator" : [ {"criteria" : "expression1", "prioroity" : "1", "type" : "MIN"}, {"criteria" : "expression2", "prioroity" : "2", "type" : "MAX"}]}
	console.log('sending data is ', dataJson);
	var url = 'saveGoal.action';
	$.ajax({
		url: url,
		type: 'POST',
		datatype: 'json',
		data : {
			goal: dataJson
		},
		success: function(data) {
			if(data){
				$('#goalsListPanel').initGoals();
				$('.goalAddUpdateBlock').removeClass('visible').addClass('hidden');
			} else {
				console.log('Error! Goal not saved!');//alert	
			}
		}
	});
}

function getCompositeAbstracts(){

	var url = 'initCompositeParent.action';
	$.ajax({
		url: url,
		type: 'POST',
		datatype: 'json',
		success: function(json) {
			if (json) {
				var escaped = json.replace(/'/g, '"');
				var JSONarray = $.parseJSON(escaped);
					$('#goalAddUpdateForm #goalPreCondition, #goalAddUpdateForm #goalPostCondition').empty();
					$('#goalAddUpdateForm #goalPreCondition').append('<option value="">Select preCondition</option>');
					$('#goalAddUpdateForm #goalPostCondition').append('<option value="">Select postCondition</option>');
				fillConceptList(JSONarray);
			} else { return false;}
		}
	});
};

function getCompositeAbstractForWrapper() {
	var url = 'initCompositeParent.action';
	$.ajax({
		url: url,
		type: 'POST',
		datatype: 'json',
		success: function(json) {
			if (json) {
				var escaped = json.replace(/'/g, '"');
				var JSONarray = $.parseJSON(escaped);
					$('#planWrapperAddUpdateForm #planWrapperPreCondition').empty();
					$('#planWrapperAddUpdateForm #planWrapperPreCondition').append('<option value="">Select preCondition</option>');
				fillConceptListForWrapper(JSONarray);
			} else { return false;}
		}
	});
};

function getCriteriaPropertiesForGoal(index) {
	var jsonCriteria = $("#criteriaTypeJson").text();
	var escaped = jsonCriteria.replace(/'/g, '"');
	var JSONarray = $.parseJSON(escaped);
	if(jsonCriteria){
		$('#goalAddUpdateForm #goalCriteriaType_' + index).empty();
		$('#goalAddUpdateForm #goalCriteriaType_' + index).append('<option value="">Select Criteria Type</option>');
			fillGoalFormCriteria(JSONarray, index);
	} else {return false;}
}

function getCriteriaPropertiesForPlanWrapper(index) {
	var jsonCriteria = $("#criteriaTypeJson").text();
	var escaped = jsonCriteria.replace(/'/g, '"');
	var JSONarray = $.parseJSON(escaped);
	if(jsonCriteria){
		$('#planWrapperAddUpdateForm #planWrapperCriteriaType_' + index).empty();
		$('#planWrapperAddUpdateForm #planWrapperCriteriaType_' + index).append('<option value="">Select Criteria Type</option>');
			fillPlanWrapperFormCriteria(JSONarray, index);
	} else {return false;}
}

function fillConceptList(json) {
		json.ConceptList.pop();
		$.each(json.ConceptList, function(i) {
			$('#goalAddUpdateForm #goalPreCondition, #goalAddUpdateForm #goalPostCondition').append('<option value="'
					+ json.ConceptList[i].name + '">'+ json.ConceptList[i].name +'</option>');
		});
};

function fillConceptListForWrapper(json) {
	json.ConceptList.pop();
	$.each(json.ConceptList, function(i) {
		$('#planWrapperAddUpdateForm #planWrapperPreCondition').append('<option value="'
				+ json.ConceptList[i].name + '">'+ json.ConceptList[i].name +'</option>');
	});
};

function fillGoalFormCriteria(jsonArray ,index) {
	$.each(jsonArray.criteriaTypeList, function(i) {
		$('#goalAddUpdateForm #goalCriteriaType_' + index).append('<option value="'+ jsonArray.criteriaTypeList[i] +'">'
			+ jsonArray.criteriaTypeList[i] +'</option>');
	});
};

function fillPlanWrapperFormCriteria(jsonArray ,index) {
	$.each(jsonArray.criteriaTypeList, function(i) {
		$('#planWrapperAddUpdateForm #planWrapperCriteriaType_' + index).append('<option value="'+ jsonArray.criteriaTypeList[i] +'">'
			+ jsonArray.criteriaTypeList[i] +'</option>');
	});
};

function resetGoalForm(){
	console.log('count in reset is ' , count);
	this.dataObj = null;
	$('#goalAddUpdateForm #goalName').empty();
	$('#goalAddUpdateForm #goalDepth').empty();
	$('.goalAddUpdateBlock').find('#evalAttrContainerGoal').empty();
}

function resetPlanWrapperForm(){
	console.log('count in reset is ' , count);
	$('#planWrapperAddUpdateForm #planWrapperName').empty();
	$('#planWrapperAddUpdateForm #planWrapperDepth').empty();
	$('.planWrapperAddUpdateBlock').find('#evalAttrContainerPlanWrapper').empty();
}

function showGoalForm(name){
	resetGoalForm();
	if(name === false){
		console.log('fresh new goal');
		$('#goalAddUpdateForm #goalName').val("");
		$('#goalAddUpdateForm #goalDepth').val("");
		$('#goalAddUpdateForm #goalCriteriaPriorety').val("");
		$('#goalAddUpdateForm #goalCriteriaExpression').val("");
		$('#goalFormHead').text('New goal');
		getCompositeAbstracts();
	}
	else {
		$('#goalFormHead').text(name);

		var url = 'viewGoal.action';
		$.ajax({
			url: url,
			type: 'POST',
			data: {
				goalName: name
			},
			beforeSend: getCompositeAbstracts(),
			datatype: 'json',
			success: function(json) {
				if (json) {
					var escaped = json.replace(/'/g, '"');
					var goalObject = $.parseJSON(escaped);
					fillGoalForm(goalObject);
				} else { return false;}
			}
		});
	}
	$('.goalAddUpdateBlock').removeClass('hidden').addClass('visible');
}

function showPlanWrapperForm(planWrapperName){
	resetPlanWrapperForm();
	if(planWrapperName == false){
		$('#planWrapperAddUpdateForm #planWrapperName').val("");
		$('#planWrapperFormHead').text('New plan wrapper');
		getPlanList();
		getCompositeAbstractForWrapper();
	}
	else{
		$('#planWrapperFormHead').text(planWrapperName);
		var url = 'viewPlanWrapper.action';
		$.ajax({
			url: url,
			type: 'POST',
			data: 'planWrapperName='+planWrapperName,
			datatype: 'json',
			success: function(json) {
				if (json) {
					var escaped = json.replace(/'/g, '"');
					var planWrapperObject = $.parseJSON(escaped);
					fillPlanWrapperForm(planWrapperObject);
				} else { return false;}
			}
		});
	}
	$('.planWrapperAddUpdateBlock').removeClass('hidden').addClass('visible');
}

function fillPlanWrapperForm(planWrapper){
	if(planWrapper.name){	
		$('#planWrapperAddUpdateForm #planWrapperName').val(planWrapper.name);
		$('#planWrapperAddUpdateForm #planWrapperPreCondition').val(planWrapper.preCondition);
		$('#planWrapperAddUpdateForm #plans').val(planWrapper.plan);
		
		// TODO:
		$('#planWrapperAddUpdateForm #planWrapperCriteriaType').val(planWrapper.type);
		$('#planWrapperAddUpdateForm #planWrapperCriteriaPriorety').val(planWrapper.priorety);
		$('#planWrapperAddUpdateForm planWrappeCriteriaExpression').val(planWrapper.expression);
		$.each(planWrapper.evaluator, function(i){
			var countForPlanWrapper = i+1;
			var indexPanel = '<select id="planWrapperCriteriaType_' + countForPlanWrapper + '" name="criteriaType_' + countForPlanWrapper + '">' +
					'<option value="'+ this.type +'">'+ this.type +'</option></select>' +
					'<input type="number" name="criteriaPriorety_' + countForPlanWrapper +
					'" id=""planWrapperCriteriaPriorety_' + countForPlanWrapper + '" placeholder="Insert Priorety" value="'+ this.priority +'"/>' +
					'<input type="text" name="criteriaExpression_' + countForPlanWrapper +
					'" id=""planWrapperCriteriaExpression_' + countForPlanWrapper + '" placeholder="Insert Expression" value="'+ this.criteria +'"/>';
			$('.planWrapperAddUpdateBlock').find('#evalAttrContainer').append(indexPanel);
			count ++;
		})
	}	
}


function fillGoalForm(goal){
	if(goal.name){	
		$('#goalAddUpdateForm #goalName').val(goal.name);
		$('#goalAddUpdateForm #goalPreCondition').val(goal.preCondition);
		$('#goalAddUpdateForm #goalPostCondition').val(goal.postCondition);
		$('#goalAddUpdateForm #goalDepth').val(goal.depth);
		$('#goalAddUpdateForm #goalCriteriaType').val(goal.type);
		$('#goalAddUpdateForm #goalCriteriaPriorety').val(goal.priorety);
		$('#goalAddUpdateForm #goalCriteriaExpression').val(goal.expression);
		$('#goalAddUpdateForm #goalIsGoalFinal').attr('checked', goal.primary == "true");
		$.each(goal.evaluator, function(i){
			var count = i+1;
			var indexPanel = '<select id="goalCriteriaType_' + count + '" name="criteriaType_' + count + '">' +
					'<option value="'+ this.type +'">'+ this.type +'</option></select>' +
					'<input type="number" name="criteriaPriorety_' + count +
					'" id="goalCriteriaPriorety_' + count + '" placeholder="Insert Priorety" value="'+ this.priority +'"/>' +
					'<input type="text" name="criteriaExpression_' + count +
					'" id="goalCriteriaExpression_' + count + '" placeholder="Insert Expression" value="'+ this.criteria +'"/>';
			$('.goalAddUpdateBlock').find('#evalAttrContainer').append(indexPanel);
			count ++;
		})
	}	
}
///////////////////////////////////// plans ////////////////////////////////////////////////
function savePlan(planName){
	var planGoalObjArray = $('#planAddUpdateForm select[name="planGoal"]');
	var planGoalArray = [];
	for(var i = 0; i < planGoalObjArray.length; i++){
		var data = {
				'name' 		: planGoalObjArray[i].value,
				'priority'	: i+1
		}
		planGoalArray.push(data);
	}

	
	if(planName == false){
		var dataArray = {
			'name'		:	$('#planAddUpdateForm input[name="name"]').val(),
			'goals'		:	planGoalArray,
		};
	}
	else{
		var dataArray = {
			'planName'	:	planName,
			'name'		:	$('#planAddUpdateForm input[name="name"]').val(),
			'goals'		:	planGoalArray,
		};
	}
	var dataJson= $.toJSON(dataArray);

	////{"name":"name","goals":[ {"name" : "Goal 1" , "priority" : "1"}, {"name" : "Goal 3", "priority" : "2"}]}
	////{"name":"aaa","goals":["hitpawn","hitpawn","hitpawn"]}:
	
	var url = 'savePlan.action';
	$.ajax({
		url: url,
		type: 'POST',
		datatype: 'json',
		data : {
			plan: dataJson
		},
		success: function(data) {
			if(data){
				$('#plansListPanel').initPlans();
				$('.planAddUpdateBlock').removeClass('visible').addClass('hidden');
			}
			else{
				alert('Error! Plan not saved!');
			}
		}
	});
}
// name: nn, precondition:precConc, plan:planName, evaluator...

function savePlanWrapper(planWrapperName){	
	var dataObjArray = [];
	
	if(planWrapperName == false){
		var mainObj = {
				'name'			:	$('#planWrapperAddUpdateForm input[name="name"]').val(),
				'preCondition'	:	$('#planWrapperAddUpdateForm select[name="preCondition"]').val(),
				'plan'          :   $('#planWrapperAddUpdateForm select[name="plans"]').val()
			};
			for(var i = 1; i < countForPlanWrapper ; i++){
				var data = {
				'priority'       :   $('#planWrapperAddUpdateForm input[name="criteriaPriorety_' + i + '"]').val(),
				'type'	         :   $('#planWrapperAddUpdateForm select[name="criteriaType_' + i + '"]').val(),
				'criteria'     :     $('#planWrapperAddUpdateForm input[name="criteriaExpression_' + i + '"]').val(),
				
				};
			dataObjArray.push(data);
			}
		} else {
			var mainObj = {
				'name'					:	planWrapperName,
				'preCondition'			:	$('#goalAddUpdateForm select[name="preCondition"]').val(),
				'plan'          :   $('#planWrapperAddUpdateForm select[name="plans"]').val()
			};
			for(var i = 1 ; i < countForPlanWrapper; i++) {
				var data = {
						'priority': $('#planWrapperAddUpdateForm input[name="criteriaPriorety_' + i + '"]').val(),
						'type': $('#planWrapperAddUpdateForm select[name="criteriaType_' + i + '"]').val(),
						'criteria': $('#planWrapperAddUpdateForm input[name="criteriaExpression_' + i + '"]').val(),	
				};
				dataObjArray.push(data);
			}
		}
	
	countForPlanWrapper = 1;
	var dataArray = $.extend(mainObj, {
		"evaluator": dataObjArray  
	});
	
	
	var dataJson= $.toJSON(dataArray);
	console.log(dataJson);

	
	var url = 'savePlanWrapper.action';
	$.ajax({
		url: url,
		type: 'POST',
		datatype: 'json',
		data : {
			planWrapper: dataJson,
		},
		success: function(data) {
			if(data){
				$('#plansWrapperListPanel').initPlanWrappers();
				$('.planWrapperAddUpdateBlock').removeClass('visible').addClass('hidden');
			}
			else{
				alert('Error! PlanWrapper not saved!');
			}
		}
	});

	
}



function getGoalList(){
	var url = 'initGoal.action';

	$.ajax({
		url: url,
		type: 'POST',
		datatype: 'json',
		success: function(json) {

			if (json) {
				var escaped = json.replace(/'/g, '"');
				var JSONarray = $.parseJSON(escaped);
				$('#planAddUpdateForm #planGoals ul select').empty();
				$('#planAddUpdateForm #planGoals ul select').append('<option value="">Select goal</option>');
				fillPlanFormGoals(JSONarray);
			} else { return false;}
		}
	});
}

function getPlanList() {
	var url = 'initPlan.action';
	
	$.ajax({
		url: url,
		type: 'POST',
		datatype: 'json',
		success: function(json) {
			if (json) {
				var escaped = json.replace(/'/g, '"');
				var JSONarray = $.parseJSON(escaped);
				$('#planWrapperAddUpdateForm #plans ul select').empty();
				$('#planWrapperAddUpdateForm #plans ul select').append('<option value="">Select plan</option>');
				fillPlanWrapperFormPlans(JSONarray);
			} else { return false;}
		}
	});
}

function fillPlanFormGoals(jsonArray) {
	$.each(jsonArray.GoalsList, function(i) {
		$('#planAddUpdateForm #planGoals ul select').append('<option value="'+jsonArray.GoalsList[i].name+'">'
			+jsonArray.GoalsList[i].name+'</option>');
	});
}

function fillPlanWrapperFormPlans(jsonArray) {
	console.log('json for plan wrapper',jsonArray);
	$.each(jsonArray.PlansList, function(i) {
		$('#planWrapperAddUpdateForm #plans ul select').append('<option value="'+jsonArray.PlansList[i].name+'">'
			+jsonArray.PlansList[i].name+'</option>');
	});
}

function showPlanForm(planName){
	if(planName == false){
		$('#planAddUpdateForm #planName').val("");
		$('#planFormHead').text('New plan');
		getGoalList();
	}
	else{
		$('#planFormHead').text(planName);
		var url = 'viewPlan.action';
		$.ajax({
			url: url,
			type: 'POST',
			data: 'planName='+planName,
			beforeSend: getGoalList(),
			datatype: 'json',
			success: function(json) {
				if (json) {
					var escaped = json.replace(/'/g, '"');
					var planObject = $.parseJSON(escaped);
					fillPlanForm(planObject); 
				} else { return false;}
			}
		});
	}
	$('.planAddUpdateBlock').removeClass('hidden').addClass('visible');
}

function fillPlanForm(plan){
	$('#planAddUpdateForm #planName').val(plan.name);
	var firstLi = $('#planAddUpdateForm #planGoals ul li:first').clone();
	$('#planAddUpdateForm #planGoals ul').empty();
	for(i = 0; i < plan.goals.length; i++){
		if(i == 0){
			$('#planAddUpdateForm #planGoals ul').append(firstLi);
			$('#planAddUpdateForm #planGoals ul li:last select option[value="'+plan.goals[i].name+'"]').attr("selected","selected");
		}
		else{
			$('#planAddUpdateForm #planGoals ul').append($('#planAddUpdateForm #planGoals ul li:first').clone());
			$('#planAddUpdateForm #planGoals ul li:last select option[value="'+plan.goals[i].name+'"]').attr("selected","selected");
		}
	}
}
function removeGoal(goalName){
	if(confirm("Are you sure to delete this goal: "+ goalName +"?")){
		var url = 'removeGoal.action';
		$.ajax({
			url: url,
			type: 'POST',
			datatype: 'json',
			data : {
				goalName: goalName
			},
			success: function(data) {
				if (data) {
					$('#goalsListPanel').initGoals();
				} else {
					alert("Error! Goal not deleted");
				}
			}
		});
	}
}
function removePlan(name){
	if(confirm("Are you sure to delete this plan: "+ name +"?")){
		var url = 'removePlan.action';
		$.ajax({
			url: url,
			type: 'POST',
			datatype: 'json',
			data : {
				planName: name
			},
			success: function(data) {
				if (data) {
					$('#plansListPanel').initPlans();
				}
				else {
					alert("Error! Plan not deleted");
				}
			}
		});
	}
}

function removePlanWrapper(name){
	if(confirm("Are you sure to delete this plan: "+ name +"?")){
		var url = 'removePlanWrapper.action';
		$.ajax({
			url: url,
			type: 'POST',
			datatype: 'json',
			data : {
				planWrapperName: name
			},
			success: function(data) {
				if (data) {
					$('#plansWrapperListPanel').initPlanWrappers();
				}
				else {
					alert("Error! PlanWrapper not deleted");
				}
			}
		});
	}
}

/* Generic Plans Functions From Vanand */
$.fn.initPlans = function(){
	$(this).empty();
	if( $(this).length != 0){
		var url = 'initPlan.action';
		var that = this; 
		$.ajax({
			url: url,
			type: 'POST',
			datatype: 'json',
			success: function(json) {
				if (json) {
					var escaped = json.replace(/'/g, '"');
					var JSONarray = $.parseJSON(escaped);
					$(that).fillPlansList(JSONarray)
				}
			},
			error: function(){
				return false
			}
		});
	} else {
		console.info('Selector not exists');
		return false;
	}
}
$.fn.fillPlansList = function(planList){
	that = this;
	switch($(this).get(0).nodeName.toLowerCase()){
	case 'select': 
		var options = '' 
		$.each( planList.PlansList , function(){
			options += '<option value="'+this.name+'">'+this.name+'</option>';
		} )
		$(that).append(options);
		break;
	case 'div':
		var options = '' 
		$.each( planList.PlansList , function(){
			options += '<div class="treeNameInTable treeNameInTableStyle"><div class="PlanTab">'+this.name+'</div><span class="planRemoveBtn" >x</span></div>';
		} )
		$(that).append(options);
		break
	}
}

/* Generic Goals Functions From Vanand */
$.fn.initGoals = function(){
	console.log('init goal function');

	$(this).empty();
	if( $(this).length != 0){
		var url = 'initGoal.action';
		var that = this; 
		$.ajax({
			url: url,
			type: 'POST',
			datatype: 'json',
			success: function(json) {
				if (json) {
					var escaped = json.replace(/'/g, '"');
					var JSONarray = $.parseJSON(escaped);
					$(that).fillGoalsList(JSONarray)
				}
			},
			error: function(){
				return false
			}
		});
	} else {
		console.info('Selector not exists');
		return false;
	}
}
$.fn.fillGoalsList = function(goalList){
	that = this;
	switch($(this).get(0).nodeName.toLowerCase()){
	case 'select': 
		var options = '' 
		$.each( goalList.GoalsList , function(){
			options += '<option value="'+this.name+'">'+this.name+'</option>';
		} )
		$(that).append(options);
		break;
	case 'div':
		var options = '' 
		$.each( goalList.GoalsList , function(){
			options += '<div class="treeNameInTable treeNameInTableStyle"><div class="GoalTab">'+this.name+'</div><span class="goalRemoveBtn" >x</span></div>';
		} )
		$(that).append(options);
		break
	}
}


/* Generic Plan Wrappers Functions From Vanand/Narek */

$.fn.initPlanWrappers = function(){
	console.log('init planWrapper function');
	$(this).empty();
	if( $(this).length != 0){
		var url = 'initPlanWrapper.action';
		var that = this; 
		$.ajax({
			url: url,
			type: 'POST',
			datatype: 'json',
			success: function(json) {
				console.log(json);
				if (json) {
					var escaped = json.replace(/'/g, '"');
					var JSONarray = $.parseJSON(escaped);
					$(that).fillPlanWrappersList(JSONarray);
				}
			},
			error: function(){
				return false;
			}
		});
	} else {
		console.info('Selector not exists');
		return false;
	}
}
$.fn.fillPlanWrappersList = function(planWrapperList){
	that = this;
	switch($(this).get(0).nodeName.toLowerCase()){
	case 'select': 
		var options = '' ;
		$.each( planWrapperList.PlanWrappersList , function(){
			options += '<option value="'+this.name+'">'+this.name+'</option>';
		} );
		$(that).append(options);
		break;
	case 'div':
		var options = '' ;
		$.each( planWrapperList.PlanWrappersList , function(){
			options += '<div class="treeNameInTable treeNameInTableStyle"><div class="PlanWrapperTab">'+this.name+'</div><span class="planWrapperRemoveBtn" >x</span></div>';
		} );
		$(that).append(options);
		break;
	}
}




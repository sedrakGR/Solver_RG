var messageIsNotShown = true;

function initChess() {
    $("#board > div").attr('data-color', 0);
    $("#board > div").attr('data-figure', 0);

    $("#figures > div").draggable({ revert: true, addClasses: false,helper: 'clone' });
    
    /*
     * Removing attributes from board by doubleclicking.
     */
    $("#board > div").dblclick(function(){
    	 var cell = $(this);
    	 cell.attr('data-color', 0);
         cell.attr('data-figure', 0);
    });
    
     
    
    $("#board > div").droppable({ accept: "#figures > div", addClasses: false, drop: function (event, ui) {
    	if (messageIsNotShown) {
    		showMessage();
    	}
        var figure = ui.draggable;
        var cell = $(this);
        
            cell.attr('data-color', figure.data("color"));
            cell.attr('data-figure', figure.data("figure"));   
            
       /* cell.append($(ui.helper).clone().draggable({ 
        	revert: false, addClasses: false, drag: function (event, ui) {
            	var figure = $(this);
            	figure.removeAttr('data-cell');
            	}
            })
        );*/    

        $("#actives").empty();
    	$("#actives").hide();
    	$("#send-meaning").hide();
    	$("#board > div.highlighted").removeClass("highlighted");
    }
    });
        
    $("#actives").change(function () {
    });
    $("#planProcess").live('click',function(){
    	SendBoardState('plan');
    })
    
      $("#processBestPlan").live('click',function(){
    	SendBoardState('bestPlan');
    })
    
    $("#nextSuggestion").live('click',function(){
    	nextSuggestion();
    })
}

function SendBoardState(action) {
    var elements = [];
    $("#board > div").each(function (idx, elem) {
        cell = $(elem);
		
        var instances = [];
		
        instances.push({
            'type': "cordX",
            'value': (idx % 8) + 1
        });

        instances.push({
            'type': "cordY",
            'value': 8 - Math.floor(idx / 8)
        });

        instances.push({
            'type': "FigureType",
            'value': cell.attr("data-figure")
        });

        instances.push({
            'type': "FigureColor",
            'value': cell.attr("data-color")
        });
    
        elements.push({
            'groupid': idx+1,
            'instances': instances
        });
    });

    var object = {
		'name': "",
        'elements': elements
    };

	var json = $.toJSON(object);

	var url = 'saveSituation.action';
	$.ajax({
		url: url,
		type: 'POST',
		datatype: 'json',
		data: {
			situationString: json
		},
		success: function(data){
			if( action == 'situation'){
				processSituation();				
			} else if(action == 'bestPlan') {
				console.log('kuagas hasnis')
				findBestPlan();				
			}else{
				processPlan();
			}			
		}
	});
}

function processSituation() {
	var url = 'processSituation.action';
	$.ajax({
		url: url,
		type: 'POST',
		datatype: 'json',
		data: {
			'situationName': ""
		},
		success: function(json){
			var escaped = json.trim().replace(/'/g, '"');
			var meaning = $.parseJSON(escaped);
			initActivatedMeanings(meaning);
		}
	});
}

function initActivatedMeanings(activeAbstracts) {
    $("#actives").show();
    $("#actives").empty();
	$.each(activeAbstracts.actives, function(j) {
		var active = activeAbstracts.actives[j];
		$("#actives").append("<option value='" + active.name + "'>" + active.name + "</option>");
    });
    $("#send-meaning").show();
}

function SendMeaning() {
    var meaning = $("#actives option:selected").text();
    $("#board > div.highlighted").removeClass("highlighted");

	var url = 'nextActiveInstance.action';
	$.ajax({
		url: url,
		type: 'POST',
		datatype: 'json',
		data: {
			'abstractName': meaning
		},
		success: function(json){
			var escaped = json.trim().replace(/'/g, '"');
			var instance = $.parseJSON(escaped);
			showActiveInstance(instance);
		}
	});
}

function showActiveInstance(instance) {
	$.each(instance.groupids, function(j) {
		var groupid = instance.groupids[j].id - 1;
		$("#board > div").eq(groupid).addClass("highlighted");
    });
}


function showMessage() {
  $("#message").show();
  messageIsNotShown = false;
  setInterval(function() {closeMessage();},5000);
  }
 

function closeMessage() {
	$("#message").hide();
}
function processPlan(){
	var processData = {};
	if($("#plansList").val()){
		processData = {
			'planName'  	: $("#plansList").val(),
			'side'			: $("#sideList").val(),
			'situationName'	: ""
		}
		var url = 'processPlan.action';
		$.ajax({
			url: url,
			type: 'POST',
			datatype: 'json',
			data: processData,
			success: function(json){
				$("#nextSuggestion").attr('disabled', false);
			}
		});
	} else{
		return false
	}
	
}

function findBestPlan(){
	var processData = {};
		processData = {
			'side'			: $("#sideList").val(),
			'situationName'	: ""
		}
		var url = 'selectBestPlan.action';
		$.ajax({
			url: url,
			type: 'POST',
			datatype: 'json',
			data: processData,
			success: function(json){
				$("#nextSuggestion").attr('disabled', false);
			}
		});	
}

function nextSuggestion(){
	
	$("#board > div.highlighted").removeClass("highlighted");
	
	var url = 'nextSuggestedAction.action';
	$.ajax({
		url: url,
		type: 'POST',
		datatype: 'json',
		success: function(json){
			var escaped = json.trim().replace(/'/g, '"');
			var instance = $.parseJSON(escaped);
			showActiveInstance(instance);
		}
	});
}
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

    // If this run came from an image upload, close the loop in its status line.
    if (window._imgSitStatusPending) {
        var st = document.getElementById('imgSitStatus');
        if (st) {
            var n = (activeAbstracts.actives || []).length;
            st.textContent = window._imgSitStatusPending + ' ' + n + ' active abstract'
                + (n === 1 ? '' : 's') + ' found - pick one and press "Show Next Instance".';
        }
        window._imgSitStatusPending = null;
    }
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
// =====================================================================
// Image-to-Situation upload (Phase D MVP).
// Sends the image to createSituationFromImage.action as base64; the Java
// bridge shells out to Solver_train/predict_board.py and registers the
// resulting Situation in the existing SituationManager library.
// =====================================================================
// NOTE: the bundled jQuery is 1.5.2 — $(document).on() only exists from 1.7,
// so we use the legacy .live() delegation like the rest of this codebase.
$('#imgSitGo').live('click', function () {
	var fileInput = document.getElementById('imgSitFile');
	var nameInput = document.getElementById('imgSitName');
	var status    = document.getElementById('imgSitStatus');
	var result    = document.getElementById('imgSitResult');
	if (!fileInput || !fileInput.files || !fileInput.files[0]) {
		alert('Pick an image file first.');
		return;
	}
	var file = fileInput.files[0];
	status.textContent = 'reading ' + file.name + ' (' + Math.round(file.size / 1024) + ' KB)...';
	result.style.display = 'none';
	result.textContent = '';

	var reader = new FileReader();
	reader.onload = function (e) {
		// FileReader produces a data URL like 'data:image/png;base64,XXXX'
		var b64 = String(e.target.result || '');
		status.textContent = 'running classifiers (this is CPU inference; expect ~10-20s the first call)...';

		$.ajax({
			url:  'createSituationFromImage.action',
			type: 'POST',
			dataType: 'json',
			data: {
				imageBase64: b64,
				sitName:     (nameInput && nameInput.value) || ''
			},
			success: function (resp) {
				var payload = (resp && resp.json) || resp || {};
				if (payload.error) {
					status.textContent = 'Error: ' + payload.error;
					status.style.color = '#a40000';
					result.style.display = 'block';
					result.textContent = JSON.stringify(payload, null, 2);
					return;
				}
				status.style.color = '#0a3b6e';
				status.textContent = 'Situation "' + payload.name + '" created - '
					+ (payload.cellCount || 0) + ' cells. Drawing on the board...';
				result.style.display = 'block';
				result.textContent = 'name: ' + payload.name + '\n'
					+ 'cells: ' + (payload.cellCount || 0) + '\n'
					+ 'fen:  ' + (payload.fen || '(unavailable)')
					+ (payload.fenError ? '\nfenError: ' + payload.fenError : '');

				// Draw the recognized position onto the chess board, then run the
				// meaning matching against it so the actives list (and "Show Next
				// Instance") work exactly as they do for a hand-placed board.
				if (payload.fen) {
					renderFenOnBoard(payload.fen);
					status.textContent = 'Situation "' + payload.name + '" created - '
						+ (payload.cellCount || 0) + ' cells, drawn on the board. '
						+ 'Finding active abstracts...';
					// processSituation() is async; initActivatedMeanings() reports
					// the outcome through this flag once the matching returns.
					window._imgSitStatusPending = 'Situation "' + payload.name + '" created - '
						+ (payload.cellCount || 0) + ' cells, drawn on the board.';
					processSituation();
				}
			},
			error: function (xhr) {
				status.style.color = '#a40000';
				status.textContent = 'HTTP ' + xhr.status + ': ' + xhr.statusText;
				result.style.display = 'block';
				result.textContent = xhr.responseText || '';
			}
		});
	};
	reader.onerror = function () {
		status.style.color = '#a40000';
		status.textContent = 'Could not read the file.';
	};
	reader.readAsDataURL(file);
});


// =====================================================================
// Draw a FEN placement string onto the chess board.
//
// Board cells are row-major starting at a8, which is also the order the
// image bridge assigns IdGroup ids (1..64) - so a highlighted instance
// returned by nextActiveInstance.action lines up with the cell drawn here.
//
// Figure codes follow the Solver's chess convention (2016 User Guide
// App. B and the sprites in ChessSituation.css):
//   1 Pawn, 2 Bishop, 3 Knight, 4 Rook, 5 Queen, 6 King; 0 = empty.
// =====================================================================
function renderFenOnBoard(fen) {
	var FIGURE_OF_LETTER = { p: 1, b: 2, n: 3, r: 4, q: 5, k: 6 };
	var cells = $('#board > div');

	// Clear the board first: any square the FEN does not mention stays empty.
	cells.attr('data-figure', 0).attr('data-color', 0);
	$('#board > div.highlighted').removeClass('highlighted');

	var placement = String(fen).split(' ')[0];   // ignore side/castling if present
	var ranks = placement.split('/');
	for (var r = 0; r < ranks.length && r < 8; ++r) {
		var file = 0;
		for (var i = 0; i < ranks[r].length && file < 8; ++i) {
			var ch = ranks[r].charAt(i);
			if (ch >= '1' && ch <= '8') {          // run of empty squares
				file += parseInt(ch, 10);
				continue;
			}
			var figure = FIGURE_OF_LETTER[ch.toLowerCase()];
			if (figure) {
				// Uppercase = white (1), lowercase = black (2).
				var color = (ch === ch.toUpperCase()) ? 1 : 2;
				cells.eq(r * 8 + file).attr('data-figure', figure).attr('data-color', color);
			}
			++file;
		}
	}
}

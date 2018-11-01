(function () {

	var macroName = 'issue-import-macro';
	console.log("issueImport macro init")

	var updateMacro = function () {


		// Standard sizes are 400, 600, 800 and 960 pixels wide
		var dialog = new AJS.Dialog({
			width: 400,
			height: 400,
			id: "example-dialog",
			closeOnOutsideClick: true
		});


		dialog.addPanel("Panel 1", "<h4>Paste here your jsonArray from Jira, existing issues from this page will be overwritten</h4><br>" +
			"<textarea rows='4' cols='40' class='jsonPasteTextArea'></textarea>", "panel-body");


		dialog.addLink("Cancel", function (dialog) {
			dialog.hide();
		}, "#");

		dialog.addHeader("Dialog");

		dialog.addButton("ok", function (dialog) {
			console.log("closing Dialog");
			//get all textareas
			var allTextAreas = $(".jsonPasteTextArea");
			var userInput = $((allTextAreas)[allTextAreas.length - 1]).val();

			try {
				var userObject = JSON.parse(userInput);
			} catch (e) {
				showFlag("error", "Error parsing your input." + e);
			}
			var pageId = parseInt(AJS.params.pageId);
			userObject["pageId"]=pageId;
			postIssueArray(userObject, function (some) {
				console.log("here", some)
			});


			dialog.hide();
		});
		dialog.show()

	};

	function postIssueArray(jsonArray, callback) {
		postJSON(AJS.Data.get('context-path') + "/rest/jsonIssues/1.0/issueRest/add-issue-array", jsonArray,
			function (error, result) {
				if (error === null) {
					callback(result);
					showFlag("success", "Json Issues updated");
				} else {
					showFlag("error", "An Server Error occured." + error);
				}
			});
	}

	function postJSON(url, data, callback) {
		var xhr = new XMLHttpRequest();
		xhr.open("POST", url, true);
		xhr.setRequestHeader("Content-type", "application/json; charset=utf-8");
		xhr.setRequestHeader("Accept", "application/json");
		xhr.responseType = "json";
		xhr.onload = function () {
			var status = xhr.status;
			if (status === 200) {
				callback(null, xhr.response);
			} else {
				callback(status);
			}
		};
		xhr.send(JSON.stringify(data));
	}

	function getJSON(url, callback) {
		var xhr = new XMLHttpRequest();
		xhr.open("GET", url, true);
		xhr.setRequestHeader("Content-type", "application/json; charset=utf-8");
		xhr.responseType = "json";
		xhr.onload = function () {
			var status = xhr.status;
			if (status === 200) {
				callback(null, xhr.response);
			} else {
				callback(status);
			}
		};
		xhr.send();
	}


	function showFlag(type, message) {
		AJS.flag({
			type: type,
			close: "auto",
			title: type.charAt(0).toUpperCase() + type.slice(1),
			body: message
		});
	}


	AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("updateButton", function (e, macroNode) {
		console.log("myMacro")
		updateMacro();
	});


})();


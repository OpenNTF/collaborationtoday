dojo.require("dojo.io.script");

function load() {
	var height;
	var width;
	var count;
	var filter;
	if (typeof collab2dayHeight == "undefined") {
		height = 400;
	}
	else {
		height = collab2dayHeight;
	}
	if (typeof collab2dayWidth == "undefined") {
		width = 200;
	}
	else {
		width = collab2dayWidth;
	}	
	if (typeof collab2dayCount == "undefined") {
		count = 10;
	}
	else {
		count = collab2dayCount;
	}	
	if (typeof collab2dayFilter == "undefined") {
		filter = "all";
	}
	else {
		filter = collab2dayFilter;
	}	
	
	var xSnippetsNode = dojo.byId("collab2day");
	var xSnippetsJSNode = dojo.byId("xsnippetsJS");	
	var urlBase;
	if (xSnippetsNode) {
		if (!xSnippetsJSNode) {
			//urlBase = "http://nheidloff-1/ct.nsf/"			
			urlBase = "http://collaborationtoday.info/"
		}
		else {
			urlBase = xSnippetsJSNode.getAttribute('src');
			urlBase = urlBase.substring(0, urlBase.indexOf(".nsf") + 4) + "/";
		}
		
		var outerDiv = document.createElement('div');
		outerDiv.setAttribute('style','height:' + height + 'px; width:' + width + 'px;');
		outerDiv.setAttribute('class','outerDiv');
		xSnippetsNode.appendChild(outerDiv);
		
		var innerDiv = document.createElement('div');
		innerDiv.setAttribute('class','innerDiv');
		outerDiv.appendChild(innerDiv);
		
		var link = document.createElement('a');
		link.setAttribute('href','http://openntf.org/news.nsf/home.xsp');
		innerDiv.appendChild(link);
		
		var imgDiv = document.createElement('img');
		imgDiv.setAttribute('style', 'background:black; margin-bottom: 10px;border:white;');
		imgDiv.setAttribute('src', urlBase + 'collab2day.png');
		link.appendChild(imgDiv);
		
		var xSnippetsEntriesNode = document.createElement('div');
		xSnippetsEntriesNode.setAttribute('id', 'newsEntries');
		xSnippetsEntriesNode.setAttribute('class','newsEntries');		
		innerDiv.appendChild(xSnippetsEntriesNode);
			            
		var jsonpArgs = {
			url: urlBase + "ct.nsf/api.xsp?count=" + count + "&filter=" + filter + "&format=jsonp",		
		    callbackParamName: "json",
		    load: function(data) {
		        var results = data.responseData.results;
				
		        var displayedHTML = "";	        
				if (results != null) {
					for (var i = 0; i < results.length; i++) {	                    	                	
						displayedHTML = displayedHTML + "<div class='newsEntry'><a href='" + results[i].link + "' target='_blank'>" + decodeURIComponent(results[i].title) + "</a>" + "<div class='author'>" + results[i].person_display_name + "</div></div>";
					}
				}                    
				xSnippetsEntriesNode.innerHTML = displayedHTML;  
				for (var i = xSnippetsEntriesNode.children.length - 1; i >= 0; i--) {
					var snippet = xSnippetsEntriesNode.children[i];
					if (height < snippet.offsetTop + snippet.offsetHeight) {
						xSnippetsEntriesNode.removeChild(snippet);
					}					
				}	
				for (var i = xSnippetsEntriesNode.children.length - 1; i >= 0; i--) {
					var snippet = xSnippetsEntriesNode.children[i];
					if (height < snippet.offsetTop + snippet.offsetHeight) {
						xSnippetsEntriesNode.removeChild(snippet);
					}					
				}				
			},
		    
			error: function(error) {
				xSnippetsEntriesNode.innerHTML = "An unexpected error occurred: " + error;
			}
		};
		
		dojo.io.script.get(jsonpArgs);
	}
}

dojo.addOnLoad(load);
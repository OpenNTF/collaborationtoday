var mpObj = null;

function micropublisher(wallUserID, streamDivID) {
	this.wallUserID = wallUserID;
	this.streamDivID = streamDivID;
	this.mpType = 'Status';
	
	this.mpBox = dojo.byId("shareBox");
	this.mpLink = '';
	this.upl = null;
	
	this.show = function( mpType ) {
		if (mpType) this.mpType = mpType;
		dojo.style("blockscreen", { display: "block"});
		var vp=dojo.window.getBox();
		var pLeft = (vp.w / 2) - 600 / 2; // center
		dojo.style(this.mpBox, { display: "block", left: pLeft+"px", top: (vp.t + 50)+"px" });
		
	}
		
	this.hide = function() {
		dojo.byId("shareBoxButtons").innerHTML="";
		dojo.byId("shareBoxForm").innerHTML="";
		dojo.style("shareBox", { display: "none" });
		dojo.style("blockscreen", { display: "none"});
		upl = null; // kill uploader
	}
	
	this.switchMicroPublisherType = function(mpType) {
		this.mpType = mpType;
		var xhrArgs = {
			    url: "micropublisher-form-" + mpType + ".html?OpenPage",
			    handleAs: "text",
			    preventCache: true,
			    load: function(data){ dojo.byId("shareBoxForm").innerHTML=data; },
			    error: function(error){ dojo.byId("shareBoxForm").innerHTML = "Error: " + error; }
		}
		var deferred = dojo.xhrGet(xhrArgs);
		
		var xhrArgs = {
			    url: "micropublisher-buttons-" + mpType + ".html?OpenPage",
			    handleAs: "text",
			    preventCache: true,
			    load: function(data){ dojo.byId("shareBoxButtons").innerHTML=data; },
			    error: function(error){ dojo.byId("shareBoxButtons").innerHTML = "Error: " + error; }
		}
		var deferred = dojo.xhrGet(xhrArgs);
	}
	
	this.submit = function(mpType) {
		var audience = dojo.byId('mwgAudience').value;
		// check for images to upload
		if (this.upl) {
			if (this.upl.uploadQueue.length>0) {
				this.mpUploadPhotos(mpType);
				return false;
			}		
		}
		var txtArea=dojo.byId('shareTextArea');
		var postData='';
		
		if (mpType=="Status") {
			if (txtArea.value=="") return false;
			var link = encodeURIComponent(dojo.byId('shareURL').value);
			var linkObj="";
			if (link!="") {
				linkObj = {		
						"url": link,
						"title": g_mpLinkTitle,
						"desc": g_mpLinkDesc,
						"thumb": (g_mpLinkCurrentThumb==0?"":g_mpLinkThumbs[g_mpLinkCurrentThumb])
				}
			}
			postData = {"method": "activitystreams.create",
					"objectType": "Status",
					"audience": audience, 
					"link": linkObj,
					"content": encodeURIComponent(txtArea.value)
			};
		}	
		// submit to server
		var xhrArgs = {
		    url: "Services.xsp/activitystreams/"+this.wallUserID,
		    postData: dojo.toJson(postData),
		    handleAs: "json",
		    headers: { "Content-Type": "application/json"},
		    load: (function(m) {
		    	return function(data) {
		    		if (!data.result) return; 
		    		if (data.result!="OK") {
		    			alert("Posting has failed: " + data.result);
		    			return;
		    		}
		        	m.hide();
		        	fetchStream(m.wallUserID, m.streamDivID);
		    	}
		    })(this),
		    error: function(error) { alert("Error :" + error); }
		};
		dojo.xhrPost(xhrArgs);
		return false;		
	}
	
	this.mpSwitchType = function(e) { // micropublisher type switcher
		var mpType = e.id.replace("share", "");
		
		if (mpType!="Status") return; // only status is active right now
		
		dojo.query("#shareBoxNavWrapper ul li").removeClass("selected");
	    dojo.addClass( e, "selected");    
	    this.switchMicroPublisherType(mpType);
	}
	
	this.showPhotoUploader = function() {
		dojo.style("shareStatusAttachActions", { display: "none" });
		dojo.style("shareAttachPhoto", { display: "block"});
		
		if (this.upl==null) {
			this.upl = new flxUploader("./Services.xsp/photo/Upload", 2097152 , "jpeg,jpg,png,gif");
			//upl.afterFileDragDrop = function() {}
			//upl.afterAllUploadsFinished = function() {}
		}
		  
		if (this.upl.isHTML5ready) {  
			dojo.style("html4uploader", { display: "none"});
			dojo.style("html5uploader", { display: ""});
		}		
	}
	this.mpUploadPhotos = function(mpType) {
		this.upl.requestMetaData = {
				  "method":"photo.upload",  
				  "publish": true,
				  "target": this.wallUserID,
				  "title": dojo.byId('shareTextArea').value,
				  "filecount": this.upl.uploadQueue.length,
				  "uploadgroup": this.upl.getTimeStamp()
		};

		this.upl.serialUploadNow(); // this will upload photos, one by one..  --> attention: uploader is no longer serial
		this.waitForMPPhotoUploadsToComplete();
	}
	this.waitForMPPhotoUploadsToComplete = function() {
		if (console) console.log("Waiting for uploads to complete...");
		if (this.upl.uploadsInProgress > 0 ) { // wait..
			document.getElementById('flx-thumb-preview').innerHTML = '<img src="loader1.gif" border=0>&nbsp; Please wait.. ' + this.upl.uploadsInProgress + " file(s) in queue to upload...";
			setTimeout("mpObj.waitForMPPhotoUploadsToComplete()", 1000); 
		} else { // done
			if (console) console.log("All uploads completed. Reloading Activity Stream.");
			this.upl.clearUploadQueue();
			this.hide();
			fetchStream(this.wallUserID, this.streamDivID);
		}	
	}
}

// -- URL parsing
var g_linkCatchRegExp = new RegExp(  "(((http|HTTP|https|HTTPS)://)[-a-zA-Z0-9@:%_\+.~#?&//=]+)[\n,\r, ]+"  ); // detects space or CRLF
var g_linkCatchRegExpNoCRLF = new RegExp(  "(((http|HTTP|https|HTTPS)://)[-a-zA-Z0-9@:%_\+.~#?&//=]+)"  );
var g_mpLinkStatus = 0; // 0=empty; 1=querying; ;
var g_mpLink='';
var g_mpLinkTitle;
var g_mpLinkDesc;
var g_mpLinkThumbs;
var g_mpLinkCurrentThumb=0;

function mpURLCheck(chkSpace) {
	if (g_mpLinkStatus==1) return; // already in progress
	var httplink;
	if (chkSpace==true) {
		httplink = g_linkCatchRegExp.exec( dojo.byId("shareTextArea").value );
	} else {
		httplink = g_linkCatchRegExpNoCRLF.exec( dojo.byId("shareTextArea").value );
	}
	
	httplink = dojo.byId('hiddenLink').value;
	if (httplink) {
		var link=dojo.byId('hiddenLink').value;
		dojo.byId('shareURL').value = dojo.byId('hiddenLink').value;
		// show link field
		dojo.style("shareStatusAttachActions", { display: "none" });
		dojo.style("shareAttachLink", { display: "block"});
		if (link!=g_mpLink) mpFetchLinkData( link );
	}
}

function mpFetchLinkData( url ) {
	g_mpLink = url;
	g_mpLinkCurrentThumb=0; // reset
	var postData={ "method" : "url.fetchmetadata", "link": url };
	var xhrArgs = { // submit to server
	    url: "Services.xsp/micropublisher/fetchLink",
	    postData: dojo.toJson(postData),
	    handleAs: "json",
	    headers: { "Content-Type": "application/json"},
	    load: function(data) {
    		dojo.style("shareURLwait", {display: "none"} );
	    	if (data.result=="" || !data.result) { // no data received for this url
	    		g_mpLinkTitle='';
	    		g_mpLinkDesc=''; 
	    	} else {
	    		g_mpLinkTitle=data.title;
	    		g_mpLinkDesc=data.desc;
	    		dojo.style("shareURLmeta", {display: ""} );
	    		// use decodeURIComponent(data.desc) ??
	    	
	    		if (data.images.length > 1) { // length = 1 when "no image"
	    			g_mpLinkThumbs = data.images;
	    			dojo.style('shareURLThumb',{ display: ""} );
	    			mpLinkThumbScroll(1); // load first image
	    		}
	    	}
	    },
	    error: function(error) { 
    			dojo.style("shareURLwait", {display: "none"} );
    			//alert("Unable to parse link: " + error); 
	    	}
	};
	dojo.xhrPost(xhrArgs);	
}

var lastdirection=1;
function mpLinkThumbScroll(direction) {
	//debugger;
	lastdirection = direction; // we use this when onload of image fails, to fetch next image in list..
	direction==1?g_mpLinkCurrentThumb++:g_mpLinkCurrentThumb--;
	if (g_mpLinkCurrentThumb>g_mpLinkThumbs.length-1) g_mpLinkCurrentThumb=0;
	if (g_mpLinkCurrentThumb<0) g_mpLinkCurrentThumb=g_mpLinkThumbs.length-1;
	dojo.byId('mpLinkThumbPreview').innerHTML= g_mpLinkCurrentThumb==0?'No image':'<img onerror="mpLinkThumbScroll(lastdirection)"src="' + g_mpLinkThumbs[g_mpLinkCurrentThumb] + '" width=180 height=180>';
	dojo.byId('mpLinkThumbCount').innerHTML= '(' + g_mpLinkCurrentThumb +'/'+(g_mpLinkThumbs.length-1)+') ';
	//debugger;
	//var sel = g_mpLinkThumbs[g_mpLinkCurrentThumb];
	//dojo.byId('hiddenSelect').value = g_mpLinkThumbs[g_mpLinkCurrentThumb];
	//var ojdaf = "";
}

function getCurrentThumbUrl() {
	
	return g_mpLinkThumbs[g_mpLinkCurrentThumb];
}

function mpPasteStatusField() {
	//setTimeout("mpURLCheck(false)", 500); // check for links in text (without space at the end or CRLF)
}
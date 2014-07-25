/*****************************************************
*	image-view.js:
*		javascript that complements image-view.html
******************************************************/

var user = -1
var imgSrc = -1
var uploader = -1

//iniates page, grabs image, tags and comments, then updates view count
function loadFunction(){
	validateLogin();
	getImg();
	getTags();
	getUploader()
	getComments();
	updateViewCount();
	
}

//checks to see if current user is the uploader. If they aren't it disables tagging and the delete button 
function isUploader(){
	var div = document.getElementById('tagger-div');
	var div2 = document.getElementById('delete-div');
	if(user == uploader){
		div.style.display = 'block';
		div2.style.display = 'block';
	}
}

//If the user is not signed in, disables commenting
function isNotGuest(){
	var div1 = document.getElementById('commentBox-div');
	var div2 = document.getElementById('commentBox-div2');
	var div3 = document.getElementById('commentBox-div3');
	div1.style.display = 'block';
	div2.style.display = 'block';
	div3.style.display = 'block';

}

//retrieves image from "images" folder, limits the size of the image to 800x800 px
function getImg(){
	var url1 = window.location.search.substring(1);
	var args = url1.split("=");
	imgSrc = args[1];
	var div = document.getElementById('photoDiv');
	div.innerHTML = "<img src=images/" + imgSrc + ".PNG style=\"max-height:800px; max-width:800px\">";
}

// populates the dropdown for tags
function initializeDropdown(divId, xmlFileName){
	var url = "servlet/child-names/" + xmlFileName
	var req = new XMLHttpRequest()
	req.open("GET", url, true)
	req.onreadystatechange = function() {
		if ( req.readyState == 4) {	
			var d = req.responseText.trim()
			if(d != ""){
				d = d.split("/")
				var select = document.getElementById(divId + '-select')
				for(var i = 0; i < d.length; i++){
					select.innerHTML += "<option>" + d[i]
				}
			}
		}
	}							
	req.send(null)
}	

//calls the img-tags servlet to retrieve tags for current image
function getTags(){
	var url = "servlet/child/img-tags/i" + imgSrc.split(".")[0]
	var div = document.getElementById("tag-div")
	div.innerHTML = "Tags: "
	var req = new XMLHttpRequest()
	req.open("GET", url, true)
	req.onreadystatechange = function() {
		if ( req.readyState == 4) {	
			var d = req.responseXML.documentElement
			var e = d.childNodes;
			e = e[0].textContent.split("/")
			for(var i = 1; i < e.length; i++){
				if(i == e.length - 1){
					div.innerHTML += e[i]
				}
				else{							
					div.innerHTML += e[i] + ", "
				}
			}
			
		}
	}						
	req.send(null) 
}

//calls image-uploader servlet to retrieve uploader name
function getUploader(){
	var url = "servlet/child/img-uploader/i" + imgSrc.split(".")[0]
	var div = document.getElementById("uploader-div")
	div.innerHTML = "Uploader: "
	var req = new XMLHttpRequest()
	req.open("GET", url, true)
	req.onreadystatechange = function() {
		if ( req.readyState == 4) {	
			var d = req.responseXML.documentElement
			var e = d.childNodes;
			e = e[0].textContent.trim()
			if(e != ""){
				uploader = e;
				div.innerHTML += e;
				isUploader();
			}
		}					
	}						
	req.send(null) 
}

	
//displays notice to user as to whether or not they are logged in 
function validateLogin(){
	user = getCookie("user")
	var div = document.getElementById('loggedInAs')
	if(user != -1){
		div.innerHTML += "<br><p align='left'>Logged in as: " + user + "</p>"
		div.style.display = 'block'
		isNotGuest();
		return
	}

	div.innerHTML = "no user logged in"
}

//retrieves the cookie on the current machine where the current username is stored
function getCookie(c_name){
	var name, value, cookieSplit
	var cookies = document.cookie.split(";");
	for(var i = 0; i < cookies.length; i++){
		cookieSplit = cookies[i].split("=")
		name = cookieSplit[0].trim()
		if(name == c_name){					
			value = cookieSplit[1].trim()
			return value
		}
	}
	return -1
}

//calls tags servlet to add a tag to the current image if the tag hasn't already been added 
function tagPhoto(){
	var tag = document.getElementById('tags-select');
	var url = 'servlet/addChild/tags/'
	
	var div = document.getElementById("tag-div");
	var currentTags = div.innerHTML;
	if(currentTags.indexOf(tag.value) != -1){
		alert("This photo has already been tagged with that!")
		return;
	}

	var xmlDoc = document.implementation.createDocument(null, null, null);
	var xml = xmlDoc.createElement('tag');
	xml.appendChild( xmlDoc.createTextNode( tag.value + "/" + imgSrc));
	xmlDoc.appendChild( xml );
		
	var req = new XMLHttpRequest();
	req.open("POST", url, true);
	req.onreadystatechange = function() {
			if ( req.readyState == 4) {
				window.location = "image-view.html?img=" + imgSrc;
				var response = req.responseText.trim()
				if(response == "successful"){

				}
			}
	}
	req.send(xmlDoc);
}

//calls comments servlet to add a new comment
function postComment(){
	var textArea = document.getElementById('commentBox')
	var text = textArea.value.trim()
	var xmlDoc = document.implementation.createDocument(null, null, null);
	var xml = xmlDoc.createElement('comment');
	if(text == "")
		return;
//		xml.appendChild( xmlDoc.createTextNode( " ") );
	var reg = /</
	var match = text.search(reg)
	if(text.search(/</) != -1 || text.search(/>/) != -1){
		text = text.replace(/</g, "&lt")
		text = text.replace(/>/g, "&gt")
	}
	xml.appendChild( xmlDoc.createTextNode( text) );
	xmlDoc.appendChild( xml );
	
	var req = new XMLHttpRequest();
	var url = 'servlet/addChild/comments/' + imgSrc;
	req.open("POST", url, true);
		req.onreadystatechange = function() {
		if ( req.readyState == 4) {
			var div = document.getElementById('comment-field');
			var comment = req.responseText.trim();
			getComments()
			textArea.value = ""
			}	
			
		}
	req.send(xmlDoc);
	text.value = '';
}

//calls comments servlet to retrieve comments for current image  
function getComments(){
	var url = "servlet/child/comments/i" + imgSrc.split(".")[0]
	var div = document.getElementById("comment-field")
	div.innerHTML = "<br><p><b><u>Comments: </u></b></p>"
	var req = new XMLHttpRequest()
	req.open("GET", url, true)
	req.onreadystatechange = function() {
		if ( req.readyState == 4) {	
			var d = req.responseXML.documentElement
			var e = d.childNodes;
			e = e[0];
			var f = e.childNodes
			for(var i = 0; i < f.length; i++){
				var comment = f[i].childNodes;
				if(!(f[i].nodeName == "#text")){
					div.innerHTML += "<p style='padding:10px; border:1px solid #000000'><b>" + f[i].nodeName + ": </b>" + f[i].textContent + "</p>";
				}
			}
		}
	}						
	req.send(null) 
}

//calls view-count servlet to update the view count, this is used when browsing image by most viewed
function updateViewCount(){
	var xmlDoc = document.implementation.createDocument(null, null, null);
	var xml = xmlDoc.createElement('views');
	xml.appendChild( xmlDoc.createTextNode( " ") );
	xmlDoc.appendChild( xml );

	var url = "servlet/addChild/view-count/" + imgSrc
	var req = new XMLHttpRequest()
	
	req.open("POST", url, true)
	req.onreadystatechange = function() {
		if ( req.readyState == 4) {
			var count = req.responseText.trim();
		}
	}
	req.send(xmlDoc);
}

//used to build the report and share links, the current image URL is put directly into the body of the image
function getURL(){
	var html = document.getElementById("report")
	var share = document.getElementById("share")
	var url = window.location.href
	html.innerHTML += "<a href=\"mailto:photosectNL@gmail.com?subject=Reported%20Image&body="+ encodeURIComponent(url) +"\">Report offensive image here</a>"
	share.innerHTML += "<a href=\"mailto:?subject=Check%20out%20this%20image%20on%20PhotoSect!&body="+ encodeURIComponent(url) +"\">Share with friends!</a>"
}

//deletes photo from user's account (not from server) and removes it from XML files (tags, view-count etc.)
function deletePhoto(){
	var answer = confirm("Are you sure? Deletion is Permanent!")
	if(answer){
		var url = "servlet/delete/" + imgSrc
		req = new XMLHttpRequest();
		req.open("GET",url,true);
		req.onreadystatechange = function(){
			if(req.readyState == 4){
				window.location = "browse.html";
			}
		
		}
		req.send(null);
	}
}
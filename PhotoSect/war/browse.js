/*****************************************************
*	browse.js:
*		javascript that complements browse.html
******************************************************/

var divs = ['myphotos', 'tags', 'users', 'views', 'comments']
var user = -1

// logout() - log user out by deleting cookie and redirecting to index.html
function logout(){
	delCookie()
	window.location = 'index.html'
}


// loadFunction() - function to run when this page is loaded
function loadFunction(){
	validateLogin()
	// initialize sorting divs
	initializeSortDiv('tags', 'tags')			
	initializeSortDiv('users', 'passwords')
	initializeCountDiv('comments', 'comment-count')
	initializeCountDiv('views', 'view-count')	
	if(user != -1){
		getMyImages()	// populate myimages
		openDiv('myphotos')
	}
	else{
		openDiv('tags')
	}
}

// populates dropdowns in divs for tag and user sorting
function initializeSortDiv(divId, xmlFileName){
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
					select.innerHTML += "<option>" + d[i]	// add option to select
				}
			}
		}
	}							
	req.send(null)
}

// populates images in divs for view and comment count sorting
function initializeCountDiv(divId, servlet){
	var url = "servlet/" + servlet
	var req = new XMLHttpRequest()
	req.open("GET", url, true)
	req.onreadystatechange = function() {
		if ( req.readyState == 4) {	
			var d = req.responseText.trim()
			if(d != ""){
				populateImages(d, divId + "photoarea")
			}
		}
	}							
	req.send(null)
}


// populates the myimages section
function getMyImages(){
	var url = "servlet/child/usr-imgs/" + user
	var req = new XMLHttpRequest()
	req.open("GET", url, true)
	req.onreadystatechange = function() {
		if ( req.readyState == 4) {	
			var d = req.responseXML.documentElement
			var e = d.childNodes;
			var images = e[0].textContent.trim()
			if(images.indexOf("/") == -1){
				var imageDiv = document.getElementById('myphotoarea');
				imageDiv.innerHTML = "<h1>You don't have any photos! Why not upload some?</h1>"
			}else{
				populateImages(images, 'myphotoarea')
			}
		}
	}							
	req.send(null)
}

// get and display images based on selection of drop down
function getSortedImages(divId, file){
	var select = document.getElementById(divId + '-select')
	var index = select.selectedIndex
	if(index == 0){ 
		return;
	}
	select = select.options[index].value
	var url = "servlet/child/" + file + "/" + select
	var req = new XMLHttpRequest()
	req.open("GET", url, true)
	req.onreadystatechange = function() {
		if ( req.readyState == 4) {	
			var d = req.responseXML.documentElement
			var e = d.childNodes;
			var images = e[0].textContent.trim()
			if(images.indexOf("/") == -1){
				var imageDiv = document.getElementById(divId + 'photoarea');
				imageDiv.innerHTML = "No images to display!"
			}else{
				populateImages(images, (divId + 'photoarea'))
			}
		}
	}							
	req.send(null)
}	

// build the html for the tabs at the top of the page
function buildTabs(excludeTab){
	var tabs = document.getElementById('tabs');
	tabs.innerHTML = ""
	for(var i = 0; i < divs.length; i++){
		if(divs[i] != excludeTab && divs[i] != 'myphotos'){	// exclude tab that you are currently on
			tabs.innerHTML += "<a href='javascript:openDiv(" + '"' + divs[i] + '"' + ")' class='menulink'>" + divs[i] + "</a> |"
		}
		else if(divs[i] == 'myphotos' && excludeTab != 'myphotos'){
			if(user != -1){
				tabs.innerHTML += "<a href='javascript:openDiv(" + '"' + divs[i] + '"' + ")' class='menulink'>" + divs[i] + "</a> |"
			}
		}
		else{
			tabs.innerHTML += "<font color='grey'> " + divs[i] + "</font> |"	//this is the tab you are currently on
		}
	}
	if(user != -1){
		tabs.innerHTML += "<a href='javascript:logout()'  class='menulink'>logout</a>"	//add logout if user is logged in
	}
	else{
		tabs.innerHTML += "<a href='index.html'  class='menulink'>home</a>"	// add link back to home if user is logged in
	}
}			

// check if a user is logged on
function validateLogin(){
	user = getCookie("user")	// get username of logged in user
	var div = document.getElementById('loggedInAs')
	if(user != -1){
		div.innerHTML += "<p>Logged in as: " + user + "</p>"
		div.style.display = 'block'
		return
	}
	div.innerHTML = "no user logged in"
}


// writes the html to display images in a div
function populateImages(images, div){
	images = images.split("/")
	var imageDiv = document.getElementById(div)
	var count = images.length - 1
	imageDiv.innerHTML = "";
	imageDiv.innerHTML += "<table style=\"border-spacing: 40px 10px;\"border=\"0\">"
	var j = 1
	if((div == "viewsphotoarea") || (div == "commentsphotoarea")){
		if(count > 12){
		count = 12;
		}
	}
	while (count > 0){
		imageDiv.innerHTML += "<tr>"
		for(var i = 0; (i < 2) && (count > 0); i++){
			imageDiv.innerHTML += "<td align='left'><A href='image-view.html?img=" + images[j] + "'><img src='images/" + images[j] + ".PNG' vspace=\"10\" hspace=\"10\" style=\"max-height:200px; max-width:200px\"></A></td>"
			count = count - 1
			j = j+1
		}
		imageDiv.innerHTML += "</tr>"
	}
	imageDiv.innerHTML += "</table>"
}

// javascript for the image uploading popup
function popitup(url) {
	var name = document.getElementById("userfile1").value
	var error = document.getElementById("uploadError")
	error.innerHTML = ""
	var dot = name.lastIndexOf(".");
	var extension = name.substring(dot + 1);
	extension = extension.toLowerCase() ;
	if( extension != "png" && extension != "jpeg" && extension != "jpg"){
		alert("Error, you can only upload PNG or JPEG images")
	}
	else{
		var iMyWidth;
		var iMyHeight;
		//half the screen width minus half the new window width (plus 5 pixel borders).
		iMyWidth = (window.screen.width/2) - (235);
		//half the screen height minus half the new window height (plus title and status bars).
		iMyHeight = (window.screen.height/2) - (125 + 50);
		var win2 = window.open(url,"Image Upload","status=no,height=100,width=400,resizable=yes,left=" + iMyWidth + ",top=" + iMyHeight + ",screenX=" + iMyWidth + ",screenY=" + iMyHeight + ",toolbar=no,menubar=no,scrollbars=no,location=no,directories=no");
		if (window.focus) {win2.focus()}
		return false;
	}
}
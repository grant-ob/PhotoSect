/*****************************************************
*	index.js:
*		javascript that complements index.html
******************************************************/

var divs = ['login', 'register', 'about', 'regSuccess']
var banners = ["","1","2"]
bIndex = 0
var loggedIn = -1

// changes the banner image
function rotateBanner(){
	var banner = document.getElementById("banner")
	bIndex = (bIndex + 1)%(banners.length)
	banner.src = "banners/banner_logo" + banners[bIndex] + ".png"
}

// function that is called when the page is loaded
function loadFunction(){
	openDiv('about')
	getNewImages()
}

// function that is called when a user attempts to login
function login(){
	validateUser()
}


//makes a call to the new images servlet to get new images
function getNewImages(){
	var url = "servlet/new-images"
	var req = new XMLHttpRequest()
	var div = document.getElementById("newphotos")
	div.innerHTML = ""
	req.open("GET", url, true)
	req.onreadystatechange = function() {
		if ( req.readyState == 4) {	
			var response = req.responseText.trim()				
			if(response != ""){
				response = response.split("/");
				for(var i = 0; i < response.length; i++){
					div.innerHTML += "<a href='image-view.html?img=" + response[i] + "' rel='thumbnail'><img src='images/" + response[i] + ".PNG' vspace='10' hspace='10' style = 'max-width: 100px; max-height: 100px'></a>"
			}
			}
			else{
				div.innerHTML += "There are no new images to display!"
			}
		}
	}				
	req.send(null)
}

// builds the html for the tabs at the top of the page, based on the current tab
function buildTabs(excludeTab){
	var tabs = document.getElementById('tabs');
	tabs.innerHTML = ""
	for(var i = 0; i < divs.length; i++){
		if(divs[i] != excludeTab){
			if(divs[i] != 'regSuccess'){
				tabs.innerHTML += "<a href='javascript:openDiv(" + '"' + divs[i] + '"' + ")' class='menulink'>" + divs[i] + "</a> |"
			}
		}
		else{
			tabs.innerHTML += "<font color='grey'> " + divs[i] + "</font> |"
		}
	}
	tabs.innerHTML += "<a href='browse.html'  class='menulink'>browse</a>"
}


// validates login information, and if valid creates a cookie and redirects to browse.html
function validateUser(){
	var url = "servlet/child/passwords"
	var uname = document.getElementById("uname").value.trim()
	var pwd = document.getElementById("pwd").value.trim()
	url = url + "/" + uname
	var req = new XMLHttpRequest()
	req.open("GET", url, true)
	req.onreadystatechange = function() {
		if ( req.readyState == 4) {	
			var d = req.responseXML.documentElement
			var e = d.childNodes;
			var storedPwd = e[0].textContent.trim()
			if(storedPwd == pwd){
				setCookie('user', uname);
				window.location = 'browse.html'
			}
			else{
				alert("invalid username or password!")
			}
		}
	}						
	req.send(null) 
	
}

// checks registration data and if valid, creates a new account based on a servlet call
function createAccount(){
	var username = document.getElementById('r-uname');
	var pwd = document.getElementById('r-pwd');
	var confirmPwd = document.getElementById('r-pwd2');
	var invalidUser = "Invalid User name!";
	var duplicateUser = "Account name already exists! Try again!";
	var invalidpwd = "Invalid Password!";
	var pwdUnconfimed = "Passwords didn't match!";
	
	if(pwd.value.length < 6){
		alert(invalidpwd);
		return;
	}
	if(username.value.length < 4){
		alert(invalidUser);
		return;
	}
	
	if(pwd.value.trim() == confirmPwd.value.trim()){
		var xmlDoc = document.implementation.createDocument(null, null, null);
		var xml = xmlDoc.createElement('user');
		xml.appendChild( xmlDoc.createTextNode( username.value + "/" + pwd.value) );
		xmlDoc.appendChild( xml );
		
		var req = new XMLHttpRequest();
		var url = 'servlet/addChild/passwords';
		req.open("POST", url, true);
		req.onreadystatechange = function() {
			if ( req.readyState == 4) {
				var response = req.responseText.trim()
				if(response == "successful"){
					openDiv('regSuccess');
				}
				else if (response == "duplicate"){
					var text = "Account name already exists! Try again!";
					alert(duplicateUser);
				}else{					
				alert(invalidUser);
				}
			}
		}
		req.send(xmlDoc);
	}else{
		alert(pwdUnconfirmed);
	}
}
/*****************************************************
*	Cookie Functions:
*		functions for setting, getting, and deleting
		document cookie.
******************************************************/

// setCookie() - sets document cookie as "cname=value"
function setCookie(c_name,value){
	document.cookie=c_name + "=" + value;
}

// getCookie() - gets document cookie with name c_name if exists, else -1
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

// delCookie() - deletes cookie by seeting expiry date to current date
function delCookie(){
	var d = new Date()
	document.cookie += ";expires=" + d.toGMTString() + ";;" 
}
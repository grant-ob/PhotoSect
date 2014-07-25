/*****************************************************
*	Div Functions:
*		functions for showing and hiding divs
******************************************************/

// openDiv() - shows the div with id divId and hides other divs
function openDiv(divId){
	collapseDivs()
	document.getElementById(divId).style.display = 'block'
	buildTabs(divId)
}

// hides all divs in the divs array
function collapseDivs(){
	for(var i = 0; i < divs.length; i++){
		var div = document.getElementById(divs[i])
		div.style.display = 'none'
	}
}
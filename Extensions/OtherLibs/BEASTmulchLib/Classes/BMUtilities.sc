+ SimpleNumber {

	getTimeString {
		var string, minutes, hours, seconds;
		minutes = (this/60).trunc(1);
		if(minutes >= 60,{ hours = (minutes/60).trunc(1);
			minutes = minutes%60;
		},{
			hours = 0;
		});
		seconds = (this%60).trunc(0.1);
		
		if(hours == 0, {string = "00:"}, {string = hours.asString ++ ":" });
		if(minutes < 10, {string = string ++ "0" ++ minutes ++ ":"}, 
			{string = string ++ minutes ++ ":"; });
		if(seconds<10,{string = string ++ "0" ++ seconds},
			{string = string ++ seconds});
		if(string.size < 10, {string = string ++ ".0"});
		^string
    }
    
}
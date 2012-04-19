
/*

601.12345.minsec;

601.12345 minsec: 0.001;

*/

+ SimpleNumber {
	minsec { | round = 0 |
		// return as minutes and seconds, optionally rounded
		^[(this / 60).floor, this % 60 round: round]
	} 	
}


+ SimpleNumber {
	minsecstring { | round = 0, min = "min", sec = "sec" |
		// return as minutes and seconds, optionally rounded
		^format("% % % %", (this / 60).floor, min, this % 60 round: round, sec)
	} 	
}
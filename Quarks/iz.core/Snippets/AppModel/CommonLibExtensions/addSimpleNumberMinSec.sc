/* iz Fri 28 September 2012  1:06 PM EEST

Format number as minutes - seconds like this: "% min, % sec"

123.456789.minSec;

*/

+ SimpleNumber {
	minSec { | resolution = 0.001 |
		^format("% min, % sec", this / 60 round: 1, this % 60 round: resolution)
	}	
}
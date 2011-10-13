+ SimpleNumber {

	asNoteOct {
		var absPitch = this.asInteger % 12;
		^absPitch.switch(
					0, 	{"do"}, 
					1, 	{"do#"}, 
					2, 	{"re"}, 
					3, 	{"re#"},
					4, 	{"mi"}, 
					5, 	{"fa"}, 
					6, 	{"fa#"}, 
					7, 	{"sol"}, 
					8, 	{"sol#"}, 
					9, 	{"la"}, 
					10, 	{"la#"}, 
					11, 	{"si"})
		++
		((this - absPitch - 24) / 12).round;
	}

	bornes {
		arg min, max;
		^case	{this < min}	{min;}
				{this > max}	{max;}
				{this;};
	}
		

}
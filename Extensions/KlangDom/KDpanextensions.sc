// needed for KDpan

+ Function { 
	asSynthFunc { ^this }	
}

/* 

p = KDpan([\bphasor, \bufnum, O@\swallowsa]);

p = KDpan([\blfn3, \bufnum, O@\swallowsa, \vol, 10, \rate, 0.01]);


*/

+ Array { 
	asSynthFunc {
		var defname, params;
	 	#defname ... params = this;
		^{ | out, group |
			Synth(defname, params ++ [\out, out], group, \addToHead);
		}

	}	
}

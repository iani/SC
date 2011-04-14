
/* Draft of simple class for buffers for a project 


Ia1Pattern.load;

Ia1Pattern.at(\indust1);
Ia1Patterns.at(\buffer1, \buffer2);


*/

Ia1Pattern {
	classvar <patterns;
	*load {
		var s;

		patterns = IdentityDictionary.new;

		s = Server.default;
		
		patterns[\indust1] = Pdef(\buf, Pbind(
				\instrument,	\buf,
				\amp,		Pseq([0.8], inf),
				\dur,		Pseq([50], 1),
				\startPos,	0,
				\rate,		Pseq([1], inf),
				\sust,		Pseq([ 10 ], inf),
				\rls,		Pseq([ 30 ], inf),
				\pan,  		Pseq([ 0.3], inf),
				\out, 		Pseq([~revBus], inf),
				\bufnum,		 Pseq([~indbuf], inf)
			)
		);
		
		
		
	}
	
	*at { | name |
		
		^patterns.at(name.asSymbol);
	}
	
	*atN { | ... names |
		^names collect: { | name | patterns.at(name.asSymbol); };
	}
	
	
}
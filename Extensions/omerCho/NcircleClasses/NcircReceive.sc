
/*
NcircReceive.load;
*/


NcircReceive {
	*load {
///////////////////////////////////////
		Preceive(
			\1 -> { { WhiteNoise.ar(EnvGen.kr(Env.perc, doneAction: 2) * 0.1) }.play },
			\2 -> { { SinOsc.ar(400, 0, EnvGen.kr(Env.perc, doneAction: 2) * 0.1) }.play },
			\6 -> { { SinOsc.ar(1000, 0, EnvGen.kr(Env.perc, doneAction: 2) * 0.1) }.play },
			\10 -> { { WhiteNoise.ar(EnvGen.kr(Env.perc, doneAction: 2) * 0.1) }.play },
			\3 -> { { PinkNoise.ar(EnvGen.kr(Env.perc, doneAction: 2) * 0.1) }.play }
		).play;
//////////////////////////////////////
	}
	
	*unLoad { 
		 
	}
}

/*
NcircReceive.load;

Posc(\msg, Pseq([Psend(1)], 10)).play;

Posc(
	\dur,  	Pseq([~duyekDur], inf),
	\msg, 	Psend( Pseq([2], inf))

).play;




Pdef(\cir1def).play;

(
Pdef(\cir1def, Posc(
	\msg, Pseq([\1, \nil, \2, 	\nil, \nil, \6, \nil, \3, \cir1, \10], inf),
	\dur, Pseq([~duyekDur], inf)
	)
);
)

Pdef(\cir1def).stop;


*/
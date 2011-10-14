//:Morphing from Images to Rite of Spring

(
a = SynthDef("pitchFollow_Debussy_record",{ | ampInc = 0, ampExp = 1, ampScale = 0.1, bufnum = 0 |
	var in, amp, freq, hasFreq, out;
	in = SoundIn.ar(0);
	amp = Amplitude.kr(in, 0.05, 0.05);
	# freq, hasFreq = Pitch.kr(in, ampThreshold: 0.02, median: 7);
	//freq = Lag.kr(freq.cpsmidi.round(1).midicps, 0.05);
	out = Mix.new(VarSaw.ar(freq * [0.5,1.2, 1.5, 1.7, 2.2] / 2.5, 0, LFNoise1.kr(0.3,0.1,0.1), 
	amp + ampInc pow: ampExp * ampScale));
	6.do({
		out = AllpassN.ar(out, 0.040, [0.040.rand,0.040.rand], 2)
	});
	
	Out.ar(0, out);
}).play(Server.default);
)

a.set(\ampInc, 0.0);


a.set(\ampInc, 0);
a.set(\ampInc, 0.5);
a.set(\ampExp, 0.2);


//:ampexp 1
a.set(\ampExp, 1);

//:ampexp 0.9
a.set(\ampExp, 0.9);

//:ampexp 0.8
a.set(\ampExp, 0.8);

//:ampexp 0.7
a.set(\ampExp, 0.7);

//:ampexp 0.5
a.set(\ampExp, 0.5);

//:ampexp 0.5
a.set(\ampExp, 0.05);

//:ampexp 1.5
a.set(\ampExp, 1.5);

//:ampexp 2
a.set(\ampExp, 2);

//:ampexp 3
a.set(\ampExp, 3);

//:ampexp 3
a.set(\ampExp, 5);


//:ampScale 1
a.set(\ampScale, 0.16);

//:ampScale 0.9
a.set(\ampScale, 0.9);

//:ampScale 0.8
a.set(\ampScale, 0.8);

//:ampScale 0.7
a.set(\ampScale, 0.7);

//:ampScale 0.5
a.set(\ampScale, 0.5);

//:ampScale 0.5
a.set(\ampScale, 0.01);

//:ampScale 0.5
a.set(\ampScale, 0.03);

//:ampScale 0.5
a.set(\ampScale, 0.0);

//:ampScale 1.5
a.set(\ampScale, 1.5);

//:ampScale 2
a.set(\ampScale, 2);


//:ampInc 1
a.set(\ampInc, 1);

//:ampInc 0.9
a.set(\ampInc, 0.9);

//:ampInc 0.8
a.set(\ampInc, 0.8);

//:ampInc 0.7
a.set(\ampInc, 0.7);

//:ampInc 0.5
a.set(\ampInc, 0.5);

//:ampInc 1.5
a.set(\ampInc, 1.5);

//:ampInc 2
a.set(\ampInc, 2);

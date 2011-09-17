
r = RecordInput(
SynthDef("help-Diskout", { | bufnum = 0 |
	var in, amp, freq, hasFreq, out;
	in = SoundIn.ar(0);
	amp = Amplitude.kr(in, 0.05, 0.05);
	# freq, hasFreq = Pitch.kr(in, ampThreshold: 0.02, median: 7);
	//freq = Lag.kr(freq.cpsmidi.round(1).midicps, 0.05);
	out = Mix.new(VarSaw.ar(freq * [0.5,1.2, 1.5, 1.7, 2.2] / 2.5, 0, LFNoise1.kr(0.3,0.1,0.1), amp));
	6.do({
		out = AllpassN.ar(out, 0.040, [0.040.rand,0.040.rand], 2)
	});
	// RECORD HERE ============
	
	DiskOut.ar(bufnum, in);
	Out.ar(0, out);
})
);

r.start;
r.stop;

//:-----



r = RecordInput(
SynthDef("pitchFollow_Debussy_record",{ | ampInc = 0, ampExp = 1, ampScale = 1, bufnum = 0 |
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
	DiskOut.ar(bufnum, in);
	
	Out.ar(0, out);
}));

//:start
r.start;
//:stop
r.stop;

//:asdf

r.set(\ampExp, 1);
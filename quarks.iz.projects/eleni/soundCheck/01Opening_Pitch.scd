

s = Server.local;

//:Debussy images pour orchestre 1 - mixtur
(
SynthDef("pitchFollow1",{
	var in, amp, freq, hasFreq, out;
	in = Mix.new(SoundIn.ar([0,1]));
	amp = Amplitude.kr(in, 0.05, 0.05);
	# freq, hasFreq = Pitch.kr(in, ampThreshold: 0.02, median: 7);
	//freq = Lag.kr(freq.cpsmidi.round(1).midicps, 0.05);
	out = Mix.new(VarSaw.ar(freq * [0.5,1.2, 1.5, 1.7, 2.2] / 2.5, 0, LFNoise1.kr(0.3,0.1,0.1), amp));
	6.do({
		out = AllpassN.ar(out, 0.040, [0.040.rand,0.040.rand], 2)
	});
	Out.ar(0,out)
}).play(s);
)

//:comb stuff
(
SynthDef("pitchFollow2",{
	var in, amp, freq, hasFreq, out;
	in = Mix.new(SoundIn.ar([0,1]));
	amp = Amplitude.kr(in, 0.05, 0.05);
	# freq, hasFreq = Pitch.kr(in, ampThreshold: 0.02, median: 7);
	out = CombC.ar(LPF.ar(in, 1000), 0.1, (2 * freq).reciprocal, -6).distort * 0.05;
	6.do({
		out = AllpassN.ar(out, 0.040, [0.040.rand,0.040.rand], 2)
	});
	Out.ar(0,out);
}).play(s);
)


//:Variation of the above 
(
SynthDef("pitchFollow2",{
	var in, amp, freq, hasFreq, out;
	in = Mix.new(SoundIn.ar([0,1]));
	amp = Amplitude.kr(in, 0.05, 0.05);
	# freq, hasFreq = Pitch.kr(in, ampThreshold: 0.02, median: 7);
	out = CombC.ar(LPF.ar(in, 1000), 0.1, (LFNoise0.kr(7).range(0.5, 2.5) * freq).reciprocal, -6).distort * 0.05;
	6.do({
		out = AllpassN.ar(out, 0.040, [0.040.rand,0.040.rand], 2)
	});
	Out.ar(0,out * 9);
}).play(s);
)

//:DiskOut

SynthDef("help-Diskout", {arg bufnum;
	DiskOut.ar(bufnum, In.ar(0,2));
}).send(s);

//:Debussy images pour orchestre 1 - mixtur
(
SynthDef("pitchFollow_Debussy_record",{ | bufnum = 0 |
	var in, amp, freq, hasFreq, out;
	in = SoundIn.ar(0);
	amp = Amplitude.kr(in, 0.05, 0.05);
	# freq, hasFreq = Pitch.kr(in, ampThreshold: 0.02, median: 7);
	//freq = Lag.kr(freq.cpsmidi.round(1).midicps, 0.05);
	out = Mix.new(VarSaw.ar(freq * [0.5,1.2, 1.5, 1.7, 2.2] / 2.5, 0, LFNoise1.kr(0.3,0.1,0.1), amp + 1 pow: 2));
	6.do({
		out = AllpassN.ar(out, 0.040, [0.040.rand,0.040.rand], 2)
	});
	
	Out.ar(0, out);
}).play(Server.default);
)


// start something to record
x = Synth.new("bubbles");

// allocate a disk i/o buffer
b = Buffer.alloc(s, 65536, 2);

// create an output file for this buffer, leave it open
b.write("~/diskouttest.aiff".standardizePath, "aiff", "int16", 0, 0, true);
// create the diskout node; making sure it comes after the source
d = Synth.tail(nil, "help-Diskout", ["bufnum", b]);
// stop recording
d.free;
// stop the bubbles
x.free;
// close the buffer and the soundfile
b.close;
// free the buffer
b.free;


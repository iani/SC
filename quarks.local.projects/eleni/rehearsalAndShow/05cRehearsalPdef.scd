p = ProxySpace.new;
p.fadeTime = 2; // fadeTime specifies crossfade

p = { SinOsc.ar(440, 0, 0.1) }.play;
p.free;

(
p = { | ampInc = 0, ampExp = 1, ampScale = 0.1, bufnum = 0, scale = 2.5 |
	var in, amp, freq, hasFreq, out;
	in = SoundIn.ar(0);
	amp = Amplitude.kr(in, 0.05, 0.05);
	
	# freq, hasFreq = Pitch.kr(in, ampThreshold: 0.02, median: 7);
	//freq = Lag.kr(freq.cpsmidi.round(1).midicps, 0.05);
	out = Mix.new(VarSaw.ar(freq * [0.5,1.2, 1.5, 1.7, 2.2] / scale, 0, LFNoise1.kr(0.3,0.1,0.1), 
	amp + ampInc pow: ampExp * ampScale));
	6.do({
		out = AllpassN.ar(out, 0.040, [0.040.rand,0.040.rand], 2)
	});
	
//	Out.ar(0, out);
	out;
}.play;
)

{ SinOsc.ar([


p.set(\ampScale, 0.12);
p.set(\ampScale, 1.1);
p.set(\ampExp, 1.5);

p.set(\scale, 2.5);


r = { 
	loop {
		p.set(\ampScale, 0.3 rrand: 1.5);
		0.1.wait;
//		0.1.rrand(0.3).wait;	
		p.set(\ampScale, 0.01 rrand: 0.1);
//		2.1.rrand(0.3).wait;
		[0.1, 0.2, 0.4, 2, 4].choose.wait;
	};	
}.fork;

r.stop;


p.release(10);


(
p = { | ampInc = 0, ampExp = 1, ampScale = 0.1, bufnum = 0, modrate = 1 |
	var in, amp, freq, hasFreq, out;
	in = SoundIn.ar(0);
	amp = Amplitude.kr(in, 0.05, 0.05);
	
	# freq, hasFreq = Pitch.kr(in, ampThreshold: 0.02, median: 7);
	//freq = Lag.kr(freq.cpsmidi.round(1).midicps, 0.05);
	out = Mix.new(VarSaw.ar(freq * [0.5,1.2, 1.5, 1.7, 2.2] / 2.5 * LFNoise1.kr(modrate).range(-5, 5), 0, LFNoise1.kr(0.3,0.1,0.1), 
	amp + ampInc pow: ampExp * ampScale));
	6.do({
		out = AllpassN.ar(out, 0.040, [0.040.rand,0.040.rand], 2)
	});
	
//	Out.ar(0, out);
	out;
}.play;
)

(
p = { | ampInc = 0, ampExp = 1, ampScale = 0.1, bufnum = 0, 
		f1 = 0.5, f2 = 1.2, f3 = 1.5, f4 = 1.7, f5 = 2.2 
	|
	var in, amp, freq, hasFreq, out;
	in = SoundIn.ar(0);
	amp = Amplitude.kr(in, 0.05, 0.05);
	# freq, hasFreq = Pitch.kr(in, ampThreshold: 0.02, median: 7);
	//freq = Lag.kr(freq.cpsmidi.round(1).midicps, 0.05);
	out = Mix.new(SinOsc.ar(freq * [f1, f2, f3, f4, f5] / 2.5, 0, LFNoise1.kr(0.3,0.1,0.1), 
	amp + ampInc pow: ampExp * ampScale));
	6.do({
		out = AllpassN.ar(out, 0.040, [0.040.rand,0.040.rand], 2)
	});
	
//	Out.ar(0, out);
	out;
}.play;
)

(
p = { | ampInc = 0, ampExp = 1, ampScale = 0.1, bufnum = 0 |
	var in, amp, freq, hasFreq, out;
	in = SoundIn.ar(0);
	amp = Amplitude.kr(in, 0.05, 0.05);
	# freq, hasFreq = Pitch.kr(in, ampThreshold: 0.02, median: 7);
	//freq = Lag.kr(freq.cpsmidi.round(1).midicps, 0.05);
	out = Mix.new(SinOsc.ar(freq * [0.5,1.2, 1.5, 1.7, 2.2] / 2.5, 0, LFNoise1.kr(0.3,0.1,0.1), 
	amp + ampInc pow: ampExp * ampScale));
	6.do({
		out = AllpassN.ar(out, 0.040, [0.040.rand,0.040.rand], 2)
	});
	
//	Out.ar(0, out);
	out;
}.play;
)





//:OTHER
 = SynthDef("pitchFollow_Debussy_record",{ | ampInc = 0, ampExp = 1, ampScale = 0.1, bufnum = 0 |
	var in, amp, freq, hasFreq, out;
	in = SoundIn.ar(0);
	amp = Amplitude.kr(in, 0.05, 0.05);
	# freq, hasFreq = Pitch.kr(in, ampThreshold: 0.02, median: 7);
	//freq = Lag.kr(freq.cpsmidi.round(1).midicps, 0.05);
	out = Mix.new(SinOsc.ar(freq * [0.5,1.2, 1.5, 1.7, 2.2] / 2.5, 0, LFNoise1.kr(0.3,0.1,0.1), 
	amp + ampInc pow: ampExp * ampScale));
	6.do({
		out = AllpassN.ar(out, 0.040, [0.040.rand,0.040.rand], 2)
	});
	
	Out.ar(0, out);
}).play(Server.default);
)

(
a = SynthDef("pitchFollow_Debussy_record",{ | ampInc = 0, ampExp = 1, ampScale = 0.1, bufnum = 0 |
	var in, amp, freq, hasFreq, out;
	in = SoundIn.ar(0);
	amp = Amplitude.kr(in, 0.05, 0.05);
	# freq, hasFreq = Pitch.kr(in, ampThreshold: 0.02, median: 7);
	//freq = Lag.kr(freq.cpsmidi.round(1).midicps, 0.05);
	out = Mix.new(SinOsc.ar(freq * [0.5,1.2, 1.5, 1.7, 2.2] / 2.5, 0, LFNoise1.kr(0.3,0.1,0.1), 
	amp + ampInc pow: ampExp * ampScale));
	6.do({
		out = AllpassN.ar(out, 0.040, [0.040.rand,0.040.rand], 2)
	});
	
	Out.ar(0, out);
}).play(Server.default);
)


(
a = SynthDef("pitchFollow_Debussy_record",{ | ampInc = 0, ampExp = 1, ampScale = 0.2, bufnum = 0 |
	var in, amp, freq, hasFreq, out;
	in = SoundIn.ar(0);
	amp = Amplitude.kr(in, 0.05, 0.05);
	# freq, hasFreq = Pitch.kr(in, ampThreshold: 0.02, median: 7);
	//freq = Lag.kr(freq.cpsmidi.round(1).midicps, 0.05);
	out = Mix.new(LFNoise0.ar(freq * [0.5,1.2, 1.5, 1.7, 2.2] / 2.5, LFNoise1.kr(0.3,0.1,0.1), 
	amp + ampInc pow: ampExp * ampScale));
	6.do({
		out = AllpassN.ar(out, 0.040, [0.040.rand,0.040.rand], 2)
	});
	
	Out.ar(0, out);
}).play(Server.default);
)


(
a = SynthDef("pitchFollow_Debussy_record",{ | ampInc = 0, ampExp = 1, ampScale = 0.1, bufnum = 0 |
	var in, amp, freq, hasFreq, out;
	in = SoundIn.ar(0);
	amp = Amplitude.kr(in, 0.05, 0.05);
	# freq, hasFreq = Pitch.kr(in, ampThreshold: 0.02, median: 7);
	//freq = Lag.kr(freq.cpsmidi.round(1).midicps, 0.05);
	out = Mix.new(LFNoise0.ar(freq * [0.5,1.2, 1.5, 1.7, 2.2] / 2.5, LFNoise1.kr(0.3,0.1,0.1), 
	amp + ampInc pow: ampExp * ampScale));
	6.do({
		out = AllpassN.ar(out, 0.040, [0.040.rand,0.040.rand], 2)
	});
	
	Out.ar(0, out);
}).play(Server.default);
)

(
a = SynthDef("pitchFollow_Debussy_record",{ | ampInc = 0, ampExp = 1, ampScale = 0.1, bufnum = 0 |
	var in, amp, freq, hasFreq, out;
	in = SoundIn.ar(0);
	amp = Amplitude.kr(in, 0.05, 0.05);
	# freq, hasFreq = Pitch.kr(in, ampThreshold: 0.02, median: 7);
	//freq = Lag.kr(freq.cpsmidi.round(1).midicps, 0.05);
	out = Mix.new(LFNoise1.ar(freq * [0.5,1.2, 1.5, 1.7, 2.2] / 2.5, LFNoise1.kr(0.3,0.1,0.1), 
	amp + ampInc pow: ampExp * ampScale));
	6.do({
		out = AllpassN.ar(out, 0.040, [0.040.rand,0.040.rand], 2)
	});
	
	Out.ar(0, out);
}).play(Server.default);
)

(
a = SynthDef("pitchFollow_Debussy_record",{ | ampInc = 0, ampExp = 1, ampScale = 0.1, bufnum = 0 |
	var in, amp, freq, hasFreq, out;
	in = SoundIn.ar(0);
	amp = Amplitude.kr(in, 0.05, 0.05);
	# freq, hasFreq = Pitch.kr(in, ampThreshold: 0.02, median: 7);
	//freq = Lag.kr(freq.cpsmidi.round(1).midicps, 0.05);
	out = Mix.new(LFNoise2.ar(freq * [0.5,1.2, 1.5, 1.7, 2.2] / 2.5, LFNoise1.kr(0.3,0.1,0.1), 
	amp + ampInc pow: ampExp * ampScale));
	6.do({
		out = AllpassN.ar(out, 0.040, [0.040.rand,0.040.rand], 2)
	});
	
	Out.ar(0, out);
}).play(Server.default);
)



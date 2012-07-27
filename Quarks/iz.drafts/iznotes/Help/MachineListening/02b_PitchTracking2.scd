

//:Basic Example From Help, Live input

SynthDef("pitchFollow2", {
Ê Ê var in, amp, freq, hasFreq, out;
Ê Ê in = Mix.new(SoundIn.ar([0,1]));
Ê Ê amp = Amplitude.kr(in, 0.05, 0.05);
Ê Ê # freq, hasFreq = Pitch.kr(in, ampThreshold: 0.02, median: 7);
Ê Ê out = CombC.ar(LPF.ar(in, 1000), 0.1, (2 * freq).reciprocal, -6).distort * 0.05;
Ê Ê 6.do({
Ê Ê Ê Ê out = AllpassN.ar(out, 0.040, [0.040.rand,0.040.rand], 2)
Ê Ê });
Ê Ê Out.ar(0,out);
}).play(Server.default);

//:Example from Help, mod: Using buffer

// Make sure buffers are loaded: 

BufferResource.loadPaths("/Users/iani2/Music/sounds/osmosis_sounds_wav/seals_normalized/*.wav".pathMatch);
BufferResource.loadPaths("/Users/iani2/Music/sounds/osmosis_sounds_wav/swallows/*.wav".pathMatch);


//:Now use a buffer for pitchtracking: 
SynthDef("pitchFollow2",{ | buf |
Ê Ê 	var in, amp, freq, hasFreq, out;
Ê Ê in = PlayBuf.ar(1, buf, loop: 1);
Ê Ê amp = Amplitude.kr(in, 0.05, 0.05);
Ê Ê # freq, hasFreq = Pitch.kr(in, ampThreshold: 0.02, median: 7);
Ê Ê out = CombC.ar(LPF.ar(in, 1000), 0.1, (2 * freq).reciprocal, -6).distort * 0.05;
Ê Ê 6.do({
Ê Ê Ê Ê out = AllpassN.ar(out, 0.040, [0.040.rand,0.040.rand], 2)
Ê Ê });
Ê Ê Out.ar(0, [out * MouseY.kr(5, 0), in * MouseX.kr(0, 1)]);
}).play(Server.default, args: [\buf, \swallowsa.buffer]);

//:Silence the sound when no pitch is tracked 
a = SynthDef("pitchFollow2",{ | buf, ampThreshold = 0.02, transpose = 2 |
Ê Ê 	var in, amp, freq, hasFreq, out;
Ê Ê in = PlayBuf.ar(1, buf, loop: 1);
Ê Ê amp = Amplitude.kr(in, 0.05, 0.05);
Ê Ê # freq, hasFreq = Pitch.kr(in, ampThreshold: ampThreshold, median: 7);
Ê Ê out = CombC.ar(LPF.ar(in, 1000), 0.1, (transpose * freq).reciprocal, -6).distort * 0.05;
	out = out * hasFreq;
Ê Ê 6.do({
Ê Ê Ê Ê out = AllpassN.ar(out, 0.040, [0.040.rand,0.040.rand], 2)
Ê Ê });
Ê Ê Out.ar(0, [out * MouseY.kr(5, 0), in * MouseX.kr(0, 1)]);
}).play(Server.default, args: [\buf, \swallowsa.buffer]);

//:changing some parameters
a.set(\transpose, 1);
a.set(\transpose, 0.5);
a.set(\transpose, 2.0);

//:Parametrize the amplitudes of source and synthesized sound 
a = SynthDef("pitchFollow2",{ | buf, ampThreshold = 0.02, transpose = 2, srcAmp = 0, fxAmp = 1 |
Ê Ê 	var in, amp, freq, hasFreq, out;
Ê Ê in = PlayBuf.ar(1, buf, loop: 1);
Ê Ê amp = Amplitude.kr(in, 0.05, 0.05);
Ê Ê # freq, hasFreq = Pitch.kr(in, ampThreshold: ampThreshold, median: 7);
Ê Ê out = CombC.ar(LPF.ar(in, 1000), 0.1, (transpose * freq).reciprocal, -6).distort * 0.05;
	out = out * hasFreq;
Ê Ê 6.do({
Ê Ê Ê Ê out = AllpassN.ar(out, 0.040, [0.040.rand,0.040.rand], 2)
Ê Ê });
Ê Ê Out.ar(0, [out * fxAmp, in * srcAmp]);
}).play(Server.default, args: [\buf, \swallowsa.buffer]);

//:changing some parameters
a.set(\transpose, 1);
a.set(\transpose, 0.5);
a.set(\transpose, 2.0);
a.set(\srcAmp, 0.04);
a.set(\fxAmp, 4);

a.set(\srcAmp, 0.02);
a.set(\fxAmp, 1.4);



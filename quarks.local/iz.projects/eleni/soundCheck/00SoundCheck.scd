//:1 chan test tone 
{ SinOsc.ar([440, 550], 0, 0.1) }.play;

//:all 4
{ SinOsc.ar([440, 550, 660, 880], 0, 0.1) }.play;

//:4 in round
a = { | out = 0 | Out.ar(out, WhiteNoise.ar(Decay.kr(Impulse.kr(5, 0, 0.2)))) }.play; 
a.set(\out, 0);
a.set(\out, 1);
a.set(\out, 2);
a.set(\out, 3);


//:4 chan test tone not
{ Out.ar(0, PanAz.ar(4, WhiteNoise.ar(EnvGen.kr(Env.perc(0.1)), LFSaw.kr(0.2)) }.play;



//:audio input, with pitchshift 

{ Out.ar(0, DelayN.ar(PitchShift.ar(In.ar(8), 0.01, MouseX.kr(0.1, 10)), 2, 2, 0.1)) }.play;

//:audio input delayed simply
{ Out.ar(0, DelayN.ar(In.ar(8), 1, 1, 0.1)) }.play;


(
SynthDef(\ChicagoPad2, { |out = 0, freq = 440, freq2=660, freq3=528, cutoff
= 500, amp = 0.2, gate=1|

Ê Ê Ê Ê var snd;

Ê Ê Ê Ê freq = freq + SinOsc.kr(freq*1/2, 0, freq/2, 0);

Ê Ê Ê Ê snd = Saw.ar([freq, freq+1, freq-1, freq2, freq3] * [0.99,1])*0.1;

Ê Ê Ê Ê snd = snd + VarSaw.ar(0.99*[freq, freq+1, freq-1, freq2, freq3, freq2+1,
freq3+1],0, LFTri.kr(7.13).range(0.00,0.1))*0.1;


Ê Ê Ê Ê snd = Mix(snd);
Ê Ê Ê Ê snd = FreeVerb.ar(snd, 0.51,10,0.1)*2;
Ê Ê Ê Ê snd = snd *8;
Ê Ê Ê Ê snd = RLPF.ar(snd, SinOsc.kr([0.1,4.2], 0, [1700,480],
[4000,700,5000])/[20.51,20], SinOsc.ar(0.1,1.5*pi)+1.05)/3;
Ê Ê Ê Ê snd = Mix(snd);

Ê Ê Ê Ê snd = MoogFF.ar(snd, SinOsc.kr(0.08, 0.5, cutoff/10, cutoff), 3, 0);

Ê Ê Ê Ê snd = snd * EnvGen.ar(Env.adsr(0.4, 0.01,0.8,0.4), gate, doneAction:2);

Ê Ê Ê Ê snd = [snd , AllpassC.ar(snd, 0.45, 0.05+SinOsc.ar(0.11).range(0,0.045),
0.3)];
Ê Ê Ê Ê snd = snd * amp;
Ê Ê Ê Ê Out.ar(out, snd);

}).add;
)


Pdef(\plop2, Pbind(
Ê Ê Ê Ê \instrument, \ChicagoPad2,
Ê Ê Ê Ê \degree, Pseq([0,4,0,3,1],inf),
Ê Ê Ê Ê \root, Pseq([Pn(2,4),Pn(1,4)],inf),
Ê Ê Ê Ê \freq2, Pkey(\freq)*(3/2) / 2,
Ê Ê Ê Ê \freq3, Pkey(\freq)*(5/6) / 2,
Ê Ê Ê Ê \legato, 0.2,
Ê Ê Ê Ê \cutoff, 4500,
Ê Ê Ê Ê \dur, ÊPseq([Pn(0.25,4),Pn(0.5,4)],inf),
Ê Ê Ê Ê \amp, 0.5
)).play;





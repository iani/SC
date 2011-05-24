/*
Prepering osmo05
Let's start play music

Aris Bezas Tue, 24 May 2011, 18:26
*/

SendSpectrogramData.new
//:Usefull commants
~synthArray = [\pvBuf0, \pvBuf1, \pvBuf2, \pvBuf3, \pvBuf4, \pvBuf5, \pvBuf6, \pvBuf7];
~synthArray do: _.playDef(\pv0);
~synthArray do: _.releaseSynth(1.5);
(1..10) do: { | i | ~synthArray[i].set(\bufnum, i);i.postln}

//:PV start synth
Udef(\pv_diffuser, {
	| out = 0, bufnum = 0, rate = 1, trigger = 1, loop = 1, pos = 0, level = 1, in, chain |
	in =  PlayBuf.ar(1, bufnum, rate, trigger, 0, loop);
	chain = FFT(LocalBuf(2048), in);
	chain = PV_Diffuser(chain, MouseY.kr > 0.5 ); 
	Out.ar(out, 0.5 * IFFT(chain).dup);
     Out.ar(out,Pan2.ar( in, pos,level))
});
\pv_diffuser.playDef;


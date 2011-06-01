/*
Prepering osmo05
Let's start play music:

MySynths.load //	Create class for my Synths
Udef.all 		//	View all Synhs

MyBuffers

Aris Bezas Tue, 24 May 2011, 18:26
*/

//:110525
{ | i | format("mySynths%", i).asSymbol.playDef(\foubuf, [\bufnum, i]) } ! 13;
//:--
{ | i | format("mySynths%", i).asSymbol.set(\rate, 0.2 rrand: 0.3) } ! 13;
//:--
{ | i | format("mySynths%", i).asSymbol.set(\pitchRatio, 0.1 rrand: 2.12) } ! 13;
{ | i | format("mySynths%", i).asSymbol.set(\loop, 0) } ! 13;
//:--
{ | i | format("mySynths%", i).asSymbol.set(\level, 0.0) } ! 13;
//:--
{ | i | format("mySynths%", i).asSymbol.free } ! 13;
//:GUI
w = Window.new("Level",Rect(300,Window.screenBounds.height-300,400,100)).front;
a = Slider(w, Rect(50, 20, 300, 40))
	.value_(0.5)
	.step_(0.005)
	.focus
	.action_({
		{ | i | format("mySynths%", i).asSymbol.set(\level, a.value) } ! 13;
	});
//:Change variables
OF.reverse
//:SendSpectrogramData
SendSpectrogramData.new;
SendSpectrogramData('fft').rate = 0.046
\pv_rectcomb_0.playDef(\pv_rectcomb).rsync({	| s |
		loop {
			//s.set(\lftWidthMul, 400 rrand: 4000);
			//s.set(\numTeeth, 1 rrand: 15);
			//s.set(\lftPhasePhase, 0 rrand: 15);
			//s.set(\lftWidthPhase, 0 rrand: 15);

			
			0.05.wait;
		}
	});
\pv_rectcomb_0.playDef(\pv_rectcombSin);	
\pv_rectcomb_0.set(\numTeeth, 20);	
\pv_rectcomb_0.set(\sinPhaseFreq, 0.1);	
\pv_rectcomb_0.set(\sinWidthFreq, 0);	
\pv_rectcomb_0.set(\sinPhasePhase, 0);
\pv_rectcomb_0.set(\sinWidthPhase, 1);
\pv_rectcomb_0.set(\sinPhaseMul, 0);
\pv_rectcomb_0.set(\sinWidthMul, 0);
\pv_rectcomb_0.set(\level, 0)
\pv_rectcomb_0.free
//:Usefull commants
~synthArray = [\pvBuf0, \pvBuf1, \pvBuf2, \pvBuf3, \pvBuf4, \pvBuf5, \pvBuf6, \pvBuf7];
~synthArray do: _.playDef(\pv0);
~synthArray do: _.releaseSynth(1.5);
(1..10) do: { | i | ~synthArray[i].set(\bufnum, i);i.postln}

//:PV start synth
Udef(\pv_diffuser, {
	| out = 0, bufnum = 11, rate = 1, trigger = 1, loop = 1, pos = 0, level = 1, in, chain |
	in =  PlayBuf.ar(1, bufnum, rate, trigger, 0, loop);
	chain = FFT(LocalBuf(2048), in);
	chain = PV_Diffuser(chain, MouseY.kr > 0.5 ); 
	Out.ar(out, 0.5 * IFFT(chain).dup);
     Out.ar(out,Pan2.ar( in, pos,level))
});
\pv_diffuser.playDef;
\pv_diffuser.set(\bufnum, 1);
\pv_diffuser.free

//:PV BrickWall
Udef(\pv_brickwall, {
	| out = 0, bufnum = 11, rate = 1, trigger = 1, loop = 1, pos = 0, level = 1, in, chain, wipeFreq =  1 |
	in =  PlayBuf.ar(1, bufnum, rate, trigger, 0, loop);
	chain = FFT(LocalBuf(2048), in);
	chain = PV_BrickWall(chain, SinOsc.kr(wipeFreq)); 
     Out.ar(out,Pan2.ar( IFFT(chain).dup, pos,level))
});
//:Play

\pv_brickwall.playDef;
\pv_brickwall.set(\wipeFreq, 0.1);
\pv_brickwall.releaseSynth(2)
\pv_brickwall.free


\pv_brickwall_0.playDef(\pv_brickwall);
\pv_brickwall_1.playDef(\pv_brickwall);
\pv_brickwall_2.playDef(\pv_brickwall);

//:PV_RectComb
//:Create Synths
Udef(\pv_rectcomb, {
	|out =0, in, chain, pos = 0, level = 1, numTeeth = 8, lftPhaseFreq = 0.097, lftPhaseMul = 0.4, lftWidthMul = -0.5, lftWidthFreq = 0.24, lftPhasePhase = 0, lftWidthPhase = 0 | 
	in = WhiteNoise.ar(0.2);
	chain = FFT(LocalBuf(2048), in);
	chain = PV_RectComb(chain, 
						numTeeth, 
						SinOsc.kr(lftPhaseFreq, lftPhasePhase, lftPhaseMul, 0.5), 
						SinOsc.kr(lftWidthFreq, lftWidthPhase, lftWidthMul, 0.5)
	);
     Out.ar(out,Pan2.ar( IFFT(chain).dup, pos,level));	 

});
//:Play
2.wait;
\pv_rectcomb_0.playDef(\pv_rectcomb);
\pv_rectcomb_0.set(\numTeeth, 2);
\pv_rectcomb_0.set(\lftPhaseFreq, 1);
\pv_rectcomb_0.set(\lftWidthMul, 0.5);
\pv_rectcomb_0.set(\lftPhaseMul, 1);


	
//: Examples from documentation
(
{
	var in, chain;
	in = WhiteNoise.ar(0.2);
	chain = FFT(LocalBuf(2048), in);
	chain = PV_RectComb(chain, 8, LFTri.kr(0.097, 0, 0.4, 0.5), 
		LFTri.kr(0.24, 0, -0.5, 0.5)); 
	IFFT(chain).dup;
}.play;
)

(
{	var in, chain;
	in = WhiteNoise.ar(0.2);
	chain = FFT(LocalBuf(2048), in);
	chain = PV_RectComb(chain,  MouseX.kr(0, 32), MouseY.kr, 0.2); 
	IFFT(chain).dup;
}.play;
)

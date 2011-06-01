/*
Prepering osmo05
Let's start play music:

MySynths.load //	Create class for my Synths
Udef.all 		//	View all Synhs

MyBuffers

Chain
Aris Bezas Tue, 24 May 2011, 18:26
*/
//:OSC Responders
SpectroTouchOSC.makeResponders;
//: My Synths
MySynths.sendToServer;
//: My Buffers
//MyBuffers.osmoFull;
MyBuffers.seals;
//MyBuffers.sketch;
BufferResource.postNames;
//:SendSpectrogramData
SendSpectrogramData.new;
//SendSpectrogramData('fft').free;
s.boot;

//:Mirrow mode
OF.int("ofSpectro", 1);
OF.int("mirrorMode", 4);
//:Sound In
\pure_clarinet.playDef;
//:PV_BinSfift
{ | i | format("mySynths%", i).asSymbol.playDef(\pv_binShift, 
	[\bufnum, \elephant_seal2.buffer, \rate, i * 0.001 + 1]) 
} ! 10;
//:fouBuf Definition
{ | i | format("mySynths%", i).asSymbol.free;} ! 10;
0.1.wait;
{ | i | format("mySynths%", i).asSymbol.playDef(\foubuf, 
	[\bufnum, \elephant_seal2.buffer, \rate, i * 0.001 + 1]) 
} ! 10;
//:Buffer edit
{ | i | format("mySynths%", i).asSymbol.playDef(\pv_binShift, [\bufnum, (0 rrand:42).round]) } ! 10;
//:Rate play
{ | i | format("mySynths%", i).asSymbol.set(\rate, 1) } ! 10;
//:Level
{ | i | format("mySynths%", i).asSymbol.set(\level, 0) } ! 10;
//:Buffer
{ | i | format("mySynths%", i).asSymbol.set(\bufnum, 5 } ! 10;
//:
{ | i | format("mySynths%", i).asSymbol.set(\stretch, 100 rrand:200) } ! 10;
//:--
{ | i | format("mySynths%", i).asSymbol.set(\loop, 1) } ! 10;
//:--
{ | i | format("mySynths%", i).asSymbol.set(\level, 1.5) } ! 10;
//:--
{ | i | format("mySynths%", i).asSymbol.free } ! 10;
//:Big Triangels
{
	500.do{|g|
	{ | i | format("mySynths%", i).asSymbol.set(\stretch, g) } ! 10;
	0.01.wait;	
	};
	500.do{|k|
	{ | i | format("mySynths%", i).asSymbol.set(\stretch, 500-k) } ! 10;
	0.01.wait	;
	};
}.fork
//:Small Ones
{
	5.do{
	150.do{|g|
	{ | i | format("mySynths%", i).asSymbol.set(\stretch, g) } ! 10;
	0.001.wait;	
	};	
	150.do{|k|
	{ | i | format("mySynths%", i).asSymbol.set(\stretch, 5*(150-k)) } ! 10;
	0.001.wait	
	};
	};
}.fork
//:Fade Out
{
	100.do{ |g|
	{ | i | format("mySynths%", i).asSymbol.set(\level, 1 - (g/99)) } ! 10;
	g.postln;
	0.1.wait;	
	};
	0.1.wait;	
	{ | i | format("mySynths%", i).asSymbol.free;} ! 10;
}.fork
//:--
{
{SinOsc.ar(2000*SinOsc.kr(XLine.kr(0.001,0.5,20, doneAction:2)), mul:0.5)}.play;
1.wait;
{SinOsc.ar(4000*SinOsc.kr(XLine.kr(0.001,0.5,20, doneAction:2)), mul:0.5)}.play;
1.wait;
{SinOsc.ar(8000*SinOsc.kr(XLine.kr(0.001,0.5,20, doneAction:2)), mul:0.5)}.play;
10.wait;
{SinOsc.ar(8000*SinOsc.kr(XLine.kr(0.5,0.001,20, doneAction:2)), mul:0.5)}.play;
1.wait;
{SinOsc.ar(4000*SinOsc.kr(XLine.kr(0.5,0.001,20, doneAction:2)), mul:0.5)}.play;
1.wait;
{SinOsc.ar(2000*SinOsc.kr(XLine.kr(0.5,0.001,20, doneAction:2)), mul:0.5)}.play;
}.fork
//:WidthFreq
\pv_rectcomb_0.set(\sinWidthFreq, 0.1);		
//:PhaseFreq
\pv_rectcomb_0.set(\sinPhaseFreq, 0.2);		

//:--
{
	\pv_rectcomb_0.playDef(\pv_rectcombSin);	
	0.9.wait;
	\pv_rectcomb_0.set(\sinWidthFreq, 0);		
}.fork
//:--
{	
	50.do { |i|
		\pv_rectcomb_0.set(\numTeeth, i);
		0.1.wait;			
	};
	50.do { |i|
		\pv_rectcomb_0.set(\numTeeth, 50-i);
		0.1.wait;			
	}
	
}.fork
//:PhaseFreq
{
	50.do { |i|
		\pv_rectcomb_0.set(\sinPhaseFreq, i/50);
		0.1.wait;			
	};
	50.do { |i|
		\pv_rectcomb_0.set(\sinPhaseFreq, 1-(50-i)/50);
		0.1.wait;			
	}
}.fork
//:Fade out
{
	50.do { |i|
		\pv_rectcomb_0.set(\level, 1-((i+1)/50));
		0.1.wait;			
	};	
}.fork

//:110525
{ | i | format("mySynths%", i).asSymbol.playDef(\foubuf, [\bufnum, i]) } ! 13;
//:--
{ | i | format("mySynths%", i).asSymbol.set(\rate, 0.1 rrand: 10.3) } ! 13;
//:--
{ | i | format("mySynths%", i).asSymbol.set(\pitchRatio, 0.1 rrand: 2.12) } ! 13;
//:--
{ | i | format("mySynths%", i).asSymbol.set(\bufnum, (0 rrand: 30).round) } ! 13;
//:--
{ | i | format("mySynths%", i).asSymbol.set(\loop, 1) } ! 13;
//:--
{ | i | format("mySynths%", i).asSymbol.set(\level, 1.5) } ! 13;
//:--
{ | i | format("mySynths%", i).asSymbol.free } ! 13;
//:GUI
w = Window.new("Level",Rect(300,Window.screenBounds.height-300,400,100)).front;
a = Slider(w, Rect(50, 20, 300, 40))
	.value_(1)
	.step_(0.005)
	.focus
	.action_({
		{ | i | format("mySynths%", i).asSymbol.set(\level, 2*a.value) } ! 13;
	});
//:Change variables
OF.reverse;
OF.int("mirrorMode", 4);
OF.float("spectroRed", 1);
OF.float("spectroGreen", 1);
OF.float("spectroBlue", 1);
OF.float("spectroRedUp", 1);
OF.float("spectroGreenUp", 1);
OF.float("spectroBlueUp", 1);

//:--
{
	200.do { |i|
		OF.float("spectroRedUp", i/200);
		OF.float("spectXXroRedDown", i/200);
		OF.float("spectroGreenUp", i/200);
		OF.float("spectroGreenDown", i/200);
		OF.float("spectroBlueUp", i/200);
		OF.float("spectroBlueDown", i/200);
		0.01.wait;

	}
}.fork

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
\pv_rectcomb_0.set(\numTeeth, 1);	
\pv_rectcomb_0.set(\sinPhaseFreq, 0.1);	
\pv_rectcomb_0.set(\sinWidthFreq, 0);	
\pv_rectcomb_0.set(\sinPhasePhase, 0);
\pv_rectcomb_0.set(\sinWidthPhase, 1);
\pv_rectcomb_0.set(\sinPhaseMul, 0);
\pv_rectcomb_0.set(\sinWidthMul, 0);
\pv_rectcomb_0.set(\level, 1)
\pv_rectcomb_0.free
//:Usefull commants
~synthArray = [\pvBuf0, \pvBuf1, \pvBuf2, \pvBuf3, \pvBuf4, \pvBuf5, \pvBuf6, \pvBuf7];
~synthArray do: _.playDef(\pv0);
~synthArray do: _.releaseSynth(1.5);
(1..10) do: { | i | ~synthArray[i].set(\bufnum, i);i.postln}


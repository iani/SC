/*
Jose Padovani

110618 igoumeninja
Aris Bezas
Amplitude
*/
//:SynthDef
SynthDef('padovani', {	|sawFreq = 1, trigDur = 0.05, triggerFreq = 10, roomSize = 2, sizeGrain = 1, signalOutput = 10|
	var in, singal;
	p= Trig.ar(Saw.ar(sawFreq),trigDur);
	y= SinOsc.ar(p*triggerFreq);
	z= SinOsc.ar(p);
	in = GrainIn.ar(
			2, 			//channelNum
			y, 			//trigger
			y/sizeGrain,	//duration-the size of the grain
			z,			//input to granulate
			p*z			//pan
			-1			//buf num 
		);
	singal = GVerb.ar(in, roomSize )/signalOutput;
	Out.ar(0, singal);
}).send(s);
//:Play the Synth
{ | i | format("mySynths%", i).asSymbol.playDef(\padovani, [\trigDur, 0.04]) } ! 5;
//:trigDur beetwin 2~0.05
{ | i | format("mySynths%", i).asSymbol.set(\trigDur, 0.1*i ) } ! 5;
//:triggerFreq
{ | i | format("mySynths%", i).asSymbol.set(\triggerFreq, i*2 ) } ! 5;
//:
{ | i | format("mySynths%", i).asSymbol.set(\trigDur, 0.05 ) } ! 5;
{ | i | format("mySynths%", i).asSymbol.set(\roomSize, 2) } ! 5;
{ | i | format("mySynths%", i).asSymbol.set(\sizeGrain, 0.05) } ! 5;
{ | i | format("mySynths%", i).asSymbol.set(\sawFreq, 5) } ! 5;
{ | i | format("mySynths%", i).asSymbol.set(\triggerFreq, 80) } ! 5;
{ | i | format("mySynths%", i).asSymbol.set(\signalOutput, 2) } ! 5;

{ | i | format("mySynths%", i).asSymbol.set(\triggerFreq, i*100 + 100 ) } ! 5;
{ | i | format("mySynths%", i).asSymbol.set(\trigDur, 0.05*i +1 ) } ! 5;
{ | i | format("mySynths%", i).asSymbol.set(\sizeGrain, i*5 + 50) } ! 5;
//:Precieve
Preceive(
	1 	-> 	{	},
	\phase1 	-> 	{	{ | i | format("mySynths%", i).asSymbol.set(\triggerFreq, i*100 + 100 ) } ! 5; },
	\b 	-> 	{	},
	10 	-> 	{ 	},
	3 	-> 	{ 	}
).play;
Psend
Posc
//:Send keys
NetAddr.localAddr.sendMsg(\phase1);
//:OSCResponders


~autSsinASpec = ControlSpec(0.001, 112.1, \lin);
~autSsinBSpec = ControlSpec(0.001, 112.1, \lin);

~autSxy =OSCresponderNode(nil,'/harmP/autSxy', {|t,r,m| var ni1, n2;
n1 (m[1]);
n2 (m[2]);

~autS.set(\sinA, ~autSsinASpec.map(n1));
~autS.set(\sinB, ~autSsinBSpec.map(n2));

}).add;



~trigDurSpec = ControlSpec(0.001, 112.1, \lin);

OSCresponderNode(n, '/trigDur', {|t,r,msg| 
	 { | i | format("mySynths%", i).asSymbol.set(\trigDur, ~trigDurSpec.map(msg[1])*i ) } ! 5;}
	 ).add;

OSCresponderNode(n, '/trigDur', {|t,r,msg| { | i | format("mySynths%", i).asSymbol.set(\trigDur, msg[1]*i ) } ! 5;}).add;

~takis = 2;
~takis.asSpec.map(2).postln;
//:Old fasion 
x = Synth(\padovani).play;
y = Synth(\padovani).play;
//:Set parameters
x.set(\sawFreq, 20);
x.set(\trigDur, 1);
y.set(\trigDur, 1);
x.set(\triggerFreq, 800);
x.set(\roomSize, 1);
x.set(\roomSize, 10);
x.release;
y.release;


//: Omer code
Jose Padovani
play{
	x=165;
	b=SinOsc;
	p=Trig.ar(Saw.ar(x),1);
	y=b.ar(p*x);
	z=b.ar(p);
	(GVerb.ar(
		GrainIn.ar(
			2,
			y,
			y/2,
			z,
			p*z,
			-1
		),
		9
	))/9
}//basso gettato #SuperCollider


(
// set them from outside later:
SynthDef('padovani', {
	var in, sinyal;
	//x=MouseX.kr(1, 165);
	//b= SinOsc;
	p= Trig.ar(Saw.ar(MouseX.kr(1, 165)),0.5);
	y= SinOsc.ar(p*MouseY.kr(1, 165));
	z= SinOsc.ar(p);
	in = GrainIn.ar(
			2, 	//channelNum
			y, 	//trigger
			y/2,	//duration-the size of the grain
			z,	//input to granulate
			p*z,	//pan
			-1	//buf num containing a singal to use for the grain envelope. -1 uses a built-in Hanning envelope.
		);
	sinyal = GVerb.ar(
		in, 
		4 //room
	)/19;
	Out.ar(0, sinyal);
}).play;
)
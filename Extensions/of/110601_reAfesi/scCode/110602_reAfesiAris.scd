/*
Prepering Aferesi performance

Aris Bezas Wed, 01 June 2011, 23:24
*/
["af1","af2","af3","af4","af5","af6","af7"].choose
(0..60).choose
//:mirrow effect
OF.img(1,0,0,1280,1024)
OF.int("sketch",1)
//:Many images
k = {
	~delayTime = 0.21;
	~countX = 0; 
	~countY = 0;
	~screenWidth = 1280;
	~screenHeight = 1024;
	~fotoWidth = ~fotoHeight = ~screenWidth/10;
	~fotoHeight = ~fotoWidth;
	
	inf.do{ |i|
				
		OF.img((0..60).choose.round, ~countX, ~countY, ~fotoWidth, ~fotoHeight);
		~countX = ~countX + ~fotoWidth;
		if (~countX>~screenWidth, {~countX = 0;~countY = ~countY + ~fotoHeight});
		if (~countY>~screenHeight, {~countY = 0;});
		~delayTime.wait;
	}
	
}.fork 
//:--

OF.int("yPosImage", 100)
OF.int("xPosImage", 100)
OF.int("wImg", 100 rrand: 500)
OF.int("mirrorMode",0);

//:Play with width and height
k = {
	inf.do	{
	OF.int("wImg", 100 rrand: 1500);
	OF.int("hImg", 100 rrand: 1500);			
	0.3.wait
	}
	
}.fork;
//:Play with width and height
k = {
	inf.do	{ |i|
	OF.int("wImg", i);
	OF.int("hImg", i);			
	0.3.wait
	}
	
}.fork;

//:Precieve
Preceive(
	'af1' -> {
		OF.img("af1",0,0,100,100);
		},
	'af2' -> {
		OF.img("af2");
		},
	'af3' -> {
		OF.img("af3");
		},
	'af4' -> {
		OF.img("af4");
		},
	'af5' -> {
		OF.img("af5");
		},
	'af6' -> {
		OF.img("af6");
		},
	'af7' -> {
		OF.img("af7");
		},
	'2' -> {
		~klk = Synth("over", [
			\atk, 0.5,
			\sus, 1,
			\rel, 4,
			\pan, 1.0.rand2,
			\freqs,  {6000.0.linrand+80}.dup(12),
			\rings, {0.1.rrand(3)}.dup(12)
		]);
		}
).play;


//:Play Pdef
Pdef(\klank).play;	
Pdef(\fullScreen).play;	
//:Fill the screen with a foto
Pdef(\fullScreen, Posc(
		\msg, Pseq([['img', 'af1",]], inf),
		\dur, Pseq([~duyekDur], inf),
		\dest,  [NetAddr.localAddr, NetAddr("localhost", 12345)]
		)
	);
//:Duyek
a = Pdef(\klank, Posc(
		\msg, Pseq([\af1, \af2, \af3, \af4, \af5, \af6, \af7], inf),
		\dur, Pseq([~duyekDur]*0.1, inf),
		\dest,  [NetAddr.localAddr, NetAddr("localhost", 12345)]
		)
	);
//:duyekKudDur
Pdef(\klank, Posc(
		\msg, Pseq([\af1, \af2], inf),
		\dur, Pseq([~duyekKudDur]*0.5, inf),
		\dest,  [NetAddr.localAddr, NetAddr("localhost", 12345)]
		)
	);

//:Posc with predefine TempoClock
Pdef(\klank, Posc(
		\msg, Pseq([\1, \2, \3, \4], inf),
		\dur, Pseq([~duyekDur]*4, inf),
		\dest,  [NetAddr.localAddr, NetAddr("localhost", 12345)]
		)
	).play(~clock1);
//:Other

Posc.play(
	[0,1,2,3,4],
	\dur, 2
	
);
//:--



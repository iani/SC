//:Cut Motion
// Aris Bezas Thu, 28 July 2011, 14:24
//:----------
//:Define Global Variables
~numImg = 91;
~width = 1280;
~height = 1024;
~delay = 0.01;
//:Format Func
f = { | int |
	var length, string;
	string = "0000".copy;
	length = string.size - 1;
	int.asString.reverse do: { | i, count | string[length - count] = i };
	string.postln;
};
//:Load Pixelation Images
{
	96.do {	|i|
		OF.loadImage(i, format("/Users/fou/images/pixxilation/%.JPG", f.(i)));
		0.1.wait;
	}
}.fork;
//:Load RVGMES Images
{
	~numImg.do {	|i|
		OF.loadImage(i, format("/Users/fou/images/rogmes/set1/%.jpg", f.(i)));
		0.1.wait;
	}
}.fork;
//:Play a Demo
{
	inf.do {
		90.do { |i|
			OF.img(i.asInteger,0,0,~width, ~height);
			0.01.wait;
		};
	};
}.fork;
//:Random Rotate Images
{
	inf.do{
			OF.img(1,rrand(0,1280),rrand(0,780),rrand(400,1280),rrand(400,780),rrand(0,360),rrand(0,360),rrand(0,360));
	0.005.wait;
	}
}.fork;
//:Random smal Rotate Images
{
	inf.do{
			OF.img(rrand(0,90).asInteger,rrand(0,1280),rrand(0,780),rrand(100,280),rrand(100,280),rrand(0,360),rrand(0,360),rrand(0,360));
	0.005.wait;
	}
}.fork;

//:Rotate Images
{
	inf.do{|i|
		OF.img(1, ~width/2, ~height/2, 400,400, i,i,0);
		0.005.wait;
	}
}.fork;

//:Control delay

{
	inf.do {
		95.do { |i|
			OF.img(i.asInteger,0,0,~width, ~height);
			~delay.wait;
		};
	};
}.fork

//:Random
{
	inf.do {
		OF.img(rrand(0,95).asInteger,0,0,~width, ~height);
		~delay.wait;
	};
}.fork;
//:Random size
{
	inf.do {
		OF.img(rrand(0,95).asInteger,rrand(0,1280),rrand(0,780),rrand(400,1280),rrand(400,780));
		~delay.wait;
	};
}.fork

//:Task-CutMotion
~delay = 0.01;
~cutMotion = Task({
	inf.do {
		10.do { |i|
			OF.img(i,0,0,1280,720);
			~delay.wait;
		};
	};
});
//:Play Task
~cutMotion.play;
//:Release Task
~cutMotion.stop;



//:-----
/*
Prepering Aferesi performance

Aris Bezas Wed, 01 June 2011, 23:24
*/

OF.loadImage(0, "/Users/fou/images/pixxilation/0000.JPG");

OF.loadImage(1, "/Users/fou/images/pixxilation/0011.JPG");

OF.img(1,0,0,1280,720);

OF.img(0,0,0,200,200);


OF.img(1,0,0,600,600);
OF.img(0,0,0,800,600);
//:mirrow effect	
OF.int("sketch",1);
OF.int("pixelate",1);
OF.int("alphaSketch",3);
OF.int("edgeDetection",1);
//:Collage
k = {
	~delayTime = 0.005;
	~countX = 0; 
	~countY = 0;
	~fotoWidth = ~fotoHeight = ~width/20;
	~fotoHeight = ~fotoWidth;
	inf.do{
	95.do{ |i|
		OF.img(i, ~countX, ~countY, ~fotoWidth, ~fotoHeight);
		~countX = ~countX + ~fotoWidth;
		if (~countX>~width, {~countX = 0;~countY = ~countY + ~fotoHeight});
		if (~countY>~height, {~countY = 0;});
		~delayTime.wait;
	}
	}
	
}.fork 
//:Small random position
k = {
	~delayTime = 0.02;
	~countX = 0; 
	~countY = 0;
	~screenWidth = 1280;
	~screenHeight = 720;
	~fotoWidth = ~fotoHeight = ~screenWidth/20;
	~fotoHeight = ~fotoWidth;
	inf.do{
	95.do{ |i|
		i.postln;		
		OF.img(95.rand.asInteger, ~screenWidth.rand,  ~screenHeight.rand, 100, 100*720/1280);
		~delayTime.wait;
	}
	}
	
}.fork 


//:Small random position  random size
k = {
	OF.img(0,0,0,1280,720);
	~delayTime = 0.02;
	~countX = 0; 
	~countY = 0;
	~screenWidth = 1280;
	~screenHeight = 720;
	~fotoWidth = ~fotoHeight = ~screenWidth/20;
	~fotoHeight = ~fotoWidth;
	inf.do{
	186.do{ |i|
		i.postln;		
		OF.img(185.rand.asInteger, ~screenWidth.rand,  ~screenHeight.rand, 1000.rand, 600.rand);
		~delayTime.wait;
	}
	}
	
}.fork 

//:Center Slide show
~delayTime = 0.01;
k = {
	~delayTime = 0.05;
	~countX = 0; 
	~countY = 0;
	~screenWidth = 1280;
	~screenHeight = 780;
	~fotoWidth = ~fotoHeight = ~screenWidth/20;
	~fotoHeight = ~fotoWidth;
	inf.do{
		186.do{ |i|
			186.postln;		
			OF.img(i, 0,0, ~screenWidth, ~screenHeight);
			~delayTime.wait;
		}
	}
	
}.fork 
//:Random Slide show
~delayTime = 0.01;
k = {
	~delayTime = 0.1;
	~countX = 0; 
	~countY = 0;
	~screenWidth = 1280;
	~screenHeight = 780;
	~fotoWidth = ~fotoHeight = ~screenWidth/20;
	~fotoHeight = ~fotoWidth;
	inf.do{
		186.do{ |i|
			186.postln;		
			OF.img(186.rand.asInteger, 0,0, ~screenWidth, ~screenHeight);
			~delayTime.wait;
		}
	}
	
}.fork 

//:Many images
k = {
	~delayTime = 0.01;
	~countX = 0; 
	~countY = 0;
	~screenWidth = 1280;
	~screenHeight = 780;
	~fotoWidth = ~fotoHeight = ~screenWidth/20;
	~fotoHeight = ~fotoWidth;
	
	inf.do{ |i|
				
		OF.img((0..200).choose.round, ~countX, ~countY, ~fotoWidth, ~fotoHeight);
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
//:Create Synth from function
a = SynthDef(\synthFunc, { |delayTime = 0.01, countX = 0, countY = 0, screenWidth = 1280, screenHeight = 1024, fotoWidth, fotoHeight|
	
	fotoWidth = screenWidth/20;
	fotoHeight = screenWidth/20;
	10.do{ |i|
				
//		OF.img((0..60).choose.round, countX, countY, fotoWidth, fotoHeight);
//		countX = countX + fotoWidth;
//		if (countX>screenWidth, {countX = 0;countY = countY + fotoHeight});
//		if (countY>screenHeight, {countY = 0;});
		//delayTime.wait;
	}

	
	
	}).send(s);

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
		\msg, Pseq([['img', 0, 0, 0, 600,600]], inf),
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
10.do{OF.img(rrand(0,95).asInteger,rrand(0,1280),rrand(0,780),rrand(400,1280),rrand(400,780));}
//:--
OF.loadImage(1,"/Users/fou/images/pixxilation/0000.JPG");
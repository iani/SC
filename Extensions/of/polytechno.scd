/*
Prepering osmo05
Let's start play music:

OF.int("mirrorMode", 0);
OF.int("intensity", 23);
ttAris Bezas Tue, 24 May 2011, 18:26
*/
//:SendSpectrogramData
SendSpectrogramData.new;
//:Sound In
\pure_clarinet.playDef;
//:Sound In free
\pure_clarinet.free;
//:Empty
"NOTHING".postln;
//:iPod
~addr = NetAddr("127.0.0.1", 12345);
// ~addr = NetAddr("169.254.198.56", 12345);
Event.addEventType(\osc, { ~dest.sendBundle(~latency, ~msg); });

// page1
OSCresponder.new(nil, "/1/r8", { arg time, resp, msg; OF.rgb("r8", msg[1].round);} ).add; 
OSCresponder.new(nil, "/1/g8", { arg time, resp, msg; OF.rgb("g8", msg[1].round);} ).add; 
OSCresponder.new(nil, "/1/b8", { arg time, resp, msg; OF.rgb("b8", msg[1].round);} ).add; 
OSCresponder.new(nil, "/1/a8", { arg time, resp, msg; OF.rgb("a8", msg[1].round);} ).add; 
OSCresponder.new(nil, "/1/r7", { arg time, resp, msg; OF.rgb("r7", msg[1].round);} ).add; 
OSCresponder.new(nil, "/1/g7", { arg time, resp, msg; OF.rgb("g7", msg[1].round);} ).add; 
OSCresponder.new(nil, "/1/b7", { arg time, resp, msg; OF.rgb("b7", msg[1].round);} ).add; 
OSCresponder.new(nil, "/1/a7", { arg time, resp, msg; OF.rgb("a7", msg[1].round);} ).add; 
OSCresponder.new(nil, "/1/r6", { arg time, resp, msg; OF.rgb("r6", msg[1].round);} ).add; 
OSCresponder.new(nil, "/1/g6", { arg time, resp, msg; OF.rgb("g6", msg[1].round);} ).add; 
OSCresponder.new(nil, "/1/b6", { arg time, resp, msg; OF.rgb("b6", msg[1].round);} ).add; 
OSCresponder.new(nil, "/1/a6", { arg time, resp, msg; OF.rgb("a6", msg[1].round);} ).add; 
// page2
OSCresponderNode.new(nil, "/2/setupSound", { | time, resp, msg| ~setupSoundTask.start;}).add; 
OSCresponderNode.new(nil, "/2/freeSound", { | time, resp, msg| ~soundSynth.free;}).add; 
OSCresponderNode.new(nil, "/2/viewSoundChanels", { | time, resp, msg| OF.interactWithSound("activate", msg[1]);}).add; 
OSCresponder.new(nil, "/2/soundLines", { arg time, resp, msg; OF.interactWithSound("glBeginType", msg[1].round);} ).add; 
OSCresponder.new(nil, "/2/numSoundSketches", { arg time, resp, msg; OF.interactWithSound("numSoundSketches", msg[1].round);} ).add; 
OSCresponder.new(nil, "/2/maxFreqIn", { arg time, resp, msg; OF.interactWithSound("maxFreqIn", msg[1].round);} ).add; 
OSCresponder.new(nil, "/2/maxAmpIn", { arg time, resp, msg; OF.interactWithSound("maxAmpIn", msg[1]);} ).add; 
OSCresponder.new(nil, "/2/elasticity", { arg time, resp, msg; OF.interactWithSound("maxSoundElasticity", msg[1]);} ).add; 
OSCresponder.new(nil, "/2/backtrans", { arg time, resp, msg;  OF.rgb("a8", msg[1].round);} ).add; 
OSCresponder.new(nil, "/2/soundtrans", { arg time, resp, msg;  OF.rgb("a6", msg[1].round);} ).add; 
OSCresponder.new(nil, "/2/soundBW", { arg time, resp, msg; 
	if (msg[1] == 0)	{~soundBWTask.stop;OF.rgb("sound",255,255,255,20);OF.rgb("background",0,0,0,0);};
	if (msg[1] == 1)	{~soundBWTask.start};
} ).add; 
OSCresponder.new(nil, "/2/backBW", { arg time, resp, msg; 
	if (msg[1] == 0)	{~backBWTask.stop;	OF.background(0,0,0);	OF.rgb("sound",255,255,255,25);OF.rgb("background",0,0,0,30);};
	if (msg[1] == 1)	{~backBWTask.start};
} ).add; 
OSCresponder.new(nil, "/2/elastTask", { arg time, resp, msg; 
	if (msg[1] == 0)	{~elastTask.stop};
	if (msg[1] == 1)	{~elastTask.start};
} ).add; 
OSCresponder.new(nil, "/backTask", { arg time, resp, msg; 
	if (msg[1] == 0)	{~backgroundTask.stop;OF.rgb("background",0,0,0,255);OF.rgb("sound",255,255,255,125);	};
	if (msg[1] == 1)	{~backgroundTask.start};
} ).add; 


~setupSoundTask = Task({
					if (not(s.serverRunning)) { s.boot };
					s.doWhenBooted {
						SendAmpFreq.start;
						0.1.wait;
						~soundSynth = Synth("SendAmpPitch");	
					};					
				});
~soundBWTask = Task({
	OF.background(0, 0, 0);
	OF.rgb("a8",0);
	OF.interactWithSound("numSoundSketches", 800);
	OF.interactWithSound("glBeginType", 1);	
	inf.do({
		OF.rgb("sound",255,255,255,20);					
		0.001.wait;	
		OF.rgb("sound",0,0,0,125);
		0.001.wait;	
		OF.rgb("sound",255,255,255,20);					
		0.001.wait;			
	});
});
~backBWTask = Task({
	inf.do({
		OF.rgb("background",0,0,0,255);
		OF.rgb("sound",255,255,255,125);					
		0.001.wait;	
		OF.rgb("background",255,255,255,255);
		OF.rgb("sound",0,0,0,125);
		0.001.wait;	
		OF.rgb("background",0,0,0,255);
		OF.rgb("sound",255,255,255,125);					
		0.001.wait;	
	});
});
~elastTask = Task({
	inf.do({
//		OF.interactWithSound("maxSoundElasticity",0.1);					
//		0.5.wait;	
		OF.interactWithSound("maxSoundElasticity",1);					
		0.3.wait;	
		OF.interactWithSound("maxSoundElasticity",0.01);					
		0.7.wait;			
	});
});

~backgroundTask = Task({
	inf.do({
		OF.rgb("background",0,0,0,255);
		OF.rgb("sound",255,255,255,125);					
		0.001.wait;	
		OF.rgb("background",255,255,255,255);
		OF.rgb("sound",0,0,0,125);
		0.001.wait;	
		OF.rgb("background",0,0,0,255);
		OF.rgb("sound",255,255,255,125);					
		0.001.wait;	
	});
});


				
// page3

OSCresponder.new(nil, "/backgroundBlack", { arg time, resp, msg; OF.background(0,0,0);} ).add; 
OSCresponder.new(nil, "/a8", { arg time, resp, msg; OF.rgb("a8", msg[1].round);} ).add; 
OSCresponderNode.new(nil, "/3/particles", { | time, resp, msg| OF.particle("activate", msg[1]);}).add; 
OSCresponderNode.new(nil, "/3/forceRadius", { | time, resp, msg| OF.particle("forceRadius", msg[1]);}).add; 
OSCresponderNode.new(nil, "/3/particleNeighborhood", { | time, resp, msg| OF.particle("particleNeighborhood", msg[1].round);}).add; 
OSCresponder.new(nil, "/3/r1", { arg time, resp, msg; OF.rgb("r1", msg[1].round);} ).add; 
OSCresponder.new(nil, "/3/g1", { arg time, resp, msg; OF.rgb("g1", msg[1].round);} ).add; 
OSCresponder.new(nil, "/3/b1", { arg time, resp, msg; OF.rgb("b1", msg[1].round);} ).add; 
OSCresponder.new(nil, "/3/a1", { arg time, resp, msg; OF.rgb("a1", msg[1].round);} ).add; 
OSCresponder.new(nil, "/3/r2", { arg time, resp, msg; OF.rgb("r2", msg[1].round);} ).add; 
OSCresponder.new(nil, "/3/g2", { arg time, resp, msg; OF.rgb("g2", msg[1].round);} ).add; 
OSCresponder.new(nil, "/3/b2", { arg time, resp, msg; OF.rgb("b2", msg[1].round);} ).add; 
OSCresponder.new(nil, "/3/a2", { arg time, resp, msg; OF.rgb("a2", msg[1].round);} ).add; 
OSCresponder.new(nil, "/3/conBW", { arg time, resp, msg; OF.rgb("r1", 255*msg[1]);OF.rgb("g1", 255*msg[1]);OF.rgb("b1", 255*msg[1]);} ).add; 
OSCresponder.new(nil, "/particlesBW", { arg time, resp, msg; OF.rgb("r2", 255*msg[1]);OF.rgb("g2", 255*msg[1]);OF.rgb("b2", 255*msg[1]);} ).add; 

OSCresponder.new(nil, "/3/BWtask", { arg time, resp, msg; 
	if (msg[1] == 0)	{~particlesBWTask.stop;OF.rgb("particleConnections",0,0,0);};
	if (msg[1] == 1)	{~particlesBWTask.start};
} ).add; 
OSCresponder.new(nil, "/iPodPush", { arg time, resp, msg; 
	if (msg[1] == 0)	{OF.particle("iPodPush", msg[1])};
	if (msg[1] == 1)	{OF.particle("iPodPush", msg[1])};
} ).add; 

~particlesBWTask = Task({
	inf.do({
		OF.rgb("particleConnections",255,255,255);					
		0.001.wait;	
		OF.rgb("particleConnections",0,0,0);					
		0.001.wait;	
		OF.rgb("particleConnections",255,255,255);					
		0.001.wait;			
	});
});


// page4
OSCresponder.new(nil, "/4/feedback", { arg time, resp, msg; OF.feedback("activate", msg[1]);} ).add; 
OSCresponder.new(nil, "/4/speedXY", { arg time, resp, msg; OF.feedback("speedXY", 10*msg[1], 10*msg[2]);} ).add; 
// page5
OSCresponder.new(nil, "/5/objActivate", { arg time, resp, msg; OF.obj("activate", msg[1]);} ).add; 
OSCresponder.new(nil, "/5/objType/1/1", { arg time, resp, msg; OF.obj("type", 0);} ).add; 
OSCresponder.new(nil, "/5/objType/1/2", { arg time, resp, msg; OF.obj("type", 1);} ).add; 
OSCresponder.new(nil, "/5/objType/1/3", { arg time, resp, msg; OF.obj("type", 7);} ).add; 
OSCresponder.new(nil, "/5/objType/1/4", { arg time, resp, msg; OF.obj("type", 4);} ).add; 
OSCresponder.new(nil, "/5/objType/1/5", { arg time, resp, msg; OF.obj("type", 5);} ).add; 
OSCresponder.new(nil, "/5/distrurbance", { arg time, resp, msg; OF.obj("distrurbance", msg[1]);~disturbance = msg[1];} ).add; 
OSCresponder.new(nil, "/5/viewControl", { arg time, resp, msg; OF.obj("viewControl", msg[1], msg[2]);} ).add; 
OSCresponder.new(nil, "/5/transparence", { arg time, resp, msg; OF.obj("transparence", msg[1]);} ).add; 
OSCresponder.new(nil, "/5/cameraInteraction", { arg time, resp, msg; OF.cameraInteraction("activate", msg[1]);} ).add; 
OSCresponder.new(nil, "/5/backtrans", { arg time, resp, msg;  OF.rgb("a8", msg[1].round);} ).add; 

OSCresponder.new(nil, "/objRotate", { arg time, resp, msg; 
	if (msg[1] == 0)	{~objRotate.stop};
	if (msg[1] == 1)	{~objRotate.start};
} ).add; 
OSCresponder.new(nil, "/objRotateWait", { arg time, resp, msg; ~objRotateWait = msg[1]} ).add; 
OSCresponder.new(nil, "/objType", { arg time, resp, msg; 
	if (msg[1] == 0)	{~objType.stop};
	if (msg[1] == 1)	{~objType.start};
} ).add; 
OSCresponder.new(nil, "/objDisturbance", { arg time, resp, msg; 
	if (msg[1] == 0)	{~objDisturbance.stop};
	if (msg[1] == 1)	{~objDisturbance.start};
} ).add; 
OSCresponder.new(nil, "/objRed", { arg time, resp, msg; 
	if (msg[1] == 0)	{~objRed.stop};
	if (msg[1] == 1)	{~objRed.start};
} ).add; 



~objRotateWait = 0.01;
~objRotate = Task({
	inf.do({
		OF.obj("viewControl", 1200.rand, 1200.rand);
		~objRotateWait.wait;			
	});
});
~objTypeWait = 0.01;
~objType = Task({
	inf.do({
		OF.obj("type", 1);
		~objTypeWait.wait;			
		OF.obj("type", 0);
		(~objTypeWait*4).wait;			
		OF.obj("type", 1);
		~objTypeWait.wait;			
		
	});
});
~disturbance = 10;
~objDisturbance = Task({
	inf.do({
		OF.obj("distrurbance", 0);
		~objTypeWait.wait;			
		OF.obj("distrurbance", ~disturbance);
		(~objTypeWait*4).wait;			
		OF.obj("distrurbance", 0);
		~objTypeWait.wait;			
	});
});
~objRed = Task({
	inf.do({
		OF.rgb("r4", 255);
		OF.rgb("g4", 0);
		OF.rgb("b4", 0);
		~objTypeWait.wait;			
		OF.rgb("r4", 255);
		OF.rgb("g4", 255);
		OF.rgb("b4", 255);
		(~objTypeWait*4).wait;			
	});
});

// 6
// ##########  SKETCH  ##############
OSCresponder.new(nil, "/drawWithPad", { arg time, resp, msg; OF.interactWithSketch("activate", msg[1]);} ).add; 
OSCresponder.new(nil, "/mouseLines", { arg time, resp, msg; OF.interactWithSketch("mouseLines", msg[1]);} ).add; 
OSCresponder.new(nil, "/numMouseSketches", { arg time, resp, msg; OF.interactWithSketch("numMouseSketches", msg[1]);} ).add; 
OSCresponder.new(nil, "/minMouseElasticity", { arg time, resp, msg; OF.interactWithSketch("minMouseElasticity", msg[1]);} ).add; 
OSCresponder.new(nil, "/maxMouseElasticity", { arg time, resp, msg; OF.interactWithSketch("maxMouseElasticity", msg[1]);} ).add; 
OSCresponder.new(nil, "/minMouseDamping", { arg time, resp, msg; OF.interactWithSketch("minMouseDamping", msg[1]);} ).add; 
OSCresponder.new(nil, "/maxMouseDamping", { arg time, resp, msg; OF.interactWithSketch("maxMouseDamping", msg[1]);} ).add; 
OSCresponder.new(nil, "/padXY", { arg time, resp, msg; OF.interactWithSketch("padXY", msg[2], 1200 - msg[1]);} ).add; 
OSCresponder.new(nil, "/sketchPat", { arg time, resp, msg; 
	if (msg[1] == 0)	{Pdef(\sketchPat).stop;};
	if (msg[1] == 1)	{Pdef(\sketchPat).play;};
} ).add; 
OSCresponder.new(nil, "/sketchBWPat", { arg time, resp, msg; 
	if (msg[1] == 0)	{Pdef(\sketchBWPat).stop;};
	if (msg[1] == 1)	{Pdef(\sketchBWPat).play;};
} ).add; 
OSCresponder.new(nil, "/sketchBWPatDur", { arg time, resp, msg; ~sketchBWDur.source = Pseq([msg[1]],inf);} ).add; 


~sketchTuple = PatternProxy( 
			Ptuple(["interactWithSketch", "padXY", Pseq(Array.series(1000, 0, 1).scramble, inf), Pseq(Array.series(200, 10, 1000/200).mirror, inf)] , inf) 
		);
~sketchDur = PatternProxy(Pseq(Array.geom(82, 0.003, 1.05).pyramid(2),inf));		

Pdef(\sketchPat,Pbind(
		\type, \osc, 
		\dur, ~sketchDur,
	 	\dest, ~addr,
		\msg, ~sketchTuple
);
);	

~sketchBWTuple = PatternProxy( 
			Ptuple(["rgb", "sketchBW", Pseq([0, 255], inf), Pseq([0, 255], inf), Pseq([0, 255], inf)]) 
		);
~sketchBWDur = PatternProxy(Pseq([0.1,0.1],inf));		

Pdef(\sketchBWPat,Pbind(
		\type, \osc, 
		\dur, ~sketchBWDur,
	 	\dest, ~addr,
		\msg, ~sketchBWTuple
);
);	



// 7
// ##########   STOP MOTION  ##############
OSCresponder.new(nil, "/stopMotionDur", { arg time, resp, msg; ~stopMotionDur.source = Pseq([msg[1]],inf);} ).add; 
OSCresponder.new(nil, "/stopMotionPat0", { arg time, resp, msg; ~stopMotionDur.source = Pseq(Array.geom(82, 0.003, 1.06),inf);} ).add; 
OSCresponder.new(nil, "/stopMotionPat1", { arg time, resp, msg; ~stopMotionDur.source = Pseq(Array.geom(82, 0.003, 1.06).scramble,inf);} ).add; 
OSCresponder.new(nil, "/stopMotionPat2", { arg time, resp, msg; ~stopMotionDur.source = Pseq(Array.geom(82, 0.003, 1.06).reverse,inf);} ).add; 
OSCresponder.new(nil, "/stopMotionPat3", { arg time, resp, msg; ~stopMotionDur.source = Pseq(Array.geom(82, 0.003, 1.06).mirror,inf);} ).add; 
OSCresponder.new(nil, "/stopMotionPat4", { arg time, resp, msg; ~stopMotionDur.source = Pseq(Array.geom(82, 0.003, 1.05).pyramid(2),inf);} ).add; 
OSCresponder.new(nil, "/stopMotionPat5", { arg time, resp, msg; ~stopMotionDur.source = Pseq(Array.series(82, 0.003, 1.06),inf);} ).add; 
OSCresponder.new(nil, "/stopMotionPat6", { arg time, resp, msg; ~stopMotionDur.source = Pseq(Array.series(82, 0.003, 1.07).shutter(13),inf);} ).add; 
OSCresponder.new(nil, "/antPat", { arg time, resp, msg; 	if (msg[1] == 0)	{Pdef(\pngPattern_1).stop};			if (msg[1] == 1)	{Pdef(\pngPattern_1).play};			}).add; 
OSCresponder.new(nil, "/stopMotion", { arg time, resp, msg; 	if (msg[1] == 0)	{Pdef(\stopMotion).stop};			if (msg[1] == 1)	{Pdef(\stopMotion).play};			} ).add; 
OSCresponderNode.new(nil, "/sketchBW", { arg time, resp, msg; 	if (msg[1] == 0)	{OF.rgb("r7",255);OF.rgb("g7",255);OF.rgb("b7",255)};	if (msg[1] == 1)	{OF.rgb("r7",0);OF.rgb("g7",0);OF.rgb("b7",0);};	}).add; 

Pdef(\pngPattern_1, Pbind(
		\type, \osc, 
		\dur, Pseq([1, 2.5, 0.5]/4,inf),
	 	\dest, ~addr,
		\msg, Ptuple(["image", "ant",
			Pseq([0.1, 0.6, 0.9], inf)
		], 1)
	)
);
~stopMotionDur = PatternProxy(Pseq([0.08],inf));
(
Pdef(\stopMotion,Pbind(
		\type, \osc, 
		\dur, ~stopMotionDur,
	 	\dest, ~addr,
		\msg, Ptuple(["image", "stopMotion", Pseq(Array.series(83, 0, 1), inf)], 1)

)
);
);			


//  ##########   IPERATOU  ##############
OSCresponder.new(nil, "/iperatouShoot", { arg time, resp, msg; OF.background(0,0,0);OF.effect("iperatou","shoot");} ).add; 
OSCresponder.new(nil, "/iperatouCharTask", { arg time, resp, msg; 
	if (msg[1] == 0)	{~iperatouCharTask.stop};
	if (msg[1] == 1)	{~iperatouCharTask.start};
} ).add; 
OSCresponder.new(nil, "/iperatouCharTaskWait", { arg time, resp, msg; ~iperatouCharTaskWait = msg[1]} ).add; 

OSCresponder.new(nil, "/iperatouShootPat", { arg time, resp, msg; 
	if (msg[1] == 0)	{Pdef(\iperatouShootPat).stop};
	if (msg[1] == 1)	{Pdef(\iperatouShootPat).play};
} ).add; 
OSCresponder.new(nil, "/iperatouShootPat0", { arg time, resp, msg; ~stopMotionDur.source = Pseq(Array.series(10, 0.003, 1.07).shutter(13),inf);} ).add; 
OSCresponder.new(nil, "/backgroundErase", { arg time, resp, msg; 
	if (msg[1] == 0)	{Pdef(\backgroundErase).stop;};
	if (msg[1] == 1)	{Pdef(\backgroundErase).play;};
} ).add; 

~iperatouCharTaskWait = 1;
~iperatouCharTask = Task({
	inf.do({
		OF.renderString("string",["~","!","@","#","$","%","^","*","(",")","-","_","+","=","[","]","{","}","|",";",":","<",".",">","/","?"].choose);	
		~iperatouCharTaskWait.wait;	
	});
});

~iperatouDur = PatternProxy(Pseq([0.08],inf));

Pdef(\iperatouShootPat,Pbind(
		\type, \osc, 
		\dur, ~iperatouDur,
	 	\dest, ~addr,
		\msg, Ptuple(["effect", "iperatou", "shoot"], 1)

)
);

Pdef(\backgroundErase,Pbind(
		\type, \osc, 
		\dur, Pseq([0.03], inf),
	 	\dest, ~addr,
		\msg, Ptuple(["background", 0,0,0], 1)

)
);



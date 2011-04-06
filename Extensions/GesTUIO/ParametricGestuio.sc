/*
Parametrize the following properties using instance variables: 
- OSC message to respond to.
- Actions to perform on object born, object moved and object died. 

// ---------------------- Last Updated: Cygnus Fri, 12, November 2010, 18:36PM

p = Parametric_GESTUIO.new;
s.boot;


//  ---------------------LOAD BUFFERS -------------------

b = Buffer.read(s, "sounds/Sydney.aiff");
c = Buffer.read(s, "sounds/Rythm_01.aif");
d = Buffer.read(s, "sounds/Rythm_02.aif", 0, -1);


// ---------------------- EVENTS (blobBorn, blobDied, blobMoved -----


p.blobBorn = { | objectID, classID |
	format("class ID: %", classID).postln;
	switch (classID, 
		5, { 5.postln; Synth("Grain02").onEnd({ | s | postf("% stopped, is running: %\n", s, s.isRunning) }) },
		6, { 6.postln; Synth("Rythym02").onEnd({ | s | postf("% stopped, is running: %\n", s, s.isRunning) }) },
		8, { 8.postln; Synth("Rythym03").onEnd({ | s | postf("% stopped, is running: %\n", s, s.isRunning) }) },
		10, { 10.postln; Synth("blobBorn2").onEnd({ | s | postf("% stopped, is running: %\n", s, s.isRunning) }) },
		0, {	0.postln; Synth("blobBorn3", [\bufnum, b]).onEnd({ | s | postf("% stopped, is running: %\n", s, s.isRunning) })  },
		57, { 57.postln; Synth("Rythym04", [\bufnum, c]).onEnd({ | s | postf("% stopped, is running: %\n", s, s.isRunning) }) },
		59, { 59.postln; Synth("MG_Rythym", [\bufnum, d]).onEnd({ | s | postf("% stopped, is running: %\n", s, s.isRunning) }) },
		1, {1.postln; Synth("Rythym01").onEnd({ | s | postf("% stopped, is running: %\n", s, s.isRunning) })  }
	)
};

p.blobDied = { | synth |
	if (synth.isRunning) { synth.free; };
};

p.blobMoved = { | synth, gestuio, positionX, positionY, rotationAngleZ |


	switch(synth.defName.asSymbol, 
		\Grain02, { synth.set(\freq, rotationAngleZ * 100 + 200,
						    \in, positionX * 40,
						    \dur, positionY ) },
		\Rythym03, { synth.set(\freq1, positionX * 100 + 100 , 
							\freq2, positionY * 100 + 100 ,
							\decay, rotationAngleZ / 10) },
		\blobBorn2, { synth.set(\freq, rotationAngleZ * 50 + 50) },
		\Rythym04, { synth.set(\freq, positionY * 10000 + 2000,
							\rate, positionX - 0.2 * 1.5 ) },
		\Rythym02, { synth.set(\freq1, positionX * 200 + 50,
							\freq2, positionY * 200 + 50 ) },
							
		\MG_Rythym, { synth.set(\grainrate, positionY * 15 + 5,
							\winsize, positionX * 0.03 + 0.005 ) },
		\blobBorn3, { synth.set(\rate, positionX - 0.2 * 2.2, 
							 \room, positionY - 0.2 * 2.2) }		
	)	

};

// ---------------------- SYNTHDEFS ------------------------------- 

p.synthDefs = [
	
	SynthDef(\Grain02, {| in = 10, dur = 0.5, freq = 80  |
	Out.ar(0,
		FreeVerb.ar(FMGrain.ar(LFDNoise3.ar(in), dur, 40, freq, 
			PinkNoise.kr([10, 150]).range(10, 200),
			EnvGen.kr(
				Env([0, 0.1, 0], [2, 2], \sine, 1), gate: 1, levelScale: 1, doneAction: 2)
			), room: (0.7), damp: (0.9)))
		
	}),
		
	SynthDef("blobBorn2", { |freq = 200|
		Out.ar(0, FreeVerb.ar(PMOsc.ar(LFNoise0.kr([10 ,10], freq ,freq), Line.kr(30, 40, 6), 0.5), room: (0.2), mix: (0.2), damp:(0.2)))
	}),
	
	SynthDef("Rythym01", {
		Out.ar(0, FreeVerb.ar(Streson.ar(PMOsc.ar(LFNoise0.kr([30, 40]), Line.kr(30, 40, 100), 8), Line.kr(LFCub.kr(0.5, 0.5), 
		LFCub.kr(0.5, 0.5), 1, 50, 90).reciprocal, 0.9, 0.5), room: (0.5), mix: (0.4), damp: (0.5)))

	}),

	SynthDef("Rythym02", { | freq = 400, freq2 = 400|
		Out.ar(0, Streson.ar
		(LFPulse.ar([freq, freq2], 0, EnvGen.kr(Env.asr(5, 0.5, 2), 1.0) * 0.3), 
		LFCub.kr(LFCub.kr(0.1, 0.5), -1, 1, freq1, freq2).reciprocal, 0.9, 0.3)) 
	
	
	}),
	
	SynthDef("Rythym03", { | freq1 = 30, freq2 = 40, decay = 0.1|
		Out.ar(0, AllpassN.ar( Streson.ar(Blip.ar(BrownNoise.kr([freq1, freq2]), Line.kr(LFCub.kr(0.3, 20), LFCub.kr(0.3, 30), 1, 500, 				900).reciprocal, 1, 0.1), 0.1, decay, 1)))
	}),
	
	SynthDef("blobBorn3", { |bufnum = 0, rate = 1, room = 0.1|
		Out.ar(0, FreeVerb.ar(PlayBuf.ar(2, bufnum, BufRateScale.kr(bufnum) * rate, doneAction:2), room))	}),
		
		SynthDef("MG_Rythym", { |bufnum = 0, winsize = 0.01, grainrate = 10, rate = 1|
		Out.ar(0, FreeVerb.ar(MonoGrain.ar(PlayBuf.ar(2, bufnum, BufRateScale.kr(bufnum) * rate, loop: 1, doneAction:2),
		winsize, grainrate, 0, 1, 1), damp: (3)))	
	}),

	SynthDef("Rythym04", { |bufnum = 0, rate = 1,  freq =0 |
		Out.ar(0, FreeVerb.ar(LPF.ar(PlayBuf.ar(2, bufnum, BufRateScale.kr(bufnum) * rate, loop: 1, doneAction:2), 
		mul:(0.8) ,freq:(freq)), damp: (0.5)))
		
	})

]; 

	


p.start; 
p.stop;
*/

Parametric_GESTUIO : Abstract_GESTUIO {
	var <oscMessage;	// osc message to respond to. Creates OSCresponder;
	var <>blobBorn, <>blobMoved, <>blobDied; // actions to perform on blob events. 
	var <synthDefs;	// synthdefs to load on server boot, on init.
	var <>server;		// server to work with. 

	*new { | oscMessage, synthDefs |
		^super.new.init(oscMessage, synthDefs);
	}
	
	init { | argOscMessage, argSynthDefs |
		// provide some defaults. 
		oscMessage = argOscMessage ? '/tuio/2Dobj';
		server = server ? Server.default;
		this.synthDefs(*argSynthDefs);
		// create responder, parametrically ...
		responder = this.makeResponder;
	}
	
	synthDefs_ { | argSynthDefs |
		synthDefs = synthDefs addAll: argSynthDefs.asArray;
		this.loadSynthDefs;
	}

	loadSynthDefs {
		server.waitForBoot {
			synthDefs do: { | synthDef | synthDef.send(server) };
		};	
	}

	makeResponder {
 		^OSCresponder(nil, oscMessage, { | time, resp, msg |
			this.respondToOSC(msg[1], msg[2..])
		});		
	}

	objectBorn { | objectID, classID |
//		var be;
		
//		be = NetAddr.new("127.0.0.1", 7771);
//		be.sendMsg('tuio/2Dobj', 'objectID' );
		
		NotificationCenter.notify(this, \objectBorn, [objectID, classID]).postln;

		allIDs.put(objectID, blobBorn.(objectID, classID, this));

//		format("previous frame before adding: %", previous_frame).postln;
		previous_frame = previous_frame add: objectID;
//		format("previous frame AFTER adding: %", previous_frame).postln;
		
//		format("an object is born, and its name is: %", objectID).postln;
		
//		["object born, dict of currently active: ", allIDs].postln;	
	}

	objectMoved { | objectID, classID, positionX, positionY, rotationAngleZ  |
		NotificationCenter.notify(this, \blobMoved, [objectID, classID, positionX, positionY, rotationAngleZ]);

		blobMoved.(allIDs.at(objectID), this, positionX, positionY, rotationAngleZ);
	
//		format("and finally we can rotate. The vector value is: %", rotationAngleZ).postln;
		
	}


	objectDied { | diedID |
		NotificationCenter.notify(this, \objectDied, [diedID]);

		blobDied.(allIDs.at(diedID), this);
		allIDs.removeAt(diedID); // .free;		
//		format("a object is dead, and its name is: %", diedID).postln;
//		["object died", allIDs].postln;
		
	}

}
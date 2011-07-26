/*
Parametrize the following properties using instance variables: 
- OSC message to respond to.
- Actions to perform on object born, object moved and object died. 

// ---------------------- Last Updated: Cygnus Fri, 12, November 2010, 18:36PM


s.boot;
p = Parametric_GESTUIO.new;

//  ---------------------LOAD BUFFERS -------------------
(
b = Buffer.read(s, "/Users/Cymac/sounds/osmosis_sounds/seals_normalized/Humpbacks.aiff");
c = Buffer.read(s, "/Users/Cymac/sounds/Rythm_01.aif");
d = Buffer.read(s, "/Users/Cymac/sounds/Rythm_02.aif", 0, -1);
e = Buffer.read(s, "/Users/Cymac/sounds/TLoop_02.aif");
i = Buffer.read(s, "/Users/Cymac/sounds/Osmosis/Seals/Belugawhales.wav"); 
k = Buffer.read(s, "/Users/Cymac/sounds/Osmosis/Seals/PilotWhale.wav"); 
m = Buffer.read(s, "/Users/Cymac/sounds/osmosis_sounds/conet_normalized/tcp_d1_16_attencion_3_finals_irdial.aiff");
)



// ----------------------- SYNTHDEFS ------------------------------- 

GESTUIO_SynthDef.load;
GESTUIO_SynthDef.gui;

// ----------------------- SYNTHDEFS ------------------------------- 


// ---------------------- EVENTS (blobBorn, blobDied, blobMoved -----


p.blobBorn = { | objectID, classID |
	format("class ID: %", classID).postln;
	switch (classID, 
		5, { 5.postln; Synth("Grain02") },
		6, { 6.postln; Synth("Rythym02") },
		8, { { | x = 1 | 

{
	loop {
		
	{ FreeVerb.ar(Blip.ar([x*1500.rand, x*1700].rand) * EnvGen.kr(Env([0, 1, 0],[0.1, 0.3],\welch), doneAction: 2), 	Line.kr(10, 0.2, 0.1)) }.play;
	0.2.wait;
		
	{ FreeVerb.ar(LFSaw.ar([x*1500.rand, x*1500]) * EnvGen.kr(Env([0, 0.5, 0],[0.1, 0.3],\welch), doneAction: 2), 		Line.kr(10, 0.2, 0.1)) }.play;
	0.2.wait;
	
	{ FreeVerb.ar(Impulse.ar([x*500.rand, x*700.rand]) * EnvGen.kr(Env([0, 1, 0],[0.1, 0.3],\welch), doneAction: 2), Line.kr	(10, 0.2, 0.1)) }.play;
	0.2.wait;	
	{FreeVerb.ar(VarSaw.ar([x*1500.rand, x*1500.rand]) * EnvGen.kr(Env([0, 1, 0],[0.1, 0.3],\welch), doneAction: 2), Line.kr(10, 0.2, 0.1))}.play; 
	0.2.wait;
	
	}
}.fork;

}.(0.1 rrand: 10.0); 
			 
			},
		10, { 10.postln; Synth("blobBorn2") },
		0, {	0.postln; Synth("blobBorn3", [\bufnum, b]) },
		55, { 55.postln; Synth("Radio", [\bufnum, k]) },
		57, { 57.postln; Synth("Rythym04", [\bufnum, c]) },
		58, { 58.postln; Synth("TLoop_01", [\bufnum, e]) },
		59, { 59.postln; Synth("MG_Rythym", [\bufnum, d]) },
		1, {1.postln; Synth("Rythym01")}
	)
};

//p.blobDied = { | synth |
//	if (synth.isRunning) { synth.free; };
//};

p.blobMoved = { | synth, gestuio, positionX, positionY, rotationAngleZ |


	switch(synth.defName.asSymbol, 
		\Grain02, { synth.set(\freq, rotationAngleZ * 100 + 200,
						    \in, positionX * 40,
						    \dur, positionY ) },
						    
		\Rythym03, { synth.set(\freq1, positionX * 100 + 100 , 
							\freq2, positionY * 100 + 100 ,
							\decay, rotationAngleZ / 10) },
							
		\blobBorn2, { synth.set(\freq, rotationAngleZ * 50 + 50,
							 \room, positionX * 500 + 500) },
		
		\Rythym04, { synth.set(\freq, positionY * 10000 + 2000,
							\rate, positionX - 0.2 * 1.5 ) },
							
		\TLoop_01, { synth.set(\damp, positionY - 0.5 * 50,
							\rate, positionX - 0.2 * 1.5 ) },
	
		\Rythym02, { synth.set(\freq, positionX * 200 + 50,
							\freq2, positionY * 200 + 50 ) },
							
		\MG_Rythym, { synth.set(\grainrate, positionY * 15 + 5,
							\winsize, positionX * 0.03 + 0.005 ) },
							
		\blobBorn3, { synth.set(\rate, positionX - 0.2 * 0.5 , 
							 \room, positionY - 0.2 * 2.2) }		
	)	

};




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
		//this.synthDefs_(*argSynthDefs);
		// create responder, parametrically ...
		responder = this.makeResponder;
	}
	
	synthDefs_ { | ... argSynthDefs |
		synthDefs = synthDefs addAll: argSynthDefs.asArray;
		this.loadSynthDefs;
	}

	loadSynthDefs {
		server.waitForBoot {
			synthDefs.postln do: { | synthDef | synthDef.send(server) };
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

//		blobDied.(allIDs.at(diedID), this);
		allIDs.removeAt(diedID).free.stop;
		
				
		format("an object is dead, and its name is: %", diedID).postln;
//		["object died", allIDs].postln;
		
		
	}


}
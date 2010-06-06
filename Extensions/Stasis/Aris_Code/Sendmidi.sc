/* 
ari 091216

A class for take midi messages from the midi controller and 
send OSC messages to port 46100 (p5) and to port 57120(sc client) 
messages type: [midi1, 1], [midi2, 13] e.t.c.

Usage:  a. First of all, this class is a tutorial on Object Oriented Programming
		b. With this class we want to take midi signal from MIDI controller Beringer and send OSC messages to Processing and Supercollider.
		. How we make this:
			i.  init: 	-	define server
						-	define address
						-	channel
						- 	initTrigID
						-	makeMidiResponder
String
Enedra						
Sendmidi.start	
OSCresponder(nil, "midi1", { | time, resp, message |	message.postln;}).add;
*/

Sendmidi {
	classvar default;
	var <server;	// the scserver that runs the listening process
	var <p5addr;		// the Processing address
	var <ofaddr;		// the openframeworks address
	var <ofServeraddr;	//of graphics Server	
	var <scaddr;		// the SuperCollider Client address 
	var <responders;	//	responders 

	*default {
		if (default.isNil) { default = this.new };  // create default (i still don't understand that)
		^default;
	}

	*start { this.default.start; }
	
	*stop { this.default.stop }
	
	*new { | server, p5addr, ofaddr, ofServeraddr, scaddr, chan = 0 |
		^super.new.init(server, p5addr, ofaddr, ofServeraddr, scaddr, chan);	//new.init
	}
	
	// start initialize	
	init { | argServer, argp5Addr, argofAddr, argofServerAddr, argscAddr, argChan = 0 |
		server =  argServer ?? { Server.default };  //define server
		p5addr =  argp5Addr ?? { NetAddr("127.0.0.1", 46100); }; //localhost, p5 port for sKeTch 46100 Igoumeninja
		ofaddr =  argofAddr ?? { NetAddr("127.0.0.1", 12345); }; //localhost, openframeworks port 12345
		ofServeraddr =  argofAddr ?? { NetAddr("192.168.1.10", 12345); }; //localhost, openframeworks port 1234	
		scaddr =  argscAddr ?? { NetAddr("127.0.0.1", 57120); }; //localhost, sc client port 57120 		
		// ?? comment: object ?? function nil check (.value, function is inlined) 
		// check on Help: Catalog of symbolic notations in SuperCollider 
		this.makeResponders;	// call makeResponders
	}
	
	makeResponders {
		responders = [
			this.makeMidiResp,	//call makeMidiResp1
			
		];
	}
	makeMidiResp {
		^CCResponder({ |src,chan,num,value|
			scaddr.sendMsg(format("midi%",num),value); //from String//
			p5addr.sendMsg(format("/midi%",num),value);
			ofServeraddr.sendMsg(format("/midi%",num),value);			
			ofaddr.sendMsg(format("/midi%",num),value)			
			},
			nil, // any source
			nil, // any channel
			nil, // any CC number
			nil // any value
			)
	}
	start {
		//responders do: _.add;	//.add all the responders
		//if (not(server.serverRunning)) { server.boot };
	}

	stop {
		responders do: _.remove;
	}
}


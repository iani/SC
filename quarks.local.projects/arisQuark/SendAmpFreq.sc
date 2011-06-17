/* Aris Bezas Mon, 19 April 2010, 03:04PM
A class for sending amplitude and frequency through OSC
Amplitude and pitch detection on SC.

Drawing is done on openframeworks.

Usage:
With this class we want to detect frequency and amplitude and
send the informations through OSC messages. 
How we make this:
			i.  init: 	-	define server
						-	define address
						-	channel
						- 	initTrigID
						-	makeResponders	
							-	makeAudioResp,	
			ii. start:	- 	SynthDef	"SendAmpPitch"
							-	detect amp and freq 200 times/sec
							-	send messages
						-	play "SendAmpPitch"	

Notes: - change IP address to send OSC messages to a different computer
SendAmpFreq.start
x = Synth("SendAmpPitch")
*/

SendAmpFreq {
	classvar default;
	var <server;			// the scserver that runs the listening process
	var <synthListen;		// the listening process
	var <synthPlay;		// the produce process
	var <addr;			// the address (p5, of ...) for sending the data for drawing
	var <chan = 0;		// the channel that we detect
	var <responders;		//	responders 
	*default {
		if (default.isNil) { default = this.new };  // greate default
		^default;
	}
	*start { this.default.start; }	
	*stop { this.default.stop }	
	*new { | server, addr, chan = 0 |
		^super.new.init(server, addr, chan);		//new.init
	}	
	// start initialize	
	init { | argServer, argAddr, argChan = 0 |
		server = argServer ?? { Server.default };  //define server
		addr =  argAddr ?? { NetAddr("127.0.0.1", 12345); }; //localhost, oF port
		//addr =  argAddr ?? { NetAddr("127.0.0.1", 57120); }; //localhost, oF port
		//addr =  argAddr ?? { NetAddr("169.254.233.24", 12345); }; // server
		chan = argChan;
		this.makeResponders;	// call makeResponders
	}
	// end initialize
	makeResponders {
		responders = [
			this.makeAudioResp		//call makeAudioResp
		];
	}
	makeAudioResp {
		^OSCresponder(server.addr, '/tr',{ arg time,responder,msg;
			switch(msg[2], 	
				1, { addr.sendMsg("/ampChan0",    msg[3]); }, 
				2, { addr.sendMsg("/freqChan0",   msg[3]); },   
				3, { addr.sendMsg("/ampChan1",   msg[3]); },  
				4, {  addr.sendMsg("/freqChan1", msg[3]); } 
			);
		});
	}
	start {
		responders do: _.add;			//.add all the responders
		if (not(server.serverRunning)) { server.boot };
		server.doWhenBooted {			// doWhenBooted very good method
			 synthListen = SynthDef("SendAmpPitch",{ | chan = 8, ampTrig = 1, freqTrig = 2, impulseRate = 60 |
				var trig, in, amp, freq, hasFreq;
				trig = Impulse.kr(impulseRate);
				in = In.ar(chan);
				amp = Amplitude.kr(in, 0.001, 0.001);
				#freq, hasFreq = Pitch.kr(in, ampThreshold: 0.02, median: 1);
				SendTrig.kr(trig, ampTrig, amp);  	
				SendTrig.kr(trig, freqTrig, freq);  	
			}).send(server);
		};
	}

	stop {
		responders do: _.remove;
		synthListen.free;
	}
}


/* 
Try to Send an array through OSC.
Take the Array as an argument, separate the contents and send them.

Example:

y =  [0,1,2,3,4];
SendArrayWithOSC.msgName(y);

Equal to: 
sendMsg("msgName", y[0], y[1], y[2], y[3], y[4]);
*/

SendArrayWithOSC { 
	classvar <default;
	var <>addr;

	*new { | addr |
		^super.new.init(addr);
	}

	init { | argAddr |
		addr = argAddr ?? { this.defaultAddress };
	}

	defaultAddress {
		^NetAddr("127.0.0.1", 12345); // 12345  "192.168.1.65"
		//^NetAddr("169.254.233.24", 12345);
	}

	*doesNotUnderstand { | message ... args |
		if (default.isNil) { default = this.new };
		//format("here I will send message % with args %", message, args).postln;
		
		args[0].size.postln;
		
//		format("OF.%%", message, args).postln;
//		default.send(message.asString, args);
	}
	
//	send { | message, args |
//		addr.sendMsg(message.asString, *args);
//	}
}

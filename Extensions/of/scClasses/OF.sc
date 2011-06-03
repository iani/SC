/* 
Dispatcher of messages as well as model for an ObjectFrameworks application 

OF./test("alpha",255)

101203: Problem with SC 3.4
SendArrayWithOSC
*/

OF { 
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
		//format("OF.%%", message, args).postln;
		default.send(message.asString, args);
		
	}
	
	send { | message, args |
		addr.sendMsg(message.asString, *args);
		//addr.sendBundle(0.2, ["/good/news", 1, 1.3, 77]);
		//addr.sendBundle(0.2, args);		
	}
}

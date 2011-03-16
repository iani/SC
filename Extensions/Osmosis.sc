/* Setup for piece: Buffers etc.

IZ 2011 0316


O@\x


(a: 1)@\a
*/

Osmosis {
	classvar <o;
	var <buffers;
	
	*initClass { o = this.new }
	
	*new { 
		^super.new.init;	
	}
	
	init { buffers = IdentityDictionary.new }
	
	setup { // setup the piece: configure and restart server, load synthdefs, load buffers.
		this.startServer;
		this.loadSynthDefs;
		this.loadBuffers;	
	}
	
	startServer {
		
	}
	
	loadSynthDefs {
		
	}
	
	loadBuffers {
		
	}
	
	*doesNotUnderstand { | message ... args | o.perform(message, *args) }
	
	@ { | bufname | ^buffers[bufname] }	
	
}

O : Osmosis { } // Define O as shortcut for Osmosis
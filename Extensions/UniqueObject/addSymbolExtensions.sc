/*

f = { "Hello world".postln };

10 do: { f.doOnce };

f.resetDoOnce;

*/


+ Symbol {
	
	// access a unique synth if present, without starting it
	usynth { | server | ^UniqueSynth.at(UniqueSynth.makeKey(this, target: server ? Server.default)) }
	
	// start a unique synth if not already running
	play { | args, target, addAction=\addToHead |
		^UniqueSynth(this, this, args, target, addAction);
	}
	
	free { | server | ^this.usynth(server).free }
	wait { | dtime, server | ^this.usynth(server).wait(dtime) }
	
	set { | ... args | this.usynth.object.set(*args) }
	setn { | ... args | this.usynth.object.setn(*args) }
	release { | server | this.usynth(server).release }
	
//	window { | rect, action |
//		
//	}
	
//	close { this.uwindow.close }
}

+ String {

	// access a unique synth if present, without starting it
	usynth { | key, server | ^this.asSymbol.usynth(key, server); }

	// start a unique synth if not already running
	play { | args, target, addAction=\addToHead |
		^this.asSymbol.play(args, target, addAction);
	}


}

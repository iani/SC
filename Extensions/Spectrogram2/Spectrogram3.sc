// changelog:
//	- 30-Mar-10 made cross-platform, fixed relativeOrigin issue
// modifications by IZ 2011 04 17 f

/* 
This here is a redo using UniqueObject subclasses to simplify the 
synchronization between window, synth and routine

NOT YET DONE!
*/

Spectrogram3 : UniqueWindow {
	/* fft sizes > 1024 are not supported, because that is the largest size of a buffer that 
		can be obtained with buf.getn at the moment
	*/
	
	var <server, <bounds, <rate, <bufsize, <name;
	var <synth;
	
	*start { | server, bounds, rate, bufsize, name |
		^this.new	(name, bounds, rate, bufsize, server).start;
	}
	
	*new { | name, bounds, rate, bufsize |
		
	}
	
	start {
		synth = this.startSynth(name, bounds, rate);
		CmdKey.add(this);
	}
	
	stop {
		synth.free;	
		CmdKey.remove(this);
	}
	
	startSynth {
		
	}
	
}


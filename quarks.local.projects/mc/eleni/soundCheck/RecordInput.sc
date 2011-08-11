
RecordInput {
	var synthdef, defname, buffer, synth;
	
	*new { | synthdef |
		^this.newCopyArgs(synthdef).init;
	}
	
	init {
		defname = synthdef.name;
		synthdef.send(Server.default);
	}
	
	start {
		{
			buffer = Buffer.alloc(Server.default, 65536, 1);
			0.1.wait;
			buffer.write(format("~/diskouttest%.aiff", 
				Date.getDate.stamp).standardizePath, "aiff", "int16", 0, 0, true
			);
			0.1.wait;
			synth = Synth.tail(nil, defname, ["bufnum", buffer]);
		}.fork
	}
	
	stop {
		{
			synth.free;
			0.1.wait;
			buffer.close;
			0.1.wait;
			buffer.free;
		}.fork;	
	}
	
	set { | ... args |	
		synth.set(*args);
	}
}

/*
r = RecordInput(
SynthDef("help-Diskout", { | bufnum = 0 |
	var in, amp, freq, hasFreq, out;
	in = SoundIn.ar(0);
	amp = Amplitude.kr(in, 0.05, 0.05);
	# freq, hasFreq = Pitch.kr(in, ampThreshold: 0.02, median: 7);
	//freq = Lag.kr(freq.cpsmidi.round(1).midicps, 0.05);
	out = Mix.new(VarSaw.ar(freq * [0.5,1.2, 1.5, 1.7, 2.2] / 2.5, 0, LFNoise1.kr(0.3,0.1,0.1), amp));
	6.do({
		out = AllpassN.ar(out, 0.040, [0.040.rand,0.040.rand], 2)
	});
	// RECORD HERE ============
	
	DiskOut.ar(bufnum, in);
	Out.ar(0, out);
})
);

//:---
r.start;

r.stop;
*/

/*
SynthDef("help-Diskout", { | bufnum = 0 |
	var in, amp, freq, hasFreq, out;
	in = SoundIn.ar(0);
	amp = Amplitude.kr(in, 0.05, 0.05);
	# freq, hasFreq = Pitch.kr(in, ampThreshold: 0.02, median: 7);
	//freq = Lag.kr(freq.cpsmidi.round(1).midicps, 0.05);
	out = Mix.new(VarSaw.ar(freq * [0.5,1.2, 1.5, 1.7, 2.2] / 2.5, 0, LFNoise1.kr(0.3,0.1,0.1), amp));
	6.do({
		out = AllpassN.ar(out, 0.040, [0.040.rand,0.040.rand], 2)
	});
	// RECORD HERE ============
	
	DiskOut.ar(bufnum, in);
	Out.ar(0, out);
}).send(Server.default);

//:allocate a disk i/o buffer
b = Buffer.alloc(s, 65536, 1);

//:create an output file for this buffer, leave it open
b.write(format("~/diskouttest%.aiff", Date.getDate.stamp).standardizePath, "aiff", "int16", 0, 0, true);
//:create the diskout node; making sure it comes after the source
d = Synth.tail(nil, "help-Diskout", ["bufnum", b]);
//:stop recording
d.free;
//:close the buffer and the soundfile
b.close;
//:free the buffer
b.free;



*/
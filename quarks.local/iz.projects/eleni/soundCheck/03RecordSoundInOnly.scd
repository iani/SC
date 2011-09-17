//: ORIGINAL 0
//:DiskOut-help synthdef (template)

SynthDef("help-Diskout", {arg bufnum;
	DiskOut.ar(bufnum, In.ar(0,2));
}).send(Server.default);

//:sound to record
x = {
	var f, zout;
	f = LFSaw.kr(0.4, 0, 24, LFSaw.kr([8,7.23], 0, 3, 80)).midicps; // glissando function
	zout = CombN.ar(SinOsc.ar(f, 0, 0.04), 0.2, 0.2, 4); // echoing sine wave
	Out.ar(0, zout);
}.play;


//:allocate a disk i/o buffer
b = Buffer.alloc(s, 65536, 2);

//:create an output file for this buffer, leave it open
b.write(format("~/diskouttest%.aiff", Date.getDate.stamp).standardizePath, "aiff", "int16", 0, 0, true);
//:create the diskout node; making sure it comes after the source
d = Synth.tail(nil, "help-Diskout", ["bufnum", b]);
//:stop recording
d.free;
//:stop the bubbles
x.free;
//:close the buffer and the soundfile
b.close;
//:free the buffer
b.free;


//: ==============  Step 1
//:DiskOut-help synthdef (template)

SynthDef("help-Diskout", { | bufnum |
	
	DiskOut.ar(bufnum, In.ar(0, 1));
}).send(Server.default);

//:sound to record
x = {
	var f, zout;
	f = LFSaw.kr(0.4, 0, 24, LFSaw.kr([8,7.23], 0, 3, 80)).midicps; // glissando function
	zout = CombN.ar(SinOsc.ar(f, 0, 0.04), 0.2, 0.2, 4); // echoing sine wave
	Out.ar(0, zout);
}.play;


//:allocate a disk i/o buffer
b = Buffer.alloc(s, 65536, 1);

//:create an output file for this buffer, leave it open
b.write(format("~/diskouttest%.aiff", Date.getDate.stamp).standardizePath, "aiff", "int16", 0, 0, true);
//:create the diskout node; making sure it comes after the source
d = Synth.tail(nil, "help-Diskout", ["bufnum", b]);
//:stop recording
d.free;
//:stop the bubbles
x.free;
//:close the buffer and the soundfile
b.close;
//:free the buffer
b.free;


//: ==============  Step 2
//:DiskOut-help synthdef (template)

SynthDef("help-Diskout", { | bufnum |
	var glissandoFunc, in;
	glissandoFunc = LFSaw.kr(0.4, 0, 24, LFSaw.kr([8,7.23], 0, 3, 80)).midicps;
	in =  CombN.ar(SinOsc.ar(glissandoFunc, 0, 0.04), 0.2, 0.2, 4);
	DiskOut.ar(bufnum, Mix.ar(in));
	Out.ar(0, in);
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

//: ==============  Step 3
//:DiskOut-help synthdef (template)

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






//:DiskOut-help synthdef (template)

SynthDef("help-Diskout", {arg bufnum;
	DiskOut.ar(bufnum, In.ar(0,2));
}).send(s);

// FOLLOWING DO NOT WORK


//:prepare a buffer and a sound file
b = Buffer.alloc(s, 65536, 2);

b.write("~/diskouttest.aiff".standardizePath, "aiff", "int16", 0, 0, true);

//:prepare a synthdef

SynthDef("pitchFollow_Debussy_record",{ | bufnum = 0 |
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

//:record 
a = Synth("pitchFollow_Debussy_record", [\bufnum, b]);


//:stop recording and playing synth
a.free;
//:close the buffer and the soundfile
b.close;
//:free the buffer
b.free;





/* 
Selectively raising the amplitude of channel groups according to their distance from a point. 

We use a 43 channel controls array, into which activation is panned. The control values are used as amplitude multipliers for the signals of the 43 inputs. 

IZ 2011 03 16

O.buffers.keys


SynthDef


BufRd.ar(numChannels, bufnum, phase, loop, interpolation)

Phasor	*ar(trig, rate, start, end, resetPos)

*/


O.startup;
a = { | bufname = \weddelle |
	var buf, playfunc;
	var sourcegroup, sources;
	buf = (O@bufname).bufnum;
//	buf.play;
//	b = { | sr = 0.1 | BufRd.ar(1, buf, SinOsc.ar(sr) * BufFrames.ir(buf)) }.play;
//	b = { | sr = 0.02 | BufRd.ar(1, buf, LFDNoise3.ar(sr) * BufFrames.ir(buf)) }.play;

	sourcegroup = Group.new;
	
	playfunc = {  | out |	
		{ | sr = 0.045 |
			var b;
			b = BufRd.ar(1, buf, LFDNoise3.ar(sr) * BufFrames.ir(buf));
		 }.play(target: sourcegroup, outbus: out);
	};
//	sources = { | i | playfunc.(i) } ! 2;
	sources = playfunc.(_) ! 43;
};


a.value;
a.(\swallowsa);
a.(\dfd);


b.set(\sr, 0.02);

b.set(\sr, 0.045);



SynthDef("Blfn3", { | out = 0, bufnum = 0, rate = 0.045 |
	Out.ar(out, BufRd.ar(1, bufnum, LFDNoise3.ar(rate) * BufFrames.ir(bufnum));	
});


a = { | bufname = \weddelle |
	var buf, playfunc;
	var sourcegroup, sources;
	buf = (O@bufname).bufnum;
//	buf.play;
//	b = { | sr = 0.1 | BufRd.ar(1, buf, SinOsc.ar(sr) * BufFrames.ir(buf)) }.play;
//	b = { | sr = 0.02 | BufRd.ar(1, buf, LFDNoise3.ar(sr) * BufFrames.ir(buf)) }.play;

	sourcegroup = Group.new;
	
	playfunc = {  | out |	
		{ | sr = 0.045 |
			var b;
			b = BufRd.ar(1, buf, LFDNoise3.ar(sr) * BufFrames.ir(buf));
		 }.play(target: sourcegroup, outbus: out);
	};
//	sources = { | i | playfunc.(i) } ! 2;
	sources = playfunc.(_) ! 43;
};

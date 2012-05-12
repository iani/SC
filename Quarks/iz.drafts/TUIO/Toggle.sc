
Toggle {
	var value = 0;
	
	*new { | value = 0 | ^super.newCopyArgs((value > 0).binaryValue) }
	
	next { ^this.value }

	value { ^value = 1 - value }
}


/*

a = Toggle.new;

10 do: { a.next.postln }

*/


/*

//:---

{ 
	var b, t;
	b = { | trig = 0 | WhiteNoise.ar(Decay.kr(Slope.kr(trig).abs > 0), 0.05) }.play;
	t = Toggle.new;
	30 do: { 1.wait; b.set(\trig, t.value) };
	0.5.wait;
	b.free;
}.fork

//: ---- 

*/

/*

//:---

{ 
	var b, t;
	b = SynthDef("help-PulseCount",{ arg trig = 0, out = 0;
		var player, perc, sin;
		player = Slope.kr(trig).abs > 0;
		perc = WhiteNoise.ar(Decay.kr(TDelay.kr(player, 0.1)), 0.1, 0.1);
		sin = SinOsc.ar((PulseCount.kr(player, Impulse.kr(0.2)) + 80).midicps, 0, 0.05);
		Out.ar(out, [sin, perc])
	}).play;
	t = Toggle.new;

	30 do: { 0.5.wait; b.set(\trig, t.value); };
	0.5.wait;
	b.free;
}.fork

//: ---- 

*/
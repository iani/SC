/* DRAFT!
Play like a Pbind, but each event sets parameters on one synth.

Similarity is only on the surface. Pset does not use Event.

(
Nbind(\default.play, Pfunc({ 0.01.exprand(0.5) }), (freq: Pwhite(400, 2000, inf), amp: Pfunc({ 0.001.exprand(0.2) })));
)

(
Nbind(\default.play, Prand([0.1, 0.2], inf), 
	(freq: Prand((100, 200..3000), 30), amp: Prand([0.01, 0.02, 0.2], inf)));
)


*/

Nbind {
	var <>node, <>dur, <>params;

	*new { | node, dur, params |
		^this.newCopyArgs(node, dur, params).init;
	}

	init {
		dur = dur.asStream;
		params.keys.do { | k | params[k] = params[k].asStream };
		node onStart: {
			{
				var sawNil = false;
				var next;
				var paramvals;
				if (node.isPlaying) {
					paramvals = params.asKeyValuePairs.collect({ | p | 
						next = p.next;
						if (next.isNil) { sawNil = true };
						next;
					});
					if (sawNil) {
						node.release;
						nil 
					}{
						node.set(*paramvals);
						dur.next;
					};
				}{
					nil;
				};
			}.sched(0, SystemClock);
		}
	}
}

/* Example of a class 2

Igoumeninja 2010 11 17: 00:12 am


p = Posc(\msg, Pseq([['alpha', 100], ['beta', 200]], inf)).play;



p = Posc(\msg, Pseq([['alpha', 100], [['beta', 200], [\gamma, 300]]], inf)).play;

Posc(\msg, Psend(Pseq([\a, \c, Psend(Pseq([\b, Beats(5)]))]))).play;


Posc(\msg, Psend(Pseq([[\bla], [\blo, 3], Psend(Pseq([[\test][[\chord1, 2], [\chord2, 2]]], 2))).play;



Posc(\msg, Psend(Pseq([\a, \c, Psend(Pseq([\b, Beats(5)]))]))).play;

Posc.play([\a, \c, [\rgb, 0.1, 0.5, 0.99], Psend(Pseq([\b, Beats(5)]))]);

Posc.play([\a, [[\c, 1], [\e]], [\rgb, 0.1, 0.5, 0.99], Psend(Pseq([\b, Beats(5)]))]);


Posc.play([\a, [[\c, 1], [\e]], [\rgb, 0.1, 0.5, 0.99], Psend(Pseq([\b, Beats(5)]))],
	\dur, 0.1
);

(
Posc.play([\a, [[\c, 1], [\e]], [\rgb, 0.1, 0.5, 0.99], Psend(Pseq([\b, Beats(5)]))],
	\dur, 0.25,
	\dest, [NetAddr.localAddr, NetAddr("127.0.0.1", 12345)]
);
)



Posc.play(Pfuncn({ Synth.new(\default); \minimaseProtypo }, 1));



*/

Posc : Pbind {
	classvar <posc;	// the single currently playing posc. Set by Posc:play;
	classvar <>defaultDest;	// default destinations, used by *play. 

	var <>name, <>dest, <>latency;
	*initClass {
		Class.initClassTree(Event);
		Event.addEventType(\osc, {
			~msg.postln;
			this.broadcastMessageWithBeat(~dest, ~msg, ~beat, ~latency);
		});
	}

	*new { | ... pairs | 	// add the type - osc pair, so we dont have to provide it explicitly
		pairs = pairs addAll: [\type, \osc];
		if (pairs includes: \dest) {} { pairs = pairs addAll: [\dest, NetAddr.localAddr] };
		if (pairs includes: \beat) {} { pairs = pairs addAll: [\beat, Pseries(1, 1, inf)] };
		^super.new(*pairs).init;
	}

	init {
		// store name and destinations in variables, for use when broadcasting by Psend
//		var nameIndex;
		dest = patternpairs[patternpairs.indexOf(\dest) + 1].asArray;
//		nameIndex = patternpairs.indexOf(\name);
//		if (nameIndex.isNil) {
//			name = \phrase;
//		}{
//			name = patternpairs[nameIndex + 1];
//		}
	}
	
	*broadcast { | msg | posc.broadcast(msg) }

	broadcast { | msg | 	// used by Psend to broadcast start, end
		dest do: _.sendBundle(0, msg);
	}
	
	*destinations { | ... destList |
		// shortcut for Posc.defaultDest = ... 
		defaultDest = destList;
	}
	
	*addLocal {
		defaultDest = defaultDest add: NetAddr.local;
	}
	
	*broadcastMessageWithBeat { | dest, msg, beat, latency |
		
		if (msg.rank > 1) {
			dest.asArray do: { | d |
				d.sendBundle(latency, [\beat, beat]);
				d.sendBundle(latency, *msg) };
		}{
			msg = msg.asArray;
			dest.asArray do: { | d |
				d.sendBundle(latency, [\beat, beat]);
				d.sendBundle(latency, msg);
			}
		};
		
	}

	*play { | patternContents ... pairs |
	// utility class for playing a Psend phrase from any pattern or array of patterns
		^this.new(\msg, Psend(Pseq(patternContents.asArray, 1)), 
			\dest, (defaultDest ?? { NetAddr.localAddr }).asArray,
			*pairs
		).play(protoEvent: (posctest: 1234567890));
	}

	play { arg clock, protoEvent, quant;
		posc = this;
		^super.play(clock, protoEvent, quant);
	}
	
}

/*

	embedInStream { arg inevent;
		var event;
		var sawNil = false;		
		var streampairs = patternpairs.copy;
		var endval = streampairs.size - 1;

		forBy (1, endval, 2) { arg i;
			streampairs.put(i, streampairs[i].asStream(this));
		};

		loop {
			if (inevent.isNil) { ^nil.yield };
			event = inevent.copy;
			forBy (0, endval, 2) { arg i;
				var name = streampairs[i];
				var stream = streampairs[i+1];	
				var streamout = stream.next(event);
//				[name, stream, streamout].postln;
				if (streamout.isNil) { ^inevent };

				if (name.isSequenceableCollection) {
					if (name.size > streamout.size) {  
						("the pattern is not providing enough values to assign to the key set:" + name).warn;
						^inevent 
					};
					name.do { arg key, i;
						event.put(key, streamout[i]);
					};
				}{
					event.put(name, streamout);
				};
				
			};
			inevent = event.yield;
		}		
	}
*/

/*
	embedInStream { arg inevent;
		var event;
		var sawNil = false;		
		var streampairs = patternpairs.copy;
		var endval = streampairs.size - 1;

		forBy (1, endval, 2) { arg i;
			streampairs.put(i, streampairs[i].asStream);
		};

		loop {
			if (inevent.isNil) { ^nil.yield };
			event = inevent.copy;
			forBy (0, endval, 2) { arg i;
				var name = streampairs[i];
				var stream = streampairs[i+1];	
				var streamout = stream.next(event);
				if (streamout.isNil) { ^inevent };

				if (name.isSequenceableCollection) {
					if (name.size > streamout.size) {  
						("the pattern is not providing enough values to assign to the key set:" + name).warn;
						^inevent 
					};
					name.do { arg key, i;
						event.put(key, streamout[i]);
					};
				}{
					event.put(name, streamout);
				};
				
			};
			inevent = event.yield;
		}		
	}
*/





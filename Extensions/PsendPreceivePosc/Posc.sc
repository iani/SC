/* */

Posc : Pbind {

	var <>name, <>dest, <>latency;
	*initClass {
		Class.initClassTree(Event);
		Event.addEventType(\osc, { | ... what |
			var dest;
			dest = ~dest;
			~beatCounters do: _.broadcast(dest);
			this.broadcastMessage(dest, ~msg);
		});
	}

	*new { | ... pairs | 	// add the type - osc pair, so we dont have to provide it explicitly
		pairs = pairs addAll: [\type, \osc];
		if (pairs includes: \dest) {} { pairs = pairs addAll: [\dest, NetAddr.localAddr] };
		^super.new(*pairs).init;
	}

	init { dest = patternpairs[patternpairs.indexOf(\dest) + 1].asArray; }

	broadcast { | msg |
		dest do: { | d | d.sendBundle(0, msg); }
	}

	*broadcastMessage { | dest, msg |
		if (msg.rank > 1) {
			dest.asArray do: { | d | msg do: { | m | d.sendMsg(*m) }; };
		}{
			dest.asArray do: { | d | d.sendMsg(*msg); };
		}
	}

	*play { | patternContents ... pairs |
		^this.new(\msg, Psend(Pseq(patternContents.asArray, 1)), *pairs)
			.play(protoEvent: (beatCounters: BeatStack.new));
	}

	play { arg clock, protoEvent, quant;
		protoEvent[\posc] = this;
		^super.play(clock, protoEvent, quant);
	}

}

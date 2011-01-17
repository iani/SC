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
		/* Here we convert all first elements of msg to strings to avoid the annoying insertion
		of "/" when sending symbols, which appeared first in SC version 3.4 */
		if (msg.rank > 1) {
			msg = msg.collect { | m | m.copy.asArray; };
			msg do: { | m | m[0] = m[0].asString; };
			dest.asArray do: { | d | msg do: { | m | d.sendMsg(*m) }; };
		}{
			msg = msg.asArray.copy;
			msg[0] = msg[0].asString;
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

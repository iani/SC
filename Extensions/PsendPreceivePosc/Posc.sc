/* */

Posc : Pbind {

	var <>name, <>dest, <>latency;
	*initClass {
		Class.initClassTree(Event);
		Event.addEventType(\osc, { | ... what |
			var dest;
			dest = ~dest;
			~beatCounters do: _.broadcast(dest);
			this.broadcastMessageWithBeat(dest, ~msg, ~latency);
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

	*broadcastMessageWithBeat { | dest, msg, latency |
		if (msg.rank > 1) {
			msg do: { | bundle |
				bundle[0] = bundle[0].asString;
			};
			dest.asArray do: { | d |
				d.sendBundle(latency, *msg) };
		}{
			msg = msg.asArray;
			msg[0] = msg[0].asString;
			dest.asArray do: { | d |
				d.sendBundle(latency, msg);
			}
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

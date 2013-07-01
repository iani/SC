


BasicMixer : EventModel {

	var <synth, <numChans = 2, <keys;

	*new { | numChans = 2, maxLevel = 1 |
		^super.new.init(numChans, maxLevel)

	}

	init { | argNumChans = 2, maxLevel = 1 |
		var key;
		numChans = argNumChans;
		keys = this.makeKeys;
		keys do: { | key |
			event[key] = 0;
			specs[key] = ControlSpec(0, maxLevel, \amp);
			this.addEventListener(this, key, { | amp | this.set(key, amp) });
		};
	}

	makeKeys {
		var key;
		^(1..numChans) collect: { | i | format("amp%", i).asSymbol };
	}

	set { | key, value |
		synth !? { synth.set(key, value) }
	}

	makeWindow { | name = "Output Levels", bounds |
		bounds = bounds ?? { Rect(0, 0, numChans * 40 + 100, 300) };
		^this.funcMakeWindow({ | w |
			w.bounds_(bounds).name_(name)
			.layout = HLayout(
				*(
					(this.makeKeys collect: { | key, i | this.fader(key, label: i.asString) })
					++
					VLayout(
						StaticText().string_("this"),
						StaticText().string_("is"),
						StaticText().string_("a"),
						StaticText().string_("test")
					)
				)
			)
		})
	}

	start {
		if (synth.notNil) { ^this };
		synth = {
			var input, output;
			input = In.ar(0, numChans);
			output = keys collect: { | key, i |
				input[i] * key.kr(0)
			};
			ReplaceOut.ar(0, output);
		}.play(Server.default, addAction: \addToTail, args:
			keys.collect({ | key, i |
				[key, event[key]];
			}).flat;
		);
	}

	stop {
		if (synth.notNil) { synth.free; synth = nil }
	}


}

SynthModel {
	var <template, <eventModel, <keys;
	var <target, <addAction = \addToHead;
	var <defName, <synth;

	*initClass {
		Class.initClassTree(ControlSpec);
		Spec.specs.addAll([
			\fadeTime -> ControlSpec(0.01, 30),
			\out -> ControlSpec(0, 127, \lin, step: 1, default: 0),
		])
	}

	*new { | template, eventModel, keys, target, addAction = \addToHead |
		^this.newCopyArgs(template, eventModel ?? { () }, keys, target.asTarget, addAction).init;
	}

	init {
		if (template.isKindOf(Symbol) or: { template.isKindOf(String) }) {
			defName = template;
		}{
			this.addSynthDef(template);
		};
		if (eventModel.isKindOf(Event)) {
			eventModel = EventModel(eventModel)
		};
		if (eventModel.event.size == 0) { this.makeEvent } { this.connectKeys; };
	}

	addSynthDef { | synthDef |
		if (synthDef.isKindOf(Function)) {
			synthDef = synthDef.asFlexSynthDef(name: SystemSynthDefs.generateTempName);
		};
		synthDef.add;
		defName = synthDef.name.asSymbol;
	}

	makeEvent {
		var event, name;
		event = eventModel.event;
		this.getControlsFromSynthDesc do: { | c | event[c.name] = c.defaultValue };
		this.connectKeys;
	}

	getControlsFromSynthDesc {
		var desc, name;
		desc = SynthDescLib.global[defName];
		if (desc.notNil) {
			^desc.controls select: { | c |
				name = c.name.asString;
				name[..1] != "i_" and: { name != "gate" }
			}
		}{ ^nil };
	}

	connectKeys {
		var event;
		event = eventModel.event;
		(keys ?? { event.keys }) do: { | key |
			this.addNotifier(event, key, { | value | this.set(key, value) })
		}
	}

	set { | key, value |
		if (synth.notNil) { synth.set(key, value) };
	}

	toggle {
		if (synth.isNil) { this.start } { this.release }
	}

	start {
		if (synth.notNil) { ^this };
		if (target.server.serverRunning) {
			this.startSynth;
		}{
			target.server.waitForBoot({ this.startSynth });
		};
	}

	startSynth {
		synth = Synth(defName,
			args: this.getArgs,
			target: target,
			addAction: addAction
		);
		NodeWatcher.register(synth);
		this.addNotifier(synth, \n_end, { | notification |
			this.changed(\synthEnded);
			notification.notifier.objectClosed;
			synth = nil;
		});
		synth.addNotifier(this, \free, { | notification |
			notification.listener.free;
			notification.listener.objectClosed;
			synth = nil;
			this.changed(\synthEnded);
		});
		synth.addNotifier(this, \release, { | fadeTime, notification |
			notification.listener.release(fadeTime);
			notification.listener.objectClosed;
			synth = nil;
			this.changed(\synthEnded);
		});
		synth.addNotifier(this, \run, { | value | synth.run(value) });
		this.changed(\synthStarted);
	}

	getArgs {
		if (keys.isNil) {
			^eventModel.event.getPairs;
		}{
			^eventModel.event.getPairs(keys);
		}
	}

	stop { this.free; synth = nil }

	free { this.changed(\free) }

	release { | fadeTime |
		this.changed(\release, fadeTime);
	}

	gui { | argKeys |
		// basic gui - under development!
		^Window(defName).front.view.layout = this.keysLayout(argKeys);
	}

	keysLayout { | argKeys |
		argKeys ?? { argKeys = keys ?? { eventModel.event.keys.asArray.sort } };
		^VLayout(
			HLayout(
				this.addView(Button,
					\synthEnded, { | n |
						n.listener.value = 0;
					},
					\synthStarted, { | n |
						n.listener.value = 1;
					}
				)
				.states_([["start"], ["fade out"]]).action_({ | me |
					[{ this.release(eventModel.event[\releaseTime]) }, { this.start }][me.value].value
				}),
				this.addView(Button,
					\synthEnded, { | n | n.listener.enabled = 0 },
					\synthStarted, { | n | n.listener.enabled = 1 }
				)
				.states_([["stop"]]).action_({ this.free })
				.enabled_(synth.notNil),
				this.addView(Button,
					\synthEnded, { | n | n.listener.value_(0).enabled = 0 },
					\synthStarted, { | n | n.listener.value_(0).enabled = 1 },
					\run, { | val, n | n.listener.value_(1 - val) }
				)
				.states_([["pause"], ["resume"]]).action_({ | me | this.changed(\run, 1 - me.value); })
				.value_(synth.notNil.and({ synth.isRunning.not }).binaryValue)
				.enabled_(synth.notNil),
				StaticText().string_("Group (0+-8)").font_(Font.default.size_(9)).maxWidth_(50),
				NumberBox().action_({ | me | this.setGroup(me.value) })
				.decimals_(0).step_(1).clipLo_(-8).clipHi_(8).maxWidth_(25),
				StaticText().string_("Outputs:"),
			),
			*(argKeys.collect(eventModel.numSlider(_)))
		)
	}

	setGroup { "not yet implemented".postln; }

	addView { | viewClass ... messagesActions |
		var view;
		view = viewClass.new;
		view.onClose = { view.objectClosed };
		messagesActions.pairsDo({ | message, action |
			view.addNotifier(this, message, action)
		})
		^view;
	}
}

EventModel {

	var <event;
	var <specs;

	*new { | event |
		^this.newCopyArgs(event ?? { () }, ())
	}

	put { | key, value |
		event.put(key, value);
		event.changed(key, value);
	}

	updateAll {
		event.keysValuesDo { | key, value | event.changed(key, value) };
	}

	addEventListener { | listener, key, action |
		listener.addNotifier(event, key, { | value | action.(value) })
	}

	// Widgets : Different types of gui views
	labeledNumberBox { | key |
		^this.numberBox(key, \makeLabel);
	}

	makeWindow { | ... widgetSpecs |
		var window, layout;
		window = Window().front;
		if (widgetSpecs[0].isKindOf(Class)) {
			layout = widgetSpecs[0];
			widgetSpecs = widgetSpecs[1..];
		}{
			layout = VLayout;
		};
		window.view.layout = layout.new(
			*(widgetSpecs collect: { | ws | this.perform(*ws) })
		);
		^window;
	}

	funcMakeWindow { | func |
		var window;
		window = Window();
		func.(window, this);
		window.front;
	}

	button { | key, states, decoratorFunc |
		var button;
		button = this.makeView(Button);
		decoratorFunc = decoratorFunc ?? {{ | argKey, argView |
			argView.states = states ?? { [[states.asString]] };
			argView.action = { this.put(key, argView.value) };
		}};
		^decoratorFunc.(key, button);
	}

	makeView { | viewClass |
		^viewClass.new.onClose_({ | view | view.objectClosed });
	}

	numberBox { | key, decoratorFunc |
		var view, widgets, value;
		view = this.makeView(NumberBox).decimals_(4);
		value = event[key];
		value !? { view.value = value };
		view.addNotifier(event, key, { | val | view.value = val });
		view.action = { | me | this.put(key, me.value) };
		^switch ( decoratorFunc,
			nil, { view },
			\makeLabel, { HLayout(StaticText().string_(key), view) },
			{ decoratorFunc.(key, view) }
		);
	}

	fader { | key = \amp, spec, decoratorFunc, label |
		^this.ctlSpecView(key, this.makeSpec(key, spec), Slider,
			decoratorFunc ?? {{ | argKey, argView |
				VLayout(
					argView.orientation_(\vertical),
					this.numberBox(key).font_(Font.default.size_(9)),
					StaticText().font_(Font.default.size_(9)).string_(label ? argKey),
				);
			}}
		);
	}

	vslider { | key, spec |
		^this.slider(key, spec, { | argKey, argView | argView.orientation_(\vertical) })
	}

	slider { | key, spec, decoratorFunc |
		^this.ctlSpecView(key, this.makeSpec(key, spec), Slider,
			decoratorFunc ?? {{ | argKey, argView |
				HLayout(StaticText().string_(argKey), argView.orientation_(\horizontal));
			}}
		);
	}

	makeSpec { | key, spec |
		/*
		If spec is given:
		- convert to spec (in case symbol or array)
		- if no spec under key in specs, then store new spec under key in specs
		If spec is not given:
		- try to get it from specs
		- if not in specs: try to get it from key
		- if not there either, use nil.asSpec;
		*/
		var storedSpec;
		storedSpec = specs[key];
		if (spec.notNil) {
			spec = spec.asSpec;
			specs[key] = spec;
		}{
			spec = storedSpec;
			spec ?? { spec = key.asSpec ?? { nil.asSpec } };
		};
		^spec;
	}

	simpleGuiLayout {
		^VLayout(*(event.keys.asArray.sort.collect(this.numSlider(_))));
	}

	numSlider { | key, spec, decoratorFunc |
		^this.ctlSpecView(key, this.makeSpec(key, spec), Slider,
			decoratorFunc ?? {{ | argKey, argView |
				HLayout(
					StaticText().string_(argKey),
					[argView.orientation_(\horizontal), s: 5],
					[this.numberBox(key), s: 1]
				);
			}}
		);
	}

	vknob { | key, spec |
		^this.knob(key, spec, { | argKey, argView | VLayout(argView, StaticText().string_(argKey)); })
	}
	knob { | key, spec, decoratorFunc |
		^this.ctlSpecView(key, this.makeSpec(key, spec), Knob, decoratorFunc);
	}

	ctlSpecView { | key, spec, viewClass, decoratorFunc |
		var view, value;
		view = this.makeView(viewClass ? Slider);
		spec = spec.asSpec;
		value = event[key];
		value !? { view.value = spec.unmap(value) };
		view.addNotifier(event, key, { | val | view.value = spec.unmap(val) });
		view.action = { | me | this.put(key, spec.map(me.value)) };
		decoratorFunc = decoratorFunc ?? {{ | argKey, argView |
			HLayout(StaticText().string_(argKey), argView);
		}};
		^decoratorFunc.(key, view);
	}

	// ============== MIDI and OSC ==============
	addMIDIarray { | ... keysNums |
		keysNums do: { | keyNum | this.addMIDI(keyNum[0], nil, keyNum[1]) };
	}

	addMIDI { | key, spec, msgNum, chan, msgType = \control, srcID, argTemplate, dispatcher |
		var midiFunc;
		spec = this.makeSpec(key, spec);
		midiFunc = MIDIFunc({ | val |
			{ this.put(key, spec.map(val / 127)) }.defer(0);
			},
			msgNum, chan, msgType, srcID, argTemplate, dispatcher
		);
		this.connectMIDIFunc(midiFunc, key);
		^midiFunc;
	}

	connectMIDIFunc { | midiFunc, key |
		midiFunc.addNotifier(this, \enableMIDI, { | argKey |
			if (argKey.isNil or: { argKey === key }) { midiFunc.enable };
		});
		midiFunc.addNotifier(this, \disableMIDI, { | argKey |
			if (argKey.isNil or: { argKey === key }) { midiFunc.disable };
		});
	}

	enableMIDI { | key | this.changed(\enableMIDI, key) }
	disableMIDI { | key | this.changed(\disableMIDI, key) }

	addOSCarray { | ... keysMsg |
		keysMsg do: { | keyMsg | this.addOSC(keyMsg[0], nil, keyMsg[1]) };
	}

	addOSC { | key, spec, path, srcID, recvPort, argTemplate, dispatcher |
		var oscFunc;
		spec = this.makeSpec(key, spec);
		oscFunc = OSCFunc({ | msg |
			{ this.put(key, spec.map.msg[1]); }.defer(0);
		}, path ? key, srcID, recvPort, argTemplate, dispatcher);
		this.connectOSCFunc(oscFunc, key);
		^oscFunc;
	}

	connectOSCFunc { | oscFunc, key |
		oscFunc.addNotifier(this, \enableOSC, { | argKey |
			if (argKey.isNil or: { argKey === key }) { oscFunc.enable };
		});
		oscFunc.addNotifier(this, \disableOSC, { | argKey |
			if (argKey.isNil or: { argKey === key }) { oscFunc.disable };
		});
	}

	enableOSC { | key | this.changed(\enableOSC, key) }
	disableOSC { | key | this.changed(\disableOSC, key) }

}


/* Parameter for handling audio buffers in scripts
0508 and later
*/

BufParameter : Parameter {
	var buffer, samples;
	init { | argAction |
		super.init(argAction);
		samples = Samples(script.session.server);
//		samples.addDependant { | ... args | args.postln };
		script.hasBufferInput = true;
	}
	makeDefaultAction { | envir |
		// the default action is different for TrOutput
		^{ | val ... args |
			envir[name] = val;
			script.setProcessParameter(name, val, *args);
			script.changed(name, val, *args);
//			thisMethod.report(val, *args);
		};
	}
	buffer_ { | argBuffer |
//		thisMethod.report(argBuffer);
		buffer = argBuffer;
		this.set(argBuffer.bufnum);
//		this.changed; // incomplete ...!
	}
	makeGui { | gui, adapterEnvir |
		var label, /* bufsink, */ numbox, menu, adapter;
		label = SCDragSink(gui, Rect(0,0,100,20)).string_(name)
			.background_(Color(0.9, 0.9, 0.2, 0.3));
/*		bufsink = SCDragSink(gui, Rect(0,0,140,20))
			.string_(" NO BUFFER ")
			.background_(Color(0.3, 0.6, 0.9))
			.font_(Font("Helvetica", 10))
			.canReceiveDragHandler_({ this.canReceiveDragHandler })
			.receiveDragHandler_({ this.receiveDragHandler });
*/		menu = SCPopUpMenu(gui, Rect(0, 0, 140, 20));
		menu.items = samples.buffers.keys.asArray.sort;
		menu.font = Font("Helvetica", 10);
		menu.action = {
			var bnum, bname;
			bname = menu.items[menu.value];
			buffer = samples.at(bname);
//			this.set(buffer.index, )
			bnum = buffer.bufnum;
			action.(bnum);
			script.changed(name, bnum, bname);
		};
		menu.keyDownAction =  { | me, char, mod, unicode |
			// do not react to space key bubbled from top view!
			//	if (char == $ , { me.valueAction = me.value + 1; ^me });
			switch (char,
				$\r, { me.valueAction = me.value + 1; },
				$\n, { me.valueAction = me.value + 1; },
				3.asAscii, { me.valueAction = me.value + 1; },
				{
					switch (unicode,
						16rF700, { me.valueAction = me.value + 1; },
						16rF703, { me.valueAction = me.value + 1; },
						16rF701, { me.valueAction = me.value - 1; },
						16rF702, { me.valueAction = me.value - 1; }
					)
				}
			)
		};
		numbox = SCNumberBox(gui, Rect(0,0,50,20));
		numbox.canFocus = false;
		numbox.value = script.envir[name];
		adapter = { | sender, whatChanged, args |
			switch (whatChanged,
				\bufferLoaded, {
//					thisMethod.report(sender, whatChanged, args, buffer, buffer === args);
					if (args === buffer) {
						this.buffer = buffer;	// resets number! Important!
					}
				},
				\bufList, {
					{ menu.items = args; }.defer;
					// must set selection to actual buffer on menu! : ...
				},
				\deleted, {
					thisMethod.report(this, \deletedBuffer, args); // since deleted: 
					script.set(name, 0); // set to empty buffer
				}
			);
		};
		samples.addDependant(adapter);
		menu.onClose = { samples.removeDependant(adapter); };
		// update slider knob color when midi responder activation state changes: 
		// TODO: examine if BufParameter is midiable, use label color to indicate active status
/*		adapterEnvir[midiName] = { | isOn |
			if (isOn) {
//				menu.knobColor = Color.red;
			}{
//				menu.knobColor = Color.clear;
			}
		};
*/		adapterEnvir[name] = { | bufnum |
			{
				var bufname;
				numbox.value = bufnum = bufnum.round(1).asInteger;
				buffer = samples.buffers.detect {|b| b.bufnum == bufnum };
				if (buffer.notNil) {
					bufname = samples.buffers.findKeyForValue(buffer);
					menu.value = menu.items.indexOf(bufname) ? 0;
				};
			}.defer;
		};
		// update label color when midi responder activation state changes: 
		// perform update of your gui after construction, thereby setting gui items
		script.changed(name, script.envir[name]);
	}
	labelBackground { ^Color.yellow.alpha_(0.1) }
	canReceiveDragHandler { | dragsink, numbox, gui |
		var object;
		object = SCView.currentDrag;
		if (object.isKindOf(MIDIResponder)) { ^true };
		^object.isKindOf(Buffer);
	}
	receiveDragHandler {
		if (SCView.currentDrag.isKindOf(MIDIResponder)) {
			this.setMIDIResponder(SCView.currentDrag);
		}{
			action.(SCView.currentDrag.bufnum ? 0);
		}
	}
	// --------------- Preset saving / loading ---------------
	getPreset {
	// return data for saving current setting as preset
	// return name of buffer as identifier instead of numeric value
		^if (buffer.notNil) { buffer.path.fileSymbol } { nil };
	}
	setPreset { | argValue |
		// restore value from data saved on preset.
//		thisMethod.report(argValue, Samples.all[script.session.server][argValue.asSymbol]);
		this.set(Samples.all[script.session.server][argValue.asSymbol].bufnum);
// there is discrepancy if server not running so kludge here:
		buffer = Samples.all[script.session.server][argValue.asSymbol];
// could not solve the update 081025 ...
//		script.changed(name, buffer.bufnum, argValue.asSymbol);
//		thisMethod.report("after the fateful:", buffer);
	}
	// --------------- MIDI ---------------
	// attempt at midiability: 
/*	canAutoMidiSetup {
		^false;
	}*/
	// attempt at midiability ...
	activateMidi {
		super.activateMidi;
		this.update(Samples.default, \bufList);
		samples.addDependant(this);
	}
	deactivateMidi {
		super.deactivateMidi;
		samples.removeDependant(this);
	}
/*
	update { | who, what |
		// buf parameters add themselves to Samples so that they can update 
		// their menu and specs when a buffer is loaded or freed. 
		// this makes it possible to scroll through the list of samples.buffers
		// when midi is received on this parameter
		this.erroriFyOnPurpose; 
		thisMethod.report(who, what);
		switch (what,
			\samples, { spec.maxval = (who.buffers.size - 1) max: 0; }
		)
//		[this, who, what].postln;
//		if (what == \samples) {
//		spec.maxval = (who.buffers.size - 1) max: 0;
//		}
	}
*/
}

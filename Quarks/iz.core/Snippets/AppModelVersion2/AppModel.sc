/* IZ Fri 31 August 2012 10:08 PM EEST

Redo of AppModel, with radically redone classes for values and views: Value and Widget classes.
*/

AppModel {
	classvar <>enabled;	// previously enabled AppModel: Disabled when the next one becomes active
	var <values;  /* IdentityDictionary: Adapters holding my values per name */

	*new { | ... args | ^this.newCopyArgs(IdentityDictionary.new, *args); }

	at { | name | ^values[name].value; }
	put { | name, value | this.getAdapter(name).valueAction = value } 
	getValue { | name, adapter |
		// Access adapter. Create one only if it does not already exist
		var value;
		value = values[name];
		if (value.isNil) { // if it does not exist, create it and set its adapter variable.
			value = Value(this);
			values[name] = value;
			adapter !? {
				value.adapter = adapter;
				adapter.container = value;
			};
			^value;
		}{
			^value; 	// Else return it as is
		}
	}

	// removing connections and inputs
	objectClosed { // not used yet?
		super.objectClosed;
		values do: _.objectClosed;
	}

	// enabling and disabling MIDI and OSC input
	enable { | disablePrevious = false |
		if (disablePrevious) { enabled !? { enabled.disable }; };
		values do: _.enable;
		enabled = this;
	}
	disable {
		values do: _.disable;
		enabled = nil;
	}

	updateListeners { values do: _.updateListeners }

	// =========== Adding views and windows ============
	window { | windowInitFunc |
		AppNamelessWindow(this, windowInitFunc);
	}

	stickyWindow { | owner, name = \window, windowInitFunc |
		AppStickyWindow(this, owner ? this, name, windowInitFunc);
	}

	windowClosed { | window, action |
		this.addNotifier(window, \windowClosed, { | widget | action.(widget) })
	}
	windowToFront { | window, action |
		this.addNotifier(window, \windowToFront, { | widget | action.(widget) })
	}
	windowEndFront { | window, action |
		this.addNotifier(window, \windowEndFront, { | widget | action.(widget) })
	}

	view { | view | ^AppNamelessView(this, view) }

	numberBox { | name | ^Widget(this, name, NumberBox()).simpleNumber; }
	knob { | name, spec | ^Widget(this, name, Knob()).mappedNumber(spec); }
	slider { | name, spec | ^Widget(this, name, Slider()).mappedNumber(spec); }
	button { | name, action | ^Widget(this, name, Button()).action_(action); }
	textField { | adapterName | 
		^Widget(this, adapterName, TextField()).text;
	}
	staticText { | adapterName, string = "<empty>" |
		^Widget(this, adapterName, StaticText()).text.do({ | me | 
			me.value.adapter.string_(me, string);
		});
	}
	textView { | adapterName, viewName | 
		^Widget(this, adapterName, TextView()).textView;
	}
	listView { | name, getItemsFunc |
		^Widget(this, name, ListView()).list(getItemsFunc);
	}
	popUpMenu { | name, getItemsFunc |
		^Widget(this, name, PopUpMenu()).list(getItemsFunc);
	}
	listIndex { | name, view, startAt = 1 | 
		^Widget(this, name, view ?? { NumberBox() }).listIndex(startAt);
	}
	listSize { | name, view |
		^Widget(this, name, view ?? { NumberBox() }).listSize;
	}
	listItem { | name, view, getItemFunc |
		^Widget(this, name, view ?? { TextField() }).listItem(getItemFunc);
	}

	// following need review - possibly their own adapter classes
	
/* // TODO
	rangeSlider { | name | ^AppValueView(this, name, RangeSlider()); }
	slider2D { | name | ^AppValueView(this, name, Slider2D()); }
	dragSource { | name | ^AppView(this, name, DragSource()); }
	dragSink { | name | ^AppView(this, name, DragSink()); }
	dragBoth { | name | ^AppView(this, name, DragBoth()); }
	scopeView { | name | ^AppView(this, name, ScopeView()); }
	multiSliderView { | name | ^AppValueView(this, name, MultiSliderView()); }
	envelopeView { | name | ^AppValueView(this, name, EnvelopeView()); }
	soundFileView { | name | ^AppView(this, name, SoundFileView()); }
	movieView { | name | ^AppView(this, name, MovieView()); }
*/	
	addMIDI { | specs |
		specs pairsDo: { | key, spec |
			this.getValue(key).addMIDI(*spec);
		}
	}
}



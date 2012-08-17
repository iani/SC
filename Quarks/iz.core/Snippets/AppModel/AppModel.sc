/* IZ Wed 15 August 2012  9:14 PM EEST
Trying whole different approach to the whole widget / value notification thing.

User acts on view, setting a value 
	view maps value (using an adapter = spec or other stuff, possibly function);
	view sends app this message: app.setValue(key, value);


Widgets that need to add extra actions like setting their specs, changing their state, etc., 
can do it like this: 

widget.addAction(<commandname>, action);

So any widget can ask to be specifically notified when it needs to change something 
different than its value. It needs not and should not store itself in the AppModel.

First example: 

//:
AppModel().window({ | w, app |
	w.view.layout = VLayout(
		app.knob(\test).spec_(\freq).view,
		app.numberBox(\test).view
	)
});
//:

*/

AppModel {

	var <values;  /* IdentityDictionary: Adapters holding my values per name */
	
	*new { ^this.newCopyArgs(IdentityDictionary.new); }

//	at { | name | ^values[name].value; }
	at { | name | ^values[name].value; }
	// creates value adapter if not present
//	putValue { | name, value | this.getAdapter(name).valueAction = value }
	put { | name, value | this.getAdapter(name).valueAction = value }
 
	getAdapter { | name, innerAdapter |
	// Access adapter.  
	// if it does not exist, create it and set its adapter variable.
	// Else return it as is. 
		var adapter;
		adapter = values[name];
		if (adapter.isNil) {
			adapter = Adapter(this);
			values[name] = adapter;
			adapter.adapter = innerAdapter;
		};
		^adapter;
	}

	getViewValue { | name | values[name].notify(\at) } // so far only used by AppTextView


	// =========== Adding views and windows ============
	window { | windowInitFunc, onCloseFunc |
		AppNamelessWindow(this, windowInitFunc, onCloseFunc);
	}
	
	view { | view | ^AppNamelessView(this, view) }

	numberBox { | name | ^AppValueView(this, name, NumberBox()); }
	knob { | name, spec | ^AppSpecValueView(this, name, Knob()).mapper(spec); }
	slider { | name, spec | ^AppSpecValueView(this, name, Slider()).mapper(spec); }
	button { | name | ^AppValueView(this, name, Button()); }
	popUpMenu { | name | ^AppItemSelectView(this, name, PopUpMenu()); }
	listView { | name | ^AppItemSelectView(this, name, ListView()); }
	textField { | name | ^AppTextValueView(this, name, TextField()); }
	staticText { | name | ^AppStaticTextView(this, name); }
	textView { | name | ^AppTextView(this, name); }
	// following need review - possibly their own adapter classes
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
}



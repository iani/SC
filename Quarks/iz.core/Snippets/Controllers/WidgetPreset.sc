/* IZ Sun 12 August 2012 10:41 PM EEST

Store the current sate of a Widget and restore it upon request.



*/

WidgetPreset {
	var <widget;
	var <>action, <>updateFunc, <>spec;
	var <value, <inputs;
	var <proxySpace, <proxy, <proxySpecs;
	var <viewValue, <items;

	*new { | widget |
		^super.new.store(widget);
	}

	store { | argWidget |
		var view;
		widget = argWidget;
		view = widget.view;
		value = widget.value;
		if (view respondsTo: \items) { items = view.items; };
		viewValue = widget.view.value;
		inputs = widget.inputs;
		spec = widget.spec;
		proxy = widget.proxy;
		proxySpace = widget.proxySpace;
		proxySpecs = widget.proxySpecs;
		action = widget.action;
		updateFunc = widget.updateFunc;
	}

	restore {
		var view;
		view = widget.view;
		widget.setValue = value;
		if (view respondsTo: \items) { view.items = items; };
		view.value = viewValue;
		widget.inputs = inputs;
		widget.spec = spec;
		widget.proxy = proxy;
		widget.proxySpace = proxySpace;
		widget.proxySpecs = proxySpecs;
		widget.action = action;
		widget.updateFunc = updateFunc;
	}	
}


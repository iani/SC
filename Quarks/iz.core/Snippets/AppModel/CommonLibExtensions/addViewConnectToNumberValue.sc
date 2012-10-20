/* IZ Mon 10 September 2012  8:23 AM BST
Let a view decide how it connects to its value/adapter.
Since View Classes from different GUI kits have no way of knowing what "type of view" they are (Slider, Knob, NumberBox etc.), the methods are provided for all 3 sets of Classes dealing with single numeric values here.


 */
/*
+ SCSlider {
	connectToNumberValue { | adapter, widget |
		this.action = { adapter.standardizedValue_(widget, this.value) };
		widget.updateAction(\number, { this.value = adapter.standardizedValue });
	}
}

+ SCKnob {
	connectToNumberValue { | adapter, widget |
		this.action = { adapter.standardizedValue_(widget, this.value) };
		widget.updateAction(\number, { this.value = adapter.standardizedValue });
	}
}


+ SCNumberBox {
	connectToNumberValue { | adapter, widget |
		this.action = { adapter.value_(widget, this.value) };
		widget.updateAction(\number, { this.value = adapter.value });
	}
}
*/

+ QSlider {
	connectToNumberValue { | adapter, widget |
		this.action = { adapter.standardizedValue_(widget, this.value) };
		widget.updateAction(\number, { this.value = adapter.standardizedValue });
	}
}

+ QKnob {
	connectToNumberValue { | adapter, widget |
		this.action = { adapter.standardizedValue_(widget, this.value) };
		widget.updateAction(\number, { this.value = adapter.standardizedValue });
	}
}


+ QNumberBox {
	connectToNumberValue { | adapter, widget |
		this.action = { adapter.value_(widget, this.value) };
		widget.updateAction(\number, { this.value = adapter.value });
	}
}

/*
+ JSCSlider {
	connectToNumberValue { | adapter, widget |
		this.action = { adapter.standardizedValue_(widget, this.value) };
		widget.updateAction(\number, { this.value = adapter.standardizedValue });
	}
}

+ JKnob {
	connectToNumberValue { | adapter, widget |
		this.action = { adapter.standardizedValue_(widget, this.value) };
		widget.updateAction(\number, { this.value = adapter.standardizedValue });
	}
}


+ JSCNumberBox {
	connectToNumberValue { | adapter, widget |
		this.action = { adapter.value_(widget, this.value) };
		widget.updateAction(\number, { this.value = adapter.value });
	}
}
*/

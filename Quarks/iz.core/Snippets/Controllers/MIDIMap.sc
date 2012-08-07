/* IZ Sat 04 August 2012  5:16 PM EEST

Untested draft. 

WILL BE REPLACED BY Object:addMIDI and Widget:addMIDI !!! 

*/

MIDIMap {
	var <>device; 	// MIDIdevice sending MIDI messages via NotificationCenter
	var <>model; 		// Object handling the received messages (e.g. a ProxyCodeEditor)
	var <>map;		// IdentityDictionary with one action for each notification key

	*new { | device, model, map |
		^this.newCopyArgs(device, model, map ?? { IdentityDictionary.new }).init;
	}

	init {
		// Deactivate and remove objectClosed connection when the model closes
		model onObjectClose: { this.closed };
	}

	activate {
		map keysValuesDo: { | widget, controller |
			device.addWidget(model.widget(widget), controller);
		}
	}

	deactivate {
		map keysDo: { | widget, controller |
			device.removeWidget(model.widget(widget), controller);
		}
	}

	closed {
		// Deactivate and remove objectClosed connection when the model closes
		this.deactivate;
		this.objectClosed; // remove objectClosed notifier connection from model
	}
}
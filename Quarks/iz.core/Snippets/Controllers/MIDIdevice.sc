/* IZ Sat 04 August 2012  1:30 PM EEST

THIS CLASS WILL BE REMOVED AND ITS FUNCTIONALITY TRANSFERRED TO WIDGET IN A MUCH SIMPLIFIED FORM. 

NOTE: BEING REFACTORED - STILL INCONSISTENT!!!

Connect and deconnect MIDI input from a named controller of a named MIDI input device to any GUI or other item. Divert MIDI input to one active window or other type of item amongst many, helping to switch amongst different items from one item to the next.  

The purpose of MIDIDevice can be made clear by following situation: 

Usually a performance may involve many different synthesis processes, where each process has several parameters that could be controlled via MIDI. If one attempted to assign a separate MIDI controller item (such as a knob or a slider) for each control parameter used during the performance, then the total number of midi controller items required could be very high. This poses two problems: First the number of parameters may be higher than that of available hardware controllers, and secondly even if all parameters can be assigned to separate controllers (for example by using several "banks" in one control hardware device), the resulting setup may be too complex to learn effectively in the time available. An alternative approach to the problem of too many control parameters is to group the parameters into several subgroups, where only one subgroup is used at each moment. Then one could use one single hardware control device to control the parameters of each one of those groups in turn. For example, a performance may be using a single midi control device with 16 controller items, and start in the first 3 minutes with  a subgroup requirihg 16 control parameters, and the next 2 minutes switch to a different subgroup with 10 parameters different from the previous one, and so on. In this case, it would be useful to be able to easily switch the controls from the MIDI device between groups as one changes in the performance between one subgroup and the other. One way to do this might be to have a different window open for each subgroup, and to automatically direct the control from the MIDI device to the foremost window. Then, selecting a control group window will automatically activate the windows' controls to receive input from the MIDI device, and deactivate the controls of the previously active window. 

MIDIdevice makes it possible to name the controller items of a MIDI input device, to assign GUI or other items to be controlled by each named controller item, and also to switch between groups of such assignments. 


Usage: 

=== 1: Creating a configuration for a MIDI input device ===


MIDIdevice.activate(item, activateAction, deactivateAction, device);

	activateAction, deactivateAction, device are optional arguments. 

activateAction: function to call when MIDI input is activated. Default: { i.activateMIDI(this) }
deactivateAction:  function to call when MIDI input is activated. Default: { i.deactivateMIDI }
device: (Symbol): name of device to receive MIDI from. Default: 'default'

Any previously active item will be deactivated, and the new item i will be receiving MIDI input instead. 
If item was not yet on the HandleMIDI list, it will be added. 

2. When an item i wants to relinquish input it calls this: 

MIDIdevice.deactivate(i);

3. When an item wants to remove itself from the list of items of a MIDIDevice, it calls this: 

MIDIDevice.remove(g, device)

Then MIDI input is deactivated from this item (if active) and the item is removed from the list of possible MIDI receivers. 

=== Part 2: Responding to MIDI input ===

An object can attach actions to messages received from MIDI input via MIDIDevice by using MIDIMap.
MIDIMap is a Dictionary (Event) whose keys correspond to the keys of the midiMap dictionary here, and whose actions are the actions to be carried out by the MIDIMap object. The MIDIMap connects itself to the MIDIDevice by adding one notifier from the MIDIDevice to MIDIMap actions for each key in the MIDImap, using the NotificationCenter.  

*/

MIDIDevice {
	classvar devices; /* dictionary of MIDIdevices, stored under their names (symbols).
	A client item i can add itself to any device by name. 
	Being controlled by several devices at the same time is possible. 
	The default device has key 'default'.  */	

	var <name;		// name of this device
	var <device;		// MIDIEndPoint instance. Its uid is used as MIDIFunction src parameter
					// If nil, input from all devices is accepted
	var <src;			// the uid of the device. If nil, input from all devices is accepted
	var <>midiFuncs;	/* dictionary (Event) with names of controllers of the device 
			and a MIDIFunc for each controller */
	var <active;		// the currently active item

	*scan {
		// EXPERIMENTAL!!!
		/* scan MIDIIn for devices, and then successively record their controllers */
//		MIDIIn.connectAll; 
		/* 	under SC 35.3 MacOS 10.8 MIDIIn.connectAll will crash SC most of the time. 
			Workaround: Include this line in the startup file: 
			{ MIDIIn.connectAll; }.defer(1);
			This does not crash at startup! */
			
	}

	*default { ^this.getDevice(\default) }

	*new { | name, src, midiFuncs |
		/* MIDIFunc instances is an array of form [key1: spec, key2: spec ...] each key is the name
			under which the responder will be notifying its midi input, and each spec
			can be one of the following: 
			An Integer ccNum: Creates MIDIFunc({...}, ccNum)
				MIDIFunc values from MIDI channel 0 and number num are broadcast through 
				this.notify(key, val), where val is the CC value 
			An array [ccNum, chan] Creates MIDIFunc({...}, ccNum, chan)
				MIDIFunc values from MIDI channel chan and number num are broadcast through 
				this.notify(key, val), where val is the CC value 
			An array [methodName, num chan, srcID, ... ]
				Sends the message specified by methodName to MIDIFunc with the rest of the 
				elements of the array as arguments (see MIDIFunc help for arguments)
			The function argument for MIDIFunc is created by the present new method. 

		*/
		^this.newCopyArgs(name, src).init(midiFuncs);
	}

	init { | argSpecs |
		src !? { this.initSrc };
		/* MIDIMap instances activate their key-action pairs by adding the present 
		instance as notifier for each key-action pair */
		// THIS IS STILL JUST TESTING
		midiFuncs = (
			test: MIDIFunc.cc({ | value, num, chan, src |
				this.notify(\test, [value, num, chan, src])
			}) // , ... ... ... 
		);
	}

	*addItem { | item, onActivate, onDeactivate, device = \default |
		// add an item (model) to the 
		this.getDevice(device).addItem(item, onActivate, onDeactivate)
	}
	

	*remove { | item, device = \default | this.getDevice(device).remove(item); }
	*activate { | item, device = \default | this.getDevice(device).activate(item); }
	*deactivate { | item, device = \default | this.getDevice(device).deactivate(item); }

	*getDevice { | deviceName = \default |
		var device;
		device = this.devices[deviceName];
		if (device.isNil) {
			device = this.new(deviceName);
			devices[deviceName] = device;
		};
		^device;
	}

	*devices {
		if (devices.isNil) { this.createDefault };
		^devices;
	}

	*createDefault {
		var default;
		MIDIIn.connectAll;
		// INCOMPLETE; 
	}

	add { | item, onActivate, onDeactivate |
		item.addNotifier(this, \activateMIDI, {
			item.notify(\activateMIDI, item);
			onActivate.(item, this)
		});
		item.addNotifier(this, \deactivateMIDI, {
			item.notify(\deactivateMIDI, item);
			onDeactivate.(item, this)
		});
	}

	remove { | item |
		if (active === item) { this.deactivate(item) };
		item.removeNotifier(this, \activateMIDI);
		item.removeNotifier(this, \deactivateMIDI);
	}


	activate { |  item |
		active !? { this.deactivate(active) };
		active = item;
		item.notify(\activateMIDI);
	}

	deactivate { | item |
		item.notify(\deactivateMIDI);
		if (active === item) { active = nil };		
	}


	addWidget { | widget, controller |
		/* add a widget to receive MIDI notifications from one of my controllers */
		widget.addNotifier(this, controller, { | value, num, chan, src |
			// map value to range 0..1. Defer to permit update of GUI items
			{ widget.valueAction_(value / 127) }.defer;
		})
	}

	removeWidget { | widget, controller |
		widget.removeNotifier(this, controller);
	}
	
	//: Utilities for checking MIDI input
	addCC { | controller = 'cc', value, num, chan, src |
		
	}

	addNoteOn { | controller = 'noteOn', value, num, chan, src |
		
	}
}

/*

Interface: A Class for creating interfaces to a model.
Basic idea

	* values of a model are stored as plain values, not as objects encapsulating a value.
	* The mechanism for changing values in the model and for getting updates from the model to the interfaces when this model changed is kept as simple as possible:
	* All objects that change a value use the method anIntf.put(key, value). This method calls this.changed(\key, value) on anIntf before exiting. An interface that changes a value at a key will always also receive update(anIntf, key, value) from the Intf when the value from that Intf changes.

*/

Interface {
/* // no these are stored in Updater and or Prototype and or Configuration! 
	classvar <configurationTemplates; // becomes parent of builders;
	classvar <specTemplates;	// becomes parent of specs
*/
	var model; /* (Event): Store values in an event, where each key stores a value. The values are stored as are, not encapsulated in some other object. */
	var prototypes; /* Event holding default specs for interfaces */
	var configurations; /* Event storing a number of interface makers (GUI building functions or other stuff). The default interface builder is stored under key default. */
	var updaters; /* (Event) Under each key stores a set of objects that:
		 1. Sets the value stored in model under the key to some object, using put(key, value).
		 2. Receives update(key, value) messages when the value stored under that key changes. Such objects can be: a gui widget, a MIDI send action, an OSC send action, an action changing something on the sound synthesis server, an OSC responder, a MIDI responder, a Node, a Server etc. For the sake of simplicity, no distinction is made between objects that set a value and objects that receive updates of a value, but all interface objects both set and receive updates. */
	var snapshots; /* Event holding a number of events with values for the keys, where each snapshot event is stored under one key. The default values for the Intf are stored here under snapshot named 'default' */

	*new { | ... specs |
		^super.new.init(specs);
	}
	init { | specs |
		model = ();
		prototypes = Prototype.new(this);	// should this go
		/*
		*/
		updaters = Updater(this, specs); 
		configurations = Configuration.new(this);
		specs do: this.makeInterfaces(_);
		snapshots.put(\default, event.copy);
	}
	// add a key with optional interfaces and defaults from a specification
	makeInterfaces { | interfaceSpecs |
		var newInterfaces, default;
	}
	/*  To write a new value to a key, use method put(key, value). This method calls this.changed(key, value). */
	put { | key, value |
		event.put(key, value);
		this.changed(key, value);
	}
	/* rewrites the standard update mechanism to operate on a single controller stored in interfaces */
	changed { | key, value | interfaces.update(key, value); }
	
	/* retrieve a value stored under a key */
	at { | key |
		^event[key];
	}
	/* Add interface at a key */
	addInterface { | key, interface |
		interfaces.addAtKey(key, interface);
	}
	/* Remove an interface from a key */
	removeInterface { | key, interface |
		
	}
	/* create interface stored under key. Default value for key is 'default'. */
	build { | key = 'default' |
		configurations[key].build(this);
	}
	/* method builder_(a builder) set the default interface */
 	builder_ { | aBuilder |
		configurations[\default] = aBuilder;
	}
	/* add a builder under key */
	addBuilder { | key, builder |
		configurations[key] = builder;
	}
	reset {
		model = snapshots[\default].copy;
		this.updateAll;
	}
	updateAll {
		model keysValuesDo: { | key, val | this.changed(key, val) };
	}
	// The following is an idea - I still do not know how to implement it.
	/* keep all existing interfaces but disable the connections put / update between them and the model. */
	deactivate {
		
	}
	/* reactivate the connection to existing interfaces */
	activate {
		
	}

}

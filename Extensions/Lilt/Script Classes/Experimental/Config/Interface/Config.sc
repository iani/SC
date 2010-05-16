/* iz Sunday; September 28, 2008: 2:14 PM (redo!)
The Interface idea is based on Conductor (see Conductor Quark by R. Kuivila). but it aims to:

    * Introduce a freer and terse scheme for specifying the elements of an Interface.
    * Redo the value update mechanism to make it simpler and more flexible and to avoid the two-way synch menchanism of Conductor which has created problems for me.
    * Enable one to create specs for several different guis, as well as osc and midi bindings for the same instance of Interface. 

*/

Config {
	classvar defaultSpecs;
	var event;		/* keys with ValueAdapter objects that communicate to interface objects */
	var specs;		/* specs for ControlSpec */
	var interfaces;	/* specifications for creating gui and other interface groups */
	var snapshots;		/* snapshots of event's values */
	
	*new { | ... keySpecs |
		^super.new.init.add(*keySpecs);
	}
	init {
		event = ();
		specs = Prototype.new;
		interfaces = InterfaceSpecs.new;
		snapshots = ();
	}
	add { | ... keySpecs | keySpecs do: this.addKey(*_) }
	addKey { | key ... keySpecs |
		ValueAdapter(this, key, keySpecs);
	}
	makeInterfaces { | ... keySpecs | keySpecs do: this.makeInterfaceFor(_) }
	makeInterfaceFor { | keySpecs |
		interfaces
	}
	
}


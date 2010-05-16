/* iz Saturday; September 27, 2008: 4:20 PM
Part of Interface.
Holds configurations for building sets of interfaces (GUI, MIDI, OSC or other) for an Interface instance. 
*/


Configuration : Event {
	classvar defaults;
	var interface, specs, gui, midi, osc;

	*initClass {
		defaults = (
/*			\gui, ...
			\midi, ...
			\osc,  ...
*/
		)
	}
	*new { | interface | // ... specs? 
		
	}
}



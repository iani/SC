/* iz Sunday; September 28, 2008: 4:09 PM

Specs for creating interfaces for Config

The defaults is a parent holding the default functions for building an interface for an object. 
Each Interface instance has defaults as parent. 


Still developing this ... Need to specify this going alternately top-bottom and bottom-up.

Top - bottom: 
The instance variable interfaces contains an event with different interface configurations under separate keys. 


The 

*/

InterfaceSpecs : Event {
	classvar defaults; 	/* holds defaults for */
	var keys;				/* like in ConductorGUI */
	
	*new {
		
	}
	
	/* make a gui for these interface objects */
	show {}
	/* hide the gui for these interface objects */
	hide {}
	/* connect the objects of this interface to the ValueAdapters - activate their
		write and their update actions */
	activate {}
	/* disconnect the objects from the interface */
	deactivate {}
}
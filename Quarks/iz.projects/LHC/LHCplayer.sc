/*
2012 07 13
Play some sounds in response to counter values and symbols received from an FSMdecoder played through a LHC interface. 

*/

LHCplayer : Event {

	var <>soundOn = false; 

	*new { | event |
		^super.new composeEvents: event;
	}
	
	update { | sender, command, argument |
//		postf("lhc player received update: %, %, %\n", sender, command, argument);		this.perform(command, *argument);
	}
	
	reset { | sender, command, argument |
		/* reset method is defined in Object. Therefore redefine it here to make it
		   check if there is a custom reset function stored in the event */
		var reset;
		reset = this[\reset]; 
		if (reset.notNil)  { 	// if custom reset function present, use it
			reset.value(this, *argument)
		}{					// else use default reset action;
			this.soundOn = false;
		}
	}
}
/*
2012 07 13
Play some sounds in response to counter values and symbols received from an FSMdecoder played through a LHC interface. 

*/

LHCplayer : Event {
	
	var <>sound = 0;

	*new { | event |
		^super.new composeEvents: event;
	}
	
	update { | sender, command, argument |
		postf("lhc player received update: %, %, %\n", sender, command, argument);
		
		if (command === \reset) {
			this.reset;	
		}{
			this.perform(command, *argument);
		};
	}
	
	reset { this.sound = 0; }
	
}
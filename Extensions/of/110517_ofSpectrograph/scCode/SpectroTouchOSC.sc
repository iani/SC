SpectroTouchOSC {
	*makeResponders {
		OSCresponder.new(nil, "/numTeeth", { arg time, resp, msg; 
				\pv_rectcomb_0.set(\numTeeth, msg[1].round);
				msg[1].round.postln;	
				} ).add;
	}	
}
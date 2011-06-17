

Ia1PreIntro {
	classvar <action;
	*load {
		action = SyncAction(\beats, { | beat |
			beat.postln;
			switch (beat,

					////////@0//////////
				0, {
					Ia1SynthDefs.load;
					Ia1Buffers.load;
					Ia1Busses.load;
				}
					        			
			)
		});
		
	}
	
	*unLoad{
	
	action.deactivate;
	
	}
	
}
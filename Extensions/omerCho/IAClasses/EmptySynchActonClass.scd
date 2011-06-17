/*

SA.load;
~part1 = SyncSender(Pbind.new);
~part1.start;
~part1.stop;
6
Ia1MainSA.unLoad;

*/

SA {
	classvar <action;
	*load {
		var s;
		
		s = Server.default;
		
		action =  SyncAction(\beats, { | beat ... otherStuff |
		//beat.postln;
		
			switch (beat,
				0, {
				},
				1, {
				},
				36, {
				},
				546, {
				},
				588, {
				},
				608, {
				},
				716, {
				}
			)  
		})
	}
	*unLoad{
	
	action.deactivate;
	
	}
	
}
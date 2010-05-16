/* iz Saturday; October 25, 2008: 3:12 PM


An Event that repeats a function 'action' at time intervals 'dt', while condition 'cond' returns true. 

This should become a simpler (?) version of Metro. For the moment Metro does the job. 

!!!!! INCOMPLETE !!!!!

*/

MEvent : Event {
	var <>action;
	var <>dt = 1;
	var <>cond = true;
	var <isRunning = false;

	start {
		if (isRunning) { ^this };
		isRunning = true;
		this.run;
	}

	run {
		if (isRunning) {
			this use: action;
			
		}
	}
	
	stop { isRunning = false; }


}
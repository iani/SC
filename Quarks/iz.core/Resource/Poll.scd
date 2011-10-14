/* 
A routine that polls data at a steady rate.
It restarts automatically when stopped by Cmd-. (It is can be stopped by sending it the message 'stop').

Rate is settable. 

Any clients that want to access the data, add functions to the Routine to do this. 
The functions are evaluated at each poll.

UNDER DEVELOPMENT

*/


Poll : UniqueRoutine {
	var <>rate, <>pollfunc, <actions, routine;
	
	
	stop {
		this.remove;	
	}	
}

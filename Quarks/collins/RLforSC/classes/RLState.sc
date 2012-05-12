//represents a short frame of audio, assuming perhaps 50 msec or 100 msec, or even 250 or 500 msec, assuming chordal data doesn't change much faster than this

//hmmm, 4 per second= 240 per minute. search gets longer as they accumulate! Need hashing, feature partition and other tricks. Can rebuild partitions off line after sessions, but must accumulate properly during a session.  

//trained on one hour of practice = 240*60 = 14400 to search every 250 msec unless can seriously cut down search time somehow!
//after one hour start removing oldest to avoid data explosion. 
//training data for function approximator which is set up during offline time?  


//need a null state of zero activity and to avoid that being continually overwritten; only add to database if had activity of some sort during the frame, and if it doesn't exist already; 
//could be nearly infinitely many silence to event transitions! 

//one state space for each key, metre? but then action might correspond to a new key and metre...

//sidestepping segmentation issues; implicit in chord detection and 
RLState {
	classvar <>bassindices= #[0,7,13,18,22,25,27];
	classvar <>alpha=0.5, <>beta=0.5; //for removal calculation
	 
	//features for matching; first two here are discrete? 
	var <>key;  //context from last 2 seconds
	var <>numonsets;  //0, 1-2, 3-4, 5+	
	var <>bassoctave; //0-6
	var <>spread;	    //0-6
	
	
	var <>density;	//num playing
	//additional context passive, along for ride!
	var <>listMIDInotes; //those playing, [duration to this point/to off during frame, midi note, velocity]
	var <>listOnsets; 	  //those starting- times, pitches, vels
	var <>keydist; //notion of quality/category of inner parts, functional or sensory consonance?
	//comparison of contribution now to prevailing tonality measure
	//var <>sync; //this frame compared to beat track
	 
	//var <>metre;
	//var <>mphase; //metrical phase 
	var <>treble; 
	var <>bass;
	var <>maxvelocity; //three levels 0-60, 60-100, 100-120 
	
	var <>action; //points to an action = another RLstate. If deleting, set this to nil!
	//action will persist in the database unless niled. If this state only exists as an action, its action field can be nil whilst something else still points to it. Eventually cast adrift when action set to nil. Can't iterate through an action sequence if links stop at some point. But no problem since just look up closest action in database!
	
	//var <>previous; //logically no need for double linked list
	
	var <>v=1.0; //value of state, trained by reinforcement learning variant, need to update more states at once following Dinerstein and Egbert 2005
	
	var <>framestart; 
	
	//*new {^super.new}
	
	
	//could compare notes using match function
	
	//can shift weights of dimensions as go as calculating each time; all 1.0 for now 
	//all params must be in range 0-1 for fair comparison? 
	//calculate metric
	proximity {|rlstate| 
		var densitydist, veldist, bassdist, trebledist, keydistdist; 
		
		densitydist = ((density - (rlstate.density)).abs.min(5))*0.2;
		veldist = (maxvelocity - (rlstate.maxvelocity));
		
		//max dist for piano 108-22 = 86
		bassdist = ((bass - (rlstate.bass)).abs.min(86))/86.0;
		trebledist = ((treble - (rlstate.treble)).abs.min(86))/86.0;
		
		keydistdist= (keydist - (rlstate.keydist));
		
		^((densitydist.squared) + (veldist.squared) + (bassdist.squared) + (trebledist.squared) + (keydistdist.squared))*0.2;  
	
	}
	
	
	getIndex {
		var tmp;
		
		tmp = (key*(4*28)) + (numonsets*(28)) + ((RLState.bassindices[bassoctave])+spread);
		
		^tmp;
	}
	
	//to force garbage collection
	//remove {
//	
//		action=nil; //now this state can only itself be an action, no forwards chain
//		
//		//if(previous.notNil,{previous.action= nil;});
//		//if(action.notNil, {action.previous = nil; action=nil;}); 
//	}
//	

	//according to paper should remove closest
	removalscore {|state, time| 
		 var age, score;
		 
		//combination oldest and closest and least valued = highest v
		//how to define old?  if <3 sec, 0, 3-30 sec , else log function with maximum?  
		
		age = time-framestart; 
		
		age= if(age<3.0,{0.0},{1.0-exp(0.01*((age-3.0).neg))});
		
		
		//equal weights... all factors are 0.0 to 1.0
		score = age + (this.proximity(state)) + v; 
	
		^score;
	}
	
	//playback any new notes; else select from playing notes; else silence
	//choose next events to schedule based on parameters
	//for now; playback some random subset of events? even, just playback actual note events...
	generate {
	var lasttime, tmp, tmp2, events; 
		
		lasttime= framestart; 
		
		if(listOnsets.notEmpty, {
		
		events= listOnsets.collect({|val| 
		
		tmp= val[0].max(framestart); //any preframe are assumed chord tones and played back together 
		
		tmp2= tmp - lasttime; 
		
		lasttime= tmp; 
		
		[tmp2, val[1], val[2]];
		});
		
		[\PLAYBACK, \listOnsets, events].postln;
		
		},{
		
		if(listMIDInotes.notEmpty,{
		
		
		},{
		//else silence
		
		});
		
		});
		
		if(events.notNil,{
		
		{
		
		events.do{|val|
		
			val[0].wait;
			Synth(\midisound,[\freq, (val[1]+12).midicps, \amp, ((val[2]).squared)*0.3]);
			
			};
		
		}.fork;
		
		});
	
	}
	
}

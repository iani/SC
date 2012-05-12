//represents a short frame of audio, assuming perhaps 50 msec or 100 msec, or even 250 or 500 msec, assuming chordal data doesn't change much faster than this

//hmmm, 4 per second= 240 per minute. search gets longer as they accumulate! Need hashing, feature partition and other tricks. Can rebuild partitions off line after sessions, but must accumulate properly during a session.  

//trained on one hour of practice = 240*60 = 14400 to search every 250 msec unless can seriously cut down search time somehow!
//after one hour start removing oldest to avoid data explosion. 
//training data for function approximator which is set up during offline time?  


//need a null state of zero activity and to avoid that being continually overwritten; only add to database if had activity of some sort during the frame, and if it doesn't exist already; 
//could be nearly infinitely many silence to event transitions! 

//one state space for each key, metre? but then action might correspond to a new key and metre...

//sidestepping segmentation issues; implicit in chord detection and 
RLState2 {
	//classvar <>bassindices= #[0,7,13,18,22,25,27];
	classvar <>alpha=0.5, <>beta=0.5; //for removal calculation
	 
	//features for matching; first two here are discrete? 
	var <>transpose;  //all materials are shifted to C then shifted back for playback in certain keys
	var <>keytype;
	var <>numonsets;  //0, 1-2, 3-4, 5-6, 7+	
	
	var <>register; //normal, full, low, high 
	
	var <>pcprofile; //corrected to base key already
	var <>density;	//num playing
	//additional context passive, along for ride!
	var <>listMIDInotes; //those playing, [duration to this point/to off during frame, midi note, velocity]
	var <>listOnsets; 	  //those starting- times, pitches, vels
	var <>keydist; //notion of quality/category of inner parts, functional or sensory consonance?
	//comparison of contribution now to prevailing tonality measure
	//var <>sync; //this frame compared to beat track
	var <>deviation; //from quantised template with groove
	var <>groove; //swung or straight? look over last window?   
	var <>ahead; //laxness, casualness 
	 
	//var <>metre;
	//var <>mphase; //metrical phase 
	var <>treble; 
	var <>bass;
	var <>maxvelocity; //three levels 0-60, 60-100, 100-120 
	
	var <>qprofile; //over frame only
	//var chordcount;
	var <>spread, <>median;
	
	
	var <>action; //points to an action = another RLstate. If deleting, set this to nil!
	//action will persist in the database unless niled. If this state only exists as an action, its action field can be nil whilst something else still points to it. Eventually cast adrift when action set to nil. Can't iterate through an action sequence if links stop at some point. But no problem since just look up closest action in database!
	
	//var <>previous; //logically no need for double linked list
	
	var <>v=0.0; //value of state, trained by reinforcement learning variant, need to update more states at once following Dinerstein and Egbert 2005
	
	var <>framestart; 
	
	//*new {^super.new}
	
	
	//testing
	var <>touched; //List of [time, update, type]
	var <>lasttouch=0.0, <>lastvalue=0.0;
	
	
	//could compare notes using match function
	
	
	
	//could use 2D distance of melody separation, but may have monophonic vs homophonic vs polyphonic materials? 
	
	//revising proximity measure; use a weighted sum, where some factors more important than others 
	//sync to beat
	//degree of correlation of onset times (avoiding num of onset disparity); compare histograms of onset time positions quantised as bins of 1/12 beats? 
	//harmonic degree correlation; difference of histograms relative to key 0
	//register correlation
	//density correlation
	//amplitude correlation
	
	//can shift weights of dimensions as go as calculating each time; all 1.0 for now 
	//all params must be in range 0-1 for fair comparison? 
	//calculate metric
	proximity {|rlstate| 
		var densitydist, veldist, bassdist, trebledist, keydistdist, timingdist; 
		var pcdist, qdist, rdist;
		var calc;
		
		densitydist = ((density - (rlstate.density)).abs.min(10))*0.1;
		
		densitydist=  (0.5*densitydist) + (( ((listOnsets.size)-(rlstate.listOnsets.size)).abs.min(10)*0.1)*0.5);  
		
		//now included directly via pcprofile and qprofile
		//veldist = (maxvelocity - (rlstate.maxvelocity)).abs;
		
		qdist= ((qprofile - (rlstate.qprofile)).abs.sum.min(16.0))*0.0625;
		
		//scaling contentious here
		pcdist= ((pcprofile - (rlstate.pcprofile)).abs.sum.min(40.0))*0.025; 
		//pcdist= ((pcprofile - (rlstate.pcprofile)).sum.min(50.0))*0.02; 
		
		//max dist for piano 108-22 = 86
		//bassdist = ((bass - (rlstate.bass)).abs.min(86))/86.0;
		//trebledist = ((treble - (rlstate.treble)).abs.min(86))/86.0;
		
		keydistdist= ((keydist - (rlstate.keydist)).abs*50).min(1.0);
		timingdist = ((deviation - (rlstate.deviation)).abs).min(1.0);
		
		//rdist1= (0.5*spread) + (0.5*middle);
		
		rdist= 0.5*(((spread- (rlstate.spread)).abs) + ((median- (rlstate.median)).abs)); 
		//max spread of activenotes?
		//mean octave 
		//quantisation score; quantprofile from groove tempmlate; //12 positions across beat
		//chord count= num of nonzero entries in quantprofile? 
		//densityirregularity
		
		//^((densitydist.squared) + (veldist.squared) + (bassdist.squared) + (trebledist.squared) + (keydistdist.squared))*0.2;  
	
		//or use manhattan metric? 
	
		//calc= ((rdist.squared) + (0.1*(densitydist.squared)) + (qdist.squared) + (pcdist.squared) + (0.1*(keydistdist.squared)) + (timingdist.squared))*0.2;  
		
		//calc= ((rdist) + (0.1*(densitydist)) + (qdist) + (pcdist) + (0.1*(keydistdist)) + (timingdist))*0.2;  
		
		calc= ((rdist) + ((densitydist)) + (qdist) + (pcdist) + timingdist + keydistdist)*0.166667;  
		
		//calc= ((rdist) + ((densitydist)) + (qdist) + (pcdist) + (timingdist))*0.2;  
		
		//calc= ((rdist.squared) + (0.1*(densitydist.squared)) + (qdist.squared) + (pcdist.squared) + (0.1*(keydistdist.squared)) + (timingdist.squared))*0.2;  
		
		//calc= ((rdist) + (densitydist) + (qdist) + (pcdist) + (timingdist) + (keydistdist))*0.166666667;  
		
		//[\calc, qprofile, rdist, densitydist, qdist, pcdist, keydistdist, timingdist].postln;
		
		^(calc.sqrt);
		
	}
	
	
	
	//compare to two groove options
	calcTiming {|list, start|
		var quant1, sqdeviations, sqmean, quant2, swdeviations, swmean, sqdevsum, swdevsum; 
		var qtest= [0.0,0.125,0.25,0.375]; 
		
		list= list-start; //absolute start times, not ioi 
		
		quant1= list.round(0.125); //sq is length 0.125 at beat 0.5 sec 
		
		sqdeviations= list-quant1; //
		
		sqmean = mean(sqdeviations);
		sqdevsum = sum(sqdeviations.abs);
		
		//nearest quaver; then look at nearest of 
		quant2= list.collect({|val| var tmp, tmp2, best, index;  
		
		tmp= val.round(0.25); if(tmp>(val+0.000001),{tmp=tmp-0.25});   
		
		//have quaver location below
		
		//0.668*0.25 is swing location
		tmp2=[val-tmp, val-(tmp+(0.167)),val-0.25-tmp].abs;
		
		best=99999.9;
		index=0;
		
		tmp2.do{|val,i| if(val<best,{best=val; index=i}); };
		
		tmp= [tmp, (tmp+(0.167)),tmp+0.25][index]; 
		
		tmp
		});	
		
		swdeviations= list-quant2;
		
		swmean = mean(swdeviations);
		swdevsum = sum(swdeviations.abs);
		
		if(sqdevsum<=(swdevsum+0.0000001), {groove=0; deviation= sqdevsum; ahead= sqmean;},{groove=1; deviation= swdevsum; ahead= swmean;}); 
		
		
		if(groove==1,{qtest= [0.0,0.165,0.25,0.415];});
		
		qprofile= [0,0,0,0]; //[0.25,0.25,0.25,0.25];
		
		listOnsets.do{|val| var onsettime, minsep=9999.9, minindex=0; 
		
		onsettime= val[0]; 
		
		//can be far more efficient; break once at least one further on, but hey...
		qtest.do{|item,i| var sep = (item-onsettime).abs; if(sep<minsep,{minsep=sep; minindex= i;}); }; 
		
		qprofile[minindex] =  qprofile[minindex] + (0.5+(0.5*(val[2]))); //includes component of velocity 
		
		}; 
		
		
		//[\testalgo, quant1, quant2, groove, deviation, ahead].postln;
	}
	
	
	
	
	
	getIndex {
		var tmp;
		
		//tmp = (keytype*(4*5)) + (numonsets*(4)) + (register);
		
		//no longer care about registral separation
		
		//tmp = (keytype*(5)) + numonsets;
		
		tmp=keytype; 
		
		^tmp;
	}
	
	//according to paper should remove closest
	removalscore {|state, time| 
		 var age, score;
		 
		//combination oldest and closest and least valued = highest v
		//how to define old?  if <3 sec, 0, 3-30 sec , else log function with maximum?  
		
		age = time-framestart; 
		
		age= if(age<3.0,{0.0},{1.0-exp(0.01*((age-3.0).neg))});
		
		//equal weights... all factors are 0.0 to 1.0
		score = age + (1.0-(this.proximity(state))) + (1.0-v); 
	
		^score;
	}
	
	//playback any new notes; else select from playing notes; else silence
	//choose next events to schedule based on parameters
	//for now; playback some random subset of events? even, just playback actual note events...
	generate {|keynow=0, transposethen=0|
		var lasttime, tmp, tmp2, events; 
		var transposefactor;
	
		transposefactor= keynow-transposethen; 	
		
		lasttime= 0.0; //framestart; 
		
		if(listOnsets.notEmpty, {
		
		events= listOnsets.collect({|val| 
		
		tmp= (val[0]).max(0.0); //.max(framestart); //any preframe are assumed chord tones and played back together 
		
		tmp2= tmp - lasttime; 
		
		lasttime= tmp; 
		
		[tmp2, val[1]+transposefactor, val[2]];
		});
		
		//[\PLAYBACK, \listOnsets, events].postln;
		
		},{
		
		if(listMIDInotes.notEmpty,{
		
		
		},{
		//else silence
		
		});
		
		});
		
		if(events.notNil,{
		
		{
		
		events.do{|val|
		
			//+12
			val[0].wait;
			Synth(\midisound2,[\freq, (val[1]).midicps, \amp, ((val[2]).squared)*0.3]);
			
			};
		
		}.fork;
		
		});
	
	}
	
}

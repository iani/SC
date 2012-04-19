//SuperCollider is under the GNU GPL; and so is this class.  
//Nick Collins Oct 2007

//streamlined MIDI based player 

//this is online trainer from live MIDI in callbacks; can also simulate offline by feed from MIDI Files? 

//form expectancy field for next beats ready for scheduling
//look at close notes; gaps of around 50msec or less; must look at local trend and overall spread areas
//need actual data on chord spreads in performance: some quick tests with MIDI keyboard! (I know it depends on performance tempo and can be confused with acciacaturas)
	//OK tried out on me; if I'm playing accurately, order under 5 msec. If looser, under 30 msec. Never up to 50 unless particularly bad! so leave as current constants for now; could reduce final 50 msec delay to 30 msec 
	
	

	
	
RLVirtualPlayer {

	//should really train up NN with weights...
	var major= #[2,0,1,-1.5,1.5,1,-0.5,1.75,-1,1,-1.5,1]; //presence of three major chords, penalise b3 and b6
	var minor= #[2,0,1,1.5,-1.5,1,-0.5,1.75,1,-1,-1.5,1]; //presence of minor,minor,major, penalise maj3 and maj6
	var neutral = #[ 1,1,1,1,1,1,1,1,1,1,1,1 ];
	var wholetone1 = #[ 2, -1, 2, -1, 2, -1, 2, -1, 2, -1, 2, -1 ];
	var wholetone2 = #[ -1, 2, -1, 2, -1, 2, -1, 2, -1, 2, -1, 2 ];

	//Array.fill(12,{|i| 0.5})

	var framesize, windowframes, windowsize;
	var managemidinotes; 
	var <>routine; 
	
	var <now, <framestart, <windowstart;
	var newmidion, newmidioff;
	
	var kw0=1.0, kw1=0.5, kw2=0.5; //weights for key profiles, pitch/ velocity/ proportion on
	var learningrate= 0.5; 

	var lastNactive, lastNonsets;
	
	var <rlstates, <prediction, <topK, numK=50; //10; //prediction is one actually used for prediction, topK are top K scoring 
	var topKindex=0, lastTopKindex=0; //gives index in topK of best scoring
	var listOnsets, listOffsets;
	var lastcreated, newcreated;
	var framekey, windowkey;
	//will introduce for beat tracking version; research prototype will impose metronome for now
	//var listframeonsets; //list of last N frames worth of onset lists, for purposes of beat tracking
	
	var <owntime; 
	
	var <>islearning=false, <>isplaying=true, <>isecho=true, <>isextending=true, <>isimproving= true; 
	
	var <protracted=false, <erroraccumulation=0.0, <confidence=0.0, <activeplaystate; 
	var <numstages, <maxstages, <actedonprediction=true; 
	
	var <>errorsum=0.0, <>directerror=0.0, <directprediction, <>dumberror=0.0;
	var <>errorN=1, <>errorNarray; 
	var <>epsilon= 0.1; 
	var <>eligibilities, <ememory=10, <>lambda=0.9; 
	
	var <>recorderrors; 
	
	*new{|fsize=0.5, wframes=8|
	
	^super.new.initRLVirtualPlayer(fsize, wframes);
	}
	
	initRLVirtualPlayer {|fsize=0.5, wframes=4|
			
		errorNarray= List.fill(errorN,{[nil,nil,nil]});
		eligibilities= List.fill(ememory,{nil}); //holds last five updates			
		recorderrors= List[]; 
			
		framesize= fsize;
		windowframes= wframes; //number of frames per window 
		windowsize= fsize* windowframes;
	
		//3 key types, 4 register types (too absolute?), 5 onset types 
		//rlstates= Array.fill(3*4*5,{List[]});  //60 search spaces max of 1000 items in each 
		
		//rlstates= Array.fill(3*5,{List[]});  //60 search spaces max of 1000 items in each 
		
		rlstates= Array.fill(3,{List[]});  //60 search spaces max of 1000 items in each 
		
		
		// 2000*36 = 72000 floats per search space
		//72000*60 is about 4 MB of data
		//
		
		//search time is that for iterating over 1000 states 
		//addition may occasionally also require the same search length
		
		owntime=0.0; //updated by both MIDI file analysis and by live playing, unique to this agent, used for age
		islearning= true; 
		
	}
	
	
	
	//running via MIDIFileAnalyse
	trainFromMIDIFile {|path|
	
		// read entire file
		var midifile, length;
		var analyser, data;
		var framestodo, framenow; 
		var index, pos, nextevent;
		var num, vel, dur, starttime; 
		var upto;
		var activeoffset, offsetlist, offsetcheck; 	
		var note; 
					
		isplaying= false;	
		errorNarray= List.fill(errorN,{[nil,nil,nil]});
		eligibilities= List.fill(ememory,{nil}); //holds last five updates				
		midifile = MIDIFile.new;
		midifile.read(path);
		midifile.format.postln;
		
		if(midifile.format==1,{
		
		midifile.ntrks.postln;
		midifile.division.postln;
		midifile.scores.at(1).postln; // midifile.scores.at(0).postln; // for format 0 
		
		//gives [ioi (seconds), channel, pitch, velocity, absTime (sec), dur (sec)]
		analyser= MIDIFileAnalyse(midifile,2); //assumes MIDIFile type 1, tempo 2 bps
		
		//playback (expressive timing via tempo curve was discarded so this is metronomic playback)
		
		data= analyser.normalform;
		
		length=data[data.size-1][4];
		
		framestodo= ((length+2)/framesize).roundUp; 

		[\length, length,\frametodo, framestodo].postln;

		//simulate setting of MIDI playback

		managemidinotes= Array.fill(128,{nil});
	
		//storing new notes in a time sorted list; older (smaller) times further back in the past
		//the custom sorting function here looks only at the first entry of each note's data, the start time  
		//data= SortedList(8, {|a,b| (a[0]) < (b[0])});

		newmidion = List[]; //SortedList(8, {|a,b| (a[0]) < (b[0])});
		newmidioff = List[]; //SortedList(8, {|a,b| (a[0]) < (b[0])});

		lastNactive = List.fill(windowframes, {0.01.dup(12)}); //not all zeroes to allow normalizeSum later		
		lastNonsets = List.fill(windowframes, {[]}); 			
		if (routine.notNil, {routine.stop;});
		
		lastcreated= nil; //avoid overlap effects
		
		//could possibly amortise in some way through a delay of a frame
		
		offsetcheck= {
		
				//"offsetcheck".postln;
		
				//check for offsets first
				offsetlist=SortedList.new(8,{|a,b| a[0]<b[0]});
				activeoffset.do{|val,i| if(val.notNil,{if((val)<pos,{offsetlist.add([val,i]); activeoffset[i]=nil});  }); };
				//offsetlist already now sorted
				
				offsetlist.do{|val|
				
					num= val[1];
					
					note= managemidinotes[num]; // = [pos,vel/127.0];
			 				
					starttime= note[0];
				
					vel=note[1];
					
					dur= val[0]-starttime;
					
					newmidioff.add([val[0],starttime,dur,num,vel]);
					
					managemidinotes[num]=nil;
					
				};
		};
		
		routine= {
				
				index=0; pos=0.0;
				
				activeoffset= Array.fill(128,nil);
				
			framestodo.do {|i|
					
				now = i*(framesize);
				upto= now+framesize;
				
				
				if(i%50==0,{
				[i, framestodo, \pos, pos, \upto, upto].postln;
				});
				
				//ASSUMES MIDI FILE STARTS WITH A BEAT, NO STRANGE UPBEAT 
				
				//set up activenotes
				while({
				(index<(data.size)) && (pos<upto)},{
				
				//data.size.postln;
				//data[index].postln;
				nextevent= data[index];
				
				pos= nextevent[4]; //startPos
				
				//add onset  
				if(pos<upto,{
				
				offsetcheck.value;
			
				num= nextevent[2]; 
				vel=nextevent[3];
				dur=nextevent[5];
			
				//add directly to offsets if finish during segment				
				//can happen due to MIDI file misalignment
			 	if(activeoffset[num].notNil,{
			 	
			 	//["odd!",pos, activeoffset[num]].postln;
			 		
					note= managemidinotes[num]; // = [pos,vel/127.0];
					starttime= note[0];
					newmidioff.add([pos-0.001,starttime,pos-0.001-starttime,num,note[1]]);
					managemidinotes[num]=nil;
			 	
			 	}); 
			 	
				newmidion.add([pos,num,vel/127.0]);
				
				//offset time recorded
				managemidinotes[num] = [pos,vel/127.0];
			 
			 	activeoffset[num]= pos+dur;
			 	
				index=index+1;
				
				});
				
				}); 
				
				
				//check for offsets
				offsetcheck.value;
				
				//Post << newmidion << nl;
				
				//"off".postln;
				
				//Post << newmidioff << nl;
				
		
				this.frameanalyse;
		
				
				//no need for wait, but minimal put in for safety!
				0.01.wait; //safety while debugging //0.01.wait;
				//framesize.wait;
				
				
			};	
		}.fork;	
	
		},{"Wrong MIDIFile format= type 0 was loaded, need type 1"});
			
	}
	
	
	
	trainLive {
		
		isplaying= true;
		errorNarray= List.fill(errorN,{[nil,nil,nil]});
		eligibilities= List.fill(ememory,{nil}); //holds last five updates		
			//use to maintain state showing which MIDI notes are currently active
		managemidinotes= Array.fill(128,{nil});
	
		//storing new notes in a time sorted list; older (smaller) times further back in the past
		//the custom sorting function here looks only at the first entry of each note's data, the start time  
		//data= SortedList(8, {|a,b| (a[0]) < (b[0])});

		newmidion = List[]; //SortedList(8, {|a,b| (a[0]) < (b[0])});
		newmidioff = List[]; //SortedList(8, {|a,b| (a[0]) < (b[0])});	
		lastcreated= nil; //avoid overlap effects with last session
			
		MIDIIn.noteOn = { arg src, chan, num, vel;  
			
			if (managemidinotes[num].notNil,{"ERROR: on before off!".postln;});
			
			//["newnote!",Main.elapsedTime-lasttime].postln;
			//lasttime= Main.elapsedTime;
			
			managemidinotes[num] = [Main.elapsedTime,vel/127.0];
			
			//could make RLNote object at this point, will act as reference to enable update of its duration later on? 
			newmidion.add([Main.elapsedTime,num,vel/127.0]);
			
			if(isecho,{
			Synth(\midisound,[\freq, num.midicps, \amp, ((vel/127.0).squared)*0.3]);
			});
		};
		
		MIDIIn.noteOff = { arg src, chan, num, vel;  
			var duration, starttime;
			 
			if (managemidinotes[num].isNil,{"ERROR: off before on!".postln;},{
			
			starttime= managemidinotes[num][0];
			
			vel=managemidinotes[num][1];
			
			duration= (Main.elapsedTime - starttime);
			
			//[starttime, duration, num, vel/127.0].postln;
			
			//velocity converted to a linear 0.0 to 1.0 
			//data.add([starttime, duration, num, vel]); 
			
			newmidioff.add([Main.elapsedTime,starttime, duration,num,vel]);
			
			managemidinotes[num]=nil;
			
			});
			
		};
	
		
					
		lastNactive = List.fill(windowframes, {0.01.dup(12)}); //not all zeroes to allow normalizeSum later		
		lastNonsets = List.fill(windowframes, {[]});
			
		if (routine.notNil, {routine.stop;});
		
		//could possibly amortise in some way through a delay of a frame
		
		routine= {
			
			inf.do {
					
				Synth(\midiclick,[\freq,1, \amp,0.5]);
			
				now = Main.elapsedTime;
			
				this.frameanalyse;
					
				framesize.wait;
			};	
		}.fork;	
	
	}
	
	
	
	
	
	
	
	//500 msec frames
	frameanalyse {
		var index, time, tmp, tmp2, activemidi, proportion;
		var activenotes; //weights of appearing notes
		var maxvel,vel, maxnote, minnote, numplaying, keydist; 
		
		framestart = now- framesize;
		windowstart = now- windowsize;
		
		listOnsets= List[];
				
		//examine onsets collected in last frame, take any apart from in LAST 0.05 seconds, which will be left in the list and be attached to next frame 
		if (newmidion.notEmpty,{
			
			index= newmidion.detectIndex({|a| (a[0])>(now-0.03)}); //30 msec
			
			//if index not nil, have some you need to preserve
			if(index==nil, {
				
				listOnsets= newmidion;
				
				newmidion= List[];		
				
			},{
			
				//"newnotes".postln;
			
				time= newmidion[index][0];
			
				//may need to look for notes close to this
			
				if((index>0) && ((now-time)>0.005),{	//0.025
				
				tmp= newmidion.detectIndex({|a| (a[0])>(time-0.03)}); //0.025 //test for close chord tones
				
				index=tmp;
				
				});
						
				if(index>0,{
				listOnsets= newmidion.copyRange(0,index-1);
				
				newmidion= newmidion.copyRange(index,newmidion.size-1);
				
				}
				//,{
				//newmidion stays as it is
				//}
				);
				
			});
			
		});

		//examine offsets collected in last frame, take any apart from in FIRST 0.05 seconds
		//list of active notes in this frame = offsets list + currently active in managemidinotes
		
		listOffsets= List[];
		
		if (newmidioff.notEmpty,{
			
			//"newoffs".postln;
			
			index= newmidioff.detectIndex({|a| (a[0])>(framestart+0.05)});
			
			//could search for nearby ending chord notes but less critical here
			
			//if index nil, only midioffs 'belonging' to previous frame, dump them
			if(index.notNil, 
			
			//==nil
			//, {
			//	listOffsets= List[];
			//},
			{
				listOffsets= newmidioff.copyRange(index,newmidioff.size-1);
			});
			
			newmidioff= List[]; //none ever survive		
		});

	
		//THE FOLLOWING CALCULATIONS SHOULD BE IN RLVirtualPlayer and RLEnsemble classes
	
		//"makeactive".postln;

		activenotes= 0.01.dup(12);			

//[Main.elapsedTime,starttime, duration,num,vel]
		
		//could add emphasis by velocity, but no implementation for now? Nah, let's do it! plus proportion by duration?
		//go through newmidioff list adding to active notes
		
			
		maxvel=0; 
		maxnote=0;
		minnote=127;
		numplaying=listOffsets.size;
		
		activemidi= List[];
		
		listOffsets.do {|val| 
			proportion = (now - val[0])/framesize;
			
			tmp= (val[3]);
			activemidi.add(val.copyRange(2,4));
			
			//could test not too long since note started, assuming decay of piano in about 2 seconds or so? 
			if(tmp>maxnote, {maxnote=tmp;});
			if(tmp<minnote, {minnote=tmp;});
			
			tmp=tmp%12;
			
			vel= val[4];
			
			if(vel>maxvel,{maxvel=vel});
			
			activenotes[tmp] = activenotes[tmp] + (kw0 + (kw1*(vel))+ (kw2*(proportion)));
			
		};
		
		//go through managemidinotes; some could correspond to new ones during bar? 
		managemidinotes.do{|val,i| if(val.notNil,{
			
			proportion = (now - val[0]).min(framesize)/framesize;
			tmp= i%12; 
			vel= val[1];
			
			if(vel>maxvel,{maxvel=vel}); //should really depend how long a note has been playing! 
			
			if(i>maxnote, {maxnote=i;});
			if(i<minnote, {minnote=i;});
			
			numplaying= numplaying+1; 
			
			activemidi.add([now-val[0], i, vel]);
			
			activenotes[tmp] = activenotes[tmp] + (kw0 + (kw1*(vel))+ (kw2*(proportion)));
			
		});
		
		}; 

		
		//update key over this frame
		//instantaneous key
		framekey= this.findKey(activenotes);

		//[\keynow,framekey].postln;

		lastNactive.pop;
		lastNactive.addFirst(activenotes);
		
		lastNonsets.pop;
		lastNonsets.addFirst(listOnsets.collect{|val| val[0]};); //just keeping time
		
		tmp=activenotes;
		
		//safe operation even at first because sums all there
		activenotes= lastNactive.sum;
		
		//sum up profiles from last N frames (or also including other players!)
		windowkey= this.findKey(activenotes);
		
		//[\key,windowkey].postln;
		
		
		//the note C versus C major scale will score badly here; what are you aiming for exactly? these frames will include not so many notes...
		keydist= (((tmp.normalizeSum)-(activenotes.normalizeSum)).squared.sum)/12.0;
		
		//update beat over last 2 seconds
		//LATER! 
		//measure of syncopation, lock-on and opposition
		
		
		//like concatenative synthesis, just higher level features! 
		newcreated= RLState2.new; //create, pass all data in
		
		//just from onsets over last window; must time reverse first
		//-0.5 to cope with negative start positions easily? 
		//[\checktiming, lastNonsets, lastNonsets.reverse.flatten, windowstart-0.5].postln;
		
		//whether to normalise; could do abs difference, with max on overall difference level?
		newcreated.pcprofile= tmp.rotate(windowkey[0]);
		
		//need an ic profile too really
		
		//also contour profiles? 
		
		
		//features
		tmp= listOnsets.size;
	
		//4 allowed possibilities here; but doesn't differentiate with respect to chords? 
		//have added options of >4, >2, >0 and 0
		newcreated.numonsets= if(tmp>6,4,{if(tmp>4,3,{if(tmp>2,2,{if(tmp>0,1,0); }); }); });
		
		
		//[\onsetstest, newcreated.numonsets, listOnsets.size, listOnsets].postln;
		
		//no point categorising for now, since used in distance metric; velocity is already 0.0-1.0
		newcreated.maxvelocity= maxvel; //if(maxvel>99,{2},{if(maxvel>60,1,0)}); 
		
		//tmp= minnote.max(24).min(107);
		//tmp2= ((maxnote.max(24).min(107))-tmp);
		//newcreated.bassoctave= (tmp-24).div(12);
		//newcreated.spread= tmp2.div(12); 
		
		//0= normal, 1= full, 2= bass, 3= treble
		
		newcreated.register = if((minnote>28) && (maxnote<90), 0, 1); //normal or full
		
		if(maxnote<55,{newcreated.register = 2}); //bass
		if(minnote>=84,{newcreated.register = 3}); //treble
		
		
		//context; absolute range
		newcreated.bass= minnote;
		newcreated.treble=maxnote;
		
		newcreated.keydist= keydist;
		
		
		//newcreated.key = (lastNkey[0]*2)+(lastNkey[1]); //alternating major and minor keys 
		newcreated.keytype= windowkey[1];
		newcreated.transpose= windowkey[0];
		
		
		//alter these by transposition if necessary? depends if will use melodic similarity algorithm for proximity, or only storing until playback
		
		newcreated.listOnsets= listOnsets.collect{|val| val[0]= val[0]-framestart; val}; //iois to each note from bar start= val[0]- newcreated.framestart
		newcreated.listMIDInotes = activemidi; 
		
		newcreated.density= numplaying;
		
		//may have to account for different creation dates in the future! use Date class etc
		//use 
		newcreated.framestart= framestart; 
		
		newcreated.calcTiming(lastNonsets.reverse.flatten, windowstart-framesize);
		
		newcreated.spread= (maxnote-minnote)/(88.0);
		
		//should really be median! 
		//(minnote+ ((maxnote-minnote)*0.5) -22)/88.0;
		if(listOnsets.notEmpty,{
		newcreated.median= ((newcreated.listOnsets.collect({|val| val[1]}).median) -22)/88.0;  
		},{
		newcreated.median=0.4318181818; //(60-22)/88
		});
		
		///TESTING!
		newcreated.touched=List[]; //for testing purposes of learning algorithm
		newcreated.lasttouch= owntime;
		
		
		//potentially could get all of above from signal processing; onset detector, key tracker, register of pitch tracker etc
		
		//add state, learning, next round of predictions 
		this.updatestates;
		
		
		//own internal clock to have equivalence of MIDI File and live input
		owntime= owntime+ framesize;
		
		if(((2*owntime+0.001).mod(10.0).asInteger)==0, {
		
		recorderrors.add([errorsum, directerror, dumberror]); 
		
		});
							
	}
	
	
	
	updatestates {
		var tmp, tmp2;
		var lastTopK; 
		var playstate; 
		var quality; 
		var nothingflag; 
		
		lastTopK= topK; 
		lastTopKindex = topKindex;
		
		
		nothingflag= if(lastcreated.notNil, {(lastcreated.density>0) && (newcreated.density>0)},false);
		
		//TESTING
		//if((prediction.notNil) && (lastcreated.notNil) && nothingflag,{
//		errorsum= errorsum + (newcreated.proximity(prediction.action)); 
//		directerror= directerror + (newcreated.proximity(directprediction.action)); 
//		dumberror= dumberror + (newcreated.proximity(lastcreated)); 
//		});
		
		errorNarray.do({|val| 
		
		if(val[0].notNil,{val[0]= val[0].action; 
		
		if(val[0].notNil,{
			errorsum= errorsum + (newcreated.proximity(val[0])); 
		}); 
		
		}); 
		
		if(val[1].notNil,{val[1]= val[1].action; 
		
		if(val[1].notNil,{
			directerror= directerror + (newcreated.proximity(val[1])); 
		}); 
		
		}); 
		
		if(val[2].notNil,{
		
			dumberror= dumberror + (newcreated.proximity(val[2])); 
		
		}); 
		
		
		}); 
		
		
		
		//search database for best fit to current state
		tmp2= newcreated.getIndex;
		
		//prediction= this.findnextaction(newcreated, tmp2);
		
		//returns winner and topK now contains up to the best K candidates for RL update
		# prediction, quality= this.findtopK(newcreated, tmp2);
		
		//if(not(prediction===directprediction),{[prediction, directprediction].postln;});
		
		///N step test
		errorNarray.pop;
		errorNarray.addFirst([prediction, directprediction, newcreated]);
		
			
		if(islearning,{
			
			if(isimproving,{
			
			if((lastTopK.notNil) && (prediction.notNil),{
			this.compareprediction(newcreated, lastcreated, prediction, lastTopK); //also pass status flag for whether acted on prediction or not
			});
			
			});
			
			
			if(isextending,{
			
			//set pointer to that which follows
			if(lastcreated.notNil,{
			
			//if not both zero 
			tmp = false; 
			if(nothingflag,{tmp=true;});
			
			if(tmp,{
			lastcreated.action= newcreated; 
			//lastcreated.v = 1.0- (newcreated.proximity(lastcreated)); //if close together start off high value?  
				//must store RLStates in a big array for recall of memory; rote learning, but with twists...
			this.addrlstate(lastcreated, lastcreated.getIndex);
			//returns index
			});
		
			
			});
			
			});
		
		});

		lastcreated = newcreated; 
		
	
		//even if fake predictions, keep doing this? 
		//update error
		if(protracted,{
			
			if(activeplaystate.action.isNil,{protracted=false;},{
				
				erroraccumulation = erroraccumulation+ (activeplaystate.proximity(newcreated)); //if staying close, stays longer
				numstages= numstages+1;
				
				
				//[\checkplay, numstages, maxstages, confidence, erroraccumulation].postln;
				
				if((erroraccumulation>confidence) || (numstages>=maxstages),{
				
					protracted= false;
				}, {
				
					activeplaystate = activeplaystate.action; //follow sequence 
					//could also look for k nearest neighbours and jump at some point! 
					
				});
				
			});
			
		}); 
		
		
		//shouldn't really ever happen that next is dead! But safety first... 
		if(prediction.notNil, {if((prediction.action.notNil) && (nothingflag), {
		
		//var <protracted=false, <erroraccumulation=0.0, <confidence=0.0; 
	//activeplaystate, numstages, maxstages; 
		
		//if playing 
		
		//if confidence high then can start playing new 
		
		actedonprediction= if(not(protracted),{
		//use prediction
		
		activeplaystate= prediction; 
		
		//assess confidence relative to others to choose number of steps to play back
		//combination of proximity to newcreated and to second best match
		
		//quality is [excess, proximity]
		confidence = ((2*(quality[0])).max(1.0)) + ((1.0-quality[1])*0.5); 
		maxstages = 1; //rrand(1,10); //no measure of phrase length here for now, could assess later! 
		numstages=0;
		protracted=true; 
		erroraccumulation=0.0;
		
		//[\newplay, numstages, confidence].postln;
		
		true;
		},
		false //next step
		);
		
		
		if(isplaying,{
		
		//prediction.action.generate(newcreated.transpose, prediction.transpose);
		activeplaystate.action.generate(newcreated.transpose, activeplaystate.transpose);
		});
		
		}, {
		
		
		if(protracted && isplaying,{
		
		//prediction.action.generate(newcreated.transpose, prediction.transpose);
		activeplaystate.action.generate(newcreated.transpose, activeplaystate.transpose);
		});
		
		}); 
		
		},{
		
		if(protracted && isplaying,{
		
		//prediction.action.generate(newcreated.transpose, prediction.transpose);
		activeplaystate.action.generate(newcreated.transpose, activeplaystate.transpose);
		});
		
		}); 	
		
		
	
	
	
	}
	
	
	
	
	
	addrlstate {|newcreated, index|
		var tmp, tmp2, list; 
		var victim, score;
		 
		list= rlstates[index];
		
		tmp2= list.size;
		
		if(tmp2<1000,{
		list.add(newcreated);
		},{
			
			"REPLACE!".postln;
			
		victim=0; 
		score=0.0;
		
		list.do {|val, i|
		
			tmp = val.removalscore(newcreated, now);
			
			if(tmp>score,{tmp=score; victim= i; });
		
		};
		
		list[victim].action= nil; //for garbage collection, avoiding long chains of preserved states
		
		list[victim] = newcreated; //now replace
		
		
		});
		
	}
	
	
	//matching based on proximity to get topK and then on value
	//should really store topK matches
	findnextaction {|state, index|
		var list, score, mindist, winning;
		
		mindist= 999999.0;
	
		list= rlstates[index];
		
		//winning= list[0];
		
		list.do {|val| 
		
		//shouldn't ever be nil since only added once clear? 
		if(val.action.notNil,{
		score= val.proximity(state);
		
		if(score<mindist,{winning= val;});
		});
		
		};
		
		^if(winning.notNil, {winning.action},nil);
		//return prediction of next state
	}
	
	
	
	
	
	//epsilon greedy- occasionally choose random action
	
	//matching based on proximity to get topK and then on value
	//should really store topK matches
	
	//depending on number there already
	findtopK {|state, index|
		var list, score, winning;
		var rankings, bestvalue=(-9999.9), bestvalue2=9999.9; 
		var workingK,secondbest=0.0; 
		
		list= rlstates[index];
		
		workingK= numK; 
		
		//for smaller search spaces don't seek out so many, because might be too disparate? 
		//if(list.size<100, {workingK= (list.size.div(10)).min(numK).max(1)}); 
			
		rankings= SortedList(workingK,{|a,b| (a[0])<(b[0])});
		workingK.do{rankings.add([9999999.9, nil])};
	
		
		//winning= list[0];
		
		list.do {|val| 
		
		//shouldn't ever be nil since only added once clear? 
		if(val.action.notNil,{
		score= val.proximity(state);
		
		//search rankings for insertion place
		
		rankings.add([score, val]); 
		rankings.pop;
		
		//if(score<mindist,{winning= val;});
		
		});
		
		};
		
		//choose one with lowest proximity or with highest value as official answer? 
		winning = rankings[0];
		
		//becomes List of any high scoring
		topK = List[]; 
		
		//winning.postln;
		//rankings.postln;
		
		
		//should really check proximity for any discoveries... else updating items far from the exemplar
		
		directprediction=nil; //for fairness; else it sticks on last one! 
		
		rankings.do{|val,j| if(val[1].notNil,{
		
		  //add [proximity, state] because useful to have already calculated later!  
		  topK.add(val);
		
		  //HACK!!!!!!!!!!
		  //if(isimproving,{
		  
		  if(((val[1]).v)>bestvalue,{secondbest= bestvalue; winning=val; bestvalue= val[1].v;  topKindex= j;}); 
		//},{
		
		  if((val[0])<bestvalue2,{directprediction=val[1]; bestvalue2= val[0];});
		//});
		  		  
		   }); };
		
		
		//[winning, rankings, bestvalue, directprediction, bestvalue2].postln;
		
		//must remove winner or at least note winning index
		
		
		^[if((winning[1]).notNil, {winning[1]},nil),[bestvalue-secondbest, winning[0]]];
		//return prediction of next state
	}
	
	
	
	//compare actual outcome  = new state to the prediction (actually, to topK predictions)
	//reinforcement learning; update value based on this
	//same situation could have many possible actions; but exactly same situation hardly ever turns up? 
	//if it does, just maintain two of same possibility! One will have greater value!
	compareprediction {|statenow, statethen, bestmatch, list|
		var score1, score2, score3; 
		//var adash; //for sarsa
		var updatestate; 
		var valuesum; 
		var gamma=0.5; //1.0; //0.5; 
		var lrmod;
		var test; 
		
		//adash= bestmatch.action; //assumes never nil; should be fine! 
		score3= bestmatch.v; 
		
		list.do {|candidate, j| 
		
		score1= candidate[0];
		updatestate = candidate[1];
		valuesum = updatestate.v;
		
		score2= updatestate.action.proximity(statenow);
		
		//add .squared? already squared distance from proximity measure! 
		//lrmod= learningrate* ((((1.0- score1).max(0.5))-0.5)*2); //if further away from 0.5, 0.0 learning, hard limit
		
		lrmod= learningrate; //* ((((1.0- (score1.squared)).max(0.5))-0.5)*2); //if further away from 0.5, 0.0 learning, hard limit
		
		if(actedonprediction==false,{"error!".postln;});
		
		//full sarsa update for the best performing, otherwise gamma=0
		if((lastTopKindex==j) && (actedonprediction),{
			
			//recursive step
			valuesum = valuesum + ((lrmod*gamma)*score3);
		
		}); 

		//other nonlinear reward functions? 
		valuesum = valuesum + (lrmod *( ((1.0- score2)) - (updatestate.v))); 
		
		//valuesum = valuesum + (lrmod *( ((score2.neg)) - (updatestate.v))); 
		
		
		//valuesum = valuesum + (lrmod *( ((1.0- score1)*(1.0- score2)) - (updatestate.v))); 
		
		//proximity is value from 0.0 to 1.0
		
		//[\updates, valuesum, lrmod, 1.0-score2, score3, (lrmod*gamma)*score3].postln; 
		
		
		
		
		//TESTING CODE
		
		updatestate.touched.add([owntime- (updatestate.lasttouch), valuesum- (updatestate.lastvalue), if((lastTopKindex==j) && (actedonprediction),1,0), valuesum, owntime]); 
		updatestate.lasttouch= owntime;
		updatestate.lastvalue= valuesum;
		
		////////eligibilities trace bonuses! 
		if((lastTopKindex==j) && (actedonprediction),{
		
		test= lrmod*((gamma*score3) + (1.0-score2) - (updatestate.v));
		
		//update eligibilities
		
		eligibilities.do{arg val,i; var lvl, case; if (val.notNil, {
		
		lvl=val[0]; 
		case= val[1];
		
		lvl= lvl*(gamma*lambda);
		
		case.v = (case.v) + (lrmod*test*lvl);
		
		eligibilities[i]=[lvl,case];
		
		}); };
		
		
		
		eligibilities.pop; 
		
		//check not already member
		test=[false,0]; 
		
		eligibilities.do{|val,i| if(val.notNil,{if(val[1]===updatestate, {test=[true,i];});});  }; 
		
		if(test[0],{
		eligibilities[test[1]]= nil; //replacing trace
		});
		
		eligibilities.addFirst([1,updatestate]);
			
		
		
		});
		
		
		//interpolation from current value to new (proximity, ie prediction) error
		updatestate.v= valuesum; // (candidate.v) + (learningrate*(score-(candidate.v))); 
		
		
		
		
		
		
		};
	
	}
	
	
	 
	findKey {arg pchistogram;
		var tmp, best;
		var type, transpose;
		//check for neutrality; if stddev low
		var score;
		
		tmp = pchistogram.mean; //assuming this isn't zero, all entries are positive so should be fine
		
		//stddev would be in units not so comparable between instances; so just like at max deviation as proportion
		best=0.0;
		
		pchistogram.do{|val| var tmp2; tmp2= (val-tmp).abs; tmp2= (tmp2/tmp);  if(tmp2>best, {best=tmp2;}); };
		
		//[\neutraltest, pchistogram, tmp, best].postln;
		
		
		//proportional point is 0.5; very rare! usually weightings on different notes outweigh any sense of evenness
		if(best<0.5, {type=2; transpose=0;}, {
		//highest score wins!
		tmp=0;
		best=(1000.neg);
		
		24.do{arg i;
		var results;
		
		results= if(i.even,{major},{minor});
		
		results= results.rotate(i.div(2));
		
		score = (pchistogram * results).sum; 
		
		if(score>best,{best=score; tmp=i;});
		
		};
		
		transpose= tmp.div(2); 
		type= if(tmp.even,0,1);
		
		});
		
		//if can beat best, add new
		
		//wholetone tests
		score = (pchistogram * wholetone1).sum;
		 
		if(score>best,{best=score; type=2; transpose=0;});
		
		score = (pchistogram * wholetone2).sum;
		 
		if(score>best,{best=score; type=2; transpose=1;});
		
		score = (pchistogram * neutral).sum;
		 
		if(score>best,{best=score; type=2; transpose=0;});
			
		
	//^[\key, (["C","Db","D","Eb","E","F","F#","G","Ab","A","Bb","B"].at(tmp.div(2)))+(if(tmp.odd,"major","minor")),\keyarray,(tmp.div(2)+(if(tmp.odd,{major},{minor})))%12  ];
	
	^[transpose, type]; 
	}
	
	
	*initClass {
	
		StartUp.add({
		
		SynthDef(\midisound, {arg freq=440, amp=0.1; 
		
		Out.ar(0,SinOsc.ar(freq, 0, amp)*Line.kr(1,0,0.2,doneAction:2).dup(2));
		}).writeDefFile;
		
		
		SynthDef(\midisound2, {arg freq=440, amp=0.1; 
		
		Out.ar(0,LPF.ar(LFSaw.ar(freq, 0, amp),2000)*Line.kr(1,0,0.2,doneAction:2).dup(2));
		}).writeDefFile;
		
		
		SynthDef(\midiclick, {arg freq=1, amp=0.75; 
		
		Out.ar(0,Decay.ar(Impulse.ar(freq, 0, amp),0.01)*Line.kr(1,0,0.1,doneAction:2).dup(2));
		}).writeDefFile;
		
		});
	
	}
	
	
	
	
}
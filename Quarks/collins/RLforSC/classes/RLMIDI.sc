//SuperCollider is under the GNU GPL; and so is this class.  
//Nick Collins Oct 2007

//minimal class for parsing input MIDI data 
//connected to RLBeatTrack and RLKeyTrack et al
//RLStateActionSpace
//RLState

//this is online trainer from live MIDI in callbacks; can also simulate offline by feed from MIDI Files? 

//form expectancy field for next beats ready for scheduling
//look at close notes; gaps of around 50msec or less; must look at local trend and overall spread areas
	
RLMIDI {

	var framesize, windowframes, windowsize;
	var managemidinotes; 
	var <>routine; 
	
	var now, framestart, windowstart;
	var newmidion, newmidioff;
	
	var kw0=1.0, kw1=0.5, kw2=0.5; //weights for key profiles, pitch/ velocity/ proportion on
	var learningrate= 0.5; 
	
	//should be classvar really! 
	var major= #[2,0,1,-1.5,1.5,1,-0.5,1.75,-1,1,-1.5,1]; //presence of three major chords, penalise b3 and b6
	var minor= #[2,0,1,1.5,-1.5,1,-0.5,1.75,1,-1,-1.5,1]; //presence of minor,minor,major, penalise maj3 and maj6
	
	var lastNactive;
	
	var <rlstates, <prediction, <topK, numK=10; //prediction is one actually used for prediction, topK are top K scoring 
	
	//will introduce for beat tracking version; research prototype will impose metronome for now
	//var listframeonsets; //list of last N frames worth of onset lists, for purposes of beat tracking
	
	*new{
	
	^super.new.initRLMIDI();
	}
	
	initRLMIDI {
		//var lasttime= Main.elapsedTime; 
		
		//status = true; 
		
		//4 note on num  24 keys (really want 25 so include straight key 0.5!12) 
		rlstates= Array.fill(24*4*28,{List[]});  //2688 search spaces max of 100 items in each 
		
		//data estimation max of 200 states (including linked actions) * (12 + (3*4) +(3*4)) 12 straight items + 2 lists assuming average of 4 new notes and 4 active at each frame
		//200*36 = 7200 floats per search space
		//7200*2688 = 19353600 is about 73 MB of data, reasonable I guess!
		//lots of these may be variants of zero- ie rest now or rest as action. But many could involve lots of action...
		
		//search time is that for iterating over 100 states 
		//addition may occasionally also require the same search length
		 
		
		//use to maintain state showing which MIDI notes are currently active
		managemidinotes= Array.fill(128,{nil});
	
		//storing new notes in a time sorted list; older (smaller) times further back in the past
		//the custom sorting function here looks only at the first entry of each note's data, the start time  
		//data= SortedList(8, {|a,b| (a[0]) < (b[0])});

		newmidion = List[]; //SortedList(8, {|a,b| (a[0]) < (b[0])});
		newmidioff = List[]; //SortedList(8, {|a,b| (a[0]) < (b[0])});	
			
		MIDIIn.noteOn = { arg src, chan, num, vel;  
			
			if (managemidinotes[num].notNil,{"ERROR: on before off!".postln;});
			
			//["newnote!",Main.elapsedTime-lasttime].postln;
			//lasttime= Main.elapsedTime;
			
			managemidinotes[num] = [Main.elapsedTime,vel/127.0];
			
			//could make RLNote object at this point, will act as reference to enable update of its duration later on? 
			newmidion.add([Main.elapsedTime,num,vel/127.0]);
			
			Synth(\midisound,[\freq, num.midicps, \amp, ((vel/127.0).squared)*0.3]);
			
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
		
	}
	
	//need actual data on chord spreads in performance: some quick tests with MIDI keyboard! (I know it depends on performance tempo and can be confused with acciacaturas)
	//OK tried out on me; if I'm playing accurately, order under 5 msec. If looser, under 30 msec. Never up to 50 unless particularly bad! so leave as current constants for now; could reduce final 50 msec delay to 30 msec 
	
	//250 msec frames
	frameanalyse {|fsize=0.25, wframes=8|
		var index, time, tmp, tmp2, activemidi, proportion, keynow, lastNkey;
		var listOnsets, listOffsets;
		var activenotes= 0.01.dup(12); //weights of appearing notes
		var lastcreated, newcreated;
		var maxvel,vel, maxnote, minnote, numplaying, keydist; 
				
				
				
		framesize= fsize;
		windowframes= wframes; //number of frames per window 
		windowsize= fsize* windowframes;
		
		lastNactive = List.fill(windowframes, {0.01.dup(12)}); //not all zeroes to allow normalizeSum later			
		if (routine.notNil, {routine.stop;});
		
		//could possibly amortise in some way through a delay of a frame
		
		routine= {
			
			inf.do {
				
				
				Synth(\midiclick,[\freq,1, \amp,0.2]);
			
				
				now = Main.elapsedTime;
				framestart = now- framesize;
				windowstart = now- windowsize;
				
				
				listOnsets= List[];
						
				//examine onsets collected in last frame, take any apart from in LAST 0.05 seconds, which will be left in the list and be attached to next frame 
				if (newmidion.notEmpty,{
					
					index= newmidion.detectIndex({|a| (a[0])>(now-0.05)}); 
					
					//if index not nil, have some you need to preserve
					if(index==nil, {
						
						listOnsets= newmidion;
						
						newmidion= List[];		
						
					},{
					
						//"newnotes".postln;
					
						time= newmidion[index][0];
					
						//may need to look for notes close to this
					
						if((index>0) && ((now-time)>0.025),{
						
						tmp= newmidion.detectIndex({|a| (a[0])>(time-0.025)}); //test for close chord tones
						
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
					
					if(vel>maxvel,{maxvel=vel}); 
					
					if(i>maxnote, {maxnote=i;});
					if(i<minnote, {minnote=i;});
					
					numplaying= numplaying+1; 
					
					activemidi.add([now-val[0], i, vel]);
					
					activenotes[tmp] = activenotes[tmp] + (kw0 + (kw1*(vel))+ (kw2*(proportion)));
					
				});
				
				}; 
		
				
				//should only really look at maxvel in new notes? 
				if(listOnsets.notEmpty,{
					//maxvel=0;
					
					listOnsets.do{|val| 
					
					tmp=val[1];
					
					if(tmp>maxnote, {maxnote=tmp;});
					if(tmp<minnote, {minnote=tmp;});
					
					};
					
					
				});
				
				
		
				//"keynow".postln;
				//activenotes.postln;
			
				//update key over this frame
				//keynow= this.findKey(activenotes);
		
				//[\keynow,keynow].postln;
		
				tmp=activenotes;
			
				lastNactive.pop;
				lastNactive.addFirst(activenotes);
				//safe operation even at first because sums all there
				activenotes= lastNactive.sum;
				
				//sum up profiles from last N frames (or also including other players!)
				lastNkey= this.findKey(activenotes);
				
				[\key,lastNkey].postln;
				
				
				//the note C versus C major scale will score badly here; what are you aiming for exactly? these frames will include not so many notes...
				keydist= (((tmp.normalizeSum)-(activenotes.normalizeSum)).squared.sum)/12.0;
				
				//update beat over last 2 seconds
				//LATER! 
				//measure of syncopation, lock-on and opposition
				
				
				//like concatenative synthesis, just higher level features! 
				newcreated= RLState.new; //create, pass all data in
				
				//features
				tmp= listOnsets.size;
			
				//4 allowed possibilities here; but doesn't differentiate with respect to chords? 
				//have added options of >4, >2, >0 and 0
				newcreated.numonsets= if(tmp>5,3,{if(tmp>2,2,{if(tmp>0,1,0); })});
				
				//no point categorising for now, since used in distance metric; velocity is already 0.0-1.0
				newcreated.maxvelocity= maxvel; //if(maxvel>99,{2},{if(maxvel>60,1,0)}); 
				
				tmp= minnote.max(24).min(107);
				tmp2= ((maxnote.max(24).min(107))-tmp);
				
				newcreated.bassoctave= (tmp-24).div(12);
				newcreated.spread= tmp2.div(12); 
				
				//context
				newcreated.bass= minnote;
				newcreated.treble=maxnote;
				newcreated.keydist= keydist;
				newcreated.key = (lastNkey[0]*2)+(lastNkey[1]); //alternating major and minor keys 
				newcreated.listOnsets= listOnsets; //iois to each note from bar start= val[0]- newcreated.framestart
				newcreated.listMIDInotes = activemidi; 
				newcreated.density= numplaying;
				
				//may have to account for different creation dates in the future! use Date class etc
				newcreated.framestart= framestart; 
				
				//potentially could get all of above from signal processing; onset detector, key tracker, register of pitch tracker etc
							
				this.compareprediction(newcreated);
				
				//set pointer to that which follows
				if(lastcreated.notNil,{
				
				//if not both zero 
				tmp = false; 
				if((lastcreated.density>0) && (newcreated.density>0),{tmp=true;});
				
				if(tmp,{
				lastcreated.action= newcreated; 
				
					//must store RLStates in a big array for recall of memory; rote learning, but with twists...
				this.addrlstate(lastcreated, lastcreated.getIndex);
				//returns index
				});
			
				
				});
	
				lastcreated = newcreated; 
				
				//search database for best fit to current state
				tmp2= newcreated.getIndex;
				
				//[\tmp2, tmp2].postln;
				
				//TO WRITE! 
				//prediction= this.findnextaction(newcreated, tmp2);
				
				//returns winner and topK now contains up to the best K candidates for RL update
				prediction= this.findtopK(newcreated, tmp2);
				
				
				//shouldn't really ever happen that next is dead! But safety first... 
				if(prediction.notNil, {prediction.generate;}); 	
								
				framesize.wait;
			};
			
		}.fork;
		
		
		
		
		//add new frame
	
	}
	
	
	addrlstate {|newcreated, index|
		var tmp, tmp2, list; 
		var victim, score;
		 
		list= rlstates[index];
		
		tmp2= list.size;
		
		if(tmp2<100,{
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
	findtopK {|state, index|
		var list, score, winning;
		var rankings, bestvalue=1.0; 
		
		rankings= SortedList(numK,{|a,b| (a[0])<(b[0])});
		numK.do{rankings.add([9999999.9, nil])};
		
		
		list= rlstates[index];
		
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
		
		//choose one with highest score or with lowest value as official answer? 
		winning = rankings[0];
		
		//becomes List of any high scoring
		topK = List[]; 
		
		//winning.postln;
		//rankings.postln;
		
		rankings.do{|val| if(val[1].notNil,{topK.add(val[1]);  if(((val[1]).v)<bestvalue,{winning=val; bestvalue= val[1].v;}); }); };
		
		//winning.postln;
		
		^if((winning[1]).notNil, {winning[1].action},nil);
		//return prediction of next state
	}
	
	
	
	//compare actual outcome  = new state to the prediction (actually, to topK predictions)
	//reinforcement learning; update value based on this
	//same situation could have many possible actions; but exactly same situation hardly ever turns up? 
	//if it does, just maintain two of same possibility! One will have greater value!
	compareprediction {|state|
		var score; 
		
		topK.do {|candidate| 
		
		score= candidate.proximity(state); 
		
		//proximity is value from 0.0 to 1.0
		
		//interpolation from current value to new (proximity, ie prediction) error
		candidate.v= (candidate.v) + (learningrate*(score-(candidate.v))); 
		
		};
	
	}
	
	
	
	//not great, particularly on which leading note; should penalise E natural in Fsharp major say! 
	findKey {arg pchistogram;
		var tmp, best;
		
		//highest score wins!
		tmp=0;
		best=(1000.neg);
		
		24.do{arg i;
		var score, results;
		
		results= if(i.even,{major},{minor});
		
		results= results.rotate(i.div(2));
		
		score = (pchistogram * results).sum; 
		
		if(score>best,{best=score; tmp=i;});
		
		};
		
	//^[\key, (["C","Db","D","Eb","E","F","F#","G","Ab","A","Bb","B"].at(tmp.div(2)))+(if(tmp.odd,"major","minor")),\keyarray,(tmp.div(2)+(if(tmp.odd,{major},{minor})))%12  ];
	
	^[tmp.div(2),if(tmp.even,0,1)] 
	}
	
	
	*initClass {
	
		StartUp.add({
		
		SynthDef(\midisound, {arg freq=440, amp=0.1; 
		
		Out.ar(0,SinOsc.ar(freq, 0, amp)*Line.kr(1,0,0.2,doneAction:2).dup(2));
		}).writeDefFile;
		
		SynthDef(\midiclick, {arg freq=1, amp=0.5; 
		
		Out.ar(0,Decay.ar(Impulse.ar(freq, 0, amp),0.01)*Line.kr(1,0,0.1,doneAction:2).dup(2));
		}).writeDefFile;
		
		});
	
	}
	
	
	
	
}
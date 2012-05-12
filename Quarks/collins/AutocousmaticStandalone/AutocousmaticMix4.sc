//Autocousmatic by Nick Collins (c)2009-2011, released under GNU GPL 3 August 2011
 

//fourth attempt at final renderer 
 
//section based construction: also work on transition points... 

//iterative processing removed for now... should just be part of single processing run? 

//next: amp envelopes, overlap amounts in placing files 
  
 //fix for 3.4 Score change 
+ Nil {
	
	quote {
		^"\"placeholder\""	
	}	
		
}  
  
 
 //section lengths from corpus details
 //types of transition
 //layering strategies when more than one source type combined
 //processing that causes greater elongation
 
+ Autocousmatic { 
	 
	 
	setupSynthDef {
		
		
		numChannels.do{|i|  
			 
			SynthDef(\AutocousmaticRender++((i+1).asString), { |out=0 bufnum=0 amp1=0.5 amp2=0.5 startpos=0.0 dur=1.0 pan=0.0|  
				var input, playbuf;   
				var betweenspeakers;  
				var env= Line.ar(amp1, amp2,dur, doneAction:2).squared; //more perceptual volume  
				  
				//env= EnvGen.ar(Env([1,1],[dur]),doneAction:2);   
				  
				//no changing rate scale on the fly  
				playbuf= PlayBuf.ar(i+1, bufnum,BufRateScale.ir(bufnum), 1,BufSampleRate.ir(bufnum)*startpos,0);  
					 
				betweenspeakers= 2.0/(i+1);  
				
				if(i==(numChannels-1)) {
				Out.ar(out,playbuf*env); 
				} {
				
				if(i>0) {
				  
				Out.ar(out,Mix.fill(i+1,{|j| PanAz.ar(numChannels, playbuf[j]*env, pan+(j*betweenspeakers))}) ); 		 		}
				{
				Out.ar(out,PanAz.ar(numChannels, playbuf*env, pan) ); 
				}
				
				}; 
				
				  
			}).writeDefFile;   
			 
		}; 
		 
		
		
	} 
	 
	 
	 
	mix4 { |whichrender=0|
		 
		var lengthavailable;  
		var excess;  
		var score, scoretosort;  
		var position;  
		var options, a, limit;  
		var finalsoundfile;  
		var maxlevels;  
		var overallmax, nearmax;  
		var numsources, sourcechances; 
		var sectionduration;
		var sectioncontents;  
		var left; 
		var workingdensity,workinggap,workingpos; //files per second 
		var sectionamp, useamp, temp, temp2; 

		//need to make one SynthDef for each situation of channel conversion, including both new pan information and ? Else easier to have sorted already in individual sound processing stage   
		var soundfilesbyderivation; 
		 
		 this.setupSynthDef; 
		 
		//could also prune out anything over 2.0 or so maxamplitude; must compensate though when mixing together
		//sectionnow.sound.maxamplitude; 
		maxlevels=  (sections.collect{|sectionnow| sectionnow.processedfiles.collect{|val| val.maxamplitude;}  }).flatten;  
		overallmax = maxlevels.maxItem;  
		 
		nearmax= maxlevels.sort;  
		//nearmax= nearmax.at(((nearmax.size-1)*0.975).asInteger);  
		nearmax= nearmax.at(((nearmax.size-1)*0.9).asInteger);  
		 
		[\nearmax, nearmax, \overallmax, overallmax].postln; 
		
		//actually, if normalize must normalize originating file. Not worth it? or flag to normalize on buffer load! 
		//could use normalize if maxamplitude > 1.0
		//sections.do{|sectionnow| sectionnow[3].do{|val| if(val.maxamplitude>1.0);}  }.flatten;  
		
		 
		//nearmax=  nearmax.copyRange(((nearmax.size-1)*0.95).asInteger, mearmax.size-1);  
		//nearmax= nearmax.minItem;  
		 
		//can set Limiter to limit anything between nearmax and maxlevels?  
		 
		 
		//will normalize later for now 
		
		SynthDef(\AutocousmaticFinalMix,{ 
			var source;  
				 
			source= In.ar(0,numChannels);  
				 
			//FreeVerb 
			//so anything over nearmax will be limited 
			//additional factor to cope with simultanous sounds? DIVIDE BY 2
			ReplaceOut.ar(0,Limiter.ar(source*0.5*(0.9/nearmax),0.99,0.01));  //*0.3
				 
		}).writeDefFile; 
		 
		
			 
			 
		 
		//if had kept track of derivations, could use sectional construction here 
		//place each of newsoundfiles somewhere 
		 
		//create top-down template for positionings based on activity levels? try to sync up onsets of attack 
		 
		//aim for a number of unison hits 
		//impactpoints =   
		 
		//climaxpoints; amplitude climax, climax of density, of timbral transformation extreme 
		 
			 
			 
			 
		//sorted when Score.new called, so no need to add messages in exact order	 
		score = List[];  
			
		//default maximum of 1026 buffers   
		if((newsoundfiles.size)>(Server.default.options.numBuffers), { 
			["AutocousmaticMix3: more sound files than buffers; increasing buffers in server options", newsoundfiles.size, Server.default.options.numBuffers].postln;
			
			Server.default.options.numBuffers = newsoundfiles.size; //obvious fix! // numBuffers
			 
		});  
			 
		newsoundfiles.do{|sound,j| 
		
		[\buffertest, j, sound.uniqueidentifier, sound.numChannels].postln; 
		
		score.add([0.0, [\b_allocRead, sound.uniqueidentifier, sound.filename, 0, -1]] ); 
		
		}; //read whole file 
		 
		position=0.01;  
			 
		//final limiter at tail 
		score.add([position, [ \s_new, \AutocousmaticFinalMix, -1, 1, 1]]);   
			 
		//rendering code: 
		
		
		sections.do{|sectionnow|
			
			sectionnow.createSection(numChannels);
	
		};
		
		
		sections.do{|sectionnow|
			var duration; 
			
			duration = sectionnow.duration; 
			
			score = score ++ sectionnow.renderSection(position);
		
			position = position+ duration; 

		};
		

		
		//lots of space for safety 
		score.add([duration+6.5, [\c_set, 0, 0]]);  
			 
		 
		//scoretosort= scoretosort.sort({|val1, val2| val1[0] < val1[1] }) 
		 
		//unifiying reverb and Limiter? 
		//[0.0, [ \s_new, \AutocousmaticFinalMix, 1000, 1, 1]]  
		 
		options = ServerOptions.new.numOutputBusChannels = numChannels; // mono output  
		//o = ServerOptions.new.numInputBusChannels = 1; // mono input  
		  
		//create in float format to preserve quality and avoid overload problems at this stage, so not int16
		
		Score.recordNRT(score, "NRTanalysis", renderdir++"/render"++whichrender++".wav", nil,44100, "WAV", "float", options); // synthesize 
		 
		SCMIR.processWait("scsynth", 5000); 
		 
//		limit=5000;	//corresponds to locking up for 500 seconds! but might be needed for big file analyses?  	 
//		0.01.wait; //make sure scsynth running first (can be zero but better safe than sorry) 
//		while({ 
//			a="ps -xc | grep 'scsynth'".systemCmd; //256 if not running, 0 if running 
//				 
//			a.postln; 
//				 
//			(a==0) and: {(limit = limit - 1) > 0} 
//			},{ 
//			0.1.wait;	 
//		}); 
//		  
//		0.01.wait;  
		 
		 
		//create as float file, can normalize afterwards by loading as a soundfile	 
	} 
	 
	 
}  

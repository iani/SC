//Autocousmatic by Nick Collins (c)2009-2011, released under GNU GPL 3 August 2011
 
+ AutocousmaticSoundFile  {


//mixed granulator for now
//create new output file by processing this one 
//preserve same number of channels as input in output
process1 {|outputfilename|

		//to vary over gesture
		var ampenv, densityenv; 
		var temp, temp2, temp3, array, size; 
		var fxsynth; 
		var playbackmode= 3.rand; 
		var playbacktemp= Array.fill(2,{duration.rand}).sort; 
		var panmode= 3.rand;
		var pantrack=1.0.rand2;  
		var durmode=3.rand; 
		var durtemp= rrand(0.1,1.0); 
		var interruptionlength;
		var outputChannels; 
		var score, options, limit, a; 
		var grainsources, nonsilencesavailable, onsetsavailable; 
		var sourcechangechance, sourcechangechancechangechance, sourcenow; 
		
		sourcechangechance= [0.5.rand,0.0,1.0.rand,rrand(0.2,0.7),1.0].choose; 
		sourcechangechancechangechance= [0.5.rand,0.0,1.0.rand,rrand(0.2,0.7),1.0].choose; //chance of change per grain
		
		//use onsets, or use nonsilences as grains
		
		nonsilencesavailable= nonsilences.size; 
		onsetsavailable = segments.size; 
		
		//if none of either, return early and don't bother to create anything
		if((onsetsavailable<1) and:(nonsilencesavailable<1),{^false; }); 

		if(onsetsavailable>=1) {
		
			grainsources = segments.copy.scramble.copyRange(0,onsetsavailable.rand); 
		};  

	
		if ( (onsetsavailable<1) or: (0.5.coin)) {
		
			grainsources = nonsilences.copy.scramble.copyRange(0,nonsilencesavailable.rand); 
		};   
		
		sourcenow= grainsources.choose; 
		playbacktemp= Array.fill(2,{(sourcenow[0])+((sourcenow[1]*0.9).rand)}).sort;
		
		//spread over final output or keep in space of input
		outputChannels= if(0.5.coin,{numChannels},{rrand(numChannels, rrand(numChannels, autocousmatic.numChannels));});
		
		//target output length
		interruptionlength= rrand(1.0,6.0); //exprand(0.5,5.0);
		 
		 
		temp=rrand(2,(4*interruptionlength).asInteger);
		//temp2= temp.div(2); 
		temp2= rrand(-30.0,0); 
		temp3= rrand(-5.0,5.0); 
		
		//var now= 0.0; if((i!=0) && (i!=(size)), {now=rrand(0.1,1.0)}); now
		array= Array.fill(temp,{|i| var now; 
		
		//var maxamp = (i-temp2).abs.asFloat/temp2; 
		
		temp2=temp2+rrand(0,temp3); 
			
		//possibility of jump
		if(0.2.coin,{temp2= rrand(-30,0); temp3= rrand(-5.0,5.0); },{
			
			if(temp2<(-40),{ if(temp3<0.0,{temp3= temp3.neg;}); }); 
		}); 
		
		if(0.1.coin,{temp3= rrand(-5.0,5.0); }); 
		
		//now=if(0.3.coin,{
//		rrand(0.1,1.0)
//		},{rrand(0.1,maxamp)}); 
		
		now= temp2.dbamp; 
		
		//if((i==0) || (i==(temp-1)),{now=0.0}); 
		
		now 
		
		});
		
		if(0.6.coin,{array= [0]++array; });
		if(0.5.coin,{array= array++[0]; });
		size= array.size; 
		
		ampenv = Env(0.5*array,Array.fill(size-1,{|i| rrand(0.1,1.0)}).normalizeSum*interruptionlength, Array.fill(size,{rrand(-6,6)})); 
		
		size= rrand(2,interruptionlength.asInteger+2);
		temp= rrand(0.1,1.0);
		array= Array.fill(size,{
		
		if(0.3.coin,{temp= rrand(0.1,1.0);},{temp= temp+(0.1.rand2);}); 
		if(temp>1.0,{temp=1.0;});
		if(temp<0.1,{temp=0.1;});
		
		temp
		}); 
		densityenv= Env(array,Array.fill(size-1,{rrand(0.1,1.0)}).normalizeSum*interruptionlength, Array.fill(size,{rrand(-6,6)})); 		
		
		

//create SynthDefs
		
		
		//difficulty with number of channels 
		//problem with PanAz if used for 2 channels is it completes circle, so -0.5 to 0.5 gives L to R



	//can't have a startpos bigger than duration? 
	//no loooping! 
	//restriction to nonsilences...


			//startpos in seconds into buffer 
		SynthDef(\AutocousmaticSynth1, { |out=0 bufnum=0 amp=0.1 rate =1.0 startpos=0.0 attack=0.001 sustain=1.0 release=0.001 pan=0.0| 
			var input, env, playbuf;  
				 
			env= EnvGen.ar(Env([0,1,1,0],[attack, sustain, release]),doneAction:2);  
				 
			//no changing rate scale on the fly 
			playbuf= PlayBuf.ar(numChannels, bufnum,BufRateScale.ir(bufnum)*rate, 1,BufSampleRate.ir(bufnum)*startpos,0); 
				 
			if(outputChannels == numChannels) {
			//no effect of panning, keep original
			Out.ar(0,playbuf*env*amp); 		
			} {
			// SplayAz
			//should keep pan in correct region
			
			if(numChannels==1) {
			Out.ar(0, PanAz.ar(outputChannels, playbuf*env*amp,pan) )
			
			}
			
			{
			Out.ar(0, Mix.fill(numChannels,{|i| PanAz.ar(outputChannels, playbuf[i]*env*amp,pan+(i*(2.0/outputChannels)))}) )
			
			}; 
			
			}; 
				 
		}).writeDefFile;  


		
		
		//since sent immediately should go before latency based grains 
		//create effects synth
		SynthDef(\AutocousmaticFX1,{|gate=1|
			var source, process, env; 
			var procfunc; 
			
			procfunc= {|input| [
			{|input| FreeVerb.ar(input,1.0,rrand(0.1,1.0),rrand(0.1,1.0))},
			//{|input| var numresonators= rrand(3,20); Klank.ar(`[Array.rand(numresonators,24.0,120.0).round(0.2).midicps, Array.rand(numresonators,0.01,0.5), Array.rand(numresonators,0.01,0.1)],input)*0.5},
			//{|input| Mix.fill(rrand(3,10), {BPF.ar(input,exprand(300,10000),rrand(0.1,1.0))}) },
			{|input| var numfilters=rrand(1,10);  Mix.fill(numfilters, {BPF.ar(input,exprand(300,10000),rrand(0.1,1.0))})/numfilters; },
			{|input| PitchShift.ar(input,exprand(0.001,1.0),[1.0,exprand(0.1,1.0),exprand(0.25,4),LFNoise1.kr(exprand(0.1,10.0),1.0,1.0)].choose, [0.0,rrand(0.0,0.1),exprand(0.05,1.0),LFNoise1.kr(exprand(0.1,10.0),0.1,0.1)].choose, [0.0,rrand(0.0,0.1),exprand(0.01,0.25),LFNoise1.kr(exprand(0.1,10.0),0.1,0.1)].choose)}
			].choose.value(input)}; 
			
			
			source= In.ar(0,outputChannels); 
			
			//asr
			env= EnvGen.kr(Env.asr,gate, doneAction:2);
			
			process=  procfunc.(source); 
			
			//iterative fx application
			if(0.3.coin,{process= procfunc.(process); }); 
			
			//mix with input
			Out.ar(0,env*process*rrand(0.1,0.9)); 
		
		}).writeDefFile; //play(group1, nil, \addToHead); 
		
	//	SynthDef(\AutocousmaticLimiter,{|gate=1|
//			var source; 
//						
//			source= In.ar(0,outputChannels); 
//		
//			Out.ar(0,Limiter.ar(source,1.0,0.005)); 
//		
//		}).writeDefFile; //play(group1, nil, \addToHead); 
//		
		//fork granules
		
		score = List[ 
		//playbufnum, fftbufnum, loggerbufnum, onsetfftbufnum, onsetloggerbufnum
		[0.0, [\b_allocRead, uniqueidentifier, filename, 0, -1]], //read whole file 
		
		[0.0, [ \s_new, \AutocousmaticFX1, 1000, 1, 1]],		//[0.0, [ \s_new, \AutocousmaticLimiter, 1001, 3, 1000]],		 
		]; 
		
		
		
		{
		var cumul= 0.0; 
		var density, waittime, dur, endtime;
		var mania= [rrand(0.1,0.4),rrand(0.1,0.9),1.0].wchoose([0.4,0.4,0.2]); 
		var duty= [mania, 2.0*mania, 1.0,2.0].choose;
		var playbackpos, panpos; 
		 
			while({cumul<interruptionlength},{
			
			if(sourcechangechancechangechance.coin) {sourcechangechance= [0.5.rand,0.0,1.0.rand,rrand(0.2,0.7),1.0].choose;}; 
			
			if(sourcechangechance.coin) {
					sourcenow= grainsources.choose;	//could use patterns etc 
					playbacktemp= Array.fill(2,{(sourcenow[0])+((sourcenow[1]*0.9).rand)}).sort;
		
			}; 
			
			density= 0.1*(densityenv.at(cumul)); 
			waittime= density*mania; //could be 1.0- but 1.0 will go to 0.0 gap 
			//worst case 0.1*0.1*0.1 = 0.001 which is too many, will overload 
			dur= switch(durmode,0,{density*duty},1,{duty},2,{durtemp});  
			
			//SAFETY FOR VERY SMALL GRAIN SIZES WHEN SCHEDULING BIG
			//SAFETY NOT NEEDED IN NRT
			if(waittime<0.0025,{waittime= 0.0025}); //minimal grain gap is 2.5 msec
			if(waittime<0.01,{if(dur>0.1,{dur= if(0.5.coin,{waittime},{(dur*0.5).min(waittime*20)}); })});  
			
			//playbackpos= switch(playbackmode, 0,{duration*(cumul/interruptionlength)},1,{rrand(playbacktemp[0],playbacktemp[1])},2,{duration.rand;});
			
			playbackpos= switch(playbackmode, 0,{(sourcenow[0])+(sourcenow[1]*(cumul/interruptionlength)) },1,{rrand(playbacktemp[0],playbacktemp[1])},2,{(sourcenow[0])+((sourcenow[1]*([0.5,0.9,1.0].choose)).rand);});
			
			if( (playbackpos+dur)>(sourcenow[1]),{dur= sourcenow[1]- playbackpos;});
			if(dur<0.01, {dur=0.01});
			
			panpos= switch(panmode, 0,{pantrack= (pantrack+(0.2.rand2)); if((pantrack<(-1.0)) || (pantrack>1.0),{pantrack=1.0.rand2}); pantrack},1,{1.0.rand2},2,{pantrack= (pantrack+(0.5.rand2)); if(pantrack<(-1.0),{pantrack=2.0+pantrack;}); if(pantrack>(1.0),{pantrack=2.0-pantrack;});  pantrack}); 
	
			//amp could have ending amplitude as well for richer behaviour? 
			//-1 generate nodeID, 0 is head of node 1 is group 1  
			score.add([cumul, [ \s_new, \AutocousmaticSynth1, -1, 0, 1, \bufnum, uniqueidentifier, \amp, ampenv.at(cumul), \rate, 1.0, \startpos,playbackpos, \attack, 0.001, \sustain,  dur-0.002, \release,0.001, \pan, panpos ]]); 
			//rrand(0.0,dur*0.5)
			
			cumul= cumul+ waittime; 
			
			}); 
			
			endtime= cumul-waittime+dur; 
			
			score.add([endtime, [ \n_set, 1000, \gate, 0]]); 
			
			score.add([endtime+1.0, [\c_set, 0, 0]]); 
			
		}.value; 
		 	 
		options = ServerOptions.new.numOutputBusChannels = outputChannels; // mono output 
		//o = ServerOptions.new.numInputBusChannels = 1; // mono input 
		 	 
		//create in float format to preserve quality and avoid overload problems at this stage, so not int16	 
		Score.recordNRT(score, "NRTanalysis", outputfilename, nil,44100, "WAV", "float", options); // synthesize

		limit=5000;	//corresponds to locking up for 500 seconds! but might be needed for big file analyses?  	
		0.01.wait; //make sure scsynth running first (can be zero but better safe than sorry)
		while({
		a="ps -xc | grep 'scsynth'".systemCmd; //256 if not running, 0 if running
		
		a.postln;
		
			(a==0) and: {(limit = limit - 1) > 0}
		},{
			0.1.wait;	
		});
 
		0.01.wait; 
		
		//SoundFile.normalize(outputfilename); 
		
		
//offline render

	^true; 

}



}
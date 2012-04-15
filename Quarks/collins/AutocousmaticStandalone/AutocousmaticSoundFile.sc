//Autocousmatic by Nick Collins (c)2009-2011, released under GNU GPL 3 August 2011
 
//it's in the mix for consideration if instantiated in this structure  
  
//add save and load to avoid recalculation?   
  
AutocousmaticSoundFile {  
	classvar <>nextID= 0;   
	classvar <>autocousmatic; //only one owning system
	classvar <>soundfile; 
	var <uniqueidentifier;   
	var <filename,<featurefilename,<onsetfilename;   
	var <duration, <numFrames, <sampleRate;   
	var <numChannels;   
	var <features, <onsets; 
	//refined features:
	var <originalf0, <refinedf0;  
	var <loudness, <rms, <maxpeaks, <hasfreq; 
	var <maxamplitude, <meanmaxamplitude;   
	var <silences, <segments;  
	var <nonsilences, <rhythm;   
	var <startpos, <usefulduration; //used to indicate first nonsilence, and last nonsilence end   
	var <>derivation; //what is the origination of this file, and what is the processing chain?  
	var <soundinglength; 
	  
	*initClass {
		soundfile= SoundFile.new;	
	}  
	  
	//analysis parameters  
	*getIdentifier {  
		  
		nextID = nextID+1;   
		  
		^nextID  
	}  
	  
	*new {|path|   
		  
		^super.new.initAutocousmaticSoundFile(path);    
	}   
	  
	initAutocousmaticSoundFile {|path|  
		  
		//load header in language and get necessary details   
		var sf= AutocousmaticSoundFile.soundfile; // SoundFile.new;   
		var success;  
		var filenamenoextension;
		 	 
		path.postln;  
		//originating folder:  path.pathOnly; 
		
		filenamenoextension= PathName(path).fileNameWithoutExtension; 
		featurefilename= (autocousmatic.analysisdir)++"/" ++ (filenamenoextension) ++".features"; 
		onsetfilename= (autocousmatic.analysisdir)++"/" ++ (filenamenoextension) ++".onsets";
		
		success= sf.openRead(path);   
		  
		if(success) { 
				 
			filename= path;    
			uniqueidentifier= AutocousmaticSoundFile.getIdentifier();   
			numFrames= sf.numFrames; 
			sampleRate= sf.sampleRate;
			duration= sf.numFrames/sf.sampleRate;  
			numChannels= sf.numChannels;  
		}; 
		  
		sf.close;  
		
		derivation= List[]; //start empty, will build it up with processing chain 
	}  
	  
	  
	//analyse an individual sound file 
	analyse { |carryoutanalysis=true|
		var buffersize, onsetsize,numfeatures;  
		var score;  
		var fftsize; 
		var hopsize; 
		var hopratio; 
		var options;  
		var a, limit; 
		var f; 
		 
		fftsize=1024; 
		hopsize= 512; 
		 
		hopratio= hopsize/fftsize; 
			 
		numfeatures=5; 
		//must be done at this stage because this is when you know the number of features to extract!  
		 
		if(carryoutanalysis) { 
		 
		//mono input only 
		SynthDef(\features1,{arg playbufnum, fftbufnum, loggerbufnum, onsetfftbufnum, onsetloggerbufnum, length;   
			var env, input, trig, fft, fft2, loudness,peaks,features; 
			var onsets, freq, hasFreq;  
				 
			env=EnvGen.ar(Env([1,1],[length]),doneAction:2);		 
			//stereo made mono 
			input= Mix(PlayBuf.ar(numChannels, playbufnum, BufRateScale.kr(playbufnum), 1, 0, 0))/numChannels;  
				 
			//get features 
			fft = FFT(fftbufnum, input, hopratio); //HOPSIZE = FFTSIZE 
			trig= fft+0.5; //ie will trigger whenever buffer changes 
			
			loudness= Loudness.kr(fft);
			
			#freq, hasFreq = Tartini.kr(input, overlap:1536); //so calculated every 512 samples to match up with other features 
			fft2= FFT(onsetfftbufnum, input);
			
			//delay trigger by one sample to avoid out of sync value; want value for this last period
			peaks= Peak.ar(input, Delay1.ar(K2A.ar(trig)));  
			
			features= [loudness,RunningSum.rms(input,hopsize), freq, hasFreq, peaks];
			
			Logger.kr(features, trig, loggerbufnum); 

			onsets= Onsets.kr(fft2, 0.5); //threshold 
	
			//each trigger increments counter, to count total number of onsets received? 
	
			///SampleRate.ir
			//sweep increases by 1.0 per second, thus measuring the time elapsed till onset
			Logger.kr(Sweep.ar(0.0, 1.0), onsets-0.01, onsetloggerbufnum); 
			
			//also see FeatureExtraction, MFCC, SpecPcile, SpecCentroid, ZCR et al
			//no output required, goes via logger buffer 
				 
		}).writeDefFile; 
			 
			 
		0.1.wait; 
		 
		buffersize= (((44100.0*duration)-fftsize)/hopsize).asInteger+2; //+1 for safety not needed unless rounding error on exact match  
		onsetsize= (20*duration).asInteger; //assuming maximum of 20 per second 

		score = [ 
		//playbufnum, fftbufnum, loggerbufnum, onsetfftbufnum, onsetloggerbufnum
		[0.0, [\b_allocRead, 0, filename, 0, -1]], //read whole file 
		[0.0, [\b_alloc, 1, fftsize, 1]],
		[0.0, [\b_alloc, 2, buffersize, numfeatures]],
		[0.0, [\b_alloc, 3, hopsize, 1]],	//onset FFT buffer 
		[0.0, [\b_alloc, 4, onsetsize, 1]], //how do you know how many onsets? Need to assume at least X per second...
		//[0.0, [\b_zero, 4]],
		//[0.0, [\b_fill, 4, 0, onsetsize, -1.0]], 
		[0.0, [\b_setn, 4, 0, onsetsize, Array.fill(onsetsize,{-1.0})].flatten], 
		
		[0.0, [ \s_new, \features1, 1000, 0, 0,\playbufnum,0, \fftbufnum, 1, \loggerbufnum,2,\onsetfftbufnum, 3, \onsetloggerbufnum,4,\length, duration]],		 
		//after length of soundfile played, end 
		[duration,[\b_write,2,featurefilename,"WAV", "float"]], 
		[duration,[\b_write,4,onsetfilename,"WAV", "float"]], 
		[duration, [\c_set, 0, 0]]  
		]; 
		 
		options = ServerOptions.new.numOutputBusChannels = 1; // mono output 
		//o = ServerOptions.new.numInputBusChannels = 1; // mono input 
		 
			 
		//using NRT mode, WriteBuf 
		//using recordNRTThen from ProcessTools Quark 
		//use checkEvery argument to choose how often to call it 
		//Score.recordNRTThen(score, "NRTanalysis", nil, nil,44100, "WAV", "float", options, checkevery: 0.1);  
		 
		//empty string is "", post 3.4 nil doesn't work due to failure of quote message  
		Score.recordNRT(score, "NRTanalysis", nil, nil,44100, "WAV", "int16", options); // synthesize

		limit=5000;	//corresponds to locking up for 500 seconds! but might be needed for big file analyses?  	
		0.05.wait; //make sure scsynth running first (can be zero but better safe than sorry)
		
		while({
		a="ps -xc | grep 'scsynth'".systemCmd; //256 if not running, 0 if running
		
		a.postln;
		
			(a==0) and: {(limit = limit - 1) > 0}
		},{
			0.1.wait;	
		});
 
		0.01.wait; 
		
		}; 
		
		
		"loading extracted features".postln;
				
		f = AutocousmaticSoundFile.soundfile; //SoundFile.new;			
		f.openRead(featurefilename);
		
		0.05.wait;
		
		//numfeatures == f.numChannels
		features= FloatArray.newClear(f.numFrames*f.numChannels);
		
		f.readData(features);
		
		0.2.wait;
		
		f.close;		//closes current file, not the whole soundfile object 
		
		0.05.wait;
		
		f.openRead(onsetfilename);
		
		0.05.wait;
		
		//just one channel here
		onsets= FloatArray.newClear(f.numFrames);
		
		f.readData(onsets);
		
		0.2.wait;
		
		f.close;
		
		0.05.wait;
		
		"loaded extracted features".postln;
		
		this.considerfeatures; 
		this.segment; 
				 
	} 
	
	//look for consecutive samples outside of 
	checkfordistortion {|filenamenow|
			
		var f, data, conseqcount=0; //conseqflag 	
		var check= false; 
			
		f = AutocousmaticSoundFile.soundfile; //SoundFile.new;			
		f.openRead(filenamenow);
		
		0.05.wait;
		
		//numfeatures == f.numChannels
		data= FloatArray.newClear(f.numFrames*f.numChannels);
		
		f.readData(data);
		
		//must check all channels? 
		//conseqflag= Array.fill(f.numChannels,{0}); 
		
		//no check for first
		(f.numFrames-1).do{|i| 
			var offset1= f.numChannels*(i);
			var offset2= offset1+(f.numChannels); 	
				
			f.numChannels.do{|j| 
				
				var prev, now; 
				
				prev= data[offset2+j]; 
				now= data[offset2+j]; 
				
				if( ( (prev>=0.99999999999) && (now>=0.99999999999) ) ||  ( (prev<=(-0.99999999999)) && (now<=(-0.99999999999)) ) ) {conseqcount= conseqcount+1; }; 
			
			};
		
		};
		
		//more than 10 problems, too loud, distorted
		if(conseqcount>10,{check=true}); 
		
		0.2.wait;
		
		f.close;		//closes current file, not the whole soundfile object 
		
		
		^check; 
	}	
	
	
	segment {
	//find regions of silence, interpret onset positions
	//using feature data and onsets, make decisions about event sections
	//-60 dB is 0.001.ampdb
	//loudness below about 0.2 is silence, for rms about 0.001
	
		//find section of 'silence' (could be within noise floor) over 2500 samples long to be significant (worth pruning); about 100 msec, ie 10 frames
		
		var numinarow=0; 
		var featurelength=loudness.size; 
		var nextsilence, silentindex; 
		var framestotime= 512.0/sampleRate; 
		var lastnonzero, prevvalue; 
		var temp; 
		
		silences= List[]; 
		
		featurelength.do{|i| 
		
			if( (loudness[i]<0.25) && (rms[i]<0.002) ) {
				numinarow= numinarow+1; 
			}  
			
			{
			
			if (numinarow>10) {
			
				silences.add([(i-numinarow)*framestotime,numinarow*framestotime]); 
			};
			
			numinarow=0; 
			
			};
		
		}; 
		
		//to cope with end conditions
		if (numinarow>10) {
		
			//can adjust to being very end of soundfile... use numFrames
			silences.add([(featurelength-numinarow)*framestotime,duration]); 
		};
		
		
		["silences",silences].postcs;
		
		if(onsets.size>1) {
		prevvalue= onsets[0];
		//now assess onsets, cross-referencing with regions of silence
		for(1, onsets.size-1, {|j| var value= onsets[j]; if ((value<prevvalue) && (lastnonzero.isNil)) {lastnonzero= j-1; } }); 
		}; 
		
		if(lastnonzero.notNil) {
		onsets=onsets[0..lastnonzero]; 
		};
		
		[\lastnonzero, lastnonzero, onsets].postcs;
				
		//rough positions good enough for now; really need separate program to do all analysis? 
		//could share pitch tracks with it...
		//refine positions using SoundFile itself in language? 
		
		//for now take distances as up to next onset, or next silence, whichever first
		segments= List[]; 
		
		if(onsets.notEmpty()) {
		
		//pure rhythm of onsets as IOIs
		rhythm= onsets.copy; //onsets.collect{|val| val[0];};
		rhythm= rhythm-rhythm[0]; 
		rhythm= rhythm.differentiate;
		if(rhythm.size>1){
		rhythm= rhythm[1..(rhythm.size-1)]; //knock off first zero element
		} {
		rhythm=[duration]; 
		}; 
		
		if(silences.notEmpty()) {nextsilence= silences[0][0]; silentindex= 0; } {
		nextsilence= duration+1.0; silentindex= nil; 
		};
		
		//[nextsilence, silentindex, silences].postcs; 
		
		onsets.do{|val,j| 
			var nextonsettime; 
			
			nextonsettime= if(j<(onsets.size-1),{onsets[j+1]},{duration}); 
			
			//[\prewhile, nextonsettime, nextsilence, silentindex].postcs; 
		
			while( { (silentindex.notNil) and:((nextsilence)<val) },{
			
				silentindex= silentindex+1; 
				
				if(silentindex>(silences.size-1),{nextsilence= duration+1.0; silentindex= nil; },				{nextsilence= silences[silentindex][0];});
				
				//[\inwhile, nextsilence, silentindex, val].postcs; 
		 
			}); 
			
			//[\postwhile, nextsilence, silentindex].postcs; 
		
			if( (silentindex.notNil) and:(nextsilence<nextonsettime),{
				nextonsettime= nextsilence; 
			}); 
			
			
			segments.add([val, nextonsettime-val])
		}; 
		
		} {rhythm=[duration]; }; 
		
		//each must be at least 100 msec long
		nonsilences= List[]; 
		
		if(silences.isEmpty(),{nonsilences.add([0.0,duration])},{
		
		temp=0.0; 
		
		silences.do{|val| 
			
			if( (val[0]-temp)>0.1,{nonsilences.add([temp,val[0]-temp])}); 
			
			temp= val[0]+val[1]; 
		
		};  
		
		if( (duration-temp)>0.1,{nonsilences.add([temp, duration-temp])}); 
		
		}); 
		
		
		//used to indicate first nonsilence, and last nonsilence end   
	  
		if(nonsilences.isEmpty) {
			usefulduration= 0.0; 
			startpos=0.0; 
			soundinglength = 0.0; 
		} {
			
			//sum duration of all nonsilences
			startpos= nonsilences[0][0]; //first nonsilent point
			
			temp= 0.0; 
			
			nonsilences.do{|val| temp= temp + val[1]}; 
			
			usefulduration= temp; 

			temp= nonsilences.last; 
			soundinglength= (temp[0]+temp[1])-startpos; 
		
		}; 
			
}
	
	
	
	
	
	
	
	
	//post process pitch trails 
	//rule 1: if loudness less than 2, freq estimate untrustworthy
	//rule 2: median filter frequency trails to avoid outliers 
	considerfeatures {
	var featurelength= (features.size).div(5);  //THIS CODE ASSUMES FIVE EXTRACTED FEATURES 
	//var loudness= Array.fill(featurelength, {|i| features[i*5]}); 
	//var rms= Array.fill(featurelength, {|i| features[i*5+1]}); 
	//var f0hasfreq= Array.fill(featurelength, {|i| features[i*5+3]}); 
	var f0trail= Array.fill(featurelength, {|i| features[i*5+2]});
	var f0hasfreq;
	var f0median; 
	var nowvalue, nextvalue, nextindex, averagevalue; 
	
	loudness= Array.fill(featurelength, {|i| features[i*5]}); 
	rms= Array.fill(featurelength, {|i| features[i*5+1]}); 
	hasfreq= Array.fill(featurelength, {|i| features[i*5+3]}); 
	maxpeaks=Array.fill(featurelength, {|i| features[i*5+4]}); 
	
	maxamplitude = maxpeaks.maxItem; 
	
	meanmaxamplitude = maxpeaks.mean; 
	
	//f0hasfreq.plot; 
	
	//f0hasfreq=f0hasfreq
	
	//want to disregard any points where has no f0 estimate from the median calculation, as junk data; 
	
	//used Tartini, so have to make a decision about how voiced it was; becomes binary decision at this point
	f0hasfreq = hasfreq.collect{|val, i| var decision = if(val<0.25,0,1); if(loudness[i]<2.0,{decision=0}); decision}; 
	
	//f0median= Array.fill(featurelength, {|i| var prev, post; prev= (i-5).max(0); post= (i+5).min(featurelength-1); (f0trail.at((prev..post))).median}); 
	
	f0median= Array.fill(featurelength, {|i| 
	var consideration, list= List[]; 
	var prev, post; 
	
	prev= (i-10).max(0); post= (i+10).min(featurelength-1); 
	
	consideration = (prev..post); 
		 
	consideration.do{|j| if(f0hasfreq[j]>0.5,{list.add(f0trail[j])})}; 
	
	//list.notEmpty	 
	if(list.size>5, {list.asArray.median},{0.0}); 
	
	}); 
	
	
	//variant of algorithm from PhD thesis, othello capture style 
	
	/*
	
	f0median = f0trail.cpsmidi.copy; 
	
	for(2,10,{|j|
	
	for(0, featurelength-1,{|i|
		
		if(f0hasfreq[i]>0.5) {
		
			nextindex= i+j;
			
			if (nextindex<=(featurelength-1)) { 
			
			if(f0hasfreq[nextindex]>0.5) {
			
				nowvalue= f0median[i]; 
				nextvalue= f0median[nextindex];  
			
				if ( (nextvalue-nowvalue).abs < 1.5) {
				
					averagevalue= (nextvalue+nowvalue)*0.5; 
					
					for(i+1,nextindex-1,{|k|
							
						if ( (f0hasfreq[nextindex]<0.5) or: ( ((f0median[k]-averagevalue).abs)>1.5 ) ) {
							f0median[k]= averagevalue; 	
						}; 	
							
					}); 
	
				}
			
			}; 
			
			};   
			
		}; 	
	
	}); 
	
	}); 
	 */
	
	/*
	
	f0median= Array.fill(featurelength, {|i| 
		var prev, post, consideration, list;
		var prelist, postlist, premed, postmed; 
		var prediff, postdiff, diff, originalval, outval; 
		var notfreqcount=0; 
		outval=f0trail[i];
		
		originalval= outval.cpsmidi;
		  
		
		//if median spreads too far, trouble at borders of a plateau; really want to check whether consistency either before or after...
		prev= (i-5).max(0); post= (i+5).min(featurelength-1); 
		
		//notfreqcount=0; 
//		
//		consideration = (prev..post); 
//		 
//		consideration.do{|j| if(f0hasfreq[j]<0.5,{notfreqcount= notfreqcount+1; })}; 
//		 
//		if(notfreqcount<(consideration.size*0.5)) {
//		
		prelist=List[]; 
		
		consideration = (prev..((i-1).max(0))); 
		 
		consideration.do{|j| if(f0hasfreq[j]>0.5,{prelist.add(f0trail[j])})}; 
		 
		premed= if(prelist.size>0, {prelist.asArray.median;},{0.0}); 
		 
		postlist=List[]; 
		  
		consideration = (((i+1).min(featurelength-1))..post); 
		 
		consideration.do{|j| if(f0hasfreq[j]>0.5,{postlist.add(f0trail[j])})}; 
		 
		postmed= if(postlist.size>0, {postlist.asArray.median;},{0.0}); 
		 
		prediff= ((originalval)-(premed.cpsmidi)).abs;
		postdiff= ((originalval)-(postmed.cpsmidi)).abs;
		diff = prediff+postdiff; 
		  
		if(prelist.notEmpty,{
		
		if(postlist.notEmpty) {
		
			//diff= ((postmed.cpsmidi)-(premed.cpsmidi)).abs;
		
			if(diff<5.0) {
				outval= outval; //no substantial change at this point 
			} {
			
				outval= postmed; 
			
			}
			
		} {
			if(prediff<2.5) {outval= outval;} {outval= premed;}; 
			
		};
		
		},{
		
			if(postlist.notEmpty) {
			
			if(postdiff<2.5) {outval= outval;} {outval= postmed;}; 
			
			
			} {
			
			//failure of continuity of any form
			outval=0.0; 
			
			}; 
		
		
		}); 
		 
		 outval; 
		 //} {0.0}; 
		 
		}); 

*/


	refinedf0= f0median; 
	originalf0= f0trail; 


	}	
	 

	  
	  
}  
 

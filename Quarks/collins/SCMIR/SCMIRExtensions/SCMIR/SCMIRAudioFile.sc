//one song in a collection, one movement in a symphony, one continuous audio file      
  
SCMIRAudioFile {
	var <>sourcepath, <>sourcedir, <>basename, <>analysispath;    
	var <>analysisfilename;      
	var <>duration, <>numChannels;      
	var <>featureinfo, <>featuredata, <>normalizationtype; 
	var <>numfeatures, <>numframes;   
	//var <nummfcc, <numchroma; //no longer used, no need to differentiate
	var <>numbeats, <>beatdata, <>tempi, <>tempo; //, <featuresforbeats;       
     var <>numonsets, <>onsetdata;
     var <>segmenttimes, <>numsegments, <>featuresbysegments;  
       
	//var normgroups;       
	  
	*new { | filename, featureinfo, normtype = 0 |
		if (filename.isNil) {
			"Meta_SCMIRAudioFile:new no filename provided".postln;
			^nil
		};  
		^super.new.initSCMIRAudioFile(filename, featureinfo, normtype);       
	}    

	initSCMIRAudioFile { | filename, fi, normtype = 0 |      
		//not multi-thread safe     
		if ( /* valid = */ SCMIR.soundfile.openRead(filename)) {
			this setPaths: filename;
			duration = SCMIR.soundfile.numFrames / SCMIR.soundfile.sampleRate;      
			numChannels = SCMIR.soundfile.numChannels;
		}{
			"SCMIRAudioFile: soundfile failed to load, wrong path?".postln;
		};
		SCMIR.soundfile.close;	       
		this.setFeatureInfo(fi, normtype); 
	}

	setPaths { | filename |
		sourcepath = filename;      
		sourcedir = sourcepath.dirname;   
		if (SCMIR.tempdir.isNil) {
			analysispath = sourcepath.dirname ++ "/";      
		}{     
			analysispath = SCMIR.tempdir; 	     
		};      
		basename = sourcepath.basename;     
		basename = basename.copyRange(0, basename.findBackwards(".") - 1);
	}
	
	//also loads from ZArchive file (usually already containing analyzed feature data)  
	*newFromZ {|filename|      
  		if (filename.isNil) {
	  		"Meta_SCMIRAudioFile:newFromZ no filename provided".postln;
	  		^nil
	  	};       
		//.initSCMIRAudioFile(filename) NO NEED, load should set everything required  
		^super.new.load(filename);       
	}     
	
	//copy settings of an existing file, for use with DTW comparison methods
	//assumes feature extraction already took place in original
	//doesn't copy everything, no segments, beats etc
	*newFromRange { | other, start = 0.0, end |
		if (other.isNil) {
			"Meta_SCMIRAudioFile:newFromRange no other file provided".postln;
			^nil
		};       
		if (other.featuredata.isNil) {
			"Meta_SCMIRAudioFile:newFromRange other file provided but no feature data".postln;
			^nil
		};    
		^super.new.initSCMIRAudioFileFromOther(other,start,end);
	}
	
	initSCMIRAudioFileFromOther { | other, start = 0.0, end |
		var framestart, frameend; 
		var top = other.numframes-1; 
		var timeperhop= SCMIR.hoptime;
		
		this.setFeatureInfo(other.featureinfo,other.normalizationtype); 
		numChannels = other.numChannels;     
		
		//don't bother	  
		//sourcepath = other.sourcepath;      
		//analysispath = other.analysispath;   	     
		//basename = other.basename;     
		 
		end = end ?? {other.duration}; 
		
		framestart = (start/timeperhop).asInteger; //rounding down
		frameend = (end/timeperhop).asInteger; 

		if(framestart<0) {framestart=0};
		if(framestart>top) {framestart=top};
		if(frameend<0) {frameend=0};
		if(frameend>top) {frameend=top};
		
		duration = (frameend-framestart)*timeperhop;      
		numframes = frameend-framestart+1; 
		numfeatures = other.numfeatures; 
		featuredata = other.featuredata.copyRange(
			framestart * numfeatures, (frameend + 1) * numfeatures - 1
		); 
	}      
	  
	//warning: will invalidate current feature data 
	//can also be used for resetting    
	setFeatureInfo { | fi, normtype = 0 |
		
		//check feature instructions      
		normalizationtype = normtype;     
		
		//invalidates old collected data since formats will be wrong now  
		//featuresforbeats = false;
		featuresbysegments = false;  
		featuredata = nil;  //any old data removed
		numbeats = nil; 
		beatdata = nil; 
		tempi = nil; 
		tempo = nil;   
		  
		featureinfo = fi ?? {[[MFCC, 10]]};   
		//before v0.4 used to be:      
		//[featureclass,normtype,featurespecificparams]     
		//[[MFCC, 1, 10]]    
		
		//impose feature defaults for MFCC?  
		//put anything not in a SequenceableCollection, in one  
		featureinfo= featureinfo.collect{|val| 
			var val2; 
			
			 val2 = if(val.isKindOf(SequenceableCollection)){val}{[val]};
			
			//defaults
			if((val2[0]==MFCC) && (val2.size==1),{val2 = [MFCC,10] });
			if((val2[0]==Chromagram) && (val2.size==1),{val2 = [Chromagram,12] });
			 
			 val2
			  };   
		  				
		//featureinfo.postln;  
		  
		numfeatures = 0;      
		numframes = 0;   
		//nummfcc = 0;     
		//numchroma = 0;     
		  
		featureinfo do: { | featuregroup |      
			//featuregroup.postln;	    
			//use Symbol rather than class name in case Tartini not installed in system   
			switch (featuregroup[0].asSymbol,    
				\MFCC, {    
					//assumes featuregroup[1] exists!!!!!!!!!!!!!!
					//nummfcc= featuregroup[2];    
					numfeatures = numfeatures + featuregroup[1]; //nummfcc;     
				},    
				\Chromagram, {    
					//numchroma= featuregroup[2];    
					numfeatures = numfeatures +  featuregroup[1]; //numchroma;     
				}, 
				\Tartini, {    
					numfeatures = numfeatures +  2;     
				},
				{ 
					numfeatures = numfeatures +  1;  
				}    
			);	    
		};     
	}  
	  
	// must be called within a fork? How to enforce, test that?       
	extractFeatures { | normalize = true, useglobalnormalization = false | //|writefeaturefile= false|   
		  
		var fftsizetimbre = 1024;    
		var fftsizepitch = 4096; //for chromagram, pitch detection  
		var fftsizespec = 2048;   
		var fftsizeonset = SCMIR.framehop; //512 or 1024; 
		// should really be 512 with 256 overlap, but need to conform to general frame size choice 
		var featurehop = SCMIR.framehop; //1024; //measurement about 40Hz
		var score; //, analysisfilename;     
		var serveroptions, buffersize;     
		var temp;     
		var normdata;     
		var ugenindex;   
		var file; 
		var def;   
		  
		postf("Extracting features for: %\n", sourcepath);  
		  
		// safety if called multiple times and switched to beats later 
		// featuresforbeats = false;     
		featuresbysegments = false; 
		  
		// mono input only     
		def = SynthDef(\SCMIRAudioFileFeatures, { | playbufnum, length |      
			var env, input, trig, chain, centroid, features;     
			var mfccfft, chromafft, specfft, onsetfft;
			var featuresave;   
			  
			env = EnvGen.ar(Env([1, 1], [length]), doneAction: 2);		     
			// stereo made mono     
			input = if (numChannels == 1) {    
				PlayBuf.ar(1, playbufnum, BufRateScale.kr(playbufnum), 1, 0, 0);    
			}{
				Mix(PlayBuf.ar(numChannels, playbufnum, BufRateScale.kr(playbufnum), 1, 0, 0))
					/ numChannels;
			};
			// get features
			// ASSUMES SR of 44100 or 48000   
			
			if (featurehop == 1024) { 
				mfccfft = FFT(LocalBuf(fftsizetimbre,1),input,1, wintype:1);     
				chromafft = FFT(LocalBuf(fftsizepitch,1),input,0.25, wintype:1);     
				//for certain spectral features
				specfft = FFT(LocalBuf(fftsizespec,1),input,0.5, wintype:1);     
				onsetfft = FFT(LocalBuf(fftsizeonset,1),input,1);     
				} {
				//else it should be 512
				mfccfft = FFT(LocalBuf(fftsizetimbre,1),input,0.5, wintype:1);     
				chromafft = FFT(LocalBuf(fftsizepitch,1),input,0.125, wintype:1);     
				//for certain spectral features
				specfft = FFT(LocalBuf(fftsizespec,1),input,0.25, wintype:1);     
				onsetfft = FFT(LocalBuf(fftsizeonset,1),input,1); //will be smaller to start with
			};   
			  
			  
			trig = chromafft;     
			  
			features = [];     
			  
			featureinfo do: { | featuregroup |      

				//special case for onsets and beat detection; each requires own FeatureSave UGen?  
				//in principle, mfccfft reused, but should be fine since only analysis operations 

				features = features ++ (switch(featuregroup[0].asSymbol,
				\MFCC, {
					MFCC.kr(mfccfft,featuregroup[1]);      
				},    
				\Chromagram, {    
					Chromagram.kr(chromafft, 4096, featuregroup[1]);    
				}, 
				\Tartini, { Tartini.kr(input, 0.93, 2048, 0, 2048-featurehop) },
				\Loudness, { Loudness.kr(mfccfft) },
				\SensoryDissonance, { SensoryDissonance.kr(specfft, 2048) },
				\SpecCentroid, { SpecCentroid.kr(specfft) },
				\SpecPcile, { SpecPcile.kr(specfft, featuregroup[1] ? 0.5) },
				\SpecFlatness, { SpecFlatness.kr(specfft) },
				\FFTCrest, { FFTCrest.kr(specfft, featuregroup[1] ? 0, featuregroup[2] ? 50000) },
				\FFTSpread, { FFTSpread.kr(specfft) },
				\FFTSlope, { FFTSlope.kr(specfft) },
				//always raw detection function in this feature extraction context
				\Onsets, { Onsets.kr(onsetfft, odftype: (featuregroup[1] ?  \rcomplex), rawodf: 1) },
				//more to add: FFTRumble (in combination with pitch detection, energy under f0) 
				\RMS, { Latch.kr(RunningSum.rms(input,1024),mfccfft) },
				\ZCR, { Latch.kr(ZeroCrossing.ar(input),mfccfft) }
				));	  
			};     
			featuresave = FeatureSave.kr(features, trig);    
			  
			//issue in that doesn't seem to correspond to necessary unit index 
			//ugenindex =  featuresave.synthIndex;   
			//must check post hoc, because of optimisation changes   
			  
			//[\ugenindex, ugenindex].postln;  
			  
			//no actual output required, goes via logger buffer     
			  
		}); 
		
		//find synth index for FeatureSave
		
		def.children do: { | val, i | if (val.class == FeatureSave) { ugenindex = val.synthIndex } };
		def.writeDefFile;

		//for batch processing, need this fork outside;     
		  
		//wait for SynthDef sorting just in case     
		//0.1.wait;     
		
		//SCMIR.waitIfRoutine(0.1); 

		analysisfilename = analysispath ++ basename ++ "features.data";   
		//analysisfilename= analysispath++basename++"features.wav";     
		//analysisfilename.postln;     
		  
		//allow for 10 beats per second, else unreasonable, 
		// 2 floats per beat = [beat time, curr tempo estimate]        
		//windows per second * length . numfeatures is number of channels in buffer?      
		//initial delay in FFT implementation is hopsize itself.      
		buffersize = ((44100 * duration) / featurehop).asInteger; 
			// + 1 for safety not needed unless rounding error on exact match
		score = [
			[0.0, [\b_allocRead, 0, sourcepath, 0, 0]],     
			[0.0, [ \s_new, \SCMIRAudioFileFeatures, 1000, 0, 0, \playbufnum, 0, \length, duration]], 
			// plus any params for fine tuning   
			// [0.0, [\u_cmd, 1000, ugenindex, "createfile", analysisfilename]], 
			[0.01, [\u_cmd, 1000, ugenindex, "createfile", analysisfilename]], 
		// can't be at 0.0, needs allocation time for synth before calling u_cmd on it  
		// [0.0, [\b_alloc, 1, buffersize, numfeatures.postln]],     
		// [0.0, [ \s_new, \SCMIRAudioFileFeatureExtraction, 1000, 0, 0, \playbufnum, 0, 
		// \loggerbufnum,1,\length, duration]], 
		// plus any params for fine tuning     
		  
		//after length of soundfile played, end     
		//[duration,[\b_write,1,analysisfilename,"WAV", "float"]],  
			[duration,[\u_cmd, 1000, ugenindex, "closefile"]],     
			[duration, [\c_set, 0, 0]]      
		];  

		serveroptions = ServerOptions.new;    
		serveroptions.numOutputBusChannels = 1; // mono output     
		  
		//can set how verbose to be?      
		  
		//"NRTanalysis.wav"     
		//issue with Score under 3.4 that it doesn't accept a nil argument for output?    
		
		Score.recordNRTSCMIR(
			score, "/tmp/NRTanalysis", "/tmp/NRToutput", nil, 44100, "WAV", "int16", serveroptions
		); 
		// synthesize   
		//Score.recordNRT(score, "NRTanalysis", "NRToutput", nil,44100, "WAV", "int16", serveroptions); 
		// synthesize  

		//SCMIR.processWait("scsynth");    
		  
		//LOAD FEATURES  
		//Have to be careful; Little Endian is standard for Intel processors  
		file = SCMIRFile(analysisfilename, "rb");
		numframes = file.getInt32LE;
		//[\numframes,numframes].postln;  
		  
		temp = file.getInt32LE;  
		if (numfeatures!= temp) {
			"extract features: mismatch of expectations in number of features ".postln;  
			[numfeatures, temp].postln;   
		};   
		  
		temp = numframes*numfeatures;  
		featuredata= FloatArray.newClear(temp);     
		
		//faster implementation? 
		file.readLE(featuredata); 
		  
		file.close;   
		  
		//TODO normalisation step    
		if (normalize && (numframes>=1)) {
			featuredata = this.normalize(featuredata,false,useglobalnormalization);
		};		  
		("rm "++ (analysisfilename.asUnixPath)).systemCmd;
		"Feature extraction complete".postln;  
	}    

	  
	save { | filename | 
		/*	Archive detected features. 
			Archive - ascii  
			ZArchive - binary, better for this data
		*/
		var archive;
		archive = SCMIRZArchive.write(this.archivePath(filename, ".scmirZ"));
		this.class.instVarNames do: { | iname | archive.writeItem(this.perform(iname)); };
		archive.writeClose;
	}  
	  
	load { | filename | 
		var archive;
		archive = SCMIRZArchive.read(this.archivePath(filename, ".scmirZ"));  
		this.class.instVarNames do: { | iname |
			this.perform((iname++$_).asSymbol, archive.readItem); 
		};
		archive.close;  
	}

	archivePath { | filename, ending = ".scmirZ" |
		^(filename ?? { sourcepath ++ basename ++ ending }).asAbsolutePath;
	}

	exportARFF { | filename |
		var file; 
		var last = numfeatures-1;
		file = File(this.archivePath(filename, "features.arff"), "w"); 
		file.write("@RELATION SCMIR\n");
		numfeatures do: { | i |
			file.write("@ATTRIBUTE"+i+"NUMERIC\n");
		};
		file.write("@DATA\n");
		numframes do: { | i |
			var outputstring; 
			var pos = i*numfeatures; 
			var array; 
			
			array = featuredata[pos .. (pos + last)]; 
			outputstring = ""; 
			array do: { | val, j | 
				if (j<last) {
					outputstring = outputstring++val++",";
				} {
					outputstring = outputstring++val++"\n"; 
				};
			}; 
			file.write(outputstring);
		};
		file.close; 
	}  

	//feature data exported with instances attached to a given class
	exportARFFInstances { | file, category |
		var last = numfeatures - 1;
		if (file.isNil) {
			"exportARFFInstance: no ARFF file provided".postln; 
			^nil;       
		};
		numframes do: { | i |
			var outputstring; 
			var pos = i * numfeatures; 
			var array;
			array = featuredata[pos..(pos+last)]; 
			outputstring = ""; 
			array do: { | val, j | 
				if (j < last) {
					outputstring = outputstring ++ val ++ ",";
				}{
					outputstring = outputstring ++ val ++ "," ++ category ++ "\n"; 
				};
			}; 
			file.write(outputstring);
		};
	}  
}      
  
  
  
  
 

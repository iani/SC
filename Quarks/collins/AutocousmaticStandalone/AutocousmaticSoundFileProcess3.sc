//Autocousmatic by Nick Collins (c)2009-2011, released under GNU GPL 3 August 2011
 
 + AutocousmaticSoundFile  {  
	  
	  
	//create new output file by processing this one   
	//preserve same number of channels as input in output  
	process3 {|outputfilename, secondfile|  
		  
		//to vary over gesture  
		var temp, temp2, temp3, array, size;   
		var interruptionlength;  
		var outputChannels, panpos;   
		var score, options, limit, a;   
		var playbackrate=1.0;   
		var envelopes;   
		var outputlength, sourcelength, startposition; 
		  
		//spread over final output or keep in space of input  
		outputChannels= numChannels; //if(0.5.coin,{numChannels},{rrand(numChannels, rrand(numChannels, autocousmatic.numChannels));});  
		panpos= rrand(0.0,rrand(0.0,1.0))*((-1)**(2.rand)); //not really needed  
		  
		//target output length  
		outputlength= rrand(1.0,6.0); //exprand(0.5,5.0);  
		  
		//choose any segment over 1.0 long, or use startpos and soundinglength, perhaps with subsegment  
		  
		startposition= startpos;   
		sourcelength= soundinglength.min(secondfile.soundinglength);   
		  
		//make sure a bit less source than fx, in case of reverb overlap?   
		if(sourcelength>(outputlength-0.5)) { sourcelength= outputlength-0.5}; //may lead to abrupt cut?   
		  
		if(0.1.coin,{playbackrate = (-1.0); startposition= startposition+sourcelength; }); //run backwards  
		if(0.03.coin,{playbackrate= playbackrate * exprand(0.25,1.0); }); //slower rate, no chipmunks allowed by going faster  
		  
		//create SynthDefs  
		  
		//difficulty with number of channels   
		//problem with PanAz if used for 2 channels is it completes circle, so -0.5 to 0.5 gives L to R  
		  
		  
		  
		  
		//startpos in seconds into buffer   
		SynthDef(\AutocousmaticProcess3, { |out=0 bufnum=0 bufnum2=0 amp=1.0 rate =1.0 startpos=0.0 startpos2=0.0 attack=0.001 sustain=1.0 release=0.001 pan=0.0 fftbufnum1=0 fftbufnum2=0|   
			var input, env, playbuf, playbuf2;    
			var procfunc, processed, envelopes, params;   
			  
			env= EnvGen.ar(Env([0,1,1,0],[attack, sustain, release]),doneAction:2);    
			  
			////could make repeating amps, cyclically, with scramble, or via copy substitutions  
//			envelopes = Array.fill(3,{var nodes= rrand(2,10); Env(Array.fill(nodes,{rrand(0.0,1.0); }), (Array.fill(nodes-1,{rrand(0.0,1.0)}).normalizeSum) * outputlength, Array.fill(nodes-1,{rrand(-8,8)}))});   
//			   
//			params= 	envelopes.collect{|envnow| EnvGen.ar(envnow) };    
//			  
			//no changing rate scale on the fly   
			playbuf= PlayBuf.ar(numChannels, bufnum,BufRateScale.ir(bufnum)*rate, 1,BufSampleRate.ir(bufnum)*startpos,0);   
			playbuf2= PlayBuf.ar(secondfile.numChannels, bufnum2,BufRateScale.ir(bufnum2)*rate, 1,BufSampleRate.ir(bufnum2)*startpos2,0);   
			  
			if(numChannels==1,{
			
			if(secondfile.numChannels>1) {playbuf2= Mix(playbuf2)*(secondfile.numChannels.reciprocal)}; 
			
			},{
			
			if((secondfile.numChannels)!=numChannels,{playbuf2= SplayAz.ar(numChannels,playbuf2.asArray)}); //spread   
			
				
				
			});   
			  
			  
			//cross syntheses  
			//phase vocoder, vocoder, lpcanalyzer   
			  
			processed= [ 
				{|input, input2|
				
				var ringmod; 
				
				ringmod= input*input2; 
				
				Limiter.ar(5.0*ringmod,0.99,0.01);
				
				}, 
				{|input, input2|   
				var wsize= [64,256, 512, 1024].choose;
					
				Limiter.ar(LPCAnalyzer.ar(input, input2, [64,256, 512, 1024].choose,(rrand(0.001,rrand(0.1,1.0)) * wsize).floor.max(1),testE:([0,1].wchoose([0.8,0.2])), windowtype:([0,1].wchoose([0.6,0.4])) ));	    
  
				},  
				{|input, input2|   
				
				var analysisarray; 
				var freqs, rqs; 
				
				freqs= Array.fill(rrand(5,20),{|i| exprand(100,10000)});
				
				rqs=freqs.collect({|val| (((exprand(0.01,0.25)*val)).max(20)/val)}); 
				
				analysisarray= freqs.collect({|val,i|  Amplitude.kr(BPF.ar(input,val,rqs[i])); });	  
				Mix(freqs.collect({|val,i|  BPF.ar(input2,val,rqs[i])*(analysisarray[i]); }));
					  
				},  
				{|input, input2|	  
					
					var fft1, fft2, fftprocess; 
					  
			  		//must make sure both mono before FFTs
			  		fft1= FFT(fftbufnum1, Mix(input)/input.size); 
			  		fft2= FFT(fftbufnum2, Mix(input2)/input2.size);
			  			
			  		fftprocess= [{IFFT(PV_MagMul(fft1, fft2))*0.5;},{IFFT(PV_CopyPhase(fft1, fft2));},{IFFT(PV_MagDiv(fft1, fft2))*0.5;}].choose.value; 	
			  			
			  		
			  	
					  
				}  
			].choose.(playbuf, playbuf2);   
		
			//no iterative fx applications here  
			//if(0.3.coin,{processed= procfunc.(processed); });   
			  
			  
			if(outputChannels == numChannels) {  
				//no effect of panning, keep original  
				Out.ar(0,processed*env*amp); 		  
				} {  
				// SplayAz  
				//should keep pan in correct region  
				  
				if(numChannels==1) {  
					Out.ar(0, PanAz.ar(outputChannels, processed*env*amp,pan) )  
					  
				}  
				  
				{  
					Out.ar(0, Mix.fill(numChannels,{|i| PanAz.ar(outputChannels, processed[i]*env*amp,pan+(i*(2.0/outputChannels)))}) )  
					  
				};   
				  
			};   
			  
		}).writeDefFile;    
		  
		  
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
		[0.0, [\b_allocRead, secondfile.uniqueidentifier, filename, 0, -1]], //read whole file   
		[0.0, [\b_alloc, 1022, 1024, 1]], //fftbufnum1   
		[0.0, [\b_alloc, 1023, 1024, 1]], //fftbufnum2       
		[0.0, [ \s_new, \AutocousmaticProcess2, 1000, 1, 1, \bufnum, uniqueidentifier, \bufnum2, secondfile.uniqueidentifier, \rate, playbackrate, \startpos,startposition,  \startpos2,secondfile.startpos, \attack, 0.001, \sustain,  sourcelength-0.002, \release,0.001, \pan, panpos, \fftbufnum1, 1022, \fftbufnum2, 1023 ]],		//[0.0, [ \s_new, \AutocousmaticLimiter, 1001, 3, 1000]],	  
		[outputlength, [\c_set, 0, 0]];   
		];   
		  
		  
		options = ServerOptions.new.numOutputBusChannels = outputChannels; 		 	   
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
		  
		^true;   
		  
	}  
	  
	  
	  
}  
 

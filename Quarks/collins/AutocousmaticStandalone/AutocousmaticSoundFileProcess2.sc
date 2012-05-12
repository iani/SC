//Autocousmatic by Nick Collins (c)2009-2011, released under GNU GPL 3 August 2011
 
+ AutocousmaticSoundFile  {  
	  
	  
	//create new output file by processing this one   
	//preserve same number of channels as input in output  
	process2 {|outputfilename|  
		  
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
		sourcelength= soundinglength;   
		  
		//make sure a bit less source than fx, in case of reverb overlap?   
		if(sourcelength>(outputlength-0.5)) { sourcelength= outputlength-0.5}; //may lead to abrupt cut?   
		  
		if(0.1.coin,{playbackrate = (-1.0); startposition= startposition+sourcelength; }); //run backwards  
		if(0.03.coin,{playbackrate= playbackrate * exprand(0.25,1.0); }); //slower rate, no chipmunks allowed by going faster  
		  
		//create SynthDefs  
		  
		//difficulty with number of channels   
		//problem with PanAz if used for 2 channels is it completes circle, so -0.5 to 0.5 gives L to R  
		  
		  
		  
		  
		//startpos in seconds into buffer   
		SynthDef(\AutocousmaticProcess2, { |out=0 bufnum=0 amp=1.0 rate =1.0 startpos=0.0 attack=0.001 sustain=1.0 release=0.001 pan=0.0|   
			var input, env, playbuf;    
			var procfunc, processed, envelopes, params;   
			  
			env= EnvGen.ar(Env([0,1,1,0],[attack, sustain, release]),doneAction:2);    
			  
			//could make repeating amps, cyclically, with scramble, or via copy substitutions  
			envelopes = Array.fill(3,{var nodes= rrand(2,10); Env(Array.fill(nodes,{rrand(0.01,1.0); }), (Array.fill(nodes-1,{rrand(0.0,1.0)}).normalizeSum) * outputlength, Array.fill(nodes-1,{rrand(-8,8)}))});   
			  
			  
			params= 	envelopes.collect{|envnow| EnvGen.ar(envnow) };    
			  
			//no changing rate scale on the fly   
			playbuf= PlayBuf.ar(numChannels, bufnum,BufRateScale.ir(bufnum)*rate, 1,BufSampleRate.ir(bufnum)*startpos,0);   
			  
			procfunc= {|input| [ 
				{|input| var effect;   
					  
					//flanging  
					input= input+ LocalIn.ar(numChannels); //add some feedback  
					  
					effect= DelayN.ar(input,0.02,SinOsc.ar(params[0],0,0.005,0.008)); //max delay of 20msec  
					  
					LocalOut.ar((0.1*params[1])*BPF.ar(effect,(params[2]).linexp(0.0,1.0,1000,10000),0.1));  
					  
				Out.ar(0,effect); }, 
				{|input|   
					    
					var number=10;  
					  
					//chorus  
					  
					Mix.fill(number, {  
						var maxdelaytime= 0.03; //rrand(0.01,0.03);  
						//var half= maxdelaytime*0.5;  
						//var quarter= maxdelaytime*0.25;   
						  
						//%half+(quarter*LPF.ar(WhiteNoise.ar,rrand(1.0,10)))  
						DelayC.ar(input, maxdelaytime, LFNoise1.ar(params[0].linlin(0.0,1.0,5.0,10.0).lag(params[1].linlin(0.0,1.0,0.001,0.1)),0.01,0.02) )  
						  
					})*(number.sqrt.reciprocal);  
					  
					  
				},  
				{|input|   
					  
					DelayC.ar(input, 0.01, SinOsc.ar(params[0].linlin(0.0,1.0,5.0,10.0).lag(params[1].linlin(0.0,1.0,0.001,0.1)),0,0.0025,0.0075));  
					  
					  
				},  
				{|input|	  
					  
					  
					AllpassC.ar(input,0.02,SinOsc.ar(params[0]*0.4,0,0.009,0.01)); //max delay of 20msec  
					  
					  
				},
			//	{|input|	  
//					  
//					  
//					FreeVerb.ar(input,params[0],0.2+(0.8*params[1]),0.2+(0.8*params[2])); //max delay of 20msec  
//					  
//					  
//				},
				{|input|	  
					  
					var n = rrand(3,10); 
					var values;   
					  
					  //0.95.rand2
					values = [rrand(0.85,0.9),Array.fill(n-1,{rrand(0.1,0.8)}),Array.fill(n,{exprand(0.01,0.03)})];     
					

					
					if (input.size<=1) {
						
						//input= Select.ar(CheckBadValues.ar(input, post: 0) <= 0, [Silent.ar(1), input]);  
						
						NTube.ar(input,values[0],`values[1],`values[2]); 
					}
					{  
						
					//input= Select.ar(CheckBadValues.ar(input, post: 0) <= 0, [Silent.ar(input.size), input]);  
						
					input.collect{|val| NTube.ar(val,values[0],`values[1],`values[2]); };//max delay of 20msec  
					}; 
					  
				}  
			].choose.value(input)};   
			  //wchoose([0.0, 0.0, 0.0, 0.0, 0.0, 1.0])
			 
			 
			//safety against glitches and high pitches?  
			if (playbuf.size<=1) {
					  	
			playbuf= Select.ar(CheckBadValues.ar(playbuf, post: 0) <= 0, [Silent.ar(1), playbuf]);
			} {
			playbuf= Select.ar(CheckBadValues.ar(playbuf, post: 0) <= 0, [Silent.ar(playbuf.size), playbuf]);  
			};	  
			
			playbuf= LeakDC.ar(playbuf);   
			  
			processed=  procfunc.(playbuf);   
			  
			//iterative fx application  
			if(0.3.coin,{processed= procfunc.(processed); });   
			  
			  
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
		  
		[0.0, [ \s_new, \AutocousmaticProcess2, 1000, 1, 1, \bufnum, uniqueidentifier, \rate, playbackrate, \startpos,startposition, \attack, 0.001, \sustain,  sourcelength-0.002, \release,0.001, \pan, panpos ]],		//[0.0, [ \s_new, \AutocousmaticLimiter, 1001, 3, 1000]],	  
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
 

//Autocousmatic by Nick Collins (c)2009-2011, released under GNU GPL 3 August 2011
 


 
 
//section:  duration, available inputfile indices, density, processed filenames 
 
AutocousmaticSection { 
		 
	//state for if rendered and file cached; invalidate if changing 
		 
	var <rendered; //needs update if not set?  
	var <tempfilename; //if exists was rendered? 
		 
	var starttime, endtime; //within global piece 
	//var layer; //multiple layers can co-exist?  
		 
	var <list;  
	//render score:  
	//[[time, file], [time, file]] 
	//can invalidate certain sections and force recomposition at different levels 
		 
		 
	var <>duration;  
	var <>inputindices;  //should contain an array listing source types, tells you how many tyes available 
	var <>density;  
	var <>processedfiles;  
	var <>densityenv;  
	//will just be relative to this section 
	var <>score;  
		 
		 
	*new { 
			 
		^super.new.initAutocousmaticSection(); 	 
	} 
		 
	initAutocousmaticSection { 
			 
			 
	} 
		 
	 
	//can be called multiple times as try out different arrangements of material 
	createSection {|numChannels| 
		var maxlevels;   
		var overallmax;  
		var workingdensity,workinggap,workingpos; //files per second  
		var sectionamp, useamp, temp, temp2;  
		var punctuationamp, punctuationchance;  
		var revolutions=0; //number of times around gives layers to compensate for; but would have to only write to score late on, not used for now 
		var tightstart, tightend, neatalign, gapend, subsegmentchance, withfades; 
		var doclamping,clamplocations;  //set of unifying points for common onsets 
		var ampcompensation;  
		 
		score = List[];  
		 
		 
		tightstart= 0.5.coin;  
		tightend= 0.5.coin; //else overlaps allowed; if tightend, check lengths of file being used versus space available 
		neatalign= 0.5.coin; //overlaps or end to end stuff?  
		gapend= [0.0,rrand(0.5,1.5)].choose; 
		subsegmentchance = [0.0,rrand(0.0,1.0)].choose;  
		withfades= 0.5.coin;  
			 
		if(gapend>duration) {gapend=0.0};  
			 
		//doclamping=  
		//clamplocations=  
			 
		//section based amplitude compensation 
		maxlevels= processedfiles.collect{|val| val.maxamplitude;};   
		overallmax = maxlevels.maxItem;   
			 
		//must compensate for this if too low?  
		ampcompensation = 1.0; ///NO COMPENSATION: supposedly taken care of by global limiter //overallmax.reciprocal;  
			 
		temp= [rrand(2,6),rrand(3,12)].choose; 
			 
		sectionamp= Env(ampcompensation*(Array.fill(temp,{[1.0,rrand(0.25,1.0),0.5,0.1,exprand(0.1,0.25)].choose})),((Array.fill(temp-1,{rrand(0.1,1.0)})).normalizeSum)*duration,Array.fill(temp-1,{rrand(-8,8)}));  
			 
		//what of occasional punctuations?  
		punctuationchance = [0.0,0.1,rrand(0.1,0.5)].choose;  
		punctuationamp = [0.1,1.0].choose; //quiet or loud 
			 
			 
		//useamp= 0.5.coin; //so stick at natural 1.0 amp multiplier usually 
			 
		if(0.4.coin,{sectionamp= Env([1,1]*ampcompensation,[duration])});  
			 
		//{sectionamp.plot;}.defer;   
			 
		workingpos= 0.0;  
			 
		//density decision, care taken with amplitude envelopes based on amplitude bounds and sudden or smoother section  
			 
		//choose number of files to fit in: 
		workingdensity= density; //exprand(0.5,rrand(2.5,5.0));  
		workinggap= if(0.8.coin,{rrand(1,3)*(workingdensity/0.33)*(duration/(processedfiles.size))},{workingdensity.reciprocal});  
		workingpos= 0.0;  
			 
		[\max, overallmax, \compensation, ampcompensation, \densitycompensation, workinggap.min(1.0)].postcs;  
			 
		//in first sweep make sure something on everything? want to avoid silences within file! have revised nonsilence counting procedure to help there  
			 
		processedfiles.do{|sound,whichsoundcount| 
			var notused = true;  
			var whichsynthdef, outbus=0, pan;   
			var startpos, lengthnow;  
					 
			while({notused},{ 
			 
				startpos= sound.startpos;  
				lengthnow= sound.soundinglength; //FAILURE LINE 
					 
				//if not enough space  
				//if((workingpos+(sound.duration))>(duration)) {position=[0.01,rrand(0.01,duration*0.2)].choose;};	 		 
				//+(sound.duration) 
				if((workingpos)>(duration-gapend)) { 
						 
					if(tightstart) {workingpos= 0.0;} 
					{ 
						workingpos=[0.0,rrand(0.0,1.5)].choose;   
					}; 
						 
					revolutions= revolutions+1;  
						 
				}; 
				//if it wraps, oh well 
				 
				//if sound itself longer, use some subsegment?   
				if (lengthnow>duration  || (subsegmentchance.coin)) { 
						 
					temp= sound.nonsilences.choose;  
					startpos= temp[0];  
					lengthnow= temp[1];  
						 
					//if still longer, will just have to put up with it!  
				}; 
					 
				if (tightend && ((workingpos+lengthnow)>duration) ) { 
						 
					if ((duration-lengthnow)>=0.0) { 
						workingpos= duration-lengthnow;  
							 
						//revolutions= revolutions+1;  
							 
						} { 
							 
						//hoping this helps, could make it worse, even!  
						temp= sound.nonsilences.choose;  
							 
						if(temp[1]<lengthnow) { 
							startpos= temp[0];  
							lengthnow= temp[1];  
							}{ 
							startpos=0.0; //fit it in as best you can 
						} 
							 
					} 
						 
				}; 
					 
				 
				//find closest clamp point, quantise position, use onsets data if necessary 
				//if (doclamping) 
					 
				 
				 
				//depends on how many channels  
				whichsynthdef = \AutocousmaticRender++(sound.numChannels.asString);   
				  
				//try to avoid extremes of single channel use (particularly in stereo)  
				//from left most speaker would be -1.0+(1.0/(sound.numChannels))  
				//but also want possibly of inbetween back speakers  
				  
				pan= 1.0.rand2;   
				  
				//degenerate case  
				if((sound.numChannels==numChannels) && (numChannels==2)) {  
					pan= -0.5; //to guarantee preserving current stereo relation, else can flatten image to centre  
				};   
				  
				//old out access, bus by bus rrand(0,numChannels-(sound.numChannels))  
				  
				//workinggap tries to compensate for density overlaps	  
				temp= (sectionamp.at(workingpos))*(workinggap.min(1.0));  
					 
				temp2= if(withfades,{(sectionamp.at(workingpos+lengthnow))*(workinggap.min(1.0));},{temp}); 	 
				//watch out for envelopes where both amp1 and amp2 really below -40dB, since essentially comes out silent!  
				//modified to both below -26 dB since only testing maxamplitude!  
				 
				if( (((temp*(sound.meanmaxamplitude)) <0.02) && ((temp2*(sound.meanmaxamplitude))<0.02) ) || (((temp*(sound.maxamplitude)) <0.05) && ((temp2*(sound.maxamplitude))<0.05) ),{ 
						 
					if(0.5.coin){temp= (rrand(-30,-10).dbamp) * (sound.maxamplitude.reciprocal)}{temp2= (rrand(-30,-10).dbamp) * (sound.maxamplitude.reciprocal)};  
						 
					//if(0.5.coin){temp= rrand(-30,-10).dbamp}{temp2= rrand(-30,-10).dbamp};  
						 
				});  
					 
					 
				[\sound, whichsoundcount, sound.filename, sound.maxamplitude, \amp1, temp, \amp2, temp2].postcs;  
					 
				//[position+workingpos, \channelstest, whichsynthdef, sound.numChannels, numChannels, sound.uniqueidentifier].postln;	 
				//make sure at least one run through without density pileup, for safety avoiding silent sections
				//only actually add if beat probability from density 
				if((densityenv.at(workingpos).coin) || (revolutions==0)) { 
						 
					//ampenv.at(position)  
					score.add([workingpos, [ \s_new, whichsynthdef, -1, 0, 1, \out, 0, \bufnum,sound.uniqueidentifier, \amp1, temp, \amp2, temp2, \startpos,startpos, \dur,lengthnow, \pan, pan]]);    
					notused = false; 	 
				}; 	 
					 
				if(neatalign) {	  
					workingpos= workingpos+ lengthnow;  
				} 
				{	  
					//could have varying overlap amounts  
					workingpos= workingpos + ([workinggap,rrand(0.0,2.0*workinggap),rrand(0.5,1.5)*workinggap].wchoose([0.7,0.1,0.2])); //(workingpos+(sound.duration)+([0.0,rrand(0.0,1.0),rrand(0.5,3.0)].choose))%workingpos;   
				}; 
					 
			}); //end of while; makes sure file used somewhere 
				 
		}; 
			 
			 
		  
	} 
	 
	 
	//later could render separately 
	//return score adjusted for global time offset 
	renderSection {|timeoffset|  
		var adjustedscore;  
			 
		adjustedscore = score.collect{|val|   [val[0]+timeoffset, val[1]]};  
			 
		^adjustedscore 
	} 
	 
		 
} 

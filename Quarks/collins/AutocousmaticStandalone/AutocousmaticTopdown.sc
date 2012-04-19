//Autocousmatic by Nick Collins (c)2009-2011, released under GNU GPL 3 August 2011
 

+ Autocousmatic {  
	  
	//create materials based on top-down direction of cross-synthesis and sectional need.   
	//Cageian use of time templates for materials  
	//  
	  
	  
	createsections {  
		var distinctsources, sourceprobability;   
		var sectiondur, totaldur, proportionpos;   
		var sourceindices;   
		var sectionpos, resetprob;   
		var combinationgroups, combinationpos;   
		//var averagesectionlength;   
		var tempsection;   
		var sourcedurations;   
		var transitionmodel;    
		var densitymodel; 
		  
		//assumption that contents of soundfiles are distinct and equivalent in useful mileage?   
		distinctsources= inputsoundfiles.size;   
		//square root of useful duration so small files not too biased against  
		sourceprobability= (inputsoundfiles.collect{|val| val.usefulduration**0.5}).normalizeSum; //relative probabilities of selection   
		sourceindices = (0..(distinctsources-1));   
		  
		sourceindices.postcs;  
		sourceprobability.postcs;  
		  
		sections= List[];  
		  
		totaldur = 0.0;   
		//averagelength=   
		  
		//set of available lengths?   
		//reusability conditions?  
		  
		//from real electroacoustic works!    
		//sourcedurations = [ 5.074, 6.372, 7.237, 15.425, 15.53, 15.915, 16.886, 21.696, 24.483, 25.125, 25.419, 28.221, 29.586, 30.0, 32.663, 32.932, 38.686, 39.692, 40.361, 41.512, 48.704, 51.799, 54.439, 55.44, 58.111, 59.901, 68.12266666667, 68.59, 77.093, 77.926, 88.873, 91.26256235828, 104.60399092971, 108.52, 155.123 ];  
		  
		sourcedurations= AutocousmaticSectionDurationModel();   
		densitymodel= AutocousmaticSectionDensityModel();   
		  
		//NEXT!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!  
		///transitionmodel  
		  
		  
		//duration as target total length  
		//need a length and mixing contents for each, plus general volume/density = activity targets  
		while({totaldur<duration},{  
			  
			//derive from data  
			  
			  
			sectiondur= sourcedurations.next; //(sourcedurations.size-1).rand; //[rrand(3.0,10.0),rrand(5.0,20.0),rrand(10.0,30.0)].choose;   
			  
			//sectiondur = rrand(sourcedurations[sectiondur],sourcedurations[sectiondur+1]);   
			  
			//if((totaldur+sectiondur)>(duration+5.0)) {sectiondur = max(sourcedurations[3.rand],(duration-totaldur)+0.1)};  
			  
			if((totaldur+sectiondur)>(duration+5.0)) {sectiondur = duration-totaldur+rrand(0.1,3.0)};  
			  
			proportionpos= totaldur/duration;   
			  
			//peak around 2/3 in?  
			  
			tempsection = AutocousmaticSection();   
			tempsection.duration = sectiondur;   
			
			//densities taken from section based analysis of example sources, with 0.8*norm + 0.2
		//	tempsection.density =  #[0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.44561504400123, 0.62151705213778, 0.62771572241266, 0.63601886013801, 0.66938103769424, 0.67475453043659, 0.70985773447858, 0.7119002177791, 0.72646349819437, 0.7336342802544, 0.76876461705318, 0.77261013021786, 0.77349362577382, 0.77862262442019, 0.77940497320004, 0.8095306440727, 0.83505345356843, 0.83973773411658, 0.84557776156242, 0.86182833942588, 0.86614542069108, 0.89550215779676, 0.92360929981362, 0.92373924875293, 0.99016068569936, 1.0, 1.0, 1.0, 1.0, 1.0 ].choose; //if(0.5.coin,{ 1.0-(abs(proportionpos- 0.66))},{ rrand(0.1,1.0);});   
			
			tempsection.density =  #[ 0.4, 0.4, 0.4, 0.4, 0.4, 0.4, 0.4, 0.4, 0.40305680760194, 0.40305680760194, 0.55094224576145, 0.59567138718726, 0.60102424650001, 0.63711959718822, 0.65594550122708, 0.65651678415332, 0.67840797434595, 0.68366178713978, 0.68667925036205, 0.68977932954701, 0.69156072838098, 0.69691596157325, 0.70557344896091, 0.71208756688617, 0.74790781927082, 0.77612379105468, 0.77955167497545, 0.77961137951536, 0.80212461742652, 0.80636548537377, 0.81128756249407, 0.81200385633552, 0.84351683130371, 0.86668746016308, 0.8779905736782, 0.90404907119097, 0.94042466248185, 0.97736970412662, 0.99304114812385, 1.0 ].choose; //if(0.5.coin,{ 1.0-(abs(proportionpos- 0.66))},{ rrand(0.1,1.0);});   
			
			
			tempsection.densityenv = densitymodel.createEnvelope(sectiondur); 
			  
			  
			//section should have a layering mode, and a transition mode into next and from previous (merge, abrupt)  
			  
			//sections.add([sectiondur, nil, if(0.5.coin,{ 1.0-(abs(proportionpos- 0.66))},{ rrand(0.1,1.0);}), nil ]);   
			sections.add(tempsection);   
			  
			totaldur= totaldur+ sectiondur;   
			  
		});  
		  
		//now allocate sources and densities  
		  
		combinationgroups= Array.fill(((sections.size).div(2)).max(1),{|i|    
			var number= [1,2,3,4].wchoose([0.3,0.4,0.2,0.1]);   
			var set= Set[];    
			  
			number.do{ set.add(sourceindices.wchoose(sourceprobability))};   
			  
			set.asArray;   
		});   
		  
		combinationgroups.postcs;   
		  
		  
		//allocate like a prime sieve; determine repetition time, and starting pos gradually creeps forward  
		sectionpos= 0;   
		combinationpos=0;   
		resetprob= 0.5;   
		  
		sections.do{|sectionnow,j|  
			var test;   
			var groupnow;   
			var forwardspos, forwardsshift;   
			  
			//test= sectionnow[1].isNil; //always intervene if nil   
			test= sectionnow.inputindices.isNil; //always intervene if nil   
			  
			if(resetprob.coin,{test=true;});  
			  
			if(test) {  
				  
				groupnow= combinationgroups[combinationpos];   
				  
				//sectionnow[1]= groupnow;   
				  
				sectionnow.inputindices= groupnow;   
				  
				forwardspos=j;   
				//proceed forwards, either erratically (1-3 each time), or every x (for x in 2,3,4,5,6,7)  
				forwardsshift= [rrand(2,4),rrand(4,7),{rrand(1,3)},{rrand(2,5)}].choose;   
				  
				while({forwardspos= forwardspos+ (forwardsshift.value); forwardspos<(sections.size) },{  
					  
					//sections[forwardspos][1]= groupnow;   
					  
					sections[forwardspos].inputindices = groupnow;   
					  
					  
				});  
				  
				  
				combinationpos=(combinationpos+1)%(combinationgroups.size);   
				  
				resetprob= 0.5;   
				} {  
				  
				//leave slot as is, but change chance of future intervention  
				resetprob= resetprob+rrand(0.1,0.25);   
				  
			}  
			  
		};   
		  
		^sections;  
		  
	}  
	  
	  
	//offset passed in and counted elsewhere  
	processgroup {|soundfilegroup, lengthnow, numbernow|   
		  
		var numberdone=0;   
		var lengthdone=0.0;   
		var testok;   
		var filenamenow;   
		var count=0; 	  
		var afiles= List[];   
		var newfile;   
		var sanitycheck=0;   
		  
		// numberdone<numbernow  
		while({lengthdone<lengthnow},{  
			  
			testok= true;   
			  
			filenamenow= tempdir++"/temp"++(tempfilecounter.asString)++".wav";   
			  
			//just using individually for now, no combinations  
			//or if not allowed to modify itself, force it to choose something else   
			testok= soundfilegroup.wrapAt(count).perform([\process1,\process2, \process3].choose,filenamenow, soundfilegroup.choose);  
			  
			//add cross-synthesis possibilities  
			  
			//TODO  
			  
			if(testok) {  
				  
				//CHECKING PROCESSED FILE FOR PROBLEMS   
				newfile= AutocousmaticSoundFile(filenamenow);   
				  
				if(newfile.uniqueidentifier.notNil,{  
					  
					("started analysis of " ++filenamenow).postln;  
					  
					newfile.analyse(true);   
					  
					("finished analysis of " ++filenamenow).postln;  
					  
					//checks for basic usefulness:  
					  
					if(newfile.maxamplitude.isNil) {testok= false;} {   
						if( ((newfile.maxamplitude.ampdb)<(-40)) || (newfile.usefulduration<0.1)) {testok= false;};   
						  
						//otherwise will swamp other sounds!   
						if(newfile.maxamplitude>50.0,{testok=false; });  
						  
					};   
					  
					  
					//(maxamplitude>) ||   
					  
					//this.cull; //remove any silent or 'unpleasing' soundfiles   
					  
					//look for distortions  
					//if(newfile.checkfordistortion(filenamenow)) {testok= false;};   
					  
				},{testok= false;});   
				  
			};   
			  
			  
			//if failed tests, will have to make a new processed file  
			if(testok || (sanitycheck>10)) {  
				count= count+1;   
				numberdone= numberdone+1;   
				tempfilecounter= tempfilecounter+1;    
				afiles.add(newfile); //save object here, no point loading and analyzing twice over!   
				  
				lengthdone= lengthdone+ (newfile.usefulduration);   
				  
				sanitycheck=0;  
			} {sanitycheck= sanitycheck+1; };   
			  
		});   
		  
		^afiles;  
	}   
	  
	  
	topdowncomposeOld {|number= 200, reanalyse=true, nummixes=1|  } 
	//new arguments probably  
	topdowncompose {|densityfactor= 15,nummixes=1, reanalyse=true|  
		var tempfiles, tempfilenames;   
		//var totaltempcreated=number;   
		var newgeneration, derivations; //will return [tempfilenames, derivationinfo]  
		//final mix can take account of derivation chain, matching similar processes from similar soundfiles together  
		var numbernow, lengthnow, offset=0;   
		  
		//use separate methods for each stage to avoid conceptual clutter   
		  
		{  
			 
			var timenow= Main.elapsedTime;  
			  
			if(reanalyse){  
				
				this.report("Analyzing sources"); 
				this.analyse(inputsoundfiles);   
			};  
			  
			this.report("Composing: may take a while! (don't worry about the post messages; go and use another program for a bit if you like and check back later...)");   
			  
			//sections=   
			this.createsections; 	  
			//each section has duration, involvedsoundfiles and density  
			  
			  
			//processing guided to create each section  
			//know individual files to process, and potential cross-syntheses  
			sections.do{|sectionnow|  
				var calc;   
				  
				//OR DYNAMICALLY DETERMINE NUMBER BASED ON LENGTHS OF PROCESSED FILES CREATED?   
				  
				//depends on section length and on density   
				//section:  duration, available inputfile indices, density, processed filenames  
				//numbernow = (sectionnow[0] * sectionnow[2]).asInteger+1;   
				//		  
				//		lengthnow = sectionnow[0]*10*sectionnow[2]; //up to ten times section length in density is ten files at once   
				//		  
				//		//cull any bad  = overloaded, overly distorted, files   
				//		sectionnow[3]= this.processgroup(inputsoundfiles.at(sectionnow[1]),lengthnow, numbernow); //assign processed file's filenames to this section  
				//		  
				  
				calc = sectionnow.duration * sectionnow.density;   
				numbernow = (calc).asInteger+1;   
				  
				//made slightly larger  
				lengthnow = densityfactor*calc; //up to ten times section length in density is ten files at once   
				  
				//cull any bad  = overloaded, overly distorted, files   
				  
				//sectionnow.processedfiles= this.processgroup(inputsoundfiles.at(sectionnow.inputindices),lengthnow,numbernow); //assign processed file's filenames to this section  
				  
				//maintain layers based on different sounds?   
				sectionnow.processedfiles= this.processgroup2(inputsoundfiles.at(sectionnow.inputindices),lengthnow,numbernow); //assign processed file's filenames to this section  
				  
				  
				  
			};   
			  
			  
			//master list, for iterating over all  
			newsoundfiles = List[];   
			  
			//sections.do{|sectionnow| sectionnow[3].do{|val| newsoundfiles.add(val);}  };  
			  
			sections.do{|sectionnow| sectionnow.processedfiles.do{|val| newsoundfiles.add(val);}  };  
			  
			  
			//render sections   
			//allow overlaps?  
			//this.mix3; //mix2  
			  
			//will do multiple times will different output filenames  
			
			   
			
			nummixes.do{|i|  
			this.mix4(i);   
			this.report("Creating final mix"+i);
			};
			  
			//for future work:   
			//now rate sections  
			  
			  
			//keep best, iterate on those below a quality threshold; judge transitions too?   
			 //["finished Autocousmatic run, took",Main.elapsedTime-timenow].postln; 
			  
			this.report("Finished Autocousmatic run, took" + ((Main.elapsedTime-timenow).round(0.001).asString) + "seconds");  
			  
			 running=false; 
			  
			  
		}.fork(SystemClock);   
		  
		  
	}  
	  
	  
	  
	//offset passed in and counted elsewhere  
	processgroup2 {|soundfilegroup, lengthnow, numbernow|   
		  
		//var numberdone=0;   
		var lengthdone=0.0;   
		var testok;   
		var filenamenow;   
		var count=0; 	  
		var afiles= List[];   
		var newfile;   
		var sanitycheck=0;   
		var iterationtarget, iterations, chooseiterations;   
		var nextsoundfile;   
		  
		////NEXT!!!!!!!!!!!!!!!!!!!!!!!!!!!!  
		  
		//for each group appearing, create materials of lengthnow  
		//keep them separated in own arrays (masterlist of files to load will be sorted later; should really depend on whether used, but don't worry if load too many?)  
		//more complicated if cross processing though!   
		  
		  
		  
		///////////////////////////////////  
		chooseiterations = {[1,2,3,rrand(2,5)].wchoose([0.7,0.15,0.1,0.05]);};  
		iterations = 0;   
		iterationtarget = chooseiterations.();   
		  
		nextsoundfile = soundfilegroup.wrapAt(count);  
		  
		// numberdone<numbernow  
		while({lengthdone<lengthnow},{  
			  
			testok= true;   
			  
			  
			filenamenow= tempdir++"/temp"++(tempfilecounter.asString)++"it"++(iterations.asString)++".wav";   
			  
			//just using individually for now, no combinations  
			//or if not allowed to modify itself, force it to choose something else   
			//was   [0.0,1.0,0.0,0.0]  [0.3,0.3,0.3,0.1]
			testok= nextsoundfile.perform([\process1,\process2, \process3, \process4].wchoose([0.3,0.3,0.3,0.1]),filenamenow, soundfilegroup.choose);  
			  
			//add cross-synthesis possibilities  
			  
			//TODO  
			  
			if(testok) {  
				  
				//CHECKING PROCESSED FILE FOR PROBLEMS   
				newfile= AutocousmaticSoundFile(filenamenow);   
				  
				if(newfile.uniqueidentifier.notNil,{  
					  
					("started analysis of " ++filenamenow).postln;  
					  
					newfile.analyse(true);   
					  
					("finished analysis of " ++filenamenow).postln;  
					  
					//checks for basic usefulness:  
					  
					if(newfile.maxamplitude.isNil) {testok= false;} {   
						if( ((newfile.maxamplitude.ampdb)<(-40)) || (newfile.usefulduration<0.1)) {testok= false;};   
						  
						//otherwise will swamp other sounds!   
						//HOW OFTEN DOES THIS OCCUR? WHY amplitude of 50!!!!!!!!!!!!!!!!!!!!!!!  
						  
						[\maxamptest,newfile.maxamplitude].postln;  
						  
						if(newfile.maxamplitude>50.0,{testok=false; });  
						  
					};   
					  
					//(maxamplitude>) ||   
					  
					//this.cull; //remove any silent or 'unpleasing' soundfiles   
					  
					//look for distortions  
					//if(newfile.checkfordistortion(filenamenow)) {testok= false;};   
					  
				},{testok= false;});   
				  
			};   
			  
			if(sanitycheck>20) {  
				  
				//accept previous iteration if you can  
				if(iterations>0) {  
					  
					filenamenow = tempdir++"/temp"++(tempfilecounter.asString)++"it"++((iterations-1).asString)++".wav";   
					  
					newfile= AutocousmaticSoundFile(filenamenow);   
					  
					if(newfile.uniqueidentifier.notNil,{  
						  
						("started analysis of " ++filenamenow).postln;  
						  
						newfile.analyse(true);   
						  
						("finished analysis of " ++filenamenow).postln;  
						  
						testok = true;   
						  
						iterations = iterationtarget;   
						"Dropped back to previous iteration".postln; 				  
					});  
					  
					} {  
					  
					"Failed twenty times in a row! Accepting existing file".postln;   
					//could get zero useful duration, hopefully OK now! 				  
				};  
				  
			};  
			  
			//if failed tests, will have to make a new processed file  
			if(testok || (sanitycheck>20)) {  
				  
				//numberdone= numberdone+1;   
				  
				if(testok==true) {  
					  
					iterations = iterations+1;   
					  
					  
					//going to continue around if iteration minimum  
					  
					if(iterations>=iterationtarget) {  
						  
						tempfilecounter= tempfilecounter+1;    
						  
						iterationtarget = chooseiterations.();   
						iterations =0;   
						  
						count= count+1;   
						afiles.add(newfile); //save object here, no point loading and analyzing twice over!   
						  
						lengthdone= lengthdone+ (newfile.usefulduration);   
						  
						sanitycheck=0;  
						  
						nextsoundfile = soundfilegroup.wrapAt(count);  
						} {  
						  
						nextsoundfile = newfile; //ready to go round again!   
					};  
					  
				}  
				{  
					  
					//give up! 	  
					lengthdone= lengthdone+ 5.0;   
					  
					nextsoundfile = soundfilegroup.wrapAt(count);	  
				}  
				  
			} {sanitycheck= sanitycheck+1; };   
			  
		});   
		  
		^afiles;  
	} 	  
	  
	  
}  
 

//Autocousmatic by Nick Collins (c)2009-2011, released under GNU GPL 3 August 2011
 
 
Autocousmatic { 
	var <>sourcedir, <>tempdir, <>renderdir, <>analysisdir; 
	var <inputfiles; 
	var <>numChannels;
	var <>duration;
	var <>inputsoundfiles; 
	//var <>tempsoundfiles; //all those usable in final mix  
	var <>newsoundfiles; //new filenames those just created 
	var <outputsoundfiles;
	
	//for topdown creation 
	var <>sections;  
	var <>tempfilecounter; //to keep unique temp file numbers  
	
	//set from GUI for next run
	var nextduration, nextnumChannels,nextNumMixes;  
	var statustext;  
	var running; 
	
	 
	*new {|numChannels=2, duration=60.0| 
		 
		^super.new.initAutocousmatic(numChannels, duration);  
	} 
	
	 
	initAutocousmatic {|numchan, dur| 
		 
		//array of soundfiles to operate on 
		//filenames = paths;  
		 
		numChannels= numchan; //num output channels
		 
		duration= dur;  
		
		//make sure directories exist for temporary files 
		"mkdir /tmp/autocousmaticanalysis".systemCmd;  
		
		"mkdir /tmp/autocousmatictemp".systemCmd;
		 
		sourcedir = ""; //"/data/audio/autocousmatic/source";  
		tempdir= "/tmp/autocousmatictemp"; //data/audio/autocousmatic/temp";  
		renderdir= ""; //"/data/audio/autocousmatic/render";  
		analysisdir= "/tmp/autocousmaticanalysis"; //"/data/audio/autocousmatic/analysis/";  
		
		AutocousmaticSoundFile.autocousmatic= this; 
		
		//maximum of 1000, no problem in practice; can also change ServerOptions
		
		//"sounds/*.wav".pathMatch far superior to getPathsInDirectory
//		inputfiles = (sourcedir++"/*").pathMatch; //Cocoa.getPathsInDirectory(sourcedir); 
//		
//		inputfiles.postln;
//		inputsoundfiles= inputfiles.collect{|filename| AutocousmaticSoundFile(filename)}; 
//		
//		//if problems loading any, remove those 
//		inputsoundfiles= inputsoundfiles.select{|val| val.uniqueidentifier.notNil}; 
//		
//		tempfilecounter= 0; 
//		
		nextduration = dur; 
		nextnumChannels = numchan; 
		running = false; 
		nextNumMixes = 1; 
		
		this.gui; 
		
	} 
	 
	 
	clean {
		
		("rm"+tempdir++"/*").unixCmd;
		("rm"+analysisdir++"/*").unixCmd;
		
	} 
	 
	
	//timescales, microsound, gestures, layers and sequences, abrupt and continuous transformation... 
	compose {|maxiterations=1| 
	
	
		//iterate; will look to minimise error from perfection, or else take best version so far within deadline of number of iterations 
		//keep track of diskspace; must delete intermediaries if running out of space, and settle early on version if get stuck. 
		
	
		//material analysis and generation of new combinations; bottom-up
		//psuedo listening assessment as you go; analyse with respect to loudness structure, sensory dissonance, et al. Even Schaeffarian qualities of grain et al, really timbral descriptors 
		//cluster analysis for similarities and differences to exploit? 
		
		
		//top-down; placement in wider structure
		//self assessment by running longer term segments of piece for dramatic connotations; expectancy analysis?
		//will require return to low level material tweaking 			
		
		
		//starting simple; assess input database, generate processed versions, select favourites, place into larger scale top-down determined structure with 
		maxiterations.do {
		
		
		
		}
		
		 
	}  
	
	
	
	//also set number of iterations of processing? 
	//initial tests and basic piece generation 
	compose1 {|number= 200, fromgeneration=0, reanalyse=true, iterations=1|
		var tempfiles, tempfilenames; 
		var totaltempcreated=number; 
		var newgeneration, derivations; //will return [tempfilenames, derivationinfo]
		//final mix can take account of derivation chain, matching similar processes from similar soundfiles together
		
		{
	
		//starting simple; assess input database, generate processed versions, select favourites, place into larger scale top-down determined structure 
		
		if(fromgeneration==0) {
		
		this.analyse(inputsoundfiles); 
		
		//create outputs in tempdir
		//will avoid accidentally picking up stuff from previous runs of the program, program will just overwrite stuff itself
		newgeneration = this.createnewgeneration(inputsoundfiles, number,0); 
		tempfilenames= newgeneration[0]; 
		derivations= newgeneration[1]; 
		
		totaltempcreated= number; 
		
		} {
		
		//must just load up what's there already
		tempfilenames= (tempdir++"/*").pathMatch;
		//derivations =  //but what about this? 
		
		};
		
		
		newsoundfiles = List[]; 
		
		iterations.do{|whichiteration|
		
		//if(whichiteration>=fromgeneration) {
		
		//};
		
		if(whichiteration>0) {
		//can iterate here; take any temp and further process back into temp, 
		//reduces number each generation to avoid over processing
		newgeneration= this.createnewgeneration(tempfiles, number.div(whichiteration+1), newsoundfiles.size); //offsets them 
		tempfilenames= newgeneration[0];
		derivations= newgeneration[1]; 
		}; 
		
		tempfiles= tempfilenames.collect{|filename,i| var newfile= AutocousmaticSoundFile(filename);  newfile.derivation= derivations[i]; newfile}; 
		
		//if problems loading any, remove those 
		tempfiles= tempfiles.select{|val| val.uniqueidentifier.notNil}; 
				
		this.analyse(tempfiles, reanalyse); //false to avoid NRT analysis step if already carried out 
		
		//safety; must have loaded feature data correctly
		tempfiles= tempfiles.select{|val| val.maxamplitude.notNil}; 
		
		//cull any with maxamplitude under -30 dB, or with zero usefulduration
		tempfiles= tempfiles.select{|val| ((val.maxamplitude.ampdb)>(-40)) && (val.usefulduration>0.1) }; 
		//this.cull; //remove any silent or 'unpleasing' soundfiles 
		
		newsoundfiles = newsoundfiles++tempfiles; 
		
		}; 
		
		
		
		//create final mix
		this.mix2; 	
			
		//choice of which soundfile based on proportion of total durations it represents; so can take subsections for processing too 
		//later may further rate by suitability, pliability etc		
	
		}.fork(SystemClock); 
		
	}
	 
	 
	createnewgeneration {|targetsoundfiles, number, offset=0| 
	
		var filenames= List[]; 
		//select anything in input, and process with anything you feel like 
		var derivations= List[]; 
		var derivationbase;
		
		if(offset==0) {derivationbase=0;};  	//if offset 0, getting from original sources
		if(offset>0) {derivationbase=offset-(targetsoundfiles.size);};  //if greater than 0, now at a later generation, must get derivationbase correct to trace
		
		
		number.do {|j|
			var filenamenow; 
			
			filenamenow= tempdir++"/temp"++((offset+j).asString)++".wav"; 
			
			filenames.add(filenamenow); 
			
			//probabilities relative to sizes of files and number of useful access points? 
			//was .choose
			targetsoundfiles.wrapAt(j).process1(filenamenow); 
		
			//applied process 1 on sound file indexed N. If generation 0, applied to sources, else higher generation applies to intermediate files
			//derivation is either to original source list for 1st entry, else for later entries into the master newsoundfiles list
			derivations.add((targetsoundfiles.wrapAt(j).derivation)++[[1, derivationbase+(j%(targetsoundfiles.size))]]); 
		}; 
		
		^[filenames,derivations];
	} 
	 
	 
	//call external program? Or run SC in NRT mode with Logger and feature extraction? 
	analyse {|whichfiles, redoanalysis=true| 
		 
		 //may need routine structure around this to adequately wait
		 whichfiles.do {|filenow|
		 
		 ("started analysis of " ++filenow.filename).postln;
		 
		 filenow.analyse(redoanalysis); 
		 
		 ("finished analysis of " ++filenow.filename).postln;
		 
		 }; 
		 	 
	} 
	 
	//use machine listening model on its own productions 
	selfassess { 
		 
		 
	} 
	 
	 
	 
} 

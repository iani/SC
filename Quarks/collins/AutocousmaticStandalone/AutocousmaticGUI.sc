//Autocousmatic by Nick Collins (c)2009-2011, released under GNU GPL 3 August 2011
 

+ Autocousmatic {


	
	gui {
		
		var w; 
		var chanslid, durbox,mixslid; 
		var sourcedirtext,renderdirtext; 
		
		
		w= Window("Autocousmatic by Nick Collins",Rect(100,200,600,550),false,true); 
		
		w.view.background_(Color.new255(0, 190, 80,80));
		
		
		//nextduration, nextnumChannels;  
		
		chanslid = EZSlider(w, Rect(10,20,300,30),"Num Channels ",ControlSpec(1,8,\linear,1,2), {nextnumChannels =chanslid.value.asInteger;},nil,false,100);
		
		durbox = EZNumber(w, Rect(350,20,200,30),"Duration (s) ",ControlSpec(1.0,6000.0,\linear,0.001,60.0), {nextduration = durbox.value;},nil,false,100);
		
		StaticText(w,Rect(10,80,580,30)).string_("Set directories by dragging them from Finder to the spaces below; the path to the directory should appear").background_(Color(0.1,0.3,0.1,0.1)).stringColor_(Color.black(0.9));
		StaticText(w,Rect(10,120,150,30)).string_("Sound file source directory");
		
		//sourcedirtext = TextView(w,Rect(170,120,340,30)).string_("").receiveDragHandler_({var now = View.currentDrag; "now2".postln; now.postln});  
		
		sourcedirtext = DragSink(w,Rect(170,120,340,30)).string_("drag source directory here").receiveDragHandler_({
			var now = View.currentDrag; 
			
			//[now,now.class,now.isKindOf(Array),now.isKindOf(Collection)].postln;
			
			if(now.isKindOf(Array)) {

				if(now[0].isString) {
					sourcedirtext.object = now[0]; 
					sourcedirtext.string = now[0];
					sourcedir = now[0]; 
				}
				
			}; 
			
			});
			
		//sourcedirtext = TextView(w,Rect(170,120,340,30)).string_("").action_({"now1".postln}).receiveDragHandler_({"now2".postln});  
		
		StaticText(w,Rect(10,160,150,30)).string_("Piece output directory");
		//renderdirtext = TextView(w,Rect(170,160,340,30)).string_(""); 
		
			
		renderdirtext = DragSink(w,Rect(170,160,340,30)).string_("drag output directory here").receiveDragHandler_({
			var now = View.currentDrag; 
				
			if(now.isKindOf(Array)) {

				if(now[0].isString) {
					renderdirtext.object = now[0]; 
					renderdirtext.string = now[0];
					renderdir = now[0]; 
				}
				
			}; 
			
			});
		
		
		
		
		//.action_({renderdirtext.string.postln}).receiveDragHandler_({renderdirtext.string.postln}); 
		
			
		mixslid = EZSlider(w, Rect(10,210,400,30),"Num Mixes ",ControlSpec(1,20,\linear,1,1), {nextNumMixes = mixslid.value},nil,false,100);
		
		//left out for now, since requires SCMIR and exemplar template work
		//Button(w,Rect(370,230,50,30)).states_([[""],["x"]]).action_({|button| button.value;}); 
		//StaticText(w,Rect()).string_("choose best mix automatically"); 
		
		
		
		Button(w,Rect(110,280,100,30)).states_([["questionnaire"]]).action_({|button| AutocousmaticQuestionnaire.openHelpFile;}); 
		Button(w,Rect(365,280,100,30)).states_([["help"]]).action_({|button| Autocousmatic.openHelpFile;}); 
		
		
		statustext = StaticText(w,Rect(50,320,500,150)).string_("STATUS");
		statustext.font = Font("Arial", 15);
		statustext.background = Color.white(0.7); 
	
	
		Button(w,Rect(110,500,100,30)).states_([["RUN",Color.white,Color.blue(0.4)]]).action_({|button| this.go(15,nextNumMixes); }); 
		Button(w,Rect(500,500,80,30)).states_([["CANCEL",Color.white,Color.red]]).action_({|button| this.stop(); });
		
		
		w.front;
		
	}	
	
	
	go {|densityfactor= 15,nummixes=1|
		
		//make sure not already running
		
		if(running==false) {
		
		if((renderdir.size<1) || (sourcedir.size<1)) {
			this.report("Provide valid directory locations for source and output");  ^false;
			};
		
		running = true; 
		
		this.report("Cleaning temp files"); 
		
		this.clean;
		
		this.report("Attempting to load source(s)");  
		
		inputfiles = (sourcedir++"/*").pathMatch; //Cocoa.getPathsInDirectory(sourcedir); 
		
		inputfiles.postln;
		inputsoundfiles= inputfiles.collect{|filename| AutocousmaticSoundFile(filename)}; 
		
		//if problems loading any, remove those 
		inputsoundfiles= inputsoundfiles.select{|val| val.uniqueidentifier.notNil}; 
		
		if(inputsoundfiles.size==0) {
			
			this.report("No valid input sound files. Make sure at least one is in the source directory. No mp3s, wav should be fine.");  
			running=false;
			^false;
		};
		
		tempfilecounter= 0; 

		duration = nextduration;
		numChannels = nextnumChannels; 
		
		
		
		//report("Composing: may take a while!");  
		
		this.topdowncompose(densityfactor,nummixes);	
		
		^true; 
		};
		
		this.report("Autocousmatic already running"); 
		
		^false;
	}
	
	stop {
		
		if(running==true) {
		this.report("Autocousmatic render stopped"); 
		
		CmdPeriod.run; 
		running = false;  
		};
		
	}
	
	
	report {|string|
		
		{statustext.string_(string)}.defer; 
		
	}
	
	
	
}



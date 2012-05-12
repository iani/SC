//This file is part of The BBCut Library. Copyright (C) 2001  Nick M.Collins distributed under the terms of the GNU General Public License full notice in file BBCutLibrary.help

//OnsetDetectorUI- N.M.Collins 6/11/03

//later do hack version where use two colours and differentiate two sounds! 

//store triggersize for each trigger so can order in a set for UI

//register of params, automake GUI? always controllers for looping mode of discovery

OnsetDetectorUI {	
	var w, sigview, loadb, detectb, editb, menu, envview, nametext, postb;
	var svsiz; 
	var filename, sf, data;
	var offsets, bars;
	var detectors,notdetecting;
	
	//preset recall, store
	var spb, lpb;	//save/load preset buttons
	var check, audition;
	
	*new {
	^super.new.initOnsetDetectorUI;
	}
	
	initOnsetDetectorUI {
		var names;
		
		svsiz= 850;
		
		w=  SCWindow("onset detection GUI", Rect(50,500,svsiz+50,200));
		
		sigview= SCMultiSliderView(w, Rect(5,5,svsiz,100));
		sigview.readOnly_(true);
		
		//sigview.action = {arg xb; ("index: " ++ xb.index).postln};
		sigview.drawLines_(true);
		sigview.drawRects_(false);
		sigview.isFilled_(true);
		sigview.selectionSize_(10);
		sigview.index_(10);
		sigview.thumbSize_(1); 
		sigview.gap_(0);
		sigview.colors_(Color.black, Color.blue(1.0,1.0));
		sigview.showIndex_(true);
		
		
		envview = SCEnvelopeView(w, Rect(5, 105, svsiz, 30));
		
		envview.setProperty(\thumbWidth, 3.0);
		envview.setProperty(\thumbHeight, 30.0);
		
		envview.drawLines_(false);
		envview.strokeColor_(Color.red);
		envview.selectionColor_(Color.blue);
		
		//when select new onset positions
		//envview.value_([[0.0,0.0], [0.0,0.0]]);
		//envview.select(-1); //unselect
		
		w.view.keyDownAction_({arg ...args; 
		var last, start, end;
			var key;
			//args.postln;
			
			key=args[3];
			
			last= envview.lastIndex;
			
			if((key<123) && (key>96),{
			if((sf.notNil) ,{
				key=key-97;
				last=key%(offsets.size);
				start= offsets[last];  
				end=if(last==(offsets.size-1),1.0, {offsets[last+1]});
				sf.play(offset: start*(sf.numFrames), len: (end-start)*(sf.length));
				
				sigview.index_(start*svsiz);		
				sigview.selectionSize_((end-start)*svsiz);
				
			});
			});
		
		
		});
		
		envview.keyDownAction_({arg ...args;  
			
			var last, start, end;
			var key;
			//args.postln;
			
			key=args[3];
			
			last= envview.lastIndex;
			
			//delete, no undo 
			if(key==127,{
			if(last!=(-1) && (last<(offsets.size)),{
				offsets.removeAt(last);
				this.updatebars;
				envview.select(-1);
			});
			
			});
			
			//Post <<[envview.index,envview.lastIndex]<<nl;
			
			//play sample area on space bar, assumes sorted list
			if(key==32,{
				
			if((last!=(-1)) && (last<(offsets.size)) && (sf.notNil) ,{
				start= offsets[last];  
				end=if(last==(offsets.size-1),1.0, {offsets[last+1]});
				//Post << [start,end]<<nl;	
				sf.play(offset: start*(sf.numFrames), len: (end-start)*(sf.length));
			});
				
			});
			
			});
		
		//32 is space bar
		sigview.keyDownAction_({arg ...args;  
		
			var os;
			
			os= (sigview.index)/svsiz;
			
			//add, no undo 
			if(args[3]==32,{
				
				offsets=offsets.add(os).sort;
				this.updatebars;
				envview.select(-1);
				
			});
			
		});
		
		//when have moved an offset, update internal data structure
		envview.mouseUpAction_({
			var index, start, end;
			Post <<envview.value << nl;
			
			offsets= (envview.value.at(0)); 
			
			if(offsets[0].notNil,{offsets.sort; 
			//only need to sort on final offsets array, view not affected by CPs out of order
			
			//display selection in sigview
			
			index= envview.lastIndex; 
			
			if(index!=(-1) && (index<(offsets.size)),{
			start= offsets[index];  
			end=if(index==(offsets.size-1),1.0, {offsets[index+1]});
					
			sigview.index_(start*svsiz);		
			sigview.selectionSize_((end-start)*svsiz);
				
			});
			
			});
					
			
			
		});
		
		
		nametext= SCStaticText(w, Rect(5, 155, 140,30)).font_(Font(\HelveticaNeue, 12)).string_(" ");
		
		loadb= SCButton(w, Rect(155, 155, 70,30));
		loadb.states= [["load",Color.blue]];
		
		loadb.action_({this.load;});
		
		//collect list automatically from array of subclasses of OnsetDetection?
		//have to be careful- this would pick up abstract bases as well...
		//names=OnsetDetector.allSubclasses; //how to convert to strings? 
		
		notdetecting=true;
		detectors= [FFTODHainsworthFoote.new, FFTODJensenAndersen.new,AmpAndSlope.new,RMSOD1.new];
		//OnsetDetector.allSubclasses.do({arg val; val.new});
		
		names = detectors.collect({arg val; val.class.asString}); 		
		menu= SCPopUpMenu(w, Rect(235, 155, 190,30));
		menu.items = names;
		
		detectb= SCButton(w, Rect(435, 155, 70,30));
		detectb.states= [["detect",Color.blue]];
		
		detectb.action_({ this.detect; });
		
		editb= SCButton(w, Rect(515, 155, 70,30));
		editb.states= [["edit",Color.blue]];
		
		editb.action_({detectors.at(menu.value).editUI;});
		
		postb= SCButton(w, Rect(595, 155, 70,30));
		postb.states= [["post",Color.blue]];
		
		postb.action_({
		
		//Post <<[(envview.value)[0],offsets]<<nl;
		
		Post << ((((envview.value)[0]).sort)*((sf.numFrames)).round(1.0).asInteger) <<nl;  //? 1.0 
		});
		
		SCStaticText(w, Rect(695, 150, 100,15)).font_(Font(\HelveticaNeue, 10)).string_("presets");
		
		spb= SCButton(w, Rect(685, 165, 25,20));
		spb.states= [["s",Color.blue]];
		
		spb.action_({this.savepreset;});
		
		lpb= SCButton(w, Rect(715, 165, 25,20));
		lpb.states= [["l",Color.blue]];
		
		lpb.action_({this.loadpreset;});
		
		SCStaticText(w, Rect(830, 165, 80,20)).font_(Font(\HelveticaNeue, 10)).string_("audition?");
		
		check= SCButton(w, Rect(800, 165, 20,20));
		
		check.states= [["x",Color.blue],[" ",Color.blue]];
		audition=true;
		
		check.action_({audition= if(check.value==0, true, false); });
		
		bars= Array(0);
		
		w.front;
		
		w.onClose_({detectors.do({arg val; val.close;})});
	}
	
	savepreset {
	var which;
		
		menu.value.postln;
		
		which= detectors.at(menu.value);
		
		which.postln;
		
			//directory search
		CocoaDialog.savePanel({arg path; 
		var file;
		
		path.postln;
		
		file=File(path,"w+");
		
		file.write("["++(menu.value.asString)++","++(which.save)++"]");
		
		file.close;
		
		
		});
		
	
	}
	
	loadpreset {
		
		//directory search
		CocoaDialog.getPaths({arg paths; 
		var data, which, file;
		var temp;
		 
		//kill existing UI state
		temp=detectors.at(menu.value);
		
		temp.close;
		
		file=File(paths[0],"r+");

		data=file.readAllString.interpret;
		
		Post << data[1] <<nl;
		
		which=data[0];
		
		menu.value_(which);
		
		detectors[which].load(data[1]);
		
		file.close;
		
		});
	
	}
	
	
	//what does this do to a stereo file?
	load {
		var file, minval, maxval, f,d, step;
		
		//can't use SF3 because doesn't keep open long enough for reading
		file = SoundFile.new;
		
		//directory search
		CocoaDialog.getPaths({arg paths; filename=paths.at(0);
		
		if(not(file.openRead(filename)),{^nametext.string_("load failed")});
		
		nametext.string_(PathName(filename).fileName);
		
		data= FloatArray.newClear(file.numFrames);
		file.readData(data);
		file.close;
		
		step= ((data.size).div(svsiz));
		
		minval = 0;
		maxval = 0;
		f = Array.new;
		d = Array.new;
		data.do({arg fi, i;
		
			if(fi < minval, {minval = fi});
			if(fi > maxval, {maxval = fi});
		
			if(i%step==0, {
				d = d.add((1 + maxval ) * 0.5 );
				f = f.add((1 + minval ) * 0.5 );
				
				minval = 0;
				maxval = 0;
			});
		});
		
		sigview.reference_(f); //this is used to draw the upper part of the table
		sigview.value_(d);
		
		sf=SF3(filename);
		
		});
	
	}
	
	detect {	
		var which;
		
		which= detectors.at(menu.value);
		
		if((sf.notNil) and: (notdetecting), {
		
			notdetecting=false;
			which.runOnFile(sf, audition);
			
			SystemClock.sched(sf.length+1.0, {
			offsets= which.onsets;
			
			offsets.postln;
			{this.updatebars;}.defer;
			
			notdetecting=true;
		});
		
		});
	}
	
	
	updatebars {
		//var offint;
		
		//"here".postln;
		
		//offsets in envview now
		
		envview.value_([offsets,Array.fill(offsets.size,0.0)]);
		
		/*
		//offsets= Array.rand(10, 0.0,1.0);
		offint= (offsets*svsiz).asInteger;
		
		bars.do({arg val; val.remove;});
		
		bars= Array.fill(offint.size, {arg i;
		SCStaticText(w, Rect(5+(offint.at(i)),5, 2, 140)).background_(Color.red);
		});
		
		bars.do({arg val; val.string_(" ")});
		
		*/
		
		w.refresh;
	
	}
	
	
}
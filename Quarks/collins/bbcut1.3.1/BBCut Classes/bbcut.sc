//This file is part of The BBCut Library. Copyright (C) 2001  Nick M.Collins distributed under the terms of the GNU General Public License full notice in file BBCutLibrary.help

//BBCut, 16/10/01 by N.M.Collins 
//Revision for SC3 26 July 2003

BBCut {
	//classvar <>currentserver;	//could set this in Main to be Server.local 
	var <bbcutproc, <bbcgarray; 
	var <>tempoclock; //tempo=2; //just ask tempoclock
	var alive=1, <>paused=0; 
	
	/*
	*initClass {
	currentserver= Server.local; //Server.default
	}
	
	*server {
	^currentserver; //(currentserver ? (Server.local))
	}
	*/
	
	*currentserver {
	^Server.default;
	}
	
	*server {
	^Server.default;
	}
	
	//*ar method would combine *new and ar instance method, but then lose handle on running BBCut instance
	
	*new {
	arg bbcgarray, bbcutproc, tempoclock;
	
	^super.new.initBBCut(bbcgarray, bbcutproc, tempoclock);
	}
	
	initBBCut {
	arg bbcga, bbcp, tc;
	
	//should work with any input of form BBCS, [BBCS, BBCS], [[BBCS, BBCS], [BBCS]]  , BBCG, [BBCG, BBCG] 
	if(bbcga.isKindOf(Array),
	{
	
	//if array of CutSynths not of BBCGs, act appropriately
	//if first element is an array then treat as array of cutsynth arrays
	//else if first element is CutSynth treat as singular array of cutsynths
	//otherwise  bbcgarray= bbcga; 
	
	bbcgarray= if(bbcga.at(0).isKindOf(Array),{
	Array.fill(bbcga.size,{arg i; BBCG(bbcga.at(i))});
	},
	{
	if(bbcga.at(0).isKindOf(BBCutSynth), {[BBCG(bbcga)]}, {bbcga});
	});
	},
	{
	//add BBCG automatically if passed in some BBCutSynth derived thing 
	bbcga= bbcga ?? {BBCG(BBCutSynth.new)};
	
	bbcgarray= [
	//if(this.testIfDerivedFrom(bbcga, BBCutSynth), {})
	if(bbcga.isKindOf(BBCutSynth)
	,{
	BBCG(bbcga)
	},
	{bbcga})];
	
	});
	
	bbcutproc= bbcp ?? {BBCutProc11.new};
	
	//this will be replaced by update calls through this main router class, passed to all in array
	//using this as bbcutsynth for now, should rename the variable and method later on
	bbcutproc.attachsynth(this);
	
	bbcgarray.do({arg val; val.attach(this); });
	bbcgarray.do({arg val; val.setup; });
	
	tempoclock= tc ? TempoClock.new(2); //TempoClock.default;//TempoClock.new(2);	//defaults to 2bps, just use tempo_ method to set this
	
	}
	
	
	//instead of internal tempo message
	tempo {
	^tempoclock.tempo;
	}
	
	//needs to set tempoclock tempo too as well as state var
	//if using blockclock AND tempoclock must set both to keep them locked
	tempo_ {
	arg t;
	//tempo=t;
	tempoclock.tempo_(t);
	}
	
	//hangover from the old way in SC2 but confusing for SC3- best to avoid
	//kept here for backwards compatability
	ar { arg quant=0.0;
	this.go(quant);
	}
	
	play { arg quant=0.0;
	this.go(quant);
	}
	
	go { arg quant=0.0;
	var blocks;
	
	//created already to make tempo_ setter always work
	//tempoclock= TempoClock.new(tempo ? 2);
	
	blocks=0; 
	
	//Post << [tempoclock.elapsedBeats] <<nl;
	
	//may need to set up own Thread with own measure of beats rather than Main.elapsedtime
	//start immediately, assume client code starts this at the right time
	tempoclock.schedAbs(tempoclock.elapsedBeats.roundUp(quant), { arg beat, sec; 
	var nextBeat, cuts;
	var blockclock, ec; 
	
	bbcutproc.chooseblock;
	
	//reschedule in beats
	nextBeat=bbcutproc.blocklength; 
	
	//Post << blocks << "  blocklength    " << nextBeat << nl <<"cuts   " <<bbcutproc.cuts<< nl;
	
	cuts= bbcutproc.cuts;
	
	//allows code reuse, but note, this will overwrite the cuts array source in the procedure
	//cuts.copy or cuts.deepCopy not enough to avoid this! Need explicit 
	//Array.fill(cuts.size,{arg i; cuts.at(i).copy;})
	cuts= this.convertCuts(cuts);
	
	//should automatically play array once and end with a nil
	cuts= Pseq(cuts, 1).asStream;
	
	//blockclock= TempoClock.new(tempoclock.tempo);
	ec=0;
	
	//can't you just call tempoclock.sched here again? Don't need to make blockclock each time
	//nor keep tempi updated? surely tempoclock will stop if get a nil? 
	
	//SEEMS TO WORK- BUT KEEP AN EYE ON THIS! 
	
	//blockclock usually
	//used to be tempoclock.schedAbs(tempoclock.elapsedBeats,{ })
	//sched OK here inside the outer schedAbs
	tempoclock.sched(0.0,{
	arg beat, sec; 
	var ioi;
	
	ioi= cuts.next;
	//Post <<"ioi "<<ioi <<nl;
	
	//wasteful check, paused flag will mean that this is no longer spawning anything
	//if(alive<0.5, {ioi=nil;	
	//effectively stops this loop now, though there may be previously scheduled blocks still playing
	//});
	
	if(ioi.notNil,
	{
	
	//Post <<"here  " << dur<< " " << ec <<nl; 
	
	//must call through bbcgarray in reverse order? Not so important?  
	//passes rather than [dur,ec,1.0] of BBCut1.0  [ec, array of cut info]
	
	//could pass in ec/cuts.size, nextBeat relative position
	//must send a copy of the cutinfo else may be corrupted by users of that info
	//when have a list of BBCGs (see offset use in synthesisecut in BBCutSynthSF, actually 
	//updates cutinfo object!) 
	
	if(paused<0.5,
	{
	bbcgarray.do({arg val; val.synthesisecut(ec,ioi.copy);});
	});
	
	ioi=ioi.at(0);
	}//,{Post << "end "<<blocks <<nl;}
	); 
	
	ec=ec+1;
	
	ioi
	});
	
	blocks=blocks+1; 
	
	
	if(alive<0.5, {
	nextBeat=nil;
	//effectively stops this loop now, though there may be previously scheduled blocks still playing
	});
	
	nextBeat;
	});
	
	}
	
	abortblock {
	//problem with this model- no overlapping blocks, blockclock must survive
	}
	
	kill {
	//this.abortblock; 
	//tempoclock.stop;
	paused=1;
	alive=0;
	bbcgarray.do({arg val; val.free;});
	}
	
	//so simulates a PauseStream
	stop {
	this.kill;
	}
	
	//different types of pause, pausesynths or pauseclock
	//pause tempoclock or run(false) all BBCutSynths? //bbcgarray.pause;
	//pause flag for synthesis set so doesn't spawn any more? 
	//pause tempoclock version herein  
	pause {
	arg pau=1;
	
	//Post <<["paused?",pau]<<nl;
	
	paused=pau;
	}
	
	//for compatability with normal way of doing things 
	run {
	arg bool=true;
	
	this.pause(if(bool,0,1));
	}
	
	//update beats to seconds, ioi would indicate beats between events anyway if needed
	convertCuts {
	arg cuts;
	
	//test if already in format [[ioi, dur, offset, amp, ...other synth params]]
	//can interpret all things like panpos, amp, offset in terms of 0.0-1.0 range, remapping
	//decided by cutsynth
	if(not(cuts.at(0).isKindOf(Array)),{
	
	cuts= this.backcompCuts(cuts);}); 
	 
	//but now need to convert to durations in seconds working from tempo
	//could be hard coded for speed rather than convertDuration function call
	//avoids troubles for now
	cuts.do({arg val,i; cuts.put(i, val.put(1, this.convertDuration(val.at(1)))) });
	 
	^cuts;
	}
	
	//for backwards compatibility
	backcompCuts {
	arg cuts;
	
	^Array.fill(cuts.size,{arg i; [cuts.at(i),cuts.at(i), nil,1.0]});
	}
	
	////tempo bounds of BBCut is 15 bpm+ in terms of enveloping:
	////avoids BBCut1.0 bug where tempo 0 gives infinite length duration envelopes!
	////you could change the 0.25 bps to whatever you want here-
	////a 1 beat envelope lasts  
	convertDuration {
	arg dur; 
	
	^dur*((tempoclock.tempo.max(0.25)).reciprocal)
	}
	
	convertDurSecToBeats {
	arg dur; 
	
	^dur*((tempoclock.tempo.max(0.25)))
	}
	
	//these are the functions for passing on relevant messages
	
	//only for last in array if want to save calls
	chooseoffset {
	arg ppos,bps,currlength;
	bbcgarray.do({arg val; val.chooseoffset(ppos,bps,currlength);});
	}
	
	//for bbcutprocs that also choose offsets
	setoffset {
	arg prop,phraselength;
	//a proportionate offset
	bbcgarray.do({arg val; val.setoffset(prop,phraselength);});
	}
	
	updatephrase {
	arg phrase, phraselength;
	
	bbcgarray.do({arg val; val.updatephrase(phrase, phraselength);});
	}
	
	
	//I don't like this because I have to convert cuts array twice! 
	//cuts may be the legacy version! Must be on guard- 
	//indirection in this code is to avoid any chance of overwriting cuts source,
	//else convertcuts gets applied twice! cuts.copy not good enough! deepCopy not good enough!
	updateblock {
	arg block,phraseprop,cuts,isroll;
	var recut;
	
	//allows code reuse, mustpass copy because the convertor will write over the 
	//source version otherwise
	recut= this.convertCuts(
	Array.fill(cuts.size,{arg i; cuts.at(i).copy})
	); 
	
	bbcgarray.do({arg val; val.updateblock(block,phraseprop,recut,isroll);});
	}
	

}	



/*  just use isKindOf
testIfDerivedFrom
{
arg testee, what;
var result, class;

what = what ? BBCutSynth;

result=false;

class = testee.class;
while ({ class = class.superclass; (class.notNil) && (result==false) },{ 
		if(class == what, {result=true;}); 
		});
^result
}
*/




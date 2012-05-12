//This file is part of The BBCut Library. Copyright (C) 2001  Nick M.Collins distributed under the terms of the GNU General Public License full notice in file BBCutLibrary.help

//BBCutSynth N.M.Collins 23-11-01 revised for SC3 26/7/03

//will eventually pass in owner bbcut and proc
//for closer message handling

BBCutSynth
{
//synth is for static fx cutsynths
var owner, bbcgroup, <synth;

*initClass
{

//simplify! env based on dursec
SynthDef(
\bbcsbleep,
{
arg out=0, note=60, length= 0.1, amp=0.5, pan=0.0;
var freq; 
freq= note.midicps;

length= length.max(0.02);

Out.ar(out,
Pan2.ar(
//10*Resonz.ar(Blip.ar(freq), freq, Line.kr(1.0, 0.01, length))
Blip.ar(freq)
*EnvGen.ar(Env([0,1,1,0],[0.01, length-0.02, 0.01]),levelScale:amp, doneAction:2)
, pan))
}).writeDefFile;

}

*new
{
^super.new.initBBCutSynth
}

initBBCutSynth
{

}


//attach an owning bbcut instance
attach
{
arg own, bbcgrp;

owner=own; 
bbcgroup=bbcgrp;
}

//override in subclasses
setup
{

}

//for setup? Should really be under that class's init instance method
//makeSynth {

//^[\bbcsbleep, [\note, rrand(20,70)]];
//}

//
////tempo bounds of BBCut is 15 bpm+ in terms of enveloping:
////avoids BBCut1.0 bug where tempo 0 gives infinite length duration envelopes!
////you could change the 0.25 bps to whatever you want here-
////a 1 beat envelope lasts  
convertDuration
{
arg dur; 

^dur*((owner.tempo.max(0.25)).reciprocal)
}

//necessary so can alter BBCutSynthSF playback param 
//not sure how will communciate synthesis parameter information around anymore-
//want to remove the reliance on nested function calls and replace with successive
synthesisecut
{
//arg dur,repeat,pbsmult=1.0;
arg repeat, cutinfo;

//renedring moved to derived class to make this the default behaviour 
//Synth.head(bbcgroup.group, \bleep, [\out, bbcgroup.outbus, \note, 60, \length, cutinfo.at(1)]);

}

updatephrase
{
arg phrase, phraselength;

}

//should have phraseprop = phrasepos/currphraselength param, and isroll param 
//can rewrite isroll as blocklength/cuts.size but for small cutsizes or basic cuts 
//will cause trouble, for must set a particular tolerance for 'tinyness' and num repeats
updateblock
{
arg block,phraseprop,cuts,isroll;
}

chooseoffset
{
arg phrasepos,bpsd,currlength;
}

//for bbcutprocs that also choose offsets, proportion of phrasedone
//can also pass phraselength from a cutproc if desired to pay attention to that?
setoffset
{
arg prop,phraselength;
//a proportionate offset
}

getOffset
{		
^nil; //so if crashes, you're here! 	

//returns nil to indicate no decision on offset taken
}

//default sourceindex, sourcelength
getSource
{
^nil; //[0,8.0];
}

frameOffset
{
arg phraseprop, currphraselength, fps; 
var a;

a=this.getOffset;	//returns nil for no offset decided
				//or otherwise an offset position in seconds into the source
a= if(a.notNil, {a},{phraseprop*currphraselength})*fps;

^a;
}

}


BBCSTest : BBCutSynth
{

synthesisecut
{
//arg dur,repeat,pbsmult=1.0;
arg repeat, cutinfo;

Synth.head(bbcgroup.group, \bbcsbleep, [\out, bbcgroup.outbus, \note, 60, \length, cutinfo.at(1)]);

}

}



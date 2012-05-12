//This file is part of The BBCut Library. Copyright (C) 2001  Nick M.Collins distributed under the terms of the GNU General Public License full notice in file BBCutLibrary.help

//BBCutSynthSF N.M.Collins 22/11/01 revised for SC3 27/7/03

//indexfunc is dealt with in SF3Selector, or is ignored by SF3 object alone

//BBCutSynth has a bbcut object pointer to look up tempo and other stuff
//for continuous tempo control need a tempo control bus mapped to rate argument of BBCutSynthSF Synths

//also add enveloping parameters? Need to investigate passing envelopes in SC3
//see EnvGen help file

//for more complex synths, need to make a group for every sampled beat? Like did original spawning? 
//no, can let itself spawn for dursec+ a bit each time:  Synth(\length, dursec+1.0)

//IMPORTANT synths no longer need to know anything about beats, only about dursecs-
//so convertDuration should be in the BBCut class itself
//don't pass anything like playbackrate multiplier through synthesise cut calls 

//keep offset in proportional terms until the last minute (when actually synthesise the cut)
//but then offsetshiftfunc is in samples? No, should be in beats

BBCutSynthSF : BBCSParam
{
var sf,offset,randomoffset;

*initClass
{

//needs BufRateScale to cope with Sample Rates other than 44100

//[atkprop, 1.0-atkprop-relprop, relprop]*length and can have curve parameter too 

//will be parameters for pan only if mono
//need \monobbcssf using Pan and \stereobbcsf without
//(1.0-atkprop-relprop).max(0.0) for middle linen arg?  //used to be 0.001 atkprop
SynthDef("monobbcssf",{ arg out=0,bufnum,length,rate=1,offset=0,amp=0.5, pan=0.0, atkprop=0.005, relprop=0.005, curve=0;

Out.ar( out,
Pan2.ar(
amp*PlayBuf.ar(1,bufnum,(BufRateScale.kr(bufnum))*rate, 1.0, offset, 1.0)*EnvGen.ar(Env.linen(atkprop*length,(1.0-atkprop-relprop)*length,relprop*length, 1.0, curve),doneAction:2)
,pan)
)
}).writeDefFile;

//stereo only for now- more than two would require other fixed SynthDefs! 
//could generate programatically or on the fly (more dangerous,expensive, latency)
SynthDef("stereobbcssf",{ arg out=0,bufnum,length,rate=1,offset=0,amp=0.5, atkprop=0.005, relprop=0.005, curve=0;

Out.ar( out,
amp*PlayBuf.ar(2,bufnum,(BufRateScale.kr(bufnum))*rate, 1.0, offset, 1.0)*EnvGen.ar(Env.linen(atkprop*length,(1.0-atkprop-relprop)*length,relprop*length, 1.0, curve),doneAction:2)
)
}).writeDefFile;

}


*new
{
arg sf,randomoffset, ampfunc, panfunc, pbsfunc, dutycycle, atkprop, relprop, curve, offsetshiftfunc;

^super.new(ampfunc, panfunc, pbsfunc, dutycycle, atkprop, relprop, curve, offsetshiftfunc).initBBCutSynthSF(sf,randomoffset);
}

initBBCutSynthSF
{
arg soundfiles3,randoff;

sf=soundfiles3;
randomoffset=randoff ? 0.0;

//proper initialisation comes from updateblock call
offset=0;
}


//the pbsmult might disappear from here! 
//should also pass in totalrepeats, repeatprop and durprop of block
synthesisecut
{
//arg dur,repeat,pbsmult=1.0;
arg repeat, cutinfo;	//cutinfo is of form [ioi,cutdursec,offset,amp...others]
var params, pbs;
var offsetsamp;

//could do this in bbcutgroup, setoffset call if cutinfo.at(2).notNil
//cut routine decided offset takes over from cutsynth decision
if(cutinfo.at(2).notNil, {this.setoffset(cutinfo.at(2))}, {cutinfo.put(2,offset)});

params= super.calculateparams(repeat, cutinfo, sf.beats, sf.length);

//this is only point at which tempo is used- is there a better solution? 
pbs= ((sf.secperbeat)*(owner.tempo))*(params.at(4));

//proportional offset converted to samples only at this last stage when index is totally fixed
offsetsamp= params.at(1); //(offset + (params.at(1)))%1.0;

//final offset result- problem is, multiple bbcutsynthsf's in same chain will affect each other
//I can live with that!  
cutinfo.put(2,offsetsamp);

//finally as sample frames
offsetsamp= offsetsamp *(sf.numFrames);	//modulo to keep in range! 

//may convert to Synth.grain to avoid Node ID allocation problems in long runs and 
//to maximise speed, problem is that may not be able to kill if long


//this code should be in SF3 then can derive from SF3 with SFData etc to do text output rendering wihtout writing new class?
//similarly the offset choice code should be in there too!
if(sf.numChannels==1,
{
Synth.head(bbcgroup.group, \monobbcssf, [\out, bbcgroup.outbus, \bufnum, sf.buffer.bufnum, \length, params.at(0), \rate, pbs, \offset, offsetsamp, \amp, params.at(2), \pan,params.at(3), \atkprop, params.at(5), \relprop,params.at(6), \curve, params.at(7)]);
},
{

//only works for stereo because of restrictions on SynthDef- \numchan,sf.buffer.numChannels
Synth.head(bbcgroup.group, \stereobbcssf, [\out, bbcgroup.outbus, \bufnum, sf.buffer.bufnum, \length, params.at(0), \rate, pbs, \offset, offsetsamp, \amp, params.at(2), \atkprop, params.at(5), \relprop,params.at(6), \curve, params.at(7)]);
});

}


//might want to update sf after x phrases- but getting too general to cope with
updateblock
{
arg block,phraseprop,cuts,isroll;

//playbackspeed= (sf.secperbeat.at(index))*tempokr;
//should always respond to this unless single SF3 object
//if(sf.respondsTo(\updateblock), {sf.updateblock(block,phraseprop,cuts,isroll);});
sf.tryPerform(\updateblock, block,phraseprop,cuts,isroll);

super.updateblock(block,phraseprop,cuts,isroll);

}

//pass message on to relevant auxilliary classes
updatephrase
{
arg phrase, phraselength;

//if(sf.respondsTo(\updatephrase), {sf.updatephrase(phrase, phraselength);});
sf.tryPerform(\updatephrase, phrase, phraselength);

super.updateblock(phrase, phraselength);

}


//rewritten for SF3 class
chooseoffset
{
arg phrasepos=0.0,grain=0.5,currlength;	//default cut in eighth notes	//grain- ie for 11, sdivbeats/subdiv, = beats per subdiv

var posbeats;

//sf.tryPerform(\newindex); //if(sf.respondsTo(\newindex), {sf.newindex});

//this calculation depends on the number of beats in the source sample
posbeats= if(randomoffset.value.coin,
{
//possible num of cut positions within sample- get subdivs per beat and times by beats available
grain*(((1.0/grain)*(sf.beats)).asInteger.rand)
}
,
{		

//Post << "choose offset   " << [phrasepos, sf.beats, phrasepos%(sf.beats),  (phrasepos%(sf.beats))/(sf.beats)];
//work out blocks sample params
//where are we up to in the sample?
phrasepos %  (sf.beats)
//could make slight corrections at this point if had reliable onset data, except second 
//hit would be out relative to mean
}
);		
//take a proportion of the sound length, not in samples until synthesise cut stage
offset= (posbeats/(sf.beats)); //*(sf.numFrames);
}


//for bbcutprocs that also choose offsets
setoffset
{
arg prop;
//a proportionate offset- should also pass currphraselength to work out beats position, but paradigm
//is that cutproc does not know precisely what it is cutting 

//sf.tryPerform(\newindex);

offset= prop; //*(sf.numFrames);
}

getOffset
{
^offset; //(offset/(sf.numFrames)); //(offset/(sf.sound.at(index).sampleRate));
}

getSource
{
^[sf.soundFilePath,sf.length];
}


}




BBCSOffsetFunc : BBCutSynthSF
{

chooseoffset
{
arg phrasepos=0.0,grain=0.5,currlength;	

offset= ((randomoffset.value(sf.onsets))/(sf.numFrames))*(sf.beats);
}

setoffset
{
arg prop;

offset= (sf.getNearest(prop*(sf.numFrames)))/(sf.numFrames);
}

}




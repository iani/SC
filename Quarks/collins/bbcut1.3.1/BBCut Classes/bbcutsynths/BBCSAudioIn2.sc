//This file is part of The BBCut Library. Copyright (C) 2001  Nick M.Collins distributed under the terms of the GNU General Public License full notice in file BBCutLibrary.help
//bbcutsynth AudioIn2 N.M.Collins 27/10/02

//Audio In stored in a long buffer, with access into the past

//if  buffer length too short for beatsintopast, modulo math will avoid crash- just get silly answers
//so you must make certain that for the tempo range and beatsintopast you want, there is enough buffer length allocated 

//to keep track of write buffer, must check thisThread.seconds

//have an effective area of drain based on current tempo

//assumes 44100 sample rate as standard

BBCSAudioIn2 : BBCSAudioIn
{
var randomoffset;
var beatsintopast,mode;
var writeposition,readposition, now,then,phrasestart, starttime;
var bpsound, accessiblesamp;

*new
{
arg channel=1,beatsintopast=4.0,randomoffset=0.0,mode=0,buflength=4,ampfunc, panfunc, pbsfunc, dutycycle, atkprop, relprop, curve, offsetshiftfunc;

^super.new(channel,ampfunc, panfunc, pbsfunc, dutycycle, atkprop, relprop, curve, offsetshiftfunc, buflength)
.initBBCSAudioIn2(beatsintopast, randomoffset, mode);
}

*newOnBus
{
arg bus=0,beatsintopast=4.0,randomoffset=0.0,mode=0,buflength=4,ampfunc, panfunc, pbsfunc, dutycycle, atkprop, relprop, curve, offsetshiftfunc;

^super.newOnBus(bus,ampfunc, panfunc, pbsfunc, dutycycle, atkprop, relprop, curve, offsetshiftfunc, buflength)
.initBBCSAudioIn2(beatsintopast, randomoffset, mode);
}

//initBBCSAudioIn sets offset to zero (see superclass)
initBBCSAudioIn2
{
arg bip=4.0,randoff=0.0,m=0;
var temp;

beatsintopast=bip;

mode=m;

randomoffset=randoff;

phrasestart=0;
}


//global in bbcut lib for current Server- BBCut::server classvar, BBCut.server class method
setup
{
//make Buffer for RecordBuf
buffer= Buffer.alloc(BBCut.server, bufsamp, channel.size.max(1));
 
//done here so that channel array can be passed in trouble free 
synth= SynthDef("bbcsaudioin",{ //arg bufnum, running=0.0, trigger=0.0; 
//doesn't need to Out, just run! loops constantly, always ready to be accessed by the readpointer
RecordBuf.ar(AudioIn.ar(channel), buffer.bufnum, 0, 1.0, 0.0, 1.0, 1.0, 1.0);
}).play(bbcgroup.group,nil,\addToHead);  //([\bufnum, buffer.bufnum, \running, 1.0, \trigger, 0.0], \addToHead);
 
//save time now then can always calculate where the buffer has got to
starttime= thisThread.seconds;  
  
}

synthesisecut
{
arg repeat, cutinfo;
var md,temp;
var params, dursec, offsetsamp;

if(repeat==0,	//if first repeat of a block
{

//take cut routine determined offset if exists
offset= (cutinfo.at(2)) ? offset;

bpsound= beatsintopast.value; 
accessiblesamp= (this.convertDuration(bpsound))*(44100);

//should this be within allowed area? 
offset= offset*accessiblesamp;

now=thisThread.seconds - starttime;

writeposition= (now%buflength)*(44100).round(1.0).asInteger;

md=mode.value;
//offset in samples
readposition= if(md==0, {writeposition-offset}, {

temp=now-then;

if((md==1) || (temp>buflength), {phrasestart+ offset}, {

//mode2 if safe, offset- o/w take start of phrase as repeat, so never offset into the future past the write pointer 
//as that would give us audio from old phrases 
if((phrasestart+offset)>(phrasestart+(temp*(44100))), {phrasestart}, {phrasestart+offset});

});
});
 
readposition= readposition-(BBCut.server.options.blockSize);  //for safety- never exactly on write position block

readposition= readposition % bufsamp;

//final offset is within the sample for rendering
offset= readposition/bufsamp;

//readposition.postln;
//"here!".postln;
});

//AT THIS POINT THE OFFSET IS WITHIN THE WHOLE BUFFER, not within the 'accessible area'
//the offset will have been sorted out during the first repeat, there is no chance of recalculation
cutinfo.put(2,offset);	

//beats in buffer depends on tempo, had separate bufbeats parameter before
params= this.calculateparams(repeat, cutinfo, buflength*(owner.tempo), buflength);

dursec= params.at(0);

//proportional offset converted to samples only at this last stage when index is totally fixed
//buffer length is 4 seconds, take as 8 beats long
offsetsamp= params.at(1); //(0 + (params.at(1)))%1.0;
cutinfo.put(2,offsetsamp);	//set offset always to 0
offsetsamp= ((offsetsamp) *(bufsamp)).round(1.0).asInteger; //modulo to keep in range! 

if(channel.size<2,
{
Synth.head(bbcgroup.group, \monobbcssf, [\out, bbcgroup.outbus, \bufnum, buffer.bufnum, \length, dursec, \rate, params.at(4), \offset, offsetsamp, \amp, params.at(2), \pan,params.at(3), \atkprop, params.at(5), \relprop,params.at(6), \curve, params.at(7)]);
},
{
//only works for stereo at the moment b/c of restrictions on SynthDef
Synth.head(bbcgroup.group, \stereobbcssf, [\out, bbcgroup.outbus,\bufnum, buffer.bufnum, \length, dursec, \rate, params.at(4), \offset, offsetsamp, \amp, params.at(2), \atkprop, params.at(5), \relprop,params.at(6), \curve, params.at(7)]);
});


}



updatephrase
{

arg phrase, phraselength;

super.updatephrase(phrase, phraselength);

then= thisThread.seconds - starttime;
//what is the write buffer position now at the beginning of this new phrase? 
phrasestart=((then%buflength)*(44100)).round(1.0).asInteger;

}


chooseoffset
{
arg phrasepos=0.0,grain=0.5,currlength;	//default cut in eighth notes	//grain- ie for 11, sdivbeats/subdiv, = beats per subdiv
var posbeats;

bpsound= beatsintopast.value; 

posbeats= if(randomoffset.value.coin,
{
//possible num of cut positions within sample- get subdivs per beat and times by beats available
grain*(((1.0/grain)*(bpsound)).asInteger.rand)
}
,
{0}//return zero for no offsetting into future/past- just present moment!
);		

//take a proportion of the available sample space 
offset= (posbeats/bpsound); 
}


setoffset
{
arg prop;

offset= prop; 
}


getOffset
{
^offset;
}


}

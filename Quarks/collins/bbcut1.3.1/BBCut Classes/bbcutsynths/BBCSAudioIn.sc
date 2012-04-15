//This file is part of The BBCut Library. Copyright (C) 2001  Nick M.Collins distributed under the terms of the GNU General Public License full notice in file BBCutLibrary.help

//BBCSAudioIn N.M.Collins 6/8/03

//make the store buffer in this class, RecordBuf

//RecordBuffer always runs silently, retriggered for a new cut. 

//working, what's the most reliable reset trigger message? using scheduling at the moment

//reliance on AudioIn but what if you need InFeedback? 

BBCSAudioIn : BBCSParam
{
var channel, buffer, offset; //numchan;
var buflength, bufsamp; //bufbeats;

/*
*initClass
{

//could use \monobbcssf using Pan and \stereobbcsf for playbuf part of rendering

//channels array expansion done in AudioIn class ready for In, 
//In.ar(local.server.options.numOutputBusChannels+channel) is basically the same as AudioIn
//in can't have a modulatable numChannels, AudioIn copes with that
SynthDef("bbcsaudioin",{ arg channel=1, bufnum, preLevel=0.0, running=0.0, trigger=0.0; 
RecordBuf.ar(AudioIn.ar(channel), bufnum, 0, 1.0, preLevel, running, 0.0, trigger);
}).writeDefFile;

}
*/

*new
{
arg channel=1,ampfunc, panfunc, pbsfunc, dutycycle, atkprop, relprop, curve, offsetshiftfunc, buflength=4; //, bufbeats=8;

^super.new(ampfunc, panfunc, pbsfunc, dutycycle, atkprop, relprop, curve, offsetshiftfunc).initBBCSAudioIn(channel, buflength);
}

*newOnBus
{
arg bus=0,ampfunc, panfunc, pbsfunc, dutycycle, atkprop, relprop, curve, offsetshiftfunc, buflength=4; //, bufbeats=8;

^super.new(ampfunc, panfunc, pbsfunc, dutycycle, atkprop, relprop, curve, offsetshiftfunc).initBBCSAudioIn(bus-(BBCut.server.options.numOutputBusChannels-1), buflength);
}

initBBCSAudioIn
{
arg c, bl, bb;

channel= c ? 0;
//numchan=channel.size;

buflength=bl ? 4;  
bufsamp= bl*44100; 
//bufbeats= bb ? 8; 

offset=0;
}

//global in bbcut lib for current Server- BBCut::server classvar, BBCut.server class method
setup
{
//make Buffer for RecordBuf, SF3, 4 seconds at 44100 sampleRate
buffer= Buffer.alloc(BBCut.server, bufsamp, channel.size.max(1));
 
//done here so that channel array can be passed in trouble free 
//should be messaging style
 
synth= SynthDef("bbcsaudioin",{ arg bufnum, preLevel=0.0, running=0.0, trigger=0.0; 
//doesn't need to Out, just run! 
//offset:45  samp offset to avoid read and write pointer being on top of each other
//doesn't loop, so no accidental overwrite
var chanOffset,infb;

chanOffset = NumOutputBuses.ir - 1;


//adapted from AudioIn code but turned into Feedback version
infb=if(channel.isArray.not,{
			InFeedback.ar(chanOffset + channel, 1)
		},{
		
		// check to see if channels array is consecutive [n,n+1,n+2...]
		if(channel.every({arg item, i; 
				(i==0) or: {item == (channel.at(i-1)+1)}
			}),{
			InFeedback.ar(chanOffset + channel.first, channel.size)
		},{
			// allow In to multi channel expand
			InFeedback.ar(chanOffset + channel)
		})
		
		});



//AudioIn.ar(channel) convert to InFeedback 
RecordBuf.ar(infb, bufnum, 0, 1.0, preLevel, running, 0.0, trigger);
}).play(bbcgroup.group, [\bufnum, buffer.bufnum, \running, 1.0, \trigger, 0.0], \addToHead);
 
 
//won't record until synthesisecut starts
//synth= Synth.head(bbcgroup.group, \bbcsaudioin, [\channel,channel, \bufnum, buffer.bufnum, \running, 1.0, \trigger, 0.0]);
}

//the pbsmult might disappear from here! 
//should also pass in totalrepeats, repeatprop and durprop of block
synthesisecut
{
//arg dur,repeat,pbsmult=1.0;
arg repeat, cutinfo;	//cutinfo is of form [ioi,cutdursec,offset,amp...others]
var params, dursec, offsetsamp;

//passing of cutinfo to other objects probably already happened
cutinfo.put(2,offset);	//set base offset always to 0

//beats in buffer depends on tempo, had separate bufbeats parameter before
params= super.calculateparams(repeat, cutinfo, buflength*(owner.tempo), buflength);

dursec= params.at(0);

//proportional offset converted to samples only at this last stage when index is totally fixed
//buffer length is 4 seconds, take as 8 beats long
offsetsamp= params.at(1); //(0 + (params.at(1)))%1.0;
cutinfo.put(2,offsetsamp);	//set offset always to 0
offsetsamp= (offsetsamp) *(bufsamp);	//modulo to keep in range! 

if(repeat==0,
{
//start RecordBuf going at offset 0, turn off after dursec
synth.set(\trigger,1.0);	//\running, 1.0 no need to pass this! 
//reset for next trigger
//use dursec, schedule for halfway through this cut
SystemClock.sched(dursec/2, {synth.set(\trigger,0.0); nil}); 
}
//if stopped on repeat>=1 immediately would be the assumption that the first repeat was longest- so 
//always keep recording without loop
//, {}
);

if(channel.size<2,
{
Synth.head(bbcgroup.group, \monobbcssf, [\out, bbcgroup.outbus, \bufnum, buffer.bufnum, \length, dursec, \rate, params.at(4), \offset, offsetsamp, \amp, params.at(2), \pan,params.at(3), \atkprop, params.at(5), \relprop,params.at(6), \curve, params.at(7)]);
},
{
//only works for stereo at the moment b/c of restrictions on SynthDef
Synth.head(bbcgroup.group, \stereobbcssf, [\out, bbcgroup.outbus,\bufnum, buffer.bufnum, \length, dursec, \rate, params.at(4), \offset, offsetsamp, \amp, params.at(2), \atkprop, params.at(5), \relprop,params.at(6), \curve, params.at(7)]);
});

}

/*
//might want to update sf after x phrases- but getting too general to cope with
updateblock
{
arg block,phraseprop,cuts,isroll;

super.updateblock(block,phraseprop,cuts,isroll);
}

//pass message on to relevant auxilliary classes
updatephrase
{
arg phrase, phraselength;

super.updatephrase(phrase, phraselength);
}
*/

getSource
{
^[0, buflength];
}

}
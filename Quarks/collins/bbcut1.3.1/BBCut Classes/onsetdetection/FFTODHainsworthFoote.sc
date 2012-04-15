//This file is part of The BBCut Library. Copyright (C) 2001  Nick M.Collins distributed under the terms of the GNU General Public License full notice in file BBCutLibrary.help

//2 feature FFT onset detector after Steve Hainsworth PhD- implemented N.M.Collins 26/12/03

FFTODHainsworthFoote : OnsetDetector
{
//parameters of the process
//var threshold, props;

*new
{
^super.new.initFFTODHainsworthFoote;
}

initFFTODHainsworthFoote
{
	//threshold= 0.4;
//	props=Array.fill(4,0.25);

uiparams=[
["retrigtime", 0.0,0.25,\linear,0.001,0.05],
["threshold", 0.01,2.0,\exponential,0.01,0.6],
["Hainsworth",0.0,1.0,\lin,0.001,0.75],
["Foote", 0.0,1.0,\lin,0.001,0.25]
];
//++(["Hainsworth","Foote"].collect({arg v; [v, 0.0,1.0,\lin,0.001,0.5]}));

params=uiparams.collect({arg val; val[5];});

}

//pass in the filename to run on or an SF3 object
//also pass in whether to audition ot not
runOnFile
{
arg sf, audition=true;
var s,l;
var node, buf;

s= BBCut.currentserver;

buf=Buffer.alloc(s,2048,1);
		
l=List.new;
	
//if passed in filename	
if(not(sf.isKindOf(SF3)	),{sf= SF3(sf);});
	
// register to receive this message
OSCresponder(s.addr,'/tr',{ arg time,responder,msg;
//could store msg.at(0) as amplitude test

//"received!".postln;
	l.add(time);
}).add;	
		
		
		
node=SynthDef(\fftod1,
{
 var source1, proc, test, trig, diff, trig2, test2, combine;
	
	source1= sf.playForSynthDef; 
	
	if(sf.numChannels>1,{source1=Mix.ar(source1)});
	
	SendTrig.ar(Impulse.ar(0), 1, 1);
	
	proc= PV_HainsworthFoote.ar(FFT(buf.bufnum,source1), params[2],params[3],params[1],params[0]);
	
	//takes decaytime*0.5 to drop by 60dB
	//test= Decay.ar(test-0.001, decaytime*0.5).clip2(1.0);
	
	trig= Trig1.ar(proc-0.3, 0.005);

	SendTrig.ar(trig>0, 0, proc);
	
	if(audition, {Out.ar(0,[trig*SinOsc.ar(440, 0, 0.2),0.5*source1])});
	
	}).play(s);

SystemClock.sched(sf.length+0.5,{
//node.free; frees itself due to playForSynthDef
buf.free;
//l.postln;
//adjust relative to first trigger
l= l-l.at(0);
//remove initial marker
l=l.copyRange(1, l.size-1);

//l.postln;

onsets= l/(sf.length);

//onsets.postln;
});

}

//close UI
free
{

}

}
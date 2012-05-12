//This file is part of The BBCut Library. Copyright (C) 2001  Nick M.Collins distributed under the terms of the GNU General Public License full notice in file BBCutLibrary.help

//BBCSModulator1 N.M.Collins 22/11/01 revised for SC3 29/7/03

//can add acceleration parameteres for user control, but this is fully automated 
//version

BBCSModulator1 : BBCutSynth
{
var modfreq, moddepth;

//makes SynthDef for filter FX Synth
*initClass
{

SynthDef("bbcsmodulator1",{ arg in=0, out=0, modfreq=20, moddepth=1.0;
var input;

input= In.ar(in, 2);
ReplaceOut.ar(out,
SinOsc.ar(modfreq, 0, moddepth*input, (1.0-moddepth)*input)
)
}).writeDefFile;

}

setup
{
synth= Synth.head(bbcgroup.group, \bbcsmodulator1, [\in, bbcgroup.outbus, \out, bbcgroup.outbus]);
}

synthesisecut
{
arg repeat, cutinfo;	
//every repeat update param

modfreq= modfreq*(rrand(1.1,2));

moddepth=(moddepth*rrand(1.1,1.5)).max(1.0);

//update filter parameters
synth.set(\modfreq,modfreq, \moddepth, moddepth);	

//or synth.setn(\cuttoff, [cutoff,q]) 
//set inputs to values in array starting from cuttoff input	

}

updateblock
{
arg block,phraseprop,cuts,isroll;

modfreq=rrand(1,10);			 
moddepth=rrand(0.01,0.1);
}

}


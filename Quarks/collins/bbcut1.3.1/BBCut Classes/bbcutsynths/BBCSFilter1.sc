//This file is part of The BBCut Library. Copyright (C) 2001  Nick M.Collins distributed under the terms of the GNU General Public License full notice in file BBCutLibrary.help

//BBCSFilter1 N.M.Collins 22/11/01 revised for SC3 29/7/03

BBCSFilter1 : BBCutSynth
{
var q,cutoff,cutdirect;

//makes SynthDef for filter FX Synth
*initClass
{

SynthDef("bbcsfilter1",{ arg in=0, out=0, cutoff=10000, q=0.4;
ReplaceOut.ar( out,
Resonz.ar(In.ar(in, 2), Lag.kr(cutoff, 0.05),Lag.kr(q,0.05))
)
}).writeDefFile;

}

setup
{
synth= Synth.head(bbcgroup.group, \bbcsfilter1, [\in, bbcgroup.outbus, \out, bbcgroup.outbus]);
}

synthesisecut
{
arg repeat, cutinfo;	
//every repeat update filt

if(cutdirect<1,
{	//reduce!
cutoff= (cutoff*(rrand(0.9,0.5))).max(40);
},
{
cutoff= (cutoff*(rrand(1,4))).min(10000);
//if(cutoff>10000,{cutoff=10000});
}
);


//update filter parameters
synth.set(\cutoff,cutoff, \q, q);	

//or synth.setn(\cuttoff, [cutoff,q]) 
//set inputs to values in array starting from cuttoff input	

}

updateblock
{
arg block,phraseprop,cuts,isroll;

q= 0.2+ (0.2.rand);
cutdirect=(-1)**(2.rand);			 
cutoff=if(cutdirect<1,10000,100);
}

}


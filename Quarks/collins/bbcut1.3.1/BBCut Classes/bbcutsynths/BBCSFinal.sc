//This file is part of The BBCut Library. Copyright (C) 2001  Nick M.Collins distributed under the terms of the GNU General Public License full notice in file BBCutLibrary.help

//BBCSFinal N.M.Collins 29/8/03
//forces render to given outbus from stereo bus, with volume control, and fx function
//static, just set up once

BBCSFinal : BBCutSynth
{
var outbus, vol, fxfunc; 

*new
{
arg vol=1.0, outbus=0, fxfunc;

^super.new.initBBCSFinal(vol, outbus, fxfunc);
}

initBBCSFinal
{
arg v=1.0, ob=0, ff;

vol=v;
outbus=ob;
fxfunc=ff;

}

//makes SynthDef for filter FX Synth
*initClass
{

SynthDef("bbcsfinal",{ arg in=0, out=0, vol=1.0; 
ReplaceOut.ar(out,
In.ar(in, 2)*vol
)
}).writeDefFile;

}

setup
{

synth= if(fxfunc.notNil,
{
SynthDef(\bbcsfinalx, {ReplaceOut.ar(outbus, fxfunc.value(In.ar(bbcgroup.outbus, 2))*vol)}).play(bbcgroup.group, nil, \addToHead);
},
{
Synth.head(bbcgroup.group, \bbcsfinal, [\in, bbcgroup.outbus, \out, outbus, \vol, vol]);
});

}


}

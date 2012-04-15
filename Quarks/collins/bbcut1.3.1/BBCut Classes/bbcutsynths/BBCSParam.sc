//This file is part of The BBCut Library. Copyright (C) 2001  Nick M.Collins distributed under the terms of the GNU General Public License full notice in file BBCutLibrary.help

//BBCSParam N.M.Collins 10/8/03

//common features encapsulated in a base class for BBCutSynthSF, BBCSAudioIn
//intermediary class, can't be instantiated on its own

BBCSParam : BBCutSynth
{
var ampfunc, panfunc, pbsfunc, dutycycle, atkprop, relprop, curve, offsetshift;


*new
{
arg ampfunc, panfunc, pbsfunc, dutycycle, atkprop, relprop, curve, offsetshift;

^super.new.initBBCSParam(ampfunc, panfunc, pbsfunc, dutycycle, atkprop, relprop, curve, offsetshift);
}

initBBCSParam
{
arg af, panf, pbsf, dc, atkp, relp, crv, osfunc;

ampfunc= af ? 1.0;
panfunc= panf ? 0.0;
pbsfunc= pbsf ? 1.0;
dutycycle= dc ? 1.0; //{arg len; 1.0}; 
atkprop= atkp ? 0.001;
relprop= relp ? 0.001;
curve= crv ? 0;
offsetshift= osfunc ? 0.0;
}

//calculations for synthesis parameters, returns an array of the final values?
//could be separate function for offsetshiftfunc
//[dursec (after dc application),offset+offsetshift,amp, pan, pbs, atk, rel, crv]
calculateparams
{
arg repeat, cutinfo, lengthbts, lengthsecs;

^[
(cutinfo.at(1))*(dutycycle.value(repeat, cutinfo)),
//calculate shifted offset position
((cutinfo.at(2))+(offsetshift.value(repeat, cutinfo, lengthbts, lengthsecs)))%1.0, 
(cutinfo.at(3))*(ampfunc.value(repeat, cutinfo)),
panfunc.value(repeat, cutinfo),
pbsfunc.value(repeat, cutinfo),
atkprop.value(repeat, cutinfo),
relprop.value(repeat, cutinfo),
curve.value(repeat, cutinfo)
]
}


//might want to update sf after x phrases- but getting too general to cope with
updateblock
{
arg block,phraseprop,cuts,isroll;

//if(ampfunc.respondsTo(\updateblock), {ampfunc.updateblock(block,phraseprop,cuts,isroll);});
//ampfunc.tryPerform(\updateblock, block, phraseprop, cuts, isroll); 
//this works equally well and is less code to write down

ampfunc.tryPerform(\updateblock,  block, phraseprop, cuts, isroll);
panfunc.tryPerform(\updateblock,  block, phraseprop, cuts, isroll);
pbsfunc.tryPerform(\updateblock,  block, phraseprop, cuts, isroll);
dutycycle.tryPerform(\updateblock,  block, phraseprop, cuts, isroll);
atkprop.tryPerform(\updateblock,  block, phraseprop, cuts, isroll);
relprop.tryPerform(\updateblock,  block, phraseprop, cuts, isroll);
curve.tryPerform(\updateblock,  block, phraseprop, cuts, isroll);
offsetshift.tryPerform(\updateblock,  block, phraseprop, cuts, isroll);

}

//pass message on to relevant auxilliary classes
updatephrase
{
arg phrase, phraselength;

ampfunc.tryPerform(\updatephrase,  phrase, phraselength);
panfunc.tryPerform(\updatephrase,  phrase, phraselength);
pbsfunc.tryPerform(\updatephrase,  phrase, phraselength);
dutycycle.tryPerform(\updatephrase, phrase, phraselength);
atkprop.tryPerform(\updatephrase,  phrase, phraselength);
relprop.tryPerform(\updatephrase,  phrase, phraselength);
curve.tryPerform(\updatephrase,  phrase, phraselength);
offsetshift.tryPerform(\updatephrase, phrase, phraselength);
}


}
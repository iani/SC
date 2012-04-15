//This file is part of The BBCut Library. Copyright (C) 2001  Nick M.Collins distributed under the terms of the GNU General Public License full notice in file BBCutLibrary.help

//BBCSAutoSplice N.M.Collins 3/10/02
//Renamed and reworked 12/10/03

//or BBCSRespectOffsets
//assumes SF3 object has appropriate offset data

//probably need groove positions for using groovefunc-
//could claim groove manipulation should be in the preprocessing
//and not in this class

//For BBCutSynthSFAO just overload get/setoffset to use closest offset in onsets list for SF3 object 


BBCSKnownOffsets : BBCutSynthSF
{

//*new exactly as superclass

//only difference- use of SF3 onsets information in rendering
synthesisecut
{
arg repeat, cutinfo;	//cutinfo is of form [ioi,cutdursec,offset,amp...others]
var params, pbs, data;
var offsetsamp, dursec, cutdur;
var first;

//could do this in bbcutgroup, setoffset call if cutinfo.at(2).notNil
//cut routine decided offset takes over from cutsynth decision
if(cutinfo.at(2).notNil, {this.setoffset(cutinfo.at(2))}, {cutinfo.put(2,offset)});

params= super.calculateparams(repeat, cutinfo, sf.beats, sf.length);

//this is only point at which tempo is used- is there a better solution? 
pbs= params.at(4); //((sf.secperbeat)*(owner.tempo))*(params.at(4));

//proportional offset converted to samples only at this last stage when index is totally fixed
offset= params.at(1); 
cutinfo.put(2,offset);

//now the divergence- we need to find out which onsets to play at 

cutdur=owner.convertDurSecToBeats(params.at(0));
data= sf.getKnownInRange(offset, cutdur);

if(data.notNil,
{
first= data.at(1);

//Post <<data<<nl;

data.at(0).do({arg val, j; 

owner.tempoclock.sched(val,{
var which;

which= (first+j)%(sf.onsets.size);

//Post << [first, j, which, sf.onsets.size, sf.onsets.at(which)];
//finally as sample frames
offsetsamp= sf.onsets.at(which);	//modulo to keep in range! 

//could limit to the duration left in the cut
//apply dutycycle ratio to this too
//cut duration remaining in seconds
dursec= (1.0-(val/cutdur))*(params.at(0)); //duration in seconds till end of current cut

//Post << "durs " << [dursec, val,cutdur,  params.at(0)]<<nl; 

//choice of whether to limit by cut length or not? 

//dursec=(sf.durs.at(which).min(dursec))*((params.at(0))/(cutinfo.at(1)));
dursec=(sf.durs.at(which))*((params.at(0))/(cutinfo.at(1)));

//probably DON'T RENDER CUT WHEN BELOW A CERTAIN LENGTH ie if val too close to cutdur

//Post << [sf.durs.at(which), cutinfo.at(1)]<<nl;
//Post << "play  " << [which,offsetsamp, dursec, thisThread.seconds]<<nl<<nl;

if(sf.numChannels==1,
{
Synth.head(bbcgroup.group, \monobbcssf, [\out, bbcgroup.outbus, \bufnum, sf.buffer.bufnum, \length, dursec, \rate, pbs, \offset, offsetsamp, \amp, params.at(2), \pan,params.at(3), \atkprop, params.at(5), \relprop,params.at(6), \curve, params.at(7)]);
},
{

//only works for stereo because of restrictions on SynthDef- \numchan,sf.buffer.numChannels
Synth.head(bbcgroup.group, \stereobbcssf, [\out, bbcgroup.outbus, \bufnum, sf.buffer.bufnum, \length, dursec, \rate, pbs, \offset, offsetsamp, \amp, params.at(2), \atkprop, params.at(5), \relprop,params.at(6), \curve, params.at(7)]);
});

nil;
});

});

});





}


}

//This file is part of The BBCut Library. Copyright (C) 2001  Nick M.Collins distributed under the terms of the GNU General Public License full notice in file BBCutLibrary.help

//BBCSStore, 7/12/01 by N.M.Collins, updated for SC3 Aug 15 2003

//writes dense cuts data to a file for later recall
//will go as last BBCutSynth in chain, won't synthesise anything

BBCSStore : BBCutSynth
{
var file;

*new
{
arg file;
^super.new.initBBCSStore(file)
}

initBBCSStore
{
arg fi;

file=fi;
}

//write block data
updateblock
{
arg block,phraseprop,cuts,isroll;
var offset, blocklength; 

//in case passed offset sequence is not ready
if( cuts.at(0).at(2).isNil,{
offset= bbcgroup.getOffset;
cuts.do({arg val,i;
cuts.at(i).put(2,offset);
});
});

//correct back to beats- could just copy ioi for now, more general to convert back
//in case later routines employ dutycycle shortening tricks via dur vs ioi 
cuts.do({arg val,i;

//Post << [val.at(1),owner.convertDurSecToBeats(val.at(1))]<<nl;
cuts.at(i).put(1, owner.convertDurSecToBeats(val.at(1)));

});

blocklength=Array.fill(cuts.size, {arg i; cuts.at(i).at(0);}).sum;

file << [blocklength, cuts, isroll] << nl;

}

updatephrase
{
arg phrase, phraselength;

file << phraselength << nl;
}


}
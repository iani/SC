//This file is part of The BBCut Library. Copyright (C) 2001  Nick M.Collins distributed under the terms of the GNU General Public License full notice in file BBCutLibrary.help

//BBCutGroup, 28/7/03 by N.M.Collins
 
//also need a notion of which bus to prepare on, so multiple running bbcuts can 
//be summed to the output bus 0 
//bus as additional argument to BBCG, may need to pass BBCG to BBCutSynth instead of just group

BBCutGroup
{
var <bbcsarray, <group, <outbus; 

*initClass
{

}

*new
{
arg bbcsarray,group, outbus;

^super.new.initBBCutGroup(bbcsarray,group, outbus);
}

initBBCutGroup
{
arg bbcsa, grp, ob;

if(bbcsa.isKindOf(Array),
{
bbcsarray= bbcsa; 
},
{
//put in array
bbcsarray= [bbcsa ?? {BBCutSynth.new}];
});

//construct bbcsgroup
group= grp ?? {Group.new};

outbus= ob ? 0;       //default to direct out

}


reassign { arg bus,grp;

this.free;
outbus=bus;
group=grp; 

}

//will kill all subgroups and subsynths active at this moment plus the Node
//could be more graceful? 
free
{
group.free;
}

//passes through owner
attach
{
arg owner;

bbcsarray.do({arg val; val.attach(owner, this); });
}

//initialisation for static Synths
setup
{
bbcsarray.do({arg val; val.setup; });
}



synthesisecut
{
arg repeat, cutinfo; //dur,repeat,pbsmult=1.0;

//send copy so can update individually? cutinfo.copy
bbcsarray.do({arg val; val.synthesisecut(repeat,cutinfo);});
}

//these are the functions for passing on relevant messages

//only for last in array if want to save calls
chooseoffset
{
arg ppos,bps,currlength;
bbcsarray.do({arg val; val.chooseoffset(ppos,bps,currlength);});
}

//for bbcutprocs that also choose offsets
setoffset
{
arg prop,phraselength;
//a proportionate offset
bbcsarray.do({arg val; val.setoffset(prop,phraselength);});
}

updatephrase
{
arg phrase, phraselength;

bbcsarray.do({arg val; val.updatephrase(phrase, phraselength);});
}

updateblock
{
arg block,phraseprop,cuts,isroll;

bbcsarray.do({arg val; val.updateblock(block,phraseprop,cuts,isroll);});
}


//bbcs will return nil unless they already made a decision
getOffset
{
var offset;

bbcsarray.do({arg val; 
offset= offset ?? {val.getOffset}; 
});

^ (offset ? 0)
}


//bbcs will return nil unless there is a known source
getSource
{
var source;

bbcsarray.do({arg val; 
source= source ?? {val.getSource}; 
});

^ (source ? [0,8.0])
}


}


//shortcut
BBCG : BBCutGroup
{

}

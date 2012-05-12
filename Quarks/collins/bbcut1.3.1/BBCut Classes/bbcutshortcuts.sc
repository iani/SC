//This file is part of The BBCut Library. Copyright (C) 2001  Nick M.Collins distributed under the terms of the GNU General Public License full notice in file BBCutLibrary.help

//BBCut quick shortcuts N.M.Collins 22/10/01


BBCut11 
{

*new
{
arg sf,
sdiv=8,barlength=4.0,phrasebars=3,numrepeats=nil,stutterchance=0.2,stutterspeed=2,
randomoffset=0.0,ampfunc=1.0,panfunc=0.0, tempoclock;

^BBCut(BBCutSynthSF(sf,randomoffset,ampfunc,panfunc),
BBCutProc11(sdiv,barlength,phrasebars,numrepeats,stutterchance,stutterspeed),
tempoclock)
}

}


WarpCut1 
{

*new
{
arg sf,
blocksizefunc=nil,rollfunc=nil,probs=nil,phrasefunc=12.0,accel=0.9,
randomoffset=0.0,ampfunc=1.0,panfunc=0.0, tempoclock;

^BBCut(BBCutSynthSF(sf,randomoffset,ampfunc,panfunc),
WarpCutProc1(blocksizefunc,rollfunc,probs,phrasefunc,accel),
tempoclock
)
}

}


ChooseCut
{

*new
{
arg sf,
cutsizefunc=nil,repeatfunc=nil,rollfunc=nil,phrasefunc=16.0,rollchance=0.1,rollallowed=2.0,bpsd=0.5,
randomoffset=0.0,ampfunc=1.0,panfunc=0.0, tempoclock;

^BBCut(BBCutSynthSF(sf,randomoffset,ampfunc,panfunc),
ChooseCutProc(cutsizefunc,repeatfunc,rollfunc,phrasefunc,rollchance,rollallowed,bpsd),
tempoclock
)
}

}



PermuteCut
{

*new
{
arg sf,
phraselength=4.0, subdivfunc=8, 
permutefunc, stutterfunc=1,
ampfunc=1.0,panfunc=0.0, tempoclock;

^BBCut(BBCutSynthSF(sf,ampfunc:ampfunc,panfunc:panfunc),
BBCPPermute(phraselength,subdivfunc,permutefunc, stutterfunc),
tempoclock
)
}

*rotator
{
arg sf,
phraselength=4.0, subdivfunc=8, 
rotate=0, invert=0,
stutterfunc=1,
ampfunc=1.0,panfunc=0.0,tempoclock;

^PermuteCut(sf,phraselength, subdivfunc,
{arg i,n; 
var t; 
t=i+(rotate.value.round(1.0).asInteger);

if((invert.value)>0.1, {t= n-t-1});

t%n;
}, stutterfunc, ampfunc, panfunc, tempoclock
)
}


}


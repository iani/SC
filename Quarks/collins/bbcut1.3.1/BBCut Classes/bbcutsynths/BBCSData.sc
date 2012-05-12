//This file is part of The BBCut Library. Copyright (C) 2001  Nick M.Collins distributed under the terms of the GNU General Public License full notice in file BBCutLibrary.help

//BBCSData N.M.Collins 16/10/03
//for applying cut procedures to an array of data, string or list


BBCSData : BBCutSynth
{
var offset,randomoffset;
var data, phrasestepfunc, windowsizefunc;
var phrasestep, windowsize, pos, pl;

*new
{
arg data, phrasestepfunc, windowsizefunc, randomoffset;

^super.new.initBBCSData(data,phrasestepfunc,windowsizefunc, randomoffset);
}

initBBCSData
{
arg d,psf,wsf, ro;

data = d ? "no data provided";
phrasestepfunc= psf ? 0;
windowsizefunc= wsf ? (data.size);
randomoffset= ro ? 0.0;

pos=0;
}

synthesisecut
{
arg repeat, cutinfo;	
var durindices, startindex, endindex;

durindices= ((windowsize*(cutinfo.at(0)/pl)).round(1.0).asInteger-1).max(0);

startindex= ((pos.round(1.0).asInteger) + (((offset*windowsize).round(1.0).asInteger)))%(data.size);
endindex= (startindex+ durindices)%(data.size);

Post << if(endindex<startindex,{(data.copyRange(startindex,data.size-1))++(data.copyRange(0,endindex))}, {data.copyRange(startindex,endindex)})<<nl;

}

updateblock
{
arg block,phraseprop,cuts,isroll;

}

updatephrase
{
arg phrase, phraselength;

pl=phraselength;

phrasestep= phrasestepfunc.value(phrase, phraselength);
windowsize= windowsizefunc.value(phrase, phraselength);

pos= (pos+ phrasestep)%(data.size);
}


chooseoffset
{
arg phrasepos=0.0,grain=0.5,currlength;	

offset= if(randomoffset.value.coin,
{
(windowsize.rand)/windowsize;
}
,
{		
(phrasepos/pl)
}
);		

}


//for bbcutprocs that also choose offsets
setoffset
{
arg prop;

offset= prop;
}

getOffset
{
^offset;
}
}


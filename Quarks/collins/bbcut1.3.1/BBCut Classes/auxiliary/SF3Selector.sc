//This file is part of The BBCut Library. Copyright (C) 2001  Nick M.Collins distributed under the terms of the GNU General Public License full notice in file BBCutLibrary.help

//SF3Selector N.M.Collins 6/8/03 collection of SF3s which contains its own indexing info

SF3Selector
{
var <array, <indexfunc, <>index;

*new
{
arg array, indexfunc;

^super.new.initSF3Selector(array, indexfunc);
}

initSF3Selector
{
arg arr, ifunc;

//assumed Array of SF3 objects, if not, take as single SF3 object or 
//Array of arrays means build the SF3 objects for me! 
array= if(arr.isKindOf(Array), {
if(arr.at(0).isKindOf(SF3),
{arr},{
//make SF3Array from passed in [[filenames],[bpsound]]
SF3.array(arr.at(0),arr.at(1))
});
}, 
{[arr]}	//must be a single SF3 object at this juncture
);

indexfunc= ifunc ? 0;
index=0;
}


newindex
{
index= (indexfunc.value(index).round.asInteger)%(array.size);
}

setindex
{
arg ind=0;

index= ind%(array.size);
}


//wrappers to fetch the correct info

soundFilePath
{
^array.at(index).soundFilePath;
}

soundFile
{
^array.at(index).soundFile;
}

beats
{
^array.at(index).beats;
}

tempo
{
^array.at(index).tempo;
}

numFrames
{
^array.at(index).numFrames;
}

numChannels
{
^array.at(index).numChannels;
}

sampleRate
{
^array.at(index).sampleRate;
}

buffer
{
^array.at(index).buffer;
}

length
{
^array.at(index).length;
}

secperbeat
{
^array.at(index).secperbeat;
}

getKnownInRange
{
arg offset, dur;

^array.at(index).getKnownInRange(offset,dur);
}

onsets
{
^array.at(index).onsets;
}

durs
{
^array.at(index).durs;
}

averageBPS
{
var average;	//work out average bps

average=0;

array.do(
{
arg val;
average= average+ (1.0/(val.secperbeat));

});
average= average/(array.size);

^average;
}


/*
setAverageBPS
{
var avbps;
avbps= this.averageBPS;
workOutRateFromBPS(avbps);
}
*/

}

SF3SelectPerPhrase : SF3Selector
{

//usually allow change only on new phrase
updatephrase
{
arg phrase, phraselength;

index= (indexfunc.value(index,phrase, phraselength).round(1.0).asInteger)%(array.size);
}

}


SF3SelectPerBlock : SF3Selector
{

updateblock
{
arg block,phraseprop,cuts,isroll;

index= (indexfunc.value(index, block,phraseprop,cuts,isroll).round(1.0).asInteger)%(array.size);
}

}




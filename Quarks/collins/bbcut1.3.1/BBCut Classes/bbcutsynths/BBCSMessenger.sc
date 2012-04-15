//This file is part of The BBCut Library. Copyright (C) 2001  Nick M.Collins distributed under the terms of the GNU General Public License full notice in file BBCutLibrary.help

//BBCSMessenger, 7/12/01 by N.M.Collins, updated for SC3 Aug 15 2003

//base class is do nothing implementation
//derived classes for OSCPort, MIDI, Post, TextFile etc

//will go as last BBCutSynth in chain, won't synthesise anything

BBCSMessenger : BBCutSynth
{
var <instance, numrepeats, blocksize, blockpos, <>on;

*new
{
arg instance=0;

^super.new.initBBCSMessenger(instance+1)
}

initBBCSMessenger
{
arg ins;

instance= ins;
on=1;
}


synthesisecut
{
arg repeat, cutinfo; 
//need dur in beats as well! 


//Post << "rep  " <<repeat << "   " <<cutinfo << nl; 

//if(on==1,{

if(owner.paused<0.5,
{
this.newrepeat(cutinfo.at(1),repeat, blockpos/blocksize);
});
//});

blockpos= blockpos+(cutinfo.at(0));
}

//going to keep all info in beats for storage
updateblock
{
arg block,phraseprop,cuts,isroll;
var offset, blocklength; 

//assume already got offset in cutinfo for this block

//not going to work
offset= cuts.at(0).at(2) ? bbcgroup.getOffset;

blocklength=Array.fill(cuts.size, {arg i; cuts.at(i).at(0);}).sum;

//o/w convertDuration(blocklength)
if(owner.paused<0.5//on==1
,{
this.newblock(block,blocklength,phraseprop,offset, isroll);
});

numrepeats= cuts.size;
blockpos=0.0;
blocksize=blocklength;

}



updatephrase
{
arg phrase, phraselength;
var source;

//assume index already chosen

//which source, sourcelength
source= bbcgroup.getSource;

//this.convertDuration(phraselength)
if(owner.paused<0.5 //on==1
,{
this.newphrase(phrase,phraselength);
//this.newsource(source.at(0),source.at(1));
});

}




/////////////////////////////////
/////Possible Messages///////////
/////////////////////////////////

//Msg of form [\tempo, 2.5] say
sendMsg
{
arg array;

this.performMsg(array);
}


tempo
{
arg tempo;
}


newphrase
{
arg phrase, phraselength;
}


newsource
{
arg sourceindex, sourcelength;

}

newblock
{
arg block, blocklength, phraseprop, offset, isroll;
}


newrepeat
{
arg dur, repeat, blockprop;
}


offset
{
arg prop;
}

//for arbitrary message passing
synthparam
{
arg ident, value;
}


onoff
{
arg o=1;

on=o;
}


//added to protocol since v.useful for visual intensity
amplitude
{
arg amp;

}


//no longer any use since handled internally
//global messages for convenience
onoffinstance
{
arg o=1, inst=0;

}

//say to control external fx to bbcut
synthparaminstance
{
arg ident, value, inst=0;

}

songselect
{
arg song;

//song select 3
//midiout.write(midiout.port, 3, 16rF0, 2, song);	//last argument is song =3
}



}
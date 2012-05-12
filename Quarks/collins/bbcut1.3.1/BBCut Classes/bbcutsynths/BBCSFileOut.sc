//This file is part of The BBCut Library. Copyright (C) 2001  Nick M.Collins distributed under the terms of the GNU General Public License full notice in file BBCutLibrary.help

//BBCSFileOut, 22/01/03 /sl	update for SC3 31/8/03	
//file closed outside of class, file always passed in

BBCSFileOut : BBCSMessenger
{
var filename, file, starttime;

*new
{
arg instance=0,file;

^super.new(instance).initBBCSFileOut(file);
}

initBBCSFileOut
{
arg fi;

file=fi; //must pass in valid file, b\c need to close outside class ? File("bbcsoutput.txt","w");

starttime= thisThread.seconds;
}


addmessageglobal
{
arg array, inst;
var str;

str="time " +(thisThread.seconds-starttime) +" instance " +inst; 

array.do({
arg v, i;

//+ puts spaces automatically!
str= str+ v; 
});
str=str+nl;
 
file.write(str);
}

addmessage
{
arg array;

this.addmessageglobal(array, instance);
}

tempo
{
arg tempo;

this.addmessage(["tempo", tempo*60]);
}

newphrase
{
arg phrase, phraselength;

this.addmessage([//"phrase",phrase,
"phraselength",phraselength]);
}

newsource
{
arg sourceindex, sourcelength;

this.addmessage(["sourceindex",sourceindex,"sourcelength",sourcelength]);
}


newblock
{
arg block, blocklength, phraseprop, offset, isroll;

this.addmessage([//"block",block,
"blocklength",blocklength,"phraseprop",phraseprop,"offset",offset,"isroll",isroll]);
}


newrepeat
{
arg dur, repeat, blockprop;

this.addmessage([
"dur",dur,"repeat",repeat,
"blockprop",(blockprop*127.0).round(1.0).asInteger]);
}


offset
{
arg prop;

//This is the same message as in newblock, just isolated
this.addmessage(["offset",prop]);

}

//for arbitrary message passing
synthparam
{
arg ident, value;

this.addmessage(["synthparam",ident,"value",value]);
}

amplitude
{
arg amp;

this.addmessage(["amplitude", amp]);
}


/*
onoff
{
arg on=1;

this.addmessage(["onoff",on]);
}

//global messages for convenience
onoffinstance
{
arg on=1, inst=0;

this.addmessageglobal(["onoff",on], inst);
}
*/


offsetinstance
{
arg prop, inst=0;

//This is the same message as in newblock, just isolated
this.addmessageglobal(["offset",prop],inst);

}

//say to control external fx to bbcut
synthparaminstance
{
arg ident, value, inst=0;

this.addmessageglobal(["synthparam",ident,"value",value],inst);
}


songselect
{
arg song;

this.addmessageglobal(["songselect",song],0);
}


}
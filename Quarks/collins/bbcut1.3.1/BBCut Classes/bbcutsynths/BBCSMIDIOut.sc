//This file is part of The BBCut Library. Copyright (C) 2001  Nick M.Collins distributed under the terms of the GNU General Public License full notice in file BBCutLibrary.help

//BBCSMIDIOut, 7/12/01 by N.M.Collins

//on off sends or not sends MIDI out

//optimised for live gigs!

//may use {}.defer to avoid it interfering. then gets sent whenever possible? 
//or need latest drivers for the midiman?

//instance is channel number! 
BBCSMIDIOut : BBCSMessenger
{
var <midiout;

*new{
arg instance, mo;

//midi channels 1-16, instance 0 is chan 1, instance 15 is chan 16 then wraps
//BBCSMessenger does +1
^super.new(instance).initBBCSMIDIOut(mo); 
}

initBBCSMIDIOut
{
arg mo;

midiout=mo;
}

/////////////////////////////////
/////Possible Messages///////////
/////////////////////////////////



//val is in range 0.0 to 1.0
send14bitCC
{
arg channel, cc, val;
var a;

a=val*16384;
//MSB
midiout.control(channel,cc,a.div(128));
//LSB
midiout.control(channel,cc+32,(a%128).round(1));
}

//val is in range 0.0 to 1.0
send7bitCC
{
arg channel, cc, val;

midiout.control(channel,cc,(val*127).round(1));
}


//rescale 0.5 4.5

//tempo in beats per second passed in,
//accurate to 0.000610352 bps = 0.04 bpm
tempo
{
arg tempo;

//allowed tempo range is 0.25 to 10

tempo=tempo.max(0.25).min(10);

tempo= (tempo-0.25)/9.75;

this.send14bitCC(instance,2,tempo);
}


newphrase
{
arg phrase, phraselength; //,sourceindex, sourcelength;

//hopefully won't play more than 16834 phrases in a sitting...
//this.send14bitCC(instance,3,phrase/16384.0);

//phraselengths over 60seconds are possible but unlikely
this.send14bitCC(instance,4,(phraselength.min(60.0))/60.0);

//optimised- changed to flag only
//midiout.control(instance,4,1);
}


newsource
{
arg sourceindex, sourcelength;

//no more than 127 sources per cutter! v.reasonable
//pointless to send! either VJ knows in advance or doesn't
//midiout.control(instance,5,sourceindex);

//sourcelengths over 60 sec are possible but unlikely
this.send14bitCC(instance,6,(sourcelength.min(60.0))/60.0);
}


newblock
{
arg block, blocklength, phraseprop, offset, isroll;

//no more than 127 blocks per phrase? Highly likely unless phrase long-
//make 14bit if there are problems
//midiout.control(instance,7,block);

//blocklength capped at 16 sec- though could easily get blocklength= phraselength! 
this.send14bitCC(instance,8,(blocklength.min(16.0))/16.0);

//phraseprop in appropriate form
this.send14bitCC(instance,9,phraseprop);

//offset already a proportion- no, unless divided by source length! 
this.send14bitCC(instance,10, offset);

//simple flag, just send value
midiout.control(instance,11, isroll);

}


newrepeat
{
arg dur, repeat, blockprop;

//duration of a repeat needs fine resolution so I'll assume its under 8 seconds
//could cause trouble if blocklength= phraselength= repeatlength! 
this.send14bitCC(instance,12, (dur.min(16.0))/16.0);

//no more than 128 repeats per block
midiout.control(instance,13,repeat);

//no more than 128 gradiations here per block
midiout.control(instance,14,(blockprop*127.0).round(1.0).asInteger);

}


offset
{
arg prop;

//offset already a proportion
this.send14bitCC(instance,10, prop);
}

//for arbitrary message passing
//value assumed already in range 0.0 to 1.0
//ident in range 16-32
synthparam
{
arg ident, value;

//offset already a proportion
this.send14bitCC(instance,ident,value);
}

synthparam7
{
arg ident, value;

//offset already a proportion
this.send7bitCC(instance,ident,value);
}


amplitude
{
arg amp;

this.send7bitCC(instance,16,amp);
}

/*	//don't send MIDI, only send if on! 
onoff
{
arg on=1;

//simple flag, just send value
midiout.control(instance,15, on);
}

//global messages for convenience
onoffinstance
{
arg on=1, inst=0;

midiout.control(inst+1,15, on);
}
*/

//say to control external fx to bbcut
synthparaminstance
{
arg ident, value, inst=0;

this.send14bitCC(inst+1,ident,value);
}

synthparaminstance7
{
arg ident, value, inst=0;

this.send7bitCC(inst+1,ident,value);
}

songselect
{
arg song;
//system message F3 is song select
midiout.write(3, 16rF0, 2, song);
}


}
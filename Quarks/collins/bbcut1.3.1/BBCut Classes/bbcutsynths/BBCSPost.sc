//This file is part of The BBCut Library. Copyright (C) 2001  Nick M.Collins distributed under the terms of the GNU General Public License full notice in file BBCutLibrary.help

//BBCSPoster2, 22/01/03 /sl update 31/08/03

//just inherit all the methods, less to change if want different protocol 

BBCSPost : BBCSFileOut
{

*new
{
arg instance=0;

//can just pass nil for file since never going to use it
^super.new(instance);
}

addmessageglobal
{
arg array;
var s;

s="time " +(thisThread.seconds-starttime) +" instance " +instance; 

array.do({
arg v, i;

//+ puts spaces automatically!
s= s+ v; 
});
s=s+nl;
 
Post << s;
}


}
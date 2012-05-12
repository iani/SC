//This file is part of The BBCut Library. Copyright (C) 2001  Nick M.Collins distributed under the terms of the GNU General Public License full notice in file BBCutLibrary.help

//BBCSOSCOut, 7/12/03 by N.M.Collins

//instance is channel number! 
BBCSOSCOut : BBCSFileOut
{
var <netaddr;

*new{
arg instance, na;

^super.new(instance).initBBCSOSCOut(na); 
}

initBBCSOSCOut
{
arg na;

netaddr=na;
}

addmessageglobal
{
arg array, inst;

//message pairs, address string then instance then value to send
//array.size.div(2).do({
//arg i;
//netaddr.send(array.at(i), instance, array.at(i+1));
//});

//message pairs, address string then instance then value to sendarray.clump(2).do({arg item;netaddr.sendMsg(item.at(0), inst, item.at(1));});

}


}
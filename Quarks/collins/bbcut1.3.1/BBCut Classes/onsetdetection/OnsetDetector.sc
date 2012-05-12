//This file is part of The BBCut Library. Copyright (C) 2001  Nick M.Collins distributed under the terms of the GNU General Public License full notice in file BBCutLibrary.help

//OnsetDetector base class- N.M.Collins 6/11/03

//mostly an interface and common variables

OnsetDetector
{
var <onsets;
var uiparams, params;
var <w;

*new
{
^super.new.initOnsetDetector;
}

initOnsetDetector
{

}

//auto generate ui
editUI
{
var num;

num=uiparams.size;

if(w.isNil,
{

w= SCWindow("edit "++(this.class.asString), Rect(100, 200, 210, 40*num+10));

uiparams.do(
{
arg val,i; 
var dds;

dds=DDSlider(w, Rect(5, i*40, 200, 35), val[0],val[1],val[2],val[3],val[4],params[i]);

dds.action_({
params[i]=dds.value;
});

});

w.front;

w.onClose_({w=nil;});

});

}




save
{
^params.asString;
}


load { arg loaded;

params=loaded;
}


//pass in the filename to run on or an SF3 object
runOnFile { }


//close UI
close {
if(w.notNil,
{w.close;});
}

}
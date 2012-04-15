//This file is part of The BBCut Library. Copyright (C) 2001  Nick M.Collins distributed under the terms of the GNU General Public License full notice in file BBCutLibrary.help

//RecursiveCutProc for BBCut Library 17/10/01 N.M.Collins
//auxilliary class RCutData

RCutData
{
var <>basecut,<>baseoffset,<>cutlengths,<>subdiv;	//bars variable too!

*new
{
arg basecut,baseoffset,cutlengths;

^super.new.basecut_(basecut).baseoffset_(baseoffset).cutlengths_(cutlengths).initRCutData
}

initRCutData
{
subdiv=0;
cutlengths.do({arg val; subdiv= subdiv+val;});
}

offsetseq
{
//calculate offset sequence
var in,out,temp,diffs,offsetlist;

offsetlist=List.new(0);

cutlengths.do(
{
arg val;      //val is cut length

//intersection of recursive cut
in= rrand(0,(subdiv-val));			
out=in+val;

temp=SortedList[in,out];

basecut.do({
arg va;
if((va>in) && (va<out),
{
temp.add(va);
});
});

//no duplications and ordered increasing

//take diffs
diffs= temp.rotate(-1)-temp;
diffs.pop;	//don't need last element

diffs.do(
{
arg vl,i;

offsetlist.add([vl,baseoffset.at(temp.at(i))]);
});

}
);

//offsetlist.postln;
//offsetlist is [length,offset] sequence
//will be measured in units of beat size beatsinsound/subdiv

^offsetlist
}

}


//This file is part of The BBCut Library. Copyright (C) 2001  Nick M.Collins distributed under the terms of the GNU General Public License full notice in file BBCutLibrary.help

//BBCPRecall N.M.Collins 31/8/03

//for recovering stored cut data and just playing it back
//should play back on a loop or just play once off?
//read from file as go or just read into a big list? 

//just need succession [ioi, dur, offset, amp] is enough? 
//split up by phrase, blocks so sensible- and that's all that's needed, 
//forget individual repeats or source messages

BBCPRecall : BBCutProc
{
var file;

*new
{
arg file;

^super.new(0.5,4.0).initBBCPRecall(file);
}

initBBCPRecall
{
arg fi;

file=fi;
}


chooseblock
{
var temp, beatsleft;

//new phrase to calculate?
if(phrasepos>=(currphraselength-0.001),
{

//get new phrase message
temp=(file.readUpTo($\n)).interpret;

//in case get nil, reset read pointer or default to phrases of size 8.0 at amplitude 0
if(temp.isNil, {file.reset; temp=(file.readUpTo($\n)).interpret;
}); 		//temp=temp ? 8.0;

currphraselength= temp;

bbcutsynth.updatephrase(phrase, currphraselength);

phrasepos=0.0;
phrase=phrase+1;
block=0;

}
);

beatsleft= currphraselength- phrasepos;

//get block message
temp=(file.readUpTo($\n)).interpret;

//silent phrase completion in case of loss of data
if(temp.isNil,{temp= [beatsleft, [[beatsleft, beatsleft, 0.0,0.0]]]});

//just overlap in scheduling if a problem
cuts=temp.at(1);

//always new slice/roll to calculate
blocklength=temp.at(0);

//safety to force conformity to phrases
if(blocklength>beatsleft,{
blocklength= beatsleft;
});

//no need to use setoffset, just save cuts material
this.updateblock(temp.at(2)); 

this.endBlockAccounting;
}


}

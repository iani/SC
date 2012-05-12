//This file is part of The BBCut Library. Copyright (C) 2001  Nick M.Collins distributed under the terms of the GNU General Public License full notice in file BBCutLibrary.help

//BBCSFunc N.M.Collins 16/10/03
//arbitrary function called as cut render

BBCSFunc : BBCutSynth {
	var <>cutfunc, <>blockfunc, <>phrasefunc;
	
	*new
	{
	arg cutfunc, blockfunc, phrasefunc; 
	
	^super.new.cutfunc_(cutfunc).blockfunc_(blockfunc).phrasefunc_(phrasefunc);
	}                                                                                                                                                                                   
	
	synthesisecut
	{
	arg repeat, cutinfo;	
	
	cutfunc.value(repeat,cutinfo);
	}
	
	updateblock
	{
	arg block,phraseprop,cuts,isroll;
	
	blockfunc.value(block,phraseprop,cuts,isroll);
	}
	
	updatephrase
	{
	arg phrase, phraselength;
	
	phrasefunc.value(phrase, phraselength);
	}

}


//This file is part of The BBCut Library. Copyright (C) 2001  Nick M.Collins distributed under the terms of the GNU General Public License full notice in file BBCutLibrary.help

//BBCSPlayback1, BBCSPan1, BBCSRollAmplitude N.M.Collins 12/8/03  

//easy conversion from bbcut1 but there may be problems if you overlap blocks-
//see BBCSRollAmplitude especially

BBCSPan1
{
var pandirect,pan;
var directfunc,outrate;

*new
{
arg directfunc=nil,outrate=0.2;

^super.new.initBBCSPan1(directfunc,outrate);
}

initBBCSPan1
{
arg df=nil,or=0.2;

if(df.isNil,
{
df={(-1)**(2.rand)};	//random direction
}
);

directfunc=df;
outrate=or;
}

value
{
arg repeat, cutinfo;

pan= pan + (pandirect*(outrate.value.rand));

if(pan>1.0,{pan=1.0});
if(pan<(-1.0),{pan=(-1.0)});	

^pan
}

updateblock
{
arg block,phraseprop,cuts,isroll;

pandirect=directfunc.value;
pan=0;
}

}



BBCSPlayback1
{
var <>pbmfunc,pbdirect,pbmult,pbmultrecip,currpbmult;
var <>directfunc;

*new
{
arg pbm=0.99,directfunc=nil;

^super.new.initBBCSPlayback1(pbm,directfunc);
}

initBBCSPlayback1
{
arg pbm=0.99,df=nil;

if(df.isNil,
{
df={(-1)**(2.rand)};	//random direction
}
);

pbmfunc=pbm;
directfunc=df;
}

value
{
arg repeat,cutinfo;

if(repeat>0,
{
if(pbdirect<1,
{	//reduce!
currpbmult= currpbmult*pbmult;
},
{
currpbmult= currpbmult*pbmultrecip
}
);
});	

^currpbmult
}

updateblock
{
arg block,phraseprop,cuts,isroll;

pbdirect=directfunc.value;

pbmult=pbmfunc.value;
pbmultrecip=1.0/pbmult;

currpbmult=1;
}

}






BBCSRollAmplitude
{
var ampdirect,ampexp; 
var cutamps;

*new
{
arg ampdirect=1,ampexp=2;

^super.new.initBBCSRollAmplitude(ampdirect,ampexp);
}

initBBCSRollAmplitude
{
arg ad=1,ap=2;	//default crescendo

ampdirect=ad;
ampexp=ap;
}

value
{
arg repeat,cutinfo;
var amp;

amp=cutamps.at(repeat);

^amp
}

//need number of cuts if are to fade in a roll!
updateblock
{
arg block,phraseprop,cuts,isroll;
var ampmult,repeats,temp;

repeats=cuts.size;

if(isroll==1,{

ampmult=ampexp.value;

cutamps= Array.fill(repeats,{arg i; (i.asFloat)/repeats});

cutamps=cutamps**ampmult;

if(((ampdirect.value)<(0.00001)),
{//dim not cresc
cutamps=cutamps.reverse;	//loud to soft
}
);

},
{
cutamps= Array.fill(repeats,{1.0});
});
}


}





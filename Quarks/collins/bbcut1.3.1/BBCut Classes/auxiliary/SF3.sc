//This file is part of The BBCut Library. Copyright (C) 2001  Nick M.Collins distributed under the terms of the GNU General Public License full notice in file BBCutLibrary.help

//SF3 N.M.Collins 1/7/03, reworking of Soundfiles3 class from SC2 BBCut, but for a single buffer

//garbage collection will sort out automatically, or deallocate myself? 
//will need some facility for using start and end frames 
//setter methods for tempo
//Server determined by BBCut.server

//tempo variable may be useless! 


SF3 {

	var <soundFilePath,<soundFile,<beats=4.0,<>tempo=1.0, 
	<numFrames, <numChannels, <sampleRate, <buffer, <length, <secperbeat;
	
	//for known (allowed) onsets
	var <onsets,<durs,<beatpos, <>grooveon=false, <groovepos; 
	
	*new {arg path, beats, tempo=1.0;
		^super.new.tempo_(tempo).load(path, beats);
	}
	
	//convenience
	*array { arg patharray, beatsarray;
	
		if(patharray.size!= beatsarray.size,{^"wrong lengths".postln;});
		^Array.fill(patharray.size, {arg i; SF3(patharray.at(i), beatsarray.at(i))});
	}
	
	*newFromBuf {arg buf, beats, sampRate=44100, tempo=1.0;
		^super.new.tempo_(tempo).newBuf(buf,beats, sampRate);
	}
	
	*initClass {
	
		SynthDef("bbcsf3playmono",{
		arg out=0, bufnum=0, offset=0, rate=1.0, len=0.1, amp=1.0, pan=0.0; 
		var inner;
		
		//need the BufRateScale to cope with Sample Rate differences
		inner= amp*PlayBuf.ar(1,bufnum,rate*BufRateScale.kr(bufnum), 1.0, offset, 1.0) 
		*EnvGen.ar(Env([1,1,0],[len-0.01,0.01]),doneAction:2);
		
		Out.ar(out,Pan2.ar(inner, pan));
		}).writeDefFile;
		 
		SynthDef("bbcsf3playstereo",{
		arg out=0, bufnum=0, offset=0, rate=1.0, len=0.1, amp=1.0; 
		var inner;
		
		inner= amp*PlayBuf.ar(2,bufnum,rate*BufRateScale.kr(bufnum), 1.0, offset, 1.0) 
		*EnvGen.ar(Env([1,1],[len]),doneAction:2);
		
		Out.ar(out,inner);
		}).writeDefFile;
		 
		
		SynthDef("bbcsf3loopmono",{
		arg out=0, bufnum=0, offset=0, rate=1.0, amp=1.0, pan=0.0; 
		var inner;
		
		inner= amp*PlayBuf.ar(1,bufnum,rate*BufRateScale.kr(bufnum), 1.0, offset, 1.0); 
		
		Out.ar(out,Pan2.ar(inner, pan));
		}).writeDefFile;
		 
		SynthDef("bbcsf3loopstereo",{
		arg out=0, bufnum=0, offset=0, rate=1.0, len=0.1, amp=1.0; 
		var inner;
		
		inner= amp*PlayBuf.ar(2,bufnum,rate*BufRateScale.kr(bufnum), 1.0, offset, 1.0);
		
		Out.ar(out,inner);
		}).writeDefFile; 
		 
	}
	
	
	
	//no setting of soundFilePath or SoundFile
	newBuf { arg buf, bts, sampRate=44100;
	
		buffer= buf; 
		
		//could be nil
		soundFilePath= buf.path;
		
		numChannels = buf.numChannels;
		
		//sampleRate of Server  SampleRate.ir  is only interrogation method I know
		sampleRate = sampRate; 
		numFrames= buf.numFrames;
		length= numFrames/sampleRate;	//in seconds
		
		beats= bts ?? {(length*tempo).round(1.0).asInteger.max(1)}; 
		secperbeat= length/beats;
	}
	
	//don't free old buffer, was passed in to begin with so responsibility of client code
	//but should free if loaded a soundfile and now replacing with external buffer? 
	//still leave to client since have a free method of this class
	replaceBuf { arg buf, bts, sampRate;
		var old;
		
		old=buffer;
		
		this.newBuf(buf,bts,sampRate);
		
		old.free;
		}
		
	load { | path, bts |
		
		//dependence on Sample(now Document)+ PathName classes easy to remove if too annoying
//		soundFilePath = PathName.standardizePath(path);
		soundFilePath = path ? "sounds/a11wlk01.wav";		
		soundFile = SoundFile.new;
		if(soundFile.openRead(soundFilePath),
		{soundFile.close;}, // got it
		{^("Sample failed to load SoundFile at path: " + soundFilePath).error;}
		);
		
		numChannels = soundFile.numChannels;
		sampleRate = soundFile.sampleRate;
		numFrames= soundFile.numFrames;
		length= numFrames/sampleRate;	//in seconds
		
		//for default assume tempo 1.0, find an integer number of beats 
		beats= bts ?? {(length*tempo).round(1.0).asInteger.max(1)};
		secperbeat= length/beats;
		
		
		//create a Buffer
		
		buffer= Buffer.read(BBCut.server, soundFilePath, 0, numFrames);
		 
	}
	
	replace {arg path, bts;
		var sfp, sf; 
		
		sfp = Sample.standardizePath(path);
					
		sf = SoundFile.new;
		if(sf.openRead(sfp),
		{sf.close;
		
		//create a Buffer- why create a new one? why not buffer.read? 
		Buffer.read(BBCut.server, sfp, 0, numFrames,
		{	//completion message: ready, so do the swap
		arg bf;
		var old;
		
		old=buffer;
		
		buffer=bf;
		numChannels=sf.numChannels; 
		sampleRate=sf.sampleRate; 
		numFrames= sf.numFrames;
		length= numFrames/sampleRate;
		beats= bts ?? {(length*tempo).round(1.0).asInteger.max(1)};
		secperbeat= length/beats;
		
		
		soundFile=sf;
		soundFilePath= sfp;
		
		//can't free whilst might still be playing- rely on gc?
		//gc won't free buffer. Call free after 10 seconds 
		SystemClock.sched(10,{old.free;});
		}
		);
		
		}, // got it
		{("Sample failed to load SoundFile at path: " + sfp).error;}
		);
		
	}
	
	//free the bufnum and set the reference to the buffer object to nil to force garbage collection
	free {
		buffer.free;
		buffer=nil;
	}
	
	
	workOutRateFromBPS {arg bps;	//desired tempo in bps
	
		//basic equation- original speed in seconds per beat * desired bps= playbackspeed
		^secperbeat*bps;
	}
	
	workOutRateFromBPM { arg bpm;
	
		bpm= bpm/60.0;
		
		^this.workOutRateFromBPS(bpm)
	}
	
	play { arg bus=0, target, addaction= \addToHead, rate=1.0, offset=0.0, amp=1.0, pan=0.0, len;  
	
		//play once through sample at whatever speed or play for specified duration
		len= len ? (length*(rate.reciprocal));
		
		^if(numChannels<2, {
		Synth("bbcsf3playmono",
		[\out, bus, \bufnum, buffer.bufnum, \offset, offset, \rate, rate, \len, len, \amp, amp,\pan, pan],
		target, addaction
		);}, {
		Synth("bbcsf3playstereo",
		[\out, bus, \bufnum, buffer.bufnum, \offset, offset, \rate, rate, \len, len, \amp, amp],
		target, addaction
		);
		});
		
	}
	
	//need a global in bbcut lib for current Server- BBCut::server classvar
	//play once immediately
	loop { arg bus=0, target, addaction= \addToHead, rate=1.0, offset=0.0, amp=1.0, pan=0.0;  
		
		^if(numChannels<2, {
		Synth("bbcsf3loopmono",
		[\out, bus, \bufnum, buffer.bufnum, \offset, offset, \rate, rate, \amp, amp,\pan, pan],
		target, addaction
		);}, {
		Synth("bbcsf3loopstereo",
		[\out, bus, \bufnum, buffer.bufnum, \offset, offset, \rate, rate, \amp, amp],
		target, addaction
		);
		});
	
	}
	
	
	
	/*
	//need a global in bbcut lib for current Server- BBCut::server classvar
	//play once immediately
	play
	{
	arg bus=0, target, addaction= \addToHead, rate=1.0, offset=0.0, amp=1.0, pan=0.0, len;  
	
	//play once through sample at whatever speed or play for specified duration
	len= len ? (length*(rate.reciprocal));
	
	//done here so that channel array can be passed in trouble free 
	^(SynthDef("bbcssf3play",{
	var inner, bufnum;
	
	bufnum= buffer.bufnum; 
	
	//need the BufRateScale to cope with Sample Rate differences
	inner= amp*PlayBuf.ar(numChannels,bufnum,rate*BufRateScale.kr(bufnum), 1.0, offset, 1.0) 
	*EnvGen.ar(Env([1,1],[len]),doneAction:2);
	
	if(numChannels<2, {inner= Pan2.ar(inner, pan);});
	
	Out.ar(bus,inner);
	}).play(target,nil, addaction));
	 
	}
	
	
	//need a global in bbcut lib for current Server- BBCut::server classvar
	//play once immediately
	loop
	{
	arg bus=0, target, addaction= \addToHead, rate=1.0, offset=0.0, amp=1.0, pan=0.0;  
	
	//done here so that channel array can be passed in trouble free 
	^(SynthDef("bbcssf3loopplay",{
	var inner, bufnum;
	
	bufnum= buffer.bufnum; 
	
	inner= amp*PlayBuf.ar(numChannels,bufnum,rate*BufRateScale.kr(bufnum), 1.0, offset, 1.0);   
	//play in a loop, no enveloping
	
	if(numChannels<2, {inner= Pan2.ar(inner, pan);});
	
	Out.ar(bus,inner);
	}).play(target,nil, addaction));
	 
	}
	*/
	
	
	//play once immediately
	playForSynthDef { arg rate=1.0, offset=0.0, amp=1.0, pan=0.0, len;  
		var inner, bufnum;
		
		//play once through sample at whatever speed or play for specified duration
		len= len ? (length*(rate.reciprocal));
		
		bufnum= buffer.bufnum; 
		
		//need the BufRateScale to cope with Sample Rate differences
		inner= amp*PlayBuf.ar(numChannels,bufnum,rate*BufRateScale.kr(bufnum), 1.0, offset, 1.0) 
		*EnvGen.ar(Env([1,1],[len]),doneAction:2);
		
		//don't adjust for numChannels, leave as is... if(numChannels<2, {inner= Pan2.ar(inner, pan);});
		
		^inner;
	}
	
	loopForSynthDef { arg rate=1.0, offset=0.0, amp=1.0, pan=0.0;  
		
		var inner, bufnum;
		
		bufnum= buffer.bufnum; 
		
		inner= amp*PlayBuf.ar(numChannels,bufnum,rate*BufRateScale.kr(bufnum), 1.0, offset, 1.0);   
		//play in a loop, no enveloping
		
		//if(numChannels<2, {inner= Pan2.ar(inner, pan);});
		^inner;
	}
	
	
	//will allow automatic preparations
	setKnown {arg known, durations, beatp;
	
		if((known.size)<1, {
		known= this.evenslices(known ? ((beats*4.0).round(1.0).asInteger))
		});
		
		onsets=known;
		
		beatpos= if(beatp.notNil, {(beatp.copy)}, {((onsets/numFrames)*(beats))});
		
		//need duration in seconds for enveloping
		durs= durations;
		 
		if((durs.size)<1, {durs=
		this.calculatedurations(onsets, durs ? 1.0)
		});
		
	}
	
	
	setGroove {arg quantise=0.25, groovefunc; 
		var t,prev,next;
		
		//corresponds to delay to second SQ of 0.07 beat= (0.32,0.18) swing= uk garage...
		//groovelevel can be a function, passed the argument phrasepos
		groovefunc= groovefunc ?  {arg beat; var tmp; tmp= beat%0.5; if(  ((tmp>=0.25) && (tmp<=0.49)) ,{beat-tmp+0.32},{beat})};   
		
		//assume semiquaver resolution for a rough template of regions
		groovepos=beatpos.round(quantise.value);
		
		groovepos.do({arg val,i; 
		
		t= groovefunc.value(val);
		
		//make sure between previous and next value
		prev = if(i>0, {groovepos.at(i-1)},{0.0});
		next = if(i<(groovepos.size-1), {groovepos.at(i+1)},{beats});
		
		if(t<prev,{t=prev;});
		if(t>next, {t=next;});
		
		groovepos.put(i,t);
		});
		
		//groovepos= groovepos.sort; //in case put them well out of order?
		
	}
	
	//naive search, could do binary later assuming onsets sorted list
	getNearest {arg offset;
	var index=0, dist;
	dist=numFrames;
	
	if(onsets.size==0, {^0.0});
		
	onsets.do({arg val,i; if(((val-offset).abs)<dist,{index=i; dist=val-offset; });});	
	^onsets[index];
	}
	
	
	//assumes setKnown already done
	getKnownInRange {arg offset, dur;	//offset 0.0-1.0, (dur in secs, needs to be beats)
		var playlist, first, last, beatstart,beatend, test, ext;
		var beatpositions;
		
		if(onsets.size==0, {^nil});
		
		//use beatpositions
		beatstart= offset*beats;
		
		beatend= beatstart+dur;	//assumes dur in beats
		
		//extension means that if the cut is really big, so lots of copies of the signal fit within it,
		//we check all offsets captured over many cycles of the signal
		ext= beatend.div(beats);
		
		beatpositions= if(grooveon, {groovepos}, {beatpos}); 
		
		test= beatpositions; 
		ext.do({arg i; test= test ++ ((beatpositions) + ((i+1)*beats))});
		
		if(((test.last)<beatstart), {^nil});
		
		first= 0;
		 
		while({(test.at(first))<(beatstart-0.001)}, {first=first+1;});
		
		//first is required start marker
		
		if((test.last) < beatend, {
		//solution is all from first to last in array
		last= test.size-1;
		},
		//else there is some x st test.at(x)>=beatend
		{
		last=first;
		
		while({test.at(last)<beatend}, {last=last+1;});
		
		if(last==first, {^nil});
		}
		);
		
		//Post <<[beatstart, beatend, first, last]<<nl;
		
		^[(test.copyRange(first,last-1))-beatstart, first];
	}
	
	
	//if delay before first allowed offset, can always deduct []-[].at(0) in client code before passing in- here is a helper function for that
	*normaliseallowed {arg allowed;
	
		^allowed- (allowed.at(0));
	}
	
	//helper function for preparing durations- encapsulation- also called from init
	calculatedurations {arg offsets, prop=1.0;
		var temp;
		
		temp= offsets.rotate(-1);
		temp.put(temp.size-1,numFrames);
		^((temp-offsets)/numFrames)*prop*length	//get durations in seconds
		}
		
	evenslices {arg numslices=16;
		var slicelength, slices;
		
		slicelength= numFrames/numslices;
		
		^Array.fill(numslices,{arg i; i*slicelength}).round(1.0).asInteger;
	}
	
	//for on the fly playback rate manipulations
	recalc {arg bts;
		
		beats= bts;
		secperbeat= length/beats;
	}

}
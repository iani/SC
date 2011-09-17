SlaveClock : TempoClock {
	classvar <globalMaster, uniqueSuffix="V0";
	var <master, <relativeTempo=1, <>name;
	var <inSync=false, <lastSyncMethod, waitFuncs, <meterCondition, isRun=true, >test=false;
	
	*new { arg tempo, beats, seconds, queueSize=256, beatsPerBar, masterClock, name;
		^super.new(tempo, beats, seconds, queueSize=256)
			.name_(this.uniqueCopyNameSuffix(name ? \nn).asSymbol)
			.register(masterClock).initSlave(beatsPerBar)
	}
	*initClass {
		globalMaster = TempoClock(4).permanent_(true)
	}
	*uniqueCopyNameSuffix{|sym|
		var str, i;
		if (all.any{|c| if (c.respondsTo(\name)) { c.name == sym } { false } }.not) { ^sym }{
			str = sym.asString;
			i = str.findBackwards(uniqueSuffix);
				if (i.notNil) { 
					i=i+uniqueSuffix.size; 
					^this.uniqueCopyNameSuffix((str.keep(i) ++ (str.drop(i).asInt+1)).asSymbol)
				}{ 	^this.uniqueCopyNameSuffix((str ++ uniqueSuffix ++ 1).asSymbol) }
		}
	}
	initSlave{|newBeatsPerBar|
		waitFuncs = List.new;
		relativeTempo = super.tempo;
		meterCondition = Condition.new(true);
		this.syncTempoMeter(newBPB: newBeatsPerBar);
		// 2 // this.setTempoMeter(newBPB: newBeatsPerBar);
		if (test) { this.test }
			// 1 //this.syncMasterQuant(relativeTempo * master.ttempo, quant: master.beatsPerBar);
	}
	register{|masterClock|
		master = masterClock ? globalMaster;
		master.addDependant(this)
	}
	master_ {|masterClock|
		master !? { master.removeDependant(this) };
		this.register(masterClock)
	}
	clear {
		isRun = false; super.clear
	}
	stop {
		this.changed(\stop);
		this.releaseDependants;
		master !? { master.removeDependant(this) };
		all.take(this);
		this.prStop;
	}
	update {|who, what ...args| //this.logln("update:" + [who, what, args]);
		if (isRun) {
			what.switch( 
			\tempo, { this.ttempo_( relativeTempo * master.ttempo ) } 
			,\masterTempo,  { this.prTempo_( relativeTempo * master.ttempo ) } 
			,\stop, { this.stop }
			,\synced, { this.executeLastSyncMethod }
			,\meter, {}
			,{this.logln("unmapped update:" + [who, what, args]) });
		}{ } // { this.logln("DEAD - not running anymore:" + [who, what, args]) }
	}
	//sync sched ----------------------------------------------------------------------------------
	schedSync{|func| // this.logln("schedSync something" + [inSync, master.inSync]);
		if (inSync || master.inSync ) { inSync = true; lastSyncMethod = func 
		}{ inSync = true; func.value }
	}
	synced {
//this.logln("SYNCED" + thisThread.seconds + "-- lastSyncMethod, waitFuncs" + [lastSyncMethod, waitFuncs]);
		if (lastSyncMethod.notNil) { this.executeLastSyncMethod }{
			inSync = false;
			this.changed(\synced);
			waitFuncs.do{|func| func.value };
			waitFuncs = List.new;
		}
	}
	executeLastSyncMethod{ //if immediate re-call of a syncMethod --> crash !!!
		var func = lastSyncMethod;
//this.logln("executeLastSyncMethod" + thisThread.seconds + "lastSyncMethod" + func); 
		lastSyncMethod = nil;
		{ func.value }.defer(super.timeToNextBeat / 3) 
	}
	doWhenSynced {|func| // never sched an syncMethod here without delay -> crash !!!
		if (inSync) { waitFuncs.add(func) } { func.value }
	}
	// complFunc of sync methods below are private! -> use doWhenSynced instead
	//sync 1 --------------------------------------------------------------------------------------
	syncMasterQuant {|newTempo, masterQuant=1| //timeToNextBeat(quant) always next within same bar!
		if (master.notNil) {
			newTempo = (newTempo ? relativeTempo) * master.ttempo;
			this.schedSync( { this.prSync(newTempo, master.timeToNextBeat(masterQuant)) } );
		}{ if (newTempo.notNil && (relativeTempo != newTempo)) { this.tempo_(newTempo) } };
	}
	prSync {|newTempo, delta|
		master.sched(delta, { 
			if (isRun) { 
				this.prTempo_( master.ttempo * super.timeToNextBeat(1)); //adopt tempo; no send
				master.sched(master.timeToNextBeat(1), {
					if (isRun) { 
						this.ttempo_(newTempo); //send now + newTempo
						this.synced;
					}; nil });
			}; nil });
	}
	//sync 2 --------------------------------------------------------------------------------------
	syncOnMasterBar {|newTempo, complFunc|  // var delta, d2;
		if (master.notNil) {
			newTempo = (newTempo ? relativeTempo) * master.ttempo;
/*		
		master.sched(master.timeToNextBeat(1), {
			this.logln("beatInBar: [master, slave, diff]" + [master.beatInBar.round(0.001), super.beatInBar, master.beatInBar - super.beatInBar]);
			this.logln("beats left to nextBar: [master, slave, diff]" + [master.beatsPerBar - master.beatInBar.round(0.001), super.beatsPerBar - super.beatInBar]);
			delta = master.beats2secs(master.nextBar) - master.seconds;
			d2 = super.beats2secs(super.nextBar) - super.seconds;
			this.logln("delta, d2" + [delta, d2]);
			this.logln("delta in sec: [bar, beat]" +  [(master.beats2secs(master.nextBar) - master.seconds), master.timeToNextBeat(1)]);
			this.logln("master tempo:" + master.ttempo);
			this.logln("intermediate tempo:" + [ master.ttempo * d2 / delta * super.tempo,  master.ttempo * super.timeToNextBeat(1) ] );
			this.logln("intermediate tempo:" + [ d2 / delta * super.tempo,  master.ttempo * super.timeToNextBeat(1) ] );
			nil
		});
*/		
			this.schedSync( {this.prSync2(newTempo, complFunc) } )
		}{ if (newTempo.notNil && (relativeTempo != newTempo)) { this.tempo_(newTempo) } };
	}
	prSync2 {|newTempo, complFunc|
		master.sched(master.timeToNextBeat(1), { if (isRun) {
			this.prTempo_( super.tempo * (super.beats2secs(super.nextBar) - super.seconds)
				/ (master.beats2secs(master.nextBar) - master.seconds) );
			master.schedAbs(master.nextBar, { if (isRun) {
				this.ttempo_(newTempo); //send now + newTempo
				if (complFunc.notNil) { complFunc.value } { this.synced };
			}; nil });
		}; nil })
	}
	setTempoMeter {|newTempo, newBPB, slaveBeatOffset=0, masterBeatOffset=0|
		if (master.notNil) {
			if (newBPB.isNil) { this.syncOnMasterBar(newTempo)
			}{ this.syncOnMasterBar(newTempo, { 
				this.prSyncSetBeatsPerBar(newBPB, slaveBeatOffset, masterBeatOffset) }) }
		}{ if (newTempo.notNil && (relativeTempo != newTempo)) { this.tempo_(newTempo) } }
	}
	//sync 3 --------------------------------------------------------------------------------------
	syncBeatInBarOnMasterPhase {|newTempo, beatInBar=0, phase=0, quant, complFunc|
		if (master.notNil) {
			this.schedSync({ this.prSBInBOMPhase(newTempo, beatInBar, phase, quant, complFunc) })
		}{ if (newTempo.notNil && (relativeTempo != newTempo)) { this.tempo_(newTempo) } }
	}
	prSBInBOMPhase {|newTempo, beatInBar=0, phase=0, quant, complFunc|
		newTempo = (newTempo ? relativeTempo) * master.ttempo;
		this.prSync3(newTempo, beatInBar, quant ?? { master.beatsPerBar }, phase, complFunc)
	}
	prSync3 {|newTempo, beatInBar, quant, phase, complFunc| // quant is > 0 !
		if ((master.nextTimeOnGrid(quant, phase) - master.beats) < 1) { 
			quant = master.beatsPerBar + quant };
//this.changed(\syncBeatInBarOnMasterPhase, newTempo);
		master.sched(master.timeToNextBeat(1), { if (isRun) {
		  this.prTempo_( super.tempo * // master.ttempo  *
		  	(super.beats2secs(super.nextTimeOnGrid(super.beatsPerBar, beatInBar)) - super.seconds)
			/ (master.beats2secs(master.nextTimeOnGrid(quant, phase)) - master.seconds) );
			master.schedAbs(master.nextTimeOnGrid(quant, phase), { if (isRun) {
				this.ttempo_(newTempo); //send now + newTempo
				if (complFunc.notNil) { complFunc.value } { this.synced };
			}; nil });
		}; nil })
	}
	syncTempoMeter {|newTempo, newBPB, slaveBeatOffset=0, masterBeatOffset=0|
		if (master.notNil) {
			slaveBeatOffset = (slaveBeatOffset * -1) % super.beatsPerBar;
			masterBeatOffset = (masterBeatOffset * - 1) % master.beatsPerBar;
			if (newBPB.isNil) {
				this.syncBeatInBarOnMasterPhase(newTempo, slaveBeatOffset, masterBeatOffset)
			}{
				this.syncBeatInBarOnMasterPhase(newTempo, slaveBeatOffset, masterBeatOffset,
				 	complFunc: { this.prSyncSetBeatsPerBar(newBPB, 0, 0) } ) 
			}
		}{ if (newTempo.notNil && (relativeTempo != newTempo)) { this.tempo_(newTempo) } }
	}
	syncToMasterTempo { this.syncOnMasterBar(master.ttempo) }
	//meter change -------------------------------------------------------------------------
	syncSetBeatsPerBar {|newBPB, slaveBeatOffset=0, masterBeatOffset=0, complFunc| 
		this.schedSync({ 
			this.prSyncSetBeatsPerBar(newBPB, slaveBeatOffset=0, masterBeatOffset=0, complFunc) })
	}
	prSyncSetBeatsPerBar {|newBPB, slaveBeatOffset=0, masterBeatOffset=0, complFunc| 
		var beatOfMeterChange = if (master.isNil) { nil } {
		super.secs2beats(master.beats2secs(master.nextBar + masterBeatOffset)) + slaveBeatOffset};
/*	
nothing of those ideas works:
	var msec = (master.beats2secs(master.nextBar + masterBeatOffset) - master.seconds);
this.logln("msec:" + msec);

	if (msec <= 0) { this.logln("beatOfMeterChange" + beatOfMeterChange);
	beatOfMeterChange =
		super.secs2beats(master.beats2secs(master.nextTimeOnGrid(master.beatsPerBar, master.beatsPerBar) + masterBeatOffset)) + slaveBeatOffset;
		this.logln("take next round" + [beatOfMeterChange, beatOfMeterChange.round]);
		beatOfMeterChange = min(beatOfMeterChange, beatOfMeterChange.round) - 0.001;
	};
*/
		this.prSetBeatsPerBar(newBPB, beatOfMeterChange, complFunc);
	}
	prSetBeatsPerBar { |newBPB, beatOfMeterChange, complFunc|
		newBPB = newBPB ?? { this.beatsPerBar };
		beatOfMeterChange = beatOfMeterChange ?? { super.nextBar };
		meterCondition.test = false;
		super.schedAbs(beatOfMeterChange, { if (isRun) {
			super.beatsPerBar_(newBPB);	
//this.logln("newBPBn:" + newBPB);
			master.schedAbs(master.nextBar + master.beatsPerBar, { if(isRun) {
				meterCondition.test = true; meterCondition.signal;
				if (complFunc.notNil) { complFunc.value } { this.synced };
				//Synth(\ping, [\freq, 400, \amp, 0.5]);
			}; nil });
			// if (complFunc.notNil) { complFunc.value } { this.synced };
			// Synth(\ping, [\freq, 400, \amp, 0.5]);
		}; nil })
	}
	masterBeatsPerBar { ^master.beatsPerBar }
	masterBeatsPerBar_ {|newBPB| 
		master.schedAbs(master.nextBar, { master.beatsPerBar_(newBPB) ; nil })
	}
	// relative tempo
	tempo {
		^if (master.isNil) { super.tempo } { relativeTempo }
	}
	tempo_ {|newTempo, masterQuant=1|
		relativeTempo = newTempo;
		if (master.notNil) { super.tempo_(newTempo * master.ttempo) // sends changed(\tempo)
		}{ super.tempo_(newTempo) }
	}
	// true tempo
	ttempo {
		^super.tempo 
	}
	ttempo_ {|newTempo| 
		if (master.notNil) { relativeTempo = newTempo / master.ttempo
		}{ relativeTempo = newTempo };
		this.prTempo_(newTempo); 
		this.changed(\tempo)  
	}
	prTempo_{|newTempo| 
		this.setTempoAtBeat(newTempo, this.beats);
		this.changed(\masterTempo); // don't sent intermediate tempo changes; used when sync
	}
	//direct interface ---------------------------------------------------------------------------
	super {|selector ...args| ^this.superPerformList(selector, args)}
	
	//... unfinished
	
	test { 
		var mRepeat = 1; //master.beatsPerBar;
		var sRepeat = 1; //super.beatsPerBar;
		var mSec = master.seconds;

		this.logln("repeat each beat [master, slave]" + [mRepeat, sRepeat]);
		
		// quant on bar
		master.play({|b, sec| mSec = sec; 
			this.logln("master:" + master.beatInBar); mRepeat }, master.beatsPerBar);
		this.play({|b, sec| this.logln("s" + ("   " ++ (mSec-sec).round(0.001)).keep(-4) ++ ":"
			+ this.beatInBar.round); sRepeat }, super.beatsPerBar);
		
		master.sched(master.timeToNextBeat(1)+0.01, {"-----".postln; 1});
		test = false;
	}
	
	/* // needs some tuning here...
	sync {|tempo, secs= 4, resolution= 1| // redFrik 050621
		var next, time, durCur, durNew, durDif, durAvg, stepsPerBeat,
			delta, factor, steps, sum, durs, index= 0;
		secs= secs.max(0.03);					//saftey and lower jitter limit
		next= this.timeToNextBeat(1);
		time= secs-(this.tempo.reciprocal*next);
		if(time<next, {						//jump directly
			this.setTempo(next/secs);		//set a high tempo
			this.sched(next, {
				this.setTempo(tempo);
				nil;
			});
		}, {							//else interpolate
			this.sched(next, {				//offset the thing to next beat
				durCur= this.tempo.reciprocal;
				durNew= tempo.reciprocal;
				durDif= durNew-durCur;
				durAvg= durCur+durNew/2;		//average duration for number of steps
				stepsPerBeat= resolution.max(0.001).reciprocal.round;
				steps= (time/durAvg).round*stepsPerBeat;
				delta= stepsPerBeat.reciprocal;		//quantized resolution
				durs= Array.series(steps, durCur, durDif/steps);
				sum= durs.sum/stepsPerBeat;
				factor= time/sum;
				this.sched(0, {
					var tmp;
					if(index<steps, {
						tmp= (durs[index]*factor).reciprocal;
						this.setTempo(tmp);
						index= index+1;
						delta;
					}, {
						this.setTempo(tempo);
						nil;
					});
				});
				nil;
			});
		});
	}
	*/
}

+ TempoClock {
	*cmdPeriod {
		all.do({ arg item; item.clear(false) });
		all.copy.do({ arg item; if (item.permanent.not, { item.stop })  }) //mc bugfix: copy !!
	}
	
	ttempo { ^this.tempo } // true tempo
	inSync { ^false }
}

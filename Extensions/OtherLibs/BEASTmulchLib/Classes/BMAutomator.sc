BMAbstractAutomator {
	var time, rate, referenceTime;
	var lastTime; // last received from time ref
	var <timeReference; // my clock source
	var <running = false;
	update {  this.subclassResponsibility(thisMethod);}
	
	addToRef {timeReference.addDependant(this);}
	
	removeFromRef {timeReference.removeDependant(this);}
	
	timeReference_ {|ref|
		this.removeFromRef;
		timeReference = ref;
		this.addToRef;
	}
	
	// what this is depends on subclass
	automate { this.subclassResponsibility(thisMethod);} 
	
	mappings { this.subclassResponsibility(thisMethod);}
	
	mappings_ { this.subclassResponsibility(thisMethod);} 
	
	timeInitialised { ^(time.notNil && rate.notNil && referenceTime.notNil) }
}

// update rate of this and time ref are independent
BMAbstractIndependentRateAutomator : BMAbstractAutomator {
	var <>interval = 0.005; // update interval
	var lastCurrentTime;
	
	startUpdateLoop {
		running = true;
		Routine({
		while({running}, {
			this.automate;
			interval.wait;
		});
		}).play;
	}
	
	// this is the simple case, but you can override to have
	// more complicated cleanup
	stopUpdateLoop {
		running = false;
	}
	
	reset { }
	
	update {arg changed, what ...args; 

		switch(what,
			\n_end, {
			
			},
			\play, { },
			\playFailed, {

				},
			\stop, {
				time = 0; // needed?
				this.stopUpdateLoop;
				this.reset;
				},
			\time, { time = args[0]; rate = args[1]; referenceTime = args[2]; 
				
//				time.postln;
//				("ref:" + referenceTime).postln;
//				("main:" + Main.elapsedTime).postln;
				
				// turn on the auto update loop if rate !=0, off if it does
				(rate != 0).if({ 
					running.not.if({this.startUpdateLoop;}); 
				}, {  
					running.if({this.stopUpdateLoop;});
					// check if we've moved while paused and update if so
					(time != lastTime).if({this.automate;});
				});
				
				lastTime = time;
			}
		)
	}
}


/*
The logic for this is actually pretty complicated. We need to allow for:

- unspecified start (and possibly end states)
- variable rates of playback, both positive and negative
- starting in the middle of a sequence

Solution 1:
when you enter a segment with an arbitrary state as one of its points just assign the current fader values to that point regardless of positive or negative rate. When you leave that segment clear it.

Solution 2:
As above, but add an extra interpolation point making a flat line segment between the start and current state.

NB Making a new env is very low cost.

Solution 2 is current

----

At the moment there can be only one automator assigned to each control.
It is possible to have multiple controller automators (for instance assigned to different time references) providing they don't try to automate the same controls.
A single automator can have overlapping sequences, but if they try to update the same controller in the same automation cycle all but the first will fail. 

For more elaborate and fine tuned control, just have separate sequences for each fader.

*/

BMControllerAutomator : BMAbstractIndependentRateAutomator {
	// interpolates between controller snapshots
	var <controls; // an array of controlNames or a single one
	var <sequences; // an dict of BMSnapShotSeqs
	var oldSeqs; // the sequences that were active last time through the automate loop
	var sinSmooth = true; // use sin curves for automation
	
	*new { |controls, timeref|
		^super.new.init(controls, timeref);
	}
	
	init {|argctrls, argref|
		controls = argctrls.asArray;
		controls.copy.do({|ctrlname| 
			var ctrl;
			ctrl = BMAbstractController.allControls[ctrlname];
			ctrl.automator.isNil.if({
				ctrl.automator = this;
			}, {
				("Controller automation assignment failed. Control" + ctrlname 
					+ "already has an automator").warn;
				controls.remove(ctrlname);
			});
		});
		timeReference = argref;
		sequences = IdentityDictionary.new;
		this.addToRef;
		oldSeqs = IdentitySet.new;
	}
	
	// if ctrlNames is nil add a global one
	addSequence {|seqName, startTime, ctrlNames|
		seqName = seqName.asSymbol;
		sequences.keys.includes(seqName).if({
			"Sequence Name" + seqName + "already in Use!".error; 
			^this;
		});
		sequences[seqName] = 
			BMSnapShotSeq(seqName, ctrlNames ? controls, startTime, sinSmooth.if({'sin'}, {'lin'}))
				.addDependant(this);
		this.changed(\sequencesChanged);
	}
	
	// not so sure...
	mappings { ^sequences }
	
	mappings_ { |mappings|
		sequences.do({|seq| seq.removeDependant(this);});
		sequences = mappings ?? {IdentityDictionary.new};
		sequences.do({|seq| seq.addDependant(this);});
		oldSeqs = IdentitySet.new;
		this.changed(\sequencesChanged);
	}
	
	addStartSnapShot { }
	
	addSnapShot {|seqName, ssTime, ssName| 
		ssTime = ssTime ?? {
			if(this.timeInitialised, {
				BMTimeReferences.currentTime(timeReference);
			}, {0});
		};
		sequences[seqName].addSnapShot(ssTime, ssName);
	}
	
	// convenience method to add individual sequences for each controller
	addIndividualSequences {|seqName, startTime|
		controls.do({|ctrlname| 
			seqName = (seqName.asString ++ "-" ++ ctrlname).asSymbol;
			sequences.keys.includes(seqName).if({"Sequence Name already in Use!".error; ^this;
			},{
				sequences[seqName] =
					BMSnapShotSeq(seqName, startTime, sinSmooth.if({'sin'}, {'lin'}))
						.addDependant(this);
			});
		});
		this.changed(\sequencesChanged);
	}
	
	removeSequence { |seq|
		seq.isSymbol.if({seq = sequences[seq];});
		sequences[seq.name] = nil;
		seq.removeDependant(this);
		this.changed(\sequencesChanged);
	}
	
	sequence {|name| ^sequences[name.asSymbol] }

	// how to deal with bundling?
	automate {
		var currentTime, values, control;
		currentTime = BMTimeReferences.currentTime(timeReference);
		//currentTime.postln;
		sequences.do({|seq|
			seq.containsTime(currentTime).if({
				oldSeqs.findMatch(seq).isNil.if({
					seq.reset;
					oldSeqs.add(seq);
				});
				values = seq.atTime(currentTime);
				values.keysValuesDo({|ctrlname, value| 
					control = BMAbstractController.allControls[ctrlname];
					// check another sequence hasn't touched this control
					if(control.lastAutomated != currentTime, {
						BMAbstractController.setValueByName(ctrlname, value);
						control.lastAutomated = currentTime;
					}, {
						("Multiple snapshot sequences simultaneously attempting to automate" 
							+ ctrlname).warn;
					});
				});
				
			}, {
				// check if seq should have ended and set end values
				// if we've leapt around a lot don't worry about it
				
				seq.end.exclusivelyBetween(currentTime - interval, currentTime).if({
					//\sequenceEnd.postln;
//					("CT" + currentTime).postln;
//					("CT-" + (currentTime - interval)).postln;
					values = seq.atTime(seq.end);
					values.keysValuesDo({|ctrlname, value| 
						control = BMAbstractController.allControls[ctrlname];
						// check another sequence hasn't touched this control
						if(control.lastAutomated != currentTime, {
							BMAbstractController.setValueByName(ctrlname, value);
							control.lastAutomated = currentTime;
						}, {
							("Multiple snapshot sequences simultaneously attempting to automate" 
								+ ctrlname).warn;
						});
					});
				});
				oldSeqs.remove(seq);
			});
		});
	}
	
	reset { sequences.do(_.reset); oldSeqs.clear; this.clearLastAutomated}
	
	clearLastAutomated {
		controls.do({|ctrlname| 
			BMAbstractController.allControls[ctrlname].lastAutomated = nil;
		});
	}
	
	free { controls.do({|ctrl| ctrl.automator = nil});}
	
	update {arg changed, what ...args; 
		
		switch(what,
			\segsBuilt, {
				this.changed(\sequencesChanged);
			},
			{super.update(changed, what, *args)}
		)
	}
	
}

// can't change the list of affected controllers after creation!
BMSnapShotSeq {
	var <name, controls, <curve;
	var started = false;
	var <start, <end, <duration;
	var arbStart, arbStartEnd;
	var arbEnd, arbEndEnd;
	var <snapshots; // by order
	var <snapshotsDict; // by name
	var firstSnap, snapTimes, segs;
	var oldSeg;
	var <minSegSize = 0.15; // minimum size for a segment
	
	*new {|name, controls, firstSnapTime, curve = 'lin'| // controls is an array of keys indicating control names
		^super.newCopyArgs(name, controls, curve).init(firstSnapTime);
	}
	
	init {|firstSnapTime|
		
		start = max(0, firstSnapTime - 1);
		
		firstSnap = BMArbitraryStartSnapShot(controls, start, 'Start');
		snapshots = [
			firstSnap,							// arbitrary
			BMSnapShot(controls, firstSnapTime, (name ++ "-1").asSymbol)	  	// known
		];
		snapshots.do(_.addDependant(this));
		snapshotsDict = IdentityDictionary.new; // by name rather than order
		snapshotsDict['Start'] = firstSnap;
		snapshotsDict[(name ++ "-1").asSymbol] = snapshots[1];
		this.buildSegs;
	}
	
	minSegSize_ {|newSize| minSegSize = newSize; this.buildSegs }
	
	nextNumber {
		^snapshots.size; 
	}
	
	addSnapShot {|time, ssname|
		var snap;
		ssname = ssname.asSymbol;
		snapshotsDict.keys.includes(ssname).if({
			"Snapshot" + ssname + "already exists.".error;
			^this;
		});
		if(time < start && firstSnap.isKnown.not, {
			start = max(0, time - 1);
			// avoid extra update
			firstSnap.removeDependant(this);
			firstSnap.time = start;
			firstSnap.addDependant(this);
		});
		
		snap = BMSnapShot(controls, time, ssname);
		snap.addDependant(this);
		snapshots = snapshots.add(snap).sort({|a, b| a.time < b.time });
		snapshotsDict[ssname] = snap;
		this.buildSegs;
	}
	
	removeSnapShot { |ssname|
		var snapshot;
		snapshot = snapshotsDict[ssname];
		snapshot.removeDependant(this);
		snapshots.remove(snapshot);
		snapshotsDict[ssname] = nil;
		this.buildSegs;
	}
	
	buildSegs {
		segs = [];
		// arb snapshot first, sort order correctly
		snapshots = snapshots.sort({|a, b| a.time < b.time || (a === firstSnap)  });
		//postf("snapshots(buildSegs): %\n", snapshots);
		if(firstSnap.time >= snapshots[1].time, {
			firstSnap.removeDependant(this);
			firstSnap.time = max(snapshots[1].time - minSegSize, 0);
			firstSnap.addDependant(this);
		});
		// second sort?
		snapshots = snapshots.sort({|a, b| a.time < b.time || (a === firstSnap)});
		
		//postf("snapshots(buildSegs): %\n", snapshots.collect(_.name));
		snapshots.doAdjacentPairs({|a, b|
			// check minimum length
			if(b.time - a.time < minSegSize, {
				b.removeDependant(this);
				b.time = a.time + minSegSize;
				b.addDependant(this);
			});
			segs = segs.add(BMSnapShotSequenceSeg(a, b, controls, curve));
		});
		start = snapshots.first.time;
		end = snapshots.last.time;
		duration = end - start;
		snapTimes = snapshots.collect(_.time);
		this.changed(\segsBuilt);
	
	}
	
	// returns a dict of ctrlname -> val[time]
	atTime {|time| 
		var values, seg, ind;
		// return nil if not within this sequence's duration?
		if(this.containsTime(time), {
			// maybe better to cache this, and update when a snapshot is changed
			// using dependancy
			seg = segs[snapTimes.indexInBetween(time).trunc.clip(0, segs.size - 1)];
			if(seg != oldSeg, {
				seg.makeActive(time);
				oldSeg = seg;
			});
			values = controls.collectAs({|ctrlname| 
				ctrlname -> seg.atTime(ctrlname, time);
			}, IdentityDictionary);
			
		}); 
		^values // exclusive values return nil
	}
	
	containsTime {|time| ^time.inclusivelyBetween(start, end);}
	
	reset { 
		oldSeg = nil; 
	}
	
	update {arg changed, what ...args; 
		
		switch(what,
			\snap, {
				this.buildSegs;
				this.changed(\segsBuilt);
			},
			\snapTime, {
				this.buildSegs;
				this.changed(\segsBuilt);
			}
		)
	}

}

BMSnapShotSequenceSeg {
	var <startSS, <endSS, controls, curve;
	var envs, known;
	
	*new {|startSS, endSS, controls, curve = 'sin'|
		^super.newCopyArgs(startSS, endSS, controls, curve).init;
	}
	
	init {
		known = startSS.isKnown && endSS.isKnown;
	}
	
	makeActive { |time|
		startSS.makeActive(controls, time);
		endSS.makeActive(controls, time);
		this.makeEnvs;
	}
	
	// Could also delay envs to save a subtraction
	atTime {|ctrlname, time|
		^envs[ctrlname][time - startSS.time]
	}

	// might optimise here to check if both ss are known and cache envs if true
	// maybe not worth it
	// Could also delay envs to save a subtraction
	makeEnvs {
		known.if({
			envs = controls.collectAs({|ctrlname| 
				ctrlname -> Env(
					[startSS.values[ctrlname], endSS.values[ctrlname]], 
					[endSS.time - startSS.time], 
					curve
				);
			}, IdentityDictionary);
		}, {
			// if arb with a flat segment at start
			envs = controls.collectAs({|ctrlname| 
				ctrlname -> Env(
					[startSS.values[ctrlname], startSS.values[ctrlname], endSS.values[ctrlname]], 
					[startSS.snapTime - startSS.time, endSS.time - startSS.snapTime], 
					curve
				);
			}, IdentityDictionary);
			
		});
	}
	
}

BMAbstractSnapShot {
	var <name, <time, <values, <controlNames;
	// these allow for customised behaviour upon entering a segment
	
	*new{|controls, time, name|
		^super.newCopyArgs(name).snap(controls, time);
	}
	
	time_ {|newTime| 
		time = newTime;
		this.changed(\snapTime);
	}
	
	snap {|controls, argTime|  
		time = argTime;
		controlNames = controls;
		values = controls.collectAs({|ctrlname| 
			ctrlname -> BMAbstractController.getValueByName(ctrlname);
		}, IdentityDictionary);
		//postf("snap values: %\n", values);
		this.changed(\snap);
	}
	makeActive { this.subclassResponsibility(thisMethod); }
	
	isKnown {^true}
	
	setValue {|ctrl, value| values[ctrl] = value; this.changed(\snap);}
}

// a known state
BMSnapShot : BMAbstractSnapShot {
	// no-op
	makeActive { } 
}

// for unknown start (and maybe end) states
BMArbitraryStartSnapShot : BMAbstractSnapShot {
	
	var <snapTime;
	
	*new{|controls, time, name|
		^super.newCopyArgs(name).init(time);
	}
	
	init {|argTime| time = argTime; }
	
	makeActive {|controls, argTime|  
		this.tempsnap(controls, argTime); 
	}
	
	snap { } // don't pass go, no $200
	
	tempsnap {|controls, argTime|  
		snapTime = argTime;
		values = controls.collectAs({|ctrlname| 
			ctrlname -> BMAbstractController.getValueByName(ctrlname);
		}, IdentityDictionary);
	}
	
	isKnown {^false}
}


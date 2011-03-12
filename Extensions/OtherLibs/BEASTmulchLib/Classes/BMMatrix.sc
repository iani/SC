// Matrixes for audio and and scaling with controllers

// Defines the minimum interface for a matrix AudioChainElement
BMAbstractMatrix : BMAbstractAudioChainElement {

	var <matrixArray, <mappings, defname, defSentCond; // defname is the def for a node
	
	*new { |ins, outs, target, addAction = \addToTail, name|
		^super.new.init(ins.asBMInOutArray, outs.asBMInOutArray, target, addAction, name);
		// default name is class
	}
	
	init {|argins, argouts, argTarget, argAddAction, argName|
		this.initNameAndTarget(argTarget, argAddAction, argName);
		ins = argins;
		outs = argouts;
		// used for indices for matrix lookup
		inNames = ins.keys;
		outNames = outs.keys;
		this.newCollections;
		defname = ("BMMatrix-" ++ name);
		defSentCond = Condition.new(false);
		this.sendDef;
	}
	
	newCollections {
		matrixArray = Array.newClear(outNames.size) ! ins.size;
		//should this be a set instead of a list?
		mappings = ins.keys.collectAs({|key| key -> List.new}, IdentityDictionary);
	}
	
	// allows for multiple outs mapped at once
	connect {  |input ... outputs| // Symbols
		// not so efficient, but okay for our purposes
		var inBus, outBus, inMatrixIndex, outMatrixIndex;
		(inBus = ins[input]).notNil.if({
			server.bind({
				server.sync(defSentCond);
				inMatrixIndex = inNames.indexOf(input);
				outputs = outputs.flat;
				outputs.do({ |out|
					(outBus = outs[out]).notNil.if({
						outMatrixIndex = outNames.indexOf(out);
						mappings[input].includes(out).not.if({
							matrixArray[inMatrixIndex][outMatrixIndex] = 
								Synth.new(defname, [\in, inBus, \out, outBus], group);
							mappings[input].add(out);
							this.changed;
						}, {warn(input ++ " already connected to " ++ out)});
					}, {error("Output:" + out + "is not defined.")});
				});
			});
		}, {error("Input:" + input + "is not defined.")});
	}
	
	disconnect { |input ... outputs| // Symbols
		var inBus, outBus, inMatrixIndex, outMatrixIndex;
		(inBus = ins[input]).notNil.if({
			inMatrixIndex = inNames.indexOf(input);
			outputs = outputs.flat;
			outputs.do({|out|
				(outBus = outs[out]).notNil.if({
					outMatrixIndex = outNames.indexOf(out);
					matrixArray[inMatrixIndex][outMatrixIndex].release(BMOptions.crossfade);
					matrixArray[inMatrixIndex][outMatrixIndex] = nil;
					mappings[input].remove(out);
					this.changed;
				}, {error("Output:" + out + "is not defined.")});
			});
		}, {error("Input:" + input + "is not defined.")});
	}
	
	clear { |time = 0.1|
		group.release(time);
		this.newCollections; // gc's all the Synths
		this.changed;
	}
	
	// then we're a lame duck
	free {
		this.clear;
		SystemClock.sched(0.1, {group.free; group = nil; allChainElements[name] = nil;});
		//CmdPeriod.remove(this);
	}
	
	mappings_ {|mappingsDict| // same format as instance var
		this.clear;
		mappingsDict = mappingsDict ? ();
		mappingsDict.keysValuesDo({|input, outputs| this.connect(input, outputs.asArray)});
	}
	
	// subclass stuff
	sendDef {
		^this.subclassResponsibility(thisMethod);
	}
	
	// does this take controls for inputs
	takesControlsForInputs {^false }

}

// maps ins to outs
BMAudioMatrix : BMAbstractMatrix {
	
	sendDef {
		{
			SynthDef(defname, { arg in, out, gate = 1;
				// short fade in and out
				Out.ar(out, In.ar(in, 1) 
					* EnvGen.kr(Env.asr(BMOptions.crossfade, 1, BMOptions.crossfade), gate, doneAction: 2)
				);
			}).send(server);
		server.sync(defSentCond);
		}.fork;	
	}

}

// maps ins to outs with an optional amp scale
BMAudioMixerMatrix : BMAbstractMatrix {
	
	sendDef {
		{
			SynthDef(defname, { arg in, out, amp = 1, gate = 1;
				// short fade in and out
				Out.ar(out, In.ar(in, 1) * Lag.kr(amp, BMOptions.crossfade)
					* EnvGen.kr(Env.asr(BMOptions.crossfade, 1, BMOptions.crossfade), gate, doneAction: 2)
				);
			}).send(server);
		server.sync(defSentCond);
		}.fork;	
	}
	
		// allows for multiple outs mapped at once
	connect {  |input ... outputs| // outputs are [symbol, amp]
		// not so efficient, but okay for our purposes
		var inBus, outBus, inMatrixIndex, outMatrixIndex;
		(inBus = ins[input]).notNil.if({
			server.bind({
				server.sync(defSentCond);
				inMatrixIndex = inNames.indexOf(input);
				if(outputs.rank == 3, {outputs = outputs.unbubble});
				outputs.do({ |out|
					var outname;
					if(out.size < 2, { out = [out, 1].flat}); // default amp is 1
					outname = out.first;
					(outBus = outs[outname]).notNil.if({
						outMatrixIndex = outNames.indexOf(outname);
						mappings[input].flat.includes(outname).not.if({
							matrixArray[inMatrixIndex][outMatrixIndex] = 
								Synth.new(defname, 
									[\in, inBus, \out, outBus, \amp, out[1]], 
									group
								);
							mappings[input].add(out);
							this.changed;
						}, {
							// if we find it, set the level
							matrixArray[inMatrixIndex][outMatrixIndex].set(\amp, out[1]);
							mappings[input].detect({|item| item.first == out.first})[1] = out[1];
						});
					}, {error("Output:" + out + "is not defined.")});
				});
			});
		}, {error("Input:" + input + "is not defined.")});
	}
	
	disconnect { |input ... outputs| // Symbols
		var inBus, outBus, inMatrixIndex, outMatrixIndex;
		(inBus = ins[input]).notNil.if({
			inMatrixIndex = inNames.indexOf(input);
			outputs = outputs.flat;
			outputs.do({|out|
				(outBus = outs[out]).notNil.if({
					outMatrixIndex = outNames.indexOf(out);
					matrixArray[inMatrixIndex][outMatrixIndex].release(BMOptions.crossfade);
					matrixArray[inMatrixIndex][outMatrixIndex] = nil;
					mappings[input] = mappings[input].reject({|item| item.first == out});
					this.changed;
				}, {error("Output:" + out + "is not defined.")});
			});
		}, {error("Input:" + input + "is not defined.")});
	}
}

// maps amp scales to control busses
// roll your own curves etc. elsewhere
// this only allows an output to be connected to a single input 
BMAmpControlMatrix : BMAbstractMatrix {
	
	var outmappings;
	
	newCollections {
		matrixArray = Array.newClear(outNames.size) ! ins.size;
		//should this be a set instead of a list?
		mappings = ins.keys.collectAs({|key| key -> List.new}, IdentityDictionary);
		outmappings = IdentityDictionary.new;
	}
	
	connect { |input ... outputs|
		var currentIn, mappedTo;
		
		outputs = outputs.flat;
		
		if(outputs.size == 0, {^this }); // this edge case arises sometimes
		
		// confirm that input exists
		BMAbstractController.allControls[input].isNil.if({
			("Control mapping failed because control" + input + "could not be found").error;
			^this;
		});
		
		// outputs can only be mapped to a single input (control)
		outputs.do({|output| 
			currentIn = outmappings[output];
			currentIn.notNil.if({this.disconnect(currentIn, output); });
		});
		
		// check if somebody else owns input
		BMOptions.allowMultipleControlMappings.not.if({ 
			mappedTo = BMAbstractController.allControls[input].mappedTo;
			if(mappedTo.notNil && (mappedTo !== this), {
				("Control mapping failed. Control" + input + "already controlling" + mappedTo.name).warn;
				^this;
			}, {
				BMAbstractController.allControls[input].mappedTo_(this);
			});
		});
		
		outputs.do({|output| 
			outmappings.add(output -> input);
		});
		
		super.connect(input, *outputs);
	}
	
	disconnect { |input ... outputs| // Symbols
		var mappedTo;
		super.disconnect(input, *outputs);
		
		// if I'm not using this input (control) anymore release my claim
		BMOptions.allowMultipleControlMappings.not.if({ 
			mappedTo = BMAbstractController.allControls[input].mappedTo;
			if(mappings[input].size == 0 && (mappedTo === this), {
				BMAbstractController.allControls[input].mappedTo = nil;
			});
		});
	}
	
	clear {|time|
		this.clearControlMappings;
		super.clear(time);
	}
	
	clearControlMappings {
		var control;
		BMOptions.allowMultipleControlMappings.not.if({ 
			inNames.do({|inName|
				control = BMAbstractController.allControls[inName];
				if(control.mappedTo === this, { 
					control.mappedTo = nil 
				});
			})
		});
	}
	
	sendDef {
		{
			SynthDef(defname, {arg in, out, gate = 1;
				// XFade in new scaled value, crossfade out when freeing so no clicks
				XOut.ar(out, 
					EnvGen.kr(Env.asr(BMOptions.crossfade, 1, BMOptions.crossfade), gate, doneAction: 2),
					In.ar(out, 1) * In.kr(in, 1)
				);
			}).send(server);
		server.sync(defSentCond);
		}.fork;
	}
	
	takesControlsForInputs { ^true }
	
	// for display purposes
	asSpec{ ^\db.asSpec }
	
}

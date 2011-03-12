// uses a control bus on the server to map values
// bend goes from 0 to 16384 and is mapped to values between 0 and 1 (assumes amplitude for the moment)
// assumes MIDIClient is initialised

BMAbstractMIDIController : BMAbstractController {
	var <midiport, <uid, <outPort, <outUid, <midiout;
	var responder, <>loopBack = false;
	var <>acceptsAutomation = false;
	var inputSpec;
	
	startListening { 
		this.subclassResponsibility(thisMethod);
	}
	
	makeInputSpec { 
		this.subclassResponsibility(thisMethod);
	}
	
	init { |argmidiport, argname, argserver|
		midiport = argmidiport;
		uid = midiport.inuid;
		outUid = midiport.outuid;
		outPort = midiport.outport;
		outUid.isNil.if({("outport for" + name + "not found.").warn});
		name = argname;
		server = argserver;
		this.setNumControls;
		valueArray = Array.fill(numControls, {0});
		bus = Bus.control(server, numControls);
		busIndex = bus.index;
		this.startListening;
		// protect against one sided port
		outPort.notNil.if({
			midiout = MIDIOut(outPort, outUid);
		}, {
			acceptsAutomation = false;
			loopBack = false;
			warn(name.asString + "has no midi out port. Automation and output disabled.");
		});
		spec = [0, 1, 'cos', 0.0].asSpec;
		this.makeInputSpec;
		this.updateAllValues(valueArray.copy);
		allControllers[name] = this;
	}
	
	setNumControls {
		this.subclassResponsibility(thisMethod);
	}
	
	loopback {
		this.subclassResponsibility(thisMethod);
	}
	
	// val is native midi value
	updateValue { |ind, val|
		var value;
		valueArray[ind] = val;
		value = spec.map(inputSpec.unmap(val)); // convert from midi to 0..1 and then add curve
		server.sendMsg("/c_set", busIndex + ind, value);
		if(loopBack || acceptsAutomation, {this.loopback(ind, val)});
	}
		
	updateAllValues { |array|
		array.do({|item, i| this.updateValue(i, item)});
	}
	
	// assumes fader 1 = 1 not 0
	// returns value between 0 and 1
	getVal { |controlNum| ^spec.map(inputSpec.unmap(valueArray[controlNum -1])) }
	
	setVal { |controlNum, val| this.updateValue(controlNum -1, inputSpec.map(spec.unmap(val))) }
	
	getAllValues { ^valueArray.collect({|val| spec.map(inputSpec.unmap(val))}) }
	
	setAllValues {|array| 
		array.do({|item, i| 
			this.updateValue(i, inputSpec.map(spec.unmap(item)))
		});
	}
	
	// this has no labels
	setLabel { |controlNum, name| this.shouldNotImplement(thisMethod) }
	
	getLabel { |controlNum| ^this.shouldNotImplement(thisMethod) }
	
	getAllLabels { ^this.shouldNotImplement(thisMethod) }
	
	setAllLabels { |array| this.shouldNotImplement(thisMethod)}

}

// 14 bit bend
BMMIDIBendController : BMAbstractMIDIController {

	*new { |midiport, name, server|
		^super.new.init(midiport, name, server ? Server.default).addControlsToIndex;
	}
	
	*newFromParamDict {|dict, server| 
		^this.new(dict[\midiport], dict[\name], server);
	}
	
	*parameterList { 
		var class;
		class = this;
		^(
			name: [Symbol, {class.makeName}, "Name"],
			midiport: [BMMIDIPort, nil, "MIDI Port"]
		); 
	}
	
	*humanName {  ^"MIDI Pitchbend Controller"  }

	setNumControls { numControls = 16;}
	
	makeInputSpec {
		inputSpec = [0, 16384].asSpec;
	}
	
	startListening { 
		responder = BendResponder({|src, chan, value|
			this.updateValue(chan, value);
		}, uid);
	}
	
	loopback {|ind, val|
		midiout.bend(ind, val);
	}

}

// MidiControllers on a particular channel
BMMIDICCController : BMAbstractMIDIController {
	var chan, ccArray;

	*new { |midiport, name, chan, ccArray, server|
		^super.new
			.setCCParams(chan, ccArray)
			.init(midiport, name, server ? Server.default).addControlsToIndex;
	}
	
	*newFromParamDict {|dict, server| 
		^this.new(dict[\midiport], dict[\name], dict[\chan] - 1, dict[\ccArray], server);
	}
	
	*parameterList { 
		var class;
		class = this;
		^(
			name: [Symbol, {class.makeName}, "Name"],
			midiport: [BMMIDIPort, nil, "MIDI Port"],
			chan: [Integer, [1, 16, \linear, 1, 0].asSpec, "MIDI Channel"],
			ccArray: [Int8Array, "", "CC numbers"]
		); 
	}
	
	*humanName {  ^"MIDI CC Controller"  }
	
	setNumControls { numControls = ccArray.size}
	
	setCCParams { |argchan, argccArray|
		chan = argchan.asInteger;
		ccArray = argccArray;
	}

	makeInputSpec {
		inputSpec = [0, 127].asSpec;
	}
	
	startListening { 
		responder = CCResponder({|src, chan, num, value|
			this.updateValue(ccArray.indexOf(num), value);
		}, uid, chan, ccArray);
	}
	
	loopback {|ind, val|
		midiout.control(chan, ccArray[ind], val);
	}

}


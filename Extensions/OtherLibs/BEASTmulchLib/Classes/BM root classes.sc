
// class for storing some library wide and BEASTmulch System options
BMOptions {
	classvar <version = #[1, 0, 0];
	classvar <>crossfade = 0.1;
	classvar <>numInputBusChannels = 8;
	classvar <>numOutputBusChannels = 8;
	classvar <>numWireBufs = 512; // can be complicated
	classvar <>numAudioBusChannels = 1024; // we need a lot
	classvar <>memSize = 131072; // all those delays for compensating
	classvar <>allowMultipleControlMappings = false;
	
	*defaultServerOptions {
		^ServerOptions.new
			.memSize_(memSize)
			.numWireBufs_(numWireBufs)
			.numAudioBusChannels_(numAudioBusChannels)
			.numOutputBusChannels_(numOutputBusChannels)
			.numInputBusChannels_(numInputBusChannels);
	}
}

// Defines the minimum interface for an AudioChainElement

BMAbstractAudioChainElement {
	classvar <allChainElements;
	var <ins, <outs, <inNames, <outNames; // in the default case the getters return nil, as an element need not have both ins and outs
	var <target, <addAction, <group, <server, <name;
	
	// simplest case
//	*new { |target, addAction = \addToTail, name| 
//		^super.new.init(target, addAction, name);
//	}
	
	// simplest case
	initNameAndTarget {|argtarget, argaddAction, argname|
		target = argtarget.asTarget;
		server = target.server;
		addAction = argaddAction;
		name = (argname ?? {this.makeName}).asSymbol;
		allChainElements[name] = this;
		this.makeGroup;
	}
	
	*initClass {
		allChainElements = IdentityDictionary.new;
	}
	
	mappings {
		
	}
	
	mappings_ {|mappings|
		
	}
	
	asBMInOutArray { ^outs }
	
	// this should return an instance of our default GUI class
	// which builds the window itself
	gui { ^this.subclassResponsibility(thisMethod); } 
	
	// this way if you make them in order
	makeGroup { group = Group.new(target, addAction); }
	
	makeName { ^(this.class.name ++ UniqueID.next).asSymbol} 
	
	free { group.notNil.if({group.free}); allChainElements[name] = nil}
	
	loadPiece { } // do nothing by default

}

BMAbstractAudioSource : BMAbstractAudioChainElement {

	// sources addToHead
//	*new { |target, addAction = \addToHead, name| 
//		^super.new.init(target, addAction, name);
//	}
	
	// experimental time ref support
	play { ^nil }
	
	pause { ^nil }
	
	stop { ^nil }
	
	togglePlay { ^nil }
	
	setTime { }
	
	asTarget { ^group }
	
}


BMAbstractController {
	classvar <allControllers, <allControls;
	var <name, <bus, <busIndex, valueArray, labelArray, <server, <numControls;
	var spec;
	
	*initClass {
		allControllers = IdentityDictionary.new;
		allControls = IdentityDictionary.new;
		CmdPeriod.add(this);
	}
	
	*cmdPeriod {
		allControls.do({|v| v.mappedTo_(nil)});
	}
	
	*dumpAllValues {
		"\n///////////////////\nDumping all Controller Values\n".postln;
		allControllers.keysValuesDo({|key, elem| 
			(key ++ ": ").post;
			elem.getAllValues.postcs;
			"\n".post
		});
		"///////////////////".postln;
	}
	
	*getValueByName{|ctrlName|
		^allControls[ctrlName].value;
	}
	
	*setValueByName{|ctrlName, val|
		allControls[ctrlName].value_(val);
	}
	
	*masterInOutArray {
		^BMInOutArray.new.putAll(*(allControllers.values.collect({|item| item.asBMInOutArray })));
	}
	
	addControlsToIndex {
		this.controlNames.do({|ctrlName, i|
			ctrlName = ctrlName.asSymbol;
			allControls[ctrlName] = BMControl(ctrlName, this, i + 1);
		});
	}
	
	free {
		this.controlNames.do({|ctrlName, i|
			ctrlName = ctrlName.asSymbol;
			allControls[ctrlName].mappedTo_(nil);
			allControls[ctrlName] = nil;
		});
		allControllers[name] = nil;	
		bus.free;
	}
	
	getVal { |controlNum| ^this.subclassResponsibility(thisMethod) }
	
	setVal { |controlNum, val| this.subclassResponsibility(thisMethod) }
	
	getAllValues { ^this.subclassResponsibility(thisMethod) }
	
	setAllValues {|array| this.subclassResponsibility(thisMethod)}
	
	setLabel { |controlNum, name|
		this.subclassResponsibility(thisMethod)
	}
	
	// by default controllers have no labels
	getLabel { |controlNum| ^nil }
	
	getAllLabels { ^nil}
	
	setAllLabels { |array| }
	
	controlNames {^Array.fill(numControls, {|i| (name.asString ++ "-" ++ (i+1)).asSymbol})}

	asBMInOutArray {
		^this.controlNames.collectAs({|item, i| item.asSymbol -> (i + busIndex)}, BMInOutArray);
	}
	
	// perhaps this should be more generalised and named something else like 'preset'
	mappings {
		^IdentityDictionary[\labels->this.getAllLabels, \ctrlVals->this.getAllValues];
	}
	
	mappings_ {|mappings|
		mappings = mappings ? ();
		this.setAllLabels(mappings[\labels]);
		this.setAllValues(mappings[\ctrlVals]);
	}
	
	acceptsAutomation { ^false }
	
	spec { ^spec.asSpec; }
	
	calibrate {
		("No calibration to do for " ++ name ++ ".").postln;
		^0;
	}
	
	// a dictionary of arguments, excluding server in the form
	// argname->[class, spec, humanName];
	// class should be Integer, float, String, Symbol, NetAddr or corresponding RawArrays
	// humanName is a String
	*parameterList {  ^this.subclassResponsibility(thisMethod);  }
	
	*humanName {   ^this.subclassResponsibility(thisMethod);  }
	
	*makeName { ^(this.humanName + UniqueID.next).asSymbol} 
	
	// {|argsDict| Me.new(...) }
	*newFromParamDict {|dict, server|   ^this.subclassResponsibility(thisMethod);  }
	
}

// don't make these yourself
BMControl {
	var <name, <controller, <ctrlNum, <mappedTo, <automator, <>lastAutomated;
	
	*new {|name, controller, ctrlNum|
		^super.newCopyArgs(name, controller, ctrlNum);
	}
	
	mappedTo_ {|to| mappedTo = to; this.changed(\mappedTo) }
	
	automator_ {|atmtr| automator = atmtr; this.changed(\automator) }
	
	value {^controller.getVal(ctrlNum) }
	
	value_ {|val| controller.setVal(ctrlNum, val) }
	
	controllerSpec { ^controller.spec }
	
	// experimental
	displaySpec { ^mappedTo.asSpec }
}

BMAbstractGUI {
	var <name, <window;
	var onClose;
	
	onClose_{|func|
		onClose = onClose.addFunc(func);
	}
	
	makeWindow { ^this.subclassResponsibility(thisMethod);  }
	
	close { window.close }
}
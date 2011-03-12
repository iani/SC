
// valueArray holds the controller value in its native form
// setValue should convert to 0-1 and send to the bus 
BMMotorBEAST : BMAbstractController {
	
	var <addr, responder, calibrationRanges;
	
	// address should be with port 57120 (sclang)
	*new { |addr, name, server|
		^super.new.init(addr, name, server ? Server.default).addControlsToIndex;
	}

	*newFromParamDict {|dict, server| 
		^this.new(NetAddr(dict[\addr], 57120), dict[\name], server);
	}
	
	*parameterList { 
		var class;
		class = this;
		^(
			name: [Symbol, {class.makeName}, "Name"],
			addr: [String, "0.0.0.0", "IP Address"]
		); 
	}
	
	*humanName {  ^"MotorBEAST (GLUI)"  }
	
	init { |argaddr, argname, argserver|
		addr = argaddr;
		name = argname;
		server = argserver;
		numControls = 32;
		valueArray = 0 ! 32;
		bus = Bus.control(server, numControls);
		busIndex = bus.index;
		calibrationRanges = Archive.global['MotorBEASTCal', name] ?? {this.initialCalibrations};
		spec = [0, 1, 'cos', 0.0].asSpec;
		this.startListening;
		allControllers[name] = this;
	}
	
	initialCalibrations {
		^([0, 65535, 0.0, 1.0] ! 32);
	}
	
	calibrate {
		var envs, highs, lows, interval = 0.005;
		var transitionTime = 0.18, tries = 4;
		var lowError = 0.003, hiError = 0.02;
		
		
		lowError = spec.map(lowError).asInteger;
		hiError = spec.map(hiError).asInteger;
		
		// take worst of 4 tries
		lows = 1 ! 32;
		highs = 65535 ! 32;
		{	
			("Calibrating" + name).postln;
			tries.do({
			calibrationRanges = this.initialCalibrations; // full range
			// highs
			envs = this.getAllValues.collect({|start| 
				Env([start, 1.0, 1.0], transitionTime ! 2, 'sine').asStream;
			});
			
			(transitionTime * 2 / interval).do({
				this.setAllValues(envs.collect({|env| env.next}););
				interval.wait;
			});
			0.5.wait;
			highs = valueArray.copy.min(highs);
			
			// lows
			envs = this.getAllValues.collect({|start|
				Env([start, 0.0, 0.0], transitionTime ! 2, 'sine').asStream;
			});
			
			(transitionTime * 2 / interval).do({
				this.setAllValues(envs.collect({|env| env.next}););
				interval.wait;
			});
			0.5.wait;
			lows = valueArray.copy.max(lows);
			
			
			});
			("Low Values: " ++ lows.collect({|val, i| 
				spec.map(val.linlin(*calibrationRanges[i])) 
			})).postln;
			("\nHigh Values: " ++ highs.collect({|val, i| 				spec.map(val.linlin(*calibrationRanges[i])) 
			})).postln;
			calibrationRanges = Array.fill(32, {|i| 
				[lows[i] + lowError, highs[i] - hiError, 0.0, 1.0] 
			});
			
			// for now use Archive
			Archive.global['MotorBEASTCal', name] = calibrationRanges;
			Archive.write;
			("\nCalibration for" + name + "done").postln;
		}.fork;
		^(transitionTime * 4 + 1 * tries);
		
	}
	
	*clearCalibrations {
		Archive.global['MotorBEASTCal'] = nil;
		Archive.write;
	}
	
	startListening { 
		// do updates directly here to minimize dispatch
		responder = OSCresponderNode(addr, '/analogMF', { arg time, resp, msg; 
			var values;
			values = msg.copyToEnd(1);
			server.sendMsg("/c_setn", busIndex, 32, *(values.collect({|val, i| 
				spec.map(val.linlin(*calibrationRanges[i]))
			})));
			valueArray= values;
			this.changed(\faderVal);
		}).add;
	}
	
	stopListening { responder.remove; responder = nil }
	
	// assumes fader 1 = 1 not 0
	// returns value between 0 and 1
	getVal { |controlNum| ^spec.map(valueArray[controlNum -1].linlin(*calibrationRanges[controlNum - 1])) }
	
	// we set the local value on loopback, so we're always in sync
	setVal { |controlNum, val| 
		var cal;
		cal = calibrationRanges[controlNum - 1];
		addr.sendMsg("/MF/" ++ (controlNum - 1), spec.unmap(val).linlin(*cal[[2, 3, 0, 1]]).asInteger) 
	}
	
	getAllValues { ^valueArray.collect({|val, i| spec.map(val.linlin(*calibrationRanges[i]))}) }
	
	// 32 faders
	setAllValues {|array|
		addr.sendMsg("/MF", *(array.collect({|val, i| spec.unmap(val).linlin(*calibrationRanges[i][[2, 3, 0, 1]]).asInteger})))
	}

	setLED {|controlNum, colour|
		addr.sendMsg("/mfLED/" ++ (controlNum - 1), colour)
	}
	
	setAllLED {|colour|
		addr.sendMsg("/mfLED/0", *(colour ! 32))
	}
	
	// perhaps this should be more generalised and named something else like 'preset'
	mappings {
		^IdentityDictionary[\ctrlVals->this.getAllValues];
	}
	
	mappings_ {|mappings|
		mappings = mappings ? ();
		this.setAllValues(mappings[\ctrlVals]);
	}
	
	// this has no labels
	setLabel { |fader, name| this.shouldNotImplement(thisMethod) }
	
	getLabel { |fader| ^this.shouldNotImplement(thisMethod) }
	
	getAllLabels { ^this.shouldNotImplement(thisMethod) }
	
	setAllLabels { |array| this.shouldNotImplement(thisMethod)}
	
	acceptsAutomation { ^true }
	
	initFromArchive { this.startListening }
	
	asTextArchive { 
		var arch;
		this.stopListening; 
		arch = super.asTextArchive;
		this.startListening;
		^arch	
	}

}

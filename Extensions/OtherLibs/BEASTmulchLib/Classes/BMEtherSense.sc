////// ************ port is 57120 or -8416 on Ethersense
// Open EtherSense MaxPlay. Choose interface manually, 10.0.0.2 port -8416
// Set send rate to 20 ms

// valueArray holds the controller value in its native form
// setValue should convert to 0-1 and send to the bus 
BMEtherSense : BMAbstractController {
	
	var <addr, responders, busBoard2Index;
	
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
			addr: [NetAddr, "0.0.0.0", "IP Address"]
		); 
	}
	
	*humanName {  ^"EtherSense"  }
	
	init { |argaddr, argname, argserver|
		addr = argaddr;
		name = argname;
		server = argserver;
		responders = Array.newClear(2);
		numControls = 32;
		valueArray = Array.fill(2, {0 ! 16});
		bus = Bus.control(server, numControls);
		busIndex = bus.index;
		busBoard2Index = busIndex + 16; // save an add every message
		spec = [0, 1, 'cos', 0.0].asSpec; // map from normalised to curve
		this.startListening;
		allControllers[name] = this;
	}
	
	startListening { 
		// do updates directly here to minimize dispatch
		responders[0] = OSCresponderNode(addr, '/Ethersense01/Card01', { arg time, resp, msg; 
			var values;
			values = msg.copyToEnd(1);
			server.sendMsg("/c_setn", busIndex, 16, *(values.collect({|val, i| 
				spec.map(val.linlin(0, 65535, 0.0, 1.0))
			})));
			valueArray[0] = values;
			this.changed(\faderVal);
		}).add;
		responders[1] = OSCresponderNode(addr, '/Ethersense01/Card02', { arg time, resp, msg; 			var values;
			values = msg.copyToEnd(1);
			server.sendMsg("/c_setn", busBoard2Index, 16, *(values.collect({|val, i| 
				spec.map(val.linlin(0, 65535, 0.0, 1.0))
			})));
			valueArray[1] = values;
			this.changed(\faderVal);
		}).add;
	}
	
	stopListening { responders.do(_.remove); responders = Array.newClear(2); }
	
	// assumes fader 1 = 1 not 0
	// returns value between 0 and 1
	getVal { |controlNum| ^spec.map(valueArray[controlNum -1].linlin(0, 65535, 0.0, 1.0)) }
	
	setVal { this.shouldNotImplement(thisMethod) }
	
	getAllValues { 
		^valueArray.flat.collect({|val, i| spec.map(val.linlin(0, 65535, 0.0, 1.0))}) 
	}
	
	setAllValues {|array| this.shouldNotImplement(thisMethod) }
	
	mappings {
		^nil
	}
	
	mappings_ {|mappings|	}
	
	// this has no labels
	setLabel { |fader, name| this.shouldNotImplement(thisMethod) }
	
	getLabel { |fader| ^this.shouldNotImplement(thisMethod) }
	
	getAllLabels { ^this.shouldNotImplement(thisMethod) }
	
	setAllLabels { |array| this.shouldNotImplement(thisMethod)}
	
	initFromArchive { this.startListening }
	
	asTextArchive { 
		var arch;
		this.stopListening; 
		arch = super.asTextArchive;
		this.startListening;
		^arch	
	}

}

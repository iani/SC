BMMIDIPort {
	classvar <ports, initialised = false;
	var <name, <inuid, <outuid, <device, <outport;
	
	*initClass {
		ports = IdentityDictionary.new;
	}
	
	*init {
		if(MIDIClient.initialized.not,{ MIDIIn.connectAll });
		MIDIClient.sources.do({ |source| 
			var name, dest, destuid;
			name = source.name;
			dest = MIDIOut.findPort(source.device, source.name);
			dest.notNil.if({ destuid = dest.uid; });
			this.new(name, source.uid, destuid, source.device, MIDIClient.destinations.indexOf(dest));
		});
		// sometimes only an out
		MIDIClient.destinations.do({ |dst, i|
			var name;
			name = dst.name;
			if(ports[name.asSymbol].isNil {
				this.new(name, nil, dst.uid, dst.device, i);
			});
		}); 
		initialised = true;
	}
	
	//private
	*new {|name, inuid, outuid, device, outport|
		^super.newCopyArgs(name.asSymbol, inuid, outuid, device, outport).init;
	}
	
	init {
		ports[name] = this;
	}
	
	initFromArchive {
		var port;
		initialised.not.if({
			"You must call BMMIDIPort:init before restoring instances from an archive. Initialising now.".warn;
			this.class.init;
		});
		port = ports[name.asSymbol];
		port.notNil.if({ ^this }, {
			port = ports[MIDIClient.sources.first.name.asSymbol];
			("MIDIPort" + name.asCompileString + "could not be found. Using" + port.name + "instead.").warn;
			name = port.name;
			inuid = port.inuid;
			outuid = port.outuid;
			device = port.device;
			outport = port.outport;
		});
	}	

	// post pretty
	printOn { arg stream; stream << this.class.name << "(" <<* [name, device] << ")" }


}
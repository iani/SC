
/* 

store synthdefs in a global pool and make them available to synths on any server by sending them to the server at boot time, before starting any synth.

Does not store separate synthdef copies for different servers, just one global pool. 

SynthDefs created while the server is running are sent immediately. ServerReady ensures that they will be sent before starting any further instance of SynthResource. 

*/

Udef {
	classvar <all;
	classvar <>loadPath;	// used by SynthDefs to enable storing of path of file that created me
	classvar <>add2AllServers = true;	// if true, all Udefs are shared with all Servers
	var <def, <name;
	var <path; // enable opening the file that contains the Udef def, see SynthDefs quark
	
	*initClass {
		all = MultiLevelIdentityDictionary.new;
		StartUp add: this;
	}

	*doOnStartUp {
		this.addMenu;
	}

	*menuItems {
		^[
			CocoaMenuItem.addToMenu("Code", "SynthDef list", ["f", true, false], {
				this.browse;
			}),
			CocoaMenuItem.addToMenu("Code", "Browse SynthDescs", ["f", true, true], {
				SynthDescLib.global.browse;
			}),
		]	
	}

	*fromFunc { | func, rates, prependArgs, outClass=\Out, fadeTime, name, server |
		^super.new.initFromFunc(func, rates, prependArgs, outClass, fadeTime, name, server);
	}

	initFromFunc { | func, rates, prependArgs, outClass=\Out, fadeTime, argName, server |
		this.addDef(func.asSynthDef(rates, prependArgs, outClass, fadeTime, argName), server);
		path = loadPath;
	}

	*new { | name, ugenGraphFunc, rates, prependArgs, variants, metadata, server |
		^super.new.init(name, ugenGraphFunc, rates, prependArgs, variants, metadata, server);
	}

	init { | argName, ugenGraphFunc, rates, prependArgs, variants, metadata, server |
		this.addDef(SynthDef(argName, ugenGraphFunc, rates, prependArgs, variants, metadata), server);
		path = loadPath;
	}

	addDef { | argDef, server |
		def = argDef;
		def.add;	// make available for browsing with SynthDescLib.global.browse.
		name = def.name.asSymbol;
		server = server.asTarget.server;
		all.putAtPath([server, argDef.name.asSymbol], this);
		ServerPrep(server).addDef(this);
	}
	
	play { | args, target, addAction = \addToHead |
		^name.play(args, target, addAction); // (name, args, target, addAction);
	}

	// using generic different name to be also used by BufferResource
	sendTo { | server | def.send(server) }

	openDefFile {
		Document.open(path).front;
	}
	
	*at { | server | ^this.onServer(server) }
	*onServer { | server |
		if (add2AllServers) {
			^all.leaves.flat.asSet.asArray; // prevent doubles
		};
		server = server ? Server.default;
		if (all.atPath(server).isNil) { ^[] };
		^all.leaves(server);
	}
	
	*named { | defname, server |
		^all.at(server ? Server.default, defname);
	}
	
	*browse { | server |
		var listwin, deflist;
		server = server ?? { Server.default };
		deflist = this.onServer(server);
		listwin = ListWindow('SynthDefs', nil, {
			deflist.sort({ | a, b | a.name < b.name }) collect: { | d | 
				d.name->{
					Document.current.string_(
						d.name.asString,
						Document.current.selectedRangeLocation, 0
					);
					{ listwin.close }.defer(0.1);
				}
			};
		});
	}
	
	
}

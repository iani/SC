Conductor : Environment {
	classvar <>specs;
	var <>valueKeys, <path;
	var <>gui;			// defines gui display of conductor in windows
	var <>player;
	var <>preset;
	
	*initClass {
		StartUp.add {
			Conductor.specs = IdentityDictionary.new;
			[
			// set up some ControlSpecs for common mappings
			// you can add your own after the fact.
			
			\unipolar -> ControlSpec(0, 1),
			\bipolar -> ControlSpec(-1, 1, default: 0),

			\freq -> ControlSpec(20, 20000, \exp, 0, 440, units: " Hz"),
			\lofreq -> ControlSpec(0.1, 100, \exp, 0, 6, units: " Hz"),
			\midfreq -> ControlSpec(25, 4200, \exp, 0, 440, units: " Hz"),
			\widefreq -> ControlSpec(0.1, 20000, \exp, 0, 440, units: " Hz"),
			\phase -> ControlSpec(0, 2pi),
			\rq -> ControlSpec(0.001, 2, \exp, 0, 0.707),

			\audiobus -> ControlSpec(0, 127, step: 1),
			\controlbus -> ControlSpec(0, 4095, step: 1),
			\in -> ControlSpec(0, 4095, step: 1),
			\fin -> ControlSpec(0, 4095, step: 1),

			\midi -> ControlSpec(0, 127, default: 64),
			\midinote -> ControlSpec(0, 127, default: 60),
			\midivelocity -> ControlSpec(1, 127, default: 64),
			
			\dbamp -> ControlSpec(0.ampdb, 1.ampdb, \db, units: " dB"),
			\amp -> ControlSpec(0, 1, \amp, 0, 0),
			\boostcut -> ControlSpec(-20, 20, units: " dB",default: 0),
			\db -> ControlSpec(-100, 20, default: -20),
			
			\pan -> ControlSpec(-1, 1, default: 0),
			\detune -> ControlSpec(-20, 20, default: 0, units: " Hz"),
			\rate -> ControlSpec(0.125, 8, \exp, 0, 1),
			\beats -> ControlSpec(0, 20, units: " Hz"),
			\ratio -> ControlSpec(1/64, 64, \exp, 0, 1),
			
			\delay -> ControlSpec(0.0001, 1, \exp, 0, 0.3, units: " secs"),
			\longdelay -> ControlSpec(0.001, 10, \exp, 0, 0.3, units: " secs"),

			\fadeTime -> ControlSpec(0.001, 10, \exp, 0, 0.3, units: " secs")
			
		].do { | assoc | specs.add(assoc) };
	 }
	}
	*make { arg func; 
		var obj, args, names;
		obj = this.new;
		^obj.make(func)
	}
	
	*new { ^super.new.init }
	
	init {
		gui = ConductorGUI(this, #[ ]);
		this[\player] = player = ConductorPlayer(this);
	}
			
	make { arg func; 
		var obj, args, names;


		#args, names = this.makeArgs(func);
		valueKeys = valueKeys ++ names;
		gui.keys_( gui.keys ++ names);
		this.usePresets;
		super.make({func.valueArray(this, args)});
	}

	*makeCV { | name, value |
		^CV(specs[name.asString.select{ | c | c.isAlpha}.asSymbol], value)
	}
	makeArgs { arg func;
		var argList, size, names, argNames;
		var theClassName, name, obj;
		
		size = func.def.argNames.size;
		argList = Array(size);
		argNames = Array(size);
		names = func.def.argNames;
		// first arg is Event under constructions, subsequent are CVs or instances of other classes
		if (size > 1, {
			1.forBy(size - 1, 1, { arg i;
				name = names[i];
				argNames = argNames.add(name);
				theClassName = func.def.prototypeFrame.at(i);
				if (theClassName.notNil) {
					obj = theClassName.asClass.new;
				} {
					obj = Conductor.makeCV(name)
				};
				this.put(name,obj);
				argList = argList.add(obj);
			});
		});
		^[argList, argNames];
		
	}

// saving and restoring state 
	getFile { arg argPath; var file, contents;
		if (File.exists(argPath)) {
			path = argPath;
			file = File(path,"r"); 
			contents = file.readAllStringRTF;
			file.close;
			^contents;
		} {
			(argPath + "not found").postln;
			^nil
		}
	}
	
	putFile { | vals, argPath | 
		path = argPath ? path;
		File(path,"w").putAll(vals).close;
	}

	load { | argPath |
		var v;
		if (argPath.isNil) {
			File.openDialog(nil, { arg path; 
				v = this.getFile(path);
				this.value_(v.interpret)
			});
		} {
			v = this.getFile(argPath);
			this.value_(v.interpret)
		};
	}
	
	save { | path |
		if (path.isNil) {
			File.saveDialog(nil, nil, { arg path; 
				this.putFile(this.value.asCompileString, path)
			});
		} {
			this.putFile(this.value.asCompileString, path)
		};

	}

	path_ { | path |
		this.load(path);
	}

// gui display of file saving
	noSettings { this[\settings] = nil; this[\preset] = nil; }
	
	useSettings { 
		this[\settings] = ConductorSettingsGUI(this);
		this[\preset] = nil;
	}
	
	usePresets { 
		this[\settings] = ConductorSettingsGUI(this);
		this[\preset] = preset =  CVPreset.new; 
		this.presetKeys_(valueKeys);		
	}
	
	useInterpolator { 
		this[\settings] = ConductorSettingsGUI(this);
		this[\preset] = preset =  CVInterpolator.new; 
		this.presetKeys_(valueKeys);
		this.interpKeys_(valueKeys);
	}
	
// interface to default preset and interpolator

	presetKeys_ { | keys, argPreset |
		argPreset = argPreset ? preset;
		preset.items = keys.collect { | k | this[k] };
	}
	
	interpKeys_ { | keys, argPreset |
		argPreset = argPreset ? preset;
		argPreset.interpItems = keys.collect { | k | this[k] };
	}

	input {  var keys;
		if (this[\preset].notNil) { keys = #[preset] };
		^(valueKeys ++ keys).collect { | k| [k, this[k].input ]  }  }
		
	input_ { | kvs | kvs.do { | kv| this[kv[0]].input_(kv[1]); kv[0]; } }
	
	value {  ^(valueKeys ++ #[preset]).collect { | k| [k, this[k].value ]  }  }
	
	value_ { | kvs | kvs.do { | kv| this[kv[0]].value_(kv[1]); kv[0]; } }
	
//gui interface
	show { arg argName, x = 128, y = 64, w = 900, h = 160;
		^gui.show(argName, x, y, w, h);
	}

	draw { | win, name, conductor|
		gui.draw (win, name, conductor)
	}
	
	
// play/stop/pause.resume
	stop {
		player.stop;
 	}
	
	play { 
		player.play;		
	}
 

	pause { 
		player.pause; 
	}

	resume { 	
		player.resume; 
	}

	name_ { | name | player.name_(name) }
	
	name { ^player.name }
	
//player interface

	add { | object |
		player.add(object)
	}
	
	action_ { | playFunc, stopFunc, pauseFunc, resumeFunc |
		this.add ( ActionPlayer(playFunc, stopFunc, pauseFunc, resumeFunc ) )
	}

	buffer_ { | ev| 
		ev.parent = CVEvent.bufferEvent;
		this.add(ev);
	}
	
	controlBus_ { | ev, cvs|
		ev[\cvs] = cvs;
		ev.parent = CVEvent.controlBusEvent;
		this.add(ev)
	}

	synth_ { | ev, cvs|
		ev[\cvs] = cvs;
		ev.parent = CVEvent.synthEvent;
		this.add(ev)
	}

	synthDef_ { | function, cvs, ev|
		var name;
		name = function.hash.asString;
		SynthDef(name, function).store;
		ev = ev ? ();
		ev	.put(\instrument, name)
			.put(\cvs, cvs);
		ev.parent_(CVEvent.synthEvent);
		this.add(ev);
		^ev
	}

	group_ { | ev, cvs|
		ev[\cvs] = cvs;
		ev.parent = CVEvent.groupEvent;
		this.add(ev)
	}
	
	task_ { |func, clock, quant|
		this.add(TaskPlayer(func,clock, quant));
	}
	
	pattern_ { | pat, clock, event, quant |
		this.add(PatternPlayer(pat, clock, event, quant) )
	}

		
	nodeProxy_ { | nodeProxy, args, bus, numChannels, group, multi |
		this.add(NodeProxyPlayer(nodeProxy, args, bus, numChannels, group, multi) )
	}

		
}

// Classes for implementing 'Plugins' for processing audio
// Plugins here are mono

BMPluginSpec {
	classvar <specs, defaultGuiFunc;
	var <name, <ugenGraphFunc, <specsDict, guiFunc, <>presets, <description, <defaultAttributes;
	var <setupFunc, <cleanupFunc;
	
	*new {|name, ugenGraphFunc, specsDict, guiFunc, presets, description, defaultAttributes, setupFunc, cleanupFunc|
		^super.new.init(name, ugenGraphFunc, specsDict, guiFunc, presets, description, 
			defaultAttributes, setupFunc, cleanupFunc);
	}
	
	init {|argname, argugenGraphFunc, argspecsDict, argguiFunc, argpresets, argdescription, 
		argattributes, argsetupFunc, argcleanupfunc|
		name = argname.asSymbol;
		ugenGraphFunc = argugenGraphFunc;
		specsDict = argspecsDict ? ();
		guiFunc = argguiFunc;
		presets = argpresets ? ();
		description = argdescription ? "";
		defaultAttributes = argattributes ?? { IdentityDictionary.new };
		// by default db specs are converted to linear amp in the gui
		defaultAttributes[\usesLinearAmp].isNil.if({
			defaultAttributes[\usesLinearAmp] = true;
		});
		setupFunc = argsetupFunc;
		cleanupFunc = argcleanupfunc;
		this.class.specs[name] = this;
	}
	
	*initClass {
		// define some plugin specs
		StartUp.add({ 
			specs = IdentityDictionary.new;
			BMPluginSpec('Highpass', 				// name
				{|plugin, input, freq| 	// ugenGraphFunc
					HPF.ar(input, freq);
				}, 								
				(freq: \freq.asSpec),				// specsDict
				nil, 							// default GUI
				(atcs: (freq: 80), tweeters: (freq: 10000)), // presets
				"2nd Order Butterworth Highpass Filter -12db/Oct"
			);
			BMPluginSpec('Lowpass', 				// name
				{|plugin, input, freq| 	// ugenGraphFunc
					LPF.ar(input, freq);
				}, 								
				(freq: \freq.asSpec),				// specsDict
				nil, 							// default GUI
				('very distants': (freq: 4000)), // presets
				"2nd Order Butterworth Lowpass Filter -12db/Oct"
			);
			BMPluginSpec('Bandpass', 				// name
				{|plugin, input, freq, rq| 
					BPF.ar(input, freq, rq);
				}, 								
				(freq: \freq.asSpec, rq: \rq.asSpec.units = " 1/Q"),	
				nil, 						// default GUI
				nil, // no presets
				"2nd Order Butterworth Bandpass Filter"
			);
			BMPluginSpec('Kill DC', 				// name
				{|plugin, input| 	// ugenGraphFunc
					LeakDC.ar(input);
				}, 								
				description: "Cuts through that greasy DC buildup..."
			);
			BMPluginSpec('Delay', 				// name
				{|plugin, input, delayTime| 
					DelayC.ar(input, 2, delayTime);
				},
				(delayTime: ControlSpec(0.0001, 1, \linear, 0, 0.5, units: " secs")), 
				description: "Simple Delay with Cubic Interpolation; 1 second maximum"
			);
			BMPluginSpec('Distance Compensate', 				// name
				{|plugin, input, delayTime| 
					DelayC.ar(input, 2, delayTime);
				},
				(delayTime: ControlSpec(0.0001, 1, \linear, 0, 0.5, units: " secs")), 
				description: "Automatically added Delay with Cubic Interpolation; 1 second maximum"
			);
			BMPluginSpec('FreeVerb', 				// name
				{|plugin, input, mix, roomSize, hfDamp| 
					FreeVerb.ar(input, mix,  roomSize,  hfDamp);
				},
				(
					mix: ControlSpec(0, 1, \linear, 0, 0.25, units: ""),
					roomSize: ControlSpec(0, 1, \linear, 0, 0.5, units: ""),
					hfDamp: ControlSpec(0, 1, \linear, 0, 0.5, units: "")
				), 
				description: "The classic open source Schroeder/Moorer reverb"
			);
			BMPluginSpec('Compander', 				// name
				{|plugin, input, thresh, slopeBelow, slopeAbove, 
				clampTime, relaxTime| 
					Compander.ar(input, input, thresh, slopeBelow, slopeAbove, 
				clampTime, relaxTime);
				},
				(
					thresh: ControlSpec(0.05, 1, \linear, 0, 0.5, units: ""),
					slopeBelow: ControlSpec(0, 10, \linear, 0, 0.5, units: ""),
					slopeAbove: ControlSpec(0, 10, \linear, 0, 0.5, units: ""),
					clampTime: ControlSpec(0, 1, \linear, 0, 0.01, units: "secs"),
					relaxTime: ControlSpec(0, 1, \linear, 0, 0.01, units: "secs")
				), // specsDict
				nil, 							// default GUI
				('noise gate': (thresh: 0.5, slopeBelow: 10, slopeAbove: 1, clampTime: 0.01, relaxTime: 0.01),
				'compressor': (thresh: 0.5, slopeBelow: 1, slopeAbove: 0.5, clampTime: 0.01, relaxTime: 0.01),
				'limiter': (thresh: 0.5, slopeBelow: 1, slopeAbove: 0.1, clampTime: 0.01, relaxTime: 0.01),
				'sustainer': (thresh: 0.5, slopeBelow: 0.1, slopeAbove: 1, clampTime: 0.01, relaxTime: 0.01)
				), // presets
				description: "General purpose (hard-knee) dynamics processor"
			);
			BMPluginSpec('3 Band EQ',
				{|plugin, input, lowFreq, lowGain, midFreq, midrq, midGain
					hiFreq, hiGain| 
					var eqchain;
					eqchain = BLowShelf.ar(input, lowFreq, 1, lowGain);
					eqchain = BPeakEQ.ar(eqchain, midFreq, midrq, midGain);
					BHiShelf.ar(eqchain, hiFreq, 1, hiGain);
				}, 								
				(
					lowFreq: ControlSpec(20, 20000, 'exp', 0, 100, " Hz"),
					lowGain: \boostcut.asSpec,
					midFreq: ControlSpec(20, 20000, 'exp', 0, 1000, " Hz"),
					midGain: \boostcut.asSpec,
					midrq: \rq.asSpec.units = " 1/Q",
					hiFreq: ControlSpec(20, 20000, 'exp', 0, 6000, " Hz"),
					hiGain: \boostcut.asSpec
				),	
				nil, 						// default GUI
				nil, // no presets
				"3 Band EQ based on the BEQSuite. A low shelf, mid parametric, and high shelf implemented with cascading Second Order Section (Biquad) filters."
			);
		// read application directory for source code files of user plugins specs
		// or maybe in app
		});
		defaultGuiFunc = {|plugin|
			var numSliders, spec, specsDict, window, presetMenu, sliders;
			spec = plugin.spec;
			specsDict = plugin.specsDict;
			numSliders = specsDict.size;
			window = SCWindow.new("Plugin:" + spec.name, 
				Rect(300, 300, 552, (numSliders + 1) * 24 + 24), false); // 508
			window.view.decorator = FlowLayout(window.view.bounds);
			window.view.background = Color.rand.alpha_(0.3);
			sliders = ();
			specsDict.sortedKeysValuesDo({|key, cspec|
				var initVal;
				initVal = plugin.get(key);
				(cspec.units == " dB" && plugin.attributes[\usesLinearAmp]).if({ 
					initVal = initVal.ampdb;
				});
				sliders[key] = EZSlider.new(window, 500@20, key.asString, cspec,
					{|ez| var setVal;
						setVal = ez.value;
						(cspec.units == " dB" && plugin.attributes[\usesLinearAmp]).if({ 
							setVal = setVal.dbamp;
						});
						plugin.set(key, setVal);
					}, initVal, labelWidth: 70
				);
				sliders[key].numberView.background = Color.white.alpha_(0.4);
				SCStaticText(window, Rect(0,0,40,20)).string_(cspec.units);
			
			});
			window.view.decorator.nextLine.shift(10, 10);
			presetMenu = SCPopUpMenu(window, Rect(0, 0, 100, 20));
			presetMenu.items = ["presets", "-"] ++ spec.presets.keys;
			presetMenu.action = {
				if(presetMenu.value > 1, {
					plugin.preset_(presetMenu.items[presetMenu.value].asSymbol);
					sliders.keysValuesDo({|key, slid| 
						var newVal;
						newVal = plugin.get(key);
						(slid.controlSpec.units == " dB" 
							&& plugin.attributes[\usesLinearAmp]).if({ 
							newVal = newVal.ampdb;
						});
						slid.value = newVal;
					});
				});
			};
			window.front; // this return value is stored in the plugin's gui var
		}
	}
	
	// protect for now
	guiFunc { 
		if(GUI.id == \cocoa, {
			^guiFunc ? defaultGuiFunc 
		}, {^nil})
	}
	
}

// Class which manages resources for a plugin instance
BMPlugin {
	var <spec, <server, <attributes, <defName, <def, <specsDict;
	var <synth, <values, defaultValues, <bus, numControls, controlNames, mappings;
	var <preset;
	var gui;
	
	*new {|pluginSpecName, server, attributes|
		^super.new.init(pluginSpecName, server ? Server.default, attributes);
	}
	
	copy {
		var values, newplugin;
		values = this.values;
		newplugin = BMPlugin(this.spec.name, this.server, this.attributes);
		values.keysValuesDo({|key, val| newplugin.set(key, val)});
		^newplugin;
	}
	
	init { |argpluginSpecName, argserver, argattributes|
		spec = BMPluginSpec.specs[argpluginSpecName.asSymbol];
		spec.isNil.if({
			("Plugin spec" + argpluginSpecName + "does not exist!").warn;
			^nil;
		});
		specsDict = spec.specsDict.deepCopy;
		server = argserver;
		attributes = spec.defaultAttributes.copy;
		argattributes.notNil.if({attributes.putAll(argattributes)}); // local settings override
		spec.setupFunc.value(this);
		this.makeDef;
		values = ();
		controlNames = ();
		def.allControlNames.reject({|cn| (cn.name == \i_in) || (cn.name == \cfgate)}).do({|cn| 
			var size, startVal, controlspec;
			size = cn.defaultValue.size;
			controlspec = specsDict[cn.name];
			// take defaults from the control name if no spec supplied. Hmm... maybe not?
			controlspec.isNil.if({Error("No spec for Control:" + cn.name).throw; });
			startVal = controlspec.default;
			(controlspec.units == " dB" && attributes[\usesLinearAmp]).if({ 
				startVal = startVal.dbamp;
			});
			if(size > startVal.size, {startVal = startVal ! size }); // not sure about this
			values[cn.name] = startVal;
			controlNames[cn.name] = cn;
		});
		defaultValues = values.deepCopy;
		numControls = def.controls.size; 
		bus = Bus.control(server, numControls); // this is two larger than it needs to be
		controlNames.keysValuesDo({|key, cn| 
			var value;
			value = values[key];
			server.sendBundle(nil,["/c_setn", bus.index + cn.index, 
				max(value.size, 1)] ++ value);
		});
		mappings = controlNames.values.collectAs({|cn| 
			[cn.name, ("c" ++ (bus.index + cn.index)).asSymbol];
		}, Array).flat;
	}
	
	makeDef {
		defName = spec.name; 
		if(attributes.notNil, { defName = defName ++ "-" ++ UniqueID.next});
		def = SynthDef(defName, {arg i_in, cfgate = 1;
			var input, out;
			input = In.ar(i_in);
			out = SynthDef.wrap(spec.ugenGraphFunc, nil, [this, input]);
			XOut.ar(i_in, 
				EnvGen.kr(Env.asr(BMOptions.crossfade, 1, BMOptions.crossfade), cfgate, 
					doneAction: 2),
				out;
			);
		});
		
	}
	
	set {|key, value|
		var cn;
		cn = controlNames[key];
		cn.notNil.if({
			values[key] = value;
			server.sendBundle(nil,["/c_setn", bus.index + cn.index, 
				max(value.size, 1)] ++ value);
		}, {("Plugin " ++ spec.name ++ "has no Control named " ++ key).warn });
	}
	
	get {|key|
		var cn;
		cn = controlNames[key];
		cn.notNil.if({
			^values[key];
		}, {("Plugin " ++ spec.name ++ "has no Control named " ++ key).warn; ^nil; });
	}
	
	debug {
		bus.getn(numControls, {|array|
			"Control Bus values:".postln;
			controlNames.keysValuesDo({|key, cn| 
				cn.name.postln;
				"\t".post;
				"clientside: ".post;
				values[cn.name].post;
				" actual: ".post;
				array[cn.index].postln;
			});
			synth.notNil.if({
				("\n" ++ spec.name + "plugin synth trace:").postln;
				synth.trace;
			});
		});
		^("Debugging" + spec.name + "Plugin:\n");
	}
	
	preset_{|presetname|
		var psdict;
		psdict = spec.presets[presetname];
		psdict.notNil.if({
			preset = presetname;
			psdict = defaultValues.copy.putAll(psdict); // use defaults for any non-specified
			psdict.keysValuesDo({|key, val| this.set(key, val)});
		}, {("Plugin " ++ spec.name ++ " has no preset named " ++ presetname).warn });
	}
	
	makeSynth {|in, target, addAction=\addToTail|
		(target.asTarget.server != server).if({
			Error("Target server does not match Plugin server.").throw;
		});
		synth.notNil.if({ synth.set(\cfgate, 0); });
		synth = def.play(target, [i_in: in] ++ mappings, addAction);
	}
	
	release { 
		synth.set(\cfgate, 0); 
		synth = nil; bus.free; 
		bus = nil;
		gui.notNil.if({ gui.close });
		spec.cleanupFunc.value(this);
	} // I'm a lame duck...
	
	gui {
		gui.isNil.if({
			gui = spec.guiFunc.value(this);
			gui.onClose = gui.onClose.addFunc({ gui = nil });
		}, {
			gui.front;
		});
	}
	
	// post pretty
	printOn { arg stream; stream << this.class.name << "(" <<* [spec.name] << ")" }


}
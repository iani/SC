BMTrimPluginsRack : BMAbstractAudioChainElement {
	
	var strips, autoPlugins, distanceCompPlugins;
	
	*new { |ins, target, addAction = \addToTail, name|
		^super.new.init(ins.asBMInOutArray, target, addAction, name);
	}
	
	init {|argins, argtarget, argaddAction, argname|
		this.initNameAndTarget(argtarget, argaddAction, argname);
		ins = argins;
		outs = argins;
		inNames = ins.keys;
		outNames = outs.keys;
		strips = IdentityDictionary.new;
		inNames.do({|chanName|
			strips[chanName] = BMTrimPluginsStrip(group, ins[chanName]);
		});
	}
	
	mappings { ^strips.collect({|strip, name| strip.mappings});}
	
	mappings_ { |dict| 
		dict = dict ? ();
		dict.keysValuesDo({|name, mappings| 
			strips[name].notNil.if({
				strips[name].mappings_(mappings);
			},{ error("Plugin Strip:" + name + "not defined.") });
		});
	}
	
	at { |channel| ^strips[channel] }
	
	free {
		strips.do{| pluginsStrip | 
			pluginsStrip.plugins.do{| plugin, i | 
				pluginsStrip.removePlugin(i) 
			} 
		};
		SystemClock.sched(BMOptions.crossfade, { group.free; group = strips = nil; allChainElements[name] = nil; });
		
	}

	
	////// Automated Stuff
	
	// add delays to eliminate precedence effect
	// assumes distances are in meters
	delayCompensateDistance { |bool = true|
		var rads, diff, farthest, plugin;
		
		(bool && distanceCompPlugins.isNil).if({
			rads = ins.select({|in| in.isBMSpeaker})
				.collectAs({|speaker| speaker.value.rad }, Array);
			farthest = rads.maxItem;
			ins.do({|speaker| 
				speaker.isBMSpeaker.if({
					diff = farthest - speaker.value.rad;
					if(diff > 0, { // farthest uncompensated
						// speed of sound 344 m/s at 21 degrees C in dry air
						plugin = BMPlugin('Distance Compensate').set(\delayTime, diff / 344);
						this[speaker.name].addPlugin(plugin);
						distanceCompPlugins = distanceCompPlugins.add((speaker.name) -> plugin); 
					});
				});
			});
		}, {
			distanceCompPlugins.do({|plgin| this[plgin.key].removePlugin(plgin.value) });
			distanceCompPlugins = nil;
		});	
	}
	
	// auto add plugins by speaker spec
	// requires a plugin spec name and a preset
	autoPlugins { |bool = true|
		var plugin;
		(bool && autoPlugins.isNil).if({
			ins.do({|speaker|
				speaker.isBMSpeaker.if({
					speaker.spec.plugins.do({|plgin| 
						// name, preset
						plugin = BMPlugin(plgin[0]);
						plugin.notNil.if({
							plugin.preset_(plgin[1]);
							this[speaker.name].addPlugin(plugin);
							autoPlugins = autoPlugins.add((speaker.name) -> plugin);
						}); 
					});
				});
			});
		}, {
			autoPlugins.do({|plgin| this[plgin.key].removePlugin(plgin.value) });
			autoPlugins = nil;
		});
	}
	
	// balance speakers
	autoTrim { 
		ins.do({|speaker| 
			speaker.isBMSpeaker.if({
				this[speaker.name].trim = speaker.autoTrim;
			});
		});
	}
}

BMTrimPluginsStrip {
	var <trim = 0, trimSynth, <plugins, <target, <server, <group, <input;
	
	*new {|target, input|
		^super.new.init(target, input);
	}
	
	clear { 
		plugins = List.new;
		this.makeNodes;
	}
	
	init {|argtarget, arginput|
		target = argtarget.asGroup;
		server = target.server;
		input = arginput;
		
		plugins = List.new;
		target.server.makeBundle(nil, {
			this.sendDef;
			server.sync;
			this.makeNodes; // first time only trim...
		});
	}
	
	mappings { 
		var dict;
		dict = IdentityDictionary.new;
		dict[\trim] = trim;
		dict[\plugins] = plugins.collect({|plugin|
			// could be a problem if pluginspec changes in the meantime
			[plugin.spec.name, plugin.attributes, plugin.values];
		}); // these are in order
		^dict;
	}
	
	mappings_ { |dict| 
		this.plugins.do({|plugin| plugin.release;});
		plugins = List.new;
		dict = dict ? ();
		this.trim_(dict[\trim]);
		dict[\plugins].do({|pluginArray|
			var plugin;
			plugin = BMPlugin(pluginArray[0], server, pluginArray[1]);
			plugin.notNil.if({
				this.addPlugin(plugin);
				pluginArray[2].keysValuesDo({|k, v| plugin.set(k, v)});
			});
		});
		this.changed;
	}
	
	sendDef {
		SynthDef("BMTrim", {arg in, trim = 0, gate = 1;
			// XFade in new scaled value, crossfade out when freeing so no clicks
			XOut.ar(in, 
				EnvGen.kr(Env.asr(BMOptions.crossfade, 1, BMOptions.crossfade), gate, 
					doneAction: 2),
				In.ar(in, 1) * trim
			);
		}).send(target.server);
	}
	
	target_{|argtarget|
		target = argtarget.asGroup; 
		(target.asTarget.server != server).if({
			Error("Target server does not match Plugins' server.").throw;
		});
	
	}
	
	makeNodes { 
		server.makeBundle(nil, {
			group = Group.new(target);
			trimSynth = Synth.tail(group, "BMTrim", [in: input, trim: trim.dbamp]);
			plugins.do({|plgin|
				plgin.makeSynth(input, group, \addToTail);
			});
		});
		this.changed;
	}
	
	addPlugin {|plugin|
		plugin.notNil.if({
			plugins.add(plugin);
			plugin.makeSynth(input, group, \addToTail);
			// added at end, no need to reset order on server
			this.changed;
		});
	}
	
	removePlugin {|indexOrPlugin|
		var toBeRemoved, index;
		indexOrPlugin.isInteger.not.if({ index = plugins.indexOf(indexOrPlugin) }, {
			index = indexOrPlugin;
		});
		(index.notNil && (index < plugins.size)).if({ 
			toBeRemoved = plugins.removeAt(index);
			toBeRemoved.release; // free synth and resources
			// just removed, no need to reset order on server
			this.changed;
		});
	}
	
	movePluginUp {|index|
		if(index > 0, {
			plugins.swap(index, index - 1);
			this.resetOrder;
			this.changed(\moveUp);
		});
	}

	movePluginDown {|index|
		if(index < (plugins.size -1), {
			plugins.swap(index, index + 1);
			this.resetOrder;
			this.changed(\moveDown);
		});
	}
	
	resetOrder {
		server.makeBundle(nil, {
			trimSynth.moveToTail(group);
			plugins.do({|plgin|
				plgin.synth.moveToTail(group);
			});
		});
	}
	
	trim_ {|newTrim| //in dB
		trim = newTrim;
		trimSynth.set(\trim, trim.dbamp);
		this.changed(\trim);
	}
}

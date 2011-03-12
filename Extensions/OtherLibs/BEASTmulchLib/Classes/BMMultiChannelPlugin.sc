
BMMultichannelPluginSpec {
	classvar <specs, defaultGuiFunc;
	var <name, <ugenGraphFunc, <specsDict, guiFunc, <>presets, <description, <defaultAttributes;
	var <minInputs, <minOutputs, <maxInputs, <maxOutputs; //nil for max = unlimited
	var <setupFunc, <cleanupFunc;
	
	*new {|name, ugenGraphFunc, specsDict, guiFunc, presets, description, defaultAttributes, 
		inRange, outRange, setupFunc, cleanupFunc| // ranges are [min, max]
		^super.new.init(name, ugenGraphFunc, specsDict, guiFunc, presets, description, 
			defaultAttributes, inRange, outRange, setupFunc, cleanupFunc);
	}
	
	init {|argname, argugenGraphFunc, argspecsDict, argguiFunc, argpresets, argdescription, 
		argattributes, arginRange, argoutRange, argsetupFunc, argcleanupfunc|
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
		arginRange = arginRange ?? { [1, inf] };
		argoutRange = argoutRange ?? { [1, inf] };
		minInputs = arginRange[0];
		maxInputs = arginRange[1];
		minOutputs = argoutRange[0];
		maxOutputs = argoutRange[1];
		setupFunc = argsetupFunc;
		cleanupFunc = argcleanupfunc;
		this.class.specs[name] = this;
	}
	
	*initClass {
		// define some plugin specs
		StartUp.add({ 
			specs = IdentityDictionary.new;
			BMMultichannelPluginSpec('Equal-power Xfade Seq', // name
				{|plugin, numInputs, numOutputs, inputs, position, width| // ugenGraphFunc
					PanAz.ar(
						numOutputs, 
						inputs, 
						position * 2.0, 
						1, 
						width, 
						0.0
					);
				}, 								
				(position: [0.0, 1.0, 'lin', 0.0, 0.0].asSpec, // specsDict
				width: nil // placeholder
				),				
				nil, 							// use default GUI
				nil, 							// presets
				"Crossfade a signal through a looped sequence of channels\nOrder corresponds with order of outputs\nPosition cycles between 0 and 1\nWidth is number of channels simultaneously active, i.e. 2 = stereo", // description
				nil, 							// use defaultAttributes
				[1, 1],						// inRange
				[2, inf],						// outRange
				{|plugin|						// setup
					plugin.specsDict[\width] = [2.0, plugin.numOutputs, 'lin', 0.0,  2.0].asSpec;
				}		
			);
			
			BMMultichannelPluginSpec('Splay Stereo', // name
				{|plugin, numInputs, numOutputs, inputs, spread, center| // ugenGraphFunc
					Splay.ar(inputs, spread, 1, center);
				}, 								
				(spread: [0.0, 1.0, 'lin', 0.0, 1].asSpec,		// specsDict
				center: [-1.0, 1.0, 'lin', 0.0,  0.0].asSpec
				),				
				nil, 												// use default GUI
				nil, 							// presets
				"Spread input channels across stereo", // description
				nil, 								// use defaultAttributes
				[2, inf],						// inRange
				[2, 2]						// outRange						
			);
			BMMultichannelPluginSpec('Splay Ring', // name
				{|plugin, numInputs, numOutputs, inputs, spread, center, orientation| // ugenGraphFunc
					SplayAz.ar(numOutputs, inputs, spread, 1,2, center, orientation);
				}, 								
				(spread: [0.0, 1.0, 'lin', 0.0, 1].asSpec,		// specsDict
				center: [-1.0, 1.0, 'lin', 0.0,  0.0].asSpec,
				orientation: [0.0, 1.0, 'lin', 0.0,  0.5].asSpec
				),				
				nil, 												// use default GUI
				nil, 							// presets
				"Spread input channels around a ring\nOutputs should be in clockwise order\nOrientation 0 means centre is output 1, 1 means centre is output 2", // description
				nil, 								// use defaultAttributes
				[2, inf],						// inRange
				[2, 2]						// outRange						
			);
			BMMultichannelPluginSpec('FreeVerb Ring', // name
				{|plugin, numInputs, numOutputs, inputs, mix, room, damp, blend| // ugenGraphFunc
					inputs.collect({|chan, i|
						FreeVerb.ar(chan + (inputs.wrapAt(i + [1, -1]) * blend), 
							mix, 
							room, 
							damp
						);
					});
				}, 								
				(mix: [0.0, 1.0, 'lin', 0.0,  0.33].asSpec,		// specsDict
				room: [0.0, 1.0, 'lin', 0.0,  0.5].asSpec,
				damp: [0.0, 1.0, 'lin', 0.0,  0.5].asSpec,
				blend: [0.0, 1.0, 'lin', 0.0,  0.25].asSpec
				),				
				nil, 												// use default GUI
				nil, 							// presets
				"A very simple ring reverb using FreeVerb\n Inputs and outputs should be in clockwise order and the same size\nMixes in blend of adjacent channels",	// description
				nil, 								// use defaultAttributes
				[3, inf],								// inRange
				[3, inf]								// outRange						
			);
			BMMultichannelPluginSpec('3D VBAP Panner', 				// name
				{|plugin, numInputs, numOutputs, inputs, azimuth, elevation, spread, azimuthLag| 	// ugenGraphFunc
					VBAP.ar(numOutputs, inputs, plugin.attributes[\buffer], azimuth.circleRamp(azimuthLag), elevation, spread);
				}, 								
				(azimuth: [-180, 180, 'lin', 0.0,  0, " deg"].asSpec, 
				elevation: [-90, 90, 'lin', 0.0, 0, " deg"].asSpec, 
				spread: [0, 100, 'lin', 0.0, 2, " %"].asSpec,
				azimuthLag: [0, 1, 'lin', 0.0, 0.1, " sec"].asSpec
				),				// specsDict
				nil, 							// default GUI
				('dead ahead': (azimuth: 0, elevation:0)), // presets
				"Mono input 3D Vector Base Amplitude Panner",
				nil, 							// defaultAttributes
				nil,								// inRange
				nil,								// outRange
				{|plugin| 
					var speakers;
					speakers = plugin.outputs.collectAs({|out|
						out.isBMSpeaker.not.if({
							"VBAP output not a speaker".error;
							^false;
						});
						[out.azi, out.ele];
					}, Array);
					speakers = VBAPSpeakerArray(3, speakers);
					plugin.attributes[\buffer] = 
						Buffer.loadCollection(plugin.server, speakers.getSetsAndMatrices);
				},								// setupFunc
				{|plugin|
					plugin.attributes[\buffer].free;
				}								// cleanupFunc
			);
			BMMultichannelPluginSpec('2D VBAP Panner', 				// name
				{|plugin, numInputs, numOutputs, inputs, azimuth, spread, azimuthLag| 	// ugenGraphFunc
					VBAP.ar(numOutputs, inputs, plugin.attributes[\buffer], azimuth.circleRamp(azimuthLag), 0, spread);
				}, 								
				(azimuth: [-180, 180, 'lin', 0.0,  0, " deg"].asSpec,
				spread: [0, 100, 'lin', 0.0, 2, " %"].asSpec,
				azimuthLag: [0, 1, 'lin', 0.0, 0.1, " sec"].asSpec
				),				// specsDict
				nil, 							// default GUI
				('dead ahead': (azimuth: 0)), // presets
				"Mono input 2D Vector Base Amplitude Panner",
				nil, 							// defaultAttributes
				nil,								// inRange
				nil,								// outRange
				{|plugin| 
					var speakers;
					speakers = plugin.outputs.collectAs({|out|
						out.isBMSpeaker.not.if({
							"VBAP output not a speaker".error;
							^false;
						});
						out.azi;
					}, Array);
					speakers = VBAPSpeakerArray(2, speakers);
					plugin.attributes[\buffer] = 
						Buffer.loadCollection(plugin.server, speakers.getSetsAndMatrices);
				},								// setupFunc
				{|plugin|
					plugin.attributes[\buffer].free;
				}								// cleanupFunc
			);
			BMMultichannelPluginSpec('Stereo 3D VBAP Panner', 				// name
				{|plugin, numInputs, numOutputs, inputs, azimuth, elevation, spread, azimuthLag, 
					azimuthWidth, elevationWidth| 	// ugenGraphFunc
					var azdev, eldev;
					azdev = azimuthWidth * 0.5;
					eldev = elevationWidth * 0.5;
					Mix(VBAP.ar(numOutputs, inputs, plugin.attributes[\buffer], 
						azimuth.circleRamp(azimuthLag) + [azdev.neg, azdev], 
						elevation + [eldev.neg, eldev], spread));
				}, 								
				(azimuth: [-180, 180, 'lin', 0.0,  0, " deg"].asSpec, 
				elevation: [-90, 90, 'lin', 0.0, 0, " deg"].asSpec, 
				spread: [0, 100, 'lin', 0.0, 2, " %"].asSpec,
				azimuthLag: [0, 1, 'lin', 0.0, 0.1, " sec"].asSpec,
				azimuthWidth: [0, 360, 'lin', 0.0,  60, " deg"].asSpec,
				elevationWidth: [-180, 180, 'lin', 0.0,  0, " deg"].asSpec
				),				// specsDict
				nil, 							// default GUI
				('dead ahead': (azimuth: 0, elevation:0)), // presets
				"Stereo input 3D Vector Base Amplitude Panner",
				nil, 							// defaultAttributes
				nil,								// inRange
				nil,								// outRange
				{|plugin| 
					var speakers;
					speakers = plugin.outputs.collectAs({|out|
						out.isBMSpeaker.not.if({
							"VBAP output not a speaker".error;
							^false;
						});
						[out.azi, out.ele];
					}, Array);
					speakers = VBAPSpeakerArray(3, speakers);
					plugin.attributes[\buffer] = 
						Buffer.loadCollection(plugin.server, speakers.getSetsAndMatrices);
				},								// setupFunc
				{|plugin|
					plugin.attributes[\buffer].free;
				}								// cleanupFunc
			);
			BMMultichannelPluginSpec('B-Format Decoder', 				// name
				{|plugin, numInputs, numOutputs, inputs| // ugenGraphFunc
					// w, x, y, z
					
					BFDecode1.ar(inputs[0], inputs[1], inputs[2], inputs[3], 						plugin.attributes[\speakersCoords][0], 
						plugin.attributes[\speakersCoords][1]
					);
				}, 								
				nil,				// specsDict
				nil, 							// default GUI
				nil, // presets
				"3D B-Format Ambisonic Decoder; input order w, x, y, z",
				nil, 							// defaultAttributes
				[4, 4],							// inRange
				[2, inf],							// outRange
				{|plugin| 
					var speakers;
					var atorad = (2 * pi / 360);
					speakers = plugin.outputs.collectAs({|out|
						out.isBMSpeaker.not.if({
							"Ambisonics output not a speaker".error;
							^false;
						});
						[out.azi * atorad, out.ele * atorad];
					}, Array).flop;
					plugin.attributes[\speakersCoords] = speakers;
				},								// setupFunc
				nil								// cleanupFunc
			);
			
			BMMultichannelPluginSpec('B-Format Decoder Comp', 				// name
				{|plugin, numInputs, numOutputs, inputs| // ugenGraphFunc
					// w, x, y, z
					
					BFDecode1.ar1(inputs[0], inputs[1], inputs[2], inputs[3], 						plugin.attributes[\speakersCoords][0], 
						plugin.attributes[\speakersCoords][1],
						plugin.attributes[\maxDist],
						plugin.attributes[\speakersCoords][2]
					);
				}, 								
				nil,				// specsDict
				nil, 							// default GUI
				nil, // presets
				"3D B-Format Ambisonic Decoder; delay compensated. Input order is w, x, y, z.",
				nil, 							// defaultAttributes
				[4, 4],							// inRange
				[2, inf],							// outRange
				{|plugin| 
					var speakers;
					var atorad = (2 * pi / 360);
					speakers = plugin.outputs.collectAs({|out|
						out.isBMSpeaker.not.if({
							"Ambisonics output not a speaker".error;
							^false;
						});
						[out.azi * atorad, out.ele * atorad, out.rad];
					}, Array).flop;
					plugin.attributes[\speakersCoords] = speakers;
					plugin.attributes[\maxDist] = speakers[2].maxItem;
				},								// setupFunc
				nil								// cleanupFunc
			);
			
			BMMultichannelPluginSpec('3D Ambi Panner', // name
				{|plugin, numInputs, numOutputs, inputs, azimuth, elevation, rho, azimuthLag| // ugenGraphFunc
					var w, x, y, z;
					var atorad = (2 * pi / 360);
					#w, x, y, z = BFEncode1.ar(inputs, azimuth.circleRamp(azimuthLag) * atorad, 
						elevation * atorad, rho);
					BFDecode1.ar(w, x, y, z, plugin.attributes[\speakersCoords][0], 
						plugin.attributes[\speakersCoords][1]
					);
				}, 								
				(azimuth: [-180, 180, 'lin', 0.0,  0, " deg"].asSpec, 
				elevation: [-90, 90, 'lin', 0.0, 0, " deg"].asSpec,
				rho: [0, 4, 'lin', 0.0, 1].asSpec,
				azimuthLag: [0, 1, 'lin', 0.0, 0.1, " sec"].asSpec
				),				// specsDict
				nil, 							// default GUI
				('dead ahead': (azimuth: 0, elevation:0)), // presets
				"1st Order Mono input 3D Ambisonic Panner",
				nil, 							// defaultAttributes
				[1, 1],							// inRange
				[2, inf],							// outRange
				{|plugin| 
					var speakers;
					var atorad = (2 * pi / 360);
					speakers = plugin.outputs.collectAs({|out|
						out.isBMSpeaker.not.if({
							"Ambisonics output not a speaker".error;
							^false;
						});
						[out.azi * atorad, out.ele * atorad];
					}, Array).flop;
					plugin.attributes[\speakersCoords] = speakers;
				},								// setupFunc
				nil								// cleanupFunc
			);
			
			BMMultichannelPluginSpec('Stereo 3D Ambi Panner', // name
				{|plugin, numInputs, numOutputs, inputs, azimuth, width, elevation, rho, azimuthLag| // ugenGraphFunc
					var w, x, y, z;
					var atorad = (2 * pi / 360);
					#w, x, y, z = BFEncodeSter.ar(inputs[0], inputs[1], azimuth.circleRamp(azimuthLag) * atorad, 
						width * atorad,
						elevation * atorad, rho);
					BFDecode1.ar(w, x, y, z, plugin.attributes[\speakersCoords][0], 
						plugin.attributes[\speakersCoords][1]
					);
				}, 								
				(azimuth: [-180, 180, 'lin', 0.0,  0, " deg"].asSpec,
				width: [0, 360, 'lin', 0.0,  0, " deg"].asSpec, 
				elevation: [-90, 90, 'lin', 0.0, 0, " deg"].asSpec,
				rho: [0, 4, 'lin', 0.0, 1].asSpec,
				azimuthLag: [0, 1, 'lin', 0.0, 0.1, " sec"].asSpec
				),				// specsDict
				nil, 							// default GUI
				('dead ahead': (azimuth: 0, elevation:0)), // presets
				"1st Order Stereo input 3D Ambisonic Panner; inputs are L, R",
				nil, 							// defaultAttributes
				[2, 2],							// inRange
				[2, inf],							// outRange
				{|plugin| 
					var speakers;
					var atorad = (2 * pi / 360);
					speakers = plugin.outputs.collectAs({|out|
						out.isBMSpeaker.not.if({
							"Ambisonics output not a speaker".error;
							^false;
						});
						[out.azi * atorad, out.ele * atorad];
					}, Array).flop;
					plugin.attributes[\speakersCoords] = speakers;
				},								// setupFunc
				nil								// cleanupFunc
			);
			
			BMMultichannelPluginSpec('FMH Ambi Panner', // name
				{|plugin, numInputs, numOutputs, inputs, azimuth, elevation, rho, azimuthLag| // ugenGraphFunc
					var w, x, y, z, r, s, t, u, v;
					var atorad = (2 * pi / 360);
					#w, x, y, z, r, s, t, u, v = FMHEncode1.ar(inputs, azimuth.circleRamp(azimuthLag).neg * atorad, 
						elevation * atorad, rho);
					FMHDecode1.ar(w, x, y, z, r, s, t, u, v, 
						plugin.attributes[\speakersCoords][0], 
						plugin.attributes[\speakersCoords][1]
					);
				}, 								
				(azimuth: [-180, 180, 'lin', 0.0,  0, " deg"].asSpec, 
				elevation: [-90, 90, 'lin', 0.0, 0, " deg"].asSpec,
				rho: [0, 4, 'lin', 0.0, 1].asSpec,
				azimuthLag: [0, 1, 'lin', 0.0, 0.1, " sec"].asSpec
				),				// specsDict
				nil, 							// default GUI
				('dead ahead': (azimuth: 0, elevation:0)), // presets
				"2nd Order Mono input 3D Ambisonic Panner",
				nil, 							// defaultAttributes
				[1, 1],							// inRange
				[2, inf],							// outRange
				{|plugin| 
					var speakers;
					var atorad = (2 * pi / 360);
					speakers = plugin.outputs.collectAs({|out|
						out.isBMSpeaker.not.if({
							"Ambisonics output not a speaker".error;
							^false;
						});
						[out.azi * atorad, out.ele * atorad];
					}, Array).flop;
					plugin.attributes[\speakersCoords] = speakers;
				},								// setupFunc
				nil								// cleanupFunc
			);

			BMMultichannelPluginSpec('3D VBAP Auto Panner', 				// name
				{|plugin, numInputs, numOutputs, inputs, elevation, spread, speed| 	// ugenGraphFunc
					var azimuth;
					azimuth = LFSaw.kr(speed.reciprocal).range(-180, 180);
					VBAP.ar(numOutputs, inputs, plugin.attributes[\buffer], azimuth, elevation, spread);
				}, 								
				(speed: [0.1, 20, 'lin', 0.0,  5, " sec"].asSpec, 
				elevation: [-90, 90, 'lin', 0.0, 0, " deg"].asSpec, 
				spread: [0, 100, 'lin', 0.0, 2, " %"].asSpec
				),				// specsDict
				nil, 							// default GUI
				nil, // presets
				"Mono input 3D Vector Base Amplitude Auto Panner",
				nil, 							// defaultAttributes
				nil,								// inRange
				nil,								// outRange
				{|plugin| 
					var speakers;
					speakers = plugin.outputs.collectAs({|out|
						out.isBMSpeaker.not.if({
							"VBAP output not a speaker".error;
							^false;
						});
						[out.azi, out.ele];
					}, Array);
					speakers = VBAPSpeakerArray(3, speakers);
					plugin.attributes[\buffer] = 
						Buffer.loadCollection(plugin.server, speakers.getSetsAndMatrices);
				},								// setupFunc
				{|plugin|
					plugin.attributes[\buffer].free;
				}								// cleanupFunc
			);
			
			BMMultichannelPluginSpec('2D VBAP Auto Panner', 				// name
				{|plugin, numInputs, numOutputs, inputs, spread, speed| 	// ugenGraphFunc
					var azimuth;
					azimuth = LFSaw.kr(speed.reciprocal).range(-180, 180);
					VBAP.ar(numOutputs, inputs, plugin.attributes[\buffer], azimuth, 0, spread);
				}, 								
				(speed: [0.1, 20, 'lin', 0.0,  5, " sec"].asSpec, 
				spread: [0, 100, 'lin', 0.0, 2, " %"].asSpec
				),				// specsDict
				nil, 							// default GUI
				nil, // presets
				"Mono input 2D Vector Base Amplitude Auto Panner",
				nil, 							// defaultAttributes
				nil,								// inRange
				nil,								// outRange
				{|plugin| 
					var speakers;
					speakers = plugin.outputs.collectAs({|out|
						out.isBMSpeaker.not.if({
							"VBAP output not a speaker".error;
							^false;
						});
						out.azi;
					}, Array);
					speakers = VBAPSpeakerArray(2, speakers);
					plugin.attributes[\buffer] = 
						Buffer.loadCollection(plugin.server, speakers.getSetsAndMatrices);
				},								// setupFunc
				{|plugin|
					plugin.attributes[\buffer].free;
				}								// cleanupFunc
			);
			
			BMMultichannelPluginSpec('Stereo 3D VBAP Auto Panner', 				// name
				{|plugin, numInputs, numOutputs, inputs, elevation, spread, speed, 
					azimuthWidth, elevationWidth| 	// ugenGraphFunc
					var azdev, eldev;
					var azimuth;
					azimuth = LFSaw.kr(speed.reciprocal).range(-180, 180);
					azdev = azimuthWidth * 0.5;
					eldev = elevationWidth * 0.5;
					Mix(VBAP.ar(numOutputs, inputs, plugin.attributes[\buffer], 
						azimuth + [azdev.neg, azdev], 
						elevation + [eldev.neg, eldev], spread));
				}, 								
				(speed: [0.1, 20, 'lin', 0.0,  5, " sec"].asSpec,
				elevation: [-90, 90, 'lin', 0.0, 0, " deg"].asSpec, 
				spread: [0, 100, 'lin', 0.0, 2, " %"].asSpec,
				azimuthWidth: [0, 360, 'lin', 0.0,  60, " deg"].asSpec,
				elevationWidth: [-180, 180, 'lin', 0.0,  0, " deg"].asSpec
				),				// specsDict
				nil, 							// default GUI
				nil,
				"Stereo input Auto 3D Vector Base Amplitude Panner",
				nil, 							// defaultAttributes
				nil,								// inRange
				nil,								// outRange
				{|plugin| 
					var speakers;
					speakers = plugin.outputs.collectAs({|out|
						out.isBMSpeaker.not.if({
							"VBAP output not a speaker".error;
							^false;
						});
						[out.azi, out.ele];
					}, Array);
					speakers = VBAPSpeakerArray(3, speakers);
					plugin.attributes[\buffer] = 
						Buffer.loadCollection(plugin.server, speakers.getSetsAndMatrices);
				},								// setupFunc
				{|plugin|
					plugin.attributes[\buffer].free;
				}								// cleanupFunc
			);
			
			BMMultichannelPluginSpec('3D Ambi Auto Panner', // name
				{|plugin, numInputs, numOutputs, inputs, elevation, rho, speed| // ugenGraphFunc
					var w, x, y, z;
					var atorad = (2 * pi / 360);
					var azimuth;
					
					azimuth = LFSaw.kr(speed.reciprocal).range(-180, 180);
					#w, x, y, z = BFEncode1.ar(inputs, azimuth * atorad, 
						elevation * atorad, rho);
					BFDecode1.ar(w, x, y, z, plugin.attributes[\speakersCoords][0], 
						plugin.attributes[\speakersCoords][1]
					);
				}, 								
				(speed: [0.1, 20, 'lin', 0.0,  5, " sec"].asSpec,  
				elevation: [-90, 90, 'lin', 0.0, 0, " deg"].asSpec,
				rho: [0, 4, 'lin', 0.0, 1].asSpec
				),				// specsDict
				nil, 							// default GUI
				nil, // presets
				"1st Order Mono input 3D Ambisonic Auto Panner",
				nil, 							// defaultAttributes
				[1, 1],							// inRange
				[2, inf],							// outRange
				{|plugin| 
					var speakers;
					var atorad = (2 * pi / 360);
					speakers = plugin.outputs.collectAs({|out|
						out.isBMSpeaker.not.if({
							"Ambisonics output not a speaker".error;
							^false;
						});
						[out.azi * atorad, out.ele * atorad];
					}, Array).flop;
					plugin.attributes[\speakersCoords] = speakers;
				},								// setupFunc
				nil								// cleanupFunc
			);
			
			BMMultichannelPluginSpec('Stereo Auto 3D Ambi Panner', // name
				{|plugin, numInputs, numOutputs, inputs, width, elevation, rho, speed| // ugenGraphFunc
					var w, x, y, z;
					var atorad = (2 * pi / 360);
					var azimuth;
					
					azimuth = LFSaw.kr(speed.reciprocal).range(-180, 180);
					#w, x, y, z = BFEncodeSter.ar(inputs[0], inputs[1], azimuth * atorad, 
						width * atorad,
						elevation * atorad, rho);
					BFDecode1.ar(w, x, y, z, plugin.attributes[\speakersCoords][0], 
						plugin.attributes[\speakersCoords][1]
					);
				}, 								
				(speed: [0.1, 20, 'lin', 0.0,  5, " sec"].asSpec,
				width: [0, 360, 'lin', 0.0,  0, " deg"].asSpec, 
				elevation: [-90, 90, 'lin', 0.0, 0, " deg"].asSpec,
				rho: [0, 4, 'lin', 0.0, 1].asSpec
				),				// specsDict
				nil, 							// default GUI
				nil, // presets
				"1st Order Stereo input Auto 3D Ambisonic Panner; inputs are L, R",
				nil, 							// defaultAttributes
				[2, 2],							// inRange
				[2, inf],							// outRange
				{|plugin| 
					var speakers;
					var atorad = (2 * pi / 360);
					speakers = plugin.outputs.collectAs({|out|
						out.isBMSpeaker.not.if({
							"Ambisonics output not a speaker".error;
							^false;
						});
						[out.azi * atorad, out.ele * atorad];
					}, Array).flop;
					plugin.attributes[\speakersCoords] = speakers;
				},								// setupFunc
				nil								// cleanupFunc
			);
			
			BMMultichannelPluginSpec('FMH Ambi Auto Panner', // name
				{|plugin, numInputs, numOutputs, inputs, elevation, rho, speed| // ugenGraphFunc
					var w, x, y, z, r, s, t, u, v;
					var atorad = (2 * pi / 360);
					var azimuth;
					
					azimuth = LFSaw.kr(speed.reciprocal).range(-180, 180);
					
					#w, x, y, z, r, s, t, u, v = FMHEncode1.ar(inputs, azimuth.neg * atorad, 
						elevation * atorad, rho);
					FMHDecode1.ar(w, x, y, z, r, s, t, u, v, 
						plugin.attributes[\speakersCoords][0], 
						plugin.attributes[\speakersCoords][1]
					);
				}, 								
				(speed: [0.1, 20, 'lin', 0.0,  5, " sec"].asSpec, 
				elevation: [-90, 90, 'lin', 0.0, 0, " deg"].asSpec,
				rho: [0, 4, 'lin', 0.0, 1].asSpec
				),				// specsDict
				nil, 							// default GUI
				('dead ahead': (azimuth: 0, elevation:0)), // presets
				"2nd Order Mono input 3D Ambisonic Auto Panner",
				nil, 							// defaultAttributes
				[1, 1],							// inRange
				[2, inf],							// outRange
				{|plugin| 
					var speakers;
					var atorad = (2 * pi / 360);
					speakers = plugin.outputs.collectAs({|out|
						out.isBMSpeaker.not.if({
							"Ambisonics output not a speaker".error;
							^false;
						});
						[out.azi * atorad, out.ele * atorad];
					}, Array).flop;
					plugin.attributes[\speakersCoords] = speakers;
				},								// setupFunc
				nil								// cleanupFunc
			);
			BMMultichannelPluginSpec('Stereo Spectral Mag Split', // name
				{|plugin, numInputs, numOutputs, inputs| // ugenGraphFunc
					
					var chainA, chainB, fftSize = 2048;
					
					chainA = FFT(LocalBuf(fftSize, 1, 12000), inputs[0]);
					chainB = FFT(LocalBuf(fftSize), inputs[1]);
					chainA = [chainA] ++ Array.fill(numOutputs/2 - 1, {|i| PV_Copy(chainA, LocalBuf(fftSize))});
					chainB = [chainB] ++ Array.fill(numOutputs/2 - 1, {|i| PV_Copy(chainB, LocalBuf(fftSize))});
					chainA = PV_MagMul(chainA, plugin.attributes[\fftMulBufsL]);
					chainB = PV_MagMul(chainB, plugin.attributes[\fftMulBufsR]);  
					IFFT([chainA, chainB]).flop.flat;
				}, 								
				nil,				// specsDict
				nil, 							// default GUI
				nil, // presets
				"Spectral Magnitude Diffuser\nFFT size = 2048\nOutputs should be even",
				nil, 							// defaultAttributes
				[2, 2],							// inRange
				[4, inf],							// outRange
				{|plugin| 
					var scalesL, scalesR, numChannels, fftSize = 2048, fftMulBufsL, fftMulBufsR;
					numChannels = plugin.numOutputs;
					scalesL = Array.fill(fftSize, {Array.fill(numChannels/2, {1.0.rand}).normalizeSum }).flop;
					scalesR = Array.fill(fftSize, {Array.fill(numChannels/2, {1.0.rand}).normalizeSum }).flop;
					fftMulBufsL = scalesL.collect({|channel| Buffer.loadCollection(plugin.server,channel)});
					fftMulBufsR = scalesR.collect({|channel| Buffer.loadCollection(plugin.server,channel)});
					plugin.attributes[\fftMulBufsL] = fftMulBufsL;
					plugin.attributes[\fftMulBufsR] = fftMulBufsR;
				},								// setupFunc
				{|plugin|
					plugin.attributes[\fftMulBufsL].do(_.free);
					plugin.attributes[\fftMulBufsR].do(_.free);
				}								// cleanupFunc
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
					}, initVal
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
			window.front;
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
BMMultichannelPlugin {
	var <spec, <specsDict, <server, <attributes, <defName, <def;
	var <synth, <values, defaultValues, <bus, numControls, controlNames, mappings;
	var <preset;
	var <numInputs, <numOutputs, <inputs, <outputs;
	var gui;
	
	*new {|pluginSpecName, inArray, outArray, server, attributes|
		^super.new.init(pluginSpecName, inArray.asBMInOutArray, outArray.asBMInOutArray, server ? Server.default, attributes);
	}
	
	init { |argpluginSpecName, argins, argouts, argserver, argattributes|
		spec = BMMultichannelPluginSpec.specs[argpluginSpecName.asSymbol];
		spec.isNil.if({
			("Plugin spec" + argpluginSpecName + "does not exist!").warn;
			^nil;
		});
		specsDict = spec.specsDict.deepCopy;
		inputs = argins;
		outputs = argouts;
		numInputs = inputs.size;
		numOutputs = outputs.size;
		// check size and bail if wrong
		if(numInputs.inclusivelyBetween(spec.minInputs, spec.maxInputs).not || 
			numOutputs.inclusivelyBetween(spec.minOutputs, spec.maxOutputs).not, {
			("Input or output array not within allowable size range for plugin" 
				+ spec.name).error;
			^nil;	
		});
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
		defName = spec.name ++ UniqueID.next; 
		def = SynthDef(defName, {arg cfgate = 1;
			var input, out, env;
			input = In.ar(inputs);
			(input.size == 1).if({input = input[0];});
			out = SynthDef.wrap(spec.ugenGraphFunc, nil, [this, numInputs, numOutputs, input]);
			
			// fade in and out, release
			env = EnvGen.kr(Env.asr(BMOptions.crossfade, 1, BMOptions.crossfade), cfgate, 
				doneAction: 2);
			if(out.size != numOutputs, {
				"Plugin output does not match size of output array.".warn;
			});
			// if sizes don't match take the first outputs
			// use Out not XOut for multichannel
			out.do({|chan, i| Out.ar(outputs.at(i), env * chan);});
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
	
	makeSynth {|target, addAction=\addToTail|
		(target.asTarget.server != server).if({
			Error("Target server does not match Plugin server.").throw;
		});
		synth.notNil.if({ synth.set(\cfgate, 0); });
		synth = def.play(target, mappings, addAction);
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
	
	copy {
		var values, newplugin;
		values = this.values;
		newplugin = BMMultichannelPlugin(this.spec.name, this.inputs, this.outputs, this.server, this.attributes);
		values.keysValuesDo({|key, val| newplugin.set(key, val)});
		^newplugin;
	}

}



BMMultichannelPluginsRack : BMAbstractAudioChainElement {
	var <plugins;
	
	*new { |ins, outs, target, addAction = \addToTail, name|
		^super.new.init(ins.asBMInOutArray, (outs ? ins).asBMInOutArray, target, addAction, name);
		// default name is class
	}
	
	init {|argins, argouts, argtarget, argaddAction, argname|
		this.initNameAndTarget(argtarget, argaddAction, argname);
		ins = argins;
		outs = argouts;
		inNames = ins.keys;
		outNames = outs.keys;
		plugins = List.new;
	}
	
	clear { 
		plugins = List.new;
		this.makeNodes;
	}
	
	mappings { 
		var dict;
		dict = IdentityDictionary.new;
		dict[\plugins] = plugins.collect({|plugin|
			// could be a problem if pluginspec changes in the meantime
			[plugin.spec.name, plugin.inputs, plugin.outputs, plugin.attributes, plugin.values];
		}); // these are in order
		^dict;
	}
	
	mappings_ { |dict| 
		this.plugins.do({|plugin| plugin.release;});
		plugins = List.new;
		dict = dict ? ();
		dict[\plugins].do({|pluginArray|
			var plugin;
			plugin = BMMultichannelPlugin(pluginArray[0], pluginArray[1], pluginArray[2], server, 
				pluginArray[3]);
			plugin.notNil.if({
				this.addPlugin(plugin);
				pluginArray[4].keysValuesDo({|k, v| plugin.set(k, v)});
			});
		});
		this.changed;
	}
	
	makeNodes { 
		server.makeBundle(nil, {
			plugins.do({|plgin|
				plgin.makeSynth(group, \addToTail);
			});
		});
		this.changed;
	}
	
	addPlugin {|plugin|
		plugin.notNil.if({
			plugins.add(plugin);
			plugin.makeSynth(group, \addToTail);
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
			plugins.do({|plgin|
				plgin.synth.moveToTail(group);
			});
		});
	}
	
	free {
		plugins.do{| plugin, i | this.removePlugin(i) };
		SystemClock.sched(BMOptions.crossfade, { group.free; group = plugins = nil; allChainElements[name] = nil; });
	}
	
 
}

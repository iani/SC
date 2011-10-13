ClassAllGui : ObjGui {
	
	var <headZone, <scrollZone, <guiZone, <scrolly, <oldKeysRot=0, <keysRotation=0;
	var <filtBut, <filTextV, <>prefix="", <>filtering=false, prevExtract;
	var guis, modelGuiClass, <selObjs, bias, optionsDict;
			
	setDefaults {|obj, options| 
					//makeHead, scrollyWidth, orientation, makeFilter, initPos, name, selObjs
		var modelSkin;
		modelGuiClass = obj.first;
		modelSkin = modelGuiClass.skin; 
		if (options[2] == \horizontal) { bias = 0@1 } { bias = 1@0 };
		defPos = options[4] ?? { 10@260 };
		minSize = modelGuiClass.rowWidth@modelGuiClass.rowHeight + (modelSkin.margin * 2);
		minSize = minSize + (bias.transpose * minSize * (numItems - 1));
		if (options[0] ? false) { minSize = minSize + (0@skin.headHeight) }; // !! skin needed
		minSize = minSize + (bias * (options[1] ? 6 ));
		name = options[5] ?? {"" ++ modelGuiClass.name + "AllGui" };
		selObjs = options[6];
		optionsDict = IdentityDictionary.new;
		this.prSetMoreDefaults(obj, options);
		this.setMoreDefaults(obj, options);
		guis = List.new;
	}
	prSetMoreDefaults {}
	setMoreDefaults {}
	getName { ^name }

	makeViews{|obj, options|
		var scrollyWidth = options[1] ? 6; 
		if (options[0] ? false) {
			this.makeHead;
			if (options[3] ? false) { this.makeFilter };
			this.addToHead(headZone);
			
			this.makeGuiZone(zone.bounds.width@(zone.bounds.height - skin.headHeight)
				- (bias * scrollyWidth), options)
		}{
			this.makeGuiZone(zone.bounds.width@zone.bounds.height - (bias*scrollyWidth), options)
		};
		this.makeScroller(scrollyWidth, options[2] ? \vertical);
		this.finishMakeViews(obj, options)
	}
	finishMakeViews {}
	makeHead {
		headZone = CompositeView(zone, zone.bounds.width@skin.headHeight)
			.background_(Color.clear) //.background_(skin.foreground)
			.resize_(2);
		headZone.addFlowLayout(0@0, 0@0)	
	}
	makeFilter {
		this.addBeforeFilter;
		filTextV = GUI.textView.new(headZone, skin.filtTextWidth@skin.headHeight)
			.font_(Font(*skin.headFontSpecs))
			.string_("")
			.enterInterpretsSelection_(false)
			.keyDownAction_({ |txvw, char, mod, uni, keycode| 
				var str = txvw.string;
				if (str == "") { str = nil };
				this.prefix_(txvw.string);
			})
		;
		CompositeView(headZone, 1@skin.headHeight)
		;
		filtBut = GUI.button.new(headZone, 30@skin.headHeight)
			.canFocus_(false)
			.states_([["all", skin.fontColor, skin.offColor], 
				["filt", skin.alterFontColor, skin.backgroundFull]])
			.action_({ |btn| 
				this.filtering_(btn.value > 0); updFlag = true;
			})
		;
		this.addToFilter;	
	}
	addBeforeFilter {}
	addToFilter {}
	addToHead {} // overwritten by subclasses
	makeGuiZone {|bounds|
		guiZone = CompositeView(zone, bounds)
			//.background_(Color.yellow) // skin.foreground
			.resize_(5);
		guiZone.addFlowLayout(0@0, 0@0);
	}
	makeScroller {|scrollyWidth, orientation=\vertical|
		scrollZone = CompositeView(zone, 
			guiZone.bounds.width@guiZone.bounds.height * bias.transpose + (bias * scrollyWidth) )
				//.background_(Color.red)
		;
		if(orientation == \vertical) { scrollZone.resize_(6) } { scrollZone.resize_(8) };

		if (bias.x == 1) {
			scrolly = EZScroller(scrollZone, scrollZone.bounds.extent, numItems, numItems, 
				{ |sc| keysRotation = sc.value.asInteger.max(0) }
			).visible_(false)
		}{
			scrolly = EZScrollerH(scrollZone, scrollZone.bounds.extent, numItems, numItems, 
				{ |sc| keysRotation = sc.value.asInteger.max(0) }
			).visible_(false)
		};
		scrolly.slider
			.knobColor_(skin.foreground)
			.background_(Color.clear); //.resize_(3);
		if(orientation == \vertical) { scrolly.slider.resize_(6) } {scrolly.slider.resize_(8)}
	}
	updateSlow {
		var newState = this.getState;
		if (newState != prevState) { prevState = newState; 
			this.updateSlowMoreOnChange;
			updFlag = true}
	}
	getState {
		^if (selObjs.isNil) {
			object.collect{|guiClass| guiClass.observedClasses.collect{|cl|
				cl.all.values } }.flat.asSet.asList.sort({|a, b| (a.name < b.name) })
		}{	
			selObjs.select{|obj| obj.class.at(obj.name).notNil }
		}
	}
	updateSlowMoreOnChange {}
	updateFastMore {}
	updateFastAfterExtract {}
	filterObjs{|objs|
		if (prefix == "") {
			objs = objs.reject {|obj| obj.name.asString.includes($_) };
		}{
			objs = objs.select {|obj| obj.name.asString.contains(prefix) };
		};
		^objs
	}
	updateFast {
		var overflow, extract; //this.logln("updateFast" + prevState);
		
		this.updateFastMore;
		
		// extract = prevState;
		if (filtering) { 
			extract = this.filterObjs(prevState);
			if (extract != prevExtract) { prevExtract = extract; updFlag = true };
		}{ extract = prevState };
		
		if (keysRotation != oldKeysRot) {
				//this.logln("keysRotation:" + [keysRotation, oldKeysRot]);
			oldKeysRot = keysRotation; 
			updFlag = true
		};
		
		this.updateFastAfterExtract(extract);
		
		if (updFlag) {
			overflow = this.overflow(extract); 
			if (overflow > 0) { 
				scrolly.visible_(true);
				scrolly.numItems_(extract.size);
				scrolly.value_(keysRotation ? overflow);
				extract = extract.drop(keysRotation).keep(numItems);
			} { 
				scrolly.visible_(false);
			};
				// this.logln("extract:" + extract);
			this.buildGuis(extract);
			updFlag = false;
		}
	}
	overflow {|extract| 
		^(extract.size - numItems).max(0) 
	}
	buildGuis {|extract|
		this.prepareRedraw(extract);
		guis.do{|gui| gui.stopSkip; optionsDict.put(gui.object, gui.getOptions) };
		guis = List.new;
		guiZone.removeAll;
		guiZone.decorator.reset;
		guis = extract.collect{|obj|  // this.logln("extr:" + [object, obj, optionsDict[obj] ]);
		  object.detect{|guiClass| guiClass.observedClasses.any{|cl| obj.isMemberOf(cl)} }
		  .new(obj, 1, guiZone, options: optionsDict[obj] ?? { #[] })};
		this.finishRedraw(guis);
		guiZone.refresh; // needed if guis is empty
	}
	prepareRedraw {}
	finishRedraw {}
}

MldAllGui : ClassAllGui {
	
	var <>paths, pathBut;		
			//options: makeHead, scrollyWidth, orientation, makeFilter, initPos, name, paths
	prSetMoreDefaults{|obj, options| 
		paths = options[6] ?? { [[]] };
	}	
	addToFilter {
		pathBut = Button(headZone, 35@skin.headHeight).font_(font)
			.states_([["path", skin.fontColor, skin.offColor]])
			.action_({|but| Document.new("edit paths", 
				"~gui.paths = " + paths.asCompileString
				,envir: Environment.make({ ~gui = this }) ).bounds_(Rect(400, 500, 400, 150)); 
			})
	}
	getState {
		var objList = List.new;
		object.collect{|guiClass| guiClass.observedClasses.collect{|cl| paths.do{|path| 
			cl.all.leafDoFrom(path, {|folder, item|
				if (item.isMemberOf(cl)) { objList.add(item) } }) } } };
		^objList.flat.asSet.asList.sort({|a, b| (a.name < b.name) });
	}
}

SelAllGui : ClassAllGui {
	var <objects;
	prSetMoreDefaults{|obj, options| 
		objects = options[6] ?? { List.new };
this.logln("objects:" + objects);
	}
	getState {
		^objects.postln.select{|obj| obj.class.at(obj.name).notNil }
			.asList.sort({|a, b| (a.name < b.name) });
	}
	buildGuis {|extract|
		this.prepareRedraw(extract);
		guis.do{|gui| gui.stopSkip; optionsDict.put(gui.object, gui.getOptions) };
		guis = List.new;
		guiZone.removeAll;
		guiZone.decorator.reset;
		guis = extract.collect{|obj|  // this.logln("extr:" + [object, obj, optionsDict[obj] ]);
		  object.detect{|guiClass| guiClass.observedClasses.any{|cl| obj.isMemberOf(cl)} }
		  .new(obj, 1, guiZone, options: optionsDict[obj] ?? { #[] })};
		this.finishRedraw(guis);
		guiZone.refresh; // needed if guis is empty
	}

}

EZScrollerH : EZScroller {
	adjust {
		var slBounds = slider.bounds;
		var maxLength = slBounds.width max: slBounds.height + 2;
		var numTooMany = (numItems - maxItems).max(1);
		var fractionToShow = (maxItems / numItems).min(1);
		if (GUI.scheme.id == \cocoa) {
			// swingOSC posts a 'not implemented yet' warning.
			slider.thumbSize = fractionToShow * maxLength;
		};
	//	slider.step_(1 / numTooMany.max(1));		// this does the action - it should not.
		slider.setProperty(\step, 1 / numTooMany.max(1));
		spec.maxval_(numTooMany);	//maxval to not inverst here (mc) // minval to invert spec
	}
}

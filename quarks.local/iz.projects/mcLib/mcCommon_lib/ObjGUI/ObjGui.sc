ObjGui { 
	// learned from JITGui, just tweaked a little bit
	var <object, <numItems, <parent, <bounds, <skin, <parentObj;
	var <zone, <font, <minSize, <defPos; 
	var <prevState, <>updFlag=false, <skipjacks, <hasWindow = false;
	var <name, allGuiDefs, <>updFastFreq = 0.5, <>updSlowFreq = 2.0;
	
	var <nameView, <csView;
	
	*observedClasses { ^[ObjGuiExampleObj] }
	*rowWidth { ^250 }
	*rowHeight { ^this.skin.buttonHeight }
	*skin { ^GUI.skins[\small] }
	
	setDefaults { |obj, options|
		if (parent.isNil) { 
			defPos = 10@260
		} { 
			defPos = skin.margin;
		};
		minSize = if (bounds.notNil) { minSize = bounds.extent
			}{ (this.class.rowWidth) @ (numItems * this.class.rowHeight) };
		
		allGuiDefs = (
			allGuiClass: 		ClassAllGui
			,objGuiClasses:	[this.class]
			,numItems:		4
			// optional ClassAllGui defaults:
			,initPos:			550@128
			,skin:			GUI.skins[\AllGuiSkin]
			,makeHead:		true
			,scrollyWidth: 	6
			,orientation:		\vertical
			,makeFilter:		true
			,name:			"Sample AllGUI"
			,paths:			[[]]
		)
	}
	*new { |object, numItems = 1, parent, bounds, makeSkip = true, options = #[], skin, parentObj|
		^super.newCopyArgs(nil, numItems, parent, bounds, skin, parentObj)
			.init(object, makeSkip, options)
			//.object_(object);
	}
	accepts { |obj| ^true	}

	object_ { |obj| 
		if (this.accepts(obj)) {
			object = obj;
			this.updateAll
		} { 
			"% : object % not accepted!".format(this.class, obj).warn;
		}
	}
	makeClassAllGui {|objList, nItems|
		^allGuiDefs.allGuiClass.new(allGuiDefs.objGuiClasses, nItems ?? {allGuiDefs.numItems}, 
			nil, nil, true, 			
			[allGuiDefs.makeHead, allGuiDefs.scrollyWidth, allGuiDefs.orientation,
		 		allGuiDefs.makeFilter, allGuiDefs.initPos, allGuiDefs.name,
		 		objList ?? allGuiDefs.paths]
		 	, allGuiDefs.skin) 
	}
	init { |obj, makeSkip, options|
		skin = skin ?? { this.class.skin }; 
		this.setDefaults(obj, options);
		
		if (parent.isNil && obj.isNil) { ^this.makeClassAllGui }; //will return a ClassAllGui
		if (parent.isNil && obj.isKindOf(List)) { 
			^this.makeClassAllGui(obj, numItems) }; //tricky: obj must be a List not an Array
		
		font = Font(*skin.fontSpecs);		
		
		this.calcBounds(options); // calc bounds - at least minbounds 
		prevState = ();
	
		if (parent.isNil) { this.makeWindow };
		this.makeZone;
		
		this.makeViews(obj, options);
		
		this.finishInit(obj, options);
		
		if (makeSkip) { this.makeSkip }
	}
	
	finishInit {|obj, options|  // further init to be overwritten by subclasses; dependancy etc..
		
		//this.class.model.addDependant(this);
		//zone.onClose = { this.class.model.removeDependant(this) };
		
		zone.onClose = { this.stopSkip }; // win does not work here since may be inserted!
		this.object_(obj)
	}
	
	getOptions { ^nil } //indirect way to safe states of Gui; to be overwritten by subclasses
	
	name_ { |argName|
		name = argName.asString;
		if (hasWindow) { parent.name_(this.winName(name)) };
		if (nameView.notNil) { nameView.object_(object).string_("" + name) };
	}
	
	getName {	^try { object.name.asString } ? "_anon_" }
	winName { |name| ^this.class.name.asString + ":" + (name ?? { this.getName }) }
		
	calcBounds { 
		var defBounds;
		if(bounds.isKindOf(Rect)) { 
			bounds.setExtent(max(bounds.width, minSize.x), max(bounds.height, minSize.y));
			^this
		}; 
		
		defBounds = Rect.fromPoints(defPos, defPos + minSize + (skin.margin + skin.margin));

		if (bounds.isNil) { 
			bounds = defBounds;
			^this
		}; 
		
		if (bounds.isKindOf(Point)) { 
			bounds = defBounds.setExtent(max(bounds.x, minSize.x), max(bounds.y, minSize.y));
		}
	}
//should be false by default	
	makeWindow {|resizable = false| 
		parent = Window(this.winName, bounds, resizable).front; // not resizable
		parent.view.resize_(5)
//.background_(Color.green)
		;
		parent.addFlowLayout(skin.margin, 0@0);
		hasWindow = true;
	}
	
	makeZone { 
		zone = CompositeView(parent, bounds.extent - (skin.margin * 2)).resize_(5)
//.background_(Color.red)
		;
		zone.addFlowLayout(0@0, skin.gap);
	}
	window { ^if (hasWindow) {parent} { zone.getParents.last.findWindow } }
		
	moveTo { |h, v| if (hasWindow) { parent.bounds = parent.bounds.moveTo(h, v); } }
	close { if (hasWindow) { parent.close } }
	
	makeSkip { 
		var sjName = this.class.asString ++ "_" ++ name;
		skipjacks = [ SkipJack({ this.updateFast }, 
				updFastFreq,
				{ parent.isClosed },
				sjName)
			,SkipJack({ this.updateSlow }, 
				updSlowFreq,
				{ parent.isClosed },
				sjName++"_scanAll")
		];
	}
	startSkip { skipjacks.do{|skippy| skippy.start } }
	stopSkip { skipjacks.do{|skippy| skippy.stop } }
	updateAll {
		this.updateSlow;
		this.updateFast;
	}
	//to be overwriten -------------------------------------------------------------------------
	makeViews { 
		var lineheight = zone.bounds.height - (skin.margin.y * 2); 
		
		nameView = DragBoth(zone, Rect(0,0, 80, lineheight)).resize_(4)
			.font_(font)
			.align_(\center)
			.receiveDragHandler_({ arg obj; this.object = View.currentDrag });
			
		csView = EZText(zone, 
			Rect(0,0, bounds.width - 80 - (2*skin.margin.x), lineheight), 
			nil, { |ez| object = ez.value; })
			.font_(font);
		csView.view.resize_(5);
	}
	updateSlow {}
	updateFast {
		var newState = this.getState;
		if (newState == prevState) { ^this };
		
		if (newState[\object] != prevState[\object]) { 
			this.name_(this.getName);
			if (csView.textField.hasFocus.not) { csView.value_(object) };
			prevState = newState //mc
		}		
	}
	getState { 
		// get all the state I need to know of the object I am watching
		^(object: object) 
	}
}
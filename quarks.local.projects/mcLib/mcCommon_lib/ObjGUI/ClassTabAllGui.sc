ClassTabAllGui : ClassAllGui {
	var tabView, <>selectName, emptySink, overflow=0, scrollyWidth;
	
	*sinkClasses { ^[Object] }
	*scroll { ^false }
	*redrawAllTabs { ^false }
	
	setDefaults {|obj, options| //makeHead, scrollyWidth, orientation, makeFilter, initPos, name
		var modelSkin;
		modelGuiClass = obj.first;
		modelSkin = modelGuiClass.skin; 
		if (options[2] == \horizontal) { bias = 0@1 } { bias = 1@0 };
		defPos = options[4] ?? { 10@260 };
		minSize = modelGuiClass.rowWidth@modelGuiClass.rowHeight + (modelSkin.margin * 2);
//minSize = minSize + (bias.transpose * minSize * (numItems - 1));
		if (options[0] ? false) { minSize = minSize + (0@skin.headHeight) }; // !! skin needed
		scrollyWidth = options[1] ? 6; //extra here
		minSize = minSize + (bias * (options[1] ? 6 ));
		name = options[5] ?? {"" ++ modelGuiClass.name + "TabAllGui" };
		selObjs = options[6];
		optionsDict = IdentityDictionary.new;
		this.prSetMoreDefaults(obj, options);
		this.setMoreDefaults(obj, options);
		guis = List.new;
	}
	
	makeWindow { ^super.makeWindow(true) }
	makeGuiZone {|bounds, options|
		tabView = TabbedView(zone, bounds, [], scroll: this.class.scroll);
		guiZone = tabView.view.resize_(5).background_(Color.clear) // skin.foreground
		;
		if (options[2] == \horizontal) {tabView.tabPosition_(\top) 
			}{tabView.tabPosition_(\left).followEdges_(false)};

		// guiZone.addFlowLayout(0@0, 0@0);
	}
	finishMakeViews {
		scrollZone.visible = false;
		guiZone.bounds_(guiZone.bounds.width_(guiZone.bounds.width + scrollyWidth));
		this.finishMakeViewsMoreTab;
	}
	receiveDragHandler {|view|
		View.currentDrag.postln
	}
	buildGuis {|extract|
		var guiObjs, view, guis2Kill, selIndex;
		selectName = selectName ?? { 
			guis[tabView.activeTab] !? { guis[tabView.activeTab].object.name } };
		guis2Kill = guis.collect{|gui, i| 
			if (extract.includes(gui.object).not ||Êthis.class.redrawAllTabs) {[gui, i]} {nil} }
				.select{|list| list.notNil };
		this.prepareRedraw(extract, guis2Kill);
		guis2Kill.reverse.do{|l| l[0].stopSkip; 
			optionsDict.put(l[0].object, l[0].getOptions);
			guis.remove(l[0]); tabView.removeAt(l[1]) };
		guiObjs = guis.collect{|gui| gui.object};
		guis = extract.sort({|a, b| (a.name < b.name) }).collect{|obj, i|
			var index = guiObjs.indexOf(obj);
			if (index.notNil) { guis[index] } { 
				view = tabView.insert(i, obj.name);
				if (obj.name == selectName) { selIndex = i };
				[view, obj]
			}	
		};
		tabView.focus(selIndex ? 0); selectName = nil;
		guis = guis.collect{|obj| if (obj.isKindOf(Array).not) { obj }{
			object.detect{|guiClass| guiClass.observedClasses.any{|cl| obj[1].isMemberOf(cl)} }
			.new(obj[1], 1, obj[0], obj[0].bounds, options: optionsDict[obj[1]] ?? { #[] }) } 
		};
		this.finishRedraw(guis);
		if (guis.isEmpty) {
			emptySink = DragSink(guiZone, guiZone.bounds).align_(\center).font_(font).resize_(5)
				.canReceiveDragHandler_({ 
					this.class.sinkClasses.any{|c| View.currentDrag.isKindOf(c) } })
				.receiveDragHandler_( {|view| this.receiveDragHandler(view) } )
				.string_("empty")
		}{ emptySink !? { emptySink.remove; emptySink = nil } }
	}
	updateFastAfterExtract {|extract|
		var maxItems, lastTab = tabView.tabViews.last;
		lastTab !? { 
			numItems = extract.size;
			maxItems = (guiZone.bounds.height).div(tabView.tbht);
			overflow = (numItems - maxItems + 1).max(0) };
		
		case // has still to be generalised for other orientations
		{ (overflow > 0) && scrolly.visible.not } { updFlag = true; scrollZone.visible = true;
			guiZone.bounds_(guiZone.bounds.width_(guiZone.bounds.width - scrollyWidth)) }
		{ (overflow == 0) && scrolly.visible } { updFlag = true; scrollZone.visible = false;
			guiZone.bounds_(guiZone.bounds.width_(guiZone.bounds.width + scrollyWidth)) };
		if (scrollZone.visible) { scrolly.maxItems_(maxItems) };
		
		this.updateFastMoreTab;
	}
	overflow { ^overflow }
}

+ TabbedView {
	tbht { ^tbht }
	labels { ^labels }
	// add only overwritten to get mouse doublicks...
	add { arg label,index; //actually this is an insert method with args backwards
		var tab, container, calcTabWidth, i;
		
		index = index ? labels.size; //if index is nil, add it to the end
		i=index;
		labels=labels.insert(i,label.asString);
		i = labels.size-1;
		
		label=label.asString; //allows for use of symbols as arguments
		
//		if (tabWidth == \auto) //overwrite tabWidths if autowidth
//			{ calcTabWidth=label.bounds.width + labelPadding }
//			{ calcTabWidth = tabWidth };
			
		tabWidths=tabWidths.insert(index,50);	
		
		tab = context.userView.new(view); //bounds are set later
		tab.enabled = true;
		tab.focusColor=focusFrameColor;
		tab.mouseDownAction_({ this.logln("mouse down tab"); //never triggered
			this.focus(i);
			unfocusTabs.if{tab.focus(false)}; 
		});
		tabViews = tabViews.insert(index, tab);
		
		scroll.if{
				container = context.scrollView.new(view).resize_(5)
			}{
				container = context.compositeView.new(view,view.bounds).resize_(5)
			}; //bounds are set later
			
		container.background = backgrounds[i%backgrounds.size];
		
		views = views.insert(index,container);
		
		focusActions = focusActions.insert(index,{});
		
		unfocusActions = unfocusActions.insert(index,{});
		
		tabViews.do{ arg tab, i;
			tab.mouseDownAction_({|v, x, y, mod, bN, cC| 
//this.logln("mouse down all" +  views[activeTab]);
			if (cC == 2) { 
				SCRequestString(labels[activeTab], "new label:", {|str| 
					this.changed(\label, activeTab, str); 
					// still have to write back the resulting string to the tabedView
					this.updateViewSizes() })
			}{
				this.focus(i);
				unfocusTabs.if{tab.focus(false)}; 
			}});
			tab.canReceiveDragHandler_({
				this.focus(i);
				unfocusTabs.if{tab.focus(false)}; 
			});
		};
		
		this.updateViewSizes();
		
		^this.views[index];

	}
}
/*
ObjTabGui.new
ObjTabGuiExampleObj("someName")

20.do{|i| SYSTab("someName" + i) }
60.do{|i| SYSTab("someName" + i) }
*/
BMTrimPluginsRackGUI : BMAbstractGUI {
	var trimPluginsRack, trimPluginsStripGUIs, defaultHelpString, descriptionHelpText;
	
	*new {|trimPluginsRack, name, origin|
		^super.new.init(trimPluginsRack, name ? trimPluginsRack.name)
			.makeWindow(origin ? (40@200));
	}
	
	init {|argtrimPluginsRack, argname|
		trimPluginsRack = argtrimPluginsRack;
		name = argname;
		trimPluginsStripGUIs = List.new;
	}
	
	makeWindow {|origin|
		var x, y, width, pluglist, numTypes, numStrips, stripGUIs, buttons;
		x = origin.x;
		y = origin.y;
		width = 4 + 170 + 4 + min(104 * trimPluginsRack.ins.size, 1078); // max 7 visible
		window = SCWindow(name, Rect.new(x, y, width, 618), false);
		window.view.decorator = FlowLayout(window.view.bounds);
		pluglist = SCScrollView(window, Rect(0, 0, 160, 508))
			.hasHorizontalScroller_(false)
			.hasBorder_(true);
		numTypes = BMPluginSpec.specs.size;
		numStrips = trimPluginsRack.ins.size;
		pluglist = SCVLayoutView(pluglist, Rect(4,4,150, numTypes * 24 + 4));
		BMPluginSpec.specs.keysDo({|piName| 
			SCDragSource(pluglist, Rect(0, 0, 150, 20)).string_("   " ++ piName.asString)
				.background_(Color.grey.alpha_(0.2))
				.font_(Font("Helvetica-Bold", 12))
				.dragLabel_(piName.asString)
				.beginDragAction_({BMPlugin(piName)}) // one channel for now
				.mouseDownAction_({
					descriptionHelpText.string = piName ++ ": " ++ 
						BMPluginSpec.specs[piName].description;
				});
		});
		stripGUIs = SCScrollView(window, Rect(0, 0, width - 174, 508))
			.hasVerticalScroller_(false)
			.hasBorder_(true);
		stripGUIs.action = {window.refresh};
		//stripGUIs = SCHLayoutView(stripGUIs, Rect(4, 4, 104 * numStrips + 4, 500));
		stripGUIs = SCCompositeView(stripGUIs, Rect(4, 4, 104 * numStrips + 4, 500));
		stripGUIs.decorator = FlowLayout(stripGUIs.bounds, 0@0);
		trimPluginsRack.inNames.do({|chanName|
			trimPluginsStripGUIs.add(
				BMTrimPluginsStripGUI(trimPluginsRack[chanName], stripGUIs, chanName)
			);
		});
		defaultHelpString = "Click names at left for description.\nDrag from left to add plugins.\nDouble-click or select and press enter to edit plugin settings.\nCmd down and up arrows to change order.\nCmd drag to copy trim or a plugin and its settings to another channel.";
		window.view.decorator.nextLine;
		window.view.decorator.shift(20, 0);
		
		descriptionHelpText = SCStaticText(window, Rect(0, 0, width - 58, 100))
			.string_(defaultHelpString)
			.font_(Font("Helvetica-Bold", 12));
		
		buttons = SCVLayoutView(window, Rect(0, 0, 20, 80));
		RoundButton(buttons, Rect(0, 0, 20, 20))
			.extrude_(false)
			.canFocus_(false)
			.radius_(5)
			.states_([["?", Color.black, Color.white.alpha_(0.2)]])
			.font_(Font("Helvetica-Bold", 14))
			.action_({descriptionHelpText.string = defaultHelpString;});
		RoundButton(buttons, Rect(0, 0, 20, 20))
			.extrude_(false)
			.canFocus_(false)
			.radius_(5)
			.states_([["APi", Color.black, Color.white.alpha_(0.2)], ["APi", Color.black, Color.white]])
			.font_(Font("Helvetica-Bold", 8))
			.action_({|but| trimPluginsRack.autoPlugins(but.value.booleanValue) });
		RoundButton(buttons, Rect(0, 0, 20, 20))
			.extrude_(false)
			.canFocus_(false)
			.radius_(5)
			.states_([["ATr", Color.black, Color.white.alpha_(0.2)]])
			.font_(Font("Helvetica-Bold", 8))
			.action_({ trimPluginsRack.autoTrim });
		RoundButton(buttons, Rect(0, 0, 20, 20))
			.extrude_(false)
			.canFocus_(false)
			.radius_(5)
			.states_([["dT", Color.black, Color.white.alpha_(0.2)], ["dT", Color.black, Color.white]])
			.font_(Font("Helvetica-Bold", 12))
			.action_({|but| trimPluginsRack.delayCompensateDistance(but.value.booleanValue) });

		window.onClose = { 
			trimPluginsStripGUIs.do({|tpisg|
				tpisg.trimPluginsStrip.removeDependant(tpisg);
			});	
			onClose.value(this);
		};
		window.front;
	}
}

// only in a larger GUI
BMTrimPluginsStripGUI {
	var <trimPluginsStrip, containerView, ezKnob, labelView, listView;
	
	*new { |trimPluginsStrip, parent, name, origin|
		^super.new.init(trimPluginsStrip, parent).makeGUI(parent, name, origin ? 0@0);
	 }
	 
	 init {|argtrimPluginsStrip|
	 	trimPluginsStrip = argtrimPluginsStrip;
	 	trimPluginsStrip.addDependant(this);
	 }
	 
	 makeGUI{|parent, name, origin|
	 	
	 	containerView = SCCompositeView(parent, Rect(origin.x, origin.y, 100, 500));
	 	containerView.decorator = FlowLayout(containerView.bounds);
	 	labelView = SCStaticText(containerView, Rect(0, 0, 100, 30))
	 		.font_(Font("Helvetica-Bold", 13))
	 		.background_(Color.grey.alpha_(0.3))
	 		.string_(" " ++ name);
	 	ezKnob = EZKnob(containerView, 96@96, " Trim (dBFS)", \db.asSpec, 
	 		{|ez| trimPluginsStrip.trim_(ez.value);}, trimPluginsStrip.trim, false);
	 	ezKnob.labelView.align_(\left).font_(Font("Helvetica-Bold", 12));
	 	ezKnob.numberView.background_(Color.white.alpha_(0.3));
	 	listView = SCListView(containerView, Rect(0, 0, 100, 334))
	 		.items_(trimPluginsStrip.plugins.collect({|plugin| plugin.spec.name}));
	 	listView.enterKeyAction = {
	 		var plgin;
	 		plgin = trimPluginsStrip.plugins[listView.value];
	 		plgin.notNil.if({plgin.gui}); 
	 	}; // can duplicate
	 	listView.keyDownAction = { arg view,char,modifiers,unicode,keycode;
	 		block { |break|
				if((modifiers == 11534600) && (unicode == 63233), {
					trimPluginsStrip.movePluginDown(listView.value);
					break.value;
				});
				if((modifiers == 11534600) && (unicode == 63232), {
					trimPluginsStrip.movePluginUp(listView.value);
					break.value;
				});
				if(unicode == 127, {trimPluginsStrip.removePlugin(listView.value)});
				listView.defaultKeyDownAction(char,modifiers,unicode);
			}
		};
		listView.mouseDownAction = {|view, x, y, modifiers, buttonNumber, clickCount|
			if(clickCount == 2, {
				listView.enterKeyAction.value;
			});
		};
		listView.canReceiveDragHandler = { SCView.currentDrag.isKindOf(BMPlugin) };
		listView.receiveDragHandler = { trimPluginsStrip.addPlugin(SCView.currentDrag) };
		listView.beginDragAction = { 
			listView.dragLabel = listView.item.asString;
			trimPluginsStrip.plugins[listView.value].copy;
		};
	 }
	 
	 update {|tpv, what|
	 	if(what == \trim, {ezKnob.value = trimPluginsStrip.trim;});
	 	listView.items_(trimPluginsStrip.plugins.collect({|plugin| plugin.spec.name}));
	 	switch(what,
	 		\moveDown, {listView.value = listView.value + 1},
	 		\moveUp, {listView.value = listView.value - 1}
	 	)
	 }

}

// Quickly hacked from BMTrimPluginRackGUI and stripGUI

BMMultichannelPluginsRackGUI : BMAbstractGUI {
	var pluginsRack, pluginsStripGUIs, defaultHelpString, descriptionHelpText;
	
	*new {|pluginsRack, name, origin|
		^super.new.init(pluginsRack, name ? pluginsRack.name)
			.makeWindow(origin ? (40@200));
	}
	
	init {|argpluginsRack, argname|
		pluginsRack = argpluginsRack;
		name = argname;
		pluginsStripGUIs = List.new;
	}
	
	makeWindow {|origin|
		var x, y, width, pluglist, numTypes, numStrips, stripGUIs, buttons;
		x = origin.x;
		y = origin.y;
		
		width = 4 + 210 + 300;
		window = SCWindow(name, Rect.new(x, y, width, 618), false);
		window.view.decorator = FlowLayout(window.view.bounds);
		pluglist = SCScrollView(window, Rect(0, 0, 200, 508))
			.hasHorizontalScroller_(false)
			.hasBorder_(true);
		numTypes = BMMultichannelPluginSpec.specs.size;
		numStrips = 1;
		pluglist = SCVLayoutView(pluglist, Rect(4,4,190, numTypes * 24 + 4));
		BMMultichannelPluginSpec.specs.keysDo({|piName| 
			SCDragSource(pluglist, Rect(0, 0, 150, 20)).string_("   " ++ piName.asString)
				.background_(Color.grey.alpha_(0.2))
				.font_(Font("Helvetica-Bold", 12))
				.dragLabel_(piName.asString)
				.beginDragAction_({
					piName.asSymbol;
				}) 
				.mouseDownAction_({
					descriptionHelpText.string = piName ++ ": " ++ 
						BMMultichannelPluginSpec.specs[piName].description;
				});
		});

		pluginsStripGUIs.add(
			BMMultichannelPluginsStripGUI(pluginsRack, window, pluginsRack.name)
		);

		defaultHelpString = "Click names at left for description.\nDrag from left to add plugins.\nDouble-click or select and press enter to edit plugin settings.\nCmd down and up arrows to change order.\nCmd drag to copy trim or a plugin and its settings to another channel.";
		window.view.decorator.nextLine;
		window.view.decorator.shift(20, 0);
		
		descriptionHelpText = SCStaticText(window, Rect(0, 0, width - 58, 100))
			.string_(defaultHelpString)
			.font_(Font("Helvetica-Bold", 12));
		
		buttons = SCVLayoutView(window, Rect(0, 0, 20, 70));
		RoundButton(buttons, Rect(0, 0, 20, 20))
			.extrude_(false)
			.canFocus_(false)
			.radius_(5)
			.states_([["?", Color.black, Color.white.alpha_(0.2)]])
			.font_(Font("Helvetica-Bold", 14))
			.action_({descriptionHelpText.string = defaultHelpString;});

		window.onClose = { 
			pluginsStripGUIs.do({|tpisg|
				tpisg.pluginsStrip.removeDependant(tpisg);
			});	
			onClose.value(this);
		};
		window.front;
	}
}

// only in a larger GUI
BMMultichannelPluginsStripGUI {
	var <pluginsStrip, containerView, ezKnob, labelView, listView;
	
	*new { |pluginsStrip, parent, name, origin|
		^super.new.init(pluginsStrip, parent).makeGUI(parent, name, origin ? 0@0);
	 }
	 
	 init {|argpluginsStrip|
	 	pluginsStrip = argpluginsStrip;
	 	pluginsStrip.addDependant(this);
	 }
	 
	 makeGUI{|parent, name, origin|
	 	containerView = SCCompositeView(parent, Rect(origin.x, origin.y, 300, 508));

	 	listView = SCListView(containerView, Rect(0, 0, 300, 508))
	 		.items_(pluginsStrip.plugins.collect({|plugin| plugin.spec.name}));
	 	listView.enterKeyAction = {
	 		var plgin;
	 		plgin = pluginsStrip.plugins[listView.value];
	 		plgin.notNil.if({plgin.gui}); 
	 	}; // can duplicate
	 	listView.keyDownAction = { arg view,char,modifiers,unicode,keycode;
	 		block { |break|
				if((modifiers == 11534600) && (unicode == 63233), {
					pluginsStrip.movePluginDown(listView.value);
					break.value;
				});
				if((modifiers == 11534600) && (unicode == 63232), {
					pluginsStrip.movePluginUp(listView.value);
					break.value;
				});
				if(unicode == 127, {pluginsStrip.removePlugin(listView.value)});
				listView.defaultKeyDownAction(char,modifiers,unicode);
			}
		};
		listView.mouseDownAction = {|view, x, y, modifiers, buttonNumber, clickCount|
			if(clickCount == 2, {
				listView.enterKeyAction.value;
			});
		};
		listView.canReceiveDragHandler = { SCView.currentDrag.isKindOf(Symbol); };
		listView.receiveDragHandler = {
			var piName;
			piName = SCView.currentDrag;
			BMSelectInsOutsGUI(parent, pluginsStrip.ins, pluginsStrip.outs, {|ins, outs|
				var plugin;
				//ins.postln;
				//outs.postln;
				plugin = BMMultichannelPlugin(piName, ins, outs, 
					pluginsStrip.server);
				// protect against bad plugin inputs
				plugin.notNil.if({pluginsStrip.addPlugin(plugin)});
			});
		};
		listView.beginDragAction = { pluginsStrip.plugins[listView.value].copy };
	 }
	 
	 update {|tpv, what|
	 	
	 	listView.items_(pluginsStrip.plugins.collect({|plugin| plugin.spec.name}));
	 	switch(what,
	 		\moveDown, {listView.value = listView.value + 1},
	 		\moveUp, {listView.value = listView.value - 1}
	 	)
	 }

}

BMSelectInsOutsGUI : BMAbstractGUI {
	var ins, outs, okFunc;
	
	*new {|parent, ins, outs, okFunc|
		^super.new.init(ins, outs, okFunc)
			.makeWindow(parent);
	}
	
	init {|argins, argouts, argokfunc|
		ins = argins;
		outs = argouts;
		okFunc = argokfunc;
		name = "Define Ins and Outs";
	}
	
	makeWindow {|parent|
		var buttons, insSources, insLV, outsSources, insSubArrays, outsSubArrays;
		var inResult, outResult, outsLV, dragSource;
		window = SCModalSheet(parent, Rect.new(0, 0, 500, 620), false);
		window.view.decorator = FlowLayout(window.view.bounds);
		
		// ins
		SCStaticText(window, Rect(0, 0, 100, 30))
			.string_("Inputs")
			.font_(Font("Helvetica-Bold", 14));
		window.view.decorator.nextLine;
		insSources = SCScrollView(window, Rect(0, 0, 160, 254))
			.hasHorizontalScroller_(false)
			.hasBorder_(true);
		insLV = SCVLayoutView(insSources, Rect(4,4,150, ins.size * 24 + 4));
		ins.keys.do({|inKey| 
			SCDragSource(insLV, Rect(0, 0, 150, 20)).string_("   " ++ inKey.asString)
				.background_(Color.grey.alpha_(0.2))
				.font_(Font("Helvetica-Bold", 12))
				.dragLabel_(inKey.asString)
				.beginDragAction_({
					dragSource = \ins;
					inKey
				}); 
		});
		
		
		inResult = SCListView(window, Rect(0, 0, 160, 254)).font_(Font("Helvetica-Bold", 12));
		inResult.canReceiveDragHandler = { 
			dragSource == \ins;
		};
		inResult.receiveDragHandler = { 
			dragSource = nil;
			inResult.items = inResult.items.add(SCView.currentDrag)
		};
		inResult.keyDownAction = { arg view,char,modifiers,unicode,keycode;
			var newItems;
	 		block { |break|
				if((modifiers == 11534600) && (unicode == 63233), {
					if(view.value < (view.items.size -1), {
						view.items = view.items.swap(view.value, view.value + 1);
						view.refresh;
						view.value = view.value + 1;
					});
					break.value;
				});
				if((modifiers == 11534600) && (unicode == 63232), {
					if(view.value > 0, {
						view.items = view.items.swap(view.value, view.value - 1);
					});
					break.value;
				});
				if(unicode == 127, {
					view.item.notNil.if({
						newItems = view.items;
						newItems.removeAt(view.value);
						view.items = newItems;
					});
					break.value;
				});
				view.defaultKeyDownAction(char,modifiers,unicode);
			}
		};
		insSubArrays = SCPopUpMenu(window, Rect(0, 0, 160, 20))
			.font_(Font("Helvetica-Bold", 12))
			.items_(["Add subarray", "-"] ++ ins.subArrays)
			.action_({|menu|
				var subArray;
				subArray = ins.getSubArray(menu.item.asSymbol);
				subArray.notNil.if({inResult.items = inResult.items ++ subArray.keys});
				menu.value = 0;
			});
		
		// outs
		SCStaticText(window, Rect(0, 0, 100, 30))
			.string_("Outputs")
			.font_(Font("Helvetica-Bold", 14));
		window.view.decorator.nextLine;
		outsSources = SCScrollView(window, Rect(0, 0, 160, 254))
			.hasHorizontalScroller_(false)
			.hasBorder_(true);
		outsLV = SCVLayoutView(outsSources, Rect(4,4,150, outs.size * 24 + 4));
		outs.keys.do({|outKey| 
			SCDragSource(outsLV, Rect(0, 0, 150, 20)).string_("   " ++ outKey.asString)
				.background_(Color.grey.alpha_(0.2))
				.font_(Font("Helvetica-Bold", 12))
				.dragLabel_(outKey.asString)
				.beginDragAction_({
					dragSource = \outs;
					outKey
				}); 

		});
		outResult = SCListView(window, Rect(0, 0, 160, 254)).font_(Font("Helvetica-Bold", 12));
		outResult.canReceiveDragHandler = { 
			dragSource == \outs;
		};
		outResult.receiveDragHandler = { 
			dragSource = nil;
			outResult.items = outResult.items.add(SCView.currentDrag)
		};
		outResult.keyDownAction = { arg view,char,modifiers,unicode,keycode;
			var newItems;
			
	 		block { |break|
				if((modifiers == 11534600) && (unicode == 63233), {
					if(view.value < (view.items.size -1), {
						view.items = view.items.swap(view.value, view.value + 1);
						view.refresh;
						view.value = view.value + 1;
					});
					break.value;
				});
				if((modifiers == 11534600) && (unicode == 63232), {
					if(view.value > 0, {
						view.items = view.items.swap(view.value, view.value - 1);
					});
					break.value;
				});
				if(unicode == 127, {
					view.item.notNil.if({
						newItems = view.items;
						newItems.removeAt(view.value);
						view.items = newItems;
					});
					break.value;
				});
				view.defaultKeyDownAction(char,modifiers,unicode);
			}
		};
		outsSubArrays = SCPopUpMenu(window, Rect(0, 0, 160, 20))
			.font_(Font("Helvetica-Bold", 12))
			.items_(["Add subarray", "-"] ++ outs.subArrays)
			.action_({|menu|
				var subArray;
				subArray = outs.getSubArray(menu.item.asSymbol);
				subArray.notNil.if({outResult.items = outResult.items ++ subArray.keys});
				menu.value = 0;
			});

		window.view.decorator.nextLine;
		window.view.decorator.shift(window.bounds.width - 242, 0);
		
		RoundButton(window, 115 @ 20)
			.extrude_(false).canFocus_(false) 
			.states_([[ "Cancel", Color.black, Color.white.alpha_(0.8) ]])
			.action_({ window.close });
			   
		RoundButton(window, 115 @ 20)
			.extrude_(false).canFocus_(false)
			.states_([[ "OK", Color.black, Color.new255(51, 111, 203, 255 * 0.95) ]])
			.action_({ 
				window.close;
				okFunc.value(
					inResult.items.collectAs({|key| key->ins[key]}, BMInOutArray),
					outResult.items.collectAs({|key| key->outs[key]}, BMInOutArray)
				);
				onClose.value(this);
			});
		
	}
	
}
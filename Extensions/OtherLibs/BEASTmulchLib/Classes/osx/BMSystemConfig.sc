// classes for configuring the BEASTmulch System Application

BMSystemAppConfig {

	var <>numInChannels = 8, <>numSoundFilePlayerChans = 8;
	var <>numVirtIns = 8, <>numVirtOuts = 8;
	var <>controllers; // BMInOutArray with name->(class, paramsDict);
	
	*new { ^super.new.init }
	
	init {
		controllers = BMInOutArray.new;
	}
}

BMSystemConfigAppGUI : BMAbstractGUI {

	var sysconfig, numInChannels, numSoundFilePlayerChans, numVirtIns, numVirtOuts;
	var controllerClasses, controllerTypes, controllerLV, addedControllers, dragSource;
	var okayFunc;
	
	*new { |sysconfig, okayFunc| ^super.new.init(sysconfig, okayFunc).makeWindow }
	
	init {|argsysconfig, argokayFunc|
		sysconfig = argsysconfig;
		okayFunc = argokayFunc;
	}
	
	makeWindow {
		var controllersList;
		window = Window.new(" System Configuration", Rect(128, 64, 332, 490), resizable: false).front;
		window.addFlowLayout;
		StaticText(window, Rect(10, 10, 200, 20)).font_(Font("Helvetica-Bold", 14)).string_("Inputs and Outputs");
		
		numInChannels = EZNumber(	window,  	// parent
			300@20,	// bounds
			"Number of Input Channels",	// label
			[1, 200, \linear, 1, sysconfig.numInChannels].asSpec, 	// controlSpec
			nil, // action
			nil,		// initValue
			true,		// initAction
			200
		);
		numInChannels.numberView.background_(Color.white.alpha_(0.3));
		
		numSoundFilePlayerChans = EZNumber(	window,  	// parent
			300@20,	// bounds
			"Max Soundfile Player Channels",	// label
			[1, 200, \linear, 1, sysconfig.numSoundFilePlayerChans].asSpec, 	// controlSpec
			nil, // action
			nil,		// initValue
			true,		// initAction
			200
		);
		numSoundFilePlayerChans.numberView.background_(Color.white.alpha_(0.3));
		
		numVirtIns = EZNumber(	window,  	// parent
			300@20,	// bounds
			"Virtual Ins",	// label
			[1, 200, \linear, 1, sysconfig.numVirtIns].asSpec, 	// controlSpec
			nil, // action
			nil,		// initValue
			true,		// initAction
			200
		);
		numVirtIns.numberView.background_(Color.white.alpha_(0.3));
		
		numVirtOuts = EZNumber(	window,  	// parent
			300@20,	// bounds
			"Virtual Outs",	// label
			[1, 200, \linear, 1, sysconfig.numVirtOuts].asSpec, 	// controlSpec
			nil, // action
			nil,		// initValue
			true,		// initAction
			200
		);
		numVirtOuts.numberView.background_(Color.white.alpha_(0.3));
		
		window.view.decorator.nextLine;
		StaticText(window, Rect(10, 10, 320, 20)).font_(Font("Helvetica-Bold", 11)).string_(" Number of output channels is set automatically").align_(\center);
		
		// controllers
		
		controllersList = sysconfig.controllers.deepCopy;
		window.view.decorator.nextLine.nextLine;
		StaticText(window, Rect(10, 10, 200, 20)).font_(Font("Helvetica-Bold", 14)).string_(" Controllers");
		
		window.view.decorator.nextLine;
		controllerClasses = BMAbstractController.allSubclasses.select({|class| class.name.asString.containsi("abstract").not });
		
		controllerTypes = SCScrollView(window, Rect(0, 0, 160, 254))
			.hasHorizontalScroller_(false)
			.hasBorder_(true);
		controllerLV = SCVLayoutView(controllerTypes, Rect(4,4,150, controllerClasses.size * 24 + 4));
		
		controllerClasses.do({|class| 
			SCDragSource(controllerLV, Rect(0, 0, 150, 20))
				.string_("  " ++ class.humanName)
				.background_(Color.grey.alpha_(0.2))
				.font_(Font("Helvetica-Bold", 10))
				.dragLabel_(class.humanName)
				.beginDragAction_({
					dragSource = \controllers;
					class
				}); 
		});
		
		addedControllers = SCListView(window, Rect(0, 0, 160, 254))
			.font_(Font("Helvetica-Bold", 12))
			.items = controllersList.keys;
			
		addedControllers.canReceiveDragHandler = { 
			dragSource == \controllers;
		};
		addedControllers.receiveDragHandler = { 
			var class;
			dragSource = nil;
			class = SCView.currentDrag;
			BMControllerConfigGUI(class, window, {|result|
				controllersList.add(result.name.asSymbol -> (class: class, paramsDict: result));
				addedControllers.items = controllersList.keys;
				addedControllers.focus;
				addedControllers.value = controllersList.size - 1;
			});
		};
		addedControllers.keyDownAction = { arg view,char,modifiers,unicode,keycode;

		 	block { |break|
				if((modifiers == 11534600) && (unicode == 63233), {
					if(view.value < (view.items.size -1), {
						view.items = controllersList.swap(view.value, view.value + 1).keys;
						view.refresh;
						view.value = view.value + 1;
					});
					break.value;
				});
				if((modifiers == 11534600) && (unicode == 63232), {
					if(view.value > 0, {
						view.items = controllersList.swap(view.value, view.value - 1).keys;
						view.value = view.value - 1;
					});
					break.value;
				});
				if(unicode == 127, {
					view.item.notNil.if({
						controllersList.removeAt(view.item.asSymbol);
						view.items = controllersList.keys;
					});
					break.value;
				});
				view.defaultKeyDownAction(char,modifiers,unicode);
			}
		};
				 
		addedControllers.enterKeyAction = {
			var class, index;
			class = controllersList[addedControllers.item][\class];
			index = addedControllers.value;
	 		BMControllerConfigGUI(class, window, {|result|
				controllersList.removeAt(addedControllers.item);
				controllersList.insert(index, result.name.asSymbol -> (class: class, paramsDict: result));
				addedControllers.items = controllersList.keys;
				addedControllers.focus;
			},  controllersList[addedControllers.item][\paramsDict]);
	 	};
	 	
		addedControllers.mouseDownAction = {|view, x, y, modifiers, buttonNumber, clickCount|
			if(clickCount == 2, {
				addedControllers.enterKeyAction.value;
			});
		};
		
		StaticText(window, Rect(10, 10, 320, 20)).font_(Font("Helvetica-Bold", 11)).string_(" Drag from left to create a new controller");
		
		window.view.decorator.nextLine.nextLine;
		window.view.decorator.shift(window.bounds.width - 242, 0);
		
		RoundButton(window, 115 @ 20)
			.extrude_(false).canFocus_(false) 
			.states_([[ "Cancel", Color.black, Color.white.alpha_(0.8) ]])
			.action_({ window.close });
			   
		RoundButton(window, 115 @ 20)
			.extrude_(false).canFocus_(false)
			.states_([[ "OK", Color.black, Color.new255(51, 111, 203, 255 * 0.95) ]])
			.action_({ 
				sysconfig.numInChannels = numInChannels.value;
				sysconfig.numSoundFilePlayerChans = numSoundFilePlayerChans.value;
				sysconfig.numVirtIns = numVirtIns.value;
				sysconfig.numVirtOuts = numVirtOuts.value;
				sysconfig.controllers = controllersList;
				window.close;
				okayFunc.value(sysconfig);
				onClose.value(this);
			});
		
	}
}
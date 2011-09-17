SystemaGui : ObjGui {
/*
wslib
SystemaGui.new
SYS.agmUni.gui
*/	
	var delBut, tunigD, saveBut, descBut, postlBut, postiBut, postliBut;
	var tabBut, freqRootNum, midiRootNum, playBut;
	var isDesending=false;
		
	*observedClasses { ^[Systema, SYS] }
	*rowWidth { ^15 + 130 + 130 +15 + 25 + 30 + 30 + 30 + 30 + 35 + 15 + 30 }
	*rowHeight { ^this.skin.buttonHeight}
	*skin { ^GUI.skins[\small] }
	
	setDefaults { |obj, options|
		if (parent.isNil) { 
			defPos = 1050@800
		} { 
			defPos = skin.margin;
		};
		minSize = (this.class.rowWidth) @ (numItems * this.class.rowHeight);
		
		obj.addDependant(this);
		
		allGuiDefs = (
			allGuiClass: 		SystemaAllGui	//LoopStageAllGui
			,objGuiClasses:	[this.class]
			,numItems:		8
			// optional ClassAllGui defaults:
			,initPos:			295@5
			,skin:			GUI.skins[\AllGuiSkin]
			,makeHead:		true
			,scrollyWidth: 	6
			,orientation:		\vertical
			,makeFilter:		true
			,name:			"Systema AllGUI"
		)
	}
	update {|who, what ...args| //this.logln("update:" + [who, what, args]);
		what.switch(
			\midiRoot, { { freqRootNum.value_( who.midiRoot.midicps.round(0.1) );
				midiRootNum.value_( who.midiRoot.round(0.01) ) }.defer }
			,\destroy, {}
			,{ this.logln("unmaped update:" + [who, what, args]) })
	}
	makeViews {|obj| 
		var lineheight = zone.bounds.height - (skin.margin.y * 2); 
		
		delBut = Button(zone, 15@lineheight).font_(font)
			.states_([["d", skin.warnFontColor, skin.offColor]])
			.action_({|but| Systema.remove(obj.name) })
		;
		nameView = SCDragSource(zone, 130@lineheight).font_(font).align_(\left)
			.receiveDragHandler_({ arg obj; this.object = View.currentDrag })
		;
		nameView.dragLabel_(obj.asString);
			
			
		tunigD=DragBoth(zone,130@lineheight).align_(\right).font_(font)
			.background_(skin.background)
			//.mouseDownAction_({ if(isLink) { this.changed(\selectLoop) } })
			.canReceiveDragHandler_( {View.currentDrag.isKindOf(RCTuning) ||				View.currentDrag.isKindOf(Tuning) } )
			.receiveDragHandler_( {|view| obj.tuning_(View.currentDrag) } )
		;
		tunigD.object_(obj.tuning).string_(obj.tuning.name.asString + "")
			.dragLabel_("Tuning: " + obj.tuning.name.asString);
			
		saveBut = Button(zone, 15@lineheight).font_(font)
			.states_([["s", skin.fontColor, skin.offColor]])
			.action_({|but| Dialog.savePanel({|path| 
					obj.copy(path.basename.split($.)[0]).save(path.dirname) })
			})
		;
		tabBut = Button(zone, 25@lineheight).font_(font)
			.states_([["tab", skin.fontColor, skin.offColor]])
			.action_({|but| SYSTab(obj.name.asString, [obj.name]) })
		;
		freqRootNum = NumberBox(zone, 30@lineheight).font_(font)
			.value_( obj.midiRoot.midicps.round(0.1) )
			.action_({|but| obj.midiRoot_(but.value.cpsmidi)})
		;
		midiRootNum = NumberBox(zone, 30@lineheight).font_(font).align_(\right)
			.value_( obj.midiRoot.round(0.01) )
			.action_({|but| obj.midiRoot_(but.value)})
		;
		postlBut = Button(zone, 30@lineheight).font_(font)
			.states_([["postl", skin.fontColor, skin.offColor]])
			.action_({|but| if (isDesending) {obj.postld} {obj.postl} })
		;
		postiBut = Button(zone, 30@lineheight).font_(font)
			.states_([["posti", skin.fontColor, skin.offColor]])
			.action_({|but| if (isDesending) {obj.postid} {obj.posti} })
		;
		postliBut = Button(zone, 35@lineheight).font_(font)
			.states_([["postli", skin.fontColor, skin.offColor]])
			.action_({|but| if (isDesending) {obj.postlid} {obj.postli} })
		;
		descBut = Button(zone, 15@lineheight).font_(font)
			.states_([["+", skin.fontColor, skin.offColor], 
				["Ð", skin.fontColor, skin.alterFontColor]])
			.action_({|but| isDesending = but.value > 0 })
		;
		playBut = Button(zone, 30@lineheight).font_(font)
			.states_([["play", skin.fontColor, skin.onColor]])
			.action_({|but| if (isDesending) {obj.playR} {obj.play} })
		;
		zone.onClose = { obj.removeDependant(this) }
	}
	updateFast {
		var newState = this.getState;
		if (newState == prevState) { ^this };
		
		if (newState[\object] != prevState[\object]) { 
			this.name_(this.getName);
			prevState = newState //mc
		}		
	}
}

SystemaAllGui : ClassAllGui {
	var delBut, encBut, tabBut, loadPopT, pathTree, sysTabWin;
	prSetMoreDefaults { CodePage.addDependant(this) }
	update {|who, what ...args| //this.logln("update:" + [who, what, args]);
		what.switch(
			\active, { { encBut.value_( if (CodePage.active) {1} {0} ) }.defer }
			,{ this.logln("unmaped update:" + [who, what, args]) })
	}
	addBeforeFilter {
		delBut = Button(headZone, 15@skin.headHeight)
			.states_([["d", skin.warnFontColor, skin.offColor]])
			.action_({|but| guis.do{|gui| Systema.remove(gui.object.name) } })
		;
	}
	addToHead {
		headZone.onClose = { CodePage.removeDependant(this) };
		
		encBut = Button(headZone, 30@skin.headHeight)
			.states_([["enc", skin.fontColor, skin.offColor], 
				["enc", skin.fontColor, skin.onColor]])
			.action_({|but| CodePage.active = but.value > 0 })
		;
		encBut.value_( if (CodePage.active) {1} {0} );
		
		StaticText(headZone, 98@skin.headHeight).font_(font).align_(\center).string_("Tuning")
		;
		tabBut = Button(headZone, 42@skin.headHeight)
			.states_([["TABL", skin.fontColor, skin.offColor]])
			.action_({|but| if (sysTabWin.isNil) { sysTabWin = SYSTabGui.new.window;
				}{ if (sysTabWin.isClosed) { sysTabWin = SYSTabGui.new.window 
					}{ sysTabWin.front} } })
		;
		StaticText(headZone, 25@skin.headHeight).font_(font).align_(\center).string_("freq")
		;
		StaticText(headZone, 10@skin.headHeight).font_(font).align_(\center).string_("R")
		;
		StaticText(headZone, 25@skin.headHeight).font_(font).align_(\center).string_("midi")
		;
	
		loadPopT = PopUpTreeMenu2(headZone, 
			(headZone.bounds.width-(15+130+130+40+30+30+6))@skin.headHeight)
			.items_(["load: SYSTEMATA"])
			.openAction_{|view| view.tree_(Systema.makePathTree.dictionary) }
			.closeAction_{|view| view.items_(["load: SYSTEMATA"])}
			.action_{|view, val| 
				var path = Systema.userDirs.asList.select{|p| 
					PathName(p).fileName.asSymbol == val.first };
				if (path.isEmpty) { Systema.systemDirs.asList.select{|p| 
					PathName(p).fileName.asSymbol == val.first } };
				val = val.drop(1); // this.logln("val:" + val);
				if (val.last == 'All:') {
					path = path.first +/+ (val.collect{|v| 
						v.asString }.drop(-1).reduce('+/+') ?? {""});
					Systema.loadDir(path);
				}{ 
					path = path.first +/+ val.collect{|v| v.asString }.reduce('+/+');
					Systema.load(path);
				}
			}
		;
		headZone.onClose = { CodePage.removeDependant(this) }
	}
}

PopUpTreeMenu2 : PopUpTreeMenu {

	init {|argParent, argBounds|
		var dec;
		lst= List.new;							//one array in here for each submenu
		tree= (\nil: ());							//default tree
		font= Font("Monaco", 9);
		hiliteColor= Color.grey;
		
		//--create popUpMenu.  visible when submenus not open
		pop= PopUpMenu(argParent, argBounds)
			.font_(font)
.background_(Color.grey(0.80))
			.stringColor_(Color.black);
		bounds= pop.bounds;
		pop.onClose= {lst.do{|z| if(z[1].notNil, {z[1].close})}};
		this.view_(pop);
		
		//--search for parent decorator.  shift it to allow for userView on top of popUpMenu
		dec= this.parent.decorator;
		if(dec.notNil, {
			dec.shift(bounds.left-dec.left, bounds.top-dec.top);
		});
		
		//--create userView on top of popUpMenu.  any decorator is bypassed with shift above
		usr= UserView(argParent, bounds);
		usr.mouseDownAction_({|v, x, y| mouseMoved= false; this.prUserAction(v, x, y)});
		usr.mouseMoveAction_({|v, x, y| mouseMoved= true; this.prUserAction(v, x, y)});
		usr.mouseUpAction_({|v, x, y| this.prUserActionEnd(v, x, y)});
		usr.canFocus= false;
		
		//--find parentWindow and compensate for some containers that add extra offset (TabbedView)
		argParent= this.parent;
		add= Point(0, 0);
		while({argParent.respondsTo(\findWindow).not}, {
			add= add+Point(argParent.bounds.left.neg, argParent.bounds.top);
			argParent= argParent.parent;
		});
		parentWindow= argParent.findWindow;			//set main window
	}
}

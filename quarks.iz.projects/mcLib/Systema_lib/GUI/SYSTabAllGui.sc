/*
Buttons:
make annotate option --> bigger offset / smaller offset
make annotat mod(3) optional
make cents / ratios optional

check frameRate
check annimated pluck on play ...> dependency ---> should be easy!!!
*/
SYSTabAllGui : ClassTabAllGui {
	var loadPopT, frameRateNum;
	var activeGui;
	
	var debug = true;
	
	*sinkClasses { ^[Systema, SYS] }
	*scroll { ^false } // if true creates a ScrollView within TabbedView!
	*redrawAllTabs { ^true }


	addBeforeFilter {
		Button(headZone, 15@skin.headHeight)
			.states_([["d", skin.warnFontColor, skin.offColor]])
			.action_({|but| guis[tabView.activeTab] !? { 
				SYSTab.remove(guis[tabView.activeTab].object.name) } })
		;
	}
	addToHead {
		Button(headZone, 15@skin.headHeight)
			.states_([["n", skin.fontColor, skin.offColor]])
			.action_({|but| SCRequestString("", "new Tableau with name:", {|str| SYSTab(str) }) })
		;
		Button(headZone, 15@skin.headHeight)
			.states_([["c", skin.fontColor, skin.offColor]])
			.action_({|but| activeGui !? { SCRequestString("", "copy Tableau with new name:", {|str| 
				activeGui.object.copy(str) }) } })
		;
		Button(headZone, 15@skin.headHeight)
			.states_([["C", skin.fontColor, skin.offColor]])
			.action_({|but| activeGui !? { SCRequestString("", "deep copy Tableau with new name:"
				,{|str| activeGui.object.deepCopy(str) }) } })
		;
		Button(headZone, 15@skin.headHeight)
			.states_([["s", skin.fontColor, skin.offColor]])
			.action_({|but| activeGui !? { activeGui.saveBackParamsSave } })
		;
		Button(headZone, 15@skin.headHeight)
			.states_([["S", skin.fontColor, skin.offColor]])
			.action_({|but| activeGui !? { activeGui.copySYSsSave } })
		;
		Button(headZone, 80@skin.headHeight)
			.states_([["SYSs of Tab", skin.fontColor, skin.offColor]])
			.action_({|but| var objs, displace; 
				activeGui !? { 
					objs = activeGui.object.sysSymbols.collect{|sym| Systema.at(sym)}.asList;
					displace = 10.rrand(40);
					SystemaGui.new(objs, objs.size.min(46)).moveTo(100 + displace,700-displace)
				} })
		;
		Button(headZone, 15@skin.headHeight)
			.states_([["l", skin.fontColor, skin.offColor]])
			.action_({|but| File.openDialog("load a SYSTab", {|fn| fn.load }) })
		;
		loadPopT = PopUpTreeMenu2(headZone, 155@skin.headHeight) //PopUpTreeMenu2 in SystemaGui.sc
			.items_(["load new Tab with SYSs:"])
			.openAction_{|view| view.tree_(Systema.makePathTree.dictionary) }
			.closeAction_{|view| view.items_(["load new Tab with SYSs:"])}
			.action_{|view, val|
				var path = Systema.userDirs.asList.select{|p| 
					PathName(p).fileName.asSymbol == val.first };
				if (path.isEmpty) { Systema.systemDirs.asList.select{|p| 
					PathName(p).fileName.asSymbol == val.first } };
				val = val.drop(1); // this.logln("val:" + val);
				SCRequestString("", "Tableau name:", {|str| var loaded = List.new;
					if (val.last == 'All:') {
						path = path.first +/+ (val.collect{|v| 
							v.asString }.drop(-1).reduce('+/+') ?? {""});
						loaded.addAll(Systema.loadDir(path, warn:false));
					}{ 
						path = path.first +/+ val.collect{|v| v.asString }.reduce('+/+');
						loaded.addAll(Systema.load(path, warn: false));
					};
					SYSTab(str, loaded.collect{|sys| sys.name})
				});
			}
		;
		StaticText(headZone, 70@skin.headHeight).font_(font).align_(\right).string_("framerate:  ")
		;
		frameRateNum = NumberBox(headZone, 20@(skin.headHeight - 2)).font_(font)
			.action_({|but| activeGui !? { activeGui.frameRate_(but.value) } })
		;
	if (debug) {
		StaticText(headZone, 60@skin.headHeight).font_(font).align_(\right).string_("debug: ")
		;
		Button(headZone, 30@skin.headHeight)
			.states_([["upd", skin.warnColor, skin.offColor]])
			.action_({|but| activeGui !? { activeGui.rebuildDrawGraph } })
		;
		StaticText(headZone, 10@skin.headHeight).font_(font).align_(\center).string_(" ")
		;
		Button(headZone, 60@skin.headHeight)
			.states_([["updClear", skin.warnColor, skin.offColor]])
			.action_({|but| activeGui !? { activeGui.rebuildDrawGraph(true) } })
		;
		StaticText(headZone, 50@skin.headHeight).font_(font).align_(\right).string_(" sBack: ")
		;
		Button(headZone, 35@skin.headHeight)
			.states_([["UPD", skin.warnColor, skin.offColor]])
			.action_({|but| activeGui !? { activeGui.saveBackParamsRebuildDrawGraph } })
		;
		StaticText(headZone, 10@skin.headHeight).font_(font).align_(\center).string_(" ")
		;
		Button(headZone, 65@skin.headHeight)
			.states_([["UPDClear", skin.warnColor, skin.offColor]])
			.action_({|but| activeGui !? { activeGui.saveBackParamsRebuildDrawGraph(true) } })
		;
		StaticText(headZone, 10@skin.headHeight).font_(font).align_(\center).string_(" ")
		;
		}
	}
	finishMakeViewsMoreTab {
		tabView.addDependant(this);
		guiZone.onClose = { tabView.removeDependant(this) };
		selectName = SYSTab.selectName;
	}
	update {|who, what ...args| //this.logln("update:" + [who, what, args]);
		if (what == \label) { who.labels[args[0]] = args[1]; 
			guis[args[0]].object.rename(args[1]); updFlag = true }
	}
	receiveDragHandler {|view|
		SYSTab( View.currentDrag.name.asString, [View.currentDrag.name])
	}
	updateFastMoreTab {
		//var view = tabView.views[tabView.activeTab];
		//view !? { view.children.do{|child| child.refresh} }
		var gui = guis[tabView.activeTab];
		if (gui.notNil && (activeGui != gui)) {
			activeGui = gui;
			guis.reject{|g| g == activeGui}.do{|g| g.deactivate };
			activeGui.activate(tabView.activeTab);
			this.restoreState(activeGui);
		}
	}
	restoreState {|activeGui|
		frameRateNum.value_(activeGui.frameRate);
	}
}
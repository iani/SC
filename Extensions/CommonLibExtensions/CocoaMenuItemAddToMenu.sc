

+ CocoaMenuItem { 
	*addToMenu { | menuName, itemName, shortCut, action |
		var menu, item;
		menu = CocoaMenuItem.topLevelItems detect: { | m | m.name == menuName };
		if (menu.isNil) {
			menu = SCMenuGroup(nil, menuName, 10);	
		};
		item = SCMenuItem(menu, itemName);
		if (shortCut.notNil) {
			item.setShortCut(*shortCut); // Cmd-ctrl-alt-$
		};
		item.action = action;
	}
}
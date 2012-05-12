/*
CocoaMenuItem.addToMenu("User Menu", "test");
*/

+ CocoaMenuItem { 
	*addToMenu { | menuName, itemName, shortCut, action, unique = true |
		var menu, item, found;
		menu = CocoaMenuItem.topLevelItems detect: { | m | m.name == menuName };
		if (menu.isNil) {
			menu = SCMenuGroup(nil, menuName, 10);
		};
		found = menu.children detect: { | c | c.name == itemName };
		if (found.notNil and: { unique }) { ^this };
		item = SCMenuItem(menu, itemName);
		if (shortCut.notNil) {
			item.setShortCut(*shortCut); // Cmd-ctrl-alt-$
		};
		item.action = action;
		^item;
	}
}
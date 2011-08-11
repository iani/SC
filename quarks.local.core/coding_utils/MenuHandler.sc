/*

Works together with Object:addMenu, Object:removeMenu 
to help any object or Class to add and remove menus. 

*/


MenuHandler {
	
	classvar <menuItems;
	
	*initClass {
		menuItems = IdentityDictionary.new;	
	}
	
	*addMenu { | object |
		menuItems[object] = object.menuItems;
	}

	*removeMenu { | object |
		menuItems[object] do: _.remove;
		menuItems[object] = nil;	
	}
}


+ Object {
	addMenu { | ... items |
		MenuHandler.addMenu(this);	
	}
	
	removeMenu { MenuHandler.removeMenu }
		
}
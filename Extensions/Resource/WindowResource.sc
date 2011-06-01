WindowResource : Resource {
//	*mainKey { ^[UniqueWindow] } // all subclasses store instances under UniqueWindow
//	*removedMessage { ^\closed }
	classvar defaultBounds;

	init { | windowFunc |
		super.init(windowFunc ?? { Window(key.last.asString).bounds_(this.defaultBounds).front });
		this.addWindowOnCloseAction;
	}

	defaultBounds { 
		if (defaultBounds.isNil	) { defaultBounds = Rect(100, 100, 400, 400) };
		defaultBounds = defaultBounds.moveBy(40, 40);
		if (defaultBounds.top + defaultBounds.height > Window.screenBounds.height) {
			defaultBounds.top = 100;
		};
		if (defaultBounds.left + defaultBounds.width > Window.screenBounds.width) {
			defaultBounds.left = 100;
		};
		^defaultBounds;
	}

	addWindowOnCloseAction {
		object.onClose = {
			this.remove;
			object.releaseDependants;
			object.objectClosed;
			object = nil;	
		};
	}

	*front { | windowName |
		var window;
		window = this.at(windowName);
		if (window.notNil) { window.front };
	}

	// shortcuts and synonyms
	window { ^object }
	view { ^object.view }
	name { ^object.name }
	bounds { ^object.bounds }
	bounds_ { | rect | object.bounds = rect; }
	front { object.front }
	/* prevent crashing when closing from keyboard on window */
	close { { object.close }.defer(0.01); }
	isOpen { ^object.notNil }
}


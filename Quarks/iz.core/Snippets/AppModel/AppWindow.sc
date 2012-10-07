/* IZ Thu 16 August 2012  4:02 PM EEST

Window creation help classes for AppModel

*/

AppNamelessWidget {
	var <model;
	*new { | ... args | ^this.newCopyArgs(*args).init; }
	widgetDo { | func |
		/* perform func on the widget. Needed to customize the widget at create time,
			while returning the view widget for further processing.
			Useful for adding notifiers to the widget from other sources than its AppModel,
			for example, running processes, other system events. 
			When the the view or window used by this widget closes, the notification is also 
			removed. 
			This is the preferable method for adding notifications from external sources. 
		 */
		func.(this)
	}	
}

AppNamelessWindow : AppNamelessWidget {
	var <windowInitFunc, <window;

	init {
		window = Window();
		windowInitFunc.(window, model);
		window.onClose = {
			window.notify(\windowClosed, this);
			window.objectClosed;
		};
		window.toFrontAction = { window.notify(\windowToFront, this) };
		window.endFrontAction = { window.notify(\windowEndFront, this) };
		window.front;	// Update views next, after window has drawn:
		model.updateListeners;
	}
}

AppStickyWindow : AppNamelessWindow {
	*new { | model, owner, name, windowInitFunc |
		var windowMaker;
		windowMaker = Library.at(owner, name);
		if (windowMaker.notNil) { 
			windowMaker.showWindow;
		}{
			Library.put(owner, name, super.new(model, windowInitFunc));
		}
	}
	showWindow { if (window.isClosed) { this.init } { window.front }; }
}

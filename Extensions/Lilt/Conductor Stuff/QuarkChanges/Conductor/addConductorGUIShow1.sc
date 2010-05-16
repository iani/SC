/* IZ 080506

A method so that ConductorGUI does not create a second gui window if one is already open for it. 

Also modifying ConductorGUI draw so that it notifies when window closes.
Also modified reshow so that it does not notify "closed" when closing the old window.

*/


+ Conductor {
	show1 { |argName, x = 128, y = 64, w = 900, h = 160|
		{
			gui.show1(argName, x, y, w, h);
		}.defer;
	}
}

+ ConductorGUI {
	show1 { | argName, x = 128, y = 64, w = 900, h = 160 |
		var win;
		win = this[\win];
		if (win.notNil and: { win.isClosed.not }) {
			win.front;
		}{
			this.show(argName, x, y, w, h);
		};
		^win;
	}
	draw { | win, name, key |
		win.onClose = {
			this.changed(\closed);
		};
		if (stopOnClose) {
			SimpleController(win).put(\closed, { conductor.stop });
		};
		this.use { this.drawItems(win) }
	}
	reshow {
		var oldWin;
		oldWin = this[\win];
		this.show(oldWin.name, *oldWin.bounds.asArray);
		oldWin.onClose = nil;
		oldWin.close;
	}
}

+ Conductor {
	hide {
		gui.hide;
		this.changed(\hide);
	}
}

+ ConductorGUI {
	hide {
		var win;
		win = this[\win];
		if (win.notNil) { win.close }
	}
}
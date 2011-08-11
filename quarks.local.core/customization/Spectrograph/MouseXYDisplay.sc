
Dependant {
	var <>model;	
	*new { | model | ^this.newCopyArgs(model).init; }
	init { }
	addMessage { | message, action | model.addListener(this, message, action); }
}

MouseXYDisplay : Dependant {
	var <info = "-----";
	var <userview;
	var <>rect, <x, <y;

	init {
		rect = Rect(5.5, 5.5, 400, 20);
		this.addMessage(\viewsInited, {
			userview = model.userview;
			model.object.acceptsMouseOver = true;
			userview.mouseOverAction = { | v, argX, argY |
				x = argX; y = argY;
				this.display;
			};
			this.display;
		});
		model.onClose({
			NotificationCenter.unregister(model, \viewsInited, this);
		});
	}

	display {
		this.makeInfo;
		this.refreshUserView;
	}

	makeInfo { info = format("X: %, Y: %", x, y); }

	refreshUserView { userview.refreshInRect(rect); }

	update {
		Pen.use {
			Pen.width_(0.5);
			Color.black.alpha_(0.4).setFill;
			Color.white.setStroke;
			Pen.fillRect(rect);
			Pen.strokeRect(rect);
			info.drawAtPoint(10@10, Font.default, Color.white);
		}
	}	
}

SpectrographInfoDisplay : MouseXYDisplay {
	init {
		super.init;
		this.addMessage(\rate_, { this.display; });
	}
	makeInfo { info = format("X: %, Y: %, rate: %", x, y, model.rate); }
}
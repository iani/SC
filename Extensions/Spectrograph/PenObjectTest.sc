
PenObjectTest {
	var <model;
	var <info = "-----", <userview;
	var rect;
	*new { | model |
		^this.newCopyArgs(model).init;
	}

	init {
		rect = Rect(5.5, 5.5, 400, 20);
		model.addListener(this, \viewsInited, {
			model.object.view.mouseOverAction = { | v, x, y |
				[v, x, y].postln;	
			};
			userview = model.userview;
			model.object.acceptsMouseOver = true;
			userview.mouseOverAction = { | v, x, y |
				info = format("X: %, Y: %", x, y);
				userview.refreshInRect(rect);
			};
		});
		model.onClose({
			NotificationCenter.unregister(model, \viewsInited, this);
		});
	}

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
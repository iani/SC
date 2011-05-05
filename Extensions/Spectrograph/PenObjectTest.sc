
PenObjectTest {
	var <info = "-----", <userview;
	var rect;
	*new { | spectrogram |
		^super.new.init(spectrogram);
	}

	init { | spectrogram |
		rect = Rect(5.5, 5.5, 400, 20);
		NotificationCenter.register(spectrogram, \viewsInited, this, {
			{ spectrogram.userview.postln; } ! 10;
			spectrogram.object.view.mouseOverAction = { | v, x, y |
				[v, x, y].postln;	
			};
			userview = spectrogram.userview;
			spectrogram.object.acceptsMouseOver = true;
			userview.mouseOverAction = { | v, x, y |
				info = format("X: %, Y: %", x, y);
				userview.refreshInRect(rect);
			};
		});
		spectrogram.onClose({
			NotificationCenter.unregister(spectrogram, \viewsInited, this);
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
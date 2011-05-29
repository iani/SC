
+ Window {
	*centeredWindowBounds { | width = 400, numLines = 20, lineHeight = 18 |
		var screenBounds, centerX, itemsHeight, centerY;
		screenBounds = Window.screenBounds;
		centerX = screenBounds.width / 2;
		itemsHeight = numLines max: 3 * lineHeight + 30 min: screenBounds.height;
		centerY = screenBounds.height - itemsHeight / 2;
		^Rect(centerX - (width / 2), centerY, width, itemsHeight);
	}
	
	*fullScreenBounds { 
		^Rect(0, 0, Window.screenBounds.width, Window.screenBounds.height);
	}
}

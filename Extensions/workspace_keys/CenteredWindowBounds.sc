
+ Window {
	*centeredWindowBounds { | width = 400, numLines = 20 |
		var screenBounds, centerX, itemsHeight, centerY;
		screenBounds = Window.screenBounds;
		centerX = screenBounds.width / 2;
		itemsHeight = numLines max: 3 * 18 + 30 min: screenBounds.height;
		centerY = screenBounds.height - itemsHeight / 2;
		^Rect(centerX - (width / 2), centerY, width, itemsHeight);
	}
}

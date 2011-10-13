
Scroll {
	var pixelCache, blankPixels, cacheWidth, scrollWidth, imageHeight, color;
	*new { | image, scrollWidth = 200, color |
		^super.new.init(image.width, image.height, scrollWidth, color);
	}
	
	init { | imageWidth, argImageHeight, argScrollWidth, color |
		scrollWidth = argScrollWidth;
		cacheWidth = imageWidth - scrollWidth;
		imageHeight = argImageHeight;
		pixelCache = Int32Array.fill(cacheWidth * imageHeight, 0);
		this.setColor(color);
	}
	
	setColor { | argColor |
		color = argColor ?? { Color.black };
		this.initImageData;	
	}
	
	initImageData {
		blankPixels = Int32Array.fill(scrollWidth * imageHeight, Image colorToPixel: color);
	}
	
	update { | argIndex, image |
		if (argIndex < image.width) { ^argIndex };
		argIndex = argIndex % scrollWidth;
		if (argIndex == 0) {
			image.loadPixels(pixelCache, Rect(scrollWidth, 0, cacheWidth, imageHeight));
			image.setPixels(pixelCache, Rect(0, 0, cacheWidth, imageHeight));
			image.setPixels(blankPixels, Rect(cacheWidth, 0, scrollWidth, image.height));
		};
		^argIndex + cacheWidth;
	}
}

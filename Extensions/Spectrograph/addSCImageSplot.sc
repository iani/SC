/* 
Towards a scrolling plot for fft's
*/

+ SCImage { 
	splot { arg name, bounds, freeOnClose=false, background=nil, showInfo=true;
		var uview, window, nw, nh, ratio = width / height, info="";
		var routine;
		nw = width.min(600).max(200);
		nh = nw / ratio;
		window = SCWindow.new(name ? "plot", bounds ? Rect(400,400,nw,nh)/*, textured: false*/);
		allPlotWindows = allPlotWindows.add(window);

		if(background.notNil, {
			window.view.background_(background);
		});
		window.acceptsMouseOver = true;

		uview = SCUserView(window, window.view.bounds)
			.resize_(5)
			.focusColor_(Color.clear);

		window.onClose_({
			routine.stop;
			allPlotWindows.remove(window);
			if(freeOnClose, {
				this.free
			});
		});
		
		routine = {
			var scrollpixels, width = 10, height;
			height = this.height;
			width = this.width - 1;
			scrollpixels = Int32Array.fill(height * width, 0);
			100 do: { | i |
				0.25.wait;
				"scrolling".postln;
				this.loadPixels(scrollpixels, Rect(1, 0, width, height));
				this.setPixels(scrollpixels, Rect(0, 0, width, height));
//				this.loadPixels(scrollpixels, Rect(50, 0, width, height));
//				tileInRect( rect, fromRect, operation, fraction )
//	loadPixels(array, region, start)
//	setPixels(array, region, start)
				uview.refresh;	
			};
		}.fork(AppClock);
		
		uview.drawFunc_({

			SCPen.use {
				this.drawInRect(window.view.bounds, this.bounds, 2, 1.0);
			};

			if(showInfo, {
				SCPen.use {
					SCPen.width_(0.5);
					Color.black.alpha_(0.4).setFill;
					Color.white.setStroke;
					SCPen.fillRect(Rect(5.5,5.5,100,20));
					SCPen.strokeRect(Rect(5.5,5.5,100,20));
					info.drawAtPoint(10@10, Font.default, Color.white);
				}
			});
		});
		uview.mouseOverAction_({|v, x, y|
			if(showInfo, {
				if (this.isValid) {
					info = format("X: %, Y: %",
					((x / window.view.bounds.width) * this.width).floor.min(width-1),
					((y / window.view.bounds.height) * this.height).floor.min(height-1) );
				}{
					info = "invalid image";
				};
				window.view.refreshInRect(Rect(5.5,5.5,100,20));
			});
		});
		
		^window.front;
	}
}

DrawSpectrogramCrosshair {
	var drawCrossHair = false; // mYIndex, mXIndex, freq;
	var <bufSize, <binfreqs;	// size of FFT
	var <>crosshairColor;
	var userview, mouseX, mouseY, freq, drawCrossHair = false; // mYIndex, mXIndex, freq;

	mouseTrigger { | vbounds | // experimental
		Pen.color = crosshairColor;
		Pen.addRect( vbounds.moveTo( 0, 0 ));
		Pen.clip;
		Pen.line(0@mouseY, vbounds.width@mouseY);
		Pen.line(mouseX@0, mouseX@vbounds.height);
		Pen.font = Font( "Helvetica", 10 );
		Pen.stringAtPoint( "freq: " + freq.asString, mouseX + 20@mouseY - 15);
		Pen.stroke;
	}

}

/*

	mouseTrigger { | on | // experimental
		if (on) {
			NotificationCenter.register(this, \drawPen, thisMethod, { | vbounds |
				Pen.color = crosshairColor;
				Pen.addRect( vbounds.moveTo( 0, 0 ));
				Pen.clip;
				Pen.line(0@mouseY, vbounds.width@mouseY);
				Pen.line(mouseX@0, mouseX@vbounds.height);
				Pen.font = Font( "Helvetica", 10 );
				Pen.stringAtPoint( "freq: " + freq.asString, mouseX + 20@mouseY - 15);
				Pen.stroke;
			});
		}{
			NotificationCenter.unregister(this, \drawPen, thisMethod);
		}
	}
*/
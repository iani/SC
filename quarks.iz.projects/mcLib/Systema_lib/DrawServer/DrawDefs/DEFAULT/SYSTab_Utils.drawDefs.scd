if (this.isLog(2)) { ("\n\n" + thisProcess.nowExecutingPath.basename + "\n").postln; };

(
DrawDef(\strDisplay, \movedX, nil
	,{|node, cP, str, font, color, bgColor|
		var rect = str.bounds(font).center_(cP);
		color = color ?? {Color.white}; bgColor = bgColor ?? { Color.grey };
		{|e| Pen.use {
				bgColor.setFill; Pen.fillRect(rect.insetBy(-6, -2)); 
				str.drawInRect(rect, font, color) }
	}})
);

(
DrawDef(\dragRect_draw, \draw, nil
	,{|node, rect, color, width|
		color = color ?? {Color.white};
		width = width ? 1;
		{|e| Pen.use { Pen.width = width; color.setStroke; Pen.strokeRect(rect) }
	}})
);

(
DrawDef(\lineY_moved, \moved, nil
	, {|node, pE, y=100, dashArray, color|
		pE.newMidiRefY = y;
		pE.newMidiRefFloatArray = dashArray; // make it an unique accessible var
		pE.newMidiRefColor = color ?? { Color.yellow };
		{|e, drawWidth, newMidiRefY, newMidiRefFloatArray, newMidiRefColor|
		Pen.use{ 
			if (newMidiRefFloatArray.notNil) { Pen.lineDash_(newMidiRefFloatArray) };
			newMidiRefColor.set; 
			Pen.width = 1.5; Pen.line(0@newMidiRefY, drawWidth@newMidiRefY); Pen.stroke;
		}}})
);
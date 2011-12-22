/* IZ 20111222 
Simple display of pitch classes sets on a 12-node circle, as filled shapes with different colors.
The pitch classes are arranged in fifths-circle arrangement: 
0, 7, 2, 9, 4, 11, 6, 1, 8, 3, 10, 5

//:---
a = Screen.new;
a add: PitchPoly(lines: [10, 3, 4, 9]);

//:----
a add: PitchPoly(lines: [10, 3, 4, 9] + 2);

//:----
a add: PitchPoly(lines: [10, 3, 4, 9] + 4);


//:---
*/
PitchPoly : Polygon {
	var width, height, radius, center, margin = 20;
	
	init {
		super.init;
		drawMethod = \fill;
		color = Color(1.0.rand, 1.0.rand, 1.0.rand, 0.5);
	}
	
	value { | bounds |
		width = bounds.width;
		height = bounds.height;
		radius = width min: height / 2 - margin;
		center = (width / 2)@(height / 2);
		Pen.fillColor = color;
		Pen.strokeColor = color;
		Pen.moveTo(this coordsFor: lines[0]);
		lines do: { | p | Pen.lineTo(this coordsFor: p) };
		Pen perform: drawMethod;
	}

	coordsFor { | pclass |
		if (pclass.isNil) { ^0@0 };
		^Polar(radius, pclass * 7 - 3 % 12 * pi / 6).asPoint + center;
	} 
	
	add { | point |
		lines add: point;
		graph.changed;
	}
}

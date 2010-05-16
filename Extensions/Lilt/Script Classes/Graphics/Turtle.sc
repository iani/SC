/* iz Friday; January 8, 2010: 7:59 AM
A point in 2D space that can move in one direction, turn to change direction, and jump to an arbitrary position. 
Used to implement tracing lines with Patterns.

t = Turtle.new;
{ t.turn(pi/2); t.move(1) } ! 4;

(
w = Window("Turtle", Rect(10, 10, 800, 700).fromTop.fromRight).front;
v = UserView(w, w.view.bounds);
v.background = Color.white;
o = 200@200;
t = Turtle(o.x, o.y);
p = { t.turn(pi/2); t.move(100) } ! 4;

w.drawHook = {
	// set the Color
	Pen.strokeColor = Color.black;
	Pen.width = 1;
	Pen moveTo: o;
	p do: { | pt |
	 Pen lineTo: pt
	};
	Pen.stroke;
};
w.refresh;
)
*/

BasicTurtle {
	/*  BasicTurtle cannot move without drawing a line. Turtle can */
	classvar <defaultWindow;
	var <x, <y, <orientation;
	var <sin, <cos;	// cache the sine and cosine of orientation to save recalculating at each "move"
	var <origin, <vertices;	// store these here for access by custom functions in eval method of Turtle
	classvar <autoscale = true; // if true, drawing is scaled to fit to bounds of view, at each new vertex
				// setting autoscale will immediately redraw the default window if it is open, thereby refreshing
				// the drawing with the new scale settings. 
	var <minX, <maxX, <minY, <maxY;	// track the maximum bounds of the drawing for scaling

	*new { | x = 0, y = 0, orientation = 0 |
		^this.newCopyArgs(x, y).orientation_(orientation);
	}
	
	*autoscale_ { | argAutoscale |
		autoscale = argAutoscale;
		this.redraw;
	}
	
	*redraw {
		if (defaultWindow.notNil) { defaultWindow.refresh };		
	}
	
	*rescale {
		/* set autoscale to true, refresh the window, and restore autoscale to its previous value
		this redraws the drawing and scales it to fit to the size of the window */
		var func;
		var autoscale_backup;
		autoscale_backup = autoscale;
		func = { | sender, message ... args |
			if (message == \drawing_ended) {
				format("restoring autoscale value to: %", autoscale_backup).postln;
				autoscale = autoscale_backup;
				Turtle.removeDependant(func);
			};
		};
		Turtle.addDependant(func);
		this.autoscale = true;
		thisMethod.postln;
	}

	orientation_ { | angle |
		orientation = angle;
		// calculate and cache the sin and cos 
		sin = orientation.sin;
		cos = orientation.cos;
	}


	turn { | angle |
		this.orientation = orientation + angle;
	}
	
	move { | distance |
		x = distance * cos + x;
		y = distance * sin + y;
		^x@y
	}
	
	xy { ^x@y }
	
	jumpTo { | point |
		x = point.x;
		y = point.y;
		^point;
	}

	// draw a pattern on a window
	*draw { | patternFunc, window, x = 0, y = 0, orientation = 0, rate = 0.1 |
		var turtle;
		turtle = this.new(x, y, orientation);
		turtle.draw(patternFunc.(turtle), window, x@y, rate);
		^turtle;
	}
	*makeWindow {
		var view;
		defaultWindow = Window("Turtle", Rect(10, 10, 800, 800).fromTop.fromRight);
		defaultWindow.onClose = { defaultWindow = nil };
		view = UserView(defaultWindow, defaultWindow.view.bounds);
		view.background = Color.white;
		view.resize = 5;
		^defaultWindow;
	}
	draw { | pattern, window, argOrigin, rate = 0.1 |
		var nextPoint, count = 0;
		var event;		// patterns can save other stuff here for reuse;
		var stream;		// the stream that generates the vertex data for the drawing
		event = (count: count);
		vertices = [];
		if (window.isNil) { window = defaultWindow ?? { this.class.makeWindow }};
		window.front;
		origin = argOrigin ?? { window.view.bounds.center };
		event use: {
			if (pattern.isKindOf(SequenceableCollection)) {
				pattern = Pseq(pattern, 1);
			};
			stream = pattern.asStream;
		};
		window.drawHook = {
			// set the Color
			this.drawAll(origin, vertices, window);
		};
		{
			while {
				window.isClosed.not and: {
					count = count + 1;
					event[\count] = count;
					format("%, ", count).post;
					(nextPoint = stream.next).notNil
				}
			}{
				vertices = vertices add: nextPoint;
				if (rate > 0) {
					window.refresh;
					rate.wait;
				}
			};
			window.refresh;
			"\n ------------------------- DRAWING ENDED -------------------------".postln;
		}.fork(AppClock);
	}

	drawAll { | origin, vertices, window |
		this.prepareDrawing(origin, window);
		this.drawAllVertices(vertices);
		Pen.stroke;
		this.class.changed(\drawing_ended, this);		
	}

	prepareDrawing { | origin |
		Pen.strokeColor = Color.black;
		if (autoscale.not) { Pen.width = 1 };
		Pen moveTo: origin;
	}

	drawAllVertices { | vertices |
		vertices do: Pen.lineTo(_);
	}
}

/* Turtle implements lifting of the pen to jump to different vertices without drawing a line in between. */

Turtle : BasicTurtle {
	var <>setupFunc;	// custom setup of the drawing. Initialization of pen etc.

	prepareDrawing { | origin, window |
		if (autoscale) { this.scaleDrawing(window) };
		super.prepareDrawing(origin);
		setupFunc.(this)
	}

	scaleDrawing { | window |
		var width, scaleFactor;
		var xvert, yvert;
		if (vertices.size <= 1) { ^this }; 
		#xvert, yvert = vertices.slice(nil, 2).collect({ | p | [p.x, p.y] }).flop;
		#minX, maxX, minY, maxY = [xvert.smallest, xvert.largest, yvert.smallest, yvert.largest];
		width = window.view.bounds.width min: window.view.bounds.height;
//		this.updateBounds;
		scaleFactor = width / ((maxX - minX) max: (maxY - minY) max: 200);
		scaleFactor = scaleFactor * 0.95;
//		format("minX: %, maxX: %, minY: %, maxY: %, scaleFactor: %", minX, maxX, minY, maxY, scaleFactor).postln;
		Pen.translate(width - ((maxX - minX) * scaleFactor) / 2 - (minX * scaleFactor),
			width - ((maxY - minY) * scaleFactor) / 2 - (minY * scaleFactor));
		Pen.scale(scaleFactor, scaleFactor);
		Pen.width = 0.5 / scaleFactor;
	}

	drawAllVertices { | vertices |		
		vertices do: { | p |
//			Pen.perform(p[0], *p[1..]);
			p[0].perform(p[1], *p[2..]);
//			this.updateBounds;
//			p[0].perform(p[1], p[2..], *p[3..]);
		}
	}
/*	
	updateBounds {
		maxX = x max: maxX;
		minX = x min: minX;
		maxY = y max: maxY;
		minY = y min: minY;
		format("--- minX: %, maxX: %, minY: %, maxY: %", minX, maxX, minY, maxY).postln;
		#minX, maxX, minY, maxY = this.vertexBounds;
//		format("!!! minX: %, maxX: %, minY: %, maxY: %", minX, maxX, minY, maxY).postln;
		
	}
*/	
	move { | distance, draw = 1, point1, point2 |
		x = distance * cos + x;
		y = distance * sin + y;
//		point = point ?? { x@y };
		^[Pen, [\moveTo, \lineTo, \curveTo, \quadCurveTo, \strokeOval][draw], x@y, point1, point2]
//		cannot get other shapes than lines to work in the stream:
//		^[Pen, [\moveTo, \lineTo, \curveTo, \quadCurveTo, \strokeOval][draw], point, args]
	}

	wrap { | rect, draw = 1, point1, point2 |
		// wraps within a rectangle, to keep a drawing within bounds
		var newX, newY;
		var changed;
		newX = x.wrap(rect.left, rect.right);
		newY = y.wrap(rect.top, rect.bottom); 
		if ([newX, newY] == [x, y]) {
			^[Pen, [\moveTo, \lineTo, \curveTo, \quadCurveTo /* , \strokeOval */][draw], x@y, point1, point2]
		}{
			x = newX;
			y = newY;
			^[Pen, \moveTo, x@y]
		}
		
	}
	basicMove { | distance, draw = 1, point1, point2 |
		// used to return helper point for curves
		x = distance * cos + x;
		y = distance * sin + y;
		^x@y
	}

	jumpTo { | point, draw = 1 |
		x = point.x;
		y = point.y;
		^[Pen, [\moveTo, \lineTo][draw], point]
	}
	
	// Does not work yet
	eval { | func |
		// permit evaluation of arbitrary functions inside the drawing pattern
		^[func, \value, this]
	}
	
//  =======================================================================
// debugging the 0 rate scale drawing problem, saving and loading data etc. 
	vertexBounds {
		var xvert, yvert;
		#xvert, yvert = vertices.slice(nil, 2).collect({ | p | [p.x, p.y] }).flop;
		^[xvert.smallest, xvert.largest, yvert.smallest, yvert.largest]
	}

}



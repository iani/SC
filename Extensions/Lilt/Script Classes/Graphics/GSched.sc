/* By Ronald Kuivila. 
2009 / 2010

Changes by IZ: 
100113: added default for animation = false in *show 

*/

EventAdaptor {
	var <>event, <>key;
	
	*new { | event, key = \drawFunc |
		^super.new.event_(event).key_(key)
	}
	
	valueEnvir {
		^event.use{ event[key].valueEnvir }
	}
}

GraphicsProxy : Order {
	var <>window, <>view;
	var <>streams, <>envir, <>task;
	
	*new { ^super.new.streams_(Order.new).envir_(()) }
	
	*show { | win, animation = false |  ^this.new.show(win, animation) }
	
	run { |rate = 0.05| task = Task({ loop { window.refresh; rate.wait } }).play(AppClock) }
	
	stop { task.stop; task = nil }
// Function interface
	value { | window |
		var width, height;
		envir[\window] = window;
		envir[\width] = window.bounds.width;
		envir[\height] = window.bounds.height;
		envir.use {
			this.do { |obj| obj.valueEnvir };
		}
	}

// Pattern interface
	embedInStream { | event |
		var cleanup, index, finishFunc, adaptor; 
		index = event[\id] ? this.pos;	
		adaptor = EventAdaptor(event);
		this.put(index, adaptor);
		cleanup = EventStreamCleanup.new;
		cleanup.addFunction(event, { this.removeAt(index) } );
		finishFunc = { adaptor.event_(currentEnvironment); currentEnvironment.proto = envir };
		loop {
			event[\play] = finishFunc;
			event = index.yield;
			cleanup.update(event)
		}
	}
	
	asStream { ^Routine({ arg inval; this.embedInStream(inval) }) }
	
	show { |  win, animation = true |
		if (win.isNil) { 
			window = Window.new("", Rect(100, 100, 400, 300)).front 
		} {
			window = win
		};
		if (animation) {
			view = AnimationView(window, window.bounds.moveTo(0,0))
		} {
			view = UserView(window, window.bounds.moveTo(0,0))
		};
		view	.resize_(5).drawFunc_(this);
	}
	
	clear { 
		streams.do(_.stop);
		super.clear
	}

	put { | index, obj |
		var esp;
		if (obj.isNil) { 
			obj = this.removeAt(index);
			if ( (esp = streams.removeAt(index)).notNil ) {
				esp.stop; 
			};
		}{ 
			if (obj.isKindOf(Pattern) ) {
				esp = Pchain(Pbind(\id, index, \id, this), obj).play;
				streams[index] = esp;
			} {
				super.put(index, obj);
			}
		}
	}
	
	
	pop {
		var str,  i = indices.pop;
		if( (str = streams[i]).notNil ) { str.stop }
		^array.pop;
	}
	
}

/*
An example of use.
	/*
	An image from the processing book using the standard window interface:
	var w= Window("processing - page_136", Rect(100, 100, 360, 550), true);
		w.view.background_(Color.white);
		w.drawHook_{
			Pen.setSmoothing(true);
			Pen.fillColor_(Color.grey(0, 10/255));
			Pen.translate(33, 66);
			45.do{|i|
				Pen.scale(1.01, 1.01);
				Pen.fillOval(Rect.aboutPoint(Point(25, 90), 200/2, 200/2));
			};
		};
		w.front;
	*/

// here it is using Graphics Proxy
GUI.swing;
a = GraphicsProxy.show(Window("graphics", Rect(969, 68, 459, 784)), animation: false );
a.window.front;
a.view.background_(Color.white);
a.run;
a
a[1] = { | win, x, y, xscale = 1.005, yscale = 1.005 |
		Pen.setSmoothing(true);
		Pen.fillColor_(Color.grey(0, 10/255));
		Pen.translate(33, 66);
		90.do{|i|
			Pen.scale(xscale, yscale);
			Pen.fillOval(Rect.aboutPoint(Point(25, 90), 200/2, 200/2));
		};
	};

a[2] = { | win, x, y, xscale = 1.002, yscale = 1.002 |
		Pen.setSmoothing(true);
		Pen.fillColor_(Color.red(0.4, 4/255));
		Pen.translate(33, 66);
//		SCPen.scale(0.5,0.5);
		145.do{|i|
			SCPen.scale(xscale, yscale);
			Pen.fillOval(Rect.aboutPoint(Point(25, 90), 200/2, 200/2));
		};
	}
a[2] = nil;
	
// A pattern can be assigned to an index, just like a function
// drawFunc is the key for the graphics function that will take its 
// arguments from the pattern
a[1] = Pbind(*[
	drawFunc: { | x = 25, y = 90, xscale = 1.005, yscale = 1.05, iter = 90 |
		Pen.setSmoothing(true);
		Pen.fillColor_(Color.grey(0, 10/255));
		Pen.fillColor_(Color.red(0.5, 10/255));
		Pen.translate(0,0);
		iter.do{|i|
			Pen.scale(xscale, yscale);
			Pen.fillOval(Rect.aboutPoint(Point(x, y), 200/2, 200/2));
		};
	},
	xscale:	Pseg(Pwhite(0.0,1).linexp(0, 1, 1, 1.1),4),
	yscale:	Pseg(Pwhite(0.0,1).linexp(0, 1, 1, 1.1),4),
	dur: 0.01,
	x:	Pseg(Pseq([200, 500], inf), 10),
	y:	Pseg(Pseq([0, 100], inf), 2),	
])

a.resume
a.stop
.a.play
*/
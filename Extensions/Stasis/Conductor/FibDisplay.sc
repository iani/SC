/* IZ May 22, 2010
Graphic display of the progress of the fibonacci structure. 

d = FibDisplay(Fib.ascending(15).flat.size);
d.numLevels;

(
d = FibDisplay.new;
d.show;

t = { c = TempoClock(10); };
a = SyncSender(clockFunc: t);
a.pattern = Pfib(Fib.ascending(15)).asPbind(a);
a.start;
)

c.tempo = 1;

*/


FibDisplay {
	var <numBeats = 144;	// number of total beats contained in the Fibonacci structures to be played
	var <oscResponder;		// the oscResponder that gets the data for each new branch
	var <>bounds;
	var <numLevels;
	var <xScale, <yScale;
	var <window;
	var <fibData;
	var <rects, <colors;
	var <margin = 5;
	var <labelResponder;	// for getting the labels to print the phrase where we are at. 
	var <label1 = "", <label2 = "", label3 = "";
	var <labelCache;

	*new { | numBeats = 144, oscMessage = 's_branch', bounds |
		^this.newCopyArgs(numBeats).init(oscMessage, bounds);
	}

	init { | oscMessage, argBounds |
		oscResponder = OSCresponder(nil, oscMessage, { | time, resp, msg |
			{ this.addBranch(msg[1..]); }.defer;
		}).add;
		this.rescale(argBounds ?? { this.defaultWindowBounds}, numBeats);
		labelResponder = SyncResponder(SyncSender.defaultSyncMessage);
		labelResponder.addDependant({ | resp, msg, label |
			labelCache = label.asString;
			if (labelCache[2..4] == "sta") { 
				label1 = labelCache[8..10];
				label2 = labelCache[11..13];
				label3 = labelCache[14..];
			}; 
		});
	}
	
	activate { oscResponder.add }
	deactivate { oscResponder.remove }

	defaultWindowBounds { ^Rect(0, 0, 1200, 300) }

	addBranch { | branch |
		fibData = fibData add: branch;
		this.rescale(bounds, branch[3]);
		rects = rects add: this.scaleBranch(branch);
//		colors = colors add: this.getColor(branch[
		if (window.notNil) { window.refresh };
	}

	scaleBranch { | branch |
		var pos, size, level;
		#pos, size, level = branch;
		^Rect(pos * xScale + margin, level * yScale + margin, size * xScale, numLevels - level * yScale);
	}

	show {
		if (window.isNil) { this.makeWindow };
		window.front;
	}

	makeWindow {
		window = Window("Fib", bounds);
		window.view.background = Color.white;
		window.onClose = { window = nil };
		window.drawHook = { this.draw };
	}

	draw {
		this.rescale(window.view.bounds, numBeats);
		Pen.stringInRect(label1, Rect(10, 5, 200, 20));
		Pen.stringInRect(label2, Rect(40, 5, 200, 20));
		Pen.stringInRect(label3, Rect(70, 5, 200, 20));
		
		rects do: { | r, i |
//			Pen.fillColor = colors@@i;
			Pen.addRect(r);			
//			Pen.fill;
		};
		Pen.stroke;
	}

	clear {
		fibData = rects = nil;
	}

	drawBranch { | pos = 0, size = 144, level = 1 |
		format("%, %, %, %", thisMethod.name, numLevels, level, numLevels - level).postln;
		Pen.addRect(Rect(pos * xScale + margin, margin, size * xScale, numLevels - level * yScale));
	}

	rescale { | newBounds, newNumBeats |
		if (newBounds == bounds and: { newNumBeats == numBeats }) { ^this };
		bounds = newBounds;
		numBeats = newNumBeats;
		numLevels = ({ | i = 1 |
			var a = 0, b = 1, c = 0;
			i do: { 
				c = a + b;
				a = b;
				b = c;
			};
			c;
		} ! 30);
		numLevels = numLevels.indexOf(numLevels detect: (_ >= numBeats));
		xScale = bounds.width - (margin * 2) / numBeats;
		yScale = bounds.height - (margin * 2) / numLevels;
		rects = fibData collect: (this.scaleBranch(_));
	}	
}
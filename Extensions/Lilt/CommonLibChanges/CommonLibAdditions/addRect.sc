/* IZ 0305: 
fromTop: Convert rect for y to be from top of screen instead of from bottom of screen.
fromRight: ...

Also utilities for calculating dimensions of GUI widgets in rows and columns: rows, cols, gap
These work better for me than FlowLayout for subdivisions that have variable sizes of subpanes both in x and y. Example: 
(
var dims1, dims2, dims3, w;
w = GUI.window.new("subdivisions", Rect(400, 400, 600, 300));
dims1 = w.view.bounds.insetBy(20, 20).cols(100).postln;
dims2 = dims1[0].g_rows(2, 2, 20).postln;
dims3 = dims2[0].g_cols(2, 0, 10, 10).postln;
dims3 do: { | r | GUI.textView.new(w, r) };
GUI.textView.new(w, dims2[1].gap(2, 0));
GUI.textView.new(w, dims1[1]);
w.front;
)
*/
+ Rect {
	fromTop {
		top = SCWindow.screenBounds.height - (top + height + 44);
	}
	fromRight {
		left = SCWindow.screenBounds.width - left - width;
	}
	resize { | argWidth, argHeight |
	// resize rect for window, keeping its top at the original y
			top = top + height - argHeight;
			height = argHeight ? height;
			width = argWidth ? width;
	}
	rows { | ... specs |
		var rows, next_rect, rest, y;
		y = top;
		rows = specs collect: { | s |
			if (s.isInteger) {} { s = (height * s).round(1).asInteger };
			next_rect = Rect(left, y, width, s);
			y = y + s;
			next_rect;
		};
		^if ((rest = top + height - y) > 2) {
			rows.add(Rect(left, y, width, rest))
		}{ rows };
	}
	g_rows { | x, y ... specs |
		var rows, g_rows;
		rows = this.rows(*specs);
		g_rows = rows.copy;
		g_rows.pop;
		g_rows do: _.gap(x, y);
		^rows;
	}
	cols { | ... specs |
		var cols, next_rect, rest, x;
		x = left;
		cols = specs collect: { | s |
			if (s.isInteger) {} { s = (width * s).round(1).asInteger };
			next_rect = Rect(x, top, s, height);
			x = x + s;
			next_rect;
		};
		^if ((rest = left + width - x) > 2) {
			cols.add(Rect(x, top, rest, height))
		}{ cols };
	}
	g_cols { | x, y ... specs |
		var cols, g_cols;
		cols = this.cols(*specs);
		g_cols = cols.copy;
		g_cols.pop;
		g_cols do: _.gap(x, y);
		^cols;
	}
	gap { | x = 2, y = 2 |
		width = width - x;
		height = height - y;
	}
}

/*
SCWindow.screenBounds;
(
5.do{|i|GUI.window.new("rft",Rect(10*i+300,i,	
	rrand(200, 400),300).fromTop)
	.front.view.background_(Color.black)};
)

GUI.window.new("1", Rect(0, 0, 200, 200).fromTop).front;
GUI.window.new("2", Rect(200, 200, 200, 200).fromTop).front;

*/
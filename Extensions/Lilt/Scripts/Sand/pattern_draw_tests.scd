/* iz Thursday; January 7, 2010: 5:54 PM
Using patterns to draw. 

!!!! This is not yet a script !!!!
*/


Pbind(
	\type, Pseq([\note, \note, \rest], inf),
	\degree, Pseq((0..10), inf),
	\dur, Pseq([0.05, 0.05, 0.2, 0.4], inf)
).play;




Event.addEventType(\line, { ~freq.value.postln });

Pbind(\type, \line).play;




// =======================================================================

(
w = Window("sand", Rect(10, 10, 450, 450).fromTop.fromRight);
v = UserView(w, w.view.bounds.insetBy(5, 5));
v.background = Color.white;
w.front;
r = true;
w.onClose = { r = false };
o = 200@200;
)

(
p = Array.rand(10, -15, 15).clump(2) collect: { | coords | coords[0]@coords[1] };
p = Pseq(p, 20).asStream;
a = [];
)

(
w.drawHook = {
	// set the Color
	Pen.strokeColor = Color.black;
	Pen.width = 1;
	Pen moveTo: o;
	c = o;
	a do: { | delta |
		c = c + delta;
		Pen lineTo: c;
	};
	Pen.stroke
};
)

(
{
	while { r and: { (n = p.next).notNil } }
	{
		a = a add: n;
		w.refresh;
		0.25.wait;
	}
}.fork(AppClock);
)

r = false;

// =======================================================================


(
w = Window("sand", Rect(10, 10, 450, 450).fromTop.fromRight);
v = UserView(w, w.view.bounds.insetBy(5, 5));
v.background = Color.white;
w.front;
r = true;
w.onClose = { r = false };
o = 150@50;
p = [10@10, -5@10, 5@(-10), 4@(-15)];
p = Pseq(p, 200).asStream;
a = [];
w.drawHook = {
	// set the Color
	Pen.strokeColor = Color.black;
	Pen.width = 1;
	Pen moveTo: o;
	c = o;
	a do: { | delta |
		c = c + delta;
		Pen.translate(delta.x, delta.y);
		Pen.rotate(0.02, o.x, o.y);
		Pen lineTo: o;
	};
	Pen.stroke
};

{
	while { r and: { (n = p.next).notNil } }
	{
		a = a add: n;
		w.refresh;
		(1/40).wait;
	}
}.fork(AppClock);
)

r = false;

// =======================================================================

(
w = Window("sand", Rect(10, 10, 450, 450).fromTop.fromRight);
v = UserView(w, w.view.bounds.insetBy(5, 5));
v.background = Color.white;
w.front;
r = true;
w.onClose = { r = false };
o = 150@50;
p = [10@10, -5@10, 5@(-10), 4@(-15)];
p = Pcollect({ | delta | 
	{
		Pen.translate(delta.x, delta.y);
		Pen.rotate(0.02, o.x, o.y);
		Pen lineTo: o;
	}
	}, Pseq(p, 200)
).asStream;
a = [];
w.drawHook = {
	// set the Color
	Pen.strokeColor = Color.black;
	Pen.width = 1;
	Pen moveTo: o;
	a do: _.value;
	Pen.stroke
};

{
	while { r and: { (n = p.next).notNil } }
	{
		a = a add: n;
		w.refresh;
		(1/40).wait;
	}
}.fork(AppClock);
)

r = false;

// =======================================================================

(
w = Window("sand", Rect(10, 10, 450, 450).fromTop.fromRight);
v = UserView(w, w.view.bounds.insetBy(5, 5));
v.background = Color.white;
w.front;
r = true;
w.onClose = { r = false };
o = 200@200;
p = Pcollect({ | delta | 
	{
		Pen.translate(delta[0], delta[1]);
		Pen.rotate(pi * 0.51, o.x - 20, o.y + 10);
		Pen lineTo: o;
	}
	}, Pseq(Array.rand(400, -2, 2).clump(2) + 50, 1)
).asStream;
a = [];
w.drawHook = {
	// set the Color
	Pen.strokeColor = Color.black;
	Pen.width = 1;
	Pen moveTo: o;
	a do: _.value;
	Pen.stroke
};

{
	while { r and: { (n = p.next).notNil } }
	{
		a = a add: n;
		w.refresh;
		(1/40).wait;
	}
}.fork(AppClock);
)

r = false;


// =======================================================================

(
w = Window("sand", Rect(10, 10, 450, 450).fromTop.fromRight);
v = UserView(w, w.view.bounds.insetBy(5, 5));
v.background = Color.white;
w.front;
r = true;
w.onClose = { r = false };
o = 200@200;
p = Pseq(Array.rand(400, 50, 350).clump(2), 1).asStream;

a = [];
w.drawHook = {
	// set the Color
	Pen.strokeColor = Color.black;
	Pen.width = 1;
	Pen moveTo: o;
	a do: { | p | Pen lineTo: (p[0]@p[1]) };
	Pen.stroke;
};

{
	while { r and: { (n = p.next).notNil } }
	{
		a = a add: n;
		w.refresh;
		(1/40).wait;
	}
}.fork(AppClock);
)

r = false;

// =======================================================================

(
w = Window("sand", Rect(10, 10, 450, 450).fromTop.fromRight);
v = UserView(w, w.view.bounds.insetBy(5, 5));
v.background = Color.white;
w.front;
r = true;
w.onClose = { r = false };
i = 0;
p = Pcollect({ | delta |
		i = i + 0.1;
		delta = delta + 100;
		(delta[0]@delta[1]).rotate(i).translate(200@200);
	},
	Pseq(Array.rand(400, -5, 5).clump(2), 1)
).asStream;

o = p.next;

a = [];
w.drawHook = {
	// set the Color
	Pen.strokeColor = Color.black;
	Pen.width = 1;
	Pen moveTo: o;
	a do: { | p | Pen lineTo: p };
	Pen.stroke;
};

{
	while { r and: { (n = p.next).notNil } }
	{
		a = a add: n;
		w.refresh;
		(1/40).wait;
	}
}.fork(AppClock);
)

r = false;

// =======================================================================

(
w = Window("sand", Rect(10, 10, 450, 450).fromTop.fromRight);
v = UserView(w, w.view.bounds.insetBy(5, 5));
v.background = Color.white;
w.front;
r = true;
w.onClose = { r = false };
c = 50@50;
p = Pcollect({
		c = c.rotate(pi/2);
		c = c.translate((50.rand - 51)@(50.rand - 51));
		c.copy.translate(200@200);
	},
	Pn([], 400)
).asStream;

o = p.next;

a = [];
w.drawHook = {
	// set the Color
	Pen.strokeColor = Color.black;
	Pen.width = 1;
	Pen moveTo: o;
	a do: { | p | Pen lineTo: p };
	Pen.stroke;
};

{
	while { r and: { (n = p.next).notNil } }
	{
		a = a add: n;
		w.refresh;
		(1/40).wait;
	}
}.fork(AppClock);
)

r = false;

p = Pcollect({
		c = c.translate(10@10);
		c = c.rotate(0.1);
		c.copy;
	},
	Pn([], 400)
).asStream;
p.next;

// =======================================================================

(
w = Window("sand", Rect(10, 10, 450, 450).fromTop.fromRight);
v = UserView(w, w.view.bounds.insetBy(5, 5));
v.background = Color.white;
w.front;
r = true;
w.onClose = { r = false };
c = 50@50;
p = Pcollect({
		c = c.rotate(pi/2);
		c = c.translate((50.rand - 51)@(50.rand - 51));
		c.copy.translate(200@200);
	},
	Pn([], 400)
).asStream;

o = 100@100;

a = [];
w.drawHook = {
	// set the Color
	Pen.strokeColor = Color.black;
	Pen.width = 1;
	Pen moveTo: o;
	a do: { | p | 
		Pen.perform(\lineTo, (100.rand - 50)@(100.rand - 50));
		Pen.perform(\rotate, pi / 2, 200, 200);
//		Pen.lineTo((100.rand - 50)@(100.rand - 50));
//		Pen.rotate(pi / 2, 200, 200);
	};
	Pen.stroke;
};

{
	while { r and: { (n = p.next).notNil } }
	{
		a = a add: n;
		w.refresh;
		(1/40).wait;
	}
}.fork(AppClock);
)

r = false;

// =======================================================================

(
w = Window("sand", Rect(10, 10, 450, 450).fromTop.fromRight);
v = UserView(w, w.view.bounds.insetBy(5, 5));
v.background = Color.white;
w.front;
r = true;
w.onClose = { r = false };
c = 50@50;
p = Pcollect({ (100.rand - 50)@(100.rand - 50) },
	Pn([], 400)
).asStream;

o = 100@100;

a = [];
w.drawHook = {
	// set the Color
	Pen.strokeColor = Color.black;
	Pen.width = 1;
	Pen moveTo: o;
	a do: { | p | 
		Pen.perform(\lineTo, p);
		Pen.perform(\rotate, pi / 2, 200, 200);
	};
	Pen.stroke;
};

{
	while { r and: { (n = p.next).notNil } }
	{
		a = a add: n;
		w.refresh;
		(1/40).wait;
	}
}.fork(AppClock);
)

r = false;


// =======================================================================

(
w = Window("sand", Rect(10, 10, 450, 450).fromTop.fromRight);
v = UserView(w, w.view.bounds.insetBy(5, 5));
v.background = Color.white;
w.front;
r = true;
w.onClose = { r = false };
p = Pcollect({ 50.rand - 25 },
	Pn([], 400)
).asStream;

o = 200@200;

a = [];
w.drawHook = {
	// set the Color
	Pen.strokeColor = Color.black;
	Pen.width = 1;
	Pen moveTo: o;
	a do: { | p |
		Pen.perform(\rotate, pi / 2, o.x, o.y);
		Pen.perform(\translate, p, 0);
		Pen.perform(\lineTo, o);
	};
	Pen.stroke;
};

{
	while { r and: { (n = p.next).notNil } }
	{
		a = a add: n;
		w.refresh;
		(1/40).wait;
	}
}.fork(AppClock);
)

r = false;

// =======================================================================

(
w = Window("sand", Rect(10, 10, 450, 450).fromTop.fromRight);
v = UserView(w, w.view.bounds.insetBy(5, 5));
v.background = Color.white;
w.front;
r = true;
w.onClose = { r = false };
p = Pcollect({ 50.rand - 25 },
	Pn([], 400)
).asStream;

o = 200@200;

a = [];
w.drawHook = {
	// set the Color
	Pen.strokeColor = Color.black;
	Pen.width = 1;
	Pen moveTo: o;
	a do: { | p |
		Pen.perform(\rotate, pi / 3, o.x, o.y);
		Pen.perform(\translate, p, 0);
		Pen.perform(\lineTo, o);
	};
	Pen.stroke;
};

{
	while { r and: { (n = p.next).notNil } }
	{
		a = a add: n;
		w.refresh;
		(1/40).wait;
	}
}.fork(AppClock);
)

r = false;

// =======================================================================

(
w = Window("sand", Rect(10, 10, 450, 450).fromTop.fromRight);
v = UserView(w, w.view.bounds.insetBy(5, 5));
v.background = Color.white;
w.front;
r = true;
w.onClose = { r = false };
p = Pcollect({ 50.rand - 25 },
	Pn([], 400)
).asStream;

o = 200@200;

a = [];
w.drawHook = {
	// set the Color
	Pen.strokeColor = Color.black;
	Pen.width = 1;
	Pen moveTo: o;
	a do: { | p |
		Pen.perform(\rotate, pi / 5, o.x, o.y);
		Pen.perform(\translate, p, 0);
		Pen.perform(\lineTo, o);
	};
	Pen.stroke;
};

{
	while { r and: { (n = p.next).notNil } }
	{
		a = a add: n;
		w.refresh;
		(1/40).wait;
	}
}.fork(AppClock);
)

r = false;

// =======================================================================

(
w = Window("sand", Rect(10, 10, 450, 450).fromTop.fromRight);
v = UserView(w, w.view.bounds.insetBy(5, 5));
v.background = Color.white;
w.front;
r = true;
w.onClose = { r = false };
p = Pcollect({ 50.rand - 25 },
	Pn([], 400)
).asStream;

o = 200@200;

a = [];
w.drawHook = {
	// set the Color
	Pen.strokeColor = Color.black;
	Pen.width = 1;
	Pen moveTo: o;
	a do: { | p |
		Pen.perform(\rotate, pi / 2, o.x, o.y);
		Pen.perform(\translate, p, 0);
		Pen.perform(\lineTo, o);
	};
	Pen.stroke;
};

{
	while { r and: { (n = p.next).notNil } }
	{
		a = a add: n;
		w.refresh;
		(1/40).wait;
	}
}.fork(AppClock);
)

r = false;

// =======================================================================

(
w = Window("sand", Rect(10, 10, 450, 450).fromTop.fromRight);
v = UserView(w, w.view.bounds.insetBy(5, 5));
v.background = Color.white;
w.front;
r = true;
w.onClose = { r = false };
p = Pcollect({ [50.rand - 25, [pi / 2, pi / 3].choose] },
	Pn([], 400)
).asStream;

o = 200@200;

a = [];
w.drawHook = {
	// set the Color
	Pen.strokeColor = Color.black;
	Pen.width = 1;
	Pen moveTo: o;
	a do: { | p |
		Pen.perform(\rotate, p[1], o.x, o.y);
		Pen.perform(\translate, p[0], 0);
		Pen.perform(\lineTo, o);
	};
	Pen.stroke;
};

{
	while { r and: { (n = p.next).notNil } }
	{
		a = a add: n;
		w.refresh;
		(1/40).wait;
	}
}.fork(AppClock);
)

r = false;




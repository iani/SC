
//:w simple unique window

UniqueWindow(\simple, onClose: { | me | postf("The window named '%' was closed\n", me.name) });

//:y does not reopen: 

{ 
	8 do: { 
		UniqueWindow(\repeatsimple, onClose: { | me | postf("The window named '%' was closed\n", me.name) })
			.window.view.background  = Color.rand;
		"It's still the same window".postln;
		0.5.wait;
	};
	5.wait;
	UniqueWindow(\repeatsimple).window.close;
}.fork(AppClock);

//:c

r = {
	5 do: { | i |
		UniqueWindow(\otherWindow, {
			var w;
			w = Window.new("MyWindow", Rect(*Array.rand(4, 250, 400)));
			w.view.background = Color.rand(0.5, 1);
			StaticText(w, Rect(0, 0, 300, 50)).string = 
				format("if you close me I will reappear - up to 5 times.\ncounting: % times", i + 1);
			w;
		}, { postf("I closed for the %th time\n", i + 1); r.next }).object.front;
		nil.yield;
	};
	"THE END".postln;
}.fork(AppClock)

//:f UniqueFunction

u = { | a, b, c | [a, b, c].sum.postln; }.doOnce(10, 20, 30);
u.onRemove({ | uf | uf.result.postln; });
u.remove;



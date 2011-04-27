//:a
UniqueWindow(\test).onClose({ |...args| { args.postln } ! 10; });
//:b
\test2.window.onClose({ ({ 10.rand } ! 10).postln });

//:w simple unique window

UniqueWindow(\simple) onClose: { | me | postf("The window named '%' was closed\n", me.name) };


//:y does not reopen: 

{ 
	8 do: { 
		UniqueWindow(\repeatsimple).onClose({ | me | postf("The window named '%' was closed\n", me.name) })
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
			StaticText(w, Rect(0, 0, 300, 50))
			.string = format("if you close me I will reappear - up to 5 times.\ncounting: % times", i + 1);
			w;
		}).front.onClose(
		{
			postf("I closed for the %th time\n", i + 1); 
			r.next;
		});
		nil.yield;
	};
	"THE END".postln;
}.fork(AppClock)

//:a 
UniqueObject.at(\myWindow, { Window.new("MyWindow") })
	.front.bounds_(Rect(0, 0, 100, 100).moveBy(400 + 500.rand, 700.rand))
	.view.background_(Color.red);

Library.postTree;
//:b
UniqueObject.remove(\myWindow);

Library.postTree;

//:B 

{ 
	15 do: { 
		UniqueObject.at(\secondWindow, { 
			var w;
			w = Window.new("MyWindow");
			StaticText(w, Rect(0, 0, 300, 50)).string = "if you close me I will reappear within 5 seconds\n15 times";
			w.onClose = { UniqueObject.remove(\secondWindow); };
			w;
		}).front.bounds_(Rect(0, 0, 300, 100).moveBy(400 + 500.rand, 700.rand));
		5.wait;
	};
}.fork(AppClock);

//:c

{
	15 do: {
		UniqueWindow([\my, \other, \Window], { 
			var w;
			w = Window.new("MyWindow");
			StaticText(w, Rect(0, 0, 300, 50)).string = "if you close me I will reappear within 5 seconds\n15 times";
			w;
		}).front.bounds_(Rect(400, 400, 400, 100));
		5.wait;
	};
}.fork(AppClock)

//:_ NOT
{ 
	15 do: { 
		UniqueWindow([\my, \other, \Window], { 
			var w;
			w = Window.new("MyWindow");
			StaticText(w, Rect(0, 0, 300, 50)).string = "if you close me I will reappear within 5 seconds\n15 times";
			w.onClose = { UniqueObject.remove(\secondWindow); };
			w;
		}).front.bounds_(Rect(0, 0, 300, 100).moveBy(400 + 500.rand, 700.rand))
		.view.background_(Color.yellow);
		5.wait;
	};
}.fork(AppClock);

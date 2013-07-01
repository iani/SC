(
e = EventModel((freq: 12345));
e.makeWindow(
	[\numberBox, \freq],
	[\slider, \freq],
	[\button, \asdf, [["asdfasfd"],["13241234"]]]
)
)

f = EventModel((freq: 12345, amp: 0.5))
Window("test1", Rect(0, 0, 300, 200)).front.view.layout = f.simpleGuiLayout;
Window("test1", Rect(330, 0, 300, 200)).front.view.layout = f.simpleGuiLayout;

e.addEventListener(\test, \freq, { | val | postf("I received frequency %\n", val) });

(
e = EventModel((amp: 0.5, freq: 1500));

w = Window().front;

w.view.layout = VLayout(
	e.numberBox(\ablissima),
	e.slider(\ablissima, \freq),
	e.knob(\ablissima),
	e.numSlider(\ablissima)
)
)

(
e = EventModel((amp: 0.5, freq: 1500));

w = Window().front;

w.view.layout = HLayout(
	e.vslider(\ablissima, 'freq'),
	e.vslider(\ablissima),
	e.knob(\ablissima),
	e.vslider(\dexterius),
	e.numberBox(\dexterius),
	e.vknob(\dexterius),
)
)

(
e = EventModel();
w = Window().front;
w.view.layout = HLayout(
	*({ | i | e.fader(format("amp%", i + 1).asSymbol, 'amp') } ! 16)
)
)

MIDIIn.connectAll;
l = MIDIFunc.cc({ | ... args | args.postln });

(
e = EventModel();
w = Window().front;
w.view.layout = HLayout(
	*({ | i |
		var key;
		i = i + 1;
		key = format("amp%", i).asSymbol;
		e.addMIDI(key, 'amp', i);
		e.fader(key, 'amp')
	} ! 16)
)
)
)

e.disableMIDI;
e.disableMIDI(\amp2);

e.enableMIDI(\amp2);
e.enableMIDI;

(
e = EventModel((amp: 0.5, freq: 1500));

w = Window().front;

w.view.layout = VLayout(
	e.numberBox(\freq),
	e.labeledNumberBox(\amp),
	e.numberBox(\amp, { | key, num | HLayout(StaticText().string_("alternative amp"), num) }),
	e.slider(\freq, \freq),
	e.slider(\amp, \amp),
	e.knob(\freq, \freq),
	e.knob(\amp, \amp),
	e.numSlider(\freq, \freq)
)
)

e.updateAll;
e.put(\freq, 1000);

Notification.all;


(
e = EventModel((a: 1, b: 2));

w = Window().front;

v = Slider(w, Rect(10, 10, 300, 20));

v.addNotifier(e, \a, { | val | v.value = \freq.asSpec.unmap(val) });

x = Slider(w, Rect(10, 40, 300, 20));

x.addNotifier(e, \a, { | val | v.value = \freq.asSpec.unmap(val) });

x.action = { | me | e.put(\a, \freq.asSpec.map(me.value)) };

n = NumberBox(w, Rect(10, 80, 200, 20));

n.addNotifier(e, \a, { | val | n.value = val });
n.action = { | me | e.put(\a, me.value) };

)

e = EventModel((a: 1, b: 2));

\test.addNotifier(e, \a, { | ... args | args.postln; });


e.event;
e.put(\a, 100);

\test.objectClosed;
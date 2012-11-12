/* iz Mon 22 October 2012 11:33 AM EEST
Show a window with a "strip" of buttons or menus at the bottom left of the screen.

(
ActionStrip(width: 800, closeButton: true).addItems(
    "my button 1", { "test".postln; },
    "my menu 1", [
        "test1", { "test1 from menu".postln },
        "test2", { "test2 from menu".postln; },
    ]
)
)
//:-00StartupMenu
var font = Font.default.size_(10);
var layout = HLayout().spacing_(1).margins_([1, 1, 1, 1]);
{
	Window("main menu", Rect(0, 0, 1000, 25), border: false).front.layout = layout;
	3.wait;
	layout add: Button().states_([["test1"]]).font_(font);
	layout add: Button().states_([["test 2"]]).font_(font);
}.fork(AppClock);

//:

*/


// TODO: Rewrite without AppModel
ActionStrip {
	var <width, <closeButton, <window, <layout;
	*new { | width = 1000, closeButton = true |
		^super.newCopyArgs(width, closeButton).makeWindow;
	}

	makeWindow {
		window = Window(border: false);
		layout = HLayout().margins_([1, 1, 1, 1]).spacing_(1);
		window.bounds_(Rect(0, 0, width, 25)).layout_(layout).front;
		if (closeButton) {
			layout add: Button()
			.states_([["x", nil, 
				Color.red]]).action_({ window.close }).font_(Font.default.size_(10))
				.fixedWidth_(30)
		}
	}

	addItems { | ... itemSpecs |
		var items;
		itemSpecs pairsDo: { | name, spec | layout add: this.makeItem(name, spec) };
	}

	makeItem { | name, spec |
		var view, items, actions;
		if (spec isKindOf: Array) {
			#items, actions = spec.clump(2).flop;
			view = PopUpMenu().items_([name] ++ items).action_({ | me |
				actions[me.value - 1].(me);
				me.value = 0;
			});
		}{
			if (name isKindOf: String) { name = [[name]] };
			view = Button().states_(name.(this)).action_({ | me | spec.(me) });
		};
		^view.font_(Font.default.size_(10))
	}
}
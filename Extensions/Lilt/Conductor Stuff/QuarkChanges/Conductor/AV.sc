/*

An AV is an SV that stores an action in instance variable 'action'.
AV creates a button as default view to perform the action. It also stores an array of states that are used to create the button and show its labels (as well as optional colors). 

AV derives the states and actions an array of specs, where each spec is a string label and a function. Optionally one can only give one function or one name (label of the button) and one function. 

The default GUI presentation in ConductorGUI is a button.

Possible formats for setting states and actions: 
AV({ "Hello".postln; }) -> create button labeled "GO" with action { "Hello".postln; }
AV(["Hello", { "Hello".postln; }]) -> create button labeled "Hello" with action { "Hello".postln; }
AV([["Hello", Color.red, Color.black], { "Hello".postln; }]) 	-> create red button labeled "Hello" with black background, red text, and action { "Hello".postln; }

(
c = Conductor.make{ | conductor, a, b, x |
	~x = AV([["xxxxx", { "xxxxx".postln }]]);
	~y = AV([["hello", { "Hello".postln }], ["go", { "go".postln }]]);
	~w = AV([["hello", { "Hello".postln }], ["go", { "go".postln }], ["third", { "third".postln }]]);
	~z = AV([["hello", { "Hello".postln }], ["go", { "go".postln }], [["go2", Color.red], { "go2".postln }]]);
	~z.items.postln;
	a.spec_(~y.spec);
	b.spec_(~z.spec);
	~y.action_({|y| a.value_(y.value)});
	~z.action_({|y| b.value_(y.value)});
	// define custom gui properties
	conductor.gui.use{ 
		~popupRect = Rect(0,0,200, ~h); 
		~listRect = Rect(0,0,200, 400); 
	
	};
	// An SV is a CV whose value is an index into an associated array.
	// ~popup and ~listview display the entries in the array.
	
	conductor.gui.keys = #[w, x, [y,a], [z,b]];
	conductor.gui.guis = (z: \list, a: \numerical, b: \numerical);
};
c.show(w: 450)	
)

\radiobuttons2 creates radio buttons for all but the first item, and uses the first item's function as action for when all selections are deactivated: 
(
Conductor.make { | conductor, labeled_radio, labeled_radio2 |
	var radio2action;
	~labeled_radio = AV([["", { "deactivated".postln; }], ["hallo2", { "hallo2".postln; }], ["hallo3", { "hallo3".postln; }]]);
	radio2action = {
		var previous_value = 0;
		{ | av |
			postf("unsetting previous value: %\n", previous_value);
			postf("the new value will now be: %\n", previous_value = av.value);
		}
	}.value;
	~labeled_radio2 = AV({ | i | ["value: " ++ i.asString, radio2action] } ! 20);
	conductor.gui.guis = (labeled_radio: \radiobuttons2, labeled_radio2: \radiobuttons2);
}.show;
)

*/

AV : SV {
	var <states, <actions;
	// AV support in ConductorGUI:
	*initClass {
		StartUp.add {
			ConductorGUI.osx.use {
				~button = {  |win, name, cv, rect |
					rect = rect ?? ~buttonRect;
					cv.asArray.do( SCButton(win,rect).connect(_) );
				};
				~smallButton = {  |win, name, cv, rect | // win, name, cv, rect
					rect = rect ?? ~numericalRect;
					cv.asArray.do( SCButton(win,rect).connect(_) );
				};
				~buttonRect = { Rect(0,0,~labelW * 2,~h) };
				~avGUI = { | win, name, av, rect | ~button.value(win, name, av, rect) };
				~radiobuttons2Rect = { Rect(0, 0, ~labelW, ~h) };
				~radiobuttons2 = { |win, name, av, rect|
					var buttons, link,  size, preVal;
					rect = rect ?? ~radiobuttons2Rect;
					~label.value(win, name);
					av.asArray.do { |av|
						size = av.spec.maxval;
						buttons = { | i |
							i = av.items[i+1];
							~simpleButton.value(win, rect)
								.states_([
									[i, Color.red, Color.grey(0.4)],
									[i, Color.red, Color.blue(0.3)]
								])

						} ! size;
						buttons.do { | bt, i |
							bt.action_({ | ...x |
								if (preVal > 0) { buttons[preVal - 1].value_(0) }; 
								if (bt.value == 0) 
									{ preVal = 0; av.value = 0 } 
									{ av.value = preVal = i + 1};
							})
						};
						link = av.action_( {
							if (preVal > 0) { buttons[preVal - 1].value = 0 };
							preVal = av.value;
							if (preVal > 0) { buttons[preVal - 1].value = 1 }
						});
						preVal = 0;				
						av.value = av.value;		// sync GUI to av
						buttons[0].onClose_({link.remove});
					}				
				}
							
			}
		}
	}

	*new { | states_actions, default = 0 | 			
		^super.new.make_states_actions(states_actions, default);
	}
	make_states_actions { | states_actions, default = 0 |
		var state, action;
		var index = 0;
		if (states_actions.isKindOf(Function)) {
			states_actions = [[["GO"], states_actions]];
		}{ if (states_actions[1].isKindOf(Function)) {
			states_actions = [states_actions];
		}};
		states_actions do: { | sa |
			#state, action = sa;
			if (state.isKindOf(String)) { state = [state] };
			states = states.add(state);
			actions = actions.add(action);
		};
		this.items_(states collect: _.first, default);
		this.action = { | av |
			actions.wrapAt(av.value - 1).(this)
		};
	}
	input_	{ | in |
		this.value_(in);
	}
	input	{
		^value;
	}
	draw { |win, name =">"|
		~avGUI.value(win, name, this);
	}
	updateValue { | newValue |
		// update value without executing actions. 
		// needed for buttons to update gui states from presets of the model
		value = newValue;
		this.changed(\update);
	}
}

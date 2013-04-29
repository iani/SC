/* IZ Fri 31 August 2012 10:08 PM EEST
Redo of AppModel, with new classes for values and views: Value and Widget classes.
*/

AppModel {
	classvar <>enabled;	// previously enabled AppModel: Disabled when the next one becomes active
	var <values;  /* IdentityDictionary: Adapters holding my values per name */

	*new { | ... args | ^this.newCopyArgs(IdentityDictionary.new, *args); }

	at { | name | ^values[name].value; }
	put { | name, value | this.getAdapter(name).valueAction = value }
	getValue { | name, adapter |
		// Access adapter. Create one only if it does not already exist
		var value;
		value = values[name];
		if (value.isNil) { // if it does not exist, create it and set its adapter variable.
			value = Value(this);
			values[name] = value;
			adapter !? {
				value.adapter = adapter;
				adapter.container = value;
			};
			^value;
		}{
			^value; 	// Else return it as is
		}
	}

	// removing connections and inputs
	objectClosed { // not used yet?
		super.objectClosed;
		values do: _.objectClosed;
	}

	// enabling and disabling MIDI and OSC input
	enable { | disablePrevious = false |
		if (disablePrevious) { enabled !? { enabled.disable }; };
		values do: _.enable;
		enabled = this;
	}
	disable {
		values do: _.disable;
		enabled = nil;
	}

	updateListeners { values do: _.updateListeners }

	// =========== Adding windows ============
	window { | windowInitFunc |
		AppNamelessWindow(this, windowInitFunc);
	}

	stickyWindow { | owner, name = \window, windowInitFunc |
		AppStickyWindow(this, owner ? this, name, windowInitFunc);
	}

	windowClosed { | window, action |
		this.addNotifier(window, \windowClosed, { | widget | action.(widget) })
	}
	windowToFront { | window, action |
		this.addNotifier(window, \windowToFront, { | widget | action.(widget) })
	}
	windowEndFront { | window, action |
		this.addNotifier(window, \windowEndFront, { | widget | action.(widget) })
	}

	// =========== Adding views ============
	view { | view | ^AppNamelessView(this, view) }

	widget { | name, view | ^Widget(this, name, view) }

	numberBox { | name | ^Widget(this, name, NumberBox()).simpleNumber; }
	knob { | name, spec | ^Widget(this, name, Knob()).mappedNumber(spec); }
	slider { | name, spec | ^Widget(this, name, Slider()).mappedNumber(spec); }
	button { | name, action | ^Widget(this, name, Button()).action_(action); }
	textField { | adapterName |
		^Widget(this, adapterName, TextField()).text;
	}
	staticText { | adapterName, string = "<empty>" |
		^Widget(this, adapterName, StaticText()).text.do({ | me |
			if (me.value.adapter isKindOf: TextAdapter) { me.value.adapter.string_(me, string); }
		});
	}
	textView { | adapterName, viewName |
		^Widget(this, adapterName, TextView()).textView;
	}
	listView { | name, getItemsFunc |
		^Widget(this, name, ListView()).list(getItemsFunc);
	}
	popUpMenu { | name, getItemsFunc |
		^Widget(this, name, PopUpMenu()).list(getItemsFunc);
	}
	actionMenu { | name, itemActionPairs |
		^this.popUpMenu(name, { | me |
			me.items.flop.first;
		}).action_({ | me |
			me.items.flop[1][me.view.value].(me);
		}).items_(this, itemActionPairs)
	}
	listIndex { | name, view, startAt = 1 |
		^Widget(this, name, view ?? { NumberBox() }).listIndex(startAt);
	}
	listSize { | name, view |
		^Widget(this, name, view ?? { NumberBox() }).listSize;
	}
	listItem { | name, view, getItemFunc |
		^Widget(this, name, view ?? { TextField() }).listItem(getItemFunc);
	}

	// GUI for editing names of items, creating new items and deleting items.
	// Consists of a HLayout with a StaticText, TextField, and an Exit button.
	// Visibility of these items is toggled by receiving updates from itemEditMenu
	// The items contents and actions can change to apply the same views work with any number
	// of different lists. So many different lists can be edited by one single itemEditor gui,
	// The connection of a new list to the editor GUI is done with method itemEditMenu.
	itemEditor { | name = \editor | ^ListItemEditor(this, name); }

	// Make menu for editing the items of a list
	// The menu actions send notifications to an itemEditor item (see method above).
	// The itemEditor changes its display and actions to work with the required list.
	itemEditMenu { | name = \list, getFunc, newFunc, renameFunc, deleteFunc, editor = \editor |
		var menu, menuItems;
		menu = this.popUpMenu(name);
		editor = this.getValue(editor);
		getFunc = getFunc ?? {{ | me | if (me.item.isNil) { nil } { me.item.asString }}};
		newFunc = newFunc ?? {{ | me, string | me.value.append(string) }};
		renameFunc = renameFunc ?? {{ | me, string | me.value.replace(string, me.value.index) }};
		deleteFunc = deleteFunc ?? {{ | me | me.value.adapter.delete(me) }};
		menuItems = [format("Edit % list", name),
			format("New % item", name),
			format("Rename % item", name),
			format("Delete % item", name),
		];
		menu.updateAction(\list, { menuItems });
		menu.updateAction(\index, { menuItems });
		menu.updater(editor, \itemName, { | me, sender, stringRef |
			if (menu === sender) { stringRef.value = getFunc.(menu, editor) };
		});
		menu.updater(editor, \append, { | me, sender, stringRef |
			if (me === sender) { newFunc.(me, stringRef) };
		});
		menu.updater(editor, \rename, { | me, sender, stringRef |
			if (me === sender) { renameFunc.(me, stringRef) };
		});
		menu.updater(editor, \delete, { | me, sender |
			if (me === sender) { deleteFunc.(me) };
		});
		menu.addNotifier(editor, \exit, {
			menu.view.stringColor = Color.black;
			menu.view.background = Color.white;
			menu.view.value = 0;
		});
		menu.updater(editor, \menu, { | me, activeMenu |
			if (me !== activeMenu) {
				me.view.stringColor = Color.black;
				me.view.background = Color.white;
				me.view.value = 0;
			}
		});
		menu.action = { | me |
			editor.changed(\menu, me);	// reset selection of any other dependent menus
			me.view.stringColor = [Color.white, Color.blue, Color.green, Color.red][me.view.value];
			me.view.background = [Color.black, Color.white, Color.black, Color.white][me.view.value];
			editor.adapter.perform([\exit, \append, \rename, \delete][me.view.value], me)
		};
		menu.view.items_(menuItems);
		^menu;
	}

	radioButtons { | name, items, selectFunc, unselectFunc, onState, offState |
		// returns array of Button Views (not Widgets)
		this.getValue(name).adapter = ListAdapter(nil, items);
		^items collect: { | item, index |
			this.radioButton(name, item, index, selectFunc, unselectFunc, onState, offState, items)
		}
	}
	radioButton { | name, item, index, selectFunc, unselectFunc, onState, offState, items |
		var updateAction;
		updateAction = { | sender, me |
			if (me.value.index == index) {
				me.view.value = 0;
				selectFunc.(me, sender);	// do this with the newly selected item
			}{ me.view.value = 1 };
		};
		onState ?? { onState = [index, nil, Color.yellow] };
		offState ?? { offState = [index] };
		^this.button(name)
			.action_({ | me |
				unselectFunc.(me);	// do this with the current item, before it changes.
				me.index_(index)
			})
			.updateAction(\list, updateAction)
			.updateAction(\index, updateAction)
			.view.states_([
				onState.(item, index, items, this),
				offState.(item, index, items, this)
			])
	}

	// under development! New type of ListAdapter needed here.
	multiListView { | name, getItemFunc, selectionMode = \extended |  // can also be: \multi
		^Widget(this, name, ListView().selectionMode = \extended)
			.action_({ | me | me.view.value.postln; me.view.item.postln });
	}

	soundFileView { | name | ^Widget(this, name, SoundFileView()).soundFileView; }

	// following need review - possibly their own adapter classes

/* // TODO
	rangeSlider { | name, loSpec, hiSpec |
		^Widget(this, name, RangeSlider()).biMappedNumber(loSpec, hiSpec);
	}
	slider2D { | name, xSpec, ySpec  |
		^Widget(this, name, Slider2D()).biMappedNumber(xSpec, ySpec);
	}
	multiSliderView { | name | ^AppValueView(this, name, MultiSliderView()); }

	envelopeView { | name | ^AppValueView(this, name, EnvelopeView()); }

	dragSource { | name | ^AppView(this, name, DragSource()); }
	dragSink { | name | ^AppView(this, name, DragSink()); }
	dragBoth { | name | ^AppView(this, name, DragBoth()); }
	scopeView { | name | ^AppView(this, name, ScopeView()); }
	multiSliderView { | name | ^AppValueView(this, name, MultiSliderView()); }
	movieView { | name | ^AppView(this, name, MovieView()); }
*/
	addMIDI { | specs |
		specs pairsDo: { | key, spec |
			this.getValue(key).addMIDI(*spec);
		}
	}
}



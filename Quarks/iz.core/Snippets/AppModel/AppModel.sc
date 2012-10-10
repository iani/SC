/* IZ Fri 31 August 2012 10:08 PM EEST

Redo of AppModel, with radically redone classes for values and views: Value and Widget classes.
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
	// Consists of a HLayout with a StaticText, TextField, and a Cancel button.
	// Visibility of these items is toggled by receiving updates from itemEditMenu
	// The items contents and actions can change to apply the same views work with any number
	// of different lists. So many different lists can be edited by one single itemEditor gui,
	// The connection of a new list to the editor GUI is done with method itemEditMenu. 
	// UNDER DEVELOPMENT
	itemEditor { | name = \editor |
		var itemEditMenu; // the menu currently editing: used tby he button action
		^HLayout( // TODO: Add functionality!
		// itemFunc is used only as action for the textField
			this.staticText(name) // string changes according to list name + action type
			.updateAction(\text, {})
			.updateActionArray(\changeTo, { | f, label, n, m, t, me | me.view.string = label; })
			.showOn(\show, false).view,
			// TODO: The textField's getItemFunc, delete, and rename funcs
			// must be customizable, and able to access different lists
			this.textField(name).showOn(\show, false)
			// string changes according to item from list
			// and action changes according to action type + list
			.updateActionArray(\changeTo, { | func, l, name, m, t, me |
				me.action = func;
				me.view.string = name;
			})
			.view,
			this.button(name).showOn(\show, false)
			.updateActionArray(\changeTo, { | f, l, n, argMenu |
				// Other menus should reset here!: 
				itemEditMenu.value.changed(\editAction, argMenu);
				itemEditMenu = argMenu;
			})
			.view.action_({ itemEditMenu.view.valueAction = 0; }).states_([["Cancel"]]),
		);
	}

	// Make menu for editing the items of a list
	// The menu actions send notifications to an itemEditor item (see method above).
	// The itemEditor changes its display and actions to work with the required list. 
	itemEditMenu { | name = \list, newItemFunc, renameItemFunc, deleteItemFunc, editor = \editor |
		var menu, menuItems, cancelFunc;
		menu = this.popUpMenu(name);
		editor = this.getValue(editor);
		menuItems = [format("Edit menu for '%'", name), 
			format("New '%' item", name),
			format("Rename '%' item", name),
			format("Delete '%' item", name),
		];
		menu.updateAction(\list, { menuItems });
		menu.updateAction(\index, { menuItems });
		menu.updateActionArray(\editAction, { | sender, notification, me |
			if (sender !== me) { me.view.value = 0 };
		});
		newItemFunc = newItemFunc ?? {{ | me | menu append: me.view.string; }};
		renameItemFunc = renameItemFunc ?? {{ | me | menu.item = me.view.string }};
		deleteItemFunc = deleteItemFunc ?? {{ menu.delete }};
		menu.action = { | me |
			editor.changed(\show, [false][me.view.value] ? true);
			editor.changed(\changeTo,
				*([
				[cancelFunc, "", "", menu],
				[newItemFunc, "Edit, then type 'return' to create new item:", 
					"asdf".scramble,
					menu],
				[renameItemFunc, "Edit, then type 'return' to change this item:",
					"asdf".scramble, 
					menu],
				[deleteItemFunc, "Type 'return' to delete this item:", 
					"asdf".scramble, 
					menu],
				][me.view.value])
			);
		};
		menu.view.items = menuItems;
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
			} { me.view.value = 1 };
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



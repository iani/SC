/* iz Thu 11 October 2012  9:44 AM EEST

Simple gui for editing items of lists

*/

ListItemEditor {
	var <model, <name = \editor, <>container;
	var <label, <textField, <deleteButton, <exitButton;

	*new { | model, name |
		^this.newCopyArgs(model, name).init;
	}

	init {
		model.getValue(name).adapter = this;
		label = model.staticText(name).updateAction(\text, {});
		textField = model.textField(name);
		deleteButton = model.button(name);
		exitButton = model.button(name);
		exitButton.view.action_({ this.exit }).states_([["Exit"]]);
		this.hide;
	}

	// default gui. Other gui methods, if defined, may be called directly.
	gui { | font, labelStretch = 2, textStretch = 3, deleteStretch = 3, exitStretch = 1 |
		^this.hLayout(font, labelStretch, textStretch, deleteStretch, exitStretch)
	}

	// return HLayout with views. Other methods may be added for different layouts.
	hLayout { | font, labelStretch = 2, textStretch = 3, deleteStretch = 3, exitStretch = 1 |
		font = font ?? { Font.default };
		^HLayout(
			[label.view.font_(font), s: labelStretch],
			[textField.view.font_(font), s: textStretch],
			[deleteButton.view.font_(font), s: deleteStretch],
			[exitButton.view.font_(font), s: exitStretch]
		);
	}

	exit {
		container.changed(\exit);
		this.hide;
	}
	
	append { | list |
		this setList: list;
		label.view.string = "Edit, press 'return' to create item:";
		textField.view.string = this getName: list;
		textField.action = { | me | container.changed(\append, list, me.view.string) };
		this.show(\label, \textField, \exitButton);
	}

	getName { | list |
		var itemName;
		container.changed(\itemName, list, itemName = `"");
		^itemName.value;
	}

	rename { | list |
		this setList: list;
		label.view.string = "Edit, press 'return' to rename item:";
		textField.view.string = this getName: list;
		textField.action = { | me | container.changed(\rename, list, me.view.string) };
		this.show(\label, \textField, \exitButton);
		
	}
	
	delete { | list |
		this setList: list;
		label.view.string = "Delete item:";
		deleteButton.view.states_([[this getName: list]])
			.action_({ container.changed(\delete, list); });
		this.show(\label, \deleteButton, \exitButton);
	}

	setList { | listWidget |
		var val;
		val = listWidget.value;
		textField.replaceNotifier(val, \list, {
			 textField.view.string = this getName: listWidget
		});
		deleteButton.replaceNotifier(val, \list, {
			 deleteButton.view.states = [[this getName: listWidget]]
		});
		textField.replaceNotifier(val, \index, {
			 textField.view.string = this getName: listWidget
		});
		deleteButton.replaceNotifier(val, \index, {
			 deleteButton.view.states = [[this getName: listWidget]]
		});
	}

	show { | ... visibleViews |
		[\label, \textField, \deleteButton, \exitButton] do: { | viewName |
			if (visibleViews includes: viewName) {
				this.perform(viewName).view.visible = true;
			}{
				this.perform(viewName).view.visible = false;
			}
		};
	}
	
	hide {
		[label, textField, deleteButton, exitButton] do: { | w | w.view.visible = false };
	}
	
	updateMessage { ^\value }
}
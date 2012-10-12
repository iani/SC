/* iz Thu 11 October 2012  9:44 AM EEST

Simple gui for editing items of lists

*/

ListItemEditor {
	var <model, <name = \editor, <>container;
	var <label, <textField, <deleteButton, <cancelButton;

	*new { | model, name |
		^this.newCopyArgs(model, name).init;
	}

	init {
		model.getValue(name).adapter = this;
	}

	gui {
		label = model.staticText(name).updateAction(\text, {});
		textField = model.textField(name);
		deleteButton = model.button(name);
		cancelButton = model.button(name);
		^HLayout(
			label.view.visible = false,
			textField.view.visible = false,
			deleteButton.view.visible = false,
			cancelButton.view.action_({ this.cancel }).states_([["Cancel"]]).visible = false
		);
	}

	cancel {
		container.changed(\cancel);
		this.hide;
	}
	
	append { | list |
		this setList: list;
		label.view.string = "Edit, press 'return' to create item:";
		textField.view.string = this getName: list;
		textField.action = { | me | container.changed(\append, list, me.view.string) };
		this.show(\label, \textField, \cancelButton);
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
		this.show(\label, \textField, \cancelButton);
		
	}
	
	delete { | list |
		this setList: list;
		label.view.string = "Delete item:";
		deleteButton.view.states_([[this getName: list]])
			.action_({ container.changed(\delete, list); });
		this.show(\label, \deleteButton, \cancelButton);
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
		[\label, \textField, \deleteButton, \cancelButton] do: { | viewName |
			if (visibleViews includes: viewName) {
				this.perform(viewName).view.visible = true;
			}{
				this.perform(viewName).view.visible = false;
			}
		};
	}
	
	hide {
		[label, textField, deleteButton, cancelButton] do: { | w | w.view.visible = false };
	}
	
	updateMessage { ^\value }
}
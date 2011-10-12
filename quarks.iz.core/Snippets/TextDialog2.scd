
/*
TextDialog2 {
	var message, inputText, okFunc, cancelFunc;
	var window;
	*new { | message = "input", inputText = "something", okFunc, cancelFunc, bounds |
		^this.newCopyArgs(message, inputText, okFunc, cancelFunc).init(bounds);
	}

	init { | bounds |
		var messageField, inputField, okButton, cancelButton, layout, view;
		window = Window("", bounds ?? { Window.centeredWindowBounds });
		window.userCanClose = false;
		layout = window.addFlowLayout(5@5, 3@3);
		view = window.view;
		messageField = StaticText(view, Rect(0, 0, layout.innerBounds.width, 180));
		messageField.string = message;
		messageField.align = \center;
		inputField = TextField(view, Rect(0, 0, layout.innerBounds.width, 180));
		inputField.string = inputText;
/*
		okButton = Button(window, layout.indentedRemaining.width = layout.indentedRemaining.width / 2;
		).states_([["OK"]]);
		okButton.action = {
			okFunc.(inputField.string);
			window.close;
		};
		cancelButton = Button(window, layout.indentedRemaining).states_([["CANCEL"]]);
		cancelButton.action = {
			cancelFunc.(inputField.string);
			window.close;
		};
*/		window.front;	
	}
}
*/

/* (IZ 2005-09-03) {
Utilities for popping up dialog windows:
Warn: present a message in a pop up window instead of posting it on the Untitled window.

DialogWindow.new
Warn("THIS IS NOT right");
Confirm("Is this right?", {| ok | (if (ok) { "yes"}{"no"}).postln});
TextDialog("Enter some text", "My default text is customizeable\nand more ... ...",
	{| ok, text | if (ok) 
		{ 
			"You entered the text between the ==== lines:\n====================".postln;
			text.postln;
			"====================".postln;
		}{
			"Enter text was canceled ...".postln;
		}
	}
);



} */


DialogWindow {
	var <>title, <>text = "", <>onClose, <>numButtons = 1;
	var <>editable = false, <>ok = true;
	var x = 200, y = 200, width = 400, height = 220;
	var window, textView, yesButton, noButton;
	*new { | title, text = "", onClose, numButtons = 1, editable = false, ok = true,
		x = 200, y = 200, width = 400, height = 220 |
			^this.newCopyArgs(title, text, onClose, numButtons, editable, ok, x, y, width, height).init;
	}
	init {
		window = GUI.window.new(title ? "Warning:", Rect(x, y, width, height).fromTop)
		.onClose_({ |me|
			onClose.(ok, textView.string, me);
			window = nil;
		});
		textView = GUI.textView.new(window, Rect(0,0, width, height - 25))
			.font_(this.textFont)
			.stringColor_(this.textColor)
			.string_(text ? "-")
			.editable_(editable);
		yesButton = GUI.button.new(window, Rect(3, height - 23, width - 8 / numButtons, 20))
			.states_([["OK"]])
			.action_({ ok = true; window.close });
		yesButton.focus(true); // if only view, then focus it anyway
		if (numButtons == 2) {
			noButton = GUI.button.new(window, Rect(width / 2, height - 23, width / 2 - 6, 20))
				.states_([["CANCEL"]])
				.action_({ ok = false; window.close });
			noButton.focus(ok.not); // if present, focus if not ok
		};
		if (editable) { textView.focus(true);};
		window.front;
	}
	textColor { ^Color.red }
	textFont { ^Font("Helvetica-Bold", 12) }
	autoCloseAfter { | seconds = 7 |
		{ if (window.notNil) { window.close } }.defer(seconds);
	}
	close { | accept = false |
		if(window.notNil) {
			if (accept) { ok = true };
			window.close;
		}
	}
}

Warn : DialogWindow {
	*new { | message, title, onClose, x = 200, y = 200, width = 400, height = 220 |
		^super.new(title ? "WARNING:", message, onClose, x: x, y: y, width: width, height: height);
	}
}

Confirm : DialogWindow {
	*new { | message, onClose, ok = false, x = 200, y = 200, width = 400, height = 220 |
		^super.new("Confirm:", message, onClose, 2, false, ok, x: x, y: y, width: width, height: height);
	}
}

TextDialog : DialogWindow {
	*new { | title, message, onClose, ok = true, x = 200, y = 200, width = 400, height = 220 |
		^super.new(title ? "Edit text:", message, onClose, 2, true, ok, x: x, y: y, width: width, height: height);
	}
	textColor { ^Color.black }
	textFont { ^Font("Monaco", 12) }
}
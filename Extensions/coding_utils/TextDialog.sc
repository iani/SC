/*

TextDialog("just some test", "input some text here", { | t | t.postln; "was okd".postln; }, { "cancelled".postln; });

ListSelectDialog("just some test", ["alpha", "beta"], { | t | t.postln; "was okd".postln; }, { "cancelled".postln; });

*/

TextDialog {
	var message, inputText, okFunc, cancelFunc;
	var window;
	*new { | message = "input", inputText = "something", okFunc, cancelFunc, bounds |
		^this.newCopyArgs(message, inputText, okFunc, cancelFunc).init(bounds);
	}

	init { | bounds |
		var messageField, inputField, okButton, cancelButton;
		window = Window("", Rect(400, 400, 400, 400));
		window.userCanClose = false;
		messageField = StaticText(window, Rect(2, 2, 396, 180));
		messageField.string = message;
		messageField.align = \center;
		inputField = TextField(window, Rect(2, 184, 396, 180));
		inputField.string = inputText;
		
		okButton = Button(window, Rect(2, 370, 100, 28)).states_([["OK"]]);
		okButton.action = {
			okFunc.(inputField.string);
			window.close;
		};
		cancelButton = Button(window, Rect(298, 370, 100, 28)).states_([["CANCEL"]]);
		cancelButton.action = {
			cancelFunc.(inputField.string);
			window.close;
		};
		window.front;	
	}
}

ListSelectDialog {
	var message, items, okFunc, cancelFunc;
	var window;
	*new { | message = "input", items, okFunc, cancelFunc |
		^this.newCopyArgs(message, items.asArray, okFunc, cancelFunc).init;
	}
	
	init {
		var messageField, listView, okButton, cancelButton;
		window = Window("", Rect(400, 400, 400, 600));
		window.userCanClose = false;
		messageField = StaticText(window, Rect(2, 2, 396, 80));
		messageField.string = message;
		messageField.align = \center;
		listView = ListView(window, Rect(2, 84, 396, 480));
		listView.items = items;
		
		okButton = Button(window, Rect(2, 570, 100, 28)).states_([["OK"]]);
		okButton.action = {
			okFunc.(listView.value, listView.items[listView.value]);
			window.close;
		};
		cancelButton = Button(window, Rect(298, 570, 100, 28)).states_([["CANCEL"]]);
		cancelButton.action = {
			cancelFunc.(listView.value, listView.items[listView.value]);
			window.close;
		};
		window.front;	
	}
}
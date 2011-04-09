// Example of a snippet document without a shortcut key

//:a Make a window
~window.postln;

if (~window.isNil) {
	~window = GUI.window.new.front;
	~window.onClose = { ~window = nil };
}{
	~window.front
};

//:b close the window

if (~window.notNil) { ~window.close; };

//:! test a mouseUp action activation method for evaluating code parts
/*
Document.current.mouseUpAction_({arg doc;
	var line;
	line = doc.currentLine;
	if (line[0..2] == "//:") {
		line[3].postln;		
	};
	
})
*/

// was already built into DocListWindow so we dont want to overwrite it here. 

// "test".postln;

// But lets test here the keyboard action: 

Document.current.keyDownAction = { | me, char, mod, ascii, key |
	if (mod == 262401 and: { char == $n }) { "hello".postln; };
	[me, char, mod, ascii, key].postln;
};
nnnz
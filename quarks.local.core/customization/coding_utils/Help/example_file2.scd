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


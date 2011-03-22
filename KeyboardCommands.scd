
Document.current.keyDownAction = { | ... args | args.postln; }

// doc, char, modifiers, unicode,keycode
Document.current.keyDownAction = { | doc, char, mod, key, ascii |
	case 
	{ [mod, key] == [262401, 22] } { "control v".postln; }
	{ [mod, key].postln; }
}

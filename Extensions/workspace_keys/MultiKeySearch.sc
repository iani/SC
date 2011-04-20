/*

//:a Activate found item immediately
if (w.notNil) { w.close };
w = Window.new;
StaticText(w, Rect(2, 2, 300, 20)).string = "Selected items are executed immediately";
l = ListView(w, Rect(2, 25, 300, 300));
l.items = ["alpha", "beta", "gamma", "Alpha", "beste", "gimli", "01", "02", ". a point", "; semicolon", "- hyphen"];
l.keyDownAction = MultiKeySearch.new;
l.action = { | me | [me.value, me.items[me.value]].postln; };

w.front;
l.focus;

//:b Hit return key to activate found item
if (w.notNil) { w.close };
w = Window.new;
StaticText(w, Rect(2, 2, 300, 20)).string = "Hit return to act on the selected item";
l = ListView(w, Rect(2, 25, 300, 300));
l.items = ["alpha", "beta", "gamma", "Alpha", "beste", "gimli", "01", "02", ". a point", "; semicolon", "- hyphen"];
l.keyDownAction = MultiKeySearch(false); // or: MultiKeySearcy(doImmediately: false);
l.action = { | me | [me.value, me.items[me.value]].postln; };

w.front;
l.focus;

//:c With EZListView

l = EZListView(bounds: Rect(0, 0, 250, 400));
l.items = ["alpha", "beta", "gamma"] collect: { | s | s->{ s.postln } };
l.widget.keyDownAction = MultiKeySearch(keystrokeWaitInterval: 0.1);
l.widget.focus;

//:r rest
*/

MultiKeySearch {
	var <>doImmediately = true;
	var <>keystrokeWaitInterval = 1.0;
	var <string;
	var <done = false;

	*new { | doImmediately = true, keystrokeWaitInterval = 1.0 |
		^this.newCopyArgs(doImmediately, keystrokeWaitInterval);
	}
	
	value { | listview, char, mod, unicode, key |
		var items, match, endPos, topView, parent;
//		char.postln;
		if (unicode == 13) { listview.doAction; done = true; ^this };
		if (unicode == 16rF700, { listview.valueAction = listview.value - 1; ^this });
		if (unicode == 16rF703, { listview.valueAction = listview.value + 1; ^this });
		if (unicode == 16rF701, { listview.valueAction = listview.value + 1; ^this });
		if (unicode == 16rF702, { listview.valueAction = listview.value - 1; ^this });
		if (string.isNil or: { unicode == 127 }) {
			string = "\\" ++ char.asString; // escape the characters to prevent regexp meanings of special chars
			{ string = nil; }.defer(keystrokeWaitInterval);
		}{
			string = string ++ char.asString;
		};
		endPos = string.size;
		items = listview.items;
		match = items detect: { | i |
			string.matchRegexp(i, 0, endPos);
		};
		if (match.notNil) {
			listview.value = items indexOf: match;
			if (doImmediately) { listview.doAction };
		};
	}
}
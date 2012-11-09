/* iz Thu 25 October 2012  8:57 PM EEST

Provide a commonly needed behavior for creating unique instances:

If an instance exists under a certain key, return that instance.
Otherwise create it and register it under that key and then return it.

*/


Unique {
	*new { | path, class ... args |
		var new;
		new = Library.global.atPath(path);
		new ?? {
			new = class.new(*args);
			Library.global.putAtPath(path, new);
		};
		^new;
	}
}
/* 
Upon receiving a notification, evaluate an array of functions once.

Example of an application, see BufferResource:play. Code there: 
			NotifyOnce(key, \loaded, this, { this.playNow(play) });

Usage: 

// Evaluate the following any number of times, with different functions as actions:
NotifyOnce(sender, message, listener, action);

// then call all these actions at once when receiving the notification from NotificationCenter: 
NotificationCenter.notify(sender, message, listener);

*/


NotifyOnce : Resource {

	*mainKey { ^\chains }

	*makeKey { | sender, message, listener |
		^format("%:%:%", sender, message, listener).asSymbol
	}

	*new { | sender, message, listener, action |
		^super.new(sender, message, listener, sender, action).add(action);
	}

	add { | action | 
		object = object add: action;
	}

	init { | message, listener, sender |
		NotificationCenter.registerOneShot(sender, message, listener, this);
	}

	valueArray { | args |
		object do: { | f | f.valueArray(args) };
		this.remove;	
	}
}

/*
Chain : UniqueObject {
	*mainKey { ^\chains }

	*makeKey { | sender, message, listener |
		^format("%:%:%", sender, message, listener).asSymbol
	}

	add { | func | object = object add: func }

	*registerOneShot { | sender, message, listener, action |
	/* shortcut for:
	NotificationCenter.registerOneShot(sender, message, listener, Chain(sender) add: action });
	*/
		this.new(sender, message, listener).registerOneShot(sender, message, listener, action);
	}

	registerOneShot { | sender, message, listener, action |
		this add: action;
		NotificationCenter.registerOneShot(sender, message, listener, this);
	}

	valueArray { | args |
		object do: { | f | f.valueArray(args) };
		this.remove;	
	}
}

*/
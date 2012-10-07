/* 

Enable adding message-specific calls using the changed/update mechanism of Object. 

Usage: 

anObject.addNotifier(aNotifier, \message, action) : 

Make anObject perform action when aNotifier calls: 
aNotifier.changed(\message);

Arguments passed to the action are: The extra arguments given in the 'changed' call + the notifier. 
The notifier and the message are *not* passed as arguments to the action. This is for several reasons: 

- to keep compatibility to NotificationCenter-type calls
- to keep the argument definition part of the action function shorter
- because the notifier that is always passed at the end contains all of: 
  - the sender (notifier, changer)
  - the receiver (listener)
  - the message
  - the action. 
  
So all the info is available there. 

Example: 
(
\listener.addNotifier(\notifier, \test, { | one, two, three, notifier |
	postf("one: %\n", one);
	postf("two: %\n", two);
	postf("three: %\n", three);
	postf("notifier: %\n", notifier);
	notifier.inspect;
});

\notifier.changed(\test, 1, 2, 3);
)

anObject.objectClosed : remove all notifiers and listeners from / to anObject.

Further methods for adding and removing notifiers/listeners are more or lest self-explanatoryl 


*/


Notification { 
	classvar <all; 
	
	var <notifier, <message, <listener, <>action; 

	*initClass { 
		all = MultiLevelIdentityDictionary.new; 
	} 

	*new { | notifier, message, listener, action |
		// First remove previous notification of same address, if it exists: 
		this.remove(notifier, message, listener);
		^this.newCopyArgs(notifier, message, listener, action).init; 
	} 

	init { 
		notifier.addDependant(this); 
		all.put(notifier, listener, message, this); 
	} 

	update { | sender, argMessage ... args |
		if (argMessage === message) {
			action.valueArray(if (args.size == 0) { [this] } { args add: this })
		}
	} 

	*remove { | notifier, message, listener |
		all.at(notifier, listener, message).remove;
	}

	remove { 
		notifier.removeDependant(this); 
		all.removeEmptyAt(notifier, listener, message); 
	} 

	*removeListenersOf { | notifier | 
		all.leafDoFrom(notifier, { | path, notification | 
			notification.notifier.removeDependant(notification); 
		});
		all.put(notifier, nil); 
	} 

	*removeNotifiersOf { | listener | 
		all do: { | listenerDict | 
			listenerDict keysValuesDo: { | argListener, messageDict |
				if (argListener === listener) { messageDict do: _.remove; }
			}
		}
	} 
} 

+ Object {

	addNotifier { | notifier, message, action | 
		Notification(notifier, message, this, action); 
	} 

	removeNotifier { | notifier, message | 
		Notification.remove(notifier, message, this); 
	} 

	addListener { | listener, message, action | 
		Notification(this, message, listener, action); 
	} 

	removeListener { | listener, message | 
		Notification.remove(this, message, listener); 
	} 

	objectClosed { 
		Notification.removeNotifiersOf(this);
		Notification.removeListenersOf(this); 
	} 

	// Utilities 
	addNotifierOneShot { | notifier, message, action | 
		Notification(notifier, message, this, { | ... args | 
			action.(args); 
			args.last.remove; 
		}); 
	} 

	addListenerOneShot { | listener, message, action | 
		listener.addNotifierOneShot(this, message, action); 
	} 

	addNotifierAction { | notifier, message, action | 
		var notification; 
		notification = Notification.all.at(notifier, message, action);
		if (notification.isNil) { 
			this.addNotifier(notifier, message, action);
		}{ 
			notification.action = notification.action addFunc: action; 
		} 
	} 

	addListenerAction { | listener, message, action | 
		listener.addNotifierAction(this, message, action); 
	} 
}


/* 
Shortcuts for establishing messaging communication between objects via NotificationCenter2.
*/

+ Object {

	// ===== basic stuff =====

	addNotifier { | notifier, message, action |
	// add self to do action when receiving message from notifier
	// if either object (notifier or self) closes, remove the notifier->receiver connection
		OnObjectCloseRegistrations add: 
			NotificationCenter2.register(notifier, message, this, action);
	}
	
	removeNotifier { | notifier, message |
	// remove notifier notification registration as well as onObjectClose registrations. 
		 OnObjectCloseRegistrations remove: NotificationCenter2.unregister(notifier, message, this);
	}

	addListener { | listener, message, action |
	// add listener to do action when receiving message from self
	// if either object (listener or self) closes, remove the notication connection
		listener.addNotifier(this, message, action);
	}

	removeListener { | listener, message |
		listener.removeNotifier(this, message);
	}
	
	objectClosed {	// remove all notifiers and listeners // and inputs from widgets
		// is the next one too cpu costly to do every time an object closes? 
//		this.disable;	// free MIDI or osc inputs from all widgets
		this.removeAllNotifications;
		Widget.removeModel(this); 	// TODO: Remove this when Widget class is gone from library
	}
	
	removeAllNotifications {
		NotificationCenter2.removeAll(this);
		OnObjectCloseRegistrations.removeAll(this);
	}

	// ===== less used stuff =====

	addMessage { | notifier, message |
		NotificationCenter2.register(notifier, message, this, { | ... args | 
			this.performList(message, args)
		});
	}

	removeMessage { | notifier, message |
		NotificationCenter2.unregister(notifier, message, this);
	}

	addNotifierWithSelf { | notifier, message, action |
		/* 	pass yourself as argument to your action. For adding notifers to objects
			that are not previously stored in variables */
		this.addNotifier(notifier, message, { | ... args | action.valueArray(this, args) });
	}

	addSelfNotifier { | notifier, message, action |
		// for widgets that need to use themselves in the action to set their value
		this.addNotifier(notifier, message, WidgetAction(this, action));
	}
	
	/* // IZ Mon 20 August 2012 11:55 AM EEST: The above should be replaced with this: 
	   // Getting rid of WidgetAction! 
	addSelfNotifier { | notifier, message, action |
		this.addNotifier(notifier, message, { | ... args | action.(this, *args) }
	}
	*/

	notify { | message, args | NotificationCenter2.notify(this, message, args); }

	registerOneShot { | message, receiver, func |
		/* NOTE: registerOneShot is different from addNotifier, addListener etc because it
		   in its name it does not "target" semantically the listener or notifier object. 
		   Therefore preserve the argument order of NotificationCenter2:register etc. 
		*/
		NotificationCenter2.registerOneShot(this, message, receiver, func);
	}

	// ===== debugging stuff =====

	registrations { | message |
		if (message.isNil) {
			^NotificationCenter2.registrations.at(this);
		}{
			^NotificationCenter2.registrations.at(this, message);
		}
	}
	
	// ===== legacy stuff =====
	removedMessage { ^\objectClosed }
}

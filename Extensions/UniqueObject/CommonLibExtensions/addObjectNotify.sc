// useful shortcut for sending notifications via NotificationCenter
+ Object {
	notify { | message, args | NotificationCenter.notify(this, message, args); }
	
	addMessage { | notifier, message |
		NotificationCenter.register(notifier, message, this, { | ... args | this.performList(message, args) });
	}

	removeMessage { | notifier, message |
		NotificationCenter.unregister(notifier, message, this);
	}
	
	// Experimental =========================================
	addNotifier { | notifier, message, action |
	// add self to do action when receiving message from notifier
	// if either object (notifier or self) closes, remove the notication connection
		NotificationCenter.register(notifier, message, this, action);
		this onClose: { NotificationCenter.unregister(notifier, message, this); };
		notifier onClose: { NotificationCenter.unregister(notifier, message, this); };
	}

	addListener { | listener, message, action |
	// add listener to do action when receiving message from self
	// if either object (listener or self) closes, remove the notication connection
		NotificationCenter.register(this, message, listener, action);
		this onClose: { NotificationCenter.unregister(this, message, listener); };
		listener onClose: { NotificationCenter.unregister(this, message, listener); }
	}
	
	objectClosed {	// remove all notifiers and listeners
		NotificationCenter.notify(this, this.removedMessage);
	}

	removedMessage { ^\objectClosed }

	onClose { | action | this.onRemove(UniqueID.next, action) } 
	onRemove { | key, func | this.doOnceOn(this.removedMessage, key, func); }
	doOnceOn { | message, receiver, func |
		NotificationCenter.registerOneShot(this, message, receiver, { func.(this) });
	}

	addToServerTree { | function, server |
		ServerPrep(server).addToServerTree(this, function);
	}
}
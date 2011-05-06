// useful shortcut for sending notifications via NotificationCenter
+ Object {
	notify { | message, what | NotificationCenter.notify(this, message, what); }
	
	addMessage { | notifier, message |
		NotificationCenter.register(notifier, message, this, { | ... args | this.performList(message, args) });
	}

	removeMessage { | notifier, message |
		NotificationCenter.unregister(notifier, message, this);
	}
}
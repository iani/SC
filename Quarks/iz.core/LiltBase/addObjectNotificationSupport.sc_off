
/* 
Shortcuts for establishing messaging communication between objects via NotificationCenter.
*/

+ Object {

	// ===== basic stuff =====

	notify { | message, args | NotificationCenter.notify(this, message, args); }

	addNotifier { | notifier, message, action |
	// add self to do action when receiving message from notifier
	// if either object (notifier or self) closes, remove the notifier->receiver connection
		^NotificationCenter.register(notifier, message, this, action);
	}
	
	removeNotifier { | notifier, message |
	// remove notifier notification registration as well as onObjectClose registrations. 
		 ^NotificationCenter.unregister(notifier, message, this);
	}

	addListener { | listener, message, action |
	// add listener to do action when receiving message from self
	// if either object (listener or self) closes, remove the notication connection
		^listener.addNotifier(this, message, action);
	}

	removeListener { | listener, message |
		^listener.removeNotifier(this, message);
	}
	
	objectClosed {	/* remove all notifiers and listeners
		that were added by this.addNotifier or this.addListener, addMessage, addNotifierOneShot,
		NotificationCenter.register(...).  */
		NotificationCenter.removeNotifiersOf(this);
		NotificationCenter.removeListenersOf(this);
	}

	addNotifierOneShot { | notifier, message, action |
		NotificationCenter.registerOneShot(notifier, message, this, action);
	}

	// ===== less used stuff =====

	addMessage { | notifier, message |
		NotificationCenter.register(notifier, message, this, { | ... args | 
			this.performList(message, args)
		});
	}

	removeMessage { | notifier, message |
		NotificationCenter.unregister(notifier, message, this);
	}

	addNotifierWithSelf { | notifier, message, action |
		/* 	pass yourself as argument to your action. For adding notifers to objects
			that are not previously stored in variables */
		this.addNotifier(notifier, message, { | ... args | action.valueArray(this, args) });
	}

	registerOneShot { | message, receiver, func |
		/* NOTE: registerOneShot is different from addNotifier, addListener etc because it
		   in its name it does not "target" semantically the listener or notifier object. 
		   Therefore preserve the argument order of NotificationCenter:register etc. 
		*/
		NotificationCenter.registerOneShot(this, message, receiver, func);
	}
	
	// ===== legacy stuff =====
	removedMessage { ^\objectClosed }
}

+ NotificationCenter {
	
	*removeNotifiersOf { | object | // remove all notifications that this object receives
		registrations.dictionary keysValuesDo: { | sender, r |
			r.values do: { | d |
				d keysDo: { | key | 
					if (key === object) {
						d[key] = nil;
						if (d.size == 0) { 
							r[r.findKeyForValue(d)] = nil;
							if (r.size == 0) {
								NotificationCenter.registrations.put(sender, nil);
							}
						}
					} 
				}
			}
		}	
	}

	// remove all notifications that this object sends
	*removeListenersOf { | object | registrations.removeAt(object) }
}

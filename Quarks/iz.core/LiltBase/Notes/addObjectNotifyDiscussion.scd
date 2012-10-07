//mc: suggesting NR message variants as replacement candidates !
// HERE FOR DISCUSSION. IZ 
/* 
Shortcuts for establishing messaging communication between objects via NotificationCenter.
*/

+ Object {
	notify { | message, args | NotificationCenter.notify(this, message, args); }
	
	addMessage { | notifier, message |
		NotificationCenter.register(notifier, message, this, { | ... args | 
			this.performList(message, args)
		});
	}
//mc: (1) why should a 'message' registration not been removed on objectClose ???
//	(2) what are the args doing here ???

/* iz: 
	(1) addMessage was only meant as a shortcut. It was not meant to include the functionality
	    of addNotifier. I have provided removeMessage so that it can be called by onObjectClosed
	    if wanted. 
	    I believe I am not using it, and therefore it would be ok to 
	    change its definition. Otherwise, to be sure, one might use a different 
	    method name for the onClose added method, like addPerformList
	(2) the args allow a function when it is the receiver of addMessage to obtain arguments
   sent by the sender via NotificationCenter. See Meta_NotificationCenter:notify
   
   Regarding the NR method versions: 
   
   Short answer: It is possible to do all the work needed without any reference to the NR
   both in these methods here and in end-user code. The Window:onClose problem has been 
   solved differently in: 
   - WindowResource (which I do not use any more and at some future moment may remove). 
   - WindowHandler. See example NanoKontrol2b.
   
   More general answer: It is possible to do all the work needed without 
   ever handling NotificationRegistration instances. This simplifies the end-user code. 
   NotificationRegistrations are not intended to be dealt with in the end-user code, 
   but are used internally by NotificationCenter. 
   NotificationCenter is sufficient in its design to do all necessary tasks, without
   having to deal with NotificationRegistration instances. 
  
   --- HOWEVER : --- 
   (Mon 20 August 2012  8:16 AM EEST): 
   using nr.remove is a faster way to remove a notification than unregister, because 
   NotificationCenter:unregister has to search for the notification first.
   Perhaps addNotifier should use the NR method, with onObjectClosed instead of onClose, 
   and without returning nr, so that it can be followed by another message to the same 
   receiver: addNotifier(...).doSomething(...) etc. 
   
*/
	addMessageNR { | notifier, message |
		var nr = NotificationCenter.register(notifier, message, this, { | ... args | 
			this.performList(message, args)
		});
		this onClose: { nr.remove }; notifier onClose: { nr.remove };
		^nr
	}

	removeMessage { | notifier, message |
		NotificationCenter.unregister(notifier, message, this);
	}

	addSelfNotifier { | notifier, message, action |
		// for widgets that need to use themselves in the action to set their value
		this.addNotifier(notifier, message, WidgetAction(this, action));
	}
	
	addNotifier { | notifier, message, action |
	// add self to do action when receiving message from notifier
	// if either object (notifier or self) closes, remove the notifier->receiver connection
		NotificationCenter.register(notifier, message, this, action);
		this onObjectClosed: { NotificationCenter.unregister(notifier, message, this); };
		notifier onObjectClosed: { NotificationCenter.unregister(notifier, message, this); };
	}
	addNotifierNR { | notifier, message, action |
	// add self to do action when receiving message from notifier
	// if either object (notifier or self) closes, remove the notication connection
		var nr = NotificationCenter.register(notifier, message, this, action);
		this onClose: { nr.remove }; notifier onClose: { nr.remove };
		^nr
	}
	
	removeNotifier { | notifier, message |
		// leaves the onClose connection dangling, 
		// which will be removed when the object calls objectClosed
		 NotificationCenter.unregister(notifier, message, this);
	}

	addListener { | listener, message, action |
	// add listener to do action when receiving message from self
	// if either object (listener or self) closes, remove the notication connection
		NotificationCenter.register(this, message, listener, action);
		this onClose: { NotificationCenter.unregister(this, message, listener); };
		listener onClose: { NotificationCenter.unregister(this, message, listener); }
	}
	addListenerNR { | listener, message, action |
	// add listener to do action when receiving message from self
	// if either object (listener or self) closes, remove the notication connection
		var nr = NotificationCenter.register(this, message, listener, action);
		this onClose: { nr.remove }; listener onClose: { nr.remove };
		^nr
	}

	removeListener { | listener, message |
		 NotificationCenter.unregister(this, message, listener);
	}
	
	objectClosed {	// remove all notifiers and listeners // and inputs from widgets
		NotificationCenter.notify(this, this.removedMessage);
		// is the next one too cpu costly to do every time an object closes? 
//		this.disable;	// free MIDI or osc inputs from all widgets
		this.removeAllNotifications;
		Widget.removeModel(this);
	}
	
	removeAllNotifications {
		NotificationCenter.removeAll(this);
	}

	removedMessage { ^\objectClosed }

	/* iz Mon 20 August 2012 10:30 AM EEST: 
	Why do we need UniqueID.next here? ALTENRATIVES WITHOUT UniqueID NEEDED.
	Example of why adding multiple oneShot notifications to same object is a problem:

NotificationCenter.registerOneShot(\sender, \release, \receiver, { "release 1".postln; });
NotificationCenter.registerOneShot(\sender, \release, \receiver, { "release 2".postln; });

\sender.notify(\release);

The second action overwrites the previous one instead of being added. 
Should we add actions using the FunctionList mechanism, via NR maybe?

	*/
	onObjectClosed { | action | this.onRemove(UniqueID.next, action) } 
	onRemove { | key, func | this.doOnceOn(this.removedMessage, key, func); }
	doOnceOn { | message, receiver, func |
	/* iz: registerOneShotNR is really not needed in this case, because doOnceOn 
		does not return the NR!
		So stick with this.registerOneShot.
		The NR versions can go to a different file after all, and be independent.
	*/
		this.registerOneShotNR(message, receiver, { func.(this) });
	}
	
	/* iz: I am aware that the arguments may better be: 
		registerOneShot { | receiver, message, func | ... }
		to be consistent with the addNotifier and addListener methods above. 
		
		Answer: Use addNotifierOneShot and addListenerOneShot as end-user code methods
		instead of registerOneShot.
		
		But: addNotifierOneShot and addListenerOneShot should not use onClose. 
		In this case it is superfluous, and onClose should not be touched anyway. 
	*/
	registerOneShot { | message, receiver, func | //mc: please use another name for private arg order!
		NotificationCenter.registerOneShot(this, message, receiver, func);
	}
	registerOneShotNR { | notifier, message, func | //mc: inconsitend args above!!!
		^NotificationCenter.registerOneShot(notifier, message, this, func);
	}
	
	//mc: or even better and really missing (since the oneShot may have been not triggert once ;-):	
	addNotifierOneShot { | notifier, message, action |
	// add self to do action when receiving message from notifier
	// if either object (notifier or self) closes, remove the notication connection
		var nr = NotificationCenter.registerOneShot(notifier, message, this, action);
		this onClose: { nr.remove }; notifier onClose: { nr.remove };
		^nr
	}
	
	addListenerOneShot { | listener, message, action |
	// add listener to do action when receiving message from self
	// if either object (listener or self) closes, remove the notication connection
		var nr = NotificationCenter.registerOneShot(this, message, listener, action);
		this onClose: { nr.remove }; listener onClose: { nr.remove };
		^nr
	}
	

	addToServerTree { | function, server |
		ServerPrep(server).addToServerTree(this, function);
	}
	removeFromServerTree { | function, server |
		ServerPrep(server).removeFromServerTree(this);
	}
}

+ NotificationCenter {
	*removeAll { | object |
		registrations.removeEmptyAtPath([object]);
	}	
}
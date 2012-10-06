+ Object { 

	addNotifier { | notifier, message, action | 
		var controller; 
		controller = SimpleController().put(message, action); 
		SimpleController.addNotifier(notifier, message, this, controller);
		notifier.addDependant(controller);
	} 

	removeNotifier { | notifier, message |
		SimpleController.removeNotifier(notifier, message, this);
	}

	objectClosed {
		SimpleController.removeNotifiersOf(this); // find notifiers of myself + remove them
		SimpleController.removeListenersOf(this);
	}

} 


SimpleController {
	classvar notifiers;

	*removeNotifier { | notifier, message, listener |
		var controller;
		controller = notifiers.removeEmptyAt(notifier, message, listener);
		controller !? { notifier.removeDependant(controller) };
	}
	
}


+ Object {

	addNotifier2 { | notifier, message, action |
		var controller;
		controller = SimpleController().put(message, action);
		notifier.addDependant(controller);
		notifier.addNotifier2(this, \closed, { notifier.removeDependant(controller) });
		// FOLLOWING CREATES INFINITE LOOP: 
		/*
		controller.addNotifier2(this, \remove, { | listener, message, notifier |
			[listener, message, notifier].postln;
			if (controller === notifier) {
				["will remove controller", controller].postln;
				notifier.removeDependant(controller);
				this.removeDependant(controller);
			};
		});
		*/
// 		this.addDependant(con.... )
	}

	objectClosed2 { this.changed(\closed) }
}

/*

\receiver.addNotifier2(\sender, \message, { | ... args | [\receiver, "received", args].postln; });


\sender.changed(\message, 'changed additional arg');

\receiver.changed(\remove, \notifier);

*/
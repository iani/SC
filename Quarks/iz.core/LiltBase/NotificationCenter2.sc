/* IZ Mon 20 August 2012 11:44 PM EEST
NotificationCenter modified to work with Object:addNotifier and related methods.

Sat 01 September 2012  6:22 PM EEST: 
OBSOLETE: Removing notifiers functionality on object closed has been incorporated into NotificationCenter. Extensions for doing this are currently in LiltBase Quark. 

*/

NotificationCenter2 {

	classvar <>registrations;
	classvar <>registrationLinks;

	*initClass { this.clear; } 
	*clear {
		registrations = MultiLevelIdentityDictionary.new;
		registrationLinks = IdentityDictionary.new; // 1 nr per notifier/message/listener
	}

	//			who		\didSomething
	*notify { arg object, message, args;
		registrations.at(object,message).copy.do({ arg function;
			function.valueArray(args)
		})
	}

	// 			who		\didSomething
	*unregister { arg object, message, listener;
		var lastKey, lastDict, nr;
		lastDict = registrations.at(object, message);
		if (lastDict.notNil) {
			nr = lastDict.at(listener);
			lastDict.removeAt(listener);
			if (lastDict.size == 0) {
				registrations.removeAt(object, message);
				if (registrations.at(object).size == 0) { registrations.removeAt(object); };
			};
		};
		this.remove(nr);
		^nr
	}
	
	*register { | object, message, listener, action |
		var nr;
		nr = NotificationRegistration2(object, message, listener, action);
		registrations.put(object, message, listener, nr);
		this.add(nr);
		^nr;
	}

	*registerOneShot {  | object, message, listener, action |
		var nr;
		nr = NotificationRegistration2(object, message, listener, { |args|
			action.value(args);
			this.unregister(object, message, listener);
		});
		registrations.put(object, message, listener, nr);
		this.add(nr);
		^nr
	}

	*registrationExists { | object, message, listener |
		^registrations.at(object, message, listener).notNil
	}

	*removeForObject { |object| registrations.removeAt(object) }
	
	*add { | registration |
		var registrations, where;
		where = registration.object;
		registrations = registrationLinks[where];
		registrations ?? { 
			registrations = RegistrationList();
			registrationLinks[where] = registrations;
		};
		registrations = registrations add: registration;
		where = registration.listener;		
		registrations = registrationLinks[where];
		registrations ?? { 
			registrations = RegistrationList();
			registrationLinks[where] = registrations;
		};
		registrations = registrations add: registration;
		registrationLinks[where] = registrations;
	}
	
	*remove { | registration |
		var registrations, where;
		registration ?? { ^this };
		where = registration.object;
		registrations = registrationLinks[where];
		registration.remove;
		registrations remove: registration;
		if (registrations.size == 0) { registrationLinks[where] = nil };
		where = registration.listener;
		registrations = registrationLinks[where];
		registration.remove;
		registrations remove: registration;
		if (registrations.size == 0) { registrationLinks[where] = nil };
	}
	
	*removeLinksForObject { | object | registrationLinks[object].copy do: this.remove(_) }

}


NotificationRegistration2 {
	var <>object,<>message,<>listener, <>action;
	
	== { | nr | 
		^object === nr.object and: { message === nr.message } and: { listener === nr.listener }
	}
	
	*new { | o, m, l, a | ^this.newCopyArgs(o, m, l, a) }
	remove { NotificationCenter2.unregister(object, message, listener) }
	valueArray { | args | ^action.valueArray(args) }
}

RegistrationList : List {

	add { | registration |
		var old;
		old = array detect: (_ == registration);
		array remove: old;
		array = array add: registration;
	}
}

/*

NotificationCenter.registrations.do({ | r |
	r.values.do({ | d |
		var sender;
		d.keysDo({ | key | 
			if (key === \receiver) {
				sender = NotificationCenter.registrations.dictionary.findKeyForValue(r);
				d[key] = nil;
				if (d.size == 0) { 
					r[r.findKeyForValue(d)] = nil;
					if (r.size == 0) {
						NotificationCenter.registrations.put(sender, nil);
					}
				}
			} 
		})
	}) 
});


*/
/*
OnObjectCloseRegistrations {
	classvar <all;
	
	*initClass { all = IdentityDictionary.new; } // 1 nr per notifier/message/listener
	
	*add { | registration |
		var registrations, where;
		where = registration.object;
		registrations = all[where];
		registrations ?? { 
			registrations = RegistrationList();
			all[where] = registrations;
		};
		registrations = registrations add: registration;
		where = registration.listener;		
		registrations = all[where];
		registrations ?? { 
			registrations = RegistrationList();
			all[where] = registrations;
		};
		registrations = registrations add: registration;
		all[where] = registrations;
	}
	
	*remove { | registration |
		var registrations, where;
		registration ?? { ^this };
		where = registration.object;
		registrations = all[where];
		registration.remove;
		registrations remove: registration;
		if (registrations.size == 0) { all[where] = nil };
		where = registration.listener;
		registrations = all[where];
		registration.remove;
		registrations remove: registration;
		if (registrations.size == 0) { all[where] = nil };
	}
	
	*removeAll { | object | all[object].copy do: this.remove(_) }
}
*/

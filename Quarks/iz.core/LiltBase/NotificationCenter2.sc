/* IZ Mon 20 August 2012 11:44 PM EEST
NotificationCenter modified to work with Object:addNotifier and related methods. 
*/

NotificationCenter2 {

	classvar <registrations;

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
		^nr
	}
	
	*register { | object, message, listener, action |
		var nr;
		nr = NotificationRegistration2(object, message, listener, action);
		registrations.put(object, message, listener, nr);
		^nr;
	}

	*registerOneShot {  arg object,message,listener,action;
		var nr;
		nr = NotificationRegistration2(object,message,listener);
		registrations.put(object,message,listener,
			{ |args|
				action.value(args);
				this.unregister(object,message,listener)
			});
		^nr
	}
	*clear {
		registrations = MultiLevelIdentityDictionary.new;
	}
	*registrationExists { |object,message,listener|
		^registrations.at(object,message,listener).notNil
	}
	*initClass {
		this.clear
	}
	*removeForListener { |listener|
		registrations.removeAt(listener)
	}
	
	*removeAll { | object |
		registrations.removeEmptyAtPath([object]);
	}	

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


OnObjectCloseRegistrations {
	classvar <all;
	
	*initClass { all = IdentityDictionary.new; } // 1 nr per notifier/message/listener
	
	*add { | registration |
		var registrations, where;
		where = registration.object;
		registrations = all[where] ?? { 
			registrations = RegistrationList();
			all[where] = registrations;
		};
		registrations = registrations add: registration;
		where = registration.listener;		
		registrations = all[where] ?? { 
			registrations = RegistrationList();
			all[where] = registrations;
		};
		registrations = registrations add: registration;
		all[where] = registrations;
	}
	
	*remove { | registration |
		var registrations, where;
		where = registration.object;
		registrations = all[where];
		registrations remove: registration;
		if (registrations.size == 0) { all[where] = nil };
		where = registration.listener;
		registrations = all[where];
		registrations remove: registration;
		if (registrations.size == 0) { all[where] = nil };
	}
	
	*removeAllFor { | object | all[object].copy do: this.remove(_) }
}

RegistrationList : List {

	add { | registration |
		var old;
		old = array detect: (_ == registration);
		array remove: old;
		array = array add: registration;
	}
}

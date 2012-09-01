/* IZ Sat 01 September 2012  3:32 PM EEST


MOVED TO LiltBase Quark

+ Felix / crucial: removeObject method: Remove all notifications sent by object.

Remove all object-message registrations that send notifications to a given listening object.
Renamed from removeForObject to removeListenersOf. 

This is useful when that object should no longer receive notifications (for example a closed view). 

*/

/*
+ NotificationCenter {
	
	*removeNotifiersOf { | object | // remove all notifications that send to object
		registrations.dictionary.keysValuesDo({ | sender, r |
			r.values.do({ | d |
				d.keysDo({ | key | 
					if (key === object) {
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
	}

	*removeListenersOf { | object | registrations.removeAt(object) }
}

+ Object {
	objectClosed {	/* remove all notifiers and listeners
		that were added by this.addNotifier or this.addListener, addMessage, addNotifierOneShot,
		NotificationCenter.register(...).  */
		NotificationCenter.removeNotifiersOf(this);
		NotificationCenter.removeListenersOf(this);
	}
}

*/
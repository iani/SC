//:
NotificationCenter.registrations.dictionary.keysValuesDo({ | key | key.postln; }).size;

//: add two different notifying objects to one receiver
NotificationCenter.register(\sender, \message, \receiver, { "received message from sender".postln; });
NotificationCenter.register(\sender2, \message, \receiver, { "received message from sender2".postln; });
NotificationCenter.register(\sender, \message2, \receiver, { "received message2 from sender".postln; });
NotificationCenter.register(\receiver, \message, \sender, { "received message from receiver".postln; });


//: test that the notification connections work
NotificationCenter.notify(\sender, \message);
NotificationCenter.notify(\sender2, \message);
NotificationCenter.notify(\sender, \message2);
NotificationCenter.notify(\receiver, \message);


//:Testing new objectClosed implementation: 
\receiver.objectClosed;

//:

NotificationCenter.removeListenersOf(\sender);


// when the receiver "closes" it needs to remove objects that notify it
//: Trying to access the registrations of objects that notify a receiver:
NotificationCenter.registrations.at(\receiver); 	// nil

//: we can get the notifications from the sender side. OK. But from the receiver side?
NotificationCenter.registrations.at(\sender);		// IdentityDictionary[ (message -> ...) ]

//: The above returns nil, so how do I access all objects that are registered to notify \receiver?
// Here is a way to remove all registrations for a listener, without registering them separately,
// by searching for them in NotificationCenter.registrations: 

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


//:testing new cache mechanism

NotificationCenter.registrations.at(\sender, \message);

NotificationCenter.registrations.put(\sender, \message, nil);

NotificationCenter.registrations.at(\sender);


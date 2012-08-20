/*
Testing new Object:addNotifier scheme
*/

NotificationCenter2.register(\notifier, \message, \listener, { "got notification".postln; });

\notifier.notify(\message);

NotificationCenter2.unregister(\notifier, \message, \listener, { "got notification".postln; });



\listener.addNotifier(\notifier, \message, { "got notification  asdf asdf 2".postln; });

\notifier.notify(\message);

OnObjectCloseRegistrations.all;

\listener.removeNotifier(\notifier, \message)

//

a = NotificationCenter2.register(\notifier, \message, \listener, { "got notification".postln; });
b = NotificationCenter2.register(\notifier, \message, \listener, { "got notification".postln; });

l = RegistrationList();

l add: a;
l add: b;
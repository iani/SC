/*
Testing new Object:addNotifier scheme
*/

NotificationCenter2.register(\notifier, \message, \listener, { "got notification".postln; });


\notifier.notify(\message);

NotificationCenter2.unregister(\notifier, \message, \listener, { "got notification".postln; });



\listener.addNotifier(\notifier, \message, { "got notification".postln; });

\notifier.notify(\message);

NotificationCenter2.registrations.at(\notifier, \message, \listener).inspect;
OnObjectCloseRegistrations.all;

\listener.removeNotifier(\notifier, \message)
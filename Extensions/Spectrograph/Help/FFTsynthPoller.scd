FFTsynthPoller(\test).start.rate = 0.210;

NotificationCenter.register(\test, \fft, \testing, { | data | data.postln });
NotificationCenter.unregister(\test, \fft, \testing);

FFTsynthPoller(\test).stop;



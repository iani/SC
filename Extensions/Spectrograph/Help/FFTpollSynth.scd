A UniqueSynth which starts an FFT analysis synth on any audio input, polls the fft buffer at a steady rate, and sends the resulting buffer data to an FFTsignalPoller. 

It is started implicitly by FFTsynthPoller.

It is not designed to be used on its own. 

See also FFTsynthPoller.

FFTsynthPoller(\test).start.rate = 0.210;

NotificationCenter.register(\test, \fft, \testing, { | data | data.postln });
NotificationCenter.unregister(\test, \fft, \testing);

FFTsynthPoller(\test).stop;



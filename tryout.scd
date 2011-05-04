Spectrograph.start.stopOnClose = false;

FFTsynthPoller.onServer.first.addListener(\test, { | data | data.postln; });

FFTsynthPoller.onServer.first.stop;

Spectrogram2.new;

\default.play;

FFTsynthPoller(\test).start.rate = 0.210;

FFTsynthPoller.at(\test).addListener({ | ... data | data.postln });

FFTsynthPoller(\test).stop;



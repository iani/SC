//:spetrogram
Spectrogram2;


//:spectrograph
Spectrograph.start;

//:sounds

\saw.playFunc({ LFSaw.ar(LFNoise1.kr(0.2).range(100, 1000), 0, 0.1) });
//  \saw.synth.free;

\pulse.playFunc({ LFPulse.ar(LFNoise1.kr(0.2).range(100, 1000), 0, 0.5, 0.1) });

// \saw.synth.release;
b = { LFNoise1.ar(LFNoise2.ar(0.2).range(400, 4000), 0.1) }.play;
/* iz Sun 09 December 2012  2:12 PM EET

Some shortcuts for adding frequently needed items in a SynthDef's UGen graph.

EXPERIMENTAL.



*/

+ Object {

	// OUTPUT
	out { | name = \out, default = 0 |
		^Out.ar(name.kr(default), this);
	}

	// (TODO:)
	pan2 {}

	pan4 {}

	panAz {}

	// ENVELOPES
	adsr { | gateName = \gate, gateDefault = 1, levelScaleName = \levelScale, levelScaleDefault = 1,
		levelBiasName = \levelBias, levelBiasDefault = 0, timeScaleName = \timeScale, timeScaleDefault = 1, doneAction = 2,
		attackTime = 0.01, decayTime = 0.3, sustainLevel = 0.5, releaseTime = 0.1, peakLevel = 1, curve = -4, bias = 0 |
		^this * EnvGen.kr(
			Env.adsr(attackTime, decayTime, sustainLevel, releaseTime, peakLevel, curve, bias),
			gateName.kr(gateDefault),
			levelScaleName.kr(levelScaleDefault),
			levelBiasName.kr(levelBiasDefault),
			timeScaleName.kr(timeScaleDefault),
			doneAction
		);
	}

	// TODO:
	asr {}

	perc {}

	sine {}

	tri {}

	env {}

	// FILTERS + DELAYS + REVERBS

	hpf {}

	lpf {}

	resonz {}

	ringz {}

	bpf {}

	combN {}

	combL {}

	combC {}

	allpassN {}

	allpassL {}

	allpassC {}

	freeVerb {}

	freeVerb2 {}

	gVerb {}

	// BUFFERS

	playBuf {}

	grainBuf {}

	// FFT / PV ...

}

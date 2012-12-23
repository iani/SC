/* iz Sun 09 December 2012  2:12 PM EET
Some shortcuts for adding frequently needed items in a SynthDef's UGen graph.
EXPERIMENTAL
*/

// Getting the default sound samples path. Useful in many examples from Help.
+ String {
	defaultSoundPath { ^Platform.resourceDir +/+ "sounds" +/+ this }
}

+ Object {

	// OUTPUT
	out { | out = 0, outName = \out |
		^Out.ar(outName.kr(out), this);
	}

	pan2 { | pos = 0, level = 1, posName = \pos, levelName = \level |
		^Pan2.ar(this, posName.kr(pos), levelName.kr(level));
	}

	// (TODO:)
	pan4 {}

	// (TODO:)
	panAz {}

	// ENVELOPES
	// Envelopes 1: With gate for release
	adsr { | attackTime = 0.01, decayTime = 0.3, sustainLevel = 0.5, releaseTime = 0.1,
		peakLevel = 1, curve = -4, bias = 0,
		gate = 1, levelScale = 1, levelBias = 0, timeScale = 1, doneAction = 2,
		gateName = \gate, levelScaleName = \levelScale, levelBiasName = \levelBias,
		timeScaleName = \timeScale |
		^this * EnvGen.kr(
			Env.adsr(attackTime, decayTime, sustainLevel, releaseTime, peakLevel, curve, bias),
			gateName.kr(gate),
			levelScaleName.kr(levelScale),
			levelBiasName.kr(levelBias),
			timeScaleName.kr(timeScale),
			doneAction
		);
	}

	// TODO:
	asr {}

	// Envelopes 2: Ends when envelope is over
	// Gate is still given because one may use it to retrigger the Envelope if doneAction == 0
	perc { | attackTime = 0.01, decayTime = 0.3, releaseTime = 0.1, level = 1, curve = -4,
		gate = 1, levelScale = 1, levelBias = 0, timeScale = 1, doneAction = 2,
		gateName = \gate, levelScaleName = \levelScale, levelBiasName = \levelBias,
		timeScaleName = \timeScale |
		^this * EnvGen.kr(
			Env.perc(attackTime, decayTime, releaseTime, level, curve),
			gateName.kr(gate),
			levelScaleName.kr(levelScale),
			levelBiasName.kr(levelBias),
			timeScaleName.kr(timeScale),
			doneAction
		);
	}

	sine { | dur = 0.1, level = 1,
		gate = 1, levelScale = 1, levelBias = 0, timeScale = 1, doneAction = 2,
		gateName = \gate, levelScaleName = \levelScale, levelBiasName = \levelBias,
		timeScaleName = \timeScale |
		^this * EnvGen.kr(
			Env.sine(dur, level),
			gateName.kr(gate),
			levelScaleName.kr(levelScale),
			levelBiasName.kr(levelBias),
			timeScaleName.kr(timeScale),
			doneAction
		);
	}

	triangle { | dur = 0.1, level = 1,
		gate = 1, levelScale = 1, levelBias = 0, timeScale = 1, doneAction = 2,
		gateName = \gate, levelScaleName = \levelScale, levelBiasName = \levelBias,
		timeScaleName = \timeScale |
		^this * EnvGen.kr(
			Env.triangle(dur, level),
			gateName.kr(gate),
			levelScaleName.kr(levelScale),
			levelBiasName.kr(levelBias),
			timeScaleName.kr(timeScale),
			doneAction
		);
	}

	xyc { | xyc,
		gate = 1, levelScale = 1, levelBias = 0, timeScale = 1, doneAction = 2,
		gateName = \gate, levelScaleName = \levelScale, levelBiasName = \levelBias,
		timeScaleName = \timeScale |
		^this * EnvGen.kr(
			Env.xyc(xyc),
			gateName.kr(gate),
			levelScaleName.kr(levelScale),
			levelBiasName.kr(levelBias),
			timeScaleName.kr(timeScale),
			doneAction
		);
	}

	// Envelopes 3: Gate is created if gate argument is provided.
	env { | levels, times, curve, releaseNode, loopNode,
		gate = 1, levelScale = 1, levelBias = 0, timeScale = 1, doneAction = 2,
		gateName = \gate, levelScaleName = \levelScale, levelBiasName = \levelBias,
		timeScaleName = \timeScale |
		^this * EnvGen.kr(
			Env(levels ? #[0, 1, 0], times ? [0.1, 0.9], curve ? \lin, releaseNode, loopNode),
			gateName.kr(gate),
			levelScaleName.kr(levelScale),
			levelBiasName.kr(levelBias),
			timeScaleName.kr(timeScale),
			doneAction
		);
	}

	// FILTERS + DELAYS + REVERBS

	hpf { | freq = 400, mul = 1, add = 0,
		freqName = \freq, mulName = \mul, addName = \add |
		^HPF.ar(this, freqName.kr(freq), mulName.kr(mul), addName.kr(add));
	}

	lpf { | freq = 400, mul = 1, add = 0,
		freqName = \freq, mulName = \mul, addName = \add |
		^HPF.ar(this, freqName.kr(freq), mulName.kr(mul), addName.kr(add));
	}

	bpf { | freq = 400, rq = 1, mul = 1, add = 0,
		freqName = \freq, rqName = \rq, mulName = \mul, addName = \add |
		^BPF.ar(this, freqName.kr(freq), rqName.kr(rq), mulName.kr(mul), addName.kr(add));
	}

	brf { | freq = 400, rq = 1, mul = 1, add = 0,
		freqName = \freq, rqName = \rq, mulName = \mul, addName = \add |
		^BRF.ar(this, freqName.kr(freq), rqName.kr(rq), mulName.kr(mul), addName.kr(add));
	}

	resonz { | freq = 400, bwr = 1, mul = 1, add = 0,
		freqName = \freq, bwrName = \bwr, mulName = \mul, addName = \add |
		^Resonz.ar(this, freqName.kr(freq), bwrName.kr(bwr), mulName.kr(mul), addName.kr(add));
	}

	ringz { | freq = 400, decaytime = 1, mul = 1, add = 0,
		freqName = \freq, decaytimeName = \decaytime, mulName = \mul, addName = \add |
		^Ringz.ar(this, freqName.kr(freq), decaytimeName.kr(decaytime), mulName.kr(mul), addName.kr(add));
	}

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

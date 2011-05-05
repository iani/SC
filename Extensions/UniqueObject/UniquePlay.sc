
UniquePlay : UniqueSynth {
	
	*new { | playFunc, target, outbus = 0, fadeTime = 0.02, addAction=\addToHead, args, key |
		^super.new(key ?? { playFunc.asKey }, playFunc, args, target, addAction, outbus, fadeTime);
	}

	prMakeObject { | target, playFunc, args, addAction = \addToHead, outbus = 0, fadeTime = 0.02 |
		object = playFunc.play(target, outbus, fadeTime, addAction, args);
	}
}


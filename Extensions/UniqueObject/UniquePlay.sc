
UniquePlay : UniqueSynth {
//	*mainKey { ^\playFuncs }
	
	*new { | playFunc, target, outbus = 0, fadeTime = 0.02, addAction=\addToHead, args, dur |
		^super.new(playFunc.hashKey, playFunc, args, target, addAction, outbus, fadeTime, dur);
	}

	makeObject { | target, playFunc, args, addAction = \addToHead, outbus = 0, fadeTime = 0.02, dur |
		object = playFunc.play(target, outbus, fadeTime, addAction, args);
		if (dur.notNil) { { this.release; }.defer(dur) };
		this.registerObject;
	}
}

// Synonym - abbreviation for UniqueSynth :  
Uplay : UniquePlay {}


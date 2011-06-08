/* Inspired by JITlib's NodeProxy. 
A simpler, more rudimentary implementation here. Just the xfade of successive synths, and storing the in, out, and fadeTime parameters so that they remain persistent. 



*/


FadeSynth : SynthResource {
	var <>in = 0, <>out = 0, <>fadeTime = 0.02, <>addAction = \addToHead;
	
	*new { | name = \out, in = 0, out = 0, fadeTime = 0.02, target, addAction = \addToHead |
		^super.new(name, target, in, out, fadeTime, addAction);
	}

	init { | argTarget, defName, argIn = 0, argOut = 0, argFadeTime = 0.02, argAction = \addToHead |
		super.initTarget(argTarget);
		in = argIn;
		out = argOut;
		fadeTime = argFadeTime;
		addAction = argAction;
	}

	-< { | func |
		var def;
		def = func.asSynthDef(fadeTime: fadeTime, name: key.last);
		def.addToServer(server);
		if (object.notNil) { object.release(fadeTime) };
		this.makeSynth(def.name, [\in, in, \out, out], addAction);
	}

	synthEnded {	// do not remove when synth ends
		object.releaseDependants; // clean up synth's dependants
	}

	releaseSynth { | dtime |
		super.releaseSynth(dtime ? fadeTime);	
	}
}
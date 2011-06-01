/* Inspired by JITlib's NodeProxy. 
A simpler, more rudimentary implementation here. Just the xfade of successive synths, and storing the in, out, and fadeTime parameters so that they remain persistent. 



*/


FadeSynth : SynthResource {
	var <>in = 0, <>out = 0, <>fadeTime = 0.02;
	
	*new { | name = \out, in = 0, out = 0, fadeTime = 0.02, target |
		^super.new(name, target, in, out, fadeTime);
	}


	init { | argTarget, argIn = 0, argOut = 0, argFadeTime = 0.02 |
		super.initTarget(argTarget);
		in = argIn;
		out = argOut;
		fadeTime = argFadeTime;
	}

	< { | func |	// very early prototype!!!
		var def;
		def = func.asSynthDef(fadeTime: fadeTime, name: key.last);
		def.addToServer(server);
		object = Synth(def.name, [\in, in, \out], target);
	}


	synthEnded {	// do not remove when synth ends
		object.releaseDependants; // clean up synth's dependants
	}

}
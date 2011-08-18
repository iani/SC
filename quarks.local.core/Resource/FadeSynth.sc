/* Inspired by JITlib's NodeProxy. 
A simpler, more rudimentary implementation here. 

Arguments and def are persistent.

*/

FadeSynth : SynthResource {
	var <>fadeTime = 0.02, <>addAction = \addToHead;
	var <>desc, <>args, <hasGate = false;
//	var <inputs, <outputs;	 // TODO!
	
	*new { | name = \out, args, target, addAction = \addToHead, fadeTime = 0.02 |
		^super.new(name, name, target, args, fadeTime, addAction);
	}

	init { | argTarget, defName, argArgs, argFadeTime = 0.02, argAction = \addToHead |
		super.initTarget(argTarget);
		args = FadeSynthArgs(argArgs);
		fadeTime = argFadeTime;
		addAction = argAction;
		this.initDesc(defName);
	}

	initDesc { | funcOrSymbol |
		desc = funcOrSymbol.asSynthDesc(nil, nil, nil, fadeTime);
		hasGate = args addArgsFromDesc: desc;	
 	}

 	<> { | funcOrSymbol |
	 	this.releaseSynth(fadeTime);
	 	this initDesc: funcOrSymbol;
		this.makeSynth(desc.name, args.fadeSynthArgs, addAction);
	}

	releaseSynth { | dtime |
		if (object.isNil) { ^this };
		if (hasGate) {
			super.releaseSynth(dtime ? fadeTime);		
		}{	// a fade out mechanism should be devised here;
			object.free;
		}; 
	}

	synthEnded { | synth |
		synth.releaseDependants;
		if (object === synth) {
			object = nil;
//			this.remove; // so we can restart this, with all its settings 
		}
	}
	
	start {
		if (object.isNil) { this.makeSynth(desc.name, args.fadeSynthArgs, addAction); };
	}

	set { | ... newArgs | 
		super.set(*newArgs);
		newArgs pairsDo: { | key, value | args[key] = value; };
	}

}
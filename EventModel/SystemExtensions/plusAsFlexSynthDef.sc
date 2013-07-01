
+ Function {
	asFlexSynthDef { | rates, prependArgs, outClass=\Out, fadeTime = 0.1, name |
		^GraphBuilder.wrapFlexOut(name ?? { this.identityHash.abs.asString },
			this, rates, prependArgs, outClass, fadeTime
		);
	}
}

+ GraphBuilder {
	*wrapFlexOut { arg name, func, rates, prependArgs, outClass=\Out, fadeTime = 0.1;
		^SynthDef.new(name, { | out = 0, amp = 1 |
			var result, rate, env;
			result = SynthDef.wrap(func, rates, prependArgs).asUGenInput;
			rate = result.rate;
			if(rate === \scalar,{
				// Out, SendTrig etc. probably a 0.0
				result
			},{
				if(fadeTime.notNil, {
						result = this.makeFadeEnv(fadeTime) * result * amp;
				});
				outClass = outClass.asClass;
				outClass.replaceZeroesWithSilence(result.asArray);
				outClass.multiNewList([rate, out]++result)
			})
		})
	}
}


// registry and convenience methods for time references

BMTimeReferences {
	classvar timeReferences;
	
	*initClass {
		timeReferences = IdentityDictionary.new;
	}
	
	*addReference {|ref|
		timeReferences[ref.name] = 
			IdentityDictionary[\ref->ref, \time->0, \rate->0, \refTime->Main.elapsedTime];
		ref.addDependant(this);
	}
	
	*removeReference {|ref|
		timeReferences[ref.name] = nil;
		ref.removeDependant(this);
	}
	
	*timeReferences { ^timeReferences.collect({|v, k| v[0] }); }
	
	*currentTime { |ref|
		var dict;
		dict = timeReferences[ref.name];
		^dict[\time] + (Main.elapsedTime - dict[\refTime] * dict[\rate]);
	}
	
	*update { arg changed, what ...args;
		var dict;
		switch(what,
			
			\time, {
				dict = timeReferences[changed.name];
				dict[\time] = args[0];
				dict[\rate] = args[1];
				dict[\refTime] = args[2];
			},
			\stop, {
				dict = timeReferences[changed.name];
				dict[\time] = 0;
				dict[\rate] = 0;
				dict[\refTime] = Main.elapsedTime;
			}
		)
	
	}

}
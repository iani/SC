
UniqueSynthDef : AbstractUniqueServerObject {
	*mainKey { ^\synthdefs }

	doWhenLoaded { | func |
		/* 	do any any actions that require synthdefs to be sent before they can be successful
			used by UniqueBuffer to load its buffers, because some of them may want to play right away
			using some synthdef sent here. 
			UniqueSynthDef.doWhenLoaded({ buffers.first.makeObject });
		*/
		Chain(key).add(func);
		
	}	
}


Udef : UniqueSynthDef {} // synonym for UniqueSynthDef 


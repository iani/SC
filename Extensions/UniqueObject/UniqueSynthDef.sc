
UniqueSynthDef : AbstractUniqueServerObject {
	*mainKey { ^\synthdefs }

	*loadAllSynthDefs { | server |
		var synthDefs;
		server = server ? Server.default;
		synthDefs = this.onServer(server);
		synthDefs.inject(nil, { | a, b |
			if (a.notNil) { NotificationCenter.registerOneShot(a.key, \loaded, a, { b.sendToServer }) };
			b;
		});
		NotificationCenter.registerOneShot(synthDefs.last.key, \loaded, synthDefs.last, {
			NotificationCenter.notify(server, \synthDefsLoaded);
		});
		synthDefs.first.sendToServer;
//		UniqueSynthDef(server).doWhenLoaded({ buffers.first.makeObject });
//		synthDefs.last
	}

	doWhenLoaded { | func |
		/* 	do any actions that require synthdefs to be sent before they can be successful
			Used by UniqueBuffer to load its buffers, because some of them may want to play right away
			using some synthdef sent here. 
			UniqueSynthDef.doWhenLoaded({ buffers.first.makeObject });
		*/
		NotifyOnce(this.server, \synthDefsLoaded, this, func);
		
	}	
}


Udef : UniqueSynthDef {} // synonym for UniqueSynthDef 


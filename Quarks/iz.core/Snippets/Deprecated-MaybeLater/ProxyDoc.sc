/* IZ 2012 07 01
Each Document plays in its own proxy. 
Cooperates with Code. 

*/


ProxyDoc {
	classvar <>default;
	var <proxySpace;
	
	*initClass {
		StartUp add: { default = this.new }
	}
	
	*new { ^super.new.init }
	init { proxySpace = ProxySpace.new }

	*play { | object | ^default.play(object) }
	play { | object |
		// play an object as input to a proxy node named after the current Document 
		// called by Code: evalCurrentSnippetInProxySpace
		var out;	// the output of the current Document
		out = proxySpace[Document.current.name.asSymbol];
		if (out.isPlaying.not) { out.play };
		out.source = object;
	}

	*playCurrentDocProxy { ^default.playCurrentDocProxy }
	playCurrentDocProxy {
		proxySpace[Document.current.name.asSymbol].play;
	}
	
	*stopCurrentDocProxy { ^default.stopCurrentDocProxy }
	stopCurrentDocProxy {
		proxySpace[Document.current.name.asSymbol].stop;
	}
	
}


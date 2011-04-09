

DocWithBounds {
	var <doc;
	var <docProxy;
	
	*new { | doc, docProxy | 
		^this.newCopyArgs(doc, docProxy ?? { DocProxy(doc) });
	}
	
	name { ^doc.name }
	path { ^doc.path }
	bounds { ^doc.bounds }
	text { ^doc.text }
	string { ^doc.string }
	
	bounds_ { | bounds |
		doc.bounds = bounds;
		docProxy.bounds = bounds;	
	}
	
	front { doc.front }
	
	isListener { ^doc.isListener }
	
	close { doc.name.postln; "closing".postln; doc.close }
}
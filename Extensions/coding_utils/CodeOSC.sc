/* Given a document with snippets defined by //: comments, create one OSCresponder for each snippet based on the name of the comment, which triggers the snippet.

See Code, CodeButtons 

*/

CodeOSC {
	var <doc, <code, <headers, <oscnames, <responders, <snippets, <historyview;
	*new { | doc |
		doc = doc ?? { Document.current };
		^this.newCopyArgs(doc, Code(doc)).init;
	}

	init {
		headers = code.headers;
		oscnames = headers collect: { | h | h.findRegexp("[^/: ]+").first[1]; };
		responders = oscnames collect: this.makeResponder(_, _);
		historyview = EZListView(bounds: Rect(0, 0, 300, 200),
			label: "History: " ++ code.doc.name);
		historyview.addNotifier(this, \snippet, { | snippet |
			{ historyview.addItem(snippet, { snippet.postln; }); }.defer;
		});
	}
	
	
	makeResponder { | name, index |
		var snippet, compiledSnippet;
		snippet = code.getSnippetStringAt(index + 1);
		compiledSnippet = snippet.compile;
		^OSCresponder(nil, name.asSymbol, { | time, addr, msg |
			(msg: msg) use: { compiledSnippet.fork };
			this.notify(\snippet, snippet);
		}).add;
	}
}

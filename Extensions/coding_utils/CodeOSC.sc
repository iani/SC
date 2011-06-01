/* Given a document with snippets defined by //: comments, create one OSCresponder for each snippet based on the name of the comment, which triggers the snippet.

See Code, CodeButtons 

*/

CodeOSC : /*Window*/Resource {
	var <code, <headers, <oscnames, <responders, <snippets, <historyview;
	*new { | doc |
		doc = doc ?? { Document.current };
		^super.new(doc.name, doc).initOSC;
	}

	initOSC {
		code = Code(object);
		code.doc.name.postln;
		headers = code.headers;
		oscnames = headers collect: { | h | h.findRegexp("[^/: ]+").first; };
		responders = oscnames collect: this.makeResponder(_, _);
		historyview = EZListView(bounds: Rect(0, 0, 300, 800),
			label: "History: " ++ code.doc.name);
		historyview.addNotifier(this, \snippet, { | msg, snippet |
			{ historyview.addItem(msg.asString, { snippet.postln; }); }.defer;
		});
		postf("OSC responders generated: %\n", oscnames.flop[1]);
	}

	makeResponder { | name, index |
		var snippet, compiledSnippet;
		snippet = code.getSnippetStringAt(index + 1);
		compiledSnippet = snippet.compile;
		^OSCresponder(nil, (name ? [nil, "---"])[1].asSymbol, { | time, addr, msg |
			(msg: msg) use: { compiledSnippet.fork };
			this.notify(\snippet, [msg.asString, snippet]);
		}).add;
	}
}

/* Given a document with snippets defined by //: comments, create one OSCresponder for each snippet based on the name of the comment, which triggers the snippet.

See Code, CodeButtons 

*/

CodeOSC : WindowResource {
	var <code, <headers, <oscnames, <responders, <snippets;
	*new { | doc |
		doc = doc ?? { Document.current };
		^super.new(doc.name, { | cosc | this.makeWindow(cosc, doc) }).initOSC(doc);
	}

	*makeWindow { | self, doc |
		var window, historyview;
		window = Window("History: " ++ doc.name, Rect(0, 0, 300, 800));
		historyview = EZListView(window.view, Rect(0, 0, 300, 800));
		window.addNotifier(self, \snippet, { | msg, snippet |
			"window codeOSC received snippet".postln;
			{ historyview.addItem(msg.asString, { snippet.postln; }); }.defer;
		});
		^window;
	}

	initOSC { | argDoc |
		this.clearResponders;
		code = Code(argDoc);
		headers = code.headers;
		code.headers.postln;
		oscnames = headers collect: { | h | h.findRegexp("^//:([A-Za-z0-9_/]+)")[1]; };
		oscnames do: this.makeResponder(_, _);
		postf("OSC responders generated: %\n", responders collect: _.cmdName);
		object.front;
	}

	clearResponders {
		responders do: _.remove;
		responders = nil;	
	}

	makeResponder { | name, index |
		var snippet, compiledSnippet;
		if (name.isNil) { ^this };
		snippet = code.getSnippetStringAt(index + 1);
		compiledSnippet = snippet.compile;
		responders = responders add: OSCresponder(nil, name[1].asSymbol, { | time, addr, msg |
			currentEnvironment[\msg] = msg;
//			(msg: msg) use: { compiledSnippet.fork };
			compiledSnippet.fork;
			this.notify(\snippet, [msg.asString, snippet]);
		}).add;
	}
}

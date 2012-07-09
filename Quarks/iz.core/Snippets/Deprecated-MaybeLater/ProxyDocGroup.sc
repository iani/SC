/* IZ 2012 07 01
Create a group of Documents for ProxyDoc, from a single document containing snippets

ProxyDocGroup.new;


ProxyDocGroup.fromCurrentDoc;

*/

ProxyDocGroup {
	classvar >default;
	
	var <doc;	// the source doc containing the code of all proxy-docs in this group
			// i.e. the document whose snippets form the proxy docs' code
	var <path;		// path of the source doc. Needed to reopen doc if it is closed
	var <docs;		// the individual proxy-docs

	*fromCurrentDoc {
		^this.new(Document.current);
	}

	*new { | doc |
		if (doc.isNil) { ^this.default };
		^super.new.init(doc);
	}
	
	*default {
		if (default.isNil) { default = this.open(this.defaultSourceDocPath) };
		^default;	
	}

	*open { | path | // open doc from path
		^this.new(Document.open(path));
	}
	
	
	*defaultSourceDocPath {
		^this.filenameSymbol.asString.dirname +/+ "ProxyDocs.scd";
	}

	init { | argDoc |
		doc = argDoc;
		path = doc.path;
		this.arrangeDocs;
	}

	*arrangeDocs { this.default.arrangeDocs }
	arrangeDocs {
		var height = 180, width = 640, screenheight, top, rect, left = 0;

		screenheight = Window.fullScreenBounds.height;
		top = screenheight - height;

		docs = this.getAllSnippetStrings collect: { | s, i |
			Document(this.getDocNameFromSnippet(s, i), s);
		};
		{
			1.wait;
			docs do: { | d |
				rect = Rect(left, top, width, height);
				d.bounds = rect;
				top = top - height;
				if (top < 0) {
					top = screenheight - height;
					left = left + width;
				};
			}
		}.fork(AppClock);
	}
	
	getAllSnippetStrings { ^Code(doc).getAllSnippetStrings; }
	
	getDocNameFromSnippet { | snippet, nr = 0 |
		var name;
		name = snippet.findRegexp("^//:([a-z][a-zA-Z0-9_]+)")[1];
		^(name ?? { [0, format("out%", nr + 1)] })[1];
	}

	hideDocs {
		
	}
	
	showDocs {
		
	}
	
	saveDocs {
		
	}
}
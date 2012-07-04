/* IZ 2012 07 01
Create a group of Documents for ProxyDoc, from a single document containing snippets

ProxyDocGroup.new;

*/


ProxyDocGroup {
	classvar >default;
	
	var <path;		// the path of the source doc. Needed to reopen doc if it is closed
	var <sourceDoc;	// the source doc containing the code of all proxy-docs in this group
	var <docs;		// the individual proxy-docs

	*new { | path |
		if (path.isNil) {
			path = this.defaultSourceDocPath;
		}
		^super.new.init(path);
	}
	
	init { | argPath |
		path = argPath;
		sourceDoc = Document.open(path);	
		this.arrangeDocs;
	}
	
	*defaultSourceDocPath {
		^this.filenameSymbol.asString.dirname +/+ "ProxyDocs.scd";
	}
	
	*arrangeDocs { this.default.arrangeDocs }
	arrangeDocs {
		var code, docname, name, doc;
		code = Code(Document.current);
		name = Document.current.name;
		code.positions.size + 1 do: { | i |
			docname = format("%[%]", name, i);
			Document(docname, code.getSnippetStringAt(i));
		};
	}
	
	hideDocs {
		
	}
	
	showDocs {
		
	}
	
	saveDocs {
		
	}
}
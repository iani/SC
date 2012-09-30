/* iz Sun 30 September 2012  2:06 PM EEST

Load text from path and create proxies and proxy history from it. 
Store path of source and names and histories of proxies created. 
Used by ScriptListGui to load and store proxy docs. 

*/

ProxyDoc {
	classvar <all;
	var <path;		// path of doc that held the original source from which the proxies were made
	var <source;
	var <proxySpace;
	var <proxyItems;
	
	*initClass {
		all = IdentityDictionary.new;
	}
	
	*new { | path, readFromDoc = false |
		var new;
		path = path.asSymbol;
		new = all[path];
		new !? { ^new };
		^this.newCopyArgs(path).init(readFromDoc);
	}

	init { | readFromDoc = false |
		all[path] = this;
		proxySpace = ProxySpace();
		proxyItems = proxySpace.proxies;
		if (readFromDoc) { this.makeProxiesFromDoc };
	}

	makeProxiesFromDoc {
		var file, thePath;
		thePath = path.asString;
		if (File.exists(thePath)) {
			file = File(thePath, "r");
			source = file.readAllString;
			file.close;
		}{
			"ProxyDoc - FILE NOT FOUND:".postln;
			thePath.postln;
			source = "";
		};
		proxySpace parseSnippets: source;
	}

	*newFromArchive { | data |
		/* When loaded from archive, recreate my proxySpace and all my ProxyItems from my history */
		var new;
		new = this.new(data[0]);
		if (new.proxyItems.size < data[1].size) { new makeProxiesFromArchive: data[1][1..] };
		^new;
	}

	makeProxiesFromArchive { | data |
		data do: { | pd |
			proxySpace.at(pd[0]);	// create ProxyItem and add it as last item in proxyItems
			proxyItems.last.history.array = pd[1];
		};
	}

	archiveData {
		/* Return path + source + all proxy names + histories for storing in sctxar format */		^[path, proxyItems collect: { | p | [p.name, p.history] }];
	}

	*loadDialog { // for testing
		Dialog.getPaths({ | paths | 
			paths collect: this.new(_).makeProxiesFromDoc;
		})
	}
}

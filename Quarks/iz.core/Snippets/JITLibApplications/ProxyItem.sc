/* IZ Wed 05 September 2012 12:01 PM BST

Sat 29 September 2012  4:03 PM EEST: 
ProxyItem is the place to put snippet parsing code.

*/

NamedItem {
	var <>name, <>item;

	*new { | name, item | ^this.newCopyArgs(name, item) }

	asString { ^name }
	== { | item |
		if (item.isNil) {
			^true
		}{
			^name == item.name
		}
	}
}

ProxyItem : NamedItem {
	var <history, <specs;
	
	*new { | name, item | ^this.newCopyArgs(name, item).init }
	
	init {
		history = List.new;
		specs = List.new;
		if (item.isNil) {
			this.specs = [['-', nil]]
		}{ this.addNotifier(item, \proxySpecs, { | specs | this.specs = specs }) };
	}
	
	addSnippet { | snippet |
		history.add(snippet);
		history.notify(\list);
	}

	specs_ { | argSpecs |
		specs.array = argSpecs collect: { | s |
			Value(item, ProxyControl().proxy_(item).parameter_(s[0]).spec_(s[1]));
		};
		specs.notify(\list);
	}

	evalSnippet { | argSnippet, start = true, addToSourceHistory = true |
		/* evaluate a code snippet and set it as source to a proxy
		If argSnippet, argProxy, argProxyName are not given, then extract 
		proxy, proxyName and snippet from the code of doc. 
		If snippet begins with a comment, skip this when entering the code to History
		*/ 
		var index, source;
		source = argSnippet.interpret;
		if (source.isValidProxyCode) {
			item.source = source;
			MergeSpecs.parseArguments(item, argSnippet);
			if (addToSourceHistory) { this.addSnippet(argSnippet); };
			if (item.rate === \audio and: { start } and: { item.isMonitoring.not }) {
				item.play;
			};
		}{
			postf("snippet: %\n", argSnippet);
		}
//		^item;
	}

	play { item.play }
	stop { item.stop }
	isMonitoring { ^item.isMonitoring }
}
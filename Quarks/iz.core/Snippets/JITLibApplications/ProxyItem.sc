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
	classvar <>extraSpecs;
	var <history, <specs;

	*initClass {
		Class.initClassTree(Spec);
		Class.initClassTree(ControlSpec);
		extraSpecs = [[\vol, ControlSpec(0, 2)], [\fadeTime, ControlSpec(0, 60)]];
//		cachedSpecs = IdentityDictionary.new;
	}
	
	*new { | name, item | ^this.newCopyArgs(name, item).init }
	
	init {
		history = List.new;
		specs = List.new;
		this.addNotifier(item, \source, { this.getParamsFromProxy });
	}

	getParamsFromProxy {
		this.specs =
			(if (item.rate === \audio) { extraSpecs } { [] })
			++ 
			(item.getKeysValues collect: { | keyVal |
				[keyVal[0], (keyVal[0].asSpec ?? { \bipolar.asSpec }).default_(keyVal[1])] 
			});
	}

	specs_ { | argSpecs |
		specs.array = argSpecs collect: { | spec | Value(item, ProxyControl(item, spec)); };
		specs.changed(\list);
	}

	addSnippet { | snippet |
		history.add(snippet);
		history.changed(\list);
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
			this.parseSnippetArguments(argSnippet);
			if (addToSourceHistory) { this.addSnippet(argSnippet); };
			if (item.rate === \audio and: { start } and: { item.isMonitoring.not }) {
				item.play; 
			};
		}{
			postf("snippet: %\n", argSnippet);
		}
	}

	parseSnippetArguments { | argSnippet |
		var snippetHeader, snippetKeys, snippetVals;
		snippetHeader = argSnippet.findRegexp("^//[^[]*([^\n]*)");
		if (snippetHeader.size > 0) {
			#snippetKeys, snippetVals = (snippetHeader[1][1].interpret ?? { [] }).clump(2).flop;
		};
		this.inspect;
//		snippetVals = 
		// NOT CoMPLETED!!!!
	}

	nilSpecs { ^['-', nil] }

	checkEvalPlay {
		/* If NodeProxy has no source yet, eval first snippet to give it source before playing */
		if (item.source.isNil) {
			this.evalSnippet(history.first, start: true, addToSourceHistory: false);
		}{
			item.play;
		}
	}
	play { item.play }
	stop { item.stop }
	isMonitoring { ^item.isMonitoring }
	delete { | proxySpace |	
		// TODO: Should ProxyItem store its ProxySpace?
		if (item.notNil) { item.clear; };
		proxySpace removeProxyItem: this;
		
	}
	
	makeHistoryString {
		var docString;
		docString = format(
			"\n/* =========== HISTORY FOR % on % =========== */", 
			name,
			Date.getDate.format("%Y-%d-%e at %Hh:%mm:%Ss'")
		);
		^history.inject(docString, { | a, b, i |
			a 
			++ format("\n\n// ***** % ***** \n", i + 1)
			++ b
		};
		);
	}
}
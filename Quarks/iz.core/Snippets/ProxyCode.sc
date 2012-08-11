/* IZ 2012 07 01

Evaluate a snippet from a Document and set the resulting object as source to a proxy named after the name of the snippet. 
Additional methods for starting, stopping, or incrementing / decrementing the volume of the proxy. 
Add the proxy code in History but also add the snippet that was evaluated in a history of snippets for this proxy. 

Some of the functionality is evoked by keyboard shortcuts set by Code, another part is used via gui through the ProxySourceEditor. 

*/

ProxyCode {
	classvar all;						// all ProxyCode instances in a Dictionary by Document
	classvar <historyTimer;				// routine that counts time from last executed snippet
	classvar <>historyEndInterval = 300;	// end History if nothing has been done for 5 minutes

 	var <doc;			// the document from which this snippet was 
	var <proxySpace; 
	var <proxy;
	var <proxyName; 
	var <params;		// parameters and their specs, as parsed from the proxy and 
					// from the initial comment line of the snippet
	var <snippet; 
	var <index;
	var <proxyHistory; // holds all source code for each NodeProxy by key
	
	*initClass {
		// since CmdPeriod stops routines, restart the historyTimer routine
		StartUp add: { CmdPeriod add: { { this.startHistoryTimer }.defer(0.1) } };
	}

	*startHistoryTimer {
		if (historyTimer.notNil) { historyTimer.stop };
		historyTimer = {
			historyEndInterval.wait;
			History.end;
		}.fork(AppClock);	
	}

	*new { | doc |
		var new;
		doc = doc ?? { Document.current };
		new = this.all[doc];
		if (new.isNil) { new = this.newCopyArgs(doc).init };
		^new;
	}

	*all {
		if (all.isNil) { all = IdentityDictionary.new };
		^all;
	}

	init {
		all[doc] = this;
		this.initProxySpace;
	}

	initProxySpace {
		proxySpace = doc.envir ?? { ProxySpace.new; };
		doc.envir = proxySpace;
		proxyHistory = IdentityDictionary.new;
	}

	getSnippet {
		var code;
		if (History.started.not) {
			History.clear;
			History.start;
			this.enterSnippet2History("ProxySpace.push");
		};
		if (doc.envir.isNil) { this.initProxySpace };
		code = Code(doc);
		index = code.findIndexOfSnippet;
		^snippet = code.getSnippetStringAt(index);	
	}

	getProxy {
		proxyName = snippet.findRegexp("^//:([a-z][a-zA-Z0-9_]+)")[1];
		proxyName = (proxyName ?? { [0, format("out%", index)] })[1].asSymbol;
		^proxy = proxySpace[proxyName];
	}

	evalInProxySpace { | argSnippet, argProxy, argProxyName, start = true, addToSourceHistory = true |
		/* evaluate a code snippet and set it as source to a proxy
		If argSnippet, argProxy, argProxyName are not given, then extract 
		proxy, proxyName and snippet from the code of doc. 
		If snippet begins with a comment, skip this when entering the code to History
		*/ 
		var index, source;
		snippet = argSnippet ?? { this.getSnippet };
		source = snippet.interpret;
		if (source.isValidProxyCode) {
			/* TODO: If the snippet comes from a ProxySourceEditor, then parse proxy name
			from snippet, and use that if available. Notify ProxySourceEditor to change 
			proxy name */
			proxy = argProxy ?? { this.getProxy };
			argProxyName !? { proxyName = argProxyName };
			proxy.source = source;
			postf("doc: %, snippet: %\nproxy (%): %\n", doc.name, snippet, proxyName, proxy);
			if (snippet[0] == $/) {
				index = snippet indexOf: $\n;
				this.enterSnippet2History(
					format("%~% = %", snippet[..index], proxyName, snippet[index + 1..])
				);
				this.parseArguments(proxy, snippet);
			}{
				this enterSnippet2History: format("~% = %", proxyName, snippet);
				this.parseArguments(proxy);
			};
			if (addToSourceHistory) { this.addNodeSourceCodeToHistory(proxyName, snippet); };
			if (proxy.rate === \audio and: { start } and: { proxy.isMonitoring.not }) {
				this.startProxy(proxy, proxyName);
			};
		}{
			postf("snippet: %\n", snippet);
			this.enterSnippet2History(snippet);
		}
	}

	parseArguments { | argProxy, argSnippet |
		var mySpecs;
		argSnippet !? { mySpecs = argSnippet.findRegexp("^//[^[]*([^\n]*)")[1][1].interpret; };
		mySpecs = MergeSpecs(argProxy, mySpecs);
		proxy.notify(\proxySpecs, mySpecs);
		ProxySpecWatcher.cacheSpecs(argProxy, mySpecs);
	}


	enterSnippet2History { | argSnippet |
		History.enter(argSnippet);
		this.class.startHistoryTimer;		
	}

	addNodeSourceCodeToHistory { | argProxyName, argSnippet |
		var nodeHistory;
		argProxyName = argProxyName.asSymbol;
		nodeHistory = proxyHistory[argProxyName];
		nodeHistory = nodeHistory add: argSnippet;
		proxyHistory[argProxyName] = nodeHistory;
		this.notify(\proxySource, [argProxyName, nodeHistory]);
	}

	startProxy { | argProxy, argProxyName |
		this.enterSnippet2History(format("~%.play;", argProxyName));
		argProxy.play;
		postf("proxy % started: %\n", argProxyName, argProxy);
	}

	deleteNodeSourceCodeFromHistory { | argProxyName, snippetIndex |
		var nodeHistory;
		argProxyName = argProxyName.asSymbol;
		nodeHistory = proxyHistory[argProxyName];
		nodeHistory.removeAt(snippetIndex - 1);
		this.notify(\proxySource, [argProxyName, nodeHistory]);
	}

	editNodeProxySource { | proxyName |
		// received from NanoK2Strip. Edit the source code of the proxy
		ProxySourceEditor(this, proxyName);
	}
	
	openProxySourceEditor {
		// called by keyboard shortcut from Code
		this.getSnippet;
		this.getProxy;
		proxyHistory[proxyName] ?? {
			this.addNodeSourceCodeToHistory(proxyName, snippet);
		};
		ProxySourceEditor(this, proxyName);		
	}

	playCurrentDocProxy {
		this.getSnippetAndProxy;
		if (proxy.source.isNil) {
			^this.evalInProxySpace;
		};
		if (proxy.isMonitoring.not) {
			proxy.play;
			postf("proxy % started: % \n", proxyName, proxy);
			this.enterSnippet2History(format("~%.play", proxyName));
		}
	}

	getSnippetAndProxy {
		this.getSnippet;
		this.getProxy;	
	}

	stopCurrentDocProxy {
		this.getSnippetAndProxy;
		this.stopProxy(proxy, proxyName)
	}

	stopProxy { | argProxy, argProxyName |
		argProxy.stop;
//		argProxy.end; // fades out well, but not good if using as input to fx NodeProxy
		postf("proxy % stopped: %\n", argProxyName, argProxy);
		this.enterSnippet2History(format("~%.stop", argProxyName));
	}

	proxyMixer {
//		ProxyMixer(doc.envir);
//		if (doc.envir.isNil) { this.initProxySpace };
		NanoKontrol2(proxySpace, doc.name, this);
	}
	
	changeVol { | increment = 0.1 |
		var vol1, vol2;
		this.getSnippetAndProxy;
		vol1 = proxy.vol;
		vol2 = vol1 + increment max: 0;
		proxy.vol = vol2;
		postf("%: vol % -> %\n", proxyName, vol1.round(0.000001), vol2.round(0.000001));
		this.enterSnippet2History(format("~%.vol = %", proxyName, vol2));
	}
}


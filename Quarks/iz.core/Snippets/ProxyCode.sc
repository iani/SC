/* IZ 2012 07 01

Evaluate a snippet from a Document and set the resulting object as source to a proxy named after the name of the snippet. 
Additional methods for starting, stopping, or incrementing / decrementing the volume of the proxy. 
Add the proxy code in History but also add the snippet that was evaluated in a history of snippets for this proxy. 

Some of the functionality is evoked by keyboard shortcuts set by Code, another part is used via gui through the ProxySourceEditor. 

TODO: proxyHistory should be per proxySpace, not per document 

*/

ProxyCode {
	classvar all;						// all ProxyCode instances in a Dictionary by Document
	classvar <historyTimer;				// routine that counts time from last executed snippet
	classvar <>historyEndInterval = 300;	// end History if nothing has been done for 5 minutes
	classvar <proxyHistory; // holds all source code for each NodeProxy

 	var <doc;			// the document from which this snippet was 
	var <proxySpace; 
	var <proxy;
	var <proxyName; 
	var <params;		// parameters and their specs, as parsed from the proxy and 
					// from the initial comment line of the snippet
	var <snippet; 
	var <index;
	
	*initClass {
		// since CmdPeriod stops routines, restart the historyTimer routine
		proxyHistory = IdentityDictionary.new;
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
		doc = doc ?? { Document.current; };
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
		proxySpace = Document.prepareProxySpace(doc);
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
		// The proxyName part maybe should be replaced by getProxyName method
		proxyName = snippet.findRegexp("^//:([a-z][a-zA-Z0-9_]+)")[1];
		proxyName = (proxyName ?? { [0, format("out%", index)] })[1].asSymbol;
		^proxy = proxySpace[proxyName];
	}

	getProxyName { | argSnippet, argIndex = 0 |
		^(argSnippet.findRegexp("^//:([a-z][a-zA-Z0-9_]+)")[1] ?? {
			[0, format("out%", argIndex)] 
		})[1].asSymbol;		
	}
	
	makeProxyHistoryFromDoc { | eraseDuplicates = true |
		// get all proxies and all snippets, and store them in proxyHistory.
		// Can be used to open ProxySourceEditor on the entire document, with history ready.
		// It will also create proxies for all snippets with different names on the comment line
		// TODO: if eraseDuplicates is true, then remove from history
		// identical snippets for the same proxy.
		Code(doc).getAllSnippetStrings(skipFirstSnippet: false) do: { | newSnippet, argIndex |
			this.addNodeSourceCodeToHistory(
				proxySpace[this.getProxyName(newSnippet, argIndex).asSymbol],
				newSnippet;
			)
		};
	}

	clearProxyHistory { // NOT TESTED
		// when clearing history, notify any editors or other objects
		var oldProxies;
		oldProxies = proxyHistory.keys;
		proxyHistory = IdentityDictionary.new;
		oldProxies do: { | oldProxy | 
//			op.notify(\proxyHistory, [[]]); 
//			[this, thisMethod.name, "notifying history now:"].postln;
			this.notifyHistoryChanged(oldProxy, [], oldProxy);
		}
	}

	removeDuplicatesFromProxyHistory {
		// under construction
		// remove those snippets from proxyHistory that have been entered twice.
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
				MergeSpecs.parseArguments(proxy, snippet);
			}{
				this enterSnippet2History: format("~% = %", proxyName, snippet);
				MergeSpecs.parseArguments(proxy);
			};
			if (addToSourceHistory) { this.addNodeSourceCodeToHistory(proxy, snippet); };
			if (proxy.rate === \audio and: { start } and: { proxy.isMonitoring.not }) {
				this.startProxy(proxy, proxyName);
			};
		}{
			postf("snippet: %\n", snippet);
			this.enterSnippet2History(snippet);
		}
	}

	enterSnippet2History { | argSnippet |
		History.enter(argSnippet);
		this.class.startHistoryTimer;		
	}
	
	// proxy history stuff 
	
	*replaceProxyHistory { | argProxy, argHistory, argChanger |
		/* 	An application that changes my history sends the new version to me 
			I notify all applications that I changed, so that they may update.
			I send also who updated me, so that the updater will not re-update itself
			(otherwise an endless loop would ensue).
		*/
		proxyHistory[argProxy] = argHistory;
		this.notifyHistoryChanged(argProxy, argHistory, argChanger);
	}

	*notifyHistoryChanged { | argProxy, argHistory, argChanger |
		/* update interested applications of my new history for a proxy. 
			Also tell them who did the change, so that the changer may avoid re-updating */
		argProxy.notify(\proxyHistory, [argHistory, argChanger]);
	}

	notifyHistoryChanged { | argProxy, argHistory, argChanger  |
		/* update interested applications of my new history for a proxy. 
			Also tell them who did the change, so that the changer may avoid re-updating */
		this.class.notifyHistoryChanged(argProxy, argHistory, argChanger);
	}
	
	addNodeSourceCodeToHistory { | argProxy, argSnippet |
		var history;
		history = proxyHistory[argProxy];
		proxyHistory[argProxy] = history = history add: argSnippet;
//		argProxy.notify(\proxyHistory, [history]);
//		[this, thisMethod.name, "notifying history now:"].postln;
		this.notifyHistoryChanged(argProxy, history, argProxy);
	}

	
	startProxy { | argProxy, argProxyName |
		this.enterSnippet2History(format("~%.play;", argProxyName));
		argProxy.play;
		postf("proxy % started: %\n", argProxyName, argProxy);
	}

	deleteNodeSourceCodeFromHistory { | argProxy, snippetIndex |
		var history;
		history = proxyHistory[argProxy];
		history.removeAt(snippetIndex - 1);
//		argProxy.notify(\proxyHistory, [history]);
//		[this, thisMethod.name, "notifying history now:"].postln;
		this.notifyHistoryChanged(argProxy, history, argProxy);
	}

	editNodeProxySource { | proxy |
		// received from NanoK2Strip. Edit the source code of the proxy
		ProxyCodeEditor(this, proxy);
	}
	
	openProxySourceEditor {
		// called by keyboard shortcut from Code
		this.getSnippet;
		this.getProxy;
		proxyHistory[proxy] ?? {
			this.addNodeSourceCodeToHistory(proxy, snippet);
		};
//		ProxySourceEditor(this, proxyName, proxy);
		ProxyCodeEditor(this, proxy);
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
		ProxyCodeMixer(doc, 8);
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
	
	// =========== Documenting code history ===========

	openHistoryInDoc { | argProxy |
		var title, docString;
		title = proxyHistory[argProxy];
		if (title.isNil) {
			title = Date.getDate.format("History for all proxies on %Y-%d-%e at %Hh:%mm:%Ss");
			docString = this.makeHistoryStringForAll;
		}{
			title = format("History for all % on %",
				proxySpace.envir.findKeyForValue(proxy),
				Date.getDate.format("%Y-%d-%e at %Hh:%mm:%Ss")
			);
			docString = this.makeHistoryStringForProxy(proxy);
		};
		^Document(title, docString)
	}

	makeHistoryStringForAll {
		var docString, histories;
		docString = format(
			"/* *********** HISTORY FOR ALL PROXIES on % *********** */\n",
			Date.getDate.format("%Y-%d-%e at %Hh:%mm:%Ss")
		);
		histories = proxyHistory.keys collect: this.makeHistoryStringForProxy(_);
		^histories.inject(docString, { | a, b | a ++ b });
	}

	makeHistoryStringForProxy { | proxy |
		var myHistory, docString;
		myHistory = proxyHistory[proxy];
		docString = format(
			"\n/* *********** HISTORY FOR % on % *********** */", 
			proxySpace.envir.findKeyForValue(proxy), 
			Date.getDate.format("%Y-%d-%e at %Hh:%mm:%Ss'")
		);
		^myHistory.inject(docString, { | a, b, i |
			a 
			++ format("\n\n// ========================= % ========================= \n", i + 1)
			++ b
		};
		);
	}
}


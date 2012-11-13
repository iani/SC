/* IZ Wed 05 September 2012 12:01 PM BST

Sat 29 September 2012  4:03 PM EEST:
ProxyItem is the place to put snippet parsing code.

*/

NamedItem {
	var <>name = "-", <>item;
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
	var <>container;	// also acts as adapter in Value. See Widget:simpleProxyWatcher
	var <>nameWithSnippet; 	// "name:snippetName". Used for display by ScriptMixer

	updateMessage { \proxy } // Value:updateListeners compatibility.

	*initClass {
		Class.initClassTree(Spec);
		Class.initClassTree(ControlSpec);
//		extraSpecs = [[\vol, ControlSpec(0, 2)], [\fadeTime, ControlSpec(0, 60)]];
		extraSpecs = [['-', nil], [\vol, ControlSpec(0, 2)], [\fadeTime, ControlSpec(0, 60)]];
//		cachedSpecs = IdentityDictionary.new;
	}

	*new { | name = "-", proxy | ^this.newCopyArgs(name).init(proxy) }

	init { | proxy |
		item = proxy ?? { NodeProxy() };
		history = List.new;
		this.addNotifier(item, \source, { this.getParamsFromProxy });
		this.resetSpecs;
	}

	resetSpecs {
		specs = List.new;
		this.getParamsFromProxy;
	}

	reset {
		item.clear;
		this.resetSpecs;
	}

	getParamsFromProxy {
		this.specs =
			(if (item.rate === \audio) { extraSpecs } { [['-', nil]] })
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
//		this.inspect;
//		snippetVals =
		// NOT CoMPLETED!!!!
	}

	nilSpecs { ^['-', nil] }

	checkEvalPlay { | snippet |
		/* If NodeProxy has no source yet, get a source from snippet */
		if (item.source.isNil) {
			this.evalSnippet(snippet ?? { history.first ?? { "" } },
				start: true, addToSourceHistory: (history.size == 0)
			);
		}{
			item.play;
		}
		^item;
	}
	toggle { if (this.isMonitoring) { this.stop } { this.play } }
	isMonitoring { ^item.isMonitoring }
	play { item.play }
	stop { item.stop }
	delete { | proxySpace |
		// TODO: Should ProxyItem store its ProxySpace?
		if (item.notNil) { item.clear; };
		proxySpace removeProxyItem: this;

	}

	fadeTo { | targetVol = 1 |
		var curVol;
		curVol = item.vol;
		if (targetVol < curVol) {
			{
				while { targetVol < curVol }{
					curVol = curVol - 0.01;
					item.vol = curVol;
					0.03.wait;
				};
				item.vol = targetVol;
			}.fork;
		}{
			{
				while { targetVol > curVol }{
					curVol = curVol + 0.01;
					item.vol = curVol;
					0.03.wait;
				};
				item.vol = targetVol;
			}.fork;
		};
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
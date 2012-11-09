/* iz 

Moving the snippet-to-code parsing functionality away from ProxyCode / ProxyCodeDoc to be able to use it with snippets stored in sctxar by ScriptListGui. 


To implement: 

evalInProxySpace

openHistoryInDoc

loadAll


*/

ProxyCodeParser {
	
	var proxySpace;
	
	addNodeSourceCodeToHistory { | argProxy, argSnippet |
		proxySpace.proxyItem(argProxy).addSnippet(argSnippet);
	}

	deleteNodeSourceCodeFromHistory { | argProxy, argSnippet |
		proxySpace.proxyItem(argProxy).deleteSnippet(argSnippet);
	}

	evalInProxySpace { | argSnippet, argProxy, argProxyName, start = true, addToSourceHistory = true |
		/* evaluate a code snippet and set it as source to a proxy
		If argSnippet, argProxy, argProxyName are not given, then extract 
		proxy, proxyName and snippet from the code of doc. 
		If snippet begins with a comment, skip this when entering the code to History
		*/ 
		var index, source;
		source = argSnippet.interpret;
		if (source.isValidProxyCode) {
			argProxy.source = source;
			if (argSnippet[0] == $/) {
				index = argSnippet indexOf: $\n;
				MergeSpecs.parseArguments(argProxy, argSnippet);
			}{
				MergeSpecs.parseArguments(argProxy);
			};
			if (addToSourceHistory) { this.addNodeSourceCodeToHistory(argProxy, argSnippet); };
			if (argProxy.rate === \audio and: { start } and: { argProxy.isMonitoring.not }) {
				argProxy.play;
			};
		}{
			postf("snippet: %\n", argSnippet);
		}
		^argProxy;
	}

	openHistoryInDoc { | argProxy |
		var title, docString;
		if (argProxy.isNil) {
			title = Date.getDate.format("History for all proxies on %Y-%m-%e at %Hh:%Mm:%Ss");
			docString = this.makeHistoryStringForAll;
		}{
			title = format("History for % on %",
				proxySpace.proxyItem(argProxy).name,
				Date.getDate.format("%Y-%d-%e at %Hh:%mm:%Ss")
			);
			docString = this.makeHistoryStringForProxy(argProxy);
		};
		^Document(title, docString)
	}

	makeHistoryStringForAll {
		var docString, histories;
		docString = format(
			"/* *********** HISTORY FOR ALL PROXIES on % *********** */\n",
			Date.getDate.format("%Y-%m-%e at %Hh:%Mm:%Ss")
		);
		docString = docString ++ this.makeLoadBuffersString;
		histories = Library.at('Proxies', proxySpace) collect: this.makeHistoryStringForProxy(_);
		^histories.inject(docString, { | a, b | a ++ b });
	}

	makeLoadBuffersString {
		var buffers;
		buffers = Library.at('Buffers').asArray;
		if (buffers.size == 0) { ^"" };
		^buffers.inject("\n// ====== BUFFERS ====== \n\n", { | str, b |
			str ++ format("BufferItem(%).load;\n", b.name.asCompileString);
		});
	}

	makeHistoryStringForProxy { | proxyItem |
		var myHistory, docString;
		myHistory = proxyItem.history;
		docString = format(
			"\n/* =========== HISTORY FOR % on % =========== */", 
			proxyItem.name;
			Date.getDate.format("%Y-%d-%e at %Hh:%mm:%Ss'")
		);
		^myHistory.inject(docString, { | a, b, i |
			a 
			++ format("\n\n// ***** % ***** \n", i + 1)
			++ b
		};
		);
	}
}
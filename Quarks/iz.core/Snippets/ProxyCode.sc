/* IZ 2012 07 01
Each Snippet plays in its own proxy. 
Cooperates with Code. 

Forked from ProxyDoc (which has been deprecated). 
Replaces ProxyDoc. 

*/

ProxyCode {
	classvar all;
	classvar <historyTimer;				// routine that counts time from last executed snippet
	classvar <>historyEndInterval = 300;	// end History if nothing has been done for 5 minutes
 
 	var <doc;
	var <proxySpace, proxy, proxyName, snippet, index;
	
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
		proxySpace = ProxySpace.new;
		doc.envir = proxySpace;
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
		snippet = code.getSnippetStringAt(index);		
	}

	getProxy {
		proxyName = snippet.findRegexp("^//:([a-z][a-zA-Z0-9_]+)")[1];
		proxyName = (proxyName ?? { [0, format("out%", index)] })[1].asSymbol;
		proxy = proxySpace[proxyName];
	}


	evalInProxySpace {
		var index, source;
		this.getSnippet;
		source = snippet.interpret;
		if (source.isValidProxyCode) {
			this.getProxy;
			proxy.source = source;
			postf("snippet: %\nproxy (%): %\n", snippet, proxyName, proxy);
			index = snippet indexOf: $\n;
			this.enterSnippet2History(
				format("%~% = %", snippet[..index], proxyName, snippet[index + 1..]);
			);
			if (proxy.rate === \audio) {
				proxy.play;
				this.enterSnippet2History(format("~%.play;", proxyName));
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

	playCurrentDocProxy {
		this.getSnippetAndProxy;
		proxy.play;
		postf("proxy % started: % \n", proxyName, proxy);
		this.enterSnippet2History(format("~%.play", proxyName));
	}

	getSnippetAndProxy {
		this.getSnippet;
		this.getProxy;	
	}

	stopCurrentDocProxy {
		this.getSnippetAndProxy;
		proxy.stop;
//		proxy.end; // fades out well, but not good if using as input to fx NodeProxy
		postf("proxy % stopped: %\n", proxyName, proxy);
		this.enterSnippet2History(format("~%.stop", proxyName));
	}

	proxyMixer {
		ProxyMixer(doc.envir);
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


/* IZ Sat 04 August 2012  9:45 AM EEST

Create array of ControlSpecs for a NodeProxy by merging specs created from the argument-default list pairs obtained by proxy.getKeysValues with any spec-specs provided in an array of form: 
[parameter1: spec1, paramereter2: spec2 ...] 
The spec-specs array is an optional part of the heading of snippets: 

//:<proxyname> [parameter1: spec1, paramereter2: spec2 ...]

*/

MergeSpecs {
	var proxy, proxyArgs, snippetArgs, rate, mergedSpecs;
	classvar <>extraSpecs;		// specs for nil, vol and fadeTime
	classvar <cachedSpecs;		// Dictionary of previously parsed specs for each proxy

	*initClass {
		Class.initClassTree(Spec);
		Class.initClassTree(ControlSpec);
		extraSpecs = [['-', nil], [\vol, ControlSpec(0, 2)], [\fadeTime, ControlSpec(0, 60)]];
		cachedSpecs = IdentityDictionary.new;
	}

	*getSpecsFor { | proxy |
		^cachedSpecs[proxy] ?? { this.new(proxy) }
	}

	*parseArguments { | argProxy, argSnippet |
		var mySpecs, myName;
		argSnippet !? {
			myName = argSnippet.findRegexp("^//[^[]*([^\n]*)");
			if (myName.size > 0) {
				mySpecs = myName[1][1].interpret;
			};
		};
		mySpecs = this.new(argProxy, mySpecs);
		argProxy.changed(\proxySpecs, mySpecs);
	}

	*new { | proxy, snippetArgs |
		if (proxy.isNil) { ^this.nilSpecs };
		^this.fromArgs(proxy, proxy.getKeysValues, snippetArgs, proxy.rate);
	}
	
	*fromArgs { | proxy, proxyArgs, snippetArgs, rate |
		^this.newCopyArgs(proxy, proxyArgs, snippetArgs, rate).mergeSpecs;
	}

	mergeSpecs {
		var index, spec, keys, key, value, snippetKeys, snippetVals, finalSpecs;
		#snippetKeys, snippetVals = (snippetArgs ?? { [] }).clump(2).flop;
		proxyArgs do: { | keyValue |
			#key, value = keyValue;
			index = snippetKeys indexOf: key;
			if (index.isNil) {
				spec = key.asSpec ?? { \bipolar.asSpec };
				value !? { spec.default = value };
			}{
				spec = snippetVals[index].asSpec
			};
			mergedSpecs = mergedSpecs add: [key, spec];
		};
		keys = proxyArgs.flop.first;
		snippetArgs pairsDo: { | key, value |
			if (keys.includes(key).not) {
				mergedSpecs = mergedSpecs add: [key, value.asSpec]
			}
		};
		if (rate === \audio) {
			finalSpecs = extraSpecs ++ mergedSpecs
		}{
			finalSpecs = this.class.nilSpecs ++ mergedSpecs
		};
		cachedSpecs[proxy] = finalSpecs;
		^finalSpecs;
	}
	
	*nilSpecs { ^[extraSpecs[0]] }
}

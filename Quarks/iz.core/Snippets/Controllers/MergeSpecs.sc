/* IZ Sat 04 August 2012  9:45 AM EEST

Create array of ControlSpecs for a NodeProxy by merging specs created from the argument-default list pairs obtained by proxy.getKeysValues with any spec-specs provided in an array of form: 
[parameter1: spec1, paramereter2: spec2 ...] 
The spec-specs array is an optional part of the heading of snippets: 

//:<proxyname> [parameter1: spec1, paramereter2: spec2 ...]

{ /* proxy source ... */ }

// =========== Prototype: ============
//:

{ | amp = 0.1 | SinOsc.ar(400, 0, amp) }

//:

a = ~out1.getKeysValues;

b = [freq1: \freq];

//:

var key, value, index;
a do: { | kv |
	#key, value = kv;
	index = b indexOf: key;
	if (index.isNil) {
		c = c add: [key, (key.asSpec ?? { \bipolar.asSpec }).default_(value ? 0)]
	}{
		c = c add: [key, (b[index + 1].asSpec)]
	}
};

d = a.flop.first;

b pairsDo: { | key, val |
	if (d.includes(key).not) {
		c = c add: [key, value.asSpec]
	}
};

//: 

*/

MergeSpecs {
	var proxyArgs, snippetArgs, rate, mergedSpecs;
	classvar <>extraSpecs;		// specs for nil, vol and fadeTime, 

	*initClass {
		Class.initClassTree(Spec);
		Class.initClassTree(ControlSpec);
		extraSpecs = [['-', nil], [\vol, ControlSpec(0, 2)], [\fadeTime, ControlSpec(0, 60)]];
	}

	*new { | proxy, snippetArgs |
		^this.fromArgs(proxy.getKeysValues, snippetArgs, proxy.rate);
	}
	
	*fromArgs { | proxyArgs, snippetArgs, rate |
		^this.newCopyArgs(proxyArgs, snippetArgs, rate).mergeSpecs;
	}
	
	mergeSpecs {
		var index, spec, keys, key, value, snippetKeys, snippetVals;
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
			^extraSpecs ++ mergedSpecs
		}{
			^[['-', nil]] ++ mergedSpecs
		}
	}
}

// An Ordered Dictionary of associations or BMSpeakers (\name->index);

// items which call do expecting the list behaviour (associations rather than values only) should use super.do
 
BMInOutArray : List {

	var subArraysKeys;
	var subArrays; // a dictionary of subArrayName->[key1, key2...]
	var busObjects;
	
	*new {|size|
		^super.new(size).init;
	}
	
	*hardwareInputArray {|server, name = "Hardware In"|
		server = server.asTarget.server; // account for nil
		^BMHardwareInputsProxy.fill(server.options.numInputBusChannels, {|i| 
			(name.asString + (i+1)).asSymbol -> (server.options.numOutputBusChannels + i)
		});
	}
	
	init {
		subArrays = IdentityDictionary.new;
		subArraysKeys = Array.new; // should this be a Set?
	}
	
	// get these when we need them
	keys {
		^this.associationsCollectAs({|item| item.key }, Array);
	}
	
	values { ^this.associationsCollectAs({|item| item.value }, Array); }
	
	*privateBusBlock {|name, size, server|
		^this.new(size).addPrivateBusBlock(name, size, server);
	}
	
	addPrivateBusBlock {|name, size, server|
		var bus, block;
		bus = Bus.audio(server, size);
		busObjects = busObjects.add(bus);
		block = BMInOutArray.fill(size, {|i| (name ++ "-" ++ (i + 1)).asSymbol->(bus.index + i) });
		this.putAll(block);
		this.defineSubArray(name, block.keys);
	}
	
	// only do this if you're sure
	freeBusObjects { busObjects.do(_.free) }
	
	add { |assoc|
		var index;
		if(assoc.isValidBMInOutArrayMember.not, { 
			MethodError("Attempted to add invalid type to BMInOutArray:" + assoc, this).throw;
		}, {
			index = this.keys.indexOf(assoc.key);
			index.isNil.if({array = array.add(assoc);}, {array.put(index, assoc)});
		});
	}
	
	addFirst { |assoc|
		var index;
		if(assoc.isValidBMInOutArrayMember.not, { 
			MethodError("Attempted to add invalid type to BMInOutArray", this).throw;
		}, {
			index = this.keys.indexOf(assoc.key);
			index.isNil.if({
				array = array.addFirst(assoc);
			}, {
				MethodError("Item with key % already exists.".format(assoc.key), this);
			});
		});
	}
	
	insert { arg index, assoc; 
		if(assoc.isValidBMInOutArrayMember.not, { 
			MethodError("Attempted to add invalid type to BMInOutArray", this).throw;
		}, {
			this.keys.indexOf(assoc.key).isNil.if({
				array = array.insert(index, assoc); 
			}, {MethodError("Item with key % already exists.".format(assoc.key), this)});		});
	}

	removeAt {|key| 
		var index, val;
		index = this.keys.indexOf(key);
		index.notNil.if({
			subArrays.do({|sa| sa.remove(key)});
			val = array.removeAt(index);
		});
		^val.value
	}
	
	at {|keyOrIndex| var index;
		if(keyOrIndex.isNumber, { ^array.at(keyOrIndex).value; });
		index = this.keys.indexOf(keyOrIndex);
		index.notNil.if({^array.at(index).value}, {^nil});
	}
	
	put { arg key, value;
		var index, assoc;
		value ?? { this.removeAt(key); ^this };
		assoc = value.isBMSpeaker.if({value.name_(key)}, {key->value});
		this.add(assoc);
	}
	
	putAll { arg ... dictionaries; 
		dictionaries.do {|dict| 
			dict.keysValuesDo { arg key, value; 
				this.put(key, value) 
			}
		}
	}
	
	// iteration
	
	keysValuesDo {|function|
		super.do({|assoc, i|
			function.value(assoc.key, assoc.value, i);
		});
	} 
	
	keysDo { arg function;
		super.do({|assoc, i|
			function.value(assoc.key, i);
		});
	}
	
	associationsDo { arg function;
		super.do(function);
	}
	
	// iterate over values only
	do {|function|
		super.do({|assoc, i|
			function.value(assoc.value, i);
		});
	}
	
	collect { arg function;
		var res = this.class.new(this.size);
		this.keysValuesDo { arg key, elem; res.put(key, function.value(elem, key)) }
		^res;
	}
	
	select { arg function;
		var res = this.class.new(this.size);
		this.keysValuesDo { arg key, elem; if(function.value(elem, key)) { res.put(key, elem) } }
		^res;
	}
	
	reject { arg function;
		var res = this.class.new(this.size);
		this.keysValuesDo { arg key, elem; if(function.value(elem, key).not) 
			{ res.put(key, elem) } }
		^res;
	}
	
	associationsCollectAs { | function, class |
		var res = class.new(this.size);
		this.associationsDo {|elem, i| res.add(function.value(elem, i)) }
		^res;
	}
	
	species {^this.class } // just in case
	
	++ {|aBMInOutArray| 
		var newlist = this.species.new(this.size + aBMInOutArray.size);
		newlist = newlist.putAll(this, aBMInOutArray);
		this.subArrays.do({|key| 
			newlist.defineSubArray(key, this.getSubArrayKeys(key));
		});
		aBMInOutArray.subArrays.do({|key| 
			newlist.defineSubArray(key, aBMInOutArray.getSubArrayKeys(key));
		});
		^newlist
	}

	defineSubArray {|name, elementNames| 
		var index;
		subArrays[name] = elementNames.sect(this.keys); 
		index = subArraysKeys.indexOf(name);
		if (index.isNil) { subArraysKeys = subArraysKeys.add(name) };	}
	
	getSubArray {|name| 
		^subArrays[name].collectAs({|key| key->this[key] }, this.class); 
	}
	
	getSubArrayKeys {|name | ^subArrays[name] }
	
	removeSubArray {|name|
		subArrays[name] = nil; 
		subArraysKeys.remove(name);
	}
	
	addToSubArray {| name, element |	
		subArrays[name] = subArrays[name].add(element); 
	}
	
	removeFromSubArray {| name, element |	
		subArrays[name].remove(element); 
	}
	
	subArrays {^subArraysKeys }
	
	isBMInOutArray {^true}
	
	asBMInOutArray {^this}
	
	asUGenInput { ^this.values.asUGenInput }
	
	asControlInput { ^this.values.asControlInput }
	
	asOSCArgBundle {
		var oscarray = Array(100);		// allocate a bunch of space
		this.do { | msg | oscarray = oscarray.add(msg.asOSCArgArray) };
		^oscarray
	}

	asOSCArgArray {
		var oscarray = Array(100);		// allocate a bunch of space
		this.do { | e | oscarray = e.asOSCArgEmbeddedArray(oscarray) };
		^oscarray
	}
	
	asOSCArgEmbeddedArray { | oscarray|
		oscarray = oscarray.add($[);
		this.do{ | e | oscarray = e.asOSCArgEmbeddedArray(oscarray) };
		oscarray.add($]);
		^oscarray;
	}
	
	printItemsOn { | stream |
		var addComma = false;
		this.associationsDo { | item |
			if (stream.atLimit) { ^this };
			if (addComma) { stream.comma.space; } { addComma = true };
			item.printOn(stream);
		};
	}
	
	boundaries {
		var lowX, hiX, lowY, hiY, lowZ, hiZ, xvals, yvals, zvals;
		xvals = this.collectAs({|spkr| spkr.x }, Array);
		yvals = this.collectAs({|spkr| spkr.y }, Array);
		zvals = this.collectAs({|spkr| spkr.z }, Array);
		hiX = xvals.maxItem;
		lowX = xvals.minItem;
		hiY = yvals.maxItem;
		lowY = yvals.minItem;
		hiZ = zvals.maxItem;
		lowZ = zvals.minItem;
		^[[lowX, lowY, lowZ], [hiX, hiY, hiZ]];
	}

}

BMHardwareInputsProxy : BMInOutArray {

	name { ^"Hardware Inputs"; }
	
	gui { ^nil }
}

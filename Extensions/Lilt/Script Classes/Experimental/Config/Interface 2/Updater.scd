
//  =======================================================================
// Draft for a controller that adds keys from other controllers so that only one 
// Event is always looked up on a change message: 

Updater : Event { 
	add { | controller |
		controller.addToControllerNode(this);
	}
	addAtKey { | key, value |
		this.get(key).add(value);
	}
	get { | key |
		var found;
		found = this.at(key);
		if (found.isNil) {
			found = Set.new;
			this.put(key, found);
		};
		^found;
	}
	remove { | controller |
		var found;
		controller keysValuesDo: { | key, value |
			found = this.at(key);
			found remove: value;
			if (found.size == 0) { this.put(key, nil) }
		}	
	}
	update { | changer, key ... values |
		this.at(key) do: _.update(changer, key, *values);
	}

}

+ Collection {
	addToControllerNode { | node |
		this do: { | keyValue |
			node.addAtKey(keyValue[0], keyValue[1]);
		}
	}
}
+ Dictionary {
	addToControllerNode { | node |
		this keysValuesDo: { | key, value |
			node.addAtKey(key, value);
		}
	}
}

/*
c = ControllerNode.new;
e = (a: 1, b:2);
f = (a: { "alpha 12 2 3 4 5".postln }, b: { | ... args | "xxx".postln; args.postln });
g = (b: { "aclobde".postln }, c: { "gragrackha".postln; });
c.add(e);
c.add(f);
c.add(g);
c.remove(f);
c.remove(e);
c.remove(g);

c.add((a: 1, b:2));
c.add((a: { "alpha 12 2 3 4 5".postln }, b: { | ... args | "xxx".postln; args.postln }));
c.update("x", \b, 1, 2, 3);
*/

//  =======================================================================
// older stuff: 
/*
Intf(100).show;
Intf.show(100);
100.show
*/

/* spec scheme with overriding: 

anObject.show(specs); -> 
	if no specs are provided, get the defaults via method intfSpecs. 
	if a symbol is provided, get the default from parent. 
	if a function is provided, evaluate it with the anObject as argument to get the method for building
*/

/*
Intf : Model {
	classvar <parent;
	var <model, <specs, <intf;
	*initClass { 
		parent = (
			test: { "abc".postln; }
		);
	}
	*new { | anObject, intf |
		^this.newCopyArgs(nil, anObject).init(intf);
	}
	init { | argIntf |
		specs = ().parent = parent;
		intf = argIntf ?? { this.defaultIntf };
	}
	*show { | anObject, intf |
		this.new(anObject, intf).show;
	}
	show {
		specs use: { intf.(model) } ;
	}
}

// have to use this way if we are to inherit intf methods. Shorter than rewriting the superclass search. 
+ Object {
	defaultIntf {
		^{ | model | GUI.window.new(model.asString).front; }
	}
	show { | intf |
		Intf(this, intf).show;
	}
}
*/
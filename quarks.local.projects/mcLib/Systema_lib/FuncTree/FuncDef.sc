FuncDef {
	
	classvar <all;
	var <name, <>protoFunc;
	
	*initClass { all = IdentityDictionary.new }
	
	*new{|name, protoFunc| ^super.newCopyArgs(name, protoFunc ?? {{{}}} ).basicInit }
	*at { |name| ^all.at(name) }
	
	basicInit { all.put(name, this) }
}

+ Function {
	
	// can be used safely to replace: valueWithEnvir(evir)
	valueWithEnvirReplFirst {|envir, firstArg|
		var prototypeFrame;
		if(envir.isNil) { ^this.value(firstArg) };
		prototypeFrame = def.prototypeFrame ?? { ^this.value };
		prototypeFrame = prototypeFrame.copy;
		def.argNames.do { |name,i|
			var val = envir[name];
			val !? { prototypeFrame[i] = val };
		};
		firstArg !? { prototypeFrame = prototypeFrame.putFirst(firstArg) };
		^this.valueArray(prototypeFrame)
	}

}

/*
t = FuncTree(\myTree)
(
FuncDef(\test, 
		{|node, deltaR| deltaR.postln; {|e, radius| e[\radius] = (radius ? 0) + deltaR } }
	)
)	

FuncDef.at(\test).protoFunc
c = FuncHolder(\test, (deltaR: 10), t.root);
c.func.isClosed
c.func.def.sourceCode
e = (radius: 2)
c.func.valueWithEnvirReplFirst(e, e)

3.do{|i| FuncHolder(\test, (deltaR: 80 * i), t.root) }

t.envirDoTree((hey: 3))

this is now history:

valueWithEnvir
performWithEnvir(selector, envir)
performKeyValuePairs(selector, pairs)

forBy (1, 9, 2) { arg i; i.postln };


// DrawDef --> calcFunc will be shortcut to protoFunc


a = IdentityDictionary.new(parent: (pro: 8, par: 10))
a = IdentityDictionary.new(proto: (pro: 5), parent: (pro: 8, par: 10))
a.keys
a.at(\pro)
a.at(\par)
a.put(\pro, 0)
a.at(\pro)
a.put(\par, 20)
a.at(\par)

a.clear

b = a.copy
b[\pro]
b[\par]

a = Event.new(parent: (pro: 8, par: 10))
a = Event.new(proto: (pro: 5), parent: (pro: 8, par: 10))

a.keys
a.at(\pro)
a.at(\par)
a.put(\pro, 0)
a.at(\pro)
a.put(\par, 20)
a.at(\par)

a.clear



c = ().play
c.proto
c.parent
c.keys
c.parent.keys.do{|key| key.postln}
c.parent.keys.select{|key| key == \value}
c.class.methods.do{|name| name.postln}
c.proto
c.value // --> returns this envir !!!

(
f = {|e, val| [e, val].postln };
e = (val: 10);
e.use{|a, b| [a, b].postln; f.value };

f.valueWithEnvir(e.put(\e, e)); // freaks out recursively !!!
)

(
f = {|envir, val| [envir, val].postln };
e = (val: 10);
e.use{|a, b| [a, b].postln; f.value };

f.valueWithEnvir(e.put(\envir, e));  // freaks out recursively, too !!!
)

(
f = {|envir, val| [envir, val].postln };
e = (envir: e, val: 10);
e.use{|a, b| [a, b].postln; f.value };

f.valueWithEnvir(e);  // works !!! why !!
)

(
f = {|envir, val| [envir, val].postln };
e = (val: 10);
e.use{|a, b| [a, b].postln; f.value };
e = e.put(\envir, e); // freaks out here
f.valueWithEnvir(e);
)

evirs can't contain themselves !! If provided as proto or parent -> crash

*** solution *** -> newMethod to eval a Func: valueWithEnvirFirstArg

(
f = {|envir, val| [envir, val].postln };
e = (val: 10);
f.valueWithEnvirReplFirst(e, e);
)

Function
f.def.prototypeFrame
f.def


*/
DrawDef : FuncDef {
	// var <name, <>protoFunc;
	classvar <all;
	var <>realm, <>protoDrawFunc, <>protoFreeFunc;
	
	*initClass { all = IdentityDictionary.new }
	*new{|name, realm, protoCalcFunc, protoDrawFunc, protoFreeFunc|
		^super.newCopyArgs(name ? \drawDef, protoCalcFunc, realm, protoDrawFunc, protoFreeFunc).init
	}
	*at { |name| 
		var def = all.at(name);
		def ?? { ("DrawDef not found:" + name).warn };
		^def
	}
init { all.put(name, this) }
	protoCalcFunc { ^protoFunc }
	protoCalcFunc_ {|val| protoFunc = val; ^val}
}


/*
d = ScaledDrawServer.new(rate: 1).active_(true);
(
a = DrawDef(\test, 
	{|node, deltaR| deltaR.postln; {|e, radius| e[\radius] = (radius ? 0) + deltaR } },
	{|node, offPoint| offPoint.postln;  {|e, point, radius| e.postln;
		Pen.fillColor = Color.blue;
			Pen.strokeColor = Color.red;
			Pen.addArc((point ?? {(30.rand)@(30.rand)}) + offPoint, radius, 0.0, 2pi);
			Pen.stroke;
		 } } );
)	
DrawDef.at(\test).protoDrawFunc;
*/
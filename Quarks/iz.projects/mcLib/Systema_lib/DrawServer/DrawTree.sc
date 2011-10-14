DrawTree : FuncTree {

	envirRealmDoTree{|e, king| // l'etat c'est moi: king provides the realm
		e = e ?? {()};
		this.doEnvirDoTree(root, {|node| 
			node.calcFunc !? {
				if (node.respondsTo(\realm).not) { node.calcFunc.valueWithEnvirReplFirst(e, e) 
				}{ king.doInRealm(node.realm, { node.calcFunc.valueWithEnvirReplFirst(e, e) }) } } 
		});
		this.doEnvirDoTree(root, {|node| 
			node.func !? {
				if (node.respondsTo(\realm).not) { node.func.valueWithEnvirReplFirst(e, e) 
				}{ king.doInRealm(node.realm, { node.func.valueWithEnvirReplFirst(e, e) }) } } 
		});
		^e
	}
}


DrawFuncGroup : FuncGroup {}
DrawDebugGroup : FuncGroup {}

DrawFunc : FuncHolder {
	var <>realm, <>calcFunc, <dependencyFuncs;
var <name; // for debug
	init {
		var def = DrawDef.at(defName);
		dependencyFuncs = IdentityDictionary.new;
		def !? { 
name = defName.asString +"("++ nodeID ++")";
			realm = def.realm;
			def.protoFunc !? {
				calcFunc = def.protoFunc.valueWithEnvirReplFirst(envir, this) };
			def.protoDrawFunc !? {
				func = def.protoDrawFunc.valueWithEnvirReplFirst(envir, this) };
			def.protoFreeFunc !? {
				freeFunc = def.protoFreeFunc.valueWithEnvirReplFirst(envir, this) }
		};
		// dependencyFuncs = IdentityDictionary.new; // BUG ?!? why is the var not set here but above ?
	}
	free { //this.logln("free DrawFunc");
		freeFunc !? { freeFunc.valueWithEnvirReplFirst(
			tree.server.drawEnvir, tree.server.drawEnvir) };
		dependencyFuncs.keysValuesDo{|func, obj| obj.removeDependant(func) }; 
		dependencyFuncs.clear;
		group = nil;
	}
	addDependantFuncTo{|obj, func|
		dependencyFuncs.put(func, obj); 
		obj.addDependant(func)
	}
	removeDependantFunc {|func| 
		var obj;
		dependencyFuncs[func] !? { obj = dependencyFuncs.removeAt(func) };
		obj !? { obj.removeDependant(func) }
	}
}
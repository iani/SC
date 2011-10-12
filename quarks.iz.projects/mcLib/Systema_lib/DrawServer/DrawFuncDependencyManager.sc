DrawFuncDependencyManager {
	
	classvar <models, <nodes;
	 
	var <node, <model, <>updateAction;
	
	*initClass {
		models = IdentityDictionary.new;
	}
	*new{|node, model, updateAction|
		^super.newCopyArgs(node, model, updateAction).init;
	}
	*update {|model, what, args| this.logln("update:" + [model, what, args]);
		models[model].do{_.updateAction(what, *args)};
	}
	*removeNode{|node|
		var obj = nodes.remove(node);
		obj !? { obj.remove };
this.logln("removed:" + obj);
	}
	
	init {
		if (models[model].isNil) {Êmodels[model] = List[this] }Ê{ models[model].add(this) };
		model.postln.addDependant(DrawFuncDependencyManager);
	}
	remove { models[model].remove(this) }
}

DrawFuncNotificationCenter {

	
}

/*
DrawFuncDependencyManager(DrawFunc.new, SYS.hypoLydianSet, {"hi".postln})

DrawFuncDependencyManager.update

SYS.hypoLydianSet.destroy
Systema.hypoLydianSet.destroy
SYS.agmUni.destroy

Systema.dependants
Systema.addDependant(this)
*/
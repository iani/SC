FuncNode {
	classvar <addActions;
	
	var <>nodeID, <>tree, <>group;
//var <>isPlaying = false, <>isRunning = false;

	*initClass {
		addActions = (
			addToHead: 0,
			addToTail: 1,
			addBefore: 2,
			addAfter: 3,
			addReplace: 4,
			h: 0,
			t: 1,
				// valid action numbers should stay the same
			0: 0, 1: 1, 2: 2, 3: 3, 4: 4
		);
	}
	
	*basicNew { arg tree, nodeID;
		tree = tree ? FuncTree.default;
		^super.newCopyArgs(nodeID ?? { tree.nextFuncNodeID }, tree)
	}
	*actionNumberFor { |addAction = (\addToHead)| ^addActions[addAction] }
	
	free { //this.logln("free");
		group = nil;
		//isPlaying = false;
		//isRunning = false;
	}
	remove { // this.logln("remove Node:" + this); //mc
		//tree.remove(this).free 
		tree.freeAll(tree.remove(this));
	}
	moveBefore { arg aNode;
		tree.remove(this);
		group = aNode.group;
		tree.add(this, 2, aNode.nodeID) 
	}
	moveAfter { arg aNode;
		tree.remove(this);
		group = aNode.group;
		tree.add(this, 3, aNode.nodeID) 
	}
	moveToHead { arg aGroup;
		if (aGroup.isKindOf(FuncHolder)) { aGroup = aGroup.group }; //mc
		(aGroup ? tree.defaultFuncGroup).moveNodeToHead(this);
	}
	moveToTail { arg aGroup;
		if (aGroup.isKindOf(FuncHolder)) { aGroup = aGroup.group }; //mc
		(aGroup ? tree.defaultFuncGroup).moveNodeToTail(this);
	}
	
	hash {  ^tree.hash bitXor: nodeID.hash	}

	== { arg aNode;
		^aNode respondsTo: #[\nodeID, \tree]
			and: { aNode.nodeID == nodeID and: { aNode.tree === tree }}
	}
	printOn { arg stream; stream << this.class.name << "(" << nodeID <<")" }
}


FuncGroup : FuncNode {

var <>nodes; // used by FuncTree to store all nodes of this group externally; bit odd but maybe best way

	*new { arg target, addAction=\addToTail;
		var group, tree, addNum, inTarget;
		inTarget = target.asFuncTarget(this); //was  target.asFuncTarget;
		tree = inTarget.tree;
		group = this.basicNew(tree);
		addNum = addActions[addAction];
		
		//if((addNum < 2), { group.group = inTarget; }, { group.group = inTarget.group; }); //why?
		group.group = if (inTarget.isKindOf(FuncHolder)) {inTarget.group} {inTarget}; //mc

		tree.add(group, addNum, inTarget.nodeID);
		^group
	}
	*after { arg aNode;    ^this.new(aNode, \addAfter) }
	*before { arg aNode; 	^this.new(aNode, \addBefore) }
	*head { arg aGroup; 	^this.new(aGroup, \addToHead) }
	*tail { arg aGroup; 	^this.new(aGroup, \addToTail) }
	*replace { arg nodeToReplace; ^this.new(nodeToReplace, \addReplace) }

	*basicNew { arg tree, nodeID;
		tree = tree ? FuncTree.default;
		^super.newCopyArgs(nodeID ?? { tree.nextFuncNodeID }, tree).nodes_(List.new)
	}

	// move Nodes to this group
	moveNodeToHead { arg aNode;
		tree.remove(aNode);
		aNode.group = this;
		tree.add(aNode, 0, this.nodeID) 
	}
	moveNodeToTail { arg aNode;
		tree.remove(aNode);
		aNode.group = this;
		tree.add(aNode, 1, this.nodeID)
	}

	freeAll {
		nodes.do{|node| node.free};
		this.free;
	}
	
}

FuncHolder : FuncNode {
	
	var <>defName, <>envir, <>func, <>freeFunc; //func could easily be replaced by a FunctionList...		
	*new { arg defName, envir, target, addAction=\addToTail;
		var holder, tree, addNum, inTarget;
		inTarget = target.asFuncTarget(this); //mc was  target.asFuncTarget;
		tree = inTarget.tree;
		addNum = addActions[addAction];
		holder = this.basicNew(defName, envir, tree);
		
		//if((addNum < 2), { holder.group = inTarget; }, { holder.group = inTarget.group; }); // why?
		holder.group = if (inTarget.isKindOf(FuncHolder)) {inTarget.group} {inTarget}; // mc
				
		tree.add(holder, addNum, inTarget.nodeID);
		^holder.init;
	}
	*basicNew { arg defName, envir, tree, nodeID;
		^super.basicNew(tree, nodeID).defName_(defName).envir_(envir);
	}
	
	*after { arg aNode, defName, envir;
		^this.new(defName, envir, aNode, \addAfter);
	}
	*before {  arg aNode, defName, envir;
		^this.new(defName, envir, aNode, \addBefore);
	}
	*head { arg aGroup, defName, envir;
		^this.new(defName, envir, aGroup, \addToHead);
	}
	*tail { arg aGroup, defName, envir;
		^this.new(defName, envir, aGroup, \addToTail);
	}
	*replace { arg nodeToReplace, defName, envir;
		^this.new(defName, envir, nodeToReplace, \addReplace)
	}
	
	init { this.logln("i was exe first");
		func = if (FuncDef.at(defName).isNil) { {} }{ 
			FuncDef.at(defName).protoFunc.valueWithEnvirReplFirst(envir, this) };
	}
	free { // this.logln("free:" + freeFunc);
		freeFunc !? { freeFunc.valueWithEnvirReplFirst(envir, this) };
		group = nil;
	}	
	printOn { arg stream; stream << this.class.name << "(" <<< defName << " : " << nodeID <<")" }
}

FuncRoot : FuncGroup {
	classvar <roots;
	
	*initClass {  roots = IdentityDictionary.new; }
	
	*new { arg tree;
		tree = tree ? FuncTree.default;
		^(roots.at(tree.name) ?? {
			^super.basicNew(tree, 0).rninit
		})
	}
	
	rninit {
		roots.put(tree.name, this);
		//isPlaying = isRunning = true;
		group = this; // self
	}
	
	moveBefore { "moveBefore has no effect on RootNode".warn; }
	moveAfter { "moveAfter has no effect on RootNode".warn; }
	moveToHead { "moveToHead has no effect on RootNode".warn; }
	moveToTail{ "moveToTail has no effect on RootNode".warn; }
}

+FuncTree {
	//asFuncTarget { ^FuncGroup.basicNew(this, 1) }
	asFuncTarget { ^root }
	asNodeID { ^0 }
}

+FuncNode {
	asFuncTarget { ^this }
	asNodeID { ^nodeID }
}

+Nil {
	asFuncTarget { ^FuncGroup.basicNew(FuncTree.default, 1) }
	asNodeID { ^this }
}

+Integer {
	asFuncTarget { ^FuncGroup.basicNew(FuncTree.default, this) }
	asNodeID { ^this }
}
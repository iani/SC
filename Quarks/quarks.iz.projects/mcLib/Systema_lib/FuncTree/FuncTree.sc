FuncTree {
	classvar <all, uniqueSuffix="_V";
	classvar <>default;
	
	var  <name, <server, allocator, <root, <defaultFuncGroup, <tail;
	
	*initClass { 
		all = IdentityDictionary.new;
		StartUp.add({ default = FuncTree.new(\default) });
	}
	*new{|name, server| ^super.newCopyArgs(this.uniqueCopyNameSuffix(name ? \funcTree), server).init }
	
	*uniqueCopyName{|sym| //this.logln("sym:" + sym);
		if (all.keys.includes(sym)) { ^this.uniqueCopyName((sym.asString++$_).asSymbol) }{ ^sym }
	}
	*uniqueCopyNameSuffix{|sym| 
		var str, i;
		if (all.keys.includes(sym).not) { ^sym }{
			str = sym.asString;
			i = str.findBackwards(uniqueSuffix);
				if (i.notNil) { 
					i=i+uniqueSuffix.size; 
					^this.uniqueCopyNameSuffix((str.keep(i) ++ (str.drop(i).asInt+1)).asSymbol)
				}{ 	^this.uniqueCopyNameSuffix((str ++ uniqueSuffix ++ 1).asSymbol) }
		}
	}
	
	init{
		allocator = NodeIDAllocator.new;
		root = FuncRoot.new(this);
		defaultFuncGroup = FuncGroup(root, \addToTail);
		tail = FuncGroup(root, \addToTail);
	}
	nextFuncNodeID { ^allocator.alloc }
	
	remove{|item|
		var b = this.detectBranch(item.group.nodeID); //this.logln("remove:" + [item, item.group, b]);
		^b.nodes.remove(item);
	}
	add{|item, addNum, targetID|
		var b = this.detectBranch(item.group.nodeID) ?? { root.nodes.add(item.group); item.group };
//this.logln("found:" + [b.nodeID, b], lfB:2);
//this.logln("add:" + [item, addNum, targetID]);
		addNum.switch(
			0, { b.nodes.addFirst(item) }
			,1, { b.nodes.add(item) }
			,2, { b.nodes.insert(b.nodes.collect{|i| i.nodeID}.indexOf(targetID), item) }
			,3, { b.nodes.insert(b.nodes.collect{|i| i.nodeID}.indexOf(targetID)+1, item) }
			,4, { this.replace(b, item, targetID) }
			, { "addNum not defined".warn });
//this.postTree; "".postln;
	}
	replace{|b, item, targetID|
		var index = b.nodes.collect{|i| i.nodeID}.indexOf(targetID);
		this.freeAll( b.nodes[index]); 
		b.nodes.put(index, item) 
	}
	freeAll{|item|
		if (item.isKindOf(FuncGroup)) { this.doBranchDo(item, {|b| b.freeAll}) } { item.free }
	}
	
	detectBranch{|id|
		this.branchDo{|branch| if(branch.nodeID == id) {^branch} }
		^nil
	}
	detectItemBranch{|id|
		this.treeDo{|branch, item| if(item.nodeID == id) {^branch} }
		^nil
	}
	
	branchDo{|func| this.doBranchDo(root, func) }
	doBranchDo {|branch, func|
		func.value(branch);
		branch.nodes.do{|item| if (item.isKindOf(FuncGroup)) { this.doBranchDo(item, func) } }
	}
	
	treeDo {|func| this.doTreeDo(root, func) }
	doTreeDo{|branch, func|
		branch.nodes.do{|item| if (item.isKindOf(FuncGroup)) {
			this.doTreeDo(item, func) }{ func.value(branch,item) } }
	}
	
	envirDoTree{|e|
		e = e ?? {()};
		this.doEnvirDoTree(root, {|node| node.func !? { node.func.valueWithEnvirReplFirst(e, e) } });
		^e
	}
	doEnvirDoTree{|branch, func|
		branch.nodes.do{|item|  if (item.isKindOf(FuncGroup)) { 
			this.doEnvirDoTree(item, func) }{ func.value(item) } }
	}
	
	envirRealmDoTree{|e, king| // l'etat c'est moi: king provides the realm
		e = e ?? {()};
		this.doEnvirDoTree(root, {|node| 
			node.func !? {
				if (node.respondsTo(\realm).not) { node.func.valueWithEnvirReplFirst(e, e) 
				}{ king.doInRealm(node.realm, { node.func.valueWithEnvirReplFirst(e, e) }) } } 
		});
		^e
	}
	
	postTree { this.postTreeDo(root)	}
	postTreeDo{|branch|
		var branches = List.new;
		("\n" ++ branch.nodeID ++ ":" + branch).postln;
		branch.nodes.do{|item| if (item.isKindOf(FuncGroup)) { branches.add(item) };
			this.postItem(item) };
		branches.do{|b| this.postTreeDo(b) };
		//"\n".postln;
	}
	postItem {|item|
		(" -> " ++ [item.nodeID, item]).post; 
	}
}

/*

a = FuncHolder.new
FuncTree.default.root

b = FuncHolder(nil, nil, a, 0) //head
c = FuncHolder(nil, nil, a, 1) //tail
d = FuncHolder(nil, nil, a, 2) //before
e = FuncHolder(nil, nil, a, 3) //after

e.moveToHead(a.group) // needed to be a group 
e.moveToTail(a.group) // needed to ba a group
e.moveToHead(a) // added mc
e.moveToTail(a) //added mc
e.moveBefore(a)
e.moveAfter(a)

f = FuncGroup.new
g = FuncGroup(e, 4) // replace e by group g

g.moveNodeToTail(a); // take a to tail of group g
g.moveNodeToHead(b); // take b to head of group g
// above should not be groups:
g.group
f.group
g.moveNodeToHead(f); // take f to head of group g // works 
// but if moved group contains the target ...
g.group
a.group
//g.moveToHead(a.group) // crash!! naturally!!
// things are not fail save! -> be alert what you do to the tree -> don't serrate the branch you sit on!
// g.moveBefore(a.group) // breaks
// g.moveBefore(a) // crashes!!


h = FuncGroup.new
i = FuncGroup(b, 0)
j = FuncGroup(b, 0)


*/


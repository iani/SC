/* iz 080819 
My own implementation of a Tree structure containing branches and leaves.
Note: I cannot get MultiLevelIdentityDictionary or Library to work sensibly for this purpose, so I redo the whole thing from scratch here. 
*/


Tree {
	var <branches, <leaves;
	*new { | path |
		^this.newCopyArgs(path).init;
	}
	init {
		branches = IdentityDictionary.new;
	}
	put { | ... path |
		var item;
		item = path.pop;
		this.putPath(path, item);
	}
	putPath { | path, item |
		this.branchAt(path).addItem(item);
	}
	branchAt { | path |
		var branch;
		branch = this;
		path do: { | p |
			branch = branch.getBranch(p)
		};
		^branch;
	}
	getBranch { | key |
		var branch;
		branch = branches.at(key);
		if (branch.isNil) {
			branch = this.class.new;
			branches.put(key, branch);
		};
		^branch;
	}
	addItem { | item |
		leaves = leaves.add(item);
	}
	at { | ... path |
		^this.atPath(path)
	}
	atPath { | path |
		var item;
		item = this;
		path do: { | p |
			item = item.branches.at(p);
			if (item.isNil) { ^nil }
		};
		^item.leaves;
	}
	prettyPrint {
		leaves.postln;
		branches keysValuesDo: { | key, value |
			key.postln;
			value.prettyPrint;
		}
	}
}
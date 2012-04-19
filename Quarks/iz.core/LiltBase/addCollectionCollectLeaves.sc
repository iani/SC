

+ Collection {
	collectLeaves { | func |
		// collect descending recursively into subcollections, applying func to leaves
		^this collect: { | branchOrLeaf |
			if (branchOrLeaf isKindOf: Collection) {
				branchOrLeaf collectLeaves: func
			}{
				func.(branchOrLeaf)
			}
		}
	}
}
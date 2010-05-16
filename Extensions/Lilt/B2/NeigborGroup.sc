/* IZ 080811

This file is OBSOLETE! See: Blob.sc

Group (partition) a set of elements into groups, where each element a in a group has at least one neighbor b in the same group so that the distance between a and b is smaller than a given max_distance.   
*/

NeighborGroup {
	var <max_distance = 1;	// maximum distance between pairs of closest neighbors in a group;
	var <elements;			// elements of the group in one array
	var <groups;			// elements of the group grouped by max_distance
	var <group_dict;		// dictionary holding the group where each element belongs to, for adding its neighbors

	*new { | max_distance, elements |
		^this.newCopyArgs(max_distance, elements).makeGroups;
	}

	makeGroups {
		var size;
		var origin_group, target_group;
		size = elements.size;
//		format("size of elements for grouping: % ----------   ", size).post;
		group_dict = IdentityDictionary.new;
		elements do: { | origin, index |
			origin_group = this.getGroup(origin);
// rewrite the algorithm below to avoid copyRange! 
/*
			if (index < size) {
				(index .. size) do: {
					
				}
			}
		

*/				// do not use copyRange. see above! 
			elements.copyRange(index + 1, size) do: { | target |
				if (abs(target - origin) <= max_distance) {
					target_group = this.getGroup(target);
					if (origin_group.size > target_group.size) {
						this.joinGroups(target_group, origin_group)
					}{
						this.joinGroups(origin_group, target_group)			
					}
				}
			}
		};
		groups = group_dict.values.asSet;
//		format(" number of groups: %, sizes of groups: %", groups.size, groups.asArray collect: _.size).postln;
	}

	addToGroup { | element, group |
		group.add(element);
		group_dict[element] = group;		
	}

	getGroup { | element |
		var group;
		group = group_dict[element];
		if (group.isNil) {
			group = Set.new;
			this.addToGroup(element, group)
		};
		^group;
	}

	joinGroups { | from_group, to_group |
		from_group do: { | element |
			from_group.remove(element);
			this.addToGroup(element, to_group);
		}
	}	
}

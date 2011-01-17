/* IZ 2011 01 17
Enable nesting of multiple Psend phrase patterns in a Posc. 

*/


BeatStack {
	var <beatCounters;
	
	push { | name |
		beatCounters = beatCounters add: BeatCounter(name);
	}
	
	pop {
		beatCounters.pop;	
	}
	
	broadcast { | destList |
		beatCounters do: _.broadcast(destList);
	}
}

// Used by BeatStack above
BeatCounter {
	var <name, <count = 0;
	
	*new { | name |
		^this.newCopyArgs(name.asString);	
	}
	
	broadcast { | destList |
		count = count + 1;
		destList do: { | dest | dest.sendMsg(name, count) };
	}
}
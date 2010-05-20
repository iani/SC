/* IZ 100520

Creating patterns that will send the structure of the fibonacci-tree 
via OSC using automatically generated messages and arguments that reflect the structure of the tree. 

===== Structure of the tree and its variants

There are four variants: 


===== Message format: 

var start_message = '/start'; 	// message marking start of branch
var end_message = '/end';		// message marking end of branch
var leaf_message = '/leaf';		// message marking leaf


The message structure is as follows: 

| message part 1           | message part 2          | message part 3 | argument 1       | argument 2        | argument 3              |
|--------------------------+-------------------------+----------------+------------------+-------------------+-------------------------|
| /start : Start of branch | _<o>*A_ / _<o>B_ branch A or B | <num>: level   | total beat count | branch beat count | branch occurrence count |
| /end : End of branch     | _<o>A_ / _<o>B_ branch A or B | <num>: level   | total beat count | branch beat count | branch occurrence count |
| /leaf : Leaf             | _<o>A_ / _<o>B_ branch A or B | <num>: level   | total beat count | leaf value        |                         |


(*) Where <o> is the direction of the generation of the tree and is as follows: 

1. <o> is "a" if the order of the numeric values of the leaves of the tree is ascending, 
	as produced by this version of the algorithm: 

(
10 do: { | i | Post << { | n = 1, prev = 1, current = 1 |
	var next;
	n do: {
		next = [current, prev + 1];
		prev = current + 1;
		current = next;
	};
	current;
}.(i).asCompileString << "\n\n";  }
)

d. <o> is "d" if the order of the numeric values of the leaves of the tree is ascending, 
	as produced by this version of the algorithm: 

(
10 do: { | i | Post << { | n = 1, prev = 1, current = 1 |
	var next;
	n do: {
		next = [prev + 1, current];
		prev = current + 1;
		current = next;
	};
	current;
}.(i).asCompileString << "\n\n";  }
)

// Note: maybe this version is more correct: 

(
10 do: { | i | Post << { | n = 1, prev = 1, current = 1 |
	var next;
	n do: {
		next = [prev, current + 1];
		prev = current;
		current = next;
	};
	current;
}.(i).asCompileString << "\n\n";  }
)

(
10 do: { | i | Post << { | n = 1, prev = 1, current = 1 |
	var next;
	n do: {
		next = [current + 1, prev];
		prev = current;
		current = next;
	};
	current;
}.(i).asCompileString << "\n\n";  }
)




Examples: 

/start_A_0 0, 0, 0 : Beginning of the first branch at level 1, first beat of piece, first beat of branch, first branch occurrence

*/

FibPat {
	var <iterations = 3;
	var <ascendingTree;	// An array holding the entire structure of the generated fibonacci tree in ascending order
	var <descendingTree;	// An array holding the entire structure of the generated fibonacci tree in descending order

	*new { | iterations = 3 |
		^this.newCopyArgs(iterations).init;
	}

	init {
		ascendingTree = this.makeAscendingTree;
		descendingTree = this.makeDescendingTree;
	}
	
	makeAscendingTree {
		^{ | n = 1, prev = 1, current = 1 |
			var next;
			n do: {
				next = [current, prev + 1];
				prev = current + 1;
				current = next;
			};
			current;
		}.(iterations)
	}

	makeDescendingTree {
		^{ | n = 1, prev = 1, current = 1 |
			var next;
			n do: {
				next = [prev + 1, current];
				prev = current + 1;
				current = next;
			};
			current;
		}.(iterations)		
	}


}
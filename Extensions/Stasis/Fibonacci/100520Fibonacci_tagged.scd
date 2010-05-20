/* IZ 100520

Fibonacci structures 2: Adding tagging of the subgroups. 


*/

// starting with my preferred version of the fib generator: 

(
~fib1inv = { | n = 1, prev = 1, current = 1 |
	var next;	
	n do: {
		next = [current, prev + 1];
		prev = current + 1;
		current = next;
	};
	current;
};
)

10 do: { | i | Post << ~fib1inv.(i).asCompileString << "\n\n";  }


// Now traverse the tree recursively down the branches, 
// and create labels for each branch:

(
~trav = { | tree, level = 0 |
	if (tree.size == 0) {
		tree
	}{
		[	
			format("level %-A", level), thisFunction.(tree[0], level + 1),
			format("level %-B", level), thisFunction.(tree[1], level + 1)
		]
	}
}
)

~trav.(~fib1inv.(5)).asCompileString;


// Trying this as routine - on the way of making it a pattern
(
~travrout = { | tree, level = 0 |
	if (tree.size == 0) {
		tree.postln;
		this.yield;
	}{
		[	
			format("level %-A", level).postln, thisFunction.(tree[0], level + 1),
			format("level %-B", level).postln, thisFunction.(tree[1], level + 1)
		]
	}
}
)

r = { ~travrout.(~fib1inv.(5)) }.fork;

while { r.next.notNil } { " ok: ".postln };

p = Prout({ ~travrout.(~fib1inv.(5)) });

q = p.asStream;

q.next;

// Making a pattern that also marks the beginning and end of each fib group section. 
(
~travrout2 = { | tree, level = 0 |
	if (tree.size == 0) {
		tree.postln;
		this.yield;
	}{
		[	
			format("START level %-A", level).postln, thisFunction.(tree[0], level + 1), format("END level %-A", level).postln,
			format("START level %-B", level).postln, thisFunction.(tree[1], level + 1), format("END level %-B", level).postln 
		]
	}
}
)

r = { ~travrout2.(~fib1inv.(5)) }.fork;

while { r.next.notNil } { " ok: ".postln };

p = Prout({ ~travrout2.(~fib1inv.(5)) });

q = p.asStream;

q.next;
/* IZ 100520

Fibonacci structures 3: Creating patterns that will send the structure of the fibonacci-tree 
via OSC using automatically generated messages and arguments that reflect the structure of the tree. 

100521: This is an older draft. See new format in FibonacciPatterns.html

var start_message = '/start'; 	// message marking start of branch
var end_message = '/end';		// message marking end of branch
var leaf_message = '/leaf';		// message marking leaf


The message structure is as follows: 

| message part 1           | message part 2          | message part 3 | argument 1       | argument 2        | argument 3              |
|--------------------------+-------------------------+----------------+------------------+-------------------+-------------------------|
| /start : Start of branch | _A_ / _B_ branch A or B | <num>: level   | total beat count | branch beat count | branch occurrence count |
| /end : End of branch     | _A_ / _B_ branch A or B | <num>: level   | total beat count | branch beat count | branch occurrence count |
| /leaf : Leaf             | _A_ / _B_ branch A or B | <num>: level   | total beat count | leaf value        |                         |



Examples: 

/start_A_0 0, 0, 0 : Beginning of the first branch at level 1, first beat of piece, first beat of branch, first branch occurrence


THE REST OF THIS WORK WILL BE DONE IN A CLASS. 

Still some tries here: 

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


// Making a pattern that also marks the beginning and end of each fib group section. 
(
~count = 0;
~travrout2 = { | tree, level = 0, controller |
	// controller = controller; 
	if (tree.size == 0) {
		format("and the count is: %", ~count = ~count + 1).postln;
		tree.postln;
		this.yield;
	}{
		[	
//		format("and the count is: %", ~count = ~count + 1).postln;
			format("/start_A_%, %, %", level, ~count, tree.flat.size).postln, 
				thisFunction.(tree[0], level + 1), format("/end_A_%", level).postln,
//		format("and the count is: %", ~count = ~count + 1).postln;
			format("/start_B_%", level).postln, thisFunction.(tree[1], level + 1), format("/end_B_%", level).postln 
		]
	}
}
)

r = { ~travrout2.(~fib1inv.(6)) }.fork;>

while { r.next.notNil } { " ok: ".postln };>

p = Prout({ ~travrout2.(~fib1inv.(5)) });

q = p.asStream;

q.next;


// ========================
// 100521 A simpler way is just to append the branch mark "A" or "B" at each recursive call of the function: 

(
~fibA1 = { | n = 1, prev = 1, current = 1 |
	var next;
	n do: { // ascending, smaller branches first
		next = [prev, current + 1];
		prev = current;
		current = next;
	};
	current;
}
)

(
~travrout3 = { | tree, label = "" |
	if (tree.size > 0) { format("start %: %", label, tree.flat.size).postln; };
	if (tree.size == 0) {
		this.yield;
		format("% %", tree, label).postln;
	}{
		thisFunction.(tree[0], label ++ "A");
		thisFunction.(tree[1], label ++ "B");		 
	};
	if (tree.size > 0) { format("end %", label).postln; };
};
)

r = { ~travrout3.(~fibA1.(6)) }.fork;
while { r.next.notNil } { '-' };









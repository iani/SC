/* IZ 100520

Fibonacci structures. 

Several versions, incrementing the numbers of the groups in different ways

*/

// Third version. I prefer it because the first number it produces is equal to the number of iterations: 

(
~fib1 = { | n = 1, prev, current |
	var next;
	prev = prev ? [1];
	current = current ? [2, 3];
	
	n do: {
		next = [prev + 1, current];
		prev = current + 1;
		current = next;
	};
	current;
};
)

10 do: { | i | Post << ~fib1.(i, 1, 1).asCompileString << "\n\n";  }

// inverse of the third version

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


// Second version
(
~fib = { | n = 1, prev, current |
	var next;
	prev = prev ? [1];
	current = current ? [2, 3];
	
	n do: {
		next = [prev, current];
		prev = current + 1;
		current = next;
	};
	current;
};
)

~fib.(1);

~fib.(2);

~fib.(3);

~fib.(3).flatten(2)

~fib.(4).flatten(2)

~fib.(5).flatten(2)

~fib.(5) collect: { | i | i.flat.size }

~fib.(6) collect: { | i | i.flat.size }

~fib.(7) collect: { | i | i.flat.size }

~fib.(5);

~fib.(5) collect: { | i | i.flat.size };

~fib.(10).flat.size;

{ | i | ~fib.(i).asArray.flat.size } ! 20

~fib.(15).asCompileString;

10 do: { | i | Post << ~fib.(i, 1, 1).asCompileString << "\n\n";  }




// ----- First version (Does not increment the numbers of the 2 initial sets

(
~fib0 = { | n = 1, prev, current |
	var next;
	prev = prev ? [1];
	current = current ? [2, 3];
	
	n do: {
		next = [prev, current];
		prev = current;
		current = next;
	};
	current;
};
)











// Genetic drift

// Central idea: select at random n-times from a population of size n. 
(
f = { | pop |
	pop.dup.flat.scramble[0..(pop.size - 1)]	
};
)

// try it with a random array of 1 and 0. 
f.(Array.rand(10, 0, 1));

// Explanation of the above: 
// This is a condensed way of selecting n times from the same array. A bit of a trick. 
f = { | pop | 	// pop is the population. It is an array of elements. 
	pop.dup		// make a copy of the population, now you have twice as many of each individual
	.flat		// dup gives you a nested array: [[], []]. We need to flatten it to get again an array: [ .... ]
	.scramble		// randomize the order of elements in the array. "Scramble them" 
	[0..(pop.size-1)]	// take the first n elements, that is half of the "long" array.					// this is now the same length as the original array.  
};

// Note: The more straightforward way of doing this would be: 
f = { | pop |
	var newPop;
	var size = pop.size;
	size do: { newPop = newPop add: pop[size.rand] };
	newPop;
}

////// Iterate the creation of random populations it times on a population pop: 
(
g = { | pop, it |
	var size;
	size = pop.size - 1;
	it do: { pop = pop.dup.flat.scramble[0..size] }; // iterate f it times. (see f above)
	pop;
};
)

g.(Array.rand(100, 0, 1), 4);

////// Iterate the creation of random populations it times on a population pop, 
// UNTIL (!!!!!) the population contains either only 1 or only 0.  
// Return the number of iterations that were needed to reach the final state of 1 or 0. 

(
i = { | pop |
	var size, it = 0, sum, sizeminus1;
	size = pop.size;
	sizeminus1 = size - 1;
	// While the population does not consist of all 0es or all 1s ...
	while { // (Note: pop.sum calculates the sum of all elements in pop)
		((sum = pop.sum) != 0) and: { (sum != size) }	
	}{	
	// ... continue to permute the population and select randomly 1 or 0: 
		pop = pop.dup.flat.scramble[0..sizeminus1];
		it = it + 1; // at each repetition, increment the iteration counterit. 
	};
	it;	// return the number of iterations, as counted.
};
)

i.([0, 0, 0, 0, 1, 1, 1, 1]);



// Adding "real time simulation" to the above: 
// This version forks a routine that runs the while loop waiting 0.1 between iterations.
// This lets us monitor the process of the selection in "real time". 
// Note that we cannot get the final value of it because the function h exits BEFORE
// the routine ends its repetitions. So we post the repetitions from inside the routine, 
// after the while loop. 

(
h = { | pop |
	var size, it = 0, sum;
	size = pop.size;
	{
		while {
			((sum = pop.sum.postln) != 0) and: { (sum != size) }
		}{	
			pop = pop.dup.flat.scramble[0..size - 1];
			pop.postln;
			0.1.wait;
			it = it + 1;
		};
		postf("Number of iterations: %\n", it);
	}.fork;
};
)

h.([0, 0, 0, 0, 1, 1, 1, 1]);


// Run (= "test") the above function "it" times and calculate the mean number of iterations 
// that were needed to terminate the genetic drift in each of the "tests". 
(
j = { | n = 4, it = 100 |
	var seedpop, pop, size, sum, itcount, itsum = 0;
	seedpop = 1.dup(n) ++ 0.dup(n);
	size = n * 2;
	it do: {
		pop = seedpop;
		itcount = 0;
		while {
			((sum = pop.sum) != 0) and: { (sum != size) }	
		}{	
			pop = pop.dup.flat.scramble[0..size - 1];
			itcount = itcount + 1;
		};
		itsum = itsum + itcount;
	};
	[itsum, itsum / it, itsum / it / (n * 2)];
};
)

j.(5, 1000);
j.(10, 1000);
j.(20, 10000);
j.(10, 10000);



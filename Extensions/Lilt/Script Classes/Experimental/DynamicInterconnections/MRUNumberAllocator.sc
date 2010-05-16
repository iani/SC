/* iz 080906
	// implements a most recently used ID allocator.
a = 	MRUNumberAllocator.new;
b = a.alloc;
c = a.alloc;
a.free(b);
d = a.alloc;
a.free(d);
e = a.alloc;


*/
MRUNumberAllocator
{	
	var <minimum = 0, <currentHi = 0, freeNumbers;
	
	*new { arg minimum = 0;
		^super.newCopyArgs(minimum, minimum)
	}
	free { arg id;
		if (freeNumbers.includes(id).not) {
			freeNumbers = freeNumbers add: id;
		}
	}
	alloc {
		if (freeNumbers.size == 0) {
			^currentHi = currentHi + 1;
		}{
			currentHi = minimum;
			^freeNumbers.pop;
		}
	}
}

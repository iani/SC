/* 

Utility class for sending a number of beats inside a Psend


a = Ptuple([\test, Pseries(1, 1, 3)], 1);

a.asStream.nextN(5);

*/

Beats {
	*new { | n, tag = \subbeat |
		^Ptuple([tag, Pseries(1, 1, n)], 1);
	}
}
/* 
A UniqueObject that holds a list.
For accumulating data in a list in streams. 
Ulist : UniqueObject {

	next { 	
}


*/

//:--
Chain(Prand([
	{
		NetAddr.localAddr.sendMsg(*[\test1, 1, 2, 3, 4, 5].postln);
		\dur.pn(0.1, 5);
	}.chain,
	{
			NetAddr.localAddr.sendMsg(*[\test2, \a.pseq((0..100000)), \b].postln);
			\dur.pn(1, 5);
	}.chain
	],
	30
));
//:--
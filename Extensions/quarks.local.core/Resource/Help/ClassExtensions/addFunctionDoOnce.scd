
evaluate a function once only. 
If the function has been already evaluated, it will not be evaluated again, until:
	- either the Class Library is recompiled (Menu Lang, item Compile Library)
	- or the fuction is set the message "doOnceReset"
	
For example, try: 

{ | a, b, c | [a, b, c].postln }.doOnce([10, 20, 30]);

As ID of the function, a hash of its compiled byte code. If a function when compiled results in the same byte code as another one, it is recognized as identical, and therefore not repeated. For example: 

Compare this: 
(
f = { | a, b, c | [a, b, c].postln };
10 do: { | i |
	postf("% : \n", i); f.doOnce(Array.rand(3, 0, 10)); 
};
f.removeDoOnce;
)

To this: 
(
f = { | a, b, c | [a, b, c].postln };
10 do: { | i |
	f.removeDoOnce;
	postf("% : \n", i + 1); f.doOnce(Array.rand(3, 0, 10)); 
};
f.removeDoOnce;
)



Verifying the identity of byte code and its hash of different function instances: 

//:c function code
o = { }; 	// this is the original instance that we compare to;
// here follow 5 different function instances.
// they are different objects, but have the same byte code and therefore the same hash: 
a = { };
b = { };
c = { nil };
d = { nil };
e = { nil };

[a, b, c, d, e] do: { | f | 
	postf("=========== object identity: % ========== \nbytecode: %, hash: %.\n!!!: hash identity: %\n", 
	f === o, f.def.code, f.def.code.hash, f.def.code.hash === o.def.code.hash);
};

//:l using library to register doOnce functions

Library.at(\doOnceFuncs).postln;

f = { };

Library.put(\doOnceFuncs, f.def.code.hash, f).postln;

Library.at(\doOnceFuncs, f.def.code.hash).postln;


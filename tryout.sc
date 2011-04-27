//:a
UniqueWindow(\test).onClose({ |...args| { args.postln } ! 10; });
//:b
\test.window.onClose({ ({ 10.rand } ! 10).postln });
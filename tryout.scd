//:r
ServerReady.addObjectAction(\test, { postf("stargint %\n",100.rand);
		{ \default.play; }.defer(0.1) 
});

//:b
Server.default.boot;
//:q
Server.default.quit;

//:xxx

\test.objectClosed;

\default.play;
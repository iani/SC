/* 

Send notifications after a Server has called initTree and created its root node, but only when it boots. 

Add to ServerReady objects that want to start Synths, Groups or routines right after a server boots, but that do not want to restart them when a Server re-inits its tree after CmdPeriod (after the user types Command-. to stop all synths)

ServerReady(action, server, object)

ServerReady({ Synth(\default) });
WaitForServer.new;
WaitForServer({ Synth(\default) });

 
ServerReady.registerOneShot(object, action, server) 

ServerReady({ | ... args | args.postln }, );

*/

//:Boot server and do something when booted
WaitForServer(function: { { WhiteNoise.ar(0.1) }.play; });

//:Wait 1 second after the server boots to do something
WaitForServer.new;
1.wait; 
{ WhiteNoise.ar(0.1) }.play;


//:a basic notification - once only

ServerReady.registerOneShot(\something, { | ... args | args.postln; });

Server.default.quit;
1.wait;

Server.default.boot;

//:something one could do

ServerReady.registerOneShot(\something, { | sender, server |
	w = Window(server.asString, Rect(400, 400, 200, 200)).front;
	StaticText(w, Rect(50, 50, 100, 100)).string = "booted";
});

Server.default.quit;
1.wait;

Server.default.boot;


//:using the default server
Server.default.quit;

ServerReady.register(\default2, { { SinOsc.ar(2000, 0, 0.1) }.play; });
Server.default.boot;

//:specifying the server
Server.internal.quit;
1.wait;

ServerReady.register(\default, { { SinOsc.ar(1000, 0, 0.1) }.scope; }, Server.internal);
Server.internal.boot;



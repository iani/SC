/* 

Send notifications after a Server has called initTree and created its root node, but only when it boots. 

Add to ServerReady objects that want to start Synths, Groups or routines right after a server boots, but that do not want to restart them when a Server re-inits its tree after CmdPeriod (after the user types Command-. to stop all synths)

*/

//:a using the default server

Server.default.quit;
1.wait;
ServerReady.add(\default2, { { SinOsc.ar(2000, 0, 0.1) }.play; });
Server.default.boot;

//:b specifying the server
Server.internal.quit;
1.wait;

ServerReady.add(\default, { { SinOsc.ar(1000, 0, 0.1) }.scope; }, Server.internal);
Server.internal.boot;



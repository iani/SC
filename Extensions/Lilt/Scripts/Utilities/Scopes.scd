/* IZ Wednesday, April 16, 2008
FFT freq-scope and scope are good to have ready especially when teaching, to show time-domain vs. frequency domain stuff. 

	Set default server to internal, in preparation for use of scopes
	Boot internal server
	Start freq scope and stethoscope side by side at bottom of screen
	
	
a = Synth("variable_sin");
a.onEnd( { | ... args | args.postln } );
*/

var script;
var default_server_bak, freq_scope, stetho_scope;

script = ~script;
default_server_bak = Server.default;

~start = { 
	Server.default = s = Server.internal;
	if (Server.default.serverRunning.not) { Server.default.boot; };
	Server.default.onBoot({
		if (script.isRunning) {
//			{ stetho_scope.run; }.defer(0.5)
			{ script.stop }.defer(0.1);
			{ script.start }.defer(0.2);
		};
	}, false);

//	freq_scope = GUI.freqScope.new.window;
//	freq_scope.bounds = Rect(0, 0, 584, 328).fromRight;
//	stetho_scope = GUI.stethoscope.new(Server.internal, 1, bufsize: 4096 * 8, zoom: 32);
	stetho_scope = Stethoscope2.new(Server.internal, 1, bufsize: 4096 * 8, zoom: 32);
	stetho_scope.addDependant { | who, how |
		if (how === 'closed') { script.stopped };
	};
	stetho_scope.window.bounds = Rect(583, 0, 370, 328).fromRight;
};

~stop = {
//	freq_scope.close;
	stetho_scope.quit;
	Server.default = s = default_server_bak;
};






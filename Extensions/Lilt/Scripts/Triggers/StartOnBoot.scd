/* IZ Friday, May 16, 2008 
Start all scripts linked to me when the server of my session boots - or immediately if it is already running. 
*/

var script;
script = ~script;

~controlSpecs = [[\t_out]];

// start yourself as soon as the session is loaded:
~script.session.addDependant { | session, what_happened |
	if (what_happened === \loaded) { script.start }
};

// Note: doWhenInited does not work for starting yourself? (Why?)
/// script doWhenInited: { script.start }; /// did not work!

~start = {
	script.onBoot({
		// defer is needed because the server may be still loading lots of stuff at boot time
		// and its slow response may retrigger the script to think it is not started ?
		{ script.output.output do: { | p | p.script.start } }.defer(0.5);
	}, script.session.server, true)
};

~stop = { script.removeServer(script.session.server) };
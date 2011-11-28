/* 2011 11 18

Simple way to toggle sound on and off for these examples: 
Type Command-Alt-Shift-b to boot / quit the default server.

Typing Command-Shift-x evaluates the present file and boots the server if needed.

TUIO.activate;
TUIO.new.verbose = true;
TUIO.new.verbose = false;

Synth("test");

*/


Udef("test", { 
	var source, env;
	env = Env.perc;
	source = SinOsc.ar(Rand(400, 4000), 0, 0.1);
	Out.ar([0, 1], source * EnvGen.kr(env, doneAction: 2));
});


TUIO.new.sessionCreatedAction = { | session |
	Synth("test");
};


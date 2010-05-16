/* iz Tuesday; November 4, 2008: 3:28 PM
Playing envelope-synths


Synth("kr_gesture");
g = Synth("kr_gesture");
g.setn(
a = { | freq = 400 | SinOsc.ar(freq, 0, 0.1) }.play;

Synth.env(
	Env(({ [400 rrand: 1700, 900, 900 rrand: 1200,800] } ! 100).flat, { 0.01 rrand: 0.5 } ! 99, \exp),
	0, a
);

a.map(\freq, 0);


a = { | freq = 400 | SinOsc.ar(freq, 0, 0.1) }.play;
a.addGesture(\freq, Env(Array.rand(100, 400, 1000), { 0.05 exprand: 10.5 } ! 99, \step));
a.addGesture(\freq, Env(Array.rand(100, 400, 1000), { 0.05 exprand: 10.5 } ! 99, \sine));
a.addGesture(\freq, Env(Array.rand(100, 400, 1000), { 0.05 exprand: 10.5 } ! 99, Array.rand(99, -50, 50)));
a.addGesture(\freq, Env([400, 1000].dup(50).flat, { 0.5 } ! 99, 15));
a.addGesture(\freq, Env([400, 1000].dup(50).flat, { 0.15 } ! 99, -15));
a.addGesture(\freq, Env([400, 1000].dup(50).flat, { 0.5 } ! 99, Array.series(99, -50, 1)));


a = { | freq = 400 | SinOsc.ar(freq, 0, 0.1) }.play;
b = Bus.control;
a.addGesture(\freq, Env([-400, 400].dup(50).flat, { 0.125 } ! 99, 15), b);
a.addGesture(\freq, Env([-400, 400].dup(50).flat, { 0.25 } ! 99, 15), b);
a.addGesture(\freq, Env([-400, 400].dup(50).flat, { 0.5 } ! 99, 15), b);
a.addGesture(\freq, Env([-400, 400].dup(50).flat, { 1 } ! 99, 15), b);
a.addGesture(\freq, Env([-400, 400].dup(50).flat, { 2 } ! 99, 15), b);

a = { | freq = 400 | SinOsc.ar(freq, 0, 0.1) }.play;
b = Bus.control;
a.addGesture(\freq, Env([0, 400].dup(50).flat, { 0.125 / 2} ! 99, \step), b);
a.addGesture(\freq, Env([0, 400].dup(50).flat, { 0.125 } ! 99, \step), b);
a.addGesture(\freq, Env([0, 400].dup(50).flat, { 0.25 } ! 99, \step), b);
a.addGesture(\freq, Env([0, 400].dup(50).flat, { 0.5 } ! 99, \step), b);
a.addGesture(\freq, Env([0, 400].dup(50).flat, { 1 } ! 99, \step), b);
a.addGesture(\freq, Env([0, 400].dup(50).flat, { 2 } ! 99, \step), b);


a.addGesture(\freq, Env([300, 400].dup(50).flat, { 0.125 / 0.2} ! 99, \step), b);
a.addGesture(\freq, Env([300, 400].dup(20).flat, { 2} ! 39, \sine), b);


a.addGesture(\freq, Env([400, 800].dup(50).flat, { 1 } ! 99, \step), b);

Synth.env(Env([700,900,900,800], [1,1,1]*0.4, \exp), b, a);
Synth.envChain(Env([700,900,900,800], [1,1,1]*0.4, \exp), b, a);

Env([0, 1, 0, 1, 0], [1, 1, 1, 1], [-2, -1, 0, 1]).plot;

*/

+ Synth {
	// 081118 envChain chains kr_gesture envelope synths with env	// to play an envelope of arbitrary length
	*envChain { | env, bus = 0, target = 0, addAction = \addBefore, defName = 'kr_gesture', 
		output = 'k_out', envCtl = 'env' |
		var synth, server, id, segment, first_breakpoint = [];
		var size, chainFunc;
		env = env.asArray.clump(28).reverse;
		bus = bus.asUGenInput;
		target = target.asTarget;
		// avoid adding the synth before the root Group: 
		if (target.nodeID == 0) {
			addAction = 0;	// == addToHead!
		}{
			addAction = addActions.at(addAction);
		};
		server = target.server;
		chainFunc = {
			if (env.size > 0) {
				synth = this.basicNew(defName, server);
				synth.onEnd({ /* thisMethod.report("done"); */ chainFunc.value; });
				id = synth.nodeID;
				segment = first_breakpoint ++ env.pop;
				size = segment.size;
				segment[1] = size / 4 - 1;
				server.sendBundle(nil,
					[\s_new, defName, id, addAction, target.nodeID, output, bus],
					[\n_setn, id, \env, size] ++ segment // .postln
				);
				first_breakpoint = [segment[size - 4], 0, -99, -99]; //.postln;
			}
		};
		chainFunc.value;
//		^synth;
	}
	// 081118 uses kr_gesture which can only play envelopes of up to 8 points
	*env { | env, bus = 0, target = 0, addAction = \addBefore, defName = 'kr_gesture', 
		output = 'k_out', envCtl = 'env' |
		var synth, server, id;
		env = env.asArray;
		bus = bus.asUGenInput;
		target = target.asTarget;
		// avoid adding the synth before the root Group: 
		if (target.nodeID == 0) {
			addAction = 0;	// == addToHead!
		}{
			addAction = addActions.at(addAction);
		};
		server = target.server;
		synth = this.basicNew(defName, server);
		id = synth.nodeID;
		server.sendBundle(nil,
			[\s_new, defName, id, addAction, target.nodeID, output, bus],
			[\n_setn, id, \env, env.size] ++ env
		);
		^synth;
	}
	/* this is a first simple case
	there are many ways to map to busses for receiving input from other processes 
	which should should be further explored in the future ...
	*/
	addGesture { | parameter, env, bus = 0 |
		var server;
		server = this.server;
		bus = bus.asUGenInput;
		Synth.envChain(env, bus, this);
		this.map(parameter, bus);
	}
}


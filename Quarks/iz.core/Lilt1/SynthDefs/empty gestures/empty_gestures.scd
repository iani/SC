/* iz Monday; October 27, 2008: 2:19 PM

Note 081115: this approach requires increasing amounts of cpu with growing size of envelope. 
One synth of a synthdef for a 300-point envelope takes ca 1 % cpu on a 2.1 GHz Intel Core Duo. 
The same for a 3000 point envelope takes ca 6-10% cpu.

Monday; November 17, 2008: 10:32 AM: I tried alternative approaches with Line, Phasor, Index, Duty, Stepper (phasor_line.scd) but it is very hard - or impractical - to create dynamic structures with changing durations between each trigger event. 
Therefore will use this approach here, limiting the SynthDef to an 8-point envelope, and creating a chain of synths to play envelopes with more than 8 break-points. 

-------------------------

Orestis Karamanlis told me about the "Empty Gestures" concept of Julio d'Escrivan. Here is an envelope player to be used for playback of envelopes on a control bus which can be mapped to any input with new methods:  Meta_Synth:env, Synth:addGesture
These methods will be written after the new SynthDefs here have been tested. 

SynthDefs based on examples in example file "env automation.rtf".
-------------------------
a = Synth("variable_sin");
e = Env([700,900,900,800], [1,1,1]*0.4, \exp).asArray;
b = Synth("kr_gesture");
a.map(\freq, 0);
b.setn(\env, [e.size] ++ e);

-------------------------

a = SynthDef("gtest", { | k_out = 0 |
	var env, envctl, ctl, sound;
//	env = Env.perc;
	env = Env.newClear(8);
	envctl = Control.names(\env).kr(env.asArray);
	ctl = EnvGen.kr(envctl, levelScale: 400, levelBias: 400, timeScale: 10, doneAction: 2);
	Out.ar(0, SinOsc.ar(ctl, 0, 0.1));
	Out.kr(k_out, 0);
}).play;
a.free;
e = Env([700,900,900,800], [1,1,1]*0.4, \exp).asArray;
a.setn(\env, *([e.size] ++ e));

s.sendBundle(nil,[\s_new, \gtest, 2003, 0, 0],[\n_setn, 2003, \env, e.size] ++ e);


SynthDef("kr_gesture_test", { | k_out = 0 |
	var env, envctl, ctl;
	env = Env.newClear(3000);
	envctl = Control.names([\env]).kr(env.asArray);
	ctl = EnvGen.kr(envctl, doneAction: 2);
	Out.ar(0, SinOsc.ar(ctl * 4 / 5, 0, 0.1));
	Out.kr(k_out, ctl);
}).load(Server.local);

f = Synth("kr_gesture_test");
e = Env(({ [700,900,900 rrand: 1200,800] } ! 10).flat, { 0.2 } ! 39, \exp).asArray;
f.setn(\env, e.size, *e);
s.sendBundle(nil,[\s_new, \kr_gesture_test, 2003, 0, 0],[\n_setn, 2003, \env, e.size] ++ e);

Synth.env(Env([700,900,900,800], [1,1,1]*10.4, \exp));
Synth.env(Env(({ [700,900,900 rrand: 1200,800] } ! 10).flat, { 0.2 } ! 39, \exp));

f.setn(\env, [e.size] ++ e);

SynthDef("kr_gesture30", { | k_out = 0 |
	var env, envctl;
	env = Env.newClear(30);
	envctl = Control.names([\env]).kr(env.asArray);
	Out.kr(k_out, EnvGen.kr(envctl, doneAction: 2));
}).send(Server.local);
SynthDef("kr_gesture300", { | k_out = 0 |
	var env, envctl;
	env = Env.newClear(300);
	envctl = Control.names([\env]).kr(env.asArray);
	Out.kr(k_out, EnvGen.kr(envctl, doneAction: 2));
}).send(Server.local);

// cannot be sent - must be loaded
SynthDef("kr_gesture3000", { | k_out = 0 |
	var env, envctl;
	env = Env.newClear(3000);
	envctl = Control.names([\env]).kr(env.asArray);
	Out.kr(k_out, EnvGen.kr(envctl, doneAction: 2));
}).load(Server.local);

// this one cannot be loaded 
// exception in GrafDef_Load: name too long > 31 chars
// while reading file '/Users/iani/Library/Application Support/SuperCollider/synthdefs/kr_gesture30k.scsyndef'

SynthDef("kr_gesture30k", { | k_out = 0 |
	var env, envctl;
	env = Env.newClear(30000);
	envctl = Control.names([\env]).kr(env.asArray);
	Out.kr(k_out, EnvGen.kr(envctl, doneAction: 2));
}).load(Server.local);

Synth(\kr_gesture30);
Synth(\kr_gesture300);
Synth(\kr_gesture3000); // takes 10 % cpu on an intel duo 
Synth(\kr_gesture30k); // cannot be loaded! 

[

SynthDef("kr_gesture", { | k_out = 0 |
	var env, envctl;
	env = Env.newClear(3000);
	envctl = Control.names([\env]).kr(env.asArray);
	Out.kr(k_out, EnvGen.kr(envctl, doneAction: 2));
}),
/// temporary, for tests: 
SynthDef("kr_gesture_test", { | k_out = 0 |
	var env, envctl, ctl;
	env = Env.newClear(3000);
	envctl = Control.names([\env]).kr(env.asArray);
	ctl = EnvGen.kr(envctl, doneAction: 2);
	Out.ar(0, SinOsc.ar(ctl, 0, 0.1));
	Out.kr(k_out, ctl);
}).play;
]

 =======================================================================
 
~levels = [500, 900].dup(20).flat ++ [450];
Env(~levels, { 1 } ! (~levels.size - 1), -2).asArray.clump(7 * 4).asCompileString;

~levels = (4..20) * 100;
(~array = Env(~levels, { 1 } ! (~levels.size - 1), -2).asArray.clump(7 * 4)).asCompileString;
~array.reverse; 
~array.pop;
~array	
~prev;
(1..22).clump(5) collect: { | x |
	
	if (~prev.notNil) { ~prev[3..] ++ x } { x }
};

Env.perc.asArray.clump(7 * 4).reverse;
Env.perc.asArray.clump(7 * 4).reverse.pop;
Env.perc.asArray.clump(7 * 4);
Env.adsr.asArray.clump(7 * 4);

  =======================================================================
  
a = { | freq = 400 | SinOsc.ar(freq, 0, 0.1) }.play;
a.addGesture(\freq, Env.perc(level: 800));
a.addGesture(\freq, Env(Array.rand(100, 400, 1000), { 0.05 exprand: 10.5 } ! 99, \step));
a.addGesture(\freq, Env(Array.rand(100, 400, 1000), { 0.05 exprand: 10.5 } ! 99, \sine));
a.addGesture(\freq, Env(Array.rand(100, 400, 1000), { 0.05 exprand: 10.5 } ! 99, Array.rand(99, -50, 50)));
a.addGesture(\freq, Env([400, 1000].dup(5).flat, { 0.5 } ! 9, 15));
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

a.addGesture(\freq, Env([400, 800].dup(50).flat, { 1 } ! 99, \step), b);
a.addGesture(\freq, Env([400, 800].dup(50).flat, { 1 } ! 99, 1), b);


a = { | freq = 400 | SinOsc.ar(freq, 0, 0.1) }.play;
b = Bus.control;
a.addGesture(\freq, Env([400, 800].dup(3).flat, { 0.1 } ! 5, 1), b);
a.addGesture(\freq, Env([400, 800].dup(40).flat, { 0.1 } ! 79, 1), b);


Synth.env(Env([700,900,900,800], [1,1,1]*0.4, \exp), b, a);
Synth.envChain(Env([700,900,900,800], [1,1,1]*0.4, \exp), b, a);


*/

[

SynthDef("kr_gesture", { | k_out = 0 |
	var env, envctl;
	env = Env.newClear(8);
	envctl = Control.names([\env]).kr(env.asArray);
	Out.kr(k_out, EnvGen.kr(envctl, doneAction: 2));
})
/*
,
SynthDef("env", { arg k_out=0;
	var env, envctl;
	
	// make a dummy 8 segment envelope
	env = Env.newClear(8);
	
	// create a control argument array
	envctl = Control.names([\env]).kr( env.asArray );
	
	Out.kr(i_outbus, EnvGen.kr(envctl, doneAction: 2));
}).load(s);
*/
]
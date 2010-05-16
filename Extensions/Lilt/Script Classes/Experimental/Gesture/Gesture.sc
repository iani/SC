/* iz Thursday; October 30, 2008: 4:09 PM

Basic class for implementation of Julio d'Escrivan's concept of "Empty Gestures". 

See also: 
Meta_Synth:env
Synth:addGesture


Testing ...

(
SynthDef("gesture_synth_help", { | out = 0, freq = 440, amp = 0.1 |

	Out.ar(out, SinOsc.ar(freq + 1500, 0, amp));
}).load(Server.local);
)

a = Synth("gesture_synth_help");

(
SynthDef("gesture_env_help1", { | out = 0, freq = 5, amp = 20 |
	var mod;
	mod = SinOsc.kr(freq, 0, amp);
	Out.kr(out, mod);
	Out.ar(0, SinOsc.ar(mod + 500, 0, 0.1));
}).load(Server.local);
)
b = Synth("gesture_env_help");

/////

(
SynthDef("gesture_env_help2", { | out = 0 |
	var env, envctl;
	var mod;
	env = Env.newClear(3000);
	envctl = Control.names([\env]).kr( env.asArray );

	mod = SinOsc.kr(freq, 0, amp);
	Out.kr(out, mod);
	Out.ar(0, SinOsc.ar(mod + 500, 0, 0.1));
}).load(Server.local);
)
b = Synth("gesture_env_help");

////

a.map(\freq, 0);
b.free


*/

Gesture {
	var <input;
	var <process;
	var <output;

	*new { | process, input, output |
		^super.new.init(process, input, output);
//		^super.new.init(process.asProcess, input.asInput, output.asOutput);

	}
	
	init { | argProcess, argInput, argOutput |
		
	
	}
	
	addInput {}
	chainInput {}
	replaceInput {}
	addProcess {}
	chainProcess {}
	replaceProcess {}
	addOutput {}
	chainOutput {}
	replaceOutput {}
}
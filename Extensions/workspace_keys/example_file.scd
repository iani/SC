// Following line defines the keyboard selector for this document:
//Doc:a

// Following snippet is always executed on loading this document, because it is marked with !
//:! Boot Server 

if (Server.default.serverRunning.not) { Server.default.boot };

//:a Start a synth

if (a isKindOf: Synth and: { a.isRunning }) { a.release };

a = { | freq = 400, gate = 1 |
	SinOsc.ar(freq, 0, EnvGen.kr(Env([0, 0.1, 0.05, 0.15, 0.02, 0], [0.01, 0.1, 0.3, 0.1, 1.0], 1, 1), gate, doneAction: 2));
}.play(args: [\freq, 500 rrand: 2000]).register;

//:b change that synth

if (a.notNil) { a.set(\freq, 500 + 1000.rand) };

//:c release the synth

if (a.notNil) { a.release };

//:d post keyboard input

EZListView.new(label: "Post Keyboard Input").widget.focus.keyDownAction = { | ... args | args.postln };

//:e freq slider for the synth

EZSlider(label: "freq control", controlSpec: \freq.asSpec).action = { | me | a.set(\freq, me.value) };


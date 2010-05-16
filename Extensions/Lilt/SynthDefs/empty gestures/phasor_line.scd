/* iz Saturday; November 15, 2008: 9:02 PM

Stopped 081117. Going back to empty_gestures.scd kr_gesture SynthDef, with 8 points per envelope only, and creating a chain of synths to create envelopes with more than 8 break-points. 

-------------------------

This is a new approach for "Empty Gestures" (see older approach in file empty_gestures.scd).
Using envelopes has the disadvantage that it requires increasing amoutns of cpu as the number of points in the envelope grows. Therefore using Phasor or Lag with Index instead. 

Orestis Karamanlis told me about the "Empty Gestures" concept of Julio d'Escrivan. Here is an envelope player to be used for playback of envelopes on a control bus which can be mapped to any input with new methods:  Meta_Synth:env, Synth:gesture

a = Synth("variable_sin");

Impulse, Stepper, Index, Phasor, Duty

a = { Impulse.ar(10, 0, 0.1) }.play;
a = { Stepper.ar(Impulse.ar(40), 0, 0, 10, 1) * 0.01 }.play

b = Buffer.loadCollection(Server.default, [0, 0.1, 0.0, 0.2, 0.0, 0.3, 0.0, 0.4, 0.0, 0.5], 1);
c = Buffer.loadCollection(Server.default, [1, 2, 3, 4, 5], 1);

a = { Index.ar(b, Stepper.ar(Impulse.ar(40), 0, 0, 9, 1)) }.play;
a = { SinOsc.ar(Index.ar(b, Stepper.ar(Impulse.ar(10), 0, 0, 9, 1), 1000, 400), 0, 0.1) }.play;
// -------------------------
(
a = { SinOsc.ar(Index.ar(b, Stepper.ar(Impulse.ar(
	10
), 0, 0, 9, 1), 1000, 400), 0, 0.1) }.play;
)

a = { Phasor.ar(1, 0.0001, -0.5, 0.5, -0.5) }.play;
a = { Phasor.ar(1, 0.0001, -0.5, 0.5, -0.5) }.play;
a = { Phasor.ar(1, 3 / s.sampleRate, -0.5, 0.5, -0.5) }.play;
a = { SinOsc.ar(Phasor.ar(1, 0.01, 400, 800, 400), 0, 0.1) }.play;

a = { SinOsc.ar(Index.ar(b, Stepper.ar(Impulse.ar(10), 0, 0, 9, 1), 1000, 400), 0, 0.1) }.play;

a = { SinOsc.ar( 100 *
	Stepper.ar(Impulse.ar(10), 0, 0, 10, 1),
	0, 0.1)
}.play;
b = Buffer.loadCollection(Server.default, [0, 0.1, 0.2, 0.3, 0.4], 1);


a.free;




*/


/* Sat 22 September 2012  8:51 PM EEST
From: 
Daniel Mayer daniel-mayer@email.de via lists.bham.ac.uk 
11:13 PM (21 hours ago)

to sc-users 
Refering to this thread:


http://new-supercollider-mailing-lists-forums-use-these.2681727.n2.nabble.com/translate-this-example-for-using-in-proxySpace-td7584467.html

This nice example made me think about a possible style of
live-coding I wasn‘t aware of. I hardly do live-coding myself,
so I‘d like to ask if I‘m overlooking some already existing
objects that do the same I'm trying here, maybe saving
below function wrapping.

As often said, synthdefs and consequently synths are
fixed structures server-side with fixed ugen graphs,
array sizes and so on. This fact can be weakened by some
practices like zeropadding arrays, use of Select ugens,
linking synths via busses.

But in this example SynthDefs are dynamically generated in the
language, so even parts of synth definitions, e.g. ugens can be
replaced by placeholders.

[... the code ...]

Surprising - at least for me,
or is such use already documented somewhere ?


Greetings

Daniel

-----------------------------
http://daniel-mayer.at
-----------------------------



*/

(
~spec = { `[500.rand*(1..9)] };
~src = { Crackle.ar(2, 0.01) };
~sus = 9;
~width = 1/4;
~dur = 1;

f = { { Klank.ar(~spec.(), ~src.()) * LFGauss.ar(~sus, ~width, 0, 0, 2) * 0.2 } !2 };
t = Task{ loop{ f.play; ~dur.wait } }.play
)

// replace, wait some seconds (fadetime)

~src = {  Decay.ar(Impulse.ar(rand(10, 20), 0, 0.002), 0.05) };

~spec = { `[1000.rand*(1,1.2..5) + 200]  };


~src = {  Dust.ar(rand(100, 200), 0.005) };

~src = {  WhiteNoise.ar(0.0005) };

t.stop;

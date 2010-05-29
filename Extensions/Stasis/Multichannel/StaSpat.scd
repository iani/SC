/* Spatializer for Stasis  --- IZ 100528
Version 0.1 Obsolete. 
Implementing the structure of groups and synths and the functions that operate on them in one Event. 

See StaSpat.sc for previous work and variables planned. 

Audio Input 	-> 	inputSynth 	-> 	inputBus
inputBus 		->	filters		-> 	filterBus
filterBus		-> 	reverbs		-> 	reverbBus
reverbBus		->	panners		->	Audio Output

*/

(
SynthDef("source_test", { | out = 0, rate = 5, amp = 0.1 |
	Out.ar(out, amp * WhiteNoise.ar(Decay2.kr(Impulse.kr(rate))));
}).load(Server.default);

SynthDef("ar_input", { | in = 0, out = 0 | Out.ar(out, In.ar(in)) }).load(Server.default);

SynthDef("filter", { | in = 0, out = 0, mix = 0.25, freq = 1000, bw = 8.0 |
	var input;
	input = In.ar(in);
	Out.ar(out, Mix.ar([1 - mix * input, BBandPass.ar(input, freq, bw, mix)]));
}).load(Server.default);

/*
z = { BBandPass.ar(SoundIn.ar([0,1]), MouseX.kr(20,20000,\exponential),
	MouseY.kr(0.0, 10.0, \linear), // bw
	0.5); // mul
}.play
*/

SynthDef("reverb", { | in = 0, out = 0, mix = 0.25, room = 0.15, damp = 0.5 |
	Out.ar(out, FreeVerb.ar(In.ar(in), mix, room, damp));
}).load(Server.default);

SynthDef("pan", { | in = 0, out = 0, pos = 0.0, level = 1.0, width = 2.0, orientation = 0.5 |
	Out.ar(out, PanAz.ar(8, In.ar(in), pos, level, width, orientation));
}).load(Server.default);

SynthDef("amp_tracker", { | amp = 0.1 |
	var src;
	src = SinOsc.kr(440, 0, amp);
	Out.kr(1000, Amplitude.kr(src));
}).load(Server.default);
)

(
~sta = Event make: {
	var inputGroup, filterGroup, reverbGroup, panGroup, limiter_and_trackerGroup;
	var inputBus, filterBus, reverbBus;
	inputGroup = Group.new;
	filterGroup = Group.after(inputGroup);
	reverbGroup = Group.after(filterGroup);
	panGroup = Group.after(reverbGroup);
	limiter_and_trackerGroup = Group.after(panGroup);
	#inputBus, filterBus, reverbBus = { Bus.audio(Server.default, 4) } ! 3;
	
	~receivers = Stasis.init.receivers;
	~inputs = [~dakis1 = (), ~dakis2 = (), ~omer1 = (), ~omer2 = ()];
	~inputs do: { | in, count |
		in use: {
			~inputBus = inputBus.index + count;
			~filterBus = inputBus.index + count;
			~reverbBus = reverbBus.index + count;
			~input = Synth("ar_input", [\in, count + 8, \out, ~inputBus], inputGroup);
			~filter = Synth("filter", [\in, ~inputBus, \out, ~filterBus], filterGroup);
			~reverb = Synth("reverb", [\in, ~filterBus, \out, ~reverbBus], reverbGroup);
			~pan = Synth("pan", [\in, ~reverbBus, \out, 0], panGroup);
		};
	};
	~test = Synth("source_test", [], inputGroup);

	~aris = (

		
	);
};
)


~input_test = Synth("ar_input", [\in, 0, \out, 1]);

~all_inputs = { | in = 0 | In.ar((0..4) + in) }.scope;

r = { 

	inf do: { | i |
		~input_test.set(\in, (i % 48).postln);
		1.wait;
	};

}.fork;


~all_inputs.set(\out, 24)

r = { 

	inf do: { | i |
		~input_test.set(\in, (i % 48).postln);
		1.wait;
	};

}.fork;


~test = Synth("source_test");

~inputtest.set(\in, 0);

~test.free;



~sta.dakis1;
~sta.test.set(\out, ~sta.dakis1.inputBus);
~sta.dakis1.filter.set(\mix, 0.9);
~sta.dakis1.filter.set(\freq, 10000);
~sta.dakis1.filter.set(\bw, 0.1);
~sta.dakis1.reverb.set(\mix, 0.8);
~sta.dakis1.reverb.set(\mix, 0.5);
~sta.dakis1.reverb.set(\mix, 0.0);
~sta.dakis1.reverb.set(\room, 0.5);
~sta.dakis1.reverb.set(\room, 0.1);
~sta.dakis1.reverb.set(\room, 0.9);
~sta.dakis1.pan.set(\pos, 0.0);
~sta.test.free;


~sta.dakis1;
~sta.test.set(\out, ~sta.dakis1.inputBus);
~sta.dakis1.filter.set(\mix, 0.9);
~sta.dakis1.filter.set(\freq, 10000);
~sta.dakis1.filter.set(\bw, 0.1);
~sta.dakis1.reverb.set(\mix, 0.8);
~sta.dakis1.reverb.set(\mix, 0.5);
~sta.dakis1.reverb.set(\mix, 0.0);
~sta.dakis1.reverb.set(\room, 0.5);
~sta.dakis1.reverb.set(\room, 0.1);
~sta.dakis1.reverb.set(\room, 0.9);
~sta.dakis1.pan.set(\pos, 0.3);
~sta.test.free;




~sta.dakis1.reverb.set(\mix, 0.0);

~sourceGroup = Group.new;
~fxGroup = Group.new;
~fxBus = Bus.audio;
~source = Synth("source_test", [\out, 0], ~sourceGroup);
~fx = Synth("ar_input", [\out, 0], ~fxGroup);
~source.set(\out, ~fxBus.index);
~fx.set(\in, ~fxBus.index);


(
a = Synth("amp_tracker");
b = Bus(\control, 1000, 12);
b.getn(20, { | amps | amps.postln; });
r = {
	var addr = NetAddr.localAddr;

	loop {
		b.getn(12, { | amps | addr.sendMsg('/amps', *amps); });
		0.03.wait;
	};
}.fork;
)








// -------- OLDER: 
(

~sta = Event make: {
	~receivers = Stasis.init.receivers;
	~dakis1 = Event make: {
		~output = Bus.audio;
		~input = Synth("ar_input", [\in, 0, \out, ~output.index]);
	};
	~dakis2 = Event make: {
		~output = Bus.audio;
		~input = Synth("ar_input", [\in, 1, \out, ~output.index]);
	};
	~omer1 = Event make: {
		~output = Bus.audio;
		~input = Synth("ar_input", [\in, 2, \out, ~output.index]);
	};
	~omer2 = Event make: {
		~output = Bus.audio;
		~input = Synth("ar_input", [\in, 3, \out, ~output.index]);
	};

	~inputs = [~dakis1, ~dakis2, ~omer1, ~omer2];

	
	~aris= ();
};
)
/* Spatializer for Stasis 

IZ 100526


a = StaSpat.new;

a.makeGroups;
a.testGroups;


b = Bus.audio(Server.default, 4).index;
c = Bus.audio(Server.default, 4);

Audio Input 	-> 	inputSynth 	-> 	inputBus
inputBus 		->	filters		-> 	filterBus
filterBus		-> 	reverbs		-> 	reverbBus
reverbBus		->	panners		->	Audio Output

inputBus 		->	featureSynth	-> OSC data output to Ari

*/


StaSpat {
	var <inputBus; // The effects can read from this buss. 
				
	var <filterBus, <reverbBus, <pannerBus; // more busses will be needed. One per effect process
	var <inputSynth;	// Out.ar(inputBus, In.ar([0, 1, 2, 3])) ... 
	var <inputGroup;
	var <filters;
	var <filterGroup;
	var <reverbs;			// Use FreeVerb
	var <reverbGroup;
	var <panners;
	var <pannerGroup;
	var <pannerControls;

	var <oscReceivers;
	var 

	makeGroups { 
		inputGroup = Group.new;
		filterGroup = Group.after(inputGroup);

	
	}	

	testGroups {
		var source, effect, bus;
		bus = Bus.audio;
		source = { Out.ar(bus.index, 0.1 * WhiteNoise.ar(Decay.kr(Impulse.kr(5)))) }.play(inputGroup);
		effect = { FreeVerb.ar(In.ar(bus.index), 0.5, 0.5, 0.5) }.play(filterGroup);

		//FreeVerb.ar(in, mix, room, damp, mul, add)
	
	}

}

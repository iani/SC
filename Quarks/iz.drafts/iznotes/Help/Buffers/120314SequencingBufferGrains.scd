/* IZ 2012 03 14
This is from the work on the SoundFeatures quark. 

Just writing down some tries made with collected sound recordings ...

*/



//:Load an audio file from disk to buffer, to play with

BufferResource.load; // evaluate this line to open a file dialog
// The names of the loaded buffers are posted in the listener window. 
// Alternatively run BufferResource.list or BufferResource.postNames to get the names

//==================== Using buffers to store the series of parameter values ==================
//:1. Prepare buffers
{ 
	[\onsets, \rates, \centers, \durations] do: { | b |
		BufferResource(b, Server.default, 100);
		0.2.wait; // slower systems need some time for the buffer to initialize
		b.buffer.sendCollection(1 ! 100, 0);
		0.2.wait;
	};
	"BUFFERS INITIALIZED".postln;
}.fork;

//:1b. Test to see that the contents have been initialized
[\onsets, \rates, \centers, \durations] do: { | b |
	b.buffer.getn(0, 100, { | vals | b.post; ":".postln; vals.postln });
}

//:2. Play (see names of loaded buffers from BufferResource.list or BufferResource.postNames)
a = 'swallows10minL'.playBuf({  // tried also with 'Belugawhales'
	| buf, onsets, rates, centers, durations, numons = 4, numrat = 4, numcen = 4, numdur = 4 |
	onsets = TDuty.kr(Dbufrd(onsets, Dseries(0, 1, inf) % numons, 1), 0, 1);
	rates = Dbufrd(rates, Dseries(0, 1, inf) % numrat, 1);
	centers = Dbufrd(centers, Dseries(0, 1, inf) % numcen, 1);
	durations = Dbufrd(durations, Dseries(0, 1, inf) % numdur, 1);
	TGrains.ar(2, onsets, buf, rates, centers, durations, Dseq([-0.5, 0.5], inf), 0.5);
}, args: [
	\onsets, \onsets.buffer,
	\rates, \rates.buffer,
	\centers, \centers.buffer,
	\durations, \durations.buffer
]);

//:3. Change the contents of the buffers
\rates.buffer.set(1, -1);

//:4 different rates 
\rates.buffer.setn(0, [1, 1.2, 1.3, 1.4]);

//:4 different onsets
\onsets.buffer.setn(0, [0.1, 0.2, 0.3, 0.4]);
\durations.buffer.setn(0, [0.1, 0.2, 0.4, 0.4]);	// also shorten the durations

//:Different centers: 
\centers.buffer.setn(0, [0, 0.5, 1, 1.5]);

//:random variations of above
r = {
	var onsets;
	inf do: { | i |
		4.wait;
		onsets = [0.1, 0.2, 0.3, 0.4].scramble + [0.1, 0.2, 0.0, -0.05].choose;
		\onsets.buffer.setn(0, onsets);
		\durations.buffer.setn(0, onsets);
		\centers.buffer.setn(0, Array.rand(4, 0.0, 600)); // 4, 0, 20.0 for belugawhales
		\rates.buffer.setn(0, [1, 1.2, 1.3, 1.4] 
			* [-1, 0.1, 0.2, 0.5, 1.0, 1.1, 1.2, 1.5, 0.75].choose
		);
	};
}.fork;

//:--
r.stop;


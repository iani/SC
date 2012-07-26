// Very straightforward case of an analysis test.

//:Load sound file
e = SCMIRAudioFile(Platform.resourceDir +/+ "sounds/a11wlk01.wav", [[MFCC, 13], [Chromagram, 12]]);

//:Extract features
{ e.extractFeatures }.fork //wrapped in fork gives posted feedback as it goes

//:Plot
e.plotFeatures;

//:Similarity Matrix
e.similarityMatrix.plot;

//:Similarity Matrix - bigger plot
e.similarityMatrix.plot(4); // show each data item as a rectangle of 4 x 4 pixels

//:
e.inspect;

//:Choosing files interactively
Dialog.getPaths({ | path |
	var sf;
	path = path.first;
	sf = SCMIRAudioFile(path, [[MFCC, 13], [Chromagram, 12]]);
	{ sf.extractFeatures }.fork;
	Library.put('scmir', path.basename.asSymbol, sf);
});
//:--

Library.put('test', { "Hello".postln });

Library.at('test');
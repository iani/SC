/* Let's see how Similarity Matrix works with analysis data */

// ===== First create some data to work with =====

//:Load sound file
e = SCMIRAudioFile("sounds/a11wlk01.wav", [[MFCC, 13], [Chromagram, 12]]);
//:Extract features
{ e.extractFeatures }.fork //wrapped in fork gives posted feedback as it goes
//:Plot
e.plotFeatures;

// ====== Now check progressively with more dimensions =====
//:1 dimension (feature data set)
m = SCMIRSimilarityMatrix(1, e.getFeatureTrail(0)); // first feature data
m.calculate(1, 1); //create matrix of unit 1, metric 1 = Manhattan
m.plot(3); 

//:2 dimensions
m = SCMIRSimilarityMatrix(2, [e.getFeatureTrail(0), e.getFeatureTrail(1)]); 
m.calculate(1, 1); //create matrix of unit 1, metric 1 = Manhattan
m.plot(3); 

//:25 dimensions (all the extracted features
m = SCMIRSimilarityMatrix(e.numfeatures, e.featuredata); 
m.calculate(1); //create matrix of unit 1, default metric (== 0 == Euclidean?)
m.plot(3); 

//:Now from the audio file instance directly: 
e.similarityMatrix.plot;  // OK.

//:Pretty example from NC doc
e = SCMIRAudioFile("sounds/a11wlk01.wav", [Loudness]);
{ e.extractFeatures; }.fork;  		// wait for me to finish
e.plotFeatureTrail;				// plot first feature by default; only one, so plots that
m = e.plotSelfSimilarity(1,3,1); 	// pretty image

//:Examining diagonals ...

n = m.matrix;
o = n.collect(_ < 0.01).clump(e.numframes);
p = o.collect({ | r, i | r.rotate(i.neg) }).flop;

p[2]

//:visualising the diagonals rotated to vertical: 
m.matrix .... 

...
[0, 0, 1, 1, 0].differentiate
...

//:More features

e = SCMIRAudioFile("sounds/a11wlk01.wav", [[Loudness],[SpecCentroid],[SpecPcile, 0.95],[SpecPcile, 0.8],[SpecFlatness],[FFTCrest],[FFTCrest, 0, 2000], [FFTCrest, 2000, 10000],[FFTSpread],[FFTSlope],[SensoryDissonance]]);

e.extractFeatures(); //wait for me to finish

e.plotFeatureTrail(0,0.0,2.0) //Loudness over two seconds
e.plotFeatureTrail(4,1.0,3.0) //SpecFlatness over two seconds starting at time 1.0

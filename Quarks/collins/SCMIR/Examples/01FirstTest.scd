// Very straightforward case of an analysis test.

//:Load sound file
e = SCMIRAudioFile("sounds/a11wlk01.wav", [[MFCC, 13], [Chromagram, 12]]);
//:Extract features
{ e.extractFeatures }.fork //wrapped in fork gives posted feedback as it goes
//:Plot
e.plotFeatures;

// Following did not seem to work.
//:Similarity Matrix
b = e.similarityMatrix(100, 2);
//:Plot similarity matrix
b.plot(5,6);

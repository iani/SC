/* Next step: Onset detection */

// ===== First create some data to work with =====

//:Load sound file
e = SCMIRAudioFile("sounds/a11wlk01.wav", [[MFCC, 13], [Chromagram, 12]]);
//:Extract features
{ e.extractFeatures }.fork //wrapped in fork gives posted feedback as it goes
//:Plot
e.plotFeatures;

//:Extract onsets
e.extractOnsets;


e.onsetdata
e.numonsets

f.gatherFeaturesByOnsets //after this operation, featuredata will be onset locked; if referencing specific frames, must use onsetdata to get (starting) times

f.featuresbysegments //this flag has now been set to true

//should now be same:
f.featuredata.size/f.numfeatures
f.numonsets

b = f.similarityMatrix(5, 2);
b.plot(1,10);

l = f.findSections(0,1,20);
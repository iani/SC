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
m.calculate(1); //create matrix of unit 1, default metric (== 2 == Euclidean?)
m.plot(3); 

//:double checking

e.featuredata.size / e.numfeatures

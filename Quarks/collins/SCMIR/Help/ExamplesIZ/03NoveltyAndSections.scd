
// This test finds no sections for the short a11wlk01 file. Too short!?

e = SCMIRAudioFile("sounds/a11wlk01.wav", [[MFCC, 13], [Chromagram, 12]]);
{ e.extractFeatures; }.fork;  	// wait for me to finish
d = e.similarityMatrix(1, 2); 	
n = e.novelty(d,30); 
n.normalize.plot; 				//plot it

l = e.findSections; //wait for me to finish

l //l is a list of section boundaries 

//:Basic test
a = FloatArray[0.05, 0.1, 0.5, 0.8, 1.2, 1.4].normalize;
m = SCMIRSimilarityMatrix(1, a); // 1 dimensional data, stored in a
m.calculate(1, 1); 			// create matrix of unit 1, metric 1 = Manhattan
m.plot(100);					// plot, each point has a size of 100 pixels 

//:Test multi dimensional data
a = FloatArray.new.addAll((1..100).scramble).normalize;
m = SCMIRSimilarityMatrix(4, [a, a]); // 4 dimensional data. Nested arrays are flattened!
m.calculate(1, 1); 			//  create matrix of unit 1, metric 1 = Manhattan
m.plot(10);					// plot, each point has a size of 100 pixels 

//:See how repeated patterns show up in the matrix plot
a = { { 1.0.rand } ! 5.rrand(15) }; // generate random subsequences;
b = [1, 3, 2, 5, 10, 4] / 10; // a pattern to repeat 
m = SCMIRSimilarityMatrix(1, [a.value, b, a.value, a.value, b, a.value, b]);
m.calculate(1, 1); //create matrix of unit 1, metric 1 = Manhattan
m.plot(10); 

m.matrix.round(0.001);

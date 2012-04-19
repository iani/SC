//:Basic test
a = FloatArray[0.05, 0.1, 0.5, 0.8, 1.2, 1.4].normalize;
m = SCMIRSimilarityMatrix(1, a); // 1 dimensional data, stored in a
m.calculate(1, 1); 			// create matrix of unit 1, metric 1 = Manhattan
m.plot(100);					// plot, each point has a size of 100 pixels 

// Test above with different metrics: 

//:0 Metric (cosine) -- actually only used for dimensions > 1
m.calculate(1, 0).plot(100);

//:1 Metric (manhattan)
m.calculate(1, 1).plot(100);

//:2 Metric (euclidean) -- identical to manhattan in 1 dimension
m.calculate(1, 2).plot(100);

//:Testing euclidean distance. Try these 1 line at a time:
SCMIRSimilarityMatrix(2, [[0, 1], [1, 0]]).calculate(1, 2).plot(100).matrix;
/* Note: 

Point(0, 1) dist: Point(1, 0) is 2.sqrt, that is 1.4142135623731...

But SCMIR always divides that distance by the square root of the number of dimensions, 
which in this case is 2.sqrt. So we get: 

	(Point(0, 1) dist: Point(1, 0)) / 2.sqrt

which makes 1. 
*/

SCMIRSimilarityMatrix(2, [[0, 1], [1, 0]]).calculate(1, 1).plot(100).matrix;

SCMIRSimilarityMatrix(2, [(0..5), (5..0)]).calculate(1, 2).plot(100).matrix;
SCMIRSimilarityMatrix(2, [(0..5), (5..0)]).calculate(1, 1).plot(100).matrix;



SCMIRSimilarityMatrix(2, [[0, 2], [1, 0]]).calculate(1, 1).plot(100).matrix;

SCMIRSimilarityMatrix(2, [[0, 2], [2, 0]]).calculate(1, 2).plot(100).matrix;
SCMIRSimilarityMatrix(2, [[0, 2], [2, 0]]).calculate(1, 1).plot(100).matrix;

SCMIRSimilarityMatrix(2, [[0, 1], [1, 0]]).calculate(1, 2).plot(100).matrix;
SCMIRSimilarityMatrix(2, [[0, 1], [1, 0]]).calculate(1, 1).plot(100).matrix;

//:Test multi-dimensional data
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

+ SCMIRAudioFile {

	similarityMatrix { | unit = 1, metric = 0, prepost = 0, reductiontype = 1, other = nil |
		var matrix; 
		var data1, data2; 
		data1 = featuredata; 
		unit = unit max: 1;
		//check if self similarity or comparative
		if (other.notNil) {
			data2 = other.featuredata; 			 
			matrix = SCMIRSimilarityMatrix(numfeatures, data1, data2); 
		}{
			matrix = SCMIRSimilarityMatrix(numfeatures, data1);  //self similarity
		};
		"Calculating Similarity Matrix".postln;
		matrix.calculate(unit, metric, prepost, reductiontype); 
		"Calculated Similarity Matrix".postln;
		^matrix;
	} 
}
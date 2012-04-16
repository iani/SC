+ SCMIRAudioFile {
	similarityMatrix { | unit = 1, metric = 0, prepost = 0, reductiontype = 1, other = nil|
		var matrix; 
		var data1, data2; 
		if (featuredata.isNil) {
			"SCMIRAudioFile:similarityMatrix: this file has no feature data".postln;
			^nil
		};
		data1 = featuredata; 
		if (unit < 1) {
			"SCMIRAudioFile:similarityMatrix: unit less than 1".postln;
			^nil
		}; 
		//check if self similarity or comparative
		if (other.notNil) {
			if(other.isKindOf(SCMIRAudioFile)==false) {
				"SCMIRAudioFile:similarityMatrix: other file not an SCMIRAudioFile".postln;
				^nil
			};
			if (other.featuredata.isNil) {
				"SCMIRAudioFile:similarityMatrix: other file has no feature data".postln; 
				^nil
			};
 			if (featureinfo != other.featureinfo) {
	 			"SCMIRAudioFile:similarityMatrix: other file different feature info; different features extracted".postln;  
	 			^nil
	 		}; //implies then if pass this test that they have the same numfeatures
			//if(other.numframes<framesconsidered){framesconsidered = other.numframes;};
			data2 = other.featuredata; 			 
			matrix = SCMIRSimilarityMatrix(numfeatures, data1, data2); 
		}{
			matrix = SCMIRSimilarityMatrix(numfeatures, data1);  //self similarity
		};
		"Calculating Similarity Matrix".postln;
		matrix.calculate(unit,metric,prepost,reductiontype); 
		"Calculated Similarity Matrix".postln;
		^matrix;
	} 
}
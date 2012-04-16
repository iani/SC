+ SCMIRAudioFile {
	plotFeatures { | xsize = 1000, ysize = 800 |
		var labels, i = 0;
		if (featuredata.isNil) {  
			"SCMIRAudioFile:plotfeatures: no feature data available; ".post; 
			"Did you extract features first?".postln;
			^false;	  
		};

		labels = featureinfo
			.collect({ | f | { | i | format("% %", f.first.name, i + 1) } ! f[1] })
			.flatten(1);

		labels.postln;

		{ | i | this.getFeatureTrail(i) }.dup(this.numfeatures)
			.plot("Feature plot for "++basename, Rect(100, 100, xsize, ysize))
			.drawFunc = { | p |
				Pen.fillColor = Color.red.alpha = 0.5;
				Pen.font = Font("Helvetica", 12);
				Pen.stringAtPoint(labels@@(i % numfeatures), 5@(p.bounds.top + 5));
				i = i + 1;
			};
	}  
	
	//single feature plot
	getFeatureTrail { | which = 0, starttime = 0.0, endtime | 
		var array, index; 
		var startframe, endframe; 
		var timeperframe = SCMIR.hoptime; //0.023219954648526; 
		
		endtime = endtime ? duration; 		
		if (starttime < 0.0) { starttime = 0.0 };
		if (starttime > duration) { starttime = 0.0 };	 
		if (endtime > duration) { endtime = duration };
		if (endtime < starttime) { endtime = duration; }; 
		startframe = min((starttime / timeperframe).asInteger, numframes - 1);
		endframe = min((endtime / timeperframe).asInteger, numframes - 1);
		index = startframe * numfeatures + which;
		array = Array.fill(endframe - startframe + 1, { | i | 
			var value;
			value = featuredata[index];
		 	index = index + numfeatures; 
			value; 
		}); 
		^array; 
	}
	  
	plotFeatureTrail { | which = 0, starttime = 0.0, endtime |
		this.getFeatureTrail(which, starttime, endtime).plot;
	}
	
	//fork included since won't be using for batch processing
	plotSelfSimilarity{ | unit = 10, stretch = 1, metric = 0 |
		var matrix;
		matrix = this.similarityMatrix(unit, metric); 
		matrix.plot(stretch);
	}	  
}  
 

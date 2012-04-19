//compare two sequences (or one to itself)

//does not assume sequences already at desired size for analysis: can reduce at calculate stage via external

//first input sequence is always longer, since greater width in screen space for plotting, and greater time along x axis 


SCMIRSimilarityMatrix {  
	var <rows, <columns; //rows is size of source2, columns size of source 1
	var <dimensions; //number of features in source vectors
	var <sequence1, <sequence2; //actual vector sequences compared 
	var <self; //flag for self similarity, saves on some calculation 
	var <matrix; 
	var <reductionfactor; //for storing reduction factor in calculation
	var <reducedrows, <reducedcolumns;	
	
	*new { | dimensions, sequence1, sequence2 |       
		if (sequence1.isNil) {
			"SCMIRSimilarityMatrix:new: no first sequence passed in".postln; ^nil;
		};  
		^super.new.initSCMIRSimilarityMatrix(dimensions, sequence1, sequence2);       
	}  	
		
	initSCMIRSimilarityMatrix { | dim, seq1, seq2 |
		var temp;
		dimensions = dim; 
		sequence1 = seq1; 	
		//must be RawArray for file write out
		if ( sequence1.isKindOf(FloatArray).not ) { sequence1 = FloatArray.newFrom(sequence1.flat); };
		self = 0; 
		//comprison or self similarity	
		sequence2 = seq2 ?? {self = 1;  sequence1}; 
		//must be RawArray for file write out
		if(not(sequence2.isKindOf(FloatArray)) ) {
			sequence2 = FloatArray.newFrom(sequence2); 
		};
		//but if do this will get confused in interpreting results? 
		//swap if needed so always have columns >= rows
		if (sequence1.size<sequence2.size) {
			temp = sequence1; 
			sequence1 = sequence2; 
			sequence2 = temp; 
			"Swapped sequence1 and sequence2, since sequence2 was longer than sequence1".postln;
		};	
		
		//must allow for dimensions amongst the input data array 
		columns = (sequence1.size).div(dimensions); 	
		rows = (sequence2.size).div(dimensions); 
			
	}	

	calculate { | unit = 1, metric = 2, prepost = 0, reductiontype = 1 |
		var file; 
		var temp; 
		var bytescheck; 
		var outputfilename, inputfilename; 
		//unit is framesperblock, must be integer
		unit = unit.asInteger; 
		if (unit < 1) { "SCMIRSimilarityMatrix:calculate: unit less than 1".postln; ^nil}; 
		//since rows <= columns
		if (unit > rows) { unit = rows; };
		reductionfactor = unit; 
		//write out binary file with input data
		inputfilename = SCMIR.getTempDir ++ "similaritymatrix2input"; 
		outputfilename = SCMIR.getTempDir ++ "similaritymatrix2output"; 
		//"similaritymatrix2input"
		file = SCMIRFile(inputfilename,"wb"); 
	 	file.putInt32LE(columns);
		file.putInt32LE(rows); 
		file.putInt32LE(dimensions); 
		file.writeLE(sequence1); 
		if (self == 0) { file.writeLE(sequence2); };
		file.close; 
		
		// call auxiliary program
		temp = SCMIR.executabledirectory++"similaritymatrix2" + metric + unit + prepost + reductiontype + self + outputfilename + inputfilename; 
		
		//"similaritymatrix2output"+ "similaritymatrix2input"; 
		//temp = SCMIR.executabledirectory++"similaritymatrix2" + metric + unit + prepost + reductiontype + self + (tempdir++"similaritymatrix2output")+ (tempdir++"similaritymatrix2input"); 

		temp.postln;

		//unixCmd(temp);    
		//SCMIR.processWait("similaritymatrix2"); 
		
		SCMIR.external(temp); 

		//read result back in //tempdir++
		//"similaritymatrix2output"
		file = SCMIRFile(outputfilename,"rb");  
		
		reducedcolumns = columns.div(unit); //file.getInt32LE;
		reducedrows = rows.div(unit); //file.getInt32LE;
		temp = reducedrows*reducedcolumns;  
		matrix= FloatArray.newClear(temp);     
		file.readLE(matrix); 		  
		file.close;
	}	
	
	//may need some way to check original audio file lengths for time positions accurately in seconds 
	//will crash if matrix is not a symmetric matrix as an array of arrays   
	//no axes drawn, just direct plot with fixed border of 20
	plot { | stretch = 1, power = 5, path |
		
		var window, uview, background = Color.white;
		var xsize = reducedcolumns * stretch; 
		var ysize = reducedrows * stretch; 
		var border = 20;  
		var xaxisy, origin;
		var totalx = xsize + (2 * border); 
		var totaly = ysize + (2 * border); 
		
		//just need path, not [score, path]
		if(path.notNil){ if(path.size==2){path = path[1];}};
		window = Window("Similarity matrix", Rect(100, 100, totalx, totaly));
		window.view.background_(background);
		uview = UserView(window, window.view.bounds).focusColor_(Color.clear); 
 		xaxisy = totaly - border; 
 		origin = border@xaxisy;
		uview.drawFunc_({ 
				 
			Pen.use {
				Pen.scale(1, -1); 
				Pen.translate(0, totaly.neg); 
				Pen.width_(1); 
				Color.black.setStroke; 
				Pen.moveTo(origin); 
				Pen.lineTo((border + xsize)@(xaxisy));
				Pen.moveTo(origin); 
				Pen.lineTo(border@border); 
				Pen.stroke; 
				reducedcolumns do: { | i |	
					var pos;
					var x = (i * stretch) + border; 
					pos = reducedrows * i; 
					reducedrows do: { | j |
					var y = border + ((reducedrows - j - 1) * stretch); 
					Pen.color = Color.grey(0.2+(0.8*((1.0-(matrix[pos+j]))**power)));
//					Pen.addRect(Rect(x,y,stretch,stretch));
					Pen.addRect(Rect(x, y, stretch, stretch));
					Pen.fill;   
					}
				};
				if(path.notNil){
					Pen.color = Color.blue(0.9,0.5); //partially transparent
					path.do{|coord|
						var x = (coord[0]*stretch)+border; 
						var y = border + ((reducedrows-coord[1]-1)*stretch);						Pen.addRect(Rect(x,y,stretch,stretch));
						Pen.fill;
					}
				};
			}; 
		});
		window.front; 
	}  
}

+ SCMIRAudioFile {
	//fork included since won't be using for batch processing
	plotSelfSimilarity { | unit = 10, stretch = 1, metric = 0 |
		var matrix;
		matrix = this.similarityMatrix(unit, metric); 
		^matrix.plot(stretch);
	}	  

	similarityMatrix { | unit = 1, metric = 0, prepost = 0, reductiontype = 1, other = nil |
		var matrix; 
		var data1, data2; 
		data1 = featuredata; 
		unit = unit max: 1;
		// check if self similarity or comparative
		if (other.notNil) {	
			data2 = other.featuredata;	// comparative
			matrix = SCMIRSimilarityMatrix(numfeatures, data1, data2); 
		}{
			matrix = SCMIRSimilarityMatrix(numfeatures, data1);  // self similarity
		};
		"Calculating Similarity Matrix".postln;
		matrix.calculate(unit, metric, prepost, reductiontype); 
		"Calculated Similarity Matrix".postln;
		^matrix;
	}
}
//Autocousmatic by Nick Collins (c)2009-2011, released under GNU GPL 3 August 2011
 

AutocousmaticSectionDurationModel {
	var sourcedurations; 
	var durationtransitions; 
	var transitionnow, transitionpos; 
	
	*new {
	
	^super.new.initAutocousmaticSectionDurationModel; 	
	}
	
	initAutocousmaticSectionDurationModel {
		
		sourcedurations = [ 5.074, 6.372, 7.237, 15.425, 15.53, 15.915, 16.886, 21.696, 24.483, 25.125, 25.419, 28.221, 29.586, 30.0, 32.663, 32.932, 38.686, 39.692, 40.361, 41.512, 48.704, 51.799, 54.439, 55.44, 58.111, 59.901, 68.12266666667, 68.59, 77.093, 77.926, 88.873, 91.26256235828, 104.60399092971, 108.52, 155.123 ];
		
		//trigrams
		durationtransitions = [ [ 5.074, 21.696, 7.237 ], [ 6.372, 51.799, 54.439 ], [ 7.237, 58.111, 68.12266666667 ], [ 15.425, 5.074, 21.696 ], [ 15.53, 25.419, 40.361 ], [ 15.915, 15.425, 5.074 ], [ 16.886, 24.483, 15.53 ], [ 21.696, 7.237, 58.111 ], [ 24.483, 15.53, 25.419 ], [ 25.419, 40.361, 32.663 ], [ 28.221, 55.44, 6.372 ], [ 29.586, 48.704, 41.512 ], [ 32.663, 25.125, 108.52 ], [ 32.932, 28.221, 55.44 ], [ 38.686, 15.915, 15.425 ], [ 40.361, 32.663, 25.125 ], [ 41.512, 88.873, 32.932 ], [ 48.704, 41.512, 88.873 ], [ 51.799, 54.439, 59.901 ], [ 54.439, 59.901, 39.692 ], [ 55.44, 6.372, 51.799 ], [ 59.901, 39.692, 77.926 ], [ 77.093, 16.886, 24.483 ], [ 88.873, 32.932, 28.221 ], [ 155.123, 29.586, 48.704 ] ]; 
		
		transitionnow = durationtransitions.choose; 
		transitionpos = 3; 
		
	}
	
	
	next {
		var selection; 
		var nearest, score; 
		
		
		//(transitionnow.isNil) || 
		if(transitionpos==3) {
			
			score = 999.9; 
			nearest =  0; 
			//nearest
			
			selection = transitionnow[2]; 
			
			durationtransitions.do{|val,j|  var now= val[0]; var test = abs(now-selection);  if(test<score) {score= test; nearest=j}; };
			
			transitionnow = if(0.7.coin,{durationtransitions[nearest]},{durationtransitions.choose}); //later could choose closest to current?  
			
			transitionpos = 0; 
			
		}; 
		
		selection = transitionnow[transitionpos]; 
		
		if(0.5.coin) { selection = selection * rrand(1.1.reciprocal,1.1); }; 
		
		transitionpos = transitionpos + 1; 
		
		^selection; 
	}
	
	
}
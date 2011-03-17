/* IZ 201103

Create VBAPSpeaker array with the coordinates of the KlangDom at ZKM. 

KlangDom.new.speakers.inspect;

This class is no longer used. Using PanAz based panning instead, which is simpler and very effective. 
See KDpan etc. 

*/


KlangDom {
	var <speakers; // a VBAPSpeakerArray
	

	*new {
		^super.new.init;
	}
	
	init {
/*		speakers = VBAPSpeakerArray.new(3, [[-22.5, 14.97], [22.5, 14.97], [-67.5, 14.97], [67.5, 14.97], 
			[-112.5, 14.97], [112.5, 14.97], [-157.5, 14.97], [157.5, 14.97], [-45, 0], [45, 0], [-90, 0], [90, 0],
			[-135, 0], [135, 0], [0, 0], [180, 0]]
		);	
*/
		speakers = VBAPSpeakerArray.new(3, this.klangdomSpeakerCoords);
	}

	klangdomSpeakerCoords {
		var xyzCoords;
		xyzCoords = [
			[0.70, -1.00, 0.00],
			[0.30, 1.00, 0.00],
			[0.70, 1.00, 0.00]		
		];
		
		^xyzCoords collect: { | coords | this.xyz2azimuth_elevation(*coords); }
	}

	xyz2azimuth_elevation { | x, y, z |
		var azimuth, elevation, radius;
		radius = (x@y).asPolar.rho;
		elevation = (radius@z).asPolar.theta;  
		azimuth = (x@y).asPolar.theta;
		^[azimuth, elevation] / pi * 180;			
	}

}
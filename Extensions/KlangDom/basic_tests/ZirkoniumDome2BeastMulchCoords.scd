
/*
How to compute the azimuth and elevation parameters that are needed for BEASTmulch lib's VBAPSpeakerArray, in order to use with: VBAP, 
from the x, y, z coordinates given in Zirkonium presets from ZKM.

*/



(0@1).asPolar;

Polar




a = [1@0, 0@1, (-1)@0, 0@(-1)];

b = a collect: _.asPolar;

c = b collect: _.theta;

c * 180 / pi;



f = { | x, y, z |
	var azimuth, elevation;
	elevation = z * 90; // this must be improved. Calculate based on x, y, z! 
	azimuth = (x@y).asPolar.theta * 180 / pi;
	[azimuth, elevation];	
};

z = [[-0.30, -0.30, 0.75], [0.30, 0.30, 0.75], [0.0, 0.40, 0.75], [-0.30, 0.30, 0.75], [0.30, -0.30, 0.75], [0.0, -0.40, 0.75]]


z collect: f.(*_);

z.collect({ | coords | f.value(coords[0], coords[1], coords[2]) });


// Improving the calculation of z (azimuth) 

/* Preliminary: 
To get the correct radius, we need to know the distance (d) of the point from 0, 0, 0 on the horizontal plane (x, y), and the
elevation (e) from the horizontral plane. If we have these then we construct a point (d@e) and we compute its polar coordinates, 
and we get the theta from those polar coordinates.  

We know e, because it is the same as z. 
We need to compute d. 
But we can get d as rho from the x and y coordinates: (x@y).asPolar.rho;

*/

/// Final algorithm
(
f = { | x, y, z |
	var azimuth, elevation;
	var radius;
	radius = (x@y).asPolar.rho;
	elevation = (radius@z).asPolar.theta;  
	/* 	// this can be written more succinctly thus: 
		elevation = (((x@y).asPolar.rho)@z).asPolar.theta;
		// !!!!!
	*/
	
	azimuth = (x@y).asPolar.theta;
	postf("azimuth: %, elevation: %\n", azimuth / pi * 180, elevation / pi * 180);
	[azimuth, elevation] / pi * 180;	
};
)

/* Note 110303: Repeating the explanation of the algorithm above, to make sure that it is correct: 

1. Compute the length of the segment from the origin point 0@0 to the point x@y on the horizontal plane. 
	radius = (x@y).asPolar.rho
2. Switch to the vertical plane. Here radius becomes the x dimension and z becomes the y dimension. 
	Given x (i.e. radius) and y (i.e. z) we now compute the angle from polar coordinates: 
	elevation = (radius@z).asPolar.theta

*/

f.(100, 100, 0.1);
f.(0.100, 0.100, 100);
f.(0.00, 0.00, 100);
f.(0.00, 0.001, 100);
f.(0.001, 0.00, 100);



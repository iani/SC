

/* mapping function for centering images on screen

Based on:

LinearWarp : Warp {
	map { arg value;
		// maps a value from [0..1] to spec range
		^value * spec.range + spec.minval
	}
} 


*/

w = { | centeredX = 0, imgWidth = 100, screenWidth = 1000 |
	(centeredX / 2 + 0.5) * (imgWidth + screenWidth) + imgWidth.neg;
};


w.value(-1);
w.value(0);
w.value(1);

w.value(-1, 200);
w.value(0, 200);
w.value(1, 200);

w.value(-1, 20);
w.value(0, 20);
w.value(1, 20);


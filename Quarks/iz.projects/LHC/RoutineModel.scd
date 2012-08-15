


r = {
	var time = 1;
	loop {
		"read input".postln;
		"calculate integer value from binary input".postln;
		"increment counter by integer value of input + take modulo".postln;
		"display number".postln;
		"add number to ";
		
		
		
		
		// MUCH LATER
		"calculate frequency".postln;
		time.wait;
	}
}.fork;

//:calculate integer value from binary input
var bits = [0, 1, 1];
var number;
number = ([4, 2, 1] * bits).sum;


//:increment counter by integer value of input + take modulo
counter = counter + number % 8; 


//:Calculate frequency

if (counter != 0) {
	[0, 60, 62, 64, 65, 67, 69, 71][number].midicps;
};

//:---


{ 
	loop {
		a = Array.rand(1000, 0, 100);
		1.wait;	
	}
	
}.fork
/* IZ Saturday, May 17, 2008 

*/

var output_scripts;

~controlSpecs = [
[\rate, 0.1, 10, 'linear', 0, 1],
[\rate_range, 0.0, 5, 'linear', 0, 0],
[\loop, 0.0, 1, 'linear', 1, 1],
[\num_channels, 2, 16, 'linear', 1, 16]
];


~script.routine {
	loop {
		~sources.do { | s | 
			s.set(\out, ~channels.choose)
		};
//		[~rate, ~rate_change, ~rate_change.rand].postln;
//		1.wait;
		(~rate + ~rate_range.rand).wait;
	};
}
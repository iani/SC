FFTMagnitudes {
	*new {  | fftbufdata |	
		var real, imaginary;
		#real, imaginary = fftbufdata.clump(2).flop;
		^Complex(Signal.newFrom(real), Signal.newFrom(imaginary)).magnitude;
	}
}
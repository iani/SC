//Wavelets library by Nick Collins for SC3

DWT : UGen
{
	*new { | buffer, in = 0.0 , hop = 0.5, wintype = 0, active=1, winsize=0, wavelettype=0|
		^this.multiNew('control', buffer, in, hop, wintype, active, winsize, wavelettype)
	}
}

IDWT : UGen
{
	*new { | buffer, wintype = 0, winsize=0, wavelettype=0|
		^this.ar(buffer, wintype, winsize, wavelettype)
	}

	*ar { | buffer, wintype = 0, winsize=0, wavelettype=0|
		^this.multiNew('audio', buffer, wintype, winsize, wavelettype);
	}

//	*kr { | buffer, wintype = 0, winsize=0|
//		^this.multiNew('control', buffer, wintype, winsize);
//	}
	
}
PolyPitch : MultiOutUGen {	

	*kr {
		arg input,maxvoices=4,levelcompressionfactor=(-0.1),mixleftterm=4.0,torprec=0.0000001,cancellationweight=1.0,polyphonyestimategamma=0.66;
		^this.multiNew('control',input,maxvoices,levelcompressionfactor,mixleftterm,torprec,cancellationweight,polyphonyestimategamma);
	}
	
	init { arg ... theInputs;
		inputs = theInputs;
		
		//inputs.postln;
		
		^this.initOutputs(2*(theInputs[1])+1, rate);
	}
}

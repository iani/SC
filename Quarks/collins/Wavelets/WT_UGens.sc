//Wavelets library by Nick Collins for SC3

WT_ChainUGen : UGen {
	
	
}

WT_MagAbove : WT_ChainUGen
{
	*new { arg buffer, threshold = 0.0, startindex=1;
		^this.multiNew('control', buffer, threshold, startindex)
	}
}


WT_MagBelow : WT_MagAbove {}
WT_MagClip : WT_MagAbove {}
WT_SoftThreshold : WT_MagAbove {}

//selectively zeroes out certain scales, in a low pass or high pass manner
WT_FilterScale : WT_ChainUGen
{
	*new { arg buffer, wipe;
		^this.multiNew('control', buffer, wipe)
	}
}


WT_TimeWipe : WT_FilterScale {}

WT_Mul : WT_ChainUGen
{
	*new { arg bufferA, bufferB;
		^this.multiNew('control', bufferA, bufferB)
	}
}

WT_Freeze : WT_ChainUGen
{
	*new { arg buffer, freeze = 0.0, scaleselect = 1.0;
		^this.multiNew('control', buffer, freeze, scaleselect)
	}
}

//normal k-rate UGen, no passing on chain
WT_Onset : UGen
{
	*kr { arg buffer, threshold = 0.5, mingap=5, rawodf=0;
		^this.multiNew('control', buffer, threshold, mingap, rawodf)
	}
}


WT_Transient : WT_ChainUGen
{
	*new { arg buffer, branchthreshold = 0.5, prunethreshold = 0.1, mode=0;
		^this.multiNew('control', buffer, branchthreshold, prunethreshold, mode)
	}
}

//k-rate feature derived from summing all coefficent moduli
WT_ModulusSum : UGen
{
	*kr { arg buffer;
		^this.multiNew('control', buffer)
	}
}




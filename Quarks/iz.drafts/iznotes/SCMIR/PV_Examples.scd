//:playbuf
{ PlayBuf.ar(b0.numChannels, b0) };

//:
{
	var source chain;
	source =	PlayBuf.ar(b0.numChannels, b0);
	chain = FFT(LocalBuf(2048), in);
	chain = PV_MagAbove(chain, 310);
Ê Ê 	IFFT(chain);
}


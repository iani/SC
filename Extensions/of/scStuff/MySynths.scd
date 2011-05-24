Udef("pure_clarinet",	
	{
		var in;
		in = In.ar(8);
		Out.ar(0, in);
	}		
);
Udef(\foubuf, {| out = 0, bufnum = 0, rate = 1, trigger = 1, loop = 1, pos = 0, level = 1, windowSize = 0.5, pitchRatio = 1 |
        Out.ar(out,
                Pan2.ar( 
                        PitchShift.ar(                  
                                PlayBuf.ar(1, 
                                        bufnum, 
                                        rate, 
                                        trigger, 
                                        0, 
                                        loop
                                ),
                                windowSize,
                                pitchRatio
                        ),
                        pos,
                        level
                )
        )
});

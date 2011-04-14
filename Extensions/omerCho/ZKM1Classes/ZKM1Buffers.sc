

/*
ZKM1Buffers.load;
*/


ZKM1Buffers {
	*load {
		var s;


		s = Server.default;
		
		~bufA = Buffer.alloc(s, s.sampleRate * 8, 1);
		~bufB = Buffer.alloc(s, s.sampleRate * 8, 1);
		
		~bufIn = Buffer.alloc(s, s.sampleRate * 8, 1);
		~bufTap = Buffer.alloc(s, s.sampleRate * 8, 1);
		
		~bufJer1 = Buffer.alloc(s, s.sampleRate * 8, 1);
		~bufJer2 = Buffer.alloc(s, s.sampleRate * 8, 1);
		
		
		~zil01 = Buffer.read(s, "sounds/zil_01_07.aiff");

/*
		
		~jazz01 = Buffer.read(s, "sounds/zil_01_07.aiff");
		~nefes01 = Buffer.read(s, "sounds/_ZKM/nefes01.aif");
		~nefes02 = Buffer.read(s, "sounds/_ZKM/nefes02.aif");
		~nefes03 = Buffer.read(s, "sounds/_ZKM/nefes03.aif");

*/			
	}
	
	*unLoad { 
		
		~bufA.free;
		~bufB.free;
		
		~bufIn.free;
		~bufTap.free;
		
		~bufJer.free;
		~bufDam.free;

/*
		~jazz01.free;
		~nefes01.free;
		~nefes02.free;
		~nefes03.free;

*/	
	}
	

}
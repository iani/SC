


Ia1Buffers {
	*load {
		var s;


		s = Server.default;
		
		~indbuf = Buffer.read(s, "sounds/_newFort/indust.aif");		~indbuf1 = Buffer.read(s, "sounds/_newFort/indust1.aif");
		~indbuf2 = Buffer.read(s, "sounds/_newFort/indust2.aif");
		~indbuf3 = Buffer.read(s, "sounds/_newFort/indust3.aif");
		~indbufRev1 = Buffer.read(s, "sounds/_newFort/industRev1.aif");
		~indbufRev2 = Buffer.read(s, "sounds/_newFort/industRev2.aif");
		~indbufRev3 = Buffer.read(s, "sounds/_newFort/industRev3.aif");	
	}
	
	*unLoad { 
		~indbuf.free;
		~indbuf1.free;
		~indbuf2.free;
		~indbuf3.free;
		~indbufRev1.free;
		~indbufRev2.free;
		~indbufRev3.free;
	}
	

}
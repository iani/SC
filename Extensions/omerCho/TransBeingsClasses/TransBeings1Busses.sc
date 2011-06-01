

TransBeings1Busses {
	*load {
		~limBus = Bus.new(\audio, 20, 2);
		~revBus = Bus.new(\audio, 22, 2);
		~dlyBus = Bus.new(\audio, 24, 2);
		~rlpBus = Bus.new(\audio, 26, 2);
		~wahBus = Bus.new(\audio, 28, 2);

	}
	
	*unLoad { 
		
		~limBus.free;
		~revBus.free;
		~dlyBus.free;
		~rlpBus.free;
		~wahBus.free;
	}
	

	
	
}
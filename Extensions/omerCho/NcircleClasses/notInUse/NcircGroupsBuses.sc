/*
NcircGroups.load;
*/

NcircGroups {
	*load {
		var s;


		s = Server.default;
		
		~piges = Group.head(s);
		
		/*
		~mainGroup = Group.new; // add a Group at the head of the default Server's default group
		
		
		~ch1 = Group.head(~mainGroup);
			~src1 = Group.head(~ch1);
			~fx1 = Group.tail(~ch1);
		~ch2 = Group.head(~mainGroup);
			~src2 = Group.head(~ch2);
			~fx2 = Group.tail(~ch2);
		~ch3 = Group.head(~mainGroup);
			~src3 = Group.head(~ch3);
			~fx3 = Group.tail(~ch3);
		~ch4 = Group.head(~mainGroup);
			~src4 = Group.head(~ch4);
			~fx4 = Group.tail(~ch4);
		~ch5 = Group.head(~mainGroup);
			~src5 = Group.head(~ch5);
			~fx5 = Group.tail(~ch5);
		~ch6 = Group.head(~mainGroup);
			~src6 = Group.head(~ch6);
			~fx6 = Group.tail(~ch6);
		*/
		//~cleanG = Group.head(~effe);
		//~reverbG = Group.after(~cleanG);
		//~delayG = Group.after(~reverbG);
		//~rlpfG = Group.after(~delayG);
		//~wahG = Group.after(~rlpfG);
		
		//~levelA = Group.new(~piges, \addToTail);
		//~levelB = Group.new(~piges, \addToTail);
		//~levelC = Group.new(~piges, \addToTail);
		
		//~recorders = Group.new(~effe, \addAfter);

	}
	
	*unLoad { 

		~piges.free;
		~effe.free;

	}
	

	
	
}


NcircBusses {
	*load {
		
		
		~mainBus = Bus.new(\audio, 22, 2);
		
		~limBus = Bus.new(\audio, 24, 2);
		~revBus = Bus.new(\audio, 26, 2);
		~dlyBus = Bus.new(\audio, 28, 2);
		~rlpBus = Bus.new(\audio, 30, 2);
		~wahBus = Bus.new(\audio, 32, 2);
		
 		~flowBus = Bus.new(\audio, 34, 2);



	}
	
	*unLoad { 
		
		~limBus.free;
		~revBus.free;
		~dlyBus.free;
		~rlpBus.free;
		~wahBus.free;
	
		~lvlABus.free;
		~lvlBBus.free;
		~lvlCBus.free;
	
	}
	

	
	
}
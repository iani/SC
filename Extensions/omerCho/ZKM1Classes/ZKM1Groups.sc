/*
ZKM1Groups.load;
*/

ZKM1Groups {
	*load {
		var s;


		s = Server.default;
		
		~piges = Group.head(s);
		~effe = Group.tail(s);
		~recorders = Group.new(~effe, \addAfter);

	}
	
	*unLoad { 

		~piges.free;
		~effe.free;
		~recorders.free;		

	}
	

	
	
}
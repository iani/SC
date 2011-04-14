

Ia1Groups {
	*load {
		var s;


		s = Server.default;
		
		~piges = Group.head(s);
		~effe = Group.tail(s);

	}
	
	*unLoad { 

		~piges.free;
		~effe.free;		

	}
	

	
	
}
/* 

Playing an array of BufRd. Each BufRd instance goes to a different channel. 

The amplitude of the output of each channel can be controlled individually by an array of kr signals (controls). 

*/

O.startup;
a = { | bufname = \weddelle |
	var buf, playfunc;
	var sourcegroup, sources;
	buf = (O@bufname).bufnum;
	sourcegroup = Group.new;
	
	playfunc = {  | out |	
		Synth("BRlfn3", (target: sourcegroup, outbus: out);
	};
//	sources = { | i | playfunc.(i) } ! 2;
	sources = playfunc.(_) ! 43;
};


a.value;
a.(\swallowsa);
a.(\dfd);


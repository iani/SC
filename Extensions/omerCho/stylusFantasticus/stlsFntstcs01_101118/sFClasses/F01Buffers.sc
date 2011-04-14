
/* Draft of simple class for buffers for a project 


sF01Buffers.load;

sF01Buffers.at(\indbuf);
sF01Buffers.atN(\buffer1, \buffer2);


~indbuf = Buffer.read(s, "sounds/_newFort/indust.aif");

*/

SF01Buffers {
	classvar <buffers;
	*load {
		var s;
		var b, c, d, g;

		buffers = IdentityDictionary.new;

		s = Server.default;


		buffers[\indbuf] = Buffer.read(s, "sounds/_newFort/indust.aif");
		buffers[\buffer2] = Buffer.read(s, "sounds/me_piano2.aiff");
		buffers[\buffer3] = Buffer.read(s, "sounds/me_piano3.aiff");
		buffers[\buffer4] = Buffer.read(s, "sounds/me_piano4.aiff");

	}
	
	*at { | name |
		
		^buffers.at(name.asSymbol);
	}
	
	*atN { | ... names |
		^names collect: { | name | buffers.at(name.asSymbol); };
	}
	
	
}
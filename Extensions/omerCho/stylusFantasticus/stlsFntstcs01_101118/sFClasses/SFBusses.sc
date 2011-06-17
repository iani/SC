
/* Draft of simple class for buffers for a project 


sF01Buffers.load;

sF01Buffers.at(\buffer1);
sF01Buffers.at(\buffer1, \buffer2);


*/

SF01Busses {
	classvar <busses;
	*load {
		var s;

		busses = IdentityDictionary.new;

		s = Server.default;
		
		busses[\limBus] = Bus.new(\audio, 20, 2);
		busses[\revBus] = Bus.new(\audio, 22, 2);
		busses[\dlyBus] = Bus.new(\audio, 24, 2);
		busses[\rlpBus] = Bus.new(\audio, 26, 2);
		busses[\wahBus] = Bus.new(\audio, 28, 2);

	}
	
	*at { | name |
		
		^busses.at(name.asSymbol);
	}
	
	*atN { | ... names |
		^names collect: { | name | busses.at(name.asSymbol); };
	}
	
	
}
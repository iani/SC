/*
2600 atoms
*/
d = DOMDocument("/Users/ari/Dropbox/orgshared/sites/org/extending/projects/Stratos-Bichakis/media/3SPX.xml");
~xPos = d.getElementsByTagName ("PDBx:Cartn_x")
~yPos = d.getElementsByTagName ("PDBx:Cartn_y")
~zPos = d.getElementsByTagName ("PDBx:Cartn_z")
~bIso = d.getElementsByTagName ("PDBx:B_iso_or_equiv")
~type_symbol = d.getElementsByTagName ("PDBx:type_symbol");
n = NetAddr("127.0.0.1", 12345);
n.sendMsg("atomID", x[1].getText.asFloat, y[1].getText.asFloat, z[2].getText.asFloat )
//:-	
{
	2600.do{|i|
		n.sendMsg(
			"atomID", 
			i.asInt, 
			~xPos[i].getText.asFloat,
			~yPos[i].getText.asFloat,
			~zPos[i].getText.asFloat,
			~bIso[i].getText.asFloat,
			~type_symbol[i].getText.asString.postln
			);			
		//i.postln;
		0.05.wait;
	}
}.fork
//:-
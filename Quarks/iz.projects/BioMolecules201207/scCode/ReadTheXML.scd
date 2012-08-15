d = DOMDocument("/Users/ari/Dropbox/orgshared/sites/org/extending/projects/Stratos-Bichakis/media/3SPX.xml");
x = d.getElementsByTagName ("PDBx:Cartn_x")
y = d.getElementsByTagName ("PDBx:Cartn_y")
z = d.getElementsByTagName ("PDBx:Cartn_z")
e[1].getText
n = NetAddr("127.0.0.1", 12345);
n.sendMsg("atomID", x[1].getText.asFloat, y[1].getText.asFloat, z[2].getText.asFloat )


50.do(|i|  {
	n.sendMsg("atomID", x[i].getText.asFloat, y[i].getText.asFloat, z[i].getText.asFloat )
	})
	
	50.do(|i|  {
	i.postln
	})
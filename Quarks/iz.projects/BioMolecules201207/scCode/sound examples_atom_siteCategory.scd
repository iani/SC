// Store the document in variable d
d = DOMDocument("/Users/stratosbichakis/Documents/Extending/Bio mol/3SPX.xml");

// Access the atom_siteCategory
a = d.getElementsByTagName ("PDBx:atom_site")
a[2].getAttribute("id")
a.size
b = a[1].getAttributeNode("id")
b.getNodeValue


e = d.getElementsByTagName ("PDBx:Cartn_x")
e[0].getText
e[0].getNodeName

/*
2600 atoms
*/
d = DOMDocument("/Users/stratosbichakis/of_0071_osx_release/apps/myApps/BIOCHEMICAL-molecule-Project/3SPX.xml");
~xPos = d.getElementsByTagName ("PDBx:Cartn_x")
~yPos = d.getElementsByTagName ("PDBx:Cartn_y")
~zPos = d.getElementsByTagName ("PDBx:Cartn_z")
~bIso = d.getElementsByTagName ("PDBx:B_iso_or_equiv");
~typeSymbol = d.getElementsByTagName ("PDBx:type_symbol");
~seqId = d.getElementsByTagName ("PDBx:label_seq_id")
~compId = d.getElementsByTagName ("PDBx:label_comp_id")


(
SynthDef(\saw, {|amp = 0.1, pan =0, freq = 400|
	Out.ar(0, Pan2.ar(
						LFTri.ar(freq, 
						0, 
						amp*EnvGen.kr(
								Env.perc(amp/100, amp, 1, 4), doneAction: 2)),pan))
}).send(s)
)

n = NetAddr("127.0.0.1", 12345);

(
{
	
	2400.do{|i|
		var amp, pan, freq, atomId, dist, seqId, compId;
		
		n.sendMsg(
			"atomID", 
			i.asInt, 
			~xPos[i].getText.asFloat,
			~yPos[i].getText.asFloat,
			~zPos[i].getText.asFloat,
			~bIso[i].getText.asFloat,
			~typeSymbol[i].getText.asString.postln,
			~seqId[i].getText.asInteger,
			~compId[i].getText.asString
			);		
			
		
		compId = ~compId[i].getText.asString.postln;
		seqId = ~seqId[i].getText.asInteger.postln;
		atomId = ~typeSymbol[i].getText.asString.postln;
		
		
		switch (atomId,
Ê Ê Ê Ê "N", {freq = [802, 800]},
Ê Ê Ê Ê "C", {freq = [200, 202]},
		"O", {freq = [600, 603]},
		"S", {freq = 85},
		"P", {freq = 60},
		"CL", {freq = 100},
		"NA", {freq = 4000},
Ê Ê Ê Ê {freq = 4000});

		amp = ~bIso[i].getText.asFloat/100;
		pan = ~xPos[i].getText.asFloat-200/1000;

		Synth(\saw, [\amp, amp, \pan, pan, \freq, freq]);
					
		dist = (sqrt(
			pow((~xPos[i+1].getText.asFloat	- ~xPos[i].getText.asFloat),2) +
			pow((~yPos[i+1].getText.asFloat	- ~yPos[i].getText.asFloat),2) + 
			pow((~zPos[i+1].getText.asFloat	- ~zPos[i].getText.asFloat),2)));
		dist = dist/100;
		dist.wait;
		
		
	}
}.fork
)



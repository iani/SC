// Store the document in variable d
d = DOMDocument("/Users/iani2/Dev/SC-supplement/BIOCHEMICAL-molecule-Project/3SPX.xml");

// Access the atom_siteCategory
a = d.getElementsByTagName ("PDBx:atom_site")
a[2].getAttribute("id")
a.size
a[1].hasAttribute("id")
a[1].getAttributeNode("id").getNodeValue


e = d.getElementsByTagName ("PDBx:Cartn_x")
e[0].getText
e[0].getNodeName

/*
2600 atoms
*/
d = DOMDocument("/Users/stratosbichakis/of_0071_osx_release/apps/myApps/BIOCHEMICAL-molecule-Project/3SPX.xml");

// ola ta atoma 1 pros 1:
~atomSiteCat = d.getElementsByTagName ("PDBx:atom_site");

// syntetagmenes atomon
~xPos = d.getElementsByTagName ("PDBx:Cartn_x");
~yPos = d.getElementsByTagName ("PDBx:Cartn_y");
~zPos = d.getElementsByTagName ("PDBx:Cartn_z");

// displacement of atoms
~bIso = d.getElementsByTagName ("PDBx:B_iso_or_equiv");

// which kind of atom
~typeSymbol = d.getElementsByTagName ("PDBx:type_symbol");
~seqId = d.getElementsByTagName ("PDBx:label_seq_id");
~compId = d.getElementsByTagName ("PDBx:label_comp_id");
~atomId = d.getElementsByTagName ("PDBx:label_atom_id"); 
~sheetCat = d.getElementsByTagName("PDBx:struct_sheet_range")
~sheetBeg = ~sheetCat[2].getElement ("PDBx:beg_label_seq_id").getText;
~sheetEnd = d.getElementsByTagName ("PDBx:end_label_seq_id");


// 

(
j=0;
{
	~atomSiteCat.size.do{|i|
		var amp, pan, freq, atomId, dist, seqId, compId, atomSymbol, atomSiteId, secStructBeg, secStructEnd;
		
		atomSiteId = ~atomSiteCat[i].getAttribute("id").asInteger;
		atomId = ~atomId[i].getText.asString;
		compId = ~compId[i].getText.asString;
		seqId = ~seqId[i].getText.asInteger;
		atomSymbol = ~typeSymbol[i].getText.asString;
		secStructBeg = ~secStructBeg[j].getText.asInteger;
		secStructEnd = ~secStructEnd[j].getText.asInteger;
	
		
		
	// Determine the beginning of a new amino acid 
		
		if( i==0 ,{seqId.postln; compId.postln; atomId.postln;},{
					if (seqId == ~seqId[i-1].getText.asInteger, 
					{atomId.postln;}, 
					{							
						// Determine the secondary structure
	
	if( seqId == ~secStructBeg[j], 
				{	
					switch (~secStructBeg[j].getParentNode.getNodeName.asString,
				Ê Ê Ê Ê"PDBx:struct_sheet_range", {"SheetBeginning".postln;},
					Ê Ê "PDBx:struct_conf", {"HellixBeginning".postln;}
						);
							
					},
			);
		
	if( secStructEnd == seqId,
				{"secstructEND".postln; j=j+1;},
			);
	

						seqId.postln; compId.postln; atomId.postln});
		};
		);
		
	// Link atomSymbol with a frequency
		
		switch (atomId,
Ê Ê Ê Ê"N", {freq = 800},
	Ê Ê "C", {freq = 200},
		"O", {freq = 600},
		"S", {freq = 85},
		"P", {freq = 60},
		"CL", {freq = 100},
		"NA", {freq = 4000},
Ê Ê Ê Ê {freq = 2000});

		amp = ~bIso[i].getText.asFloat/100;
		pan = ~xPos[i].getText.asFloat-200/1000;

		
		
		// Calculate the distance between two atoms			
		dist = (sqrt(
			pow((~xPos[i+1].getText.asFloat	- ~xPos[i].getText.asFloat),2) +
			pow((~yPos[i+1].getText.asFloat	- ~yPos[i].getText.asFloat),2) + 
			pow((~zPos[i+1].getText.asFloat	- ~zPos[i].getText.asFloat),2)));
		dist = dist/1000;
		dist.wait;
		
		
	}
}.fork
)


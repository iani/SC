CurveFile {
	
	//========================
	var <>fileTransition;
	var <>idFile = 0;
	var <>path;
	var fileW, fileR;
	
	//========================
	*new {
		arg fileTransition;
		^super.newCopyArgs(fileTransition).init;
	}
	
	init {
		this.fileTransition = [];
	}
	
	//========================
	write {	
		fileW = File(path, "w");
		fileTransition.do({
			arg courbe;
			fileW.write("["); 
			(courbe.size - 1).do({
				arg i;
				fileW.write(courbe.at(i).asString ++ ",");});
			fileW.write(courbe.last.asString ++ "];" ++ "\n"); 
		});
		fileW.close;
	}		
	
	enregist {
		arg liVal;
		var li = [];
		liVal.do({
			arg ptS;
			li = li.add(ptS.collect);});
		fileTransition = fileTransition.add(li);
		idFile = fileTransition.size - 1;
		this.write;
	}
	
	modif {
		arg liVal;
		var li = [];
		liVal.do({
			arg ptS;
			li = li.add(ptS.collect);});
		fileTransition = fileTransition.put(idFile, li);
		this.write;
	}

	effacer {
		fileTransition.removeAt(idFile);
		this.write;
	}
	
	//========================
	load {
		arg ca;
		path = ca;
		fileTransition = [];
		fileR = FileReader.readInterpret(path, true, true, delimiter: $;);
		fileR.do({
			arg fichier; 
			fichier.do({
				arg courbe; 
				if (courbe != nil)
					{fileTransition = fileTransition.add(courbe)};});
		});
	}

	rappel {
		arg id;
		var liVal = SortedList(4, {arg a, b; a.x < b.x});
		this.load(path);// Pas trs ŽlŽgant...
		idFile = id;
		fileTransition.at(idFile).do({
			arg item; 
		liVal = liVal.add(item.asPtS);
		});
		^liVal;
	}
	
	//============
	takeY {
		arg id;
		var ref = fileTransition.at(id).at(0).at(1);
		var bufferPara = [];
		this.load(path);// Pas trs ŽlŽgant...
		fileTransition.at(id).do({
			arg item;
			bufferPara = bufferPara.add(item.at(1) - ref);
		});
		^bufferPara;	
	}

}

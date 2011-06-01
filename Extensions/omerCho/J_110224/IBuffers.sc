IBuffers : List {
	
	*new {
		^super.new;
	}
	
	//========================
	loadDialog {
		Dialog.getPaths({
			arg paths;
			paths.do({
				arg path;
				this.load(path);});})
	}
	
	load {
		arg path;
		var sf = Dictionary.new;
		sf.add(\Buffer -> Buffer.read(Server.default, path));
		sf.add(\Path -> path);
		path = path.basename.splitext.first;
		sf.add(\Nom -> path);
		path = path.split($-);
		sf.add(\Famille -> path.removeAt(0));
		sf.add(\Declinaison -> path.pop);
		path.do({
			arg a;
			var paraval = a.split($_);
			sf.add(paraval.at(0).asSymbol -> paraval.at(1).asFloat);});
		this.add(sf);
	}
	
	clear {
		this.do({arg dico; dico.at(\Buffer).free;});
		this.removeAllSuchThat({arg item; item != nil;});
	}
	
	//========================
	findClosest {
		arg liParaVal;
		var liSF = this.copy;
		liParaVal.pairsDo({
			arg para, val;
			if (val.class == String) {
				liSF = this.testSymb(liSF, para, val);	
			}{
				liSF = this.testNbr(liSF, para, val);
			};
		});
		^liSF;
	}
	
	testSymb {
		arg liSF, para, val;
		liSF.takeThese({
			arg dico;
			dico.at(para) != val;});
		^liSF;
	}

	testNbr {
		arg liSF, para, val;
		var liDist, minDist, liId = [];
		liDist = liSF.collect({
				arg dico;
				dico.at(para).dist(val);});
		minDist = liDist.minItem;
		liDist.do({
			arg dist, id;
			if (dist == minDist) {
				liId = liId.add(id);};});
		^liSF = liId.collect({arg id; liSF.at(id);});
	}

}
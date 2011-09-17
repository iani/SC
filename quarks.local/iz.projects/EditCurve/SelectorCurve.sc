SelectorCurve {
	
	//============
	var <>liCurve, <>liParaCurveGUI, <>liCurveFile;
	var <>idCurve = 0;
	var <>fileSeries;
	
	*new {
		arg liCurve, liParaCurveGUI, liCurveFile;
		^this.newCopyArgs(liCurve, liParaCurveGUI, liCurveFile).init;
	}
	
	init {
		liCurve = liCurve ?? {List.new};
		liParaCurveGUI = liParaCurveGUI ?? {List.new};
		liCurveFile = liCurveFile ?? {List.new};
	}
			
	//============
	doAndNotify { 
		arg action ... args;
		action.(this, *args);
		NotificationCenter.notify(this, \changed);
	}
	
	//============
	addFileSeries {
		arg path;
		this.fileSeries = CurveFile.new.load(path);
	}
	
	//============
	addCurve {
		this.doAndNotify({
			this.liCurve.add(Curve.new);
			this.liParaCurveGUI.add(ParaCurveGUI.new);
			this.liCurveFile.add(CurveFile.new);
		});
	}
	
	//============
	// Selection de l'index
	idC {
		arg id;
		this.idCurve = id;
	}
	
	//============
	// Gestion de liCurve
	addPt {
		arg ptS;
		this.doAndNotify({
			this.liCurve.at(idCurve).addPt(ptS);
			this.liParaCurveGUI.at(idCurve).freePtS;});
	}
	
	movePt {
		arg idPt;
		this.doAndNotify({
			^this.liCurve.at(idCurve).removePt(idPt);});
	}
	
	removePt {
		arg idPt;
		this.doAndNotify({
			this.liCurve.at(idCurve).removePt(idPt);
			this.liParaCurveGUI.at(idCurve).freePtS;});
	}
	
	clearCurve {
		this.doAndNotify({
			this.liCurve.at(idCurve).clearCurve;});
	}
	
	modifPtS {
		arg idPt, para, newVal;
		this.doAndNotify({
			if (para == \idLoop)
				{this.liCurve.at(idCurve).modifPtS(
					idPt, \curveLoop, this.fileSeries.takeY(newVal));
				}{
				this.liCurve.at(idCurve).modifPtS(idPt, para, newVal);};});
	}
	
	convert {
		this.doAndNotify({
			this.liCurve.at(idCurve).convert;})
	}
	
	normX {
		this.doAndNotify({
			this.liCurve.at(idCurve).normX(
				this.liParaCurveGUI.at(idCurve).paraGUI.at(\eMinX), 
				this.liParaCurveGUI.at(idCurve).paraGUI.at(\eMaxX));});
	}
	
	normY {
		this.doAndNotify({
			this.liCurve.at(idCurve).normY(
				this.liParaCurveGUI.at(idCurve).paraGUI.at(\eMinY), 
				this.liParaCurveGUI.at(idCurve).paraGUI.at(\eMaxY));});
	}
	
	calculPts {
			this.liCurve.at(idCurve).calculPts;
	}
	
	liVal {
		^this.liCurve.at(idCurve).liVal;
	}
	
	envX {
		^this.liCurve.at(idCurve).envX;
	}

	envY {
		^this.liCurve.at(idCurve).envY;
	}

	
	//============
	// Gestion de liParaCurveGUI
	editParaGUI {
		arg liParaGUI;
		this.doAndNotify({
			this.liParaCurveGUI.at(idCurve).editParaGUI(liParaGUI);});
	}
	
	editParaGUIIter {
		arg liParaGUI;
		this.doAndNotify({
			this.liParaCurveGUI.do({
				arg paraCurveGUI;
				paraCurveGUI.editParaGUI(liParaGUI);});
		});
	}
	
	valToCoord {
		arg pt;
		^this.liParaCurveGUI.at(idCurve).valToCoord(pt);
	}
		
	coordsPtS {
		^this.liParaCurveGUI.at(idCurve).coordsPtS(this.liCurve.at(idCurve).liVal);
	}
	
	coordToVal {
		arg pt;
		^this.liParaCurveGUI.at(idCurve).coordToVal(pt);
	}
	
	detectIdPt {
		arg x;
		^this.liParaCurveGUI.at(idCurve).detectIdPt(x, this.liCurve.at(idCurve).liVal);
	}
	
	detectIdSeg {
		arg x;
		^this.liParaCurveGUI.at(idCurve).detectIdSeg(x, this.liCurve.at(idCurve).liVal);
	}
	
	editPt {
		arg gate, x, y, p1, p2, p3, p4, p5, p6, p7;
		this.doAndNotify({
			this.liParaCurveGUI.at(idCurve).editPt(gate, x, y, p1, p2, p3, p4, p5, p6, p7);});
	}
	
	formatGUI {	
		this.doAndNotify({
			^this.liParaCurveGUI.at(idCurve).formatGUI(this.liCurve.at(idCurve).liVal);});
	}
	
	paraGUI {
		^this.liParaCurveGUI.at(idCurve).paraGUI;
	}
	
	//============
	// Gestion de liCurveFile
	load {
		arg path;
		this.liCurveFile.at(idCurve).load(path);
	}
	
	enregist {
		^this.liCurveFile.at(idCurve).enregist(this.liCurve.at(idCurve).liVal);
	}
	
	modif {
		this.liCurveFile.at(idCurve).modif(this.liCurve.at(idCurve).liVal);
	}
	
	rappel {
		arg idFile;
		this.doAndNotify({
			this.liCurve.at(idCurve).liVal = this.liCurveFile.at(idCurve).rappel(idFile);});
	}
	
	effacer {
		this.liCurveFile.at(idCurve).effacer;
		this.clearCurve;
	}
	
	idFile {
		^this.liCurveFile.at(idCurve).idFile;
	}
	
	//============
	mouseDown {
		arg x, y, fonctionMouse;
		var idPt;
		var idSeg;
		var p1, p2;
		var s;
		fonctionMouse.switch(
			\xy, 
				{idPt = this.detectIdPt(x);
				if (idPt != nil) 
					{s = this.movePt(idPt); 
					this.editPt(2, s.x, s.y, s.p1, s.p2, s.p3, s.p4, s.p5, s.p6, s.p7);
					}{
					this.editPt(1, x, y, 
						0, 4, 0, 0, this.fileSeries.takeY(0), 0, 1);};},
			\removePt, 
				{idPt = this.detectIdPt(x); this.removePt(idPt);},
			\type, 
				{idSeg = this.detectIdSeg(x);
				this.modifPtS(idSeg, \type, nil);},
			\netap, 
				{idSeg = this.detectIdSeg(x);
				this.modifPtS(
				idSeg, \netap, 
				(this.liParaCurveGUI.at(idCurve).paraGUI.at(\eSizeY) - y).round(2).asInteger;);},
			\coefXY, 
				{idSeg = this.detectIdSeg(x);
				p1 = this.coordsPtS.at(idSeg);
				p2 = this.coordsPtS.at(idSeg + 1);
				this.modifPtS(
					idSeg, \coefX,
					2  - (x - p1.x - (p2.x - p1.x / 2) / (p2.x - p1.x / 2) * 2) - 2);
				this.modifPtS(
					idSeg,
					\coefY,
					y - (this.liParaCurveGUI.at(idCurve).paraGUI.at(\eSizeY) / 2)
						/ (this.liParaCurveGUI.at(idCurve).paraGUI.at(\eSizeY) / 2) * 2);}
		);
	}
	
	mouseMove {
		arg x, y, fonctionMouse;
		var idSeg;
		var p1;
		var p2;
		var xy = this.coordToVal(x@y);
		fonctionMouse.switch(
			\xy, 
				{this.editPt(0, x, y);},
			\netap, 
				{idSeg = this.detectIdSeg(x);
				this.modifPtS(
				idSeg, \netap, 
				(this.liParaCurveGUI.at(idCurve).paraGUI.at(\eSizeY) - y).round(2).asInteger;);},
			\coefXY, 
				{idSeg = this.detectIdSeg(x);
				p1 = this.coordsPtS.at(idSeg);
				p2 = this.coordsPtS.at(idSeg + 1);
				this.modifPtS(
					idSeg, \coefX,
					2  - (x - p1.x - (p2.x - p1.x / 2) / (p2.x - p1.x / 2) * 2) - 2);
				this.modifPtS(
					idSeg,
					\coefY,
					y - (this.liParaCurveGUI.at(idCurve).paraGUI.at(\eSizeY) / 2)
						/ (this.liParaCurveGUI.at(idCurve).paraGUI.at(\eSizeY) / 2) * 2);}
		);
	}
	
	mouseUp {
		arg fonctionMouse;
		if (fonctionMouse == \xy) 
			{this.addPt(this.liParaCurveGUI.at(idCurve).ptS);};
	}
	
	//============
	vue {	
		arg userView;
		NotificationCenter.register(this, \changed, userView, {userView.refresh});
	}

}
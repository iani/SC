ParaCurveGUI {
	// Variables
	var <>paraGUI, <>ptS = nil;
	
	// Appel
	*new {
		arg paraGUI;
		^this.newCopyArgs(paraGUI).init;
	}
	
	init {
		paraGUI = Dictionary.new;
	}
	
	// Edition des parametres d affichage
	editParaGUI {
		arg liParaGUI;
		liParaGUI.pairsDo({
			arg key, val;
			this.paraGUI.add(key -> val);
		});
	}
	
	// Conversions
	valToCoord {
		arg pt;
		var eMinX = this.paraGUI.at(\eMinX);
		var eMaxX = this.paraGUI.at(\eMaxX);
		var eSizeX = this.paraGUI.at(\eSizeX);
		var eMinY = this.paraGUI.at(\eMinY);
		var eMaxY = this.paraGUI.at(\eMaxY);
		var eSizeY = this.paraGUI.at(\eSizeY);
		^pt.x - eMinX * (eSizeX / (eMaxX - eMinX))@
			(eSizeY - ((pt.y - eMinY) * (eSizeY / (eMaxY - eMinY))));
	}
	
	coordsPtS {
		arg liVal;
		^liVal.collect({
			arg ptS;
			this.valToCoord(ptS);})
	}
	
	coordToVal {
		arg pt;
		var eMinX = this.paraGUI.at(\eMinX);
		var eMaxX = this.paraGUI.at(\eMaxX);
		var eSizeX = this.paraGUI.at(\eSizeX);
		var eMinY = this.paraGUI.at(\eMinY);
		var eMaxY = this.paraGUI.at(\eMaxY);
		var eSizeY = this.paraGUI.at(\eSizeY);
		var grilleX = this.paraGUI.at(\grilleX);
		var grilleY = this.paraGUI.at(\grilleY);		
		^(pt.x.bornes(0, eSizeX) / eSizeX * (eMaxX - eMinX) + eMinX).round(grilleX)@
			(eSizeY - pt.y.bornes(0, eSizeY) / eSizeY * (eMaxY - eMinY) + eMinY).round(grilleY);
	}
	
	// Formatage pour CurveGUI
	formatGUI {
		arg liVal;
		var ptsInterpo;
		var result;
		var liVals = SortedList.new(4, {arg a, b; a.x < b.x;});
		liVal.do({arg ptS; liVals = liVals.add(ptS);});
		if (this.ptS != nil) {liVals = liVals.add(this.ptS);};
		liVals.do({
			arg ptS, i;
			result = result.add(this.formatPtSGUI(ptS, 1, 5));
			if (i < (liVals.size - 1))
				{ptsInterpo =
					case	
						{ptS.p1 == 1}{Seg.new(ptS, liVals.at(i + 1));}
						{ptS.p1 == 2}{Carre.new(ptS, liVals.at(i + 1));}
						{ptS.p1 == 3}{Scie.new(ptS, liVals.at(i + 1));}						{ptS.p1 == 4}{Iter.new(ptS, liVals.at(i + 1));}						{ptS.p1 == 5}{Gliss.new(ptS, liVals.at(i + 1));}
					 	{ptS.p1 == 6}{Loop.new(ptS, liVals.at(i + 1));};
			ptsInterpo.do({
				arg pt;
				result = result.add(this.formatPtSGUI(pt, 0, 3));});
				};
		});
		^result;
	}

	formatPtSGUI {
		arg pt, gate, sizePt;
		var xy, val;
		xy = this.valToCoord(pt);
		if (gate == 1)
			{this.paraGUI.at(\affVal).switch(
				0, {val = pt.x.round(0.001)},
				1, {val = pt.y.round(0.001)},
				2, {val = pt.y.asNoteOct});
			}{
			val = nil};
		^PtS(xy.x, xy.y, val, sizePt);
	}
	
	// Detection pour l edition a la souris
	detectIdPt {
		arg x, liVal;
		var coordsPtS = this.coordsPtS(liVal);
		^coordsPtS.detectIndex({
			arg pt;
			(x > (pt.x - 5)) && (x < (pt.x + 5));});
	}
	
	detectIdSeg {
		arg x, liVal;
		var coordsPtS = this.coordsPtS(liVal);
		^coordsPtS.detectIndex({
			arg pt;
			(x < pt.x);}) - 1;
	}
	
	// Edition a la souris
	editPt {
		arg gate, x, y, p1, p2, p3, p4, p5, p6, p7;
		var xy = this.coordToVal(x@y);
		case
			{gate == 2}{this.ptS = PtS.new(x, y, p1, p2, p3, p4, p5, p6, p7);}
			{gate == 1}{this.ptS = PtS.new(xy.x, xy.y, p1, p2, p3, p4, p5, p6, p7);}
			{gate == 0}{this.ptS.changePt(this.paraGUI.at(\lockX), xy);};
	}
	
	freePtS {
		this.ptS = nil;
	}
	
}

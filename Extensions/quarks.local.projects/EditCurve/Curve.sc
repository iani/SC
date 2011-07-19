Curve {
	//============
	// Declaration des variables
	var <>liVal;
	var <>envX, <>envY, <>tsLesPts;
	var <>id;
	
	//============
	// Initialisation	
	*new {
		arg liVal;
			^this.newCopyArgs(liVal).init;
	}
	
	init {
		liVal = liVal ?? {SortedList.new(4, {arg a, b; a.x < b.x});};
	}
	
	//============
	// Edition de liVal
	// Edition standard des points
	addPt {
		arg ptS;
		this.liVal.add(ptS);
		id = this.liVal.detectIndex({arg pt; pt.x == ptS.x;});
	}

	removePt {
		arg idPt;
		^this.liVal.removeAt(idPt);
	}
	
	clearCurve {
		this.liVal.free;
		this.liVal = SortedList.new(4, {arg a, b; a.x < b.x});
	}
		
	// Association entre les PtS et les fonctions speciales
	modifPtS {
		arg idPt, para, newVal;
		if (idPt != nil) {id = idPt;};
		para.switch(
			\x,			{this.liVal.at(id).x = newVal;},
			\y,			{this.liVal.at(id).y = newVal;},
			\type, 		{if (newVal == nil) 
							{this.liVal.at(id).p1 = this.liVal.at(idPt).p1 + 1 % 7;
							}{
							this.liVal.at(id).p1 = newVal;}},
			\netap, 		{this.liVal.at(id).p2 = newVal;},
			\coefX,		{this.liVal.at(id).p3 = newVal;},
			\coefY,		{this.liVal.at(id).p4 = newVal;},
			\curveLoop,	{
						this.liVal.at(id).p5 = newVal;
						this.liVal.at(id).p6 = 0;
						this.liVal.at(id).p7 = (newVal.size - 1);
						this.liVal.at(id).p2 = (newVal.size + 1);
						},
			\idStartLoop, {this.liVal.at(id).p6 = newVal;},
			\idEndLoop, 	{this.liVal.at(id).p7 = newVal;}
		);
	}
		
	// Calcul de tous les points sous entendues par les fonctions speciales
	calculPts {
		var ptsInterpo;
		var size = liVal.size;
		this.tsLesPts = List.new;
		this.liVal.do({
			arg pts, i;
			this.tsLesPts = this.tsLesPts.add(pts.x@pts.y);
			if (i < (size - 1))
			{ptsInterpo =
				case
					{pts.p1 == 1}{Seg.new(pts, this.liVal.at(i + 1));}
					{pts.p1 == 2}{Carre.new(pts, this.liVal.at(i + 1));}
					{pts.p1 == 3}{Scie.new(pts, this.liVal.at(i + 1));}					{pts.p1 == 4}{Iter.new(pts, this.liVal.at(i + 1));}					{pts.p1 == 5}{Gliss.new(pts, this.liVal.at(i + 1));}
				 	{pts.p1 == 6}{Loop.new(pts, this.liVal.at(i + 1));};
			ptsInterpo.do({
				arg pt;
				this.tsLesPts = this.tsLesPts.add(pt);
			});};
		});
		this.envX = this.tsLesPts.collect({arg pt; pt.x;}).asList;
		this.envY = this.tsLesPts.collect({arg pt; pt.y;}).asList;
	}
	
	convert {
		this.calculPts;
		this.clearCurve;
		this.tsLesPts.do({
			arg pt;
			this.addPt(PtS(pt.x, pt.y, 0, 4, 0, 0, 0, 0, 1));
		})
	}

	// Normalisations
	normX {
		arg minX, maxX;
		var lix;
			lix = this.liVal.collect({arg pt; pt.x;});
			lix = lix.normalize(minX, maxX);
			lix.do({
				arg x, i;
				this.liVal.at(i).x = x;});
	}
	
	normY {
		arg minY, maxY;
		var liy;
			liy = this.liVal.collect({arg pt; pt.y;});
			liy = liy.normalize(minY, maxY);
			liy.do({
				arg y, i;
				this.liVal.at(i).y = y;});
	}

}
				
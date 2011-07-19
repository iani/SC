CurveGUI {
	// Declaration des variables
	var selectorCurve, nom, left, bottom, longueur, hauteur;
	var windowPara,
		bSelCurve, bShowHide, bStore, nIdConf,
		nEMinX, nEMaxX, 
		nEMinY, nEMaxY, 
		bAffVal, 
		nValX, nValY, nIdPt, 
		nGrilleX, nGrilleY, 
		bClear, 
		bNormX, bNormY, 
		bConvert,
		nINetap, bIType, nICoefX, nICoefY,
		bEnregist, bModif, bEffac, nIdFile, 
		nIdLoop, nIdStartLoop, nIdEndLoop, bLockX;
	var windowEditeur, editeur;
	var idCurve = 0, fonctionMouse = \xy;
	
	// Appel
	*new {
		arg selectorCurve, nom, left = 0, bottom = 0, longueur = 1186, hauteur = 260;
		^this.newCopyArgs(selectorCurve, nom, left, bottom, longueur, hauteur).init;
	}
	
	init {
		selectorCurve = selectorCurve ?? {SelectorCurve.new};
	
	// Fenetre para
		windowPara = Window(nom ++ "_Para", Rect(left, bottom + hauteur + 20 , 704, 126), 0);
		windowPara.view.background_(Color.black);
		windowPara.view.decorator = FlowLayout(windowPara.view.bounds, 10@10, 2@4);
				
	// Number boxes, boutons et titres
		//============
		bShowHide = Button(windowPara, Rect(0, 0, 102, 20));
		bShowHide.states_([
			["Show"],
			["Hide"]
		]);
		bShowHide.value_(
			selectorCurve.liParaCurveGUI.at(idCurve).paraGUI.at(\showHide));
		bShowHide.action_({
			arg a;
			selectorCurve.editParaGUI([\showHide, a.value])
		});
		
		bSelCurve = Button(windowPara, Rect(0, 0, 466, 20));
		bSelCurve.states_(
			selectorCurve.liParaCurveGUI.collect({
				arg paraCurveGUI;
				[paraCurveGUI.paraGUI.at(\nom), Color.black, paraCurveGUI.paraGUI.at(\couleur)];
			});
		);
		bSelCurve.action_({
			arg etat;
			idCurve = etat.value;
			selectorCurve.idC(etat.value);
			bShowHide.value = selectorCurve.liParaCurveGUI.at(idCurve).paraGUI.at(\showHide);
			nEMinX.value = selectorCurve.liParaCurveGUI.at(idCurve).paraGUI.at(\eMinX);
			nEMaxX.value = selectorCurve.liParaCurveGUI.at(idCurve).paraGUI.at(\eMaxX); 
			nEMinY.value = selectorCurve.liParaCurveGUI.at(idCurve).paraGUI.at(\eMinY); 
			nEMaxY.value = selectorCurve.liParaCurveGUI.at(idCurve).paraGUI.at(\eMaxY); 
			nGrilleX.value = selectorCurve.liParaCurveGUI.at(idCurve).paraGUI.at(\grilleX); 
			nGrilleY.value = selectorCurve.liParaCurveGUI.at(idCurve).paraGUI.at(\grilleY); 
			bAffVal.value = selectorCurve.liParaCurveGUI.at(idCurve).paraGUI.at(\affVal);
			bLockX.value = selectorCurve.liParaCurveGUI.at(idCurve).paraGUI.at(\lockX);
			nIdFile.value = selectorCurve.liCurveFile.at(idCurve).idFile;
		});
		
		bClear = Button(windowPara, Rect(0, 0, 102, 20));
		bClear.states_([["Clear"]]);
		bClear.action_({selectorCurve.clearCurve;});
	
		//============
		windowPara.view.decorator.nextLine;
		Titre.new(windowPara, "AffVal", 50);
		
		Titre.new(windowPara, "Zoom X");
		
		Titre.new(windowPara, "Zoom Y");
		
		Titre.new(windowPara, "Normalisation");
		
		Titre.new(windowPara, "Grille");
		
		Titre.new(windowPara, "Enregistrement", 214);
		
		//============		
		windowPara.view.decorator.nextLine;	
		bAffVal = Button(windowPara, Rect(0, 0, 50, 20));
		bAffVal.states_([
			["x"], 
			["y"], 
			["Notes"], 
			["Off"]
		]);
		bAffVal.value_(selectorCurve.liParaCurveGUI.at(idCurve).paraGUI.at(\affVal));
		bAffVal.action_({
			arg val; 
			selectorCurve.editParaGUI([\affVal, val.value]);
		});
		
		nEMinX = NumberBox(windowPara, Rect(0, 0, 50, 20));
		nEMinX.step_(0.1)
			.scroll_step_(0.1).background_(Color.grey).setProperty(\align, \center);
		nEMinX.setProperty(\stringColor, Color.black);
		nEMinX.value = selectorCurve.liParaCurveGUI.at(idCurve).paraGUI.at(\eMinX);
		nEMinX.action = {
			arg val; 
			selectorCurve.editParaGUIIter([\eMinX, val.value]);
		};
		
		nEMaxX = NumberBox(windowPara, Rect(0, 0, 50, 20));
		nEMaxX.step_(0.1)
			.scroll_step_(0.1).background_(Color.grey).setProperty(\align, \center);
		nEMaxX.setProperty(\stringColor, Color.black);
		nEMaxX.value = selectorCurve.liParaCurveGUI.at(idCurve).paraGUI.at(\eMaxX);
		nEMaxX.action = {
			arg val;
			selectorCurve.editParaGUIIter([\eMaxX, val.value]); 
		};

		nEMinY = NumberBox(windowPara, Rect(0, 0, 50, 20));
		nEMinY.step_(0.1)
			.scroll_step_(0.1).background_(Color.grey).setProperty(\align, \center);
		nEMinY.setProperty(\stringColor, Color.black);
		nEMinY.value = selectorCurve.liParaCurveGUI.at(idCurve).paraGUI.at(\eMinY);
		nEMinY.action = {
			arg val; 
			selectorCurve.editParaGUI([\eMinY, val.value]);
		};
				
		nEMaxY = NumberBox(windowPara, Rect(0, 0, 50, 20));
		nEMaxY.step_(0.1)
			.scroll_step_(0.1).background_(Color.grey).setProperty(\align, \center);
		nEMaxY.setProperty(\stringColor, Color.black);
		nEMaxY.value = selectorCurve.liParaCurveGUI.at(idCurve).paraGUI.at(\eMaxY);
		nEMaxY.action = {
			arg val; 
			selectorCurve.editParaGUI([\eMaxY, val.value]);
		};

		bNormX = Button(windowPara, Rect(0, 0, 50, 20));
		bNormX.states_([["NormX"]]);
		bNormX.action_({selectorCurve.normX;});
		
		bNormY = Button(windowPara, Rect(0, 0, 50, 20));
		bNormY.states_([["NormY"]]);
		bNormY.action_({selectorCurve.normY;});
		
		nGrilleX = NumberBox(windowPara, Rect(0, 0, 50, 20));
		nGrilleX.step_(0.1)
			.scroll_step_(0.1).background_(Color.grey).setProperty(\align, \center);
		nGrilleX.setProperty(\stringColor, Color.black);
		nGrilleX.value = selectorCurve.liParaCurveGUI.at(idCurve).paraGUI.at(\grilleX);
		nGrilleX.action = {
			arg val; 
			selectorCurve.editParaGUI([\grilleX, val.value]);
		};
			
		nGrilleY = NumberBox(windowPara, Rect(0, 0, 50, 20));
		nGrilleY.step_(0.1)
			.scroll_step_(0.1).background_(Color.grey).setProperty(\align, \center);
		nGrilleY.setProperty(\stringColor, Color.black);
		nGrilleY.value = selectorCurve.liParaCurveGUI.at(idCurve).paraGUI.at(\grilleY);
		nGrilleY.action = {
			arg val; 
			selectorCurve.editParaGUI([\grilleY, val.value]);
		};
		
		bEnregist = Button(windowPara, Rect(0, 0, 50, 20));
		bEnregist.states_([["Store"]]);
		bEnregist.action_({
			selectorCurve.enregist;
			nIdFile.value = selectorCurve.idFile;
		});		
		
		bModif = Button(windowPara, Rect(0, 0, 50, 20));
		bModif.states_([["Modif"]]);
		bModif.action_({
			selectorCurve.modif;});		

		bEffac = Button(windowPara, Rect(0, 0, 50, 20));
		bEffac.states_([["Delete"]]);
		bEffac.action_({
			selectorCurve.effacer;});
			
		nIdFile = NumberBox(windowPara, Rect(0, 0, 50, 20));
		nIdFile.background_(Color.grey).setProperty(\align, \center);
		nIdFile.setProperty(\stringColor, Color.black);
		nIdFile.value = 0;
		nIdFile.action = {
			arg val;
			selectorCurve.rappel(val.value);
		};

		//============		
		windowPara.view.decorator.nextLine;
		Titre.new(windowPara, "", 77);//
		Titre.new(windowPara, "idPt", 50);
		
		Titre.new(windowPara, "x/y");
		
		Titre.new(windowPara, "Interpolation", 370);
		
		//============
		windowPara.view.decorator.nextLine;
		
		bConvert = Button(windowPara, Rect(0, 0, 77, 20));
		bConvert.states_([["Convertir"]]);
		bConvert.action_({
			selectorCurve.convert;});
		
		nIdPt = NumberBox(windowPara, Rect(0, 0, 50, 20));
		nIdPt.background_(Color.grey).setProperty(\align, \center);
		nIdPt.setProperty(\stringColor, Color.black);
		
		nValX = NumberBox(windowPara, Rect(0, 0, 50, 20));
		nValX.step_(0.1)
			.scroll_step_(0.1).background_(Color.grey).setProperty(\align, \center);
		nValX.setProperty(\stringColor, Color.black);
		nValX.value = 0;
		nValX.action = {
			arg val;
			selectorCurve.modifPtS(nil, \x, val.value);};
			
		nValY = NumberBox(windowPara, Rect(0, 0, 50, 20));
		nValY.step_(0.1)
			.scroll_step_(0.1).background_(Color.grey).setProperty(\align, \center);
		nValY.setProperty(\stringColor, Color.black);
		nValY.value = 0;
		nValY.action = {
			arg val;
			selectorCurve.modifPtS(nil, \y, val.value);};
			
		bIType = Button(windowPara, Rect(0, 0, 50, 20));
		bIType.states_([	
			["Off"],
			["Tenue"],
			["Carres"],
			["Scies"],
			["Iter"],
			["Gliss"],
			["Loop"]
		]);
		bIType.action_({
			arg val; 
			selectorCurve.modifPtS(nil, \type, val.value); 
		});
		
		nINetap = NumberBox(windowPara, Rect(0, 0, 50, 20));
		nINetap.background_(Color.grey).setProperty(\align, \center);
		nINetap.setProperty(\stringColor, Color.black);
		nINetap.value = 0;
		nINetap.action = {						
			arg val; 
			selectorCurve.modifPtS(nil, \netap, val.value); 
		};
		
		nICoefX = NumberBox(windowPara, Rect(0, 0, 50, 20));
		nICoefX.step_(0.1)
			.scroll_step_(0.1).background_(Color.grey).setProperty(\align, \center);
		nICoefX.setProperty(\stringColor, Color.black);
		nICoefX.value = 0;
		nICoefX.action = {
			arg val; 
			selectorCurve.modifPtS(nil, \coefX, val.value); 
		};
			
		nICoefY = NumberBox(windowPara, Rect(0, 0, 50, 20));
		nICoefY.step_(0.1)
			.scroll_step_(0.1).background_(Color.grey).setProperty(\align, \center);
		nICoefY.setProperty(\stringColor, Color.black);
		nICoefY.value = 0;
		nICoefY.action = {
			arg val; 
			selectorCurve.modifPtS(nil, \coefY, val.value); 
		};
		
		nIdLoop = NumberBox(windowPara, Rect(0, 0, 50, 20));
		nIdLoop.background_(Color.grey).setProperty(\align, \center);
		nIdLoop.setProperty(\stringColor, Color.black);
		nIdLoop.value = 0;
		nIdLoop.action = {
			arg val;
			selectorCurve.modifPtS(nil, \idLoop, val.value);
		};

		nIdStartLoop = NumberBox(windowPara, Rect(0, 0, 50, 20));
		nIdStartLoop.background_(Color.grey).setProperty(\align, \center);
		nIdStartLoop.setProperty(\stringColor, Color.black);
		nIdStartLoop.value = 0;		
		nIdStartLoop.action = {
			arg val;
			selectorCurve.modifPtS(nil, \idStartLoop, val.value);
		};

		nIdEndLoop = NumberBox(windowPara, Rect(0, 0, 50, 20));
		nIdEndLoop.background_(Color.grey).setProperty(\align, \center);
		nIdEndLoop.setProperty(\stringColor, Color.black);
		nIdEndLoop.value = 0;		
		nIdEndLoop.action = {
			arg val;
			selectorCurve.modifPtS(nil, \idEndLoop, val.value);
		};
		
		bLockX = Button(windowPara, Rect(0, 0, 77, 20));
		bLockX.states_([["X libres"], ["X bloquŽs"]]);
		bLockX.action_({
			arg val;
			selectorCurve.editParaGUI([\lockX, val.value]);
		});

		
		windowPara.front;
		
	// Fenetre d edition
		windowEditeur = Window(nom ++ "_Edit", Rect(left, bottom, longueur, hauteur), 0);
		windowEditeur.view.background_(Color.new(0.9, 0.9, 0.9));
		editeur = UserView(windowEditeur, Rect(0, 0, longueur, hauteur));
		editeur.background = Color.black;
		
		selectorCurve.vue(editeur);
		
	// Actions de la souris et du clavier
		editeur.mouseDownAction_({
			arg view, x, y, modifiers, buttonNumber, clickCount;
			selectorCurve.mouseDown(x, y, fonctionMouse);
			if (fonctionMouse == \xy)
				{nValX.value = selectorCurve.liParaCurveGUI.at(idCurve).ptS.x.round(0.001);
				nValY.value = selectorCurve.liParaCurveGUI.at(idCurve).ptS.y.round(0.001);
				bIType.value_(selectorCurve.liParaCurveGUI.at(idCurve).ptS.p1);
				nINetap.value = selectorCurve.liParaCurveGUI.at(idCurve).ptS.p2;
				nICoefX.value = selectorCurve.liParaCurveGUI.at(idCurve).ptS.p3.round(0.001);
				nICoefY.value = selectorCurve.liParaCurveGUI.at(idCurve).ptS.p4.round(0.001);
				// nIdLoop.value = ;
				nIdStartLoop.value = selectorCurve.liParaCurveGUI.at(idCurve).ptS.p6;
				nIdEndLoop.value = selectorCurve.liParaCurveGUI.at(idCurve).ptS.p7;
				};
		});
		editeur.mouseMoveAction_({
			arg view, x, y;
			selectorCurve.mouseMove(x, y, fonctionMouse);			if (fonctionMouse == \xy)
				{nValX.value = selectorCurve.liParaCurveGUI.at(idCurve).ptS.x.round(0.001);
				nValY.value = selectorCurve.liParaCurveGUI.at(idCurve).ptS.y.round(0.001);};
		});
		editeur.mouseUpAction_({
			selectorCurve.mouseUp(fonctionMouse);
		});

		editeur.keyDownAction_({
			arg view, char, modifiers, unicode, keycode; 
			unicode.switch(
				100,	{fonctionMouse = \removePt},
				115,	{fonctionMouse = \type},
				105,	{fonctionMouse = \netap},
				120,	{fonctionMouse = \coefXY};
			)
			});
		editeur.keyUpAction_({
			fonctionMouse = \xy;
		});


	// Fonction d affichage
		editeur.drawFunc = {
			selectorCurve.liCurve.do({
				arg curve, i;
				var affMode;
				var coordsTsLesPts;
				selectorCurve.idC(i);
				if (selectorCurve.liParaCurveGUI.at(i).paraGUI.at(\showHide) == 1)
				{affMode = selectorCurve.liParaCurveGUI.at(i).paraGUI.at(\affMode);
				coordsTsLesPts = selectorCurve.formatGUI;
				if (coordsTsLesPts.size > 0)
					{Pen.color = selectorCurve.paraGUI.at(\couleur);
					Pen.moveTo(coordsTsLesPts.at(0));
					coordsTsLesPts.do({
						arg pt;
						var centrage = pt.p2 / 2 - 0.5;
						if (affMode != 1)
							{Pen.lineTo(pt); Pen.stroke;};
						if (affMode != 2)
							{Pen.fillOval(
								Rect(pt.x - centrage, pt.y - centrage, pt.p2, pt.p2));};
						if (pt.p1 != nil)
							{Pen.stringAtPoint(pt.p1.asString, pt.x@(pt.y - 15));};
						Pen.moveTo(pt);
					});
					}
				}
			});
			selectorCurve.idC(idCurve);
		};
		windowEditeur.front;

	}
	
}


			

if (this.isLog(2)) { ("\n\n" + thisProcess.nowExecutingPath.basename + "\n").postln; };

(
DrawDef(\SYSTab_RefSysLinesBefore, \moved,
	{|node, drawServer, sysTab, pE, e, refSys0Color|
		//all sysTab params already put into pE by DrawDef 'SYSTab_init'
		var rCount = pE.refSysMode, rMod = 6;
		var prevRefSysSym, refSys, refSysUpdFunc, refSysDepFunc;
		if (pE.refSysSym == \nil) { pE.refSysSym = sysTab.sysSymbols.first };
		prevRefSysSym = pE.refSysSym;
		
		pE.refSysMidiRect = Rect.new;  pE.refSysRect = Rect.new;
		
		// keyActions		
		drawServer.addActionChar($r, {|down, repeat| if (down && repeat.not) {
			pE.refSysHideOn = false; rCount = rCount+1; pE.refSysMode = rCount.mod(rMod) }; true });
		drawServer.addActionChar($R, {|down, repeat| if (down && repeat.not) {
			pE.refSysHideOn = false; rCount = rCount-1; pE.refSysMode = rCount.mod(rMod) }; true });
		drawServer.addActionChar($H, {|down, repeat| if (down && repeat.not) {
			pE.refSysHideOn = pE.refSysHideOn.not }; true });
		drawServer.addActionChar($G, {|down, repeat| if (down && repeat.not) {
			pE.gridSysOn = pE.gridSysOn.not }; true });
		drawServer.addActionChar($y, {|down, repeat| if (down && repeat.not) {
			sysTab.mlSel.selected(0).postln }; true });
		// actions go later in drawGraphÉ see below
			
		refSysUpdFunc = {
			pE.refSysRootIndex = refSys.rootIndex;
			pE.refSysMidinotes = pE.sysMidinotesPsM(refSys);
			pE.refSysMidiRef = pE.refSysMidinotes[pE.refSysRootIndex];
			pE.refSysColors = pE.sysStepColorsPsM(refSys);
			pE.refSysColors[pE.refSysRootIndex] = refSys0Color;
			pE.refSys = refSys };
		refSysDepFunc = {|who, what| refSysUpdFunc.value }; //any change need recalc anyway
		node.addDependantFuncTo(Systema, {|who, what, name| //this.logln("sCD:" + [who, what, name]);
			what.switch(
				\new, { if (name == pE.refSysSym) { 
					refSys = Systema.at(name); refSysUpdFunc.value; 
					node.addDependantFuncTo(refSys, refSysDepFunc) }Ê}
				,\destroy, { if (name == pE.refSysSym) { pE.refSys = refSys = nil }} ) } );
		pE.refSys = refSys = Systema.at(prevRefSysSym); //check only once;
		if (refSys.isNil) { pE.refSysMidinotes = []; pE.refSysColors = []
		}{ refSysUpdFunc.value; node.addDependantFuncTo(refSys, refSysDepFunc) };

		{|e, refSysHideOn, refHideOn, refSumXOff, refFontExt, refXOff, refXGab,
			offX, drawWidth, refSysSym|
			if (refSysHideOn.not) { 
				e.refSysX = if (refHideOn.not) { refSumXOff } { 0 };
				e.refSysLineWidth = drawWidth - e.refSysX;
				e.refSysSumXOff = refFontExt.x + refXGab + refXOff;
				e.offX = offX + e.refSysSumXOff;
				
				if (prevRefSysSym != refSysSym) {
					prevRefSysSym = refSysSym;
					node.removeDependantFunc(refSysDepFunc); // nil ok here!
					pE.refSys = refSys = Systema.at(refSysSym);
					refSys !? {ÊrefSysUpdFunc.value;
						node.addDependantFuncTo(refSys, refSysDepFunc) };
			}};
	}}
	,{{|e, refSysMode, refSysHideOn, gridSysOn, refXGab, refSys0Color, drawSysY0, drawWidth, trans,
		scaleDelta, refSysX, refSysLineWidth, refSysMidiRect, refSysMidinotes, refSysRootIndex,
		refSysColors, outline, refBackColor, refSysSumXOff, drawHeight, selHeight=10|
		if(refSysHideOn.not && e.refSys.notNil) {
// if (refSysMode == 5) { extra SYS specific stuff to come... enable by left click menue };
			e.refSysYs = e.midis2ysPsM(refSysMidinotes);
			e.refSysY0 = e.refSysYs[refSysRootIndex];
			
			if(gridSysOn) {
				refSysMidiRect.set(refSysX, drawSysY0 - (selHeight / 2), 
					refSysLineWidth, selHeight);		
				Pen.use{ Pen.translate(trans[0], scaleDelta[1]);
					Pen.width = 1;
					if (refSysMode == 0) {
						refSys0Color.setStroke; 
						Pen.line(refSysX@e.refSysY0, drawWidth@e.refSysY0); Pen.stroke;
					}{
						e.refGridPsM(e.refSysYs, e.refSysColors, refSysX, refSysLineWidth) 
					};
					if (outline) { Color.grey.setStroke; Pen.strokeRect(refSysMidiRect) }
				}
			}{ refSysMidiRect.set(0,0,0,0) };
		} { refSysMidiRect.set(0,0,0,0) }; 
	}})
);

(
DrawDef(\RefSysAfterMask, \moved, nil,
	{{|e, trans, scaleDelta, refMode, refSysMode, ref0Color, refBackColor, refSysX, refSysSumXOff,
		drawSysY0, drawHeight, refYs, refColors, refDashArray, refHideOn, refSysHideOn|
		
		if (refSysHideOn.not) {
			Pen.use{ Pen.translate(*trans); refBackColor.setFill; 
			Pen.fillRect( Rect(refSysX, 0, refSysSumXOff, drawHeight) ) };
			
			if (refHideOn.not) {
				Pen.use{ Pen.translate(trans[0], scaleDelta[1]); 
					Pen.width = 1; Pen.lineDash_(refDashArray);
					if (refMode > 0) { 
						e.refGridPsM(refYs, refColors, refSysX, refSysSumXOff) };
					ref0Color.set; Pen.line(refSysX@drawSysY0, (refSysX + refSysSumXOff)@drawSysY0);
					Pen.stroke };
		}}
}})
);

(
DrawDef(\SYSTab_RefSysLinesAfter, \moved, nil,
	{|node, refYGab, refXGab, refFont, pE, e, drawServer|
		var refDrawFunc, refStrFuncs = [
			{|midi| "Midi:" + midi.round(0.01) },
			{|midi| "Freq:" + midi.midicps.round(0.1) },
			{|midi| "Cents:" + ((midi - pE.midiRef) * 100).round(0.1) },
			{|midi| (midi - pE.midiRef).midiratio.asBestRatio.mcFormat(14, \c) }, 
			{|midi| "extra stuff..." }];
		
		//actions
		drawServer.addField(ActionField, pE.refSysRect, {|sV, cC, mod, bN, sP|
			mod.switch(
				131330, { pE.strDisplayPsM(sP, "SYS."++pE.refSysSym) } // shift
				,1179914, { drawServer.beginDragAction = {|v, x, y, mod| // shif-cmd
					var str = "SYS."++pE.refSysSym; v.dragLabel = str; str.asSymbol} }
				,1573160, { drawServer.beginDragAction = {|v, x, y, mod| // alt-cmd
					if (pE.refSys.notNil) { v.dragLabel =Ê"RCTuning [" ++ pE.refSys.tuning.size 
						++ "]:" + pE.refSys.tuning.name; pE.refSys.tuning }{ nil } }; true }
				,1310985, { drawServer.beginDragAction = {|v, x, y, mod| // ctrl-cmd
					if(pE.refSys.notNil) { v.dragLabel= pE.refSys.asString; pE.refSys } {nil} };
					true }
				,{ false } )}
			,{|sV, fwdArgs| fwdArgs} // shift
			,{|sV, fwdArgs| if (fwdArgs.isKindOf(FuncNode)) { fwdArgs.remove } });
		//drag 
		drawServer.addDragField(DragField, pE.refSysRect
			,{|sV, obj, p| if (obj.isKindOf(Systema)) {true} {false} }
			,{|sV, obj, p| pE.refSysSym = obj.name });
		
		DrawFunc(\RefSysAfterMask, e, pE.refLinesAfterNode, \addBefore);
		
		refDrawFunc = {			
			var strYdelta = e.refFontExt.y + refYGab;
			var ys = e.refSysYs, colors = e.refSysColors, midinotes = e.refSysMidinotes;
			e.refGridPsM(ys, colors, e.refSysX, e.refSysSumXOff);
			Pen.use{ Pen.smoothing_(false);
				ys.do{|y, i| refStrFuncs[e.refSysMode -1].value(midinotes[i]).drawAtPoint(
					(e.refSysX + refXGab)@(y - strYdelta), refFont, colors[i]) } };
// adopt min max:
//e.refYmin = ys.last - strYdelta;  e.refYmax = ys.first 
		};
		
		{|e, refSysMode, refSysHideOn, refXGab, refYGab, refFontExt, refFont, refSys0Color,
			refSysBackColor, refSysY0, drawHeight, trans, scaleDelta, refSysX, refSysSumXOff,
			refSysRect, refSysMidiRef, outline|
			if(refSysHideOn.not) {
				refSysRect.set(refSysX, 0, refSysSumXOff, drawHeight);
				Pen.use{ Pen.translate(*trans); 
					if (pE.refSys.notNil) { refSysBackColor.setFill; Pen.fillRect( refSysRect );
					}{ Color.red.setFill; refSysRect;
						pE.missingSysPsM(e.refSysSym, refSysX + (refSysSumXOff / 2)) };
					if (outline) { Color.blue.setStroke; Pen.strokeRect(refSysRect) } };
				pE.refSys !? {
					Pen.use{ Pen.translate(trans[0], scaleDelta[1]);
						Pen.width = 1; 
						if (refSysMode == 0) {
							Pen.use{ Pen.smoothing_(false);
								refStrFuncs[0].value(refSysMidiRef)
								.drawAtPoint( (refSysX + refXGab)
									@(refSysY0 - refFontExt.y - refYGab-2), refFont, refSys0Color);
								refStrFuncs[1].value(refSysMidiRef)
								.drawAtPoint( (refSysX + refXGab)
									@(refSysY0 + refYGab+2), refFont, refSys0Color) };
							refSys0Color.set; Pen.line(refSysX@refSysY0, 
								(refSysX + refSysSumXOff)@refSysY0); Pen.stroke 
						}{ refDrawFunc.value }
			}}}
	}})
);

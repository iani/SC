if (this.isLog(2)) { ("\n\n" + thisProcess.nowExecutingPath.basename + "\n").postln; };

(
DrawDef(\SYSTab_RefLinesBefore, \moved,
	{|node, drawServer, sysTab, pE, e, ref0Color, quantColors| 
		var tCount = pE.refMode, tMod = 5, qCount = pE.quantMode, qMod = pE.quantModes.size;
		pE.refFontExt = "Freq: 12342.4 ".bounds(pE.refFont).extent;
		
		// keyActions		
		drawServer.addActionChar($t, {|down, repeat| if (down && repeat.not) {
			pE.refHideOn = false; tCount = tCount+1; pE.refMode = tCount.mod(tMod) }; true });
		drawServer.addActionChar($T, {|down, repeat| if (down && repeat.not) {
			pE.refHideOn = false; tCount = tCount-1; pE.refMode = tCount.mod(tMod) }; true });
		drawServer.addActionChar($q, {|down, repeat| if (down && repeat.not) {
			pE.refHideOn = false; qCount = qCount+1; pE.quantMode = qCount.mod(qMod) }; true });
		drawServer.addActionChar($Q, {|down, repeat| if (down && repeat.not) {
			pE.refHideOn = false; qCount = qCount-1; pE.quantMode = qCount.mod(qMod) }; true });
		drawServer.addActionChar($h, {|down, repeat| if (down && repeat.not) {
			pE.refHideOn = pE.refHideOn.not }; true });
		drawServer.addActionChar($g, {|down, repeat| if (down && repeat.not) {
			pE.gridOn = pE.gridOn.not }; true });
		
		// ps-methods
		pE.refCalcPsM = {|pE| 
			var div, maxSteps, minSteps, negSteps, posSteps, posColors, negColors;
			var quant = pE.quantModes[pE.quantMode];
			var colsP, colsN;
			div = (e.midiMax - e.midiRef) / quant;
			maxSteps = div.round(1).asInt + 1;
			if (div.frac < (quant/2)) {maxSteps = maxSteps - 1};
			div =  (e.midiMin - e.midiRef) * -1 / quant;
			minSteps = div.round(1).asInt + 1;
			if (div.frac < (quant/2) ) {minSteps = minSteps - 1};
			//midinotes
			posSteps = Array.series(maxSteps, quant, quant);
			negSteps = Array.series(minSteps, quant * -1, quant * -1);
			e.refMidinotes = negSteps.reverse.add(0) ++ posSteps + e.midiRef;
			//colors
			posColors = e.refColorPsM(maxSteps);
			negColors = e.refColorPsM(minSteps);
			e.refColors = negColors.reverse.add(ref0Color) ++ posColors};
		pE.refColorPsM =  {|pE, stepsN|
			var quantMode = pE.quantMode;
			stepsN.collect{|n| quantColors[quantMode - ((n+1).gcd( (2 ** quantMode).asInt ).log2)]} };
		pE.refGridPsM = {|pE, ys, colors, startX, lineWidth|
			var endX = startX + lineWidth;
			ys.do{|y, i| colors[i].setStroke; Pen.line(startX@y, endX@y); Pen.stroke } };
		
		{|e, refHideOn, refFontExt, refXOff, refXGab|
			if (refHideOn.not) { 
				e.refSumXOff = refFontExt.x + refXGab + refXOff;
				e.offX = e.offX + e.refSumXOff };
	}}
	,{|node, pE|
		{|e, refMode, refHideOn, gridOn, refXGab, ref0Color, drawSysY0, drawWidth, trans, scaleDelta,
			refMidiRect, refSysMidinotes, refSysColors, refSysMode, refSysHideOn, selHeight=10|
			if(refHideOn.not) { 
				e.refCalcPsM;
				e.refYs = e.midis2ysPsM(e.refMidinotes);
				
				if(gridOn) {
					refMidiRect.set(0, drawSysY0 - (selHeight / 2), drawWidth, selHeight);		
					Pen.use{ Pen.translate(trans[0], scaleDelta[1]); Pen.width = 1;
						pE.refDashArray = if (refSysHideOn.not) {Ê
							FloatArray[4,4] }{ FloatArray[1,0] };
						Pen.use { Pen.lineDash_(e.refDashArray);
							if (refMode > 0) { e.refGridPsM(e.refYs, e.refColors, 0, drawWidth)
							}{ 	ref0Color.setStroke; 
								Pen.line(0@drawSysY0, drawWidth@drawSysY0); Pen.stroke } }
					}
				}{ refMidiRect.set(0,0,0,0) };
			} { refMidiRect.set(0,0,0,0) }; 
	}})
);

(
DrawDef(\SYSTab_RefLinesAfter, \moved, nil,
	{|node, drawServer, refYGab, refXGab, refFont, pE, e|
		var refDrawFunc, refStrFuncs = [
			{|midi| "Midi:" + midi.round(0.01) },
			{|midi| "Freq:" + midi.midicps.round(0.1) },
			{|midi| "Cents:" + ((midi - pE.midiRef) * 100).round(0.1) },
			{|midi| (midi - pE.midiRef).midiratio.asBestRatio.mcFormat(14, \c) } ];
		pE.refLinesAfterNode = node; // used to create a mask in SYSTab_RefSysLinesAfter
		
		
		// action fields
		pE.refRect = Rect.new; pE.refMidiRect = Rect.new;
		drawServer.addField(ActionField, pE.refRect, {|sV, cC, mod, bN, cP, p|
			if (mod == 1310985) { drawServer.beginDragAction = {|v| // ctrl-cmd
				v.dragLabel= "drag onto a degree to make it (" ++ pE.midiRef.round(0.01) 
					++ ") and change SYS offset"; pE.midiRef }; true
			}{ false } });
		drawServer.addField(MovedDeltaYActionField, pE.refMidiRect, {|sV, cC, mod, bN, cP, p|
				mod.switch(
					262401, { pE.refYsR = e.refYs.reverse; // ctrl
						DrawFunc(\lineY_moved, (pE: pE, y: e.drawSysY0 + e.scaleDelta[1]), node) } 
					,393475, { pE.refYsR = e.refYs.reverse; // shift-ctrl
						DrawFunc(\lineY_moved, (pE: pE, y: e.drawSysY0 + e.scaleDelta[1] 
							,dashArray: FloatArray[5,5]), node)  } 					,786721, { pE.refYsR = e.refYs.reverse; // ctrl-alt
						DrawFunc(\lineY_moved, (pE: pE, y: e.drawSysY0 + e.scaleDelta[1]
						,color: Color.red), node) }  					,917795, { pE.refYsR = e.refYs.reverse; // shift-ctrl-alt
						DrawFunc(\lineY_moved, (pE: pE, y: e.drawSysY0 + e.scaleDelta[1] 
							,dashArray: FloatArray[5,5], color: Color.red), node)  } 
					, {false} )
			},{|sV, fwdArgs, mod, cP, p, deltaXY| var index; 
				case
					{(mod == 262401) || (mod == 786721)} { // ctrl || ctrl-alt
						pE.newMidiRefY = cP.y + e.scaleDelta[1]; 
						pE.newMidiRefFloatArray = FloatArray[1,0] } 
					{(mod == 393475) || (mod == 917795)} { // shift-ctrl || shift-ctrl-alt
						index = pE.refYsR.indexOfGreaterThan(cP.y);  
						pE.newMidiRefY = pE.refYsR[index] + e.scaleDelta[1];
						pE.newMidiRefFloatArray = FloatArray[5,5] }; 				fwdArgs // foreward the fwdArgs once more!
			},{|sV, fwdArgs, mod, cP, p| 
				case
				{(mod == 262401) || (mod == 393475)} { // ctrl || shift-ctrl
					pE.midiRef = pE.y2midiPsM(pE.newMidiRefY - e.scaleDelta[1]) }
				{(mod == 786721) || (mod == 917795)} { // ctrl-alt || shift-ctrl-alt
					pE.midiRef = pE.y2midiPsM(pE.newMidiRefY - e.scaleDelta[1]);
					pE.sysCalcs.do{|sysCalc| sysCalc.sys !? {sysCalc.sys.midiRoot = pE.midiRef}} };
				if (fwdArgs.isKindOf(FuncNode)) { fwdArgs.remove } });
		//drag 
		drawServer.addDragField(DragField, pE.refRect
			,{|sV, obj, p| if (obj.isKindOf(Systema) || obj.isKindOf(SimpleNumber)) {true} {false} }
			,{|sV, obj, p| pE.midiRef = if (obj.isKindOf(Systema)) {obj.midiRoot} {obj} });
		
		refDrawFunc = {			
			var strYdelta = e.refFontExt.y + refYGab;
			var ys = e.refYs, colors = e.refColors, midinotes = e.refMidinotes;
			e.refGridPsM(ys, colors, 0, e.refSumXOff);
			Pen.use{ Pen.smoothing_(false);
				ys.do{|y, i| refStrFuncs[e.refMode -1].value(midinotes[i]).drawAtPoint(
					refXGab@(y - strYdelta), refFont, colors[i]) };
			//	refStrFuncs[e.refMode -1].value(e.midiRef).drawAtPoint(
			//		refXGab@(e.drawSysY0 - strYdelta), refFont, e.ref0Color) 
			};
			e.refYmin = ys.last - strYdelta;  e.refYmax = ys.first 
		};
		
		{|e, refMode, refHideOn, refXGab, refYGab, refFontExt, refFont, ref0Color, refBackColor,
			drawSysY0, midiRef, drawHeight, trans, scaleDelta, refSumXOff, refMidiRect, outline, 
			refRect|
			
			if(refHideOn.not) {
				refRect.set(0, 0, refSumXOff, drawHeight);
				Pen.use{ Pen.translate(*trans); refBackColor.setFill; Pen.fillRect(refRect);
					if (outline) { Color.cyan.setStroke; Pen.strokeRect(refRect) } };
				Pen.use{ Pen.translate(trans[0], scaleDelta[1]);
					Pen.width = 1; 
					if (refMode > 0) { refDrawFunc.value }{
						ref0Color.set; Pen.line(0@drawSysY0, refSumXOff@drawSysY0); Pen.stroke;
						Pen.use{ Pen.smoothing_(false);
							refStrFuncs[0].value(midiRef).drawAtPoint(refXGab
								@(drawSysY0 - refFontExt.y - refYGab-2), refFont, ref0Color);
							refStrFuncs[1].value(midiRef).drawAtPoint(
								refXGab@(drawSysY0 + refYGab+2), refFont, ref0Color) } };
				if (outline) { Color.grey(0.8).setStroke; Pen.strokeRect(refMidiRect) } };
			}
	}})
);

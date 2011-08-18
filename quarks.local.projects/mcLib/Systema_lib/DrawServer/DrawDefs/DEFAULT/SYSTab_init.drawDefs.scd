if (this.isLog(2)) { ("\n\n" + thisProcess.nowExecutingPath.basename + "\n").postln; };
/*
nameing conventions:
midiInt (midiInterval) -> a interval of 100 cents
midi, (midinote) -> an absolute midi value corresponding to freq by .midicps
yes, its a design decision not to use e.use{} and more effecient anyway?!
*/
(
DrawDef(\SYSTab_init, \calc,
	{|node, drawServer, pE, e, self, sysTab, anchorMode, footMode, footModeColors|
		var aCount = anchorMode, aMod = 3, bCount = footMode, bMod = footModeColors.size;
		
		pE.putAll(self.envir);
		
		drawServer.scaleMode_(pE.scaleMode); //also updates back into drawEnvir, but ok
		pE.scale !? { drawServer.actionView.scale_(pE.scale, false) }; //show next frame only 
		pE.move !? { drawServer.actionView.oneShotRefreshAction = { // e.move.postln;
			drawServer.actionView.move_(pE.move, false) 
			// drawServer.actionView.move2Origin; // when to keep, when go to origin?
			}};  //do at end of first frame
		
		// keyActions
		drawServer.addActionChar($a, {|down, repeat| if (down && repeat.not) {
			aCount = aCount+1; pE.anchorMode = aCount.mod(aMod) }; true });
		drawServer.addActionChar($A, {|down, repeat| if (down && repeat.not) {
			aCount = aCount-1; pE.anchorMode = aCount.mod(aMod) }; true });
		drawServer.addActionChar($b, {|down, repeat| if (down && repeat.not) {
			bCount = bCount+1; pE.footMode = bCount.mod(bMod); e.footModePsM }; true });
		drawServer.addActionChar($B, {|down, repeat| if (down && repeat.not) {
			bCount = bCount-1; pE.footMode = bCount.mod(bMod); e.footModePsM }; true });
		drawServer.addActionChar($s, {|down, repeat| if (down && repeat.not) {
			pE.selOn = pE.selOn.not }; true });
		drawServer.addActionChar($c, {|down, repeat| if (down && repeat.not) {
			pE.tcOn = pE.tcOn.not }; true });
		drawServer.addActionChar($o, {|down, repeat| if (down && repeat.not) {
			pE.outline = pE.outline.not }; true });
		drawServer.addActionChar(\cmdDel, {
			var param = sysTab.mlSel.removeAt(*sysTab.mlSel.selected(0));
			param !? { param.drawNode.remove } });
		drawServer.addActionChar(\cmdArrowLeft, { 
			sysTab.mlSel.selector(0).moveSelectedBwd; sysTab.changed(\sysParams) });
		drawServer.addActionChar(\cmdArrowRight, { 
			sysTab.mlSel.selector(0).moveSelectedFwd; sysTab.changed(\sysParams) });
		
		// action fields
		drawServer.addField(ActionField, pE.view.bounds, {|sV, cC, mod|
			mod.switch( 131330, { true }, 524576, { true }, 655650, true, {false} )
		},{|sV, fwdArgs, mod, cP, p, deltaXY|
			mod.switch(
				131330, { pE.sysOffX = (pE.sysOffX + ( deltaXY[0] / 50)).max(0);
					pE.refScaleY = pE.refScaleY + ( deltaXY[1] / 1000) }
				,524576, { sV.move_( (sV.move + ( deltaXY / 3000)).min(1).max(0) ) }
				,655650, { sV.scale_( sV.scale  + ( deltaXY / 10000) )  } ) 
		});
		drawServer.addField(ActionField, pE.view.bounds, {|sV, cC, mod, bN, cP, p|
			if ( cC < 3) { false }Ê{ mod.switch(
				131330, { pE.refScaleY = 1 }
				,524576,{ sV.move2Origin }
				,655650, { sV.scale_([1.0, 1.0]) }
				, {false} ) }
		});
		// drag fields
		drawServer.addDragField(DragField, pE.view.bounds
			,{|sV, obj, p| if (obj.isKindOf(Systema)) {true} {false} }
			,{|sV, obj, p| sysTab.addAll([obj.name]) });
			
		// global vars	
		pE.sysCalcs ?? { pE.sysCalcs = () };
		pE.labelHheight = "Archytas".bounds(pE.labelHFont).height - 1;
		pE.labelFheight = "Archytas".bounds(pE.labelFFont).height;

		pE.drawSysYgTop = pE.yHroom + pE.labelHheight + pE.labelHGab; // begin gab
		pE.drawSysYgBot = pE.yFroom + pE.labelFheight + pE.labelFGab; // end gab
		
		pE.footModePsM = {|pE|
			pE.sysCalcs.values.do{|sysCalc| sysCalc.footMode = pE.footMode } };

		{|e, view, anchorMode octaveRanges, scaleMode, refScaleY, drawSysYgTop, drawSysYgBot, 
			labelFGab, labelHFont, midiRef, post| 
//e.postOffset = 300@20;
			//x
			e.drawWidth = view.drawBounds.width; 
			e.xMin = 0; e.xMax = e.drawWidth; e.xAddLeft = 0; e.xAddRight = 0;
			e.offX = e.sysOffX;
			//y
			e.drawHeight = view.drawBounds.height; 
			e.yMin = 0; e.yMax = e.drawHeight; e.yAddTop = drawSysYgTop; e.yAddBot = drawSysYgBot;
			//SYS placing
			e.drawSysH = e.drawHeight - drawSysYgTop - drawSysYgBot;
			e.footerTop = e.drawSysYgTop + e.drawSysH + labelFGab;
			e.drawSysY0 = [{e.drawSysYgTop + (e.drawSysH / 2)}, {e.drawSysYgTop + e.drawSysH}
				,{e.drawSysYgTop}][anchorMode].value;
			//midi scaling
			e.midiSysH = e.drawSysH * refScaleY;
			e.midiScale = e.midiSysH / (octaveRanges.clipAt(scaleMode) * 12); 
			//default midi range
//this.logln("just before crash:" + [midiRef, e.midiRef, pE.midiRef]);
			e.midiMax = octaveRanges.clipAt(scaleMode) * [6, 12, 0][anchorMode] + midiRef; 
			e.midiMin = octaveRanges.clipAt(scaleMode) * [-6, 0, -12][anchorMode] + midiRef;
			//ref lines
			e.refYmin = inf; e.refYmax = -inf;
			
			if (drawServer.isPause) { "paused".drawCenteredIn(view.drawBounds, 
				Font("Helvetica-Bold",  e.drawHeight / 6), Color.grey) };
			
//		Pen.fillRadialGradient( //nice, but black is beautiful and gradients are hard to gloss over
//				view.bounds.center,
//				view.bounds.center,
//				0, view.bounds.width.max(view.bounds.height),
//				Color.new255(105, 130, 150, 255),
//				Color.new255(57, 73, 88, 255)
//			);
			
post.add([\postln, "SYSTab:" + sysTab.name + "refScaleY:" + e.refScaleY, 0])
	}})
);

(
DrawDef(\SYSTab_last, \calc, nil,
	{{|e, drawSysY0, midiScale, midiMin, midiMax, yAddTop, yAddBot, xAddLeft, xAddRight, 
		refYmin, refYmax, scale, scaleDelta, scaledView, post|
		
		e.xMin = e.xMin * scale[0] - xAddLeft;
		e.xMax = e.xMax.max(e.offX) * scale[0] + xAddRight;
		e.yMin = (e.midi2yPsM(midiMax) - yAddTop).min(refYmin); 
		e.yMax = (e.midi2yPsM(midiMin) + scaledView.scaledTransError[1] + yAddBot).max(refYmax);
		
		e.canvasBounds = Rect.fromPoints(e.xMin@e.yMin, e.xMax@e.yMax)
			.moveBy(0, scaleDelta[1]);

		if (scaledView.canvasBounds != e.canvasBounds) { 
				// this.logln("upd canvas:" + canvasBounds, lfB:2); 
			scaledView.canvasBounds_(e.canvasBounds, false); //shows next drawing only
		}; 
						
		post.add([\postln, "midiMax, midiMin:" + [midiMax, midiMin], 1]);
		post.add([\postln, "refYmin, refYmax:" + [refYmin, refYmax]]);
		post.add([\postln, "drawSysY0, yMin, yMax:" + [e.drawSysY0, e.yMin, e.yMax]]);
		post.add([\postln, "canvasBounds:" + e.canvasBounds, 0, 1]);
		
	}})
);

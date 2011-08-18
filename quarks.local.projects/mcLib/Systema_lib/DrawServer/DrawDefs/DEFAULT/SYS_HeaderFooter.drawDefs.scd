if (this.isLog(2)) { ("\n\n" + thisProcess.nowExecutingPath.basename + "\n").postln; };

(
DrawDef(\SYS_HeaderFooter, \movedX, nil
	,{|node, drawServer, pE, sysSym, sysProtoSym, sysTab, self, label, footMode, sysLGab, sysRGab|
		var sysCalc = pE.sysCalcs[sysSym], sel = sysTab.mlSel;
		var labelHRect = Rect.new, labelFRect = Rect.new, fCount;
		// store back vars
		sysCalc.label = label; sysCalc.footMode = fCount = footMode;
		// actions
		drawServer.addField(MovedXActionField, labelHRect, {|sV, cC, mod| case
				{ cC == 2 } { 
					SCRequestString(sysCalc.label, "new label:", {|str| sysCalc.label = str }) }
				{ mod == 262401 } { sel.select(0, self); true } // ctrl
				{ mod == 131330 } { pE.strDisplayPsM(labelHRect.center, "SYS."++sysSym) } // shift
				{ mod == 524576 } { pE.strDisplayPsM(labelHRect.center, 
					"proto: SYS."++sysProtoSym, nil, Color.yellow) } // alt
				{ mod == 1179914 } { drawServer.beginDragAction = {|v, x, y, mod| // shif-cmd
					var str = "SYS."++sysSym; v.dragLabel = str; str.asSymbol} }
				{ mod == 1573160 } { drawServer.beginDragAction = {|v, x, y, mod| // alt-cmd
					var str = "SYS."++sysProtoSym; v.dragLabel = str; str.asSymbol} }
				{ false } }
			,{|sV, fwdArgs| fwdArgs}
			, {|sV, fwdArgs| if (fwdArgs.isKindOf(FuncNode)) { fwdArgs.remove } });
		drawServer.addField(MovedXActionField, labelFRect, {|sV, cC, mod| 
			mod.switch(
				262401, { pE.refSysSym = sysSym }
				,524576, { fCount = fCount + cC; // alt
					sysCalc.footMode = fCount.mod(pE.footModeColors.size) }
				,655650, { fCount = fCount - cC; // shift-alt
					sysCalc.footMode = fCount.mod(pE.footModeColors.size) }
				, { false }) });
								
		{|e, scaledView, trans, refSysSym, sysOffX, labelHheight, labelFheight, footerTop,
			footStrFuncs, footModeColors, labelHFont, labelHColor, yHroom, labelHGab, labelFFont,
			labelFGab, selColor, selFColor, outline, selOn|
			var footStr, labelFColor;
			// header
			var xLabelH = sysCalc.xCoreL - sysOffX;
			var labelHWidth = sysCalc.coreWidth + (2 * sysOffX);
			var nsHRect = Rect(xLabelH, yHroom, labelHWidth, labelHheight);
			var nsBlackHRect = nsHRect.insetBy(0, labelHGab / -2)
				.moveBy(sysLGab * -1, 0).width_(nsHRect.width + sysLGab + sysRGab);
			
			labelHRect.set(*scaledView.transScaleX(nsHRect).asArray);
			Color.black.setFill; Pen.fillRect(scaledView.transScaleX(nsBlackHRect));
			
			if(selOn && (sysTab.mlSel.selected.first == self)) { Pen.use{
				Pen.setShadow(2@2, 20, Color.white);
				// Pen.beginTransparencyLayer; // only one object
				selColor.setFill; Pen.fillRect(labelHRect.copy.height_(labelHRect.height + 1)); 
				// Pen.endTransparencyLayer 
			}};
			
			if (sysCalc.label.bounds(labelHFont).width > labelHRect.width) {
				sysCalc.label.drawInRect(labelHRect.insetBy(0, -2), labelHFont, labelHColor)
			}{
				sysCalc.label.drawCenteredIn(labelHRect, labelHFont, labelHColor)
			};
		
			// footer
			if (sysCalc.footMode != 0) { 
				if (sysCalc.sys.notNil) {
					footStr = footStrFuncs[sysCalc.footMode].value(sysCalc);
					labelFColor = footModeColors[sysCalc.footMode]
				}{ÊfootStr = "nil"; labelFColor = Color.red };
				
				labelFRect.set(*scaledView.transScaleX(
					Rect(sysCalc.xCoreL, footerTop, sysCalc.coreWidth, labelFheight)).asArray);
				Pen.use { Pen.smoothing_(false);
					Pen.setShadow(0@1, 5, Color.new255(0, 0, 0, 255));
					Color.new255(0, 0, 0, 150).setFill; 
					Pen.fillRect(labelFRect.insetBy(labelFGab / -2));
					
					if(selOn && (refSysSym == sysSym)) { Pen.use{
						Pen.setShadow(2@2, 20, Color.white);
						// Pen.beginTransparencyLayer; // only one object
						selFColor.setFill; Pen.fillRect(labelFRect); 
						// Pen.endTransparencyLayer 
					}};
					
					if (footStr.bounds(labelFFont).width > labelFRect.width) {
						footStr.drawInRect(labelFRect, labelFFont, labelFColor)
					}{
						footStr.drawCenteredIn(labelFRect, labelFFont, labelFColor)
					};
				};
				
				if (outline) { // Pen.width = 2;
					labelHColor.setStroke; Pen.strokeRect( labelHRect );
					labelFColor.setStroke; Pen.strokeRect( labelFRect ) }
			}
	}}
//	,{{|e, drawServer, labelHAction| //this.logln("actionFields" + drawServer.actionFields);
//		drawServer.removeClickField(labelHAction)
//	}} //works! but all actions are cleared on rebuild anyway
	);
);
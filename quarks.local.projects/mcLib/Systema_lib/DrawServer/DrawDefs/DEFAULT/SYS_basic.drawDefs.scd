if (this.isLog(2)) { ("\n\n" + thisProcess.nowExecutingPath.basename + "\n").postln; };

(
DrawDef(\SYS_basic, \scaled,
	{|node, drawServer, pE, e, sysSym, spineW, ribColor, ribWL, ribWR,  ribWrootL, ribWrootR, 
		ribExtMaxL, ribExtMaxR, coreLGab, coreRGab, sysLGab, sysRGab, annoGab, annoLabelFrameW,
		tcLabel, rootLabel, tcFont, rootFont, tcMarkW, midiOffset, sysTab, self, sysProtoSym,
		selectedDegrees|
		var sysCalc, sel = sysTab.mlSel; //and more things that will not change interactively:
		var sys, sysSize = 0, sysUpdFunc, sysCalcFunc, sysDepFunc, makeDegreeActionsFunc; 
		var spineWL = spineW.div(2), spineWR = if (spineW.even) { spineWL } { spineWL + 1};
		var coreL, coreR;
		
		//sysCalc  
		//	(1) store data used outside of this func scope
		//	(2) store data that survive DrawTree rebuilds
		// 	(3) store data at the key of the defaults to be written back at rebuilds!
		if (pE.sysCalcs[sysSym].isNil) { pE.sysCalcs[sysSym] = sysCalc = (); sysCalc.restore = false;
			// store back vars 
			sysCalc.sysProtoSym = if (sysProtoSym == \nil) { sysSym } { sysProtoSym };
			sysCalc.midiOffset = midiOffset; sysCalc.selectedDegrees = selectedDegrees;
			// y
			[\midiInts, \ribExtLs, \ribExtRs, \ribColors, \ribWLs, \ribWRs]
				 .do{|sym| sysCalc[sym] = List[] };
			// anno 
			sysCalc.rootAnnoRect = rootLabel.bounds(rootFont).insetBy(annoLabelFrameW * -1);
			sysCalc.tcAnnoRect = tcLabel.bounds(tcFont).insetBy(annoLabelFrameW * -1);
		
			// action vars
			[\coreRect, \sysRect, \tcRect].do{|sym| sysCalc[sym] = Rect.new };
			[\degreeActionRects, \degeeFields].do{|sym| sysCalc[sym] = List[] };
			sysCalc.refToneSynths = [];
					
		}{ sysCalc = pE.sysCalcs[sysSym]; sysCalc.restore = true };
		
		// actions		
		drawServer.addField(ScaledActionField, sysCalc.coreRect, {|sV, cC, mod| case
			{ (cC == 3) && (mod == 262401) } { sysCalc.selectedDegrees = Set[] } // ctrl click:2
			{ mod == 262401} { sel.select(0, self); false } // ctrl
			{ mod == 1048840 } { sys !? {sys.play}; true }  //cmd
			{ mod == 1179914 } { sys !? {sys.playR}; true } //shift-cmd
			{ mod == 1573160 } { drawServer.beginDragAction = {|v, x, y, mod| // alt-cmd
				if (sys.notNil) { v.dragLabel =Ê"RCTuning [" ++ sys.tuning.size ++ "]:" 
					+ sys.tuning.name; sys.tuning }{ nil } } }
			{ mod.isCmd } { true } { false }	// swallow all cmd
		});
		
		//restore action fields in correct order!
		if (sysCalc.restore) { drawServer.actionFields.addAll(sysCalc.degeeFields) };
		
		makeDegreeActionsFunc = {|scaleDegrees| 
			sysCalc.degeeFields.do{|field| drawServer.removeField(field) };
			sysCalc.refToneSynths.do{|rts| rts !? { rts.releaseSynth(0.2) } };
			sysCalc.refToneSynths = Array.newClear(scaleDegrees.size);
			sysCalc.degeeFields.clear;  sysCalc.degreeActionRects.clear;
			scaleDegrees.do{|degree, i| 
				var aRect = Rect.new;
				sysCalc.degreeActionRects.add(aRect);
				sysCalc.degeeFields.add( 
					drawServer.addField(ScaledActionField, aRect, {|sV, cC, mod| 
						var refSynths; //if cmd is pressed cC is always 1 !
						if (sys.isNil) { false }{ mod.switch(
							262401, { sel.select(0, self);
								if (sysCalc.selectedDegrees.includes(i)) { 
									sysCalc.selectedDegrees.remove(i)
								}{ sysCalc.selectedDegrees = Set[i] } } //ctrl
							,393475, { sel.select(0, self);
								if (sysCalc.selectedDegrees.includes(i)) { 
									sysCalc.selectedDegrees.remove(i)
								}{ sysCalc.selectedDegrees.add(i) } } //shift-ctrl
							,1573160, { drawServer.beginDragAction = {|v, x, y, mod| 
									v.dragLabel = "play a step"; nil };
								sys.play(1, [degree]); true } // alt-cmd
							,1704234, { drawServer.beginDragAction = {|v, x, y, mod| 
									v.dragLabel = "set a step to continious sine"; nil };
								refSynths = sysCalc.refToneSynths; //shift-alt-cmd
								if (refSynths[i].isNil) { refSynths[i] = 
									{|freq| SinOsc.ar(freq, 0, 0.2) }.play(fadeTime: 0.2, 
										args: [\freq, sys.degreeToFreq(degree)]);
				// degreeToFreq ?? -> get Key then apply offset
								}{ refSynths[i].releaseSynth(0.2); refSynths[i] = nil } }
							,1835305, { drawServer.beginDragAction = {|v, x, y, mod| 
									v.dragLabel = "reset sine steps"; nil };
								sysCalc.refToneSynths = sysCalc.refToneSynths.collect{|rts|
									rts !? { rts.releaseSynth(0.2) }; nil } } // ctl-alt-cmd
							,1310985, { drawServer.beginDragAction = {|v, x, y, mod| // ctrl-cmd
								v.dragLabel = "drag on midi reference"; 
								sys.at(degree) + sysCalc.midiRef }; true }
							, { false } ) }
				}));
			}};
			
		// Systema data
		// updates: 2 cases: midiRoot may change frequently but tuning and annotations change rarely  
		sysUpdFunc = { // independent of e
			var midiInts, rootIndex, tcIndex, tcRi, sysMidiRef, midinotes;
			sysCalc.ribWLs.clear; sysCalc.ribWRs.clear; 
			sysCalc.ribExtLs.clear; sysCalc.ribExtRs.clear;
			
			sysCalc.rootIndex = rootIndex = sys.rootIndex;
			sysCalc.tcIndex = tcIndex = sys.scaleDegrees.indexOf(sys.tcDegree);
			sysCalc.midiInts = midiInts = pE.sysMidiIntsPsM(sys);
			sysCalc.intTC = if (tcIndex.notNil) { midiInts[tcIndex] }{ sys.at(sys.tcDegree) };
			
			if (sysSize != midiInts.size) { makeDegreeActionsFunc.value(sys.scaleDegrees) };
			sysSize = midiInts.size;
			
			midiInts.do{|item, i|
				if (i != rootIndex) { // all of those could be individualised later
					sysCalc.ribWLs.add(ribWL);
					sysCalc.ribWRs.add(ribWR);
				}{
					sysCalc.ribWLs.add(ribWrootL);
					sysCalc.ribWRs.add(ribWrootR);
				};
				sysCalc.ribExtLs.add(ribExtMaxL + spineWL * -1);//rib extent might be individualised
				sysCalc.ribExtRs.add(ribExtMaxR + spineWR);     // but must be <= ribExtMaxL/R
				sysCalc.ribColors.add(ribColor);
			};
			sysCalc.indexMin = midiInts.minIndex;
			sysCalc.indexMax = midiInts.maxIndex;
			
			tcRi = tcIndex ? rootIndex; // if tonal center is not in SYS, use extents of root
			sysCalc.tcRect.set(*Rect(sysCalc.ribExtLs[tcRi], 0, 
				sysCalc.ribExtRs[tcRi] - sysCalc.ribExtLs[tcRi],
				sysCalc.ribWLs[tcRi].max(sysCalc.ribWRs[tcRi])).insetBy(tcMarkW * -1).asArray);
			sysCalc.tcAnnoRect.left = sysCalc.ribExtRs[tcRi] + annoGab;
			sysCalc.rootAnnoRect.left = sysCalc.ribExtLs[rootIndex] 
				- annoGab - sysCalc.rootAnnoRect.width;
			
			sysCalc.midiRoot = sys.midiRoot;
			sysCalc.midiOffset = sys.midiOffset;
			sysCalc.midiRef = sysMidiRef = sysCalc.midiRoot + sysCalc.midiOffset;
			sysCalc.midiTC = sysCalc.intTC + sysMidiRef;
			sysCalc.midinotes = midinotes = sysCalc.midiInts.collect{|mInt| sysMidiRef + mInt };
			sysCalc.refToneSynths.do{|rt, i| rt !? { rt.set(\freq, midinotes[i].midicps)} }
		};
		node.addDependantFuncTo(Systema, {|who, what, name| //this.logln("sCD:" + [who, what, name]);
			what.switch(
				\destroy, { if (name == sysSym) { sysCalc.sys = sys = nil;
					 makeDegreeActionsFunc.value([]) } }, 
				\new, { if (name == sysSym) { sysCalc.sys = sys = Systema.at(name); 
					sysUpdFunc.value; node.addDependantFuncTo(sys, sysDepFunc) }Ê} )});
		sysCalcFunc = { // dependent on e anyway
			var midinotes = sysCalc.midinotes, midiTC = sysCalc.midiTC, nsYs;
			
			e.midiMin = midinotes[sysCalc.indexMin].min(midiTC).min(e.midiMin);
			e.midiMax = midinotes[sysCalc.indexMax].max(midiTC).max(e.midiMax);
			
			sysCalc.nsYs = nsYs = pE.midis2nsYsPsM(midinotes);
			sysCalc.nsY0 = sysCalc.nsYs[sysCalc.rootIndex];
			sysCalc.nsYTC = pE.midi2nsYPsM(midiTC);
			if (pE.tcOn) {
				sysCalc.rootAnnoRect.top = sysCalc.nsY0 - (sysCalc.rootAnnoRect.height / 2);
				sysCalc.tcAnnoRect.top = sysCalc.nsYTC - (sysCalc.tcAnnoRect.height / 2);
				sysCalc.tcRect.top = sysCalc.nsYTC - (sysCalc.tcRect.height / 2);
				sysCalc.tcIndex ?? {
					if (sysCalc.tcAnnoRect.top < nsYs[sysCalc.indexMax]) { 
						e.yAddTop = e.yAddTop.max((sysCalc.tcAnnoRect.height / 2) * pE.scale[1] 
							+ e.drawSysYgTop + pE.labelHGab) };
					if (sysCalc.tcAnnoRect.bottom > nsYs[sysCalc.indexMin]) { 
						e.yAddBot = e.yAddBot.max((sysCalc.tcAnnoRect.height / 2) * pE.scale[1] + 
						e.drawSysYgBot + pE.labelFGab) } }
			}
		};
		sysDepFunc = {|who, what ...args| // this.logln("sysDepFunc:" + [who, what, args]);
			what.switch(\midiRoot, { sysUpdFunc.value },\midiOffset, { sysUpdFunc.value } ) };
		// check if sys is still valid after rebuild:
		sys = Systema.at(sysSym);
		if ((sysCalc.sys != sys) && sys.notNil) { sysUpdFunc.value 
		}{ if (sys.isNil && sysCalc.restore.not) { //then we need defaults for coreRect:
				sysCalc.nsYs = pE.midis2nsYsPsM([e.midiMin, e.midiMax]); 
				sysCalc.nsYTC = sysCalc.nsYs[0]; sysCalc.indexMin = 0; sysCalc.indexMax = 1 }
		};
		sysCalc.sys = sys;
		sys !? { node.addDependantFuncTo(sys, sysDepFunc) }; //dependencies were removed on rebuild
		{|e, post|
			if (pE.tcOn) {ÊcoreLGab = coreLGab.max(sysCalc.rootAnnoRect.width + annoGab); 
				coreRGab = coreRGab.max(sysCalc.tcAnnoRect.width + annoGab) };
			sysCalc.coreL = coreL = (spineWL + ribExtMaxL + coreLGab) * -1; 
			sysCalc.coreR = coreR = spineWR + ribExtMaxR + coreRGab;
			sysCalc.coreWidth = coreR - coreL;
			sysCalc.maxL = coreL - sysLGab; sysCalc.maxR = coreR + sysRGab;
			// above necessary for spacing, below only if actually drawn
			sys !? { sysCalcFunc.value };

post.add([\postln, "SYS:" + SYS.at(sysSym), 1]);
	}}
	,{|node, pE, sysSym, refToneColor, spineColor, spineW, tcDashes, tcMarkW, tcColor, rootColor,
		tcLabel, rootLabel, tcFont, rootFont, selHeight, sysTab, self, annoGab|
		var sel = sysTab.mlSel, sysCalc = pE.sysCalcs[sysSym];
		var updDegreeActionRectsFunc = {|scaleV|
			var ribWLs = sysCalc.ribWLs, ribWRs = sysCalc.ribWRs;
			var ribExtLs = sysCalc.ribExtLs, ribExtRs = sysCalc.ribExtRs;
			sysCalc.degreeActionRects.do{|rect, i| 
				var x = ribExtLs[i] + sysCalc.xSpine, width = ribExtRs[i] - ribExtLs[i] + spineW;
				var height = (ribWLs[i].max(ribWRs[i]) + selHeight) / scaleV;
				rect.set(x, sysCalc.nsYs[i] - (height / 2), width, height) }
		};
		{|e, sysCalcs, offX, sysOffX, outline, selOn, selColor, scale, tcOn, post|
			//y  -> do calcs not need elsewhere and thus are not in sysCalc
			var maxSpY = sysCalc.nsYs[sysCalc.indexMin], minSpY = sysCalc.nsYs[sysCalc.indexMax];
			var nsYTC = sysCalc.nsYTC, maxY = maxSpY.max(nsYTC), minY = minSpY.min(nsYTC);
			var height = maxY - minY;
			//x
			sysCalc.width = sysCalc.maxR - sysCalc.maxL;
			sysCalc.xL = offX + sysOffX;
			sysCalc.xSpine = sysCalc.xL - sysCalc.maxL;
			sysCalc.xCoreL = sysCalc.xSpine + sysCalc.coreL;
			e.offX = sysCalc.xL + sysCalc.width + sysOffX;

			sysCalc.coreRect.set(sysCalc.xCoreL, minY, sysCalc.coreWidth, height);
			sysCalc.sysRect.set(sysCalc.xL, minY, sysCalc.width, height);
		
			if (sysCalc.sys.isNil) {
				Color.red.setFill; Pen.fillRect(sysCalc.coreRect);
				pE.missingSysPsM(sysSym, sysCalc.coreRect.center.x);
			}{
				updDegreeActionRectsFunc.value(scale[1]);
//show only the selected degreeÉ to come soonÉ	
				if(selOn) {
					if (sel.selected(0)[0] == self) {
						Pen.use {
							Pen.setShadow(2@4, 20, Color.white);
							Pen.beginTransparencyLayer;
							selColor.setFill; // Pen.fillRect(sysCalc.coreRect); 
							//Color.new255(255, 157, 172, 180).setFill;
							sysCalc.selectedDegrees.do{|i| 
								Pen.fillRect(sysCalc.degreeActionRects[i].copy
								.left_(sysCalc.xCoreL).width_(sysCalc.coreWidth)) };
							Pen.endTransparencyLayer 
				}}};
				if(outline) {	
					Color.red.setStroke; Pen.strokeRect(sysCalc.coreRect);
					Color.yellow.setStroke; Pen.strokeRect(sysCalc.sysRect);
					sysCalc.degreeActionRects.do{|rect| Pen.strokeRect(rect)}
				};
				Pen.translate(sysCalc.xSpine, 0);
				Pen.use{  Pen.setShadow(1@ -1, 7, Color.black);
					Pen.use{
						var ribWLs = sysCalc.ribWLs, ribWRs = sysCalc.ribWRs;
						var ribExtLs = sysCalc.ribExtLs, ribExtRs = sysCalc.ribExtRs;
						var ribColors = sysCalc.ribColors, rootIndex = sysCalc.rootIndex;
						var refTones = sysCalc.refToneSynths;
						
						Pen.beginTransparencyLayer;
						sysCalc.nsYs.do{|y, i| ribColors[i].setStroke; 
							if (refTones[i].isNil) {spineColor.setStroke}Ê{refToneColor.setStroke};
							Pen.width = ribWLs[i]; Pen.line(ribExtLs[i]@y, 0@y); Pen.stroke;
							Pen.width = ribWRs[i]; Pen.line(0@y, ribExtRs[i]@y); Pen.stroke
						};
						spineColor.setStroke; Pen.width = spineW; 
						Pen.line(0@minSpY, 0@maxSpY); Pen.stroke;
						Pen.endTransparencyLayer;
					};
					if(tcOn) {					
						tcColor.setStroke;
						sysCalc.tcIndex  ?? { 
							Pen.use{ Pen.width = spineW; Pen.lineDash = tcDashes;
								if (nsYTC > maxSpY) { 
									Pen.line(0@nsYTC, 0@maxSpY) }{ Pen.line(0@nsYTC, 0@minSpY) };
								Pen.stroke }
						};
						Pen.width = tcMarkW; Pen.strokeRect(sysCalc.tcRect);
						Color.black.setFill; 
						Pen.fillRect(sysCalc.tcAnnoRect); Pen.fillRect(sysCalc.rootAnnoRect);
						Pen.use{ // Pen.smoothing_(false);
							tcLabel.drawCenteredIn(sysCalc.tcAnnoRect, tcFont, tcColor);
							rootLabel.drawCenteredIn(sysCalc.rootAnnoRect, rootFont, rootColor) }
					}
				};
				if (outline && tcOn) { 
					Pen.width = 1; tcColor.setStroke; 
					Pen.strokeRect(sysCalc.tcRect); Pen.strokeRect(sysCalc.tcAnnoRect);
					rootColor.setStroke; Pen.strokeRect(sysCalc.rootAnnoRect);
				}
			}
//post.add([\postln, "sysCalcs:" + [sysCalcs], 1])
	}});
);	

(
DrawDef(\SYS_anno_inst, \scaled,
	{|node, align|
		{|e, post| 
				
post.add([\postln, "instAnno align:" + align])
	}}
	, {|node, align|
		{|e, post| 
				
post.add([\postln, "node, align:" + [node, align]])
	}});
);

(
DrawDef(\SYS_ano_voc, \scaled,
	{|node, align|
		{|e, post| 
			
post.add([\postln, "vocAnno align:" + align])
	}}
	, {|node, align|
		{|e, post| 

post.add([\postln, "node, align:" + [node, align]])
	}})
);
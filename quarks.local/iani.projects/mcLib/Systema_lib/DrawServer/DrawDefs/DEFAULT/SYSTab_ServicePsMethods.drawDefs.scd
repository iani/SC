if (this.isLog(2)) { ("\n\n" + thisProcess.nowExecutingPath.basename + "\n").postln; };

(
DrawDef(\SYSTab_ServicePsMethods, \calc,
	{|node, pE, e, sysTab|
		// global Pseudo-Methods -> ..PsM nameing convention:
		pE.y2midiPsM = {|pE, y| pE.y2midiIntPsM(y) + e.midiRef }; // here self is pE 
			//attention with funcs: first arg is always the envir itself -> ..PsM indstead of ..Func
			//this.logln("pE.y2midiFunc" + pE.y2midiFunc); // func is valued -> crash without args!
			//this.logln("pE.y2midiFunc" + pE[\y2midiFunc]); // access works like this
		pE.y2midiIntPsM = {|pE, y| e.drawSysY0 - y / e.midiScale };
		
		pE.midi2yPsM = {|pE, midinote| pE.midiInt2yPsM(midinote - e.midiRef) };
		pE.midi2nsYPsM = {|pE, midinote| pE.midiInt2nsYPsM(midinote - e.midiRef) };

		pE.midiInt2yPsM = {|pE, midiInt| e.drawSysY0 - (midiInt * e.midiScale * e.scale[1])};
		pE.midiInt2nsYPsM = {|pE, midiInt| e.drawSysY0 - (midiInt * e.midiScale)};

		// collect versions -- more effecient when iterating of collections:
		pE.ys2midiIntsPsM = {|pE, ys| 
			var drawSysY0 = e.drawSysY0, midiScale = e.midiScale;
			ys.collect{|y| drawSysY0 - y / midiScale } };
		pE.ys2midisPsM = {|pE, ys| 
			var drawSysY0 = e.drawSysY0, midiScale = e.midiScale, midiRef = e.midiRef;
			ys.collect{|y| drawSysY0 - y / midiScale + midiRef } };
		pE.midiInts2ysPsM = {|pE, midiInts| 
			var drawSysY0 = e.drawSysY0, midiScale = e.midiScale, scale = e.scale[1];
			midiInts.collect{|midiInt| drawSysY0 - ( midiInt * midiScale * scale) } };
		pE.midis2ysPsM = {|pE, midinotes| 
			var drawSysY0 = e.drawSysY0, midiScale = e.midiScale, scale = e.scale[1];
			var midiRef = e.midiRef;
			midinotes.collect{|midi| drawSysY0 - ((midi - midiRef) * midiScale * scale) } };
		pE.midis2nsYsPsM = {|pE, midinotes| //ns -> non scaled
			var drawSysY0 = e.drawSysY0, midiScale = e.midiScale;
			var midiRef = e.midiRef;
			midinotes.collect{|midi| drawSysY0 - ((midi - midiRef) * midiScale) } };
		// sys related:
		pE.sysMidiIntsPsM = {|pE, sys|
			sys.scaleIndices.collect{|deg| sys.tuning.degToC(deg) * 0.01 } 
			// sys.tuning.geti(sys.scaleIndices).postln --> Systema still buggy !!! //most economic
			}; 
		pE.sysMidinotesPsM = {|pE, sys|
			var midiRoot = sys.midiRoot;
			sys.scaleIndices.collect{|deg| sys.tuning.degToC(deg) * 0.01 + midiRoot} };
			
		pE.footStrFuncs = [
			{""},
			{|sysCalc| "root:" + sysCalc.midinotes[sysCalc.rootIndex].round(0.01) },
			{|sysCalc| "Freq0:" + sysCalc.midinotes[sysCalc.rootIndex].midicps.round(0.1) },
			{|sysCalc| "delta:" + (sysCalc.midinotes[sysCalc.rootIndex] - pE.midiRef * 100)
				.round(0.1) },
			{|sysCalc| (sysCalc.midinotes[sysCalc.rootIndex] - pE.midiRef)
				.midiratio.asBestRatio.mcFormat(14, \c) },
			{|sysCalc| "SYS0:" + sysCalc.midiRoot.round(0.01) },
			{|sysCalc| "SYSf0:" + sysCalc.midiRoot.midicps.round(0.1) },
			{|sysCalc| "offset:" + (sysCalc.midiOffset * 100).round(0.1) },
			{|sysCalc| sysCalc.midiOffset.midiratio.asBestRatio.mcFormat(14, \c) },
			{|sysCalc| "ToneC:" + sysCalc.midiTC.round(0.01) },
			{|sysCalc| "TCFrq:" + sysCalc.midiTC.midicps.round(0.1) },
			{|sysCalc| "TCdlt:" + (sysCalc.midiTC - pE.midiRef * 100).round(0.1) },
			{|sysCalc| (sysCalc.midiTC - pE.midiRef).midiratio.asBestRatio.mcFormat(14, \c) }];	
		pE.missingSysPsM = {|pE, sysSym, midX|
			var font = Font("Helvetica", 16), str = "SYS." ++ sysSym + "\tmissing"; 
			var strBounds = str.bounds(font), cP = midX @ (e.drawSysH / 2 + e.drawSysYgTop);
			var rect = strBounds.center_(cP).insetBy((e.drawSysH - strBounds.width).min(0) * -1, 0);
			Pen.use{ Pen.rotate(pi/ -2, cP.x, cP.y); str.drawInRect(rect, font, Color.white) }};
			
		pE.strDisplayPsM = {|pE, cP, str, font, color, bgColor| font = font ?? { pE.labelHFont };
			DrawFunc(\strDisplay, (cP: cP, str: str, font: font, color: color, bgColor: bgColor) 
				,sysTab.drawNode, 1) };
		
		// still dummies:
		pE.sysStepColorsPsM = {|pE, sys|
			Array.fill(sys.size, Color.new255(252, 202, 156, 210)) };
		pE.sysStepMidiStrPsM = {|pE, sys| "not yet" };
			
			
		{|e|
		
}})
);		
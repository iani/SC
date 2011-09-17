if (this.isLog(2)) { ("\n\n" + thisProcess.nowExecutingPath.basename + "\n").postln; };

(
DrawDef(\SYSTab_KeysLegend, \draw, nil, 
	{|node, drawServer, pE|
		var displayRect, strH;
		
		drawServer.addActionChar($k, {|down, repeat| if (down && repeat.not) {
			pE.keysOn = pE.keysOn.not }; true });
		
		{|e, view, keysOn, labelHFont, labelHColor, annoFont, post|
			if (keysOn) {
				displayRect = view.drawBounds.insetBy(35);
				strH = "A".bounds(labelHFont).height;
				Color.grey.set; Pen.fillRect(displayRect); Color.white.set; 
				Pen.width = 4; Pen.strokeRect( view.drawBounds.insetBy(20) );
				Pen.width = 2 ;Pen.strokeRect( displayRect );
				"Keys legend:".drawInRect(displayRect.moveBy(8, 10), labelHFont, labelHColor);
				["modifiers \tkey \t\ttype \t\tstate \tdescription"
				,""
				,"\t\tk \t\ton/of \t" ++ e.keysOn ++ "\t\tshow this legend"
				,"\t\tm \t\ton/of \t" ++ e.mouseOn ++ "\t\tshow mouse legend"
				,"alt \t\t0..9 \t\tmode \t" ++ e.scaleMode ++ "\t\tchange Y-resolution of display"
					+ "by octave ranges: " + e.octaveRanges
				// ,"\t\t\t\t\t\t\t\t" + e.octaveRanges
				,"\t\tr,R \t\tmode \t" ++ e.refMode 
					++ "\t\tdraw reference lines (none, base, freqency, midi note)"
				,"\t\tq,Q \t\tmode \t" ++ e.quantMode ++ "\t\tquantisation of reference lines by"
					+ "fractions of a tone (of 200 cent):  [1, 1/2, 1/4, 1/8]"
				,"\t\ty \t\tselect \t\t\tuse steps of currently selected Systema"
					+ "to draw reference lines"
				,"\t\tg \t\ton/of \t" ++ e.gridOn ++ "\t\ttoggle reference line grid"
				,"\t\ta,A \t\tmode \t" ++ e.anchorMode++"\t\tanchor reference (centre, bottom, top)"
				,"\t\ts \t\ton/of \t" ++ e.selOn ++ "\t\tshow selected objects"
				,""
				,"alt \t\tf \t\ton/of \t" ++ e.drawServer.isFullScreen ++ "\t\tfull screen mode"
				,"alt \t\th \t\ton/of \t" ++ e.drawServer.scrollersOn
					++ "\t\thide/show scroll & scale handles"
				,"alt \t\tz \t\tdo \t\t\t \tequal zoom ratio adjusted from smaller value"
				,"alt \t\tZ \t\tdo \t\t\t \tequal zoom ratio adjusted from bigger value"
				,""
				,"alt \t\tp \t\ton/of \t("++ e.drawServer.isPause ++ ")\tpause frame iteration of"
					+ "draw functions"
				,"alt \t\td \t\ton/of \t" ++ e.isPost ++ "\t\tdebug posts"
				,"alt \t\tD \t\ton/of \t" ++ e.isDebug ++ "\t\tdebug posts on grey background"
				,"\t\to \t\ton/of \t" ++ e.outline ++ "\t\tdraw outlines of objects"
				].do{|str, i|
					str.drawInRect(displayRect.moveBy(8, 40 + (i*strH)), labelHFont, labelHColor)};
			};	
//post.add([\postln, "keysOn:" + [keysOn]])
	}});
);

(
DrawDef(\SYSTab_MouseLegend, \draw, nil, 
	{|node, drawServer, pE|
		var displayRect, strH;
		
		drawServer.addActionChar($m, {|down, repeat| if (down && repeat.not) {
			pE.mouseOn = pE.mouseOn.not }; true });
			
		{|e, view, mouseOn, labelHFont, labelFColor, annoFont, post|
			if (mouseOn) {
				displayRect = view.drawBounds.insetBy(70);
				strH = "A".bounds(labelHFont).height;
				Color.grey.set; Pen.fillRect(displayRect); Color.yellow.set; 
				Pen.width = 4; Pen.strokeRect( view.drawBounds.insetBy(50) );
				Pen.width = 2 ;Pen.strokeRect( displayRect );
				"Mouse legend:".drawInRect(displayRect.moveBy(8, 10), labelHFont, labelFColor);
				["modifiers \t\tbutton \tclick \t\thow \t\t\tdescription"
				,""
				,"General"
				,"shift \t\t\tany \t\t\move \tup/down \t\tscale vertical resolution"
				,"shift \t\t\tany \t\t\move \tleft/right \t\tscale gab between Systemata"
				,"shift \t\t\tany \t\t\> 3 \t\t\t \t\treset vertical resolution"
				
				,"alt \t\t\tany \t\t\move \tup/down \t\tmove canvas vertically"
				,"alt \t\t\tany \t\t\move \tleft/right \t\tmove canvas horizontally"
				,"alt \t\t\tany \t\t\> 3 \t\t\t \t\treset moves to origin"
				
				,"shift-alt \t\tany \t\t\move \tup/down \t\tzoom vertically"
				,"shift-alt \t\tany \t\t\move \tleft/right \t\tzoom horizontally"
				,"shift-alt \t\tany \t\t\> 3 \t\t\t \t\treset zoom"
				,""
				,"alt-cmd \t\tany \t\t\= 1 \t\toutside SYS \tplay the clicked position"
				,"shift-alt-cmd \tany \t\t\= 1 \t\toutside SYS \tcontiniously sound the position"
				,"ctrl-alt-cmd \tany \t\t\= 1 \t\toutside SYS \tdelete all sounding positions"
				,"\t\t\tany \t\t\move \tposition \t\tchange sounding position"
				,""
				,"Selection"
				,"\t\t\tany \t\t\= 1 \t\titem \t\t\tselect item"
				,"shift \t\t\tany \t\t\= 1 \t\titem \t\t\t\add to selection"
				,""
				,"Systema (SYS)"
				,"\t\t\tany \t\t\= 2 \t\tlabel \t\tchange label string"
				,"cmd \t\t\tany \t\t\= 1 \t\tSystema \t\tplay"
				,"shift-cmd \t\tany \t\t\= 1 \t\tSystema \t\tplayR (reverse)"
				,"alt-cmd \t\tany \t\t\= 1 \t\tstep of SYS \tplay the step"
				,"shift-alt-cmd \tany \t\t\= 1 \t\tstep of SYS \tcontiniously sound the step"
				,"ctrl-alt-cmd \tany \t\t\= 1 \t\tstep of SYS \treset all sounding steps"
			
				].do{|str, i|
					str.drawInRect(displayRect.moveBy(8, 40 + (i*strH)), labelHFont, labelFColor)};
			};	
//post.add([\postln, "keysOn:" + [keysOn]])
	}});
);
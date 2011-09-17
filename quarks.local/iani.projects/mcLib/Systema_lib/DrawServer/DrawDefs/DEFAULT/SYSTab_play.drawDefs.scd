if (this.isLog(2)) { ("\n\n" + thisProcess.nowExecutingPath.basename + "\n").postln; };

(
DrawDef(\SYSTab_play, \draw,
	{|node, drawServer, pE|  
		var totalRect = Rect.new, rtE, refToneFunc;
		if (pE.refToneEnvir.isNil) { pE.refToneEnvir = rtE = () }{ rtE = pE.refToneEnvir;
			rtE.do{|rectE| drawServer.actionFields.add(rectE.field) } }; //restore action fields
		
		refToneFunc = {|y|
			var synth, rect = Rect.new, rectE = ();
			rtE.put(rect, rectE);
			rectE.midi = pE.y2midiPsM(y);
			rectE.rtSynth = synth = {|freq| SinOsc.ar(freq, 0, 0.2) }
				.play( fadeTime: 0.2, args: [\freq, rectE.midi.midicps]);
			rectE.field = drawServer.addField(ActionField, rect, {|sV, cC, mod| // shift-alt-cmd
				if (mod == 1704234) { synth.releaseSynth(0.2); drawServer.removeField(rectE.field);
					rtE.removeAt(rect) }; true //swallow
				},{|sV, args, mod, cP, p|
					var midi = pE.y2midiPsM(sV.convertBwd(0, p.y)[1]);
					synth.set(\freq, midi.midicps); rectE.midi = midi  
				})
		};
		
		drawServer.addField(ScaledActionField, totalRect, {|sV, cC, mod, bN, cP, p|
			mod.switch( 
				1573160, { //if cmd is pressed cC is always 1 ! // alt-cmd
					drawServer.beginDragAction = {|v, x, y, mod| v.dragLabel = "play"; nil };
					Pbind(\midinote, Pseq([pE.y2midiPsM(p.y)]), \dur, 1).play; true }
				,1704234, { 
					drawServer.beginDragAction = {|v| v.dragLabel = "set reference tone"; nil };
					refToneFunc.value(cP.y) } // shift-alt-cmd
				,1835305, { // ctl-alt-cmd
					drawServer.beginDragAction = {|v| v.dragLabel = "clear reference tones"; nil};
					rtE.do{|rectE| rectE.rtSynth.releaseSynth(0.2);
						drawServer.removeField(rectE.field) };
					rtE.clear }
				, { false }) 
		});

		{|e, scaledView|
			totalRect.set(*scaledView.totalBounds.asArray);
	}}
	,{{|e, refToneEnvir, refScaleY, scaledView, scale, outline, selHeight=10|
		var bounds = scaledView.drawBounds;
		refToneEnvir.keysValuesDo{|rect, rectE|
			var scaledY = scaledView.convertFwd(0, e.midi2nsYPsM(rectE.midi))[1];
			rect.set(bounds.left, scaledY - (selHeight / 2), bounds.width, selHeight);
			Color.red.setStroke; Pen.line(bounds.left@scaledY, bounds.right@scaledY); Pen.stroke;
			if (outline) { Color.grey.setStroke; Pen.strokeRect(rect) }
		}
	}})
);
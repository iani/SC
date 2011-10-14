TempoClockGUI : ObjGui {
	
	var <guiMetro, animateBut, <tasks, freq, soundOn, nameViewStates;
	
	*initClass{
		StartUp.add{
			SynthDef(\ping, {|freq, amp=0.1|
				var e, z;
				e= EnvGen.ar(Env.perc(0, 0.1), doneAction:2);
				z= SinOsc.ar(freq.dup, 0, amp);
				OffsetOut.ar(0, z*e);
			}).store
		}
	}	
	*observedClasses { ^[TempoClock] }
	*rowWidth { ^54 } 
	*rowHeight { ^67 }
	*skin { ^GUI.skins[\small] }
	
	setDefaults { |obj, options|
		if (parent.isNil) { 
			defPos = 10@260
		} { 
			defPos = skin.margin;
		};
		minSize = (this.class.rowWidth) @ (this.class.rowHeight);
		
		freq = options[0] ?? { 200.rrand(1500) };
		soundOn = options[1] ? false;
		
		nameViewStates = [
			["?", skin.fontColor, skin.offColor],["??", skin.fontColor, skin.onColor]];
		
		allGuiDefs = (
			allGuiClass: 		TempoClocksAllGui	
			,objGuiClasses:	[this.class, SlaveClockGUI]
			,numItems:		12
			// optional ClassAllGui defaults:
			,initPos:			700@5
			,skin:			GUI.skins[\AllGuiSkin]
			,makeHead:		true
			,scrollyWidth: 	6
			,orientation:		\horizontal
			,makeFilter:		true
			,name:			"TempoClocks AllGUI"
		)
	}
	getOptions { ^[freq, soundOn] }
	name_ { |argName| 
		name = argName.asString;
		if (nameView.notNil) { 
			nameView.states_(nameView.states.collect{|list| list.put(0, name) } )}
	}
	makeViews {|obj| 
		var lineheight = zone.bounds.height - (skin.margin.y * 2);
				
		nameView = Button(zone, Rect(0, skin.margin.y, this.class.rowWidth, 16))
			.states_(nameViewStates.deepCopy)
			.action_({|but| soundOn = if (but.value == 0) { false } { true } })
			.value_( if (soundOn) { 1 }{ 0 } )
			.font_(font)
			.receiveDragHandler_({ arg obj; this.object = View.currentDrag });
		
		this.drawMetro(obj, lineheight);
		
		tasks =  [
			Routine { inf.do{ { try { 
				guiMetro.refresh 
				 } 
			}.defer; 1.wait } }.play(obj, obj.beatsPerBar)
			,Routine { var blk = false; 
				inf.do{ 
					if (blk) { 
						blk = false; 
						{  try { 
							guiMetro.background_(skin.foreground) 
						  	} 
						}.defer
					}{ 
						blk = true; 
							{  try { 
								guiMetro.background_(skin.foreground.darken(0.8)) 
							  	} 
							}.defer
					};
					if (soundOn) { Synth(\ping, [\freq, freq]) };
					1.wait } }.play(obj, 1)
			,Routine {
				inf.do{ 
					{ 
						try { 
							nameView.states_(nameView.states.collect{|list| 
							list.put(2, skin.flashColor) } );
						nameView.refresh 
					} 
					}.defer;
					{ try { 
						nameView.states_(nameView.states.collect{|list, i| 
							list.put(2, nameViewStates[i][2]) } );
						nameView.refresh 
					 } 
					}.defer(0.1); 
						
					if (soundOn) { Synth(\ping, [\freq, freq - (freq*0.1), \amp, 0.2]) };
					if (obj.isKindOf(SlaveClock).not) { obj.beatsPerBar.wait
					}{ 
						if (obj.meterCondition.test) 
							{ obj.beatsPerBar.wait } { obj.meterCondition.wait; 
// this.logln("release:" + thisThread.seconds)
								} 
					} } }.play(obj, obj.beatsPerBar)
		];
		obj.addDependant(this);
	}
	drawMetro{|obj, lineheight|
		var midPnt, inner, outer, rect;
		guiMetro= UserView(zone, Rect(1, 0, this.class.rowWidth - 2, lineheight - 16) );
				midPnt= Point(guiMetro.bounds.width, guiMetro.bounds.height)*0.5;
				inner= guiMetro.bounds.height*0.3;
				outer= guiMetro.bounds.height*0.5;
				rect = (2*inner@inner).asRect;
		guiMetro.drawFunc_{|view| var slice, o;
		
		try{	
				var slice= 2pi/obj.beatsPerBar;
				var o = (object.beatInBar/object.beatsPerBar*2pi);
				if (o.round(0.1) == 0.0) {o = 2*pi };
				
	
				Pen.color_(skin.alterFontColor);
				Pen.addAnnularWedge(midPnt, inner, outer, 1.5pi, o );
				Pen.fill;
				
				Pen.color_(skin.fontColor);
				object.beatsPerBar.do{|x|
					Pen.addAnnularWedge(midPnt,inner,outer, x*slice+1.5pi, slice)
				};
				Pen.stroke;
				
				this.drawMore(midPnt, inner, rect);
				
				Pen.stringCenteredIn("" ++ object.beatInBar.asInt, 
					rect.moveToPoint(midPnt - (inner@(inner - 2))) );
		}{|error| "drawFunc".postln }
				
			}.canFocus_(false);
	}
	drawMore {|midPnt, inner, rect|
		Pen.font_(font.copy.size_(9));
		Pen.color_(skin.fontColor);
		Pen.stringCenteredIn("" ++ object.beatsPerBar.asInt, 
			rect.moveToPoint(midPnt - (inner@2)) );	
	}
	stopSkip { 
		skipjacks.do{|skippy| skippy.stop }; 
		try{	guiMetro.animate_(false) }{|error| //dont post --> ERROR: clock is not running
		};
		tasks.do{|task| task.stop} 
	}
	updateFast { |doFull = false|
		var newState = this.getState; 
		
		// compare newState and prevState, update gui items as needed
		if (doFull.not and: { newState == prevState }) { ^this };
		
		if (doFull.not and: { newState[\object] != prevState[\object] }) { 
			this.name_(this.getName);
		};		
	}
	
	update {|who, what ...args| //this.logln("update:" + [who, what, args]);
		what.switch( \stop, { this.stopSkip } )
	}
}
/*
TempoClockGUI.new

*/

SlaveClockGUI : TempoClockGUI {
	*observedClasses { ^[SlaveClock] }	
	
	drawMore {|midPnt, inner, rect|
		var quant = object.master.beatsPerBar;
		
		Pen.fillColor_(skin.flashColor);
		Pen.addAnnularWedge( midPnt, 0, inner*0.8, 1.5pi,
			2pi*(quant-(object.master.nextTimeOnGrid(quant)-object.master.beats))/quant );
		Pen.fill;
		
		Pen.font_(font.copy.size_(9));
		Pen.color_(skin.fontColor);
		Pen.stringCenteredIn("" ++ (quant * object.tempo).round(0.01), 
			rect.moveToPoint(midPnt - (inner@2)) );
	}
}

TempoClocksAllGui : ClassAllGui {
	var animateBut, animateState, rateTxt;
	
	addToHead {|view|
		animateBut = Button(view, (50@skin.headHeight).asRect.moveTo(view.bounds.width-40,0))
			.states_([
				["animate", skin.fontColor, skin.offColor],
				["animate", skin.fontColor, skin.onColor] ])
			.action_({|but| if (but.value == 0) { guis.do{|gui| gui.guiMetro.animate_(false) } 
				} { guis.do{|gui| gui.guiMetro.animate_(true)}  } })
			.font_(font);
		rateTxt = StaticText(view, 55@skin.headHeight )
			.font_(font).align_(\center).string_("frameRate")
	}
	getState {
		^object.collect{|guiClass| 
				guiClass.observedClasses.collect{|cl| cl.all.select{|obj| obj.isMemberOf(cl) }}
			}.flat.asSet.asList.sort({|a, b| 
				if (a.respondsTo(\name) && b.respondsTo(\name)) { a.name < b.name }{ true } });
	}
	updateFastMore { 
		if (guis.notEmpty) { rateTxt.string_(guis.choose.guiMetro.frameRate.round(0.1).asString) }
	}
	filterObjs{|objs|
		if (prefix == "") {
			objs = objs.reject {|obj| obj.respondsTo(\name) };
		}{
			objs = objs.select {|obj| if (obj.respondsTo(\name).not) {false} {
				obj.name.asString.contains(prefix) } };
		};
		^objs
	}
	prepareRedraw {
		animateState = animateBut.value;
		animateBut.valueAction_(0) // otherwise does not re-start by valueAction_ on finishRedraw
	}
	finishRedraw {
		animateBut.valueAction_(animateState)
	}
	
}
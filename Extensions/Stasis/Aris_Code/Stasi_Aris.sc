/*
100524 igoumeninja
Aris Bezas

Stasi_Aris.new
d = FibDisplay(oscMessage: 'l_branch');
d.show;
Stasis.start(local: true);
Stasis.start("AAB")
Stasis.stop
SendAmpFreq.start;
m = Synth(\SendAmpPitch, [\chan, 8, \ampTrig, 1, \freqTrig, 2]); // 
m.free;
0:output, 8:input
Sendmidi.start;
m.set(\chan, 8)


(
SynthDef("Xaraktiki",
{ arg amp = 0, pan = 0, out = 0, panlevel = 0;
var source;
var panned_source;
source = FreeVerb.ar(HPF.ar(BrownNoise.ar(amp, 0), MouseX.kr(24,200,1)), 0.2, 0.25);
panned_source = Pan2.ar(source, MouseX.kr(-0.9, 0.9));
Out.ar( out, panned_source);
}
).send(s);
)
x.free
x = Synth(\Xaraktiki);
OSCresponder(nil, "/ampXar", { | time, resp, message |x.set("amp", message[1]);}).add;

*/
//
//Stasi_Aris.new
//	Class for Stasis performance

Stasi_Aris	{
	classvar	default;
	var	<server;			
	var	<addr;
	var	<a;
	var	<q;		
	var window, wSound, wSuperformula, wSeashell;
	var mouseLines, soundLines, minAmp, maxAmp, minFreq, maxFreq, mouseMinElast, mouseMaxElast, mouseMinDamp, mouseMaxDamp, soundMinElast, soundMaxElast, soundMinDamp, soundMaxDamp, numMouseSketches, numSoundSketches, feedbackSpeedOrient, feedbackSpeedX, feedbackSpeedY;	
	var	sformM, sformn1, sformn2, sformn3, sformStep, sformScale, sformNum;
	var seashellR, seashellN, seashellH, seashellP, seashellL, seashellK, seashellDu, seashellDv, seashellScale, seashellSteps; 

	
	*default {
		if (default.isNil) { default = this.new }; 
		^default;
	}
	
	*start { this.default.start; }	
	
	*stop { this.default.stop }	
	
	*new { | server, addr, chan = 0 |
		^super.new.init(server, addr);	
	}	
	
	// start initialize	
	init { | argServer, argAddr, argChan = 0 |
		server = argServer ?? { Server.default };  //define server
		//addr =  argAddr ?? { NetAddr("192.168.1.11", 12345); }; //graphics Server, oF port
		//addr =  argAddr ?? { NetAddr("192.168.1.10", 12345); }; //arisOF on Router, oF port
		addr =  argAddr ?? { NetAddr("127.0.0.1", 12345); }; //localhost, oF port
		this.activateMIDI;	// call activateMIDI		
		this.soundGUI;	// call soundGUI		
		this.mainGUI;	// call mainGUI		
//		this.superformulaGUI;	// call superformulaGUI	
//		this.seashellGUI;	//call seashell GUI
	}
	
	activateMIDI	{
		Sendmidi.start;
	}
	
	soundGUI	{
		wSound = SCWindow("sound GUI");
		wSound.front;
		wSound.view.background_(Color.new255(185,157,157));
		wSound.bounds_(Rect(0,0,330,580));
		q = wSound.addFlowLayout( 10@10, 20@5 );
		//a = StaticText(wSound, Rect(10, 10, 200, 20));
		Button(wSound, Rect(20,20,300,50))
				.states_([
					["view sound", Color.black, Color.white],
					["stop sound", Color.white, Color.black],
				])
				.action_({ arg butt; addr.sendMsg("/viewSoundChanels", butt.value)}
		);
		//numSoundSketches
		numSoundSketches=EZSlider(wSound, 300@20, "numSoundSketches ", ControlSpec(1, 499, \lin, 1, 5), numberWidth:50,layout:\horz, initVal:99, labelWidth: 120);
		numSoundSketches.setColors(Color.grey,Color.white);
		numSoundSketches.action_({addr.sendMsg("/numSoundSketches", numSoundSketches.value)});
		//soundMinElast
		soundMinElast=EZSlider(wSound, 300@20, "soundMinElast ", numberWidth:50,layout:\horz, initVal:0.0, labelWidth: 100);
		soundMinElast.setColors(Color.grey,Color.white);
		soundMinElast.action_({addr.sendMsg("/minSoundElasticity", soundMinElast.value)});
		//soundMaxElast
		soundMaxElast=EZSlider(wSound, 300@20, "soundMaxElast ", numberWidth:50,layout:\horz, initVal:1.0, labelWidth: 100);
		soundMaxElast.setColors(Color.grey,Color.white);
		soundMaxElast.action_({addr.sendMsg("/maxSoundElasticity", soundMaxElast.value)});
		//soundMinDamp
		soundMinDamp=EZSlider(wSound, 300@20, "soundMinDamp ", numberWidth:50,layout:\horz, initVal:0.0, labelWidth: 100);
		soundMinDamp.setColors(Color.grey,Color.white);
		soundMinDamp.action_({addr.sendMsg("/minSoundDamping", soundMinDamp.value)});
		//soundMaxDamp
		soundMaxDamp=EZSlider(wSound, 300@20, "soundMaxDamp ", numberWidth:50,layout:\horz, initVal:1.0, labelWidth: 100);
		soundMaxDamp.setColors(Color.grey,Color.white);
		soundMaxDamp.action_({addr.sendMsg("/soundMaxDamp", soundMaxDamp.value)});
		Button(wSound, Rect(20,20,300,20))
				.states_([
					["sound Lines", Color.black, Color.white],
					["sound Points", Color.white, Color.black],
				])
				.action_({ arg butt; addr.sendMsg("/soundLines", butt.value)}
		);
		
		
		//minAmp
		minAmp=EZSlider(wSound, 300@20, "minAmp ",numberWidth:50,layout:\horz, initVal:0.0, labelWidth: 100);
		minAmp.setColors(Color.grey,Color.white);
		minAmp.action_({addr.sendMsg("/minAmp", minAmp.value)});
		//maxAmp
		maxAmp=EZSlider(wSound, 300@20, "maxAmp ", ControlSpec(0.001, 1, \lin, 0.001, 0.15), numberWidth:50,layout:\horz, initVal:0.15, labelWidth: 100);
		maxAmp.setColors(Color.grey,Color.white);
		maxAmp.action_({addr.sendMsg("/maxAmp", maxAmp.value)});
		//minFreq
		minFreq=EZSlider(wSound, 300@20, "minFreq ", ControlSpec(20, 10000, \lin, 1, 1), numberWidth:50,layout:\horz, initVal:20, labelWidth: 100);
		minFreq.setColors(Color.grey,Color.white);
		minFreq.action_({addr.sendMsg("/minFreq", minFreq.value)});
		//maxFreq
		maxFreq=EZSlider(wSound, 300@20, "maxFreq ", ControlSpec(40, 10000, \lin, 1, 1), numberWidth:50,layout:\horz, initVal:4000, labelWidth: 100);
		maxFreq.setColors(Color.grey,Color.white);
		maxFreq.action_({addr.sendMsg("/maxFreq", maxFreq.value)});				
	}
	//####################   Main GUI  #################
	mainGUI	{
		window = SCWindow("GUI for diadromi");
		window.front;
		window.view.background_(Color.new255(75,96,130));
		window.bounds_(Rect(330,0,330,580));
		q = window.addFlowLayout( 10@10, 20@5 );
		
		//#### MOUSE Elasticity and Damping  #######################
		//numMouseSketches
		numMouseSketches=EZSlider(window, 300@20, "numMouseSketches ", ControlSpec(1, 499, \lin, 1, 5), numberWidth:50,layout:\horz, initVal:99, labelWidth: 120);
		numMouseSketches.setColors(Color.grey,Color.white);
		numMouseSketches.action_({addr.sendMsg("/numMouseSketches", numMouseSketches.value)});
		//mouseMinElast
		mouseMinElast=EZSlider(window, 300@20, "mouseMinElast ", numberWidth:50,layout:\horz, initVal:0.0, labelWidth: 100);
		mouseMinElast.setColors(Color.grey,Color.white);
		mouseMinElast.action_({addr.sendMsg("/minMouseElasticity", mouseMinElast.value)});
		//mouseMaxElast
		mouseMaxElast=EZSlider(window, 300@20, "mouseMaxElast ", numberWidth:50,layout:\horz, initVal:1.0, labelWidth: 100);
		mouseMaxElast.setColors(Color.grey,Color.white);
		mouseMaxElast.action_({addr.sendMsg("/maxMouseElasticity", mouseMaxElast.value)});
		//mouseMinDamp
		mouseMinDamp=EZSlider(window, 300@20, "mouseMinDamp ", numberWidth:50,layout:\horz, initVal:0.0, labelWidth: 100);
		mouseMinDamp.setColors(Color.grey,Color.white);
		mouseMinDamp.action_({addr.sendMsg("/minMouseDamping", mouseMinDamp.value)});
		//mouseMaxDamp
		mouseMaxDamp=EZSlider(window, 300@20, "mouseMaxDamp ", numberWidth:50,layout:\horz, initVal:1.0, labelWidth: 100);
		mouseMaxDamp.setColors(Color.grey,Color.white);
		mouseMaxDamp.action_({addr.sendMsg("/maxMouseDamping", mouseMaxDamp.value)});
		Button(window, Rect(20,20,300,20))
			.states_([
				["mouse Lines", Color.black, Color.white],
				["mouse Points", Color.white, Color.black],
			])
			.action_({ arg butt; addr.sendMsg("/mouseLines", butt.value);}
		);
		//feedback
		a = StaticText(window, Rect(10, 10, 300, 20));
		a.string = "feedback";
		Button(window, Rect(20,20,300,20))
				.states_([
					["view feedback", Color.black, Color.white],
					["stop feedback", Color.white, Color.black],
				])
				.action_({ arg butt; addr.sendMsg("/feedbackView", butt.value)}
		);
		//timeLine
		Button(window, Rect(20,20,300,20))
				.states_([
					["view timeLine", Color.black, Color.white],
					["stop timeLine", Color.white, Color.black],
				])
				.action_({ arg butt; addr.sendMsg("/timeLine", butt.value)}
		);
		//feedback speed and orientation
		feedbackSpeedOrient = Slider2D(window, Rect(20, 20,80, 80))
						.action_({|sl|
							//[\sliderX, sl.x, \sliderY, sl.y].postln;
							addr.sendMsg("/feedbackSpeedX", linlin(sl.x, 0, 1, -10, 10));
							addr.sendMsg("/feedbackSpeedY", linlin(sl.y, 0, 1, -10, 10));
						});
		
		//viewRotate
		Button(window, Rect(20,20,300,20))
				.states_([
					["view viewRotate", Color.black, Color.white],
					["stop viewRotate", Color.white, Color.black],
				])
				.action_({ arg butt; addr.sendMsg("/viewRotate", butt.value)}
		);
		//feedbackSpeedX
		feedbackSpeedX=EZSlider(window, 300@20, "feedbackSpeedX ", ControlSpec(-10, 10, \lin, 0.05, 0.01), numberWidth:50,layout:\horz, initVal:0, labelWidth: 100);
		feedbackSpeedX.setColors(Color.grey,Color.white);
		feedbackSpeedX.action_({addr.sendMsg("/feedbackSpeedX", feedbackSpeedX.value)});
		//feedbackSpeedY
		feedbackSpeedY=EZSlider(window, 300@20, "feedbackSpeedY ", ControlSpec(-10, 10, \lin, 0.05, 0.01), numberWidth:50,layout:\horz, initVal:0, labelWidth: 100);
		feedbackSpeedY.setColors(Color.grey,Color.white);
		feedbackSpeedY.action_({addr.sendMsg("/feedbackSpeedY", feedbackSpeedY.value)});
		
		//mask
		a = StaticText(window, Rect(10, 10, 140, 20));
		a.string = "mask";
		Button(window, Rect(20,20,300,20))
				.states_([
					["view mask", Color.black, Color.white],
					["stop mask", Color.white, Color.black],
				])
				.action_({ arg butt; addr.sendMsg("/maskView", butt.value)}
		);
		Button(window, Rect(20,20,300,20))
				.states_([
					["view beat mask", Color.black, Color.white],
					["stop beat mask", Color.white, Color.black],
				])
				.action_({ arg butt; addr.sendMsg("/megethos_beat", butt.value)}
		);
		Button(window, Rect(20,20,300,20))
				.states_([
					["view scope", Color.black, Color.white],
				])
				.action_({ arg butt; addr.sendMsg("/scopeView")}
		);				
		Button(window, Rect(20,20,300,20))
				.states_([
					["view beats", Color.black, Color.white],
					["stop beats", Color.white, Color.black],
				])
				.action_({ arg butt; addr.sendMsg("/beatsView", butt.value)}
		);
		
		//circleView
		Button(window, Rect(20,20,300,20))
				.states_([
					["view circle", Color.black, Color.white],
					["stop circle", Color.white, Color.black],
				])
				.action_({ arg butt; addr.sendMsg("/circleView", butt.value)}
		);
		
		}
		//}
//	//##################### SUPERFORMULA GUI ################################
//	
//		
//	superformulaGUI	{
//	
//		wSuperformula = SCWindow("SuperFormula_GUI");
//		
//		wSuperformula.front;
//		wSuperformula.view.background_(Color.new255(100,37,49));
//		wSuperformula.bounds_(Rect(660,480,330,480));
//		q = wSuperformula.addFlowLayout( 10@10, 20@5 );
//		//a = StaticText(wSuperformula, Rect(10, 10, 200, 20));
//		Button(wSuperformula, Rect(20,20,300,20))
//				.states_([
//					["view Superformula", Color.black, Color.white],
//					["stop Superformula", Color.white, Color.black],
//				])
//				.action_({ arg butt; addr.sendMsg("/viewSuperformula", butt.value)}
//		);
//		Button(wSuperformula, Rect(20,20,300,20))
//				.states_([
//					["Superformula Lines", Color.black, Color.white],
//					["Superformula Points", Color.white, Color.black],
//				])
//				.action_({ arg butt; addr.sendMsg("/sformType", butt.value)}
//		);
//		//sformM
//		sformM = EZSlider(wSuperformula, 300@20, "M   ", ControlSpec(0, 10, \lin, 1), numberWidth:50,layout:\horz, initVal:4.0, labelWidth: 30);
//		sformM.setColors(Color.grey,Color.white);
//		sformM.action_({addr.sendMsg("/sformM", sformM.value)});
//		//sformn1
//		sformn1 = EZSlider(wSuperformula, 300@20, "n1   ", ControlSpec(0, 10, \lin, 0.1), numberWidth:50,layout:\horz, initVal:1.0, labelWidth: 30);
//		sformn1.setColors(Color.grey,Color.white);
//		sformn1.action_({addr.sendMsg("/sformn1", sformn1.value)});
//		//sformn2
//		sformn2 = EZSlider(wSuperformula, 300@20, "n2   ", ControlSpec(0, 10, \lin, 0.1), numberWidth:50,layout:\horz, initVal:1.0, labelWidth: 30);
//		sformn2.setColors(Color.grey,Color.white);
//		sformn2.action_({addr.sendMsg("/sformn2", sformn2.value)});
//		//sformn3
//		sformn3 = EZSlider(wSuperformula, 300@20, "n3   ", ControlSpec(0, 10, \lin, 0.1), numberWidth:50,layout:\horz, initVal:1.0, labelWidth: 30);
//		sformn3.setColors(Color.grey,Color.white);
//		sformn3.action_({addr.sendMsg("/sformn3", sformn3.value)});
//		//sformStep
//		sformStep = EZSlider(wSuperformula, 300@20, "step   ", ControlSpec(0.001, 2, \lin, 0.001), numberWidth:50,layout:\horz, initVal:0.1, labelWidth: 50);
//		sformStep.setColors(Color.grey,Color.white);
//		sformStep.action_({addr.sendMsg("/sformStep", sformStep.value)});
//		//sformScale
//		sformScale = EZSlider(wSuperformula, 300@20, "scale   ", ControlSpec(1, 1000, \lin, 1), numberWidth:50,layout:\horz, initVal:200.0, labelWidth: 50);
//		sformScale.setColors(Color.grey,Color.white);
//		sformScale.action_({addr.sendMsg("/sformScale", sformScale.value)});
//		//sformNum
//		sformNum = EZSlider(wSuperformula, 300@20, "num   ", ControlSpec(0, 1000, \lin, 1), numberWidth:50,layout:\horz, initVal:100.0, labelWidth: 50);
//		sformNum.setColors(Color.grey,Color.white);
//		sformNum.action_({addr.sendMsg("/sformNum", sformNum.value)});	
//	}
//	
//	//###########  SEASHELL  ####################################
//	seashellGUI	{
//		wSeashell = SCWindow("Seashell_GUI");
//		wSeashell.front;
//		wSeashell.view.background_(Color.new255(990,100,43));
//		wSeashell.bounds_(Rect(990,480,330,480));
//		q = wSeashell.addFlowLayout( 10@10, 20@5 );
//		//a = StaticText(wSeashell, Rect(10, 10, 200, 20));
//		Button(wSeashell, Rect(20,20,300,20))
//				.states_([
//					["view Seashell", Color.black, Color.white],
//					["stop Seashell", Color.white, Color.black],
//				])
//				.action_({ arg butt; addr.sendMsg("/viewSeashell", butt.value)}
//		);
//		Button(wSeashell, Rect(20,20,300,20))
//				.states_([
//					["Seashell Lines", Color.black, Color.white],
//					["Seashell Points", Color.white, Color.black],
//				])
//				.action_({ arg butt; addr.sendMsg("/seashellType", butt.value)}
//		);
//		//seashellR
//		seashellR = EZSlider(wSeashell, 300@20, "R   ", ControlSpec(0, 10, \lin, 0.01, 4), numberWidth:50,layout:\horz, initVal:4.0, labelWidth: 30);
//		seashellR.setColors(Color.grey,Color.white);
//		seashellR.action_({addr.sendMsg("/seashellR", seashellR.value)});
//		//seashellN
//		seashellN = EZSlider(wSeashell, 300@20, "N   ", ControlSpec(0, 10, \lin, 0.01, 4), numberWidth:50,layout:\horz, initVal:5.6, labelWidth: 30);
//		seashellN.setColors(Color.grey,Color.white);
//		seashellN.action_({addr.sendMsg("/seashellN", seashellN.value)});
//		//seashellH
//		seashellH = EZSlider(wSeashell, 300@20, "H   ", ControlSpec(0, 10, \lin, 0.01, 4), numberWidth:50,layout:\horz, initVal:4.5, labelWidth: 30);
//		seashellH.setColors(Color.grey,Color.white);
//		seashellH.action_({addr.sendMsg("/seashellH", seashellH.value)});
//		//seashellP
//		seashellP = EZSlider(wSeashell, 300@20, "P   ", ControlSpec(0, 10, \lin, 0.01, 4), numberWidth:50,layout:\horz, initVal:1.4, labelWidth: 30);
//		seashellP.setColors(Color.grey,Color.white);
//		seashellP.action_({addr.sendMsg("/seashellP", seashellP.value)});
//		//seashellL
//		seashellL = EZSlider(wSeashell, 300@20, "L   ", ControlSpec(0, 10, \lin, 0.01, 4), numberWidth:50,layout:\horz, initVal:4.0, labelWidth: 30);
//		seashellL.setColors(Color.grey,Color.white);
//		seashellL.action_({addr.sendMsg("/seashellL", seashellL.value)});
//		//seashellK
//		seashellK = EZSlider(wSeashell, 300@20, "K   ", ControlSpec(0, 10, \lin, 0.01, 4), numberWidth:50,layout:\horz, initVal:9.0, labelWidth: 30);
//		seashellK.setColors(Color.grey,Color.white);
//		seashellK.action_({addr.sendMsg("/seashellK", seashellK.value)});
//		//seashellDu
//		seashellDu = EZSlider(wSeashell, 300@20, "du   ", ControlSpec(0, 0.01, \lin, 0.0001), numberWidth:50,layout:\horz, initVal:0.005, labelWidth: 30);
//		seashellDu.setColors(Color.grey,Color.white);
//		seashellDu.action_({addr.sendMsg("/seashellDu", seashellDu.value)});
//		//seashellDv
//		seashellDv = EZSlider(wSeashell, 300@20, "dv   ", ControlSpec(0, 0.001, \lin, 0.00001), numberWidth:50,layout:\horz, initVal:9.0, labelWidth: 30);
//		seashellDv.setColors(Color.grey,Color.white);
//		seashellDv.action_({addr.sendMsg("/seashellDv", seashellDv.value)});
//		//seashellScale
//		seashellScale = EZSlider(wSeashell, 300@20, "scale   ", ControlSpec(0.01, 10, \lin, 0.01), numberWidth:50,layout:\horz, initVal:1.0, labelWidth: 50);
//		seashellScale.setColors(Color.grey,Color.white);
//		seashellScale.action_({addr.sendMsg("/seashellScale", seashellScale.value)});
//		//seashellSteps
//		seashellSteps = EZSlider(wSeashell, 300@20, "steps   ", ControlSpec(1, 1000, \lin, 1), numberWidth:50,layout:\horz, initVal:1.0, labelWidth: 50);
//		seashellSteps.setColors(Color.grey,Color.white);
//		seashellSteps.action_({addr.sendMsg("/seashellSteps", seashellSteps.value)});
//		
//		a = StaticText(wSeashell, Rect(10, 10, 300, 20));
//		a.string = "Default Values";
//		a = StaticText(wSeashell, Rect(10, 10, 300, 20));
//		a.string = "R = 8, N = 5,6, H = 4.5, P = 1.4, L = 4, K = 9";
//		a = StaticText(wSeashell, Rect(10, 10, 300, 20));
//		a.string = "du = 0.005, dv = 0.0002";
//
//	}
}
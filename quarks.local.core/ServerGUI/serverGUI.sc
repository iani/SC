// Sergio Luque
// www.sergioluque.com
// version 2

+ Server {
	makeWindow { arg w;
		var active, booter, killer, makeDefault, running, booting, stopped, bundling, showDefault;
		var startDump, stopDump, blockAliveThread, dumping = false;
		var recorder;
		var countsViews, ctlr;
		var label, gui;
		var font, font2, backgroundColor, fontColor;
		
		font = GUI.font.new("Helvetica", 10);
		font2 = GUI.font.new("Helvetica", 9);
		backgroundColor = Color(0.196, 0.196, 0.196);
		fontColor = Color.gray(0.925);
		
		if (window.notNil) { ^window.front };
		gui = GUI.current;
		
		if(w.isNil) {
			label = name.asString + "server";
			w = window = gui.window.new(
							label, 
							Rect(Window.screenBounds.width - 404, named.values.indexOf(this) * 22, 404, 21),
							border: false
						   );
			w.view.background_(backgroundColor);
			w.alwaysOnTop = false;
			w.view.decorator = FlowLayout(w.view.bounds);
		} {
			label = w.name
		};

		if(isLocal) {
			booter = gui.button.new(w, Rect(0,0, 15, 15));
			booter.canFocus = false;
			booter.font = font;
			booter.states = [[ "b", fontColor, Color.clear ],
						   	   [ "q", fontColor, Color.clear ]
						   	  ];

			booter.action = { arg view;
				if(view.value == 1, {
					booting.value;
					this.boot;
				});
				if(view.value == 0,{
					this.quit;
				});
			};
			booter.setProperty(\value,serverRunning.binaryValue);

			killer = gui.button.new(w, Rect(0, 0, 15, 15));
			killer.states = [[ "k", fontColor, Color.clear]];
			killer.font = font;
			killer.canFocus = false;
			killer.action = { Server.killAll; stopped.value; };
		};

		active = gui.staticText.new(w, Rect(0,0, 50, 15));
		active.string = this.name.asString;
		active.align = \center;
		active.font = gui.font.new("Helvetica", 9).boldVariant;
		active.background = Color.black;
		if(serverRunning,running,stopped);

		makeDefault = gui.button.new(w, Rect(0,0, 15, 15));
		makeDefault.font = font;
		makeDefault.canFocus = false;
		makeDefault.states = [[ "d", fontColor, Color.clear ], [ "d", fontColor, Color(0.647, 0.761, 0.38, 0.7)]];
		makeDefault.value_((this == Server.default).binaryValue);
		makeDefault.action = { Server.default_(this) };

		if(isLocal){
			recorder = gui.button.new(w, Rect(0,0, 15, 15));
			recorder.font = font2;
			recorder.states = [
				[ "r", fontColor, Color.clear ],
				[ "[]", Color.white, Color.red ]
			];
			recorder.action = {
				if (recorder.value == 1) { this.record } { this.stopRecording };
			};
			recorder.enabled = false;
		};

		w.view.keyDownAction = { arg view, char, modifiers;
			

				// if any modifiers except shift key are pressed, skip action
			if(modifiers & 16515072 == 0) {

				case
				{char === $n } { this.queryAllNodes(false) }
				{char === $N } { this.queryAllNodes(true) }
				{char === $l } { this.tryPerform(\meter) }
				{char === $p} { if(serverRunning) { this.plotTree } }
				{char === $ } { if(serverRunning.not) { this.boot } }
				{char === $s and: { gui.stethoscope.isValidServer( this ) } } {
					GUI.use( gui, { this.scope })}
				{char == $d } {
					if(this.isLocal or: { this.inProcess }) {
						if(dumping, stopDump, startDump)
					} {
						"cannot dump a remote server's messages".inform
					}

				};
			};
		};

		if (isLocal) {

			running = {
				active.stringColor_(Color.new255(165,194,97));
//				active.string = "running";
				booter.setProperty(\value,1);
				recorder.enabled = true;
			};
			stopped = {
				active.stringColor_(Color.grey(0.3));
//				active.string = "inactive";
				stopDump.value;
				booter.setProperty(\value,0);
				recorder.setProperty(\value,0);
				recorder.enabled = false;
				countsViews.do(_.string = "");
			};
			booting = {
				active.stringColor_(Color.new255(255, 140, 0));
//				active.string = "booting";
				//booter.setProperty(\value,0);
			};
			bundling = {
				active.stringColor_(Color.new255(237, 157, 196));
				booter.setProperty(\value,1);
				recorder.enabled = false;
			};
			blockAliveThread = {
				SystemClock.sched(0.2, { this.stopAliveThread });
			};
			startDump = {
				this.dumpOSC(1);
				this.stopAliveThread;
				dumping = true;
				w.name = "dumping osc: " ++ name.asString;
				CmdPeriod.add(blockAliveThread);
			};
			stopDump = {
				this.dumpOSC(0);
				this.startAliveThread;
				dumping = false;
				w.name = label;
				CmdPeriod.remove(blockAliveThread);
			};

			w.onClose = {
				window = nil;
				ctlr.remove;
			};

		} {
			running = {
				active.stringColor_(Color.new255(165,194,97));
//				active.string = "running";
				active.background = Color.white;
			};
			stopped = {
				active.stringColor_(Color.grey(0.3));
//				active.string = "inactive";

			};
			booting = {
				active.stringColor_(Color.new255(255, 140, 0));
//				active.string = "booting";
			};

			bundling = {
				active.stringColor = Color.new255(237, 157, 196);
				active.background = Color.red(0.5);
				booter.setProperty(\value,1);
			};

			w.onClose = {
				// but do not remove other responders
				this.stopAliveThread;
				window = nil;
				ctlr.remove;
			};
		};

		showDefault = {
			makeDefault.value = (Server.default == this).binaryValue;
		};

		if(serverRunning,running,stopped);
				
		countsViews =
		#[ "avg:", "peak:", "u:", "s:", "g:" ]
		 .collect { arg name, i;
			var label,numView, pctView;


			if (i < 2, {
				label = gui.staticText.new(w, Rect(0,0, 24, 15));
				label.string = name;
				label.font = font2;
				label.stringColor	= fontColor;
				label.align = \right;

				numView = gui.staticText.new(w, Rect(0,0, 21, 15));
				numView.font = font2;
				numView.stringColor	= fontColor;
				numView.align = \right;

				pctView = gui.staticText.new(w, Rect(0,0, 15, 15));
				pctView.string = "%";
				pctView.font = font2;
				pctView.stringColor	= fontColor;
				pctView.align = \left;
			},{
				label = gui.staticText.new(w, Rect(0,0, 12, 15));
				label.string = name;
				label.font = font2;
				label.stringColor	= fontColor;
				label.align = \right;
				
				numView = gui.staticText.new(w, Rect(0,0, 21, 15));
				numView.font = font2;
				numView.stringColor	= fontColor;
				numView.align = \right;
			});

			numView
		};

 		w.front;

		ctlr = SimpleController(this)
			.put(\serverRunning, {	if(serverRunning,running,stopped) })
			.put(\counts,{
				countsViews.at(0).string = avgCPU.round(0.1);
				countsViews.at(1).string = peakCPU.round(0.1);
				countsViews.at(2).string = numUGens;
				countsViews.at(3).string = numSynths;
				countsViews.at(4).string = numGroups;
//				countsViews.at(5).string = numSynthDefs;
			})
			.put(\bundling, bundling)
			.put(\default, showDefault);
		if(isLocal){
			ctlr.put(\cmdPeriod,{
					recorder.setProperty(\value,0);
				})
		};

		this.startAliveThread;
	}
}

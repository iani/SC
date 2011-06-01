// Sergio Luque
+ Server {
	makeWindow { arg w;
		var active, booter, killer, makeDefault, running, booting, stopped;
		var recorder, scoper;
		var countsViews, ctlr;
		var dumping=false, label, font = Font(\Helvetica, 10), font2 = Font(\Helvetica, 9), 
//		    gray =   Color.gray(0.925)/*Color.new255(203, 202, 192)*/, 
//		    green = Color.new255(108, 125, 20);//Color.new255(93, 107, 17); //Color.new255(141, 160, 25)
//		    gray	= Color.new255(51, 111, 203, 255 * 0.95),
		    gray	= Color.new255(50, 50, 50, 255 * 0.95),
		    green	= Color.gray(0.925),
		    gui
		    
		    
		    ;		
		    
		gui = GUI.current;
		if (window.notNil, { ^window.front });
		
		if(w.isNil) {
			label = name.asString + "server";
			w = window = SCWindow(label, 
						Rect(Window.screenBounds.width - 404 , named.values.indexOf(this) * 22, 449-17-10 - 18, 21),
						border: false);
			w.view.background_(gray);
			w.alpha = 1;
			w.alwaysOnTop = false;
			w.view.decorator = FlowLayout(w.view.bounds);
		} { label = w.name };
		
		if(isLocal,{
			booter = SCButton(w, Rect(0,0, 15, 15));
			booter.states = [["b", green, Color.clear],
						   ["q", green, Color.clear]];
		 	booter.font	= font;
			
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
			
			killer = SCButton(w, Rect(0,0, 15, 15));
			killer.states = [["k", green, Color.clear]];
			killer.font	= font;
			
			killer.action = { Server.killAll };	
		});
		
		active = SCStaticText(w, Rect(0,0, 50, 15));
		active.string = this.name.asString;
		active.align = \center;
		active.font = Font("Helvetica-Bold", 9);
		active.background = Color.black;
		if(serverRunning,running,stopped);		

		makeDefault = SCButton(w, Rect(0,0, 15, 15));
		makeDefault.states = [["d", green, Color.clear]];
		makeDefault.font	= font;
		makeDefault.action = {
			thisProcess.interpreter.s = this;
			Server.default = this;
		};

		//w.view.decorator.nextLine;
		
		recorder = SCButton(w, Rect(0,0, 15, 15));
		recorder.states = [
			["r", green, Color.clear],
			[">", Color.red, Color.gray(0.1)],
			["[]", gray, Color.red]
		];
		recorder.font	= font2;
		recorder.action = {
			if (recorder.value == 1) {
				this.prepareForRecord;
			}{
				if (recorder.value == 2) { this.record } { this.stopRecording };
			};
		};
		recorder.enabled = false;
		
		w.view.keyDownAction = { arg view, char, modifiers;
			var startDump, stopDump, stillRunning;
			
			if(modifiers & 16515072 == 0) {
				
				case 
				{char === $n } { this.queryAllNodes(false) }
				{char === $N } { this.queryAllNodes(true) }
				{char === $l } { this.tryPerform(\meter) }
				{char === $ } { if(serverRunning.not) { this.boot } }
				{char === $s and: { gui.stethoscope.isValidServer( this ) } } { 
					GUI.use( gui, { this.scope })}
				{char == $d } {
					if(this.isLocal or: { this.inProcess }) {
						stillRunning = {
							SystemClock.sched(0.2, { this.stopAliveThread });
						};
						startDump = { 
							this.dumpOSC(1);
							this.stopAliveThread;
							dumping = true;
							w.name = "dumping osc: " ++ name.asString;
							CmdPeriod.add(stillRunning);
						};
						stopDump = {
							this.dumpOSC(0);
							this.startAliveThread;
							dumping = false;
							w.name = label;
							CmdPeriod.remove(stillRunning);
						};
						if(dumping, stopDump, startDump)
					} {
						"cannot dump a remote server's messages".inform
					}
				
				}
				
			};
		};
		
		if (isLocal, {
			
			running = {
				active.stringColor_(Color.new255(165,194,97));//Color.red
				booter.setProperty(\value,1);
				recorder.enabled = true;
			};
			stopped = {
				active.stringColor_(Color.grey(0.3));
				booter.setProperty(\value,0);
				recorder.setProperty(\value,0);
				recorder.enabled = false;

			};
			booting = {
				active.stringColor_(Color.new255(255, 140, 0));//Color.yellow(0.9)
				//booter.setProperty(\value,0);
			};
			
			w.onClose = {
				//OSCresponder.removeAddr(addr);
				//this.stopAliveThread;
				//this.quit;
				window = nil;
				ctlr.remove;
			};
		},{	
			running = {
				active.background = Color.red;
				recorder.enabled = true;
			};
			stopped = {
				active.background = Color.black;
				recorder.setProperty(\value,0);
				recorder.enabled = false;

			};
			booting = {
				active.background = Color.yellow;
			};
			w.onClose = {
				// but do not remove other responders
				this.stopAliveThread;
				ctlr.remove;
			};
		});
		if(serverRunning,running,stopped);
			
//		w.view.decorator.nextLine;
		w.view.decorator.shift(3);

		countsViews = 
		#[
			"avg:", "peak:",
			"u:", "s:", "g:"
		].collect({ arg name, i;
			var label,numView, pctView;
			
		
			if (i < 2, { 
				
				label = SCStaticText(w, Rect(0,0, 24, 15));
				label.font	= font2;
				label.stringColor	= green;
				label.string = name;
				label.align = \left;
			
				numView = SCStaticText(w, Rect(0,0, 18, 15));
				numView.font	= font2;
				numView.stringColor	= green;
				numView.string = "?";
				numView.align = \right;
			
				pctView = SCStaticText(w, Rect(0,0, 15, 15));
				pctView.font	= font2;
				pctView.stringColor	= green;
				pctView.string = "%";
				pctView.align = \left;

			 	
				
			},{ label = SCStaticText(w, Rect(0,0, 12, 15));
				label.font	= font2;
				label.stringColor	= green;
				label.string = name;
				label.align = \left;
				
				numView = SCStaticText(w, Rect(0,0, 15, 15));
				numView.font	= font2;
				numView.stringColor	= green;
				numView.string = "?";
				numView.align = \right;
				
				w.view.decorator.shift(10);
				
				
			});
			
			numView
		});
		
		w.front;

		ctlr = SimpleController(this)
			.put(\serverRunning, {	if(serverRunning,running,stopped) })
			.put(\counts,{
				countsViews.at(0).string = avgCPU.round(0.1);
				countsViews.at(1).string = peakCPU.round(0.1);
				countsViews.at(2).string = numUGens;
				countsViews.at(3).string = numSynths;
				countsViews.at(4).string = numGroups;
				//countsViews.at(5).string = numSynthDefs;
			})
			.put(\cmdPeriod,{
				recorder.setProperty(\value,0);
			});	
		this.startAliveThread;
	}
}

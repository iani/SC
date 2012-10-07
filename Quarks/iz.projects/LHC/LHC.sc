/* 

Top class for Live Hardware Coding simulator


a = LHC.new.start;

a = LHC((symbol: { (degree: 0).play }, counter: { (degree: 10).play }));
a.start;

a = LHC((
	symbol: { | self, symbol | (degree: (A: 11, B: 12, C: 13, D: 14)[symbol]).play }, 
	counter: { | self, counter | (degree: counter).play }
)).start;

a = LHC((
	symbol: { | self, symbol | (degree: (A: 11, B: 12, C: 13, D: 14)[symbol]).play }, 
	counter: { | self, counter | if (counter > 0) { (degree: counter).play } }
)).start;


//:
a = LHC((
	symbol: { | self, symbol |
		self.soundOn.postln;
		if (self.soundOn) { 
			(degree: (A: 11, B: 12, C: 13, D: 14)[symbol]).play 
		}
	}, 
	counter: { | self, counter | 
		if (counter > 0) {
			(degree: counter).play;
			self.soundOn = true;
			self.soundOn.postln;
		}{
			self.soundOn = false
		}
	}
)).start;


*/

LHC {

	var <window;
	var <counterDisplay, <encoderDisplay, <bit0, <bit1, <bit2, <resetbutton;
	var <positiveEdgeDisplay;
	var <bit0numbox, <bit1numbox, <bit2numbox;
	
	var <>waitTime = 0.5, <routine;
	var <>counter = 0, <>input = 0;

	var <fsmDecoder;
	
	*new { | player | // player: sound algorithm
		^super.new.init(player);			
	}
	
	init { | player |
		this.makeFsmDecoder;
		this.addPlayer(player);	
	}
	
	makeFsmDecoder {
		fsmDecoder = FSMdecoder.new;
		fsmDecoder addDependant: this;		
	}
	
	addPlayer { | playerEvent |
		fsmDecoder addDependant: LHCplayer(playerEvent ?? { () });
	}
	
	start {
		this.makeWindow;
		this.makeRoutine;	
	}
	
	makeWindow { if (window.isNil) { this.prMakeWindow } { window.front } }
	
	prMakeWindow {
		window = Window.new("",Rect(318, 309, 506, 447)).front;
		window.onClose = { this.windowClosed };
		positiveEdgeDisplay = Button(window, Rect(20, 100, 30, 30))
			.states_([[" ", Color.black, Color.black], [" ", Color.yellow, Color.yellow]]);
		counterDisplay = NumberBox.new(window,Rect(100, 100, 100, 100))
			.font_(Font("Monaco", 60))
			.action_{|v| }
			.enabled_(false);
		StaticText.new(window,Rect(100, 200, 100, 20))
			.string_("COUNTER")
			.action_{|v| };
		encoderDisplay = TextField.new(window,Rect(225, 100, 100, 100))
			.action_{|v| }
			.font_(Font("Monaco", 60))
			.enabled_(false);
		StaticText.new(window,Rect(230, 200, 100, 20))
			.string_("ENCODER")
			.action_{|v| };

		bit0 = Button.new(window,Rect(280, 300, 100, 20))
			.states_([ [ "0", Color(1.0), Color() ], [ "1", Color(), Color(1.0) ] ])
			.action_{| v | "bit 0 was pushed".postln; };
		bit1 = Button.new(window,Rect(170, 300, 100, 20))
			.states_([ [ "0", Color(1.0), Color() ], [ "1", Color(), Color(1.0) ] ])
			.action_{| v | "bit 1 was pushed".postln; };
		bit2 = Button.new(window,Rect(60, 300, 100, 20))
			.states_([ [ "0", Color(1.0), Color()  ], [ "1", Color(), Color(1.0) ] ])
			.action_{| v | "bit 2 was pushed".postln; };
		resetbutton = Button.new(window,Rect(430, 220, 50, 50))
			.states_([ [ "RESET", Color(1.0), Color() ], [ "RESET", Color(), Color(1.0) ] ])
			.action_{| v | counter = 0; };
		window.view.keyDownAction =  { | view, char, modifiers, unicode, keycode |
//			keycode.postln;
			switch (char, 
				$d, { bit0.valueAction = 1 - bit0.value },
				$s, { bit1.valueAction = 1 - bit1.value  },
				$a, { bit2.valueAction = 1 - bit2.value  },
				$r, { resetbutton.value = 1 - resetbutton.value }
			);
//			[char, modifiers, unicode, keycode].postln;
		}
	 }

	windowClosed {
		window = nil;
		this.stopSynthsAndProcesses;
		fsmDecoder.releaseDependants;
	}
	
	stopSynthsAndProcesses {
		routine.stop;
		routine = 0;
	}
	
	makeRoutine {
		if (routine.isNil) {
			routine = {
				loop {
					this.flashPositiveEdgeDisplay;
					input = ([bit0.value, bit1.value, bit2.value] * [1, 2, 4]).sum;
					if (resetbutton.value > 0) {
						counter = 0;
						fsmDecoder.changed(\reset);
						"fsmDecoder should now broadcast change RESET".postln;
					}{
						counter = counter + input % 8;
						fsmDecoder.changed(\counter, counter);
					};
					counterDisplay.value = counter;
					this.calculateFSMstate(counter);
					waitTime.wait;
				}
			}.fork(AppClock);
		}	
	}

	flashPositiveEdgeDisplay {
		{ 
			positiveEdgeDisplay.value = 1;
			(waitTime / 3).wait;
			positiveEdgeDisplay.value = 0;
		}.fork(AppClock);
	}
	
	calculateFSMstate { | counter |
		// current state + input = next state + output
		{
			counter.asBinaryString(3) do: { | digit |
				fsmDecoder input: (digit == $1).binaryValue;
				(waitTime / 3).wait;
			};
		}.fork(AppClock);
	}
	
	update { | who, what, value |
//		postf("% updated: % to: %\n", who, what, value);
		switch (what,
			\symbol, { encoderDisplay.string = value.asString },
			\state, { }
		);
	}
	
}
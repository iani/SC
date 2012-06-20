/* 

Top class for Live Hardware Coding simulator


w = Window.new("GUI Introduction").layout_(
Ê Ê VLayout(
Ê Ê Ê Ê HLayout( Button(), TextField(), Button() ),
Ê Ê Ê Ê TextView()
Ê Ê )
).front;


LHC.makeWindow;


LHC.start;
*/

LHC {

	classvar <window;
	classvar <counterDisplay, <encoderDisplay, <bit0, <bit1, <bit2, <resetbutton;
	classvar <positiveEdgeDisplay;
	classvar <bit0numbox, <bit1numbox, <bit2numbox;
	
	classvar <>waitTime = 0.5, <routine;
	classvar <>counter = 0, <>input = 0;
	
	classvar <fmsState = 0;
	
	*start {
		this.makeWindow;
		this.makeRoutine;	
	}
	
	*makeWindow { if (window.isNil) { this.prMakeWindow } { window.front } }
	
	*prMakeWindow {
		window = Window.new("",Rect(318, 309, 506, 447)).front;
		window.onClose = { this.windowClosed };
		positiveEdgeDisplay = Button(window, Rect(20, 100, 40, 40))
			.states_([[" ", Color.black, Color.black], [" ", Color.red, Color.red]]);
		counterDisplay = NumberBox.new(window,Rect(100, 100, 100, 100))
			.font_(Font("Monaco", 60))
			.action_{|v| }
			.enabled_(false);
		StaticText.new(window,Rect(100, 200, 100, 20))
			.string_("COUNTER")
			.action_{|v| };
		encoderDisplay = TextField.new(window,Rect(225, 100, 100, 100))
			.action_{|v| }
			.enabled_(false);
		StaticText.new(window,Rect(230, 200, 100, 20))
			.string_("ENCODER")
			.action_{|v| };

		bit0 = Button.new(window,Rect(280, 300, 100, 20))
			.states_([ [ "0", Color(), Color(1.0) ], [ "1", Color(1.0), Color() ] ])
			.action_{| v | "bit 0 was pushed".postln; };
		bit1 = Button.new(window,Rect(170, 300, 100, 20))
			.states_([ [ "0", Color(), Color(1.0) ], [ "1", Color(1.0), Color() ] ])
			.action_{| v | "bit 1 was pushed".postln; };
		bit2 = Button.new(window,Rect(60, 300, 100, 20))
			.states_([ [ "0", Color(), Color(1.0) ], [ "1", Color(1.0), Color() ] ])
			.action_{| v | "bit 2 was pushed".postln; };
		resetbutton = Button.new(window,Rect(430, 220, 50, 50))
			.states_([ [ "RESET", Color(), Color(1.0) ] ])
			.action_{| v | /* this.resetBits */ };
		window.view.keyDownAction =  { | view, char, modifiers, unicode, keycode |
//			keycode.postln;
			switch (keycode, 
				2, { bit0.valueAction = 1 - bit0.value },
				1, { bit1.valueAction = 1 - bit1.value  },
				0, { bit2.valueAction = 1 - bit2.value  }
			);
//			[char, modifiers, unicode, keycode].postln;
		}
	 }

/*
LHC.makeWindow;

LHC.makeRoutine;
*/

	*windowClosed {
		window = nil;
		this.stopSynthsAndProcesses;
	}
	
	*stopSynthsAndProcesses {
		routine.stop;
		routine = 0;
	}
	
	*makeRoutine {
		if (routine.isNil) {
			routine = {
				loop {
					input = ([bit0.value, bit1.value, bit2.value] * [1, 2, 4]).sum;
					{ 
						positiveEdgeDisplay.value = 1;
						(waitTime / 2).wait;
						positiveEdgeDisplay.value = 0;
					}.fork(AppClock);
					waitTime.wait;
					counter = counter + input % 8;
					counterDisplay.value = counter;
					this.calculateFSMstate(counter);
				}
			}.fork(AppClock);
		}	
	}
	
	*calculateFSMstate { | counter |
		// current state + input = next state + output
		{
			counter.asBinaryString(3) do: { | digit |
				if (digit == $1) {
					this.nextState(1);
				}{
					this.nextState(0);
				};
				(waitTime / 3).wait;
			};
		}.fork(AppClock);
	}
}
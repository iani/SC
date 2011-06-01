
/*
Globals.load;
Globals.tempo;
*/

Globals {
	
	*groups {
		var s;


		s = Server.default;
		
		~piges = Group.head(s);
		
		/*
		~mainGroup = Group.new; // add a Group at the head of the default Server's default group
		
		
		~ch1 = Group.head(~mainGroup);
			~src1 = Group.head(~ch1);
			~fx1 = Group.tail(~ch1);
		~ch2 = Group.head(~mainGroup);
			~src2 = Group.head(~ch2);
			~fx2 = Group.tail(~ch2);
		~ch3 = Group.head(~mainGroup);
			~src3 = Group.head(~ch3);
			~fx3 = Group.tail(~ch3);
		~ch4 = Group.head(~mainGroup);
			~src4 = Group.head(~ch4);
			~fx4 = Group.tail(~ch4);
		~ch5 = Group.head(~mainGroup);
			~src5 = Group.head(~ch5);
			~fx5 = Group.tail(~ch5);
		~ch6 = Group.head(~mainGroup);
			~src6 = Group.head(~ch6);
			~fx6 = Group.tail(~ch6);
		*/
		//~cleanG = Group.head(~effe);
		//~reverbG = Group.after(~cleanG);
		//~delayG = Group.after(~reverbG);
		//~rlpfG = Group.after(~delayG);
		//~wahG = Group.after(~rlpfG);
		
		//~levelA = Group.new(~piges, \addToTail);
		//~levelB = Group.new(~piges, \addToTail);
		//~levelC = Group.new(~piges, \addToTail);
		
		//~recorders = Group.new(~effe, \addAfter);

	}
	
	*buses {
		
		
		~mainBus = Bus.new(\audio, 22, 2);
		
		~limBus = Bus.new(\audio, 24, 2);
		~revBus = Bus.new(\audio, 26, 2);
		~dlyBus = Bus.new(\audio, 28, 2);
		~rlpBus = Bus.new(\audio, 30, 2);
		~wahBus = Bus.new(\audio, 32, 2);
		
 		~flowBus = Bus.new(\audio, 34, 2);



	}

	*tempo{
		
		~clock1 = TempoClock.new(1, 8, Main.elapsedTime.ceil); 
		~scl1 = Scale.huseyni;
		
	}
	*scales{
		
		~scl1 = Scale.rast;
		
	}	
}



/*


//	*new(tempo, beats, seconds, queueSize)


~clock1 = TempoClock.new(1, 8, Main.elapsedTime.ceil); 
~clock1.schedAbs(0, { arg beat, sec; [\t, beat, sec].postln; 1 });
~clock1.tempo = ~clock1.tempo*8;
~clock1.tempo = 0.5;



(

// get elapsed time, round up to next second
v = Main.elapsedTime.ceil;

// create two clocks, starting at time v.
t = TempoClock(1, 0, v);
u = TempoClock(1, 0, v);

// start two functions at beat zero in each clock.
// t controls u's tempo. They should stay in sync.
t.schedAbs(0, { arg beat, sec; u.tempo = t.tempo * [1,2,3,4,5].choose; [\t, beat, sec].postln; 1 });
u.schedAbs(0, { arg beat, sec; [\u, beat, sec].postln; 1 });
)


(
u.tempo = u.tempo * 3;
t.tempo = t.tempo * 3;
)

(
u.tempo = u.tempo * 1/4;
t.tempo = t.tempo * 1/4;
)


(
t.stop;
u.stop;
)


~scl = Scale.huseyni;
*/
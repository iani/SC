
{
	Turtle.clear;				// clear existing screen or create new clear screen
	30.wait;					// wait for setup of screen recording
	t = Turtle.draw({ | t |		// draw the monopod
		Pseq({ | i |
			Pfuncn({ t.turn(2 ** (i ** 1.1) * pi / (i + 1 ** 1.1)); t.move(64, 1) }, i + 2)
		} ! 50, 1) 
	})
}.fork(AppClock);


Turtle.draw({ | t |
	Pseq({ | i |
		Pfuncn({ t.turn(2 ** (i ** 1.1) * pi / (i + 1 ** 1.1)); t.move(64, 1) }, i + 2)
	} ! 50, 1) 
}, rate: 0)


2, 3, 4, 5 ... 51

((1..50) + 1).sum


Turtle.draw({ | t |
	Pseq({ | i |
		Pfuncn({ t.turn(2 * pi / (i + 3)); t.move(64, 1) }, (i + 2).postln)
	} ! 50
	, 1) 
});


({ | i | 2 * pi / (i + 3) } ! 50).round(0.001);
/* (IZ 2005-08-22)

Map the input value from one range to another

BiMap(inMin, inMax, inWarp, inStep, outMin, outMax, outWarp, outStep);

Usage: 

b = BiMap(30, 90, \lin, 0, 0.5, 0.05, \exp, 0.05);
(30..90).collect { |i| b.(i) };

*/

/*
TO FIND THE SUPERCOLLIDER EXTENSIONS FOLDER DO THIS IN TERMINAL

open ~/Library/Application\ Support/SuperCollider/

In the SuperCollider folder now look for the Extensions subfolder.
If it does not exist, then create it. 

Then put this file inside. 

*/

/* TESTS: 

b = BiMap(-10, 0, \lin, 0, 0, 10, \lin, 0);

b.value(-5);
b.map(-5);
b.unmap(5);


(-20..5) collect: b.value(_)
(-5..20) collect: b.unmap(_)

*/


BiMap {
	var <>inSpec,<>outSpec;
	*new { | inMin=0, inMax=1, inWarp=\lin, inStep=0, outMin=0, outMax=1, outWarp=\lin, outStep=0 |
		^this.newCopyArgs(ControlSpec(inMin, inMax, inWarp, inStep),
			ControlSpec(outMin, outMax, outWarp, outStep)
		)
	}

	value { | inval |
		^outSpec.map(inSpec.unmap(inval))
	}
	
	map { | inval |
		^this.value(inval);
	}
	
	unmap { | inval |
		^inSpec.map(outSpec.unmap(inval));
	}
}

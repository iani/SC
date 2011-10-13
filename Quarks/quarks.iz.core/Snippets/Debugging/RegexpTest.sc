/* Trying to find source of erratic bug in Code:init due to problems with findRegexp.

The error cannot be reproduced here.

It persists in Code:init if one removes the check for positions[0] being kind of string. 

*/


RegexpTest {
	classvar <>string, positions, <routine, <doc, <snippetSeparator;
	*run {
				doc = Document.current;
				string = doc.string;
		
		routine = fork {
			loop {
				0.1.wait;
				if (string[52..61] == "// History") { snippetSeparator = " - " };
				positions = string.findRegexp("^//:");
				if ((positions ? [])[0].isKindOf(String)) {
					"error ".post;
				}{
					"ok ".post;
				}
			}
		}
	}
	
	*stop { routine.stop }
}
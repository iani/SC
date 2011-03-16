MIDI: 


// MIDIClient.init;

// MIDIIn


	MIDIIn.connect; 	// init for one port midi interface
// register functions:	
	MIDIIn.noteOff = { arg src, chan, num, vel; 	["noteoff", chan,num,vel / 127].postln; };
	MIDIIn.noteOn = { arg src, chan, num, vel; 	["noteOn", chan,num,vel / 127].postln; };
	MIDIIn.polytouch = { arg src, chan, num, vel; 	["poly", chan,num,vel / 127].postln; };
	MIDIIn.control = { arg src, chan, num, val; 	["cc", chan,num,val].postln; };
	MIDIIn.program = { arg src, chan, prog; 		["pgm", chan,prog].postln; };
	MIDIIn.touch = { arg src, chan, pressure; 	["touch", chan,pressure].postln; };
	MIDIIn.bend = { arg src, chan, bend; 			["bend", chan,bend - 8192].postln; };
	MIDIIn.sysex = { arg src, sysex; 			sysex.postln; };
	MIDIIn.sysrt = { arg src, chan, val; 			[chan,val].postln; };
	MIDIIn.smpte = { arg src, chan, val; 			[chan,val].postln; };

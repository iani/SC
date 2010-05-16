// (SC3) Script for session 'File Playback + Sampling'
// Generated on Fri May 16 13:46:09 2008

~sessionName = "File Playback + Sampling";
~server = Server.named['localhost'] ? { Server.default };
~serverAddressDetails = [ "127.0.0.1", 57110 ];	// Servers address saved for reference

//List of paths of samples to be loaded for this session:
~samplePaths = [
	"./sounds/a11wlk01.wav",
	"./sounds/a11wlk01-44_1.aiff",
	"[ 'Scripts:bufrecord_1', 705600, 1 ]",
	"[ 'Scripts:bufrecord_1[2]', 705600, 1 ]",
	"./sounds/SinedPink.aiff",
];

// Specs for creating scripts: names of Groups, Folders, Files, Scripts
~scriptGroups = [
	["Scripts",
		[ 'Buffers+audio files', 'bufrecord_1', 'bufrecord_1', 0.0, 241.0, true ],
		[ 'Buffers+audio files', 'playbuf (looping)', 'playbuf (looping)', 300.0, 262.0, true ],
		[ 'Buffers+audio files', 'bufrecord_1', 'bufrecord_1[2]', 600.0, 241.0, true ],
		[ 'Buffers+audio files', 'playbuf (looping)', 'playbuf (looping)[2]', 900.0, 262.0, true ],
	],
];

// Specs for creating links between scripts:
~linkSpecs = [
];

// Specs to create MIDIResponders for each Script:
~midiBindings = [
];

// Specs for the parameter snapshots of the scripts:
~snapshots = [
];

// -------------- SCRIPT-SPECIFIC DATA: --------------
~scriptData = [['Scripts', 'playbuf (looping)',
],
['Scripts', 'playbuf (looping)[2]',
],

];
// -------------- END SCRIPT-SPECIFIC DATA --------------

// the common part of the script for starting and stopping is included as template:
~script.include('Includes', 'SessionLoad');

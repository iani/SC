// (SC3) Script for session 'TriggerTestSession'
// Generated on Sun May 11 18:43:45 2008

~sessionName = "TriggerTestSession";
~server = Server.named['localhost'] ? { Server.default };
~serverAddressDetails = [ "127.0.0.1", 57110 ];	// Servers address saved for reference

//List of paths of samples to be loaded for this session:
~samplePaths = [
	"./sounds/a11wlk01.wav",
	"./sounds/a11wlk01-44_1.aiff",
	"./sounds/SinedPink.aiff",
];

// Specs for creating scripts: names of Groups, Folders, Files, Scripts
~scriptGroups = [
	["Scripts",
		[ 'Examples', 'decaysin', 'decaysin', 803.0, 243.0, true ],
		[ 'Trigger Script Tests', 'TriggerTest1', 'TriggerTest1', 1100.0, 391.0, true ],
	],
];

// Specs for creating links between scripts:
~linkSpecs = [
	[ 'Scripts', 'TriggerTest1', 't_out', 'Scripts', 'decaysin', 'freq' ],
];

// Specs to create MIDIResponders for each Script:
~midiBindings = [
['Scripts', 'decaysin',
	['script', CCResponder(nil, nil, 0, 19, nil, false)],
	['vibfreq', CCResponder(nil, nil, 0, 7, nil, false)],
],
];

// Specs for the parameter snapshots of the scripts:
~snapshots = [
];

// -------------- SCRIPT-SPECIFIC DATA: --------------
~scriptData = [['Scripts', 'TriggerTest1',"hello"
],

];
// -------------- END SCRIPT-SPECIFIC DATA --------------

// the common part of the script for starting and stopping is included as template:
~script.include('Includes', 'SessionLoad');

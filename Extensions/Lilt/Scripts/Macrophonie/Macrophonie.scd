// (SC3) Script for session 'Macrophonie'
// Generated on Sat May 17 21:14:58 2008

~sessionName = "Macrophonie";
~server = Server.named['localhost'] ? { Server.default };
~serverAddressDetails = [ "127.0.0.1", 57110 ];	// Servers address saved for reference

//List of paths of samples to be loaded for this session:
~samplePaths = [
	"[ 'Scripts:bufrecord_1', 705600, 1 ]",
	"/Applications/SuperColliderWithExtras/sounds/Makrophonie/17 mic_wahwah.aiff",
	"/Applications/SuperColliderWithExtras/sounds/Makrophonie/07 wackelnderStein.aiff",
	"/Applications/SuperColliderWithExtras/sounds/Makrophonie/15 nerverumble+bang.aiff",
	"/Applications/SuperColliderWithExtras/sounds/Makrophonie/20 plainlongding.aiff",
	"/Applications/SuperColliderWithExtras/sounds/Makrophonie/18 mic_ding_wahwah.aiff",
	"/Applications/SuperColliderWithExtras/sounds/Makrophonie/13 thopeia_psithyrou.aiff",
	"/Applications/SuperColliderWithExtras/sounds/Makrophonie/14 mumbling_crackles.aiff",
	"./sounds/a11wlk01.wav",
	"./sounds/a11wlk01-44_1.aiff",
	"/Applications/SuperColliderWithExtras/sounds/Makrophonie/08 scrape.aiff",
	"[ 'Scripts:bufrecord_1[2]', 705600, 1 ]",
	"/Applications/SuperColliderWithExtras/sounds/Makrophonie/09 shrumble.aiff",
	"/Applications/SuperColliderWithExtras/sounds/Makrophonie/06 stone2.aiff",
	"/Applications/SuperColliderWithExtras/sounds/Makrophonie/19 mic_ding_wah1.aiff",
	"/Applications/SuperColliderWithExtras/sounds/Makrophonie/02 wackydonner2.aiff",
	"/Applications/SuperColliderWithExtras/sounds/Makrophonie/01 wackydonner.aiff",
	"/Applications/SuperColliderWithExtras/sounds/Makrophonie/05 stone.aiff",
	"/Applications/SuperColliderWithExtras/sounds/Makrophonie/11 gentle_crackle.aiff",
	"/Applications/SuperColliderWithExtras/sounds/Makrophonie/10 crackle.aiff",
	"./sounds/SinedPink.aiff",
	"/Applications/SuperColliderWithExtras/sounds/Makrophonie/12 sparse_abrupts.aiff",
	"/Applications/SuperColliderWithExtras/sounds/Makrophonie/16 opening_gesture.aiff",
	"/Applications/SuperColliderWithExtras/sounds/Makrophonie/03 wackydonner3.aiff",
	"[ 'Scripts:bufrecord_1[3]', 705600, 1 ]",
	"/Applications/SuperColliderWithExtras/sounds/Makrophonie/04 enddampedtrill.aiff",
];

// Specs for creating scripts: names of Groups, Folders, Files, Scripts
~scriptGroups = [
	["Scripts",
		[ 'Buffers+audio files', 'bufrecord_1', 'bufrecord_1', 0.0, 241.0, false ],
		[ 'Buffers+audio files', 'playbuf (looping)', 'playbuf (looping)', 6.0, 682.0, true ],
		[ 'Buffers+audio files', 'bufrecord_1', 'bufrecord_1[2]', 600.0, 241.0, false ],
		[ 'Buffers+audio files', 'playbuf (looping)', 'playbuf (looping)[2]', 4.0, 486.0, true ],
		[ 'Buffers+audio files', 'play sound file', 'play sound file', 1136.0, 167.0, true ],
		[ 'IO', 'input_monitor', 'input_monitor', 1137.0, 54.0, true ],
		[ 'Triggers', 'StartOnBoot', 'StartOnBoot', 203.0, 2.0, true ],
		[ 'Buffers+audio files', 'BufSculptAlgo', 'BufSculptAlgo', 939.0, 808.0, true ],
		[ 'Spectral Synths', 'GraphicFilterInput', 'GraphicFilterInput', 938.0, 715.0, true ],
		[ 'Buffers+audio files', 'playbuf (looping)', 'playbuf (looping)[3]', 3.0, 290.0, true ],
		[ 'Buffers+audio files', 'playbuf (looping)', 'playbuf (looping)[4]', 1.0, 91.0, true ],
		[ 'Buffers+audio files', 'playbuf (looping)', 'playbuf (looping)[5]', 639.0, 682.0, true ],
		[ 'Buffers+audio files', 'playbuf (looping)', 'playbuf (looping)[6]', 637.0, 486.0, true ],
		[ 'Buffers+audio files', 'playbuf (looping)', 'playbuf (looping)[7]', 636.0, 290.0, true ],
		[ 'Buffers+audio files', 'playbuf (looping)', 'playbuf (looping)[8]', 633.0, 90.0, true ],
		[ 'Spectral Synths', 'PV_BinShift', 'PV_BinShift', 305.0, 703.0, true ],
		[ 'Spectral Synths', 'PV_LocalMax', 'PV_LocalMax', 306.0, 529.0, true ],
		[ 'Spectral Synths', 'PV_MagAbove', 'PV_MagAbove', 303.0, 331.0, true ],
		[ 'Spectral Synths', 'PV_RectComb', 'PV_RectComb', 300.0, 134.0, true ],
		[ 'Spectral Synths', 'PV_BinScramble', 'PV_BinScramble', 938.0, 527.0, true ],
		[ 'Spectral Synths', 'PV_MagSmear', 'PV_MagSmear', 939.0, 394.0, true ],
		[ 'Fx', 'formlet', 'formlet', 936.0, 97.0, true ],
	],
	["Triggers",
		[ 'Macrophonie', 'MasterTrigger', 'MasterTrigger', 1135.0, 488.0, true ],
		[ 'Macrophonie', 'RandomizeOutputs', 'RandomizeOutputs', 1134.0, 354.0, true ],
	],
];

// Specs for creating links between scripts:
~linkSpecs = [
	[ 'Scripts', 'playbuf (looping)', 'out', 'Scripts', 'PV_BinShift', 'in' ],
	[ 'Scripts', 'playbuf (looping)[2]', 'out', 'Scripts', 'PV_LocalMax', 'in' ],
	[ 'Scripts', 'playbuf (looping)[3]', 'out', 'Scripts', 'PV_MagAbove', 'in' ],
	[ 'Scripts', 'playbuf (looping)[4]', 'out', 'Scripts', 'PV_RectComb', 'in' ],
	[ 'Scripts', 'playbuf (looping)[5]', 'out', 'Scripts', 'GraphicFilterInput', 'in' ],
	[ 'Scripts', 'playbuf (looping)[6]', 'out', 'Scripts', 'PV_BinScramble', 'in' ],
	[ 'Scripts', 'playbuf (looping)[7]', 'out', 'Scripts', 'PV_MagSmear', 'in' ],
	[ 'Scripts', 'playbuf (looping)[8]', 'out', 'Scripts', 'formlet', 'in' ],
	[ 'Triggers', 'MasterTrigger', 't_out', 'Scripts', 'playbuf (looping)', 'out' ],
	[ 'Triggers', 'MasterTrigger', 't_out', 'Scripts', 'playbuf (looping)[2]', 'out' ],
	[ 'Triggers', 'MasterTrigger', 't_out', 'Scripts', 'playbuf (looping)[3]', 'rate' ],
	[ 'Triggers', 'MasterTrigger', 't_out', 'Scripts', 'PV_BinShift', 'in' ],
	[ 'Triggers', 'MasterTrigger', 't_out', 'Scripts', 'PV_LocalMax', 'in' ],
	[ 'Triggers', 'MasterTrigger', 't_out', 'Scripts', 'PV_MagAbove', 'in' ],
	[ 'Triggers', 'MasterTrigger', 't_out', 'Scripts', 'PV_RectComb', 'in' ],
	[ 'Triggers', 'MasterTrigger', 't_out', 'Scripts', 'playbuf (looping)[5]', 'out' ],
	[ 'Triggers', 'MasterTrigger', 't_out', 'Scripts', 'playbuf (looping)[6]', 'out' ],
	[ 'Triggers', 'MasterTrigger', 't_out', 'Scripts', 'playbuf (looping)[7]', 'out' ],
	[ 'Triggers', 'MasterTrigger', 't_out', 'Scripts', 'playbuf (looping)[8]', 'out' ],
	[ 'Triggers', 'MasterTrigger', 't_out', 'Scripts', 'GraphicFilterInput', 'in' ],
	[ 'Triggers', 'MasterTrigger', 't_out', 'Scripts', 'PV_BinScramble', 'in' ],
	[ 'Triggers', 'MasterTrigger', 't_out', 'Scripts', 'PV_MagSmear', 'in' ],
	[ 'Triggers', 'MasterTrigger', 't_out', 'Scripts', 'formlet', 'in' ],
	[ 'Triggers', 'MasterTrigger', 't_out', 'Scripts', 'playbuf (looping)[4]', 'out' ],
];

// Specs to create MIDIResponders for each Script:
~midiBindings = [
['Scripts', 'playbuf (looping)',
	['script', CCResponder(nil, nil, 0, 65, nil, false)],
	['bufnum', CCResponder(nil, nil, 0, 1, nil, false)],
	['rate', CCResponder(nil, nil, 0, 81, nil, false)],
	['loop', CCResponder(nil, nil, 0, 73, nil, false)],
	['amp', CCResponder(nil, nil, 0, 89, nil, false)],
],
['Scripts', 'playbuf (looping)[2]',
	['script', CCResponder(nil, nil, 0, 66, nil, false)],
	['bufnum', CCResponder(nil, nil, 0, 2, nil, false)],
	['rate', CCResponder(nil, nil, 0, 82, nil, false)],
	['loop', CCResponder(nil, nil, 0, 74, nil, false)],
	['amp', CCResponder(nil, nil, 0, 90, nil, false)],
],
['Scripts', 'playbuf (looping)[3]',
	['script', CCResponder(nil, nil, 0, 67, nil, false)],
	['bufnum', CCResponder(nil, nil, 0, 3, nil, false)],
	['rate', CCResponder(nil, nil, 0, 83, nil, false)],
	['loop', CCResponder(nil, nil, 0, 75, nil, false)],
	['amp', CCResponder(nil, nil, 0, 91, nil, false)],
],
['Scripts', 'playbuf (looping)[4]',
	['script', CCResponder(nil, nil, 0, 68, nil, false)],
	['bufnum', CCResponder(nil, nil, 0, 4, nil, false)],
	['rate', CCResponder(nil, nil, 0, 84, nil, false)],
	['loop', CCResponder(nil, nil, 0, 76, nil, false)],
	['amp', CCResponder(nil, nil, 0, 92, nil, false)],
],
['Scripts', 'playbuf (looping)[5]',
	['script', CCResponder(nil, nil, 0, 69, nil, false)],
	['bufnum', CCResponder(nil, nil, 0, 5, nil, false)],
	['rate', CCResponder(nil, nil, 0, 85, nil, false)],
	['loop', CCResponder(nil, nil, 0, 77, nil, false)],
	['amp', CCResponder(nil, nil, 0, 93, nil, false)],
],
['Scripts', 'playbuf (looping)[6]',
	['script', CCResponder(nil, nil, 0, 70, nil, false)],
	['bufnum', CCResponder(nil, nil, 0, 6, nil, false)],
	['rate', CCResponder(nil, nil, 0, 86, nil, false)],
	['loop', CCResponder(nil, nil, 0, 78, nil, false)],
	['amp', CCResponder(nil, nil, 0, 102, nil, false)],
],
['Scripts', 'playbuf (looping)[7]',
	['script', CCResponder(nil, nil, 0, 71, nil, false)],
	['bufnum', CCResponder(nil, nil, 0, 7, nil, false)],
	['rate', CCResponder(nil, nil, 0, 87, nil, false)],
	['loop', CCResponder(nil, nil, 0, 79, nil, false)],
	['amp', CCResponder(nil, nil, 0, 95, nil, false)],
],
['Scripts', 'playbuf (looping)[8]',
	['script', CCResponder(nil, nil, 0, 72, nil, false)],
	['bufnum', CCResponder(nil, nil, 0, 8, nil, false)],
	['rate', CCResponder(nil, nil, 0, 88, nil, false)],
	['loop', CCResponder(nil, nil, 0, 80, nil, false)],
	['amp', CCResponder(nil, nil, 0, 96, nil, false)],
],
['Scripts', 'PV_BinShift',
	['stretch', CCResponder(nil, nil, 0, 20, nil, false)],
	['shift', CCResponder(nil, nil, 0, 50, nil, false)],
	['source_level', CCResponder(nil, nil, 0, 45, nil, false)],
	['fx_level', CCResponder(nil, nil, 0, 105, nil, false)],
],
['Scripts', 'PV_LocalMax',
	['threshold', CCResponder(nil, nil, 0, 51, nil, false)],
	['source_level', CCResponder(nil, nil, 0, 46, nil, false)],
	['fx_level', CCResponder(nil, nil, 0, 106, nil, false)],
],
['Scripts', 'PV_MagAbove',
	['threshold', CCResponder(nil, nil, 0, 55, nil, false)],
	['source_level', CCResponder(nil, nil, 0, 47, nil, false)],
	['fx_level', CCResponder(nil, nil, 0, 107, nil, false)],
],
['Scripts', 'PV_RectComb',
	['numTeeth', CCResponder(nil, nil, 0, 43, nil, false)],
	['width', CCResponder(nil, nil, 0, 87, nil, false)],
	['source_level', CCResponder(nil, nil, 0, 5, nil, false)],
	['fx_level', CCResponder(nil, nil, 0, 108, nil, false)],
],
];

// Specs for the parameter snapshots of the scripts:
~snapshots = [
];

// -------------- SCRIPT-SPECIFIC DATA: --------------
~scriptData = [['Scripts', 'playbuf (looping)',"nil"
],
['Scripts', 'playbuf (looping)[2]',"nil"
],
['Scripts', 'play sound file',[  ]
],
['Scripts', 'playbuf (looping)[3]',"nil"
],
['Scripts', 'playbuf (looping)[4]',"/Applications/SuperColliderWithExtras/sounds/Makrophonie/13 thopeia_psithyrou.aiff"
],
['Scripts', 'playbuf (looping)[5]',"nil"
],
['Scripts', 'playbuf (looping)[6]',"nil"
],
['Scripts', 'playbuf (looping)[7]',"/Applications/SuperColliderWithExtras/sounds/Makrophonie/02 wackydonner2.aiff"
],
['Scripts', 'playbuf (looping)[8]',"/Applications/SuperColliderWithExtras/sounds/Makrophonie/14 mumbling_crackles.aiff"
],

];
// -------------- END SCRIPT-SPECIFIC DATA --------------

// the common part of the script for starting and stopping is included as template:
~script.include('Includes', 'SessionLoad');

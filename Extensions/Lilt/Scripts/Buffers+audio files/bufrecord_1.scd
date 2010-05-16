// (SC3:) Generated from SynthDesc on: Mon May 12 15:46:08 2008

// loadBuffer { | bufName = 'buffer', param = 'bufnum', warnIfNotFound = true, numFrames = 44100, numChans = 1, server |

var script;
script = ~script;

script doWhenInited: {
//	thisMethod.report("TTTTTTTTTTTTTEEEEEEEEEE", script);
	script.loadBuffer(format("%:%", script.scriptGroup.name, script.name), numFrames: 44100 * 16, warnIfNotFound: false);
};

~controlSpecs = [
[ 'in', 0.0, 127.0, 'linear', 1.0, 0.0 ],
[ 'bufnum', 0.0, 1023.0, 'linear', 1.0, 0.0 ],
[ 'offset', 0.0, 1.0, 'linear', 0.0, 0.0 ],
[ 'recLevel', 0.0, 2.0, 'linear', 0.0, 1.0 ],
[ 'preLevel', 0.0, 1.0, 'linear', 0.0, 0.0 ],
[ 'run', 0.0, 2.0, 'linear', 0.0, 1.0 ],
[ 'loop', 0.0, 1.0, 'linear', 1.0, 0.0 ],
[ 'trigger', 0.0, 1.0, 'linear', 1.0, 1.0 ],
// [ 'buffer_size', 1000, 4410000, \linear, 1, 441000]
];

~start = { Synth("bufrecord_1", ['in', ~in, 'bufnum', ~bufnum, 'offset', ~offset, 'recLevel', ~recLevel, 
	'preLevel', ~preLevel, 'run', ~run, 'loop', ~loop, 'trigger', ~trigger],
	~target, #[\h, \t, \addBefore, \addAfter, \addReplace][~addAction])
};

/*
~saveData = { | envir, file |
	file.putAll(envir[\buffer_size].asCompileString);	
};

~loadData = { | envir, data |
	var bufname;
//	envir[\buffer_size].postln;
//	script.name.postln;
	
	if ((bufname = envir.bufname).isNil) {
		envir[\bufname] = bufname = format("%:%", script.scriptGroup.name, script.name)
	};
	
};
*/
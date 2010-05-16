
var script;
script = ~script;

~controlSpecs = [
[ 'out', 0, 4095, 'linear', 1, 0 ],
[ 'bufnum', 1, 128, 'linear', 1, 0 ],
[ 'rate', 0.125, 8, 'exp', 0, 1 ],
[ 'trigger', 0, 1, 'linear', 1, 1 ],
[ 'startPos', 0, 1, 'linear', 0, 0 ],
[ 'loop', 0, 1, 'linear', 1, 1 ],
[ 'amp', 0, 1, 'amp', 0, 1 ]
];

~start = {
	Synth("playbuf_looping", 
		['out', ~out, 'bufnum', ~bufnum, 'rate', ~rate, 'trigger', ~trigger, 
			'startPos', ~startPos, 'loop', ~loop, 'amp', ~amp],
		~target, #[\h, \t, \addBefore, \addAfter, \addReplace][~addAction]
	)
};


~saveData = { | envir, file |
	var bufnum, buffer;
//	script.postln;
	bufnum = envir['bufnum'];
	buffer = Samples(script.session.server).buffers detect: { | b | b.bufnum == bufnum };
	
//	Post << "playbuf (looping) scirpt SAVVVVVVE data: bufnum is" << envir['bufnum'] << " buffer is: " << buffer << "\n";	
	if (buffer.notNil) {
		file.putAll(buffer.path.asString.asCompileString);
	};
};

~loadData = { | envir, data |
//	Post << "playbuf (looping) script LOAD data: " << data << "\n";	
	if (data.notNil) {
		script.loadBufferFromPath(data, 'bufnum');
	}
};

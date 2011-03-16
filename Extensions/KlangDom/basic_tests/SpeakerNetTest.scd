


~loadbuffers = { 
	~buffers = IdentityDictionary.new;
	
	Server.default.waitForBoot({
		"test".postln;	
		(Platform.userAppSupportDir ++ "/sounds/*").pathMatch.do({ | folder |
			(folder ++ "/*.aiff").pathMatch.do({ | path |
				~buffers[path.basename.splitext.first.asSymbol] = Buffer.read(Server.default, path);
			});
		})
	});
	~buffers;
};

~buffers[\weddella].path

~buffers[\weddelle].play;
~buffers[\swallowsb].play;


~buffers.keys

a = { | in = 0 |
	var outputs;
	var buffers;
	var swallows, seals1, dfd, newstar, seals2;

	buffers = ~buffers; // ~loadbuffers.value;

	seals1 = buffers[\weddelle];
	swallows = buffers[\swallowsb];

};
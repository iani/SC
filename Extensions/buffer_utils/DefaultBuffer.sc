/*
!!!!!!!!!!!!!!! THIS CLASS COULD BE EXPANDED INTO A CLASS BufLib !!!!!!!!!!!!!


DefaultBuffer.play;

DefaultBuffer.loadDialog;

DefaultBuffer.loadDialog.play; // plays the old buffer because loading is asynchronous

DefaultBuffer loadDialog: _.play; // plays new buffer

DefaultBuffer loadDialog: _.postInfo;

DefaultBuffer.buffer;

DefaultBuffer.buffer.play;

PlayBuf.ar(numChannels, bufnum, rate, trigger, startPos, loop, doneAction)

DefaultBuffer.loadTo($a);

a;

DefaultBuffer.loadTo(\buffer);

~buffer;

*/

DefaultBuffer {
	classvar all;
	classvar <>defaultPath = "sounds/a11wlk01.wav";
	var <server;
	var <>path;
	var <buffer;

	*initClass { all = IdentityDictionary.new; }
	*default { ^this.new(Server.default); }
	*new { | server, path |
		if (all[server].isNil) { all[server] = this.newCopyArgs(server, path ? defaultPath); };
		^all[server];
	}
	*loadDialog { | func |
		^this.default.loadDialog({ | p | 
			this.defaultPath = p;
			func.value(this.default);
		})
	}
	loadDialog { | onLoadFunc | 
		Dialog.getPaths({ | paths | 
			path = paths.first;
			onLoadFunc.(path);
		});
	}

	*play { | funcOrSynthDef |
		// one can optionally provide a synthdef or function to play the buffer with
		^this.default play: funcOrSynthDef;
	}

	play { | funcOrSynthDef |
		if (funcOrSynthDef isKindOf: SynthDef) {
			this loadBufferIfNeededAndDo: { funcOrSynthDef.play(server, [\bufnum, buffer.bufnum]) };
		}{
			this.loadBufferIfNeededAndDo(
				if (funcOrSynthDef.isNil) {
					{ this.buffer.play }
				}{
					{ funcOrSynthDef.(buffer) }
				});
		}
	}

	loadBufferIfNeededAndDo { | func |
		// cannot use doIfFileExists here because it causes infinite loops with other calls
		if (path.pathMatch.size == 0) { ^postf("file not found: %\n", path); };
		if (server.serverRunning.not) {
			^server.waitForBoot({ this loadBufferAndDo: func });
		};
		if (buffer.notNil and: { buffer.numFrames == SoundFile.use(path, _.numFrames) }) {
			func.(buffer, this);
		}{
			if (buffer.notNil) { buffer.free };	// free RAM of server from old buffer
			this loadBufferAndDo: func;
		}
	}

	loadBufferAndDo { | func |
		buffer = Buffer.read(server, path, action: { | b | func.(b, this) }); 
	}

	*postInfo { this.default.postInfo }
	postInfo {
		var dur;
		this doIfFileExists: { 
			this loadBufferIfNeededAndDo: {
				dur = buffer.numFrames / buffer.sampleRate;
				postln("========= BUFFER INFO : =========");
				postf("path: %\nbufnum: %\nnumChannels: %\nnumFrames: %\nsampleRate: %
duration: % minutes and % seconds\n",
					buffer.path, buffer.bufnum, buffer.numChannels, 
					buffer.numFrames, buffer.sampleRate, 
					(dur / 60) round: 1, dur % 60
				); 
			}
		}
	}

	doIfFileExists { | func |
		if (path.pathMatch.size == 0) { ^postf("file not found: %\n", path); };
		func.value;
	}

	*showInFinder { this.default.showInFinder }
	showInFinder {
		this doIfFileExists: { format("open %", path.dirname.asCompileString).unixCmd }; 
	}

	*openWithAudacity { this.default.openWithAudacity }
	openWithAudacity {
		this doIfFileExists: { format("open -a Audacity.app %", path.asCompileString).unixCmd }; 
	}

	*buffer { ^this.default.buffer }

	*loadTo { | toLoad |
		this.default.loadTo(toLoad);		
	}

	loadTo { | toLoad |
		case
		{ toLoad isKindOf: Char } {
			this loadBufferIfNeededAndDo: { thisProcess.interpreter.perform(asSymbol(toLoad.asString ++ "_"), buffer) }
		}
		{ toLoad isKindOf: Symbol } {
			this loadBufferIfNeededAndDo: { currentEnvironment[toLoad] = buffer }
		}
		{ true } { this loadBufferIfNeededAndDo: { toLoad.(buffer) } }
	}	
}
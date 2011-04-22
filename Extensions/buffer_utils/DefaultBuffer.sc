/*
!!!!!!!!!!!!!!! THIS CLASS COULD BE EXPANDED INTO A CLASS BufLib !!!!!!!!!!!!!

*/

DefaultBuffer {
	classvar all;
	classvar <>defaultPath = "sounds/a11wlk01.wav";
	var <server;
	var <path;
	var <buffer;
	var <synth;	// stores the latest synth created by play

	*initClass { all = IdentityDictionary.new; }
	*default { ^this.new(Server.default); }
	*reset { | server | // restore the default sample path
		^this.new(server ? Server.default).path_(defaultPath);
	}

	*isLoaded { | server |
		server = server ? Server.default;
		^all[server].notNil and: { all[server].buffer.notNil };
	}

	*new { | server, path, play |
		var new;
		server = server ? Server.default;
		if (all[server].isNil) { 
			all[server] = new = this.newCopyArgs(server, path ? defaultPath);
		}{
			new = all[server];
		};
		if (play.notNil) { new.play(play) };
		^all[server];
	}
	*loadDialog { | func, server |
		^this.new(server ? Server.default).loadDialog(func)
	}
	loadDialog { | func | 
		Dialog.getPaths({ | paths | 
			path = paths.first;
			if (func.notNil) { this.play(func); };
		});
	}
 
 	*defaultPlayFunc { ^{ | buffer | buffer.play } }
 	defaultPlayFunc { ^this.class.defaultPlayFunc }
 
	*play { | funcOrSynthDef |
		// one can optionally provide a synthdef or function to play the buffer with
		^this.default play: funcOrSynthDef;
	}

	play { | funcOrSynthDef |
		if (funcOrSynthDef isKindOf: SynthDef) {
			this loadBufferIfNeededAndDo: { funcOrSynthDef.play(server, [\bufnum, buffer.bufnum]) };
		}{
			this.loadBufferIfNeededAndDo(funcOrSynthDef ?? this.defaultPlayFunc);
		}
	}

	loadBufferIfNeededAndDo { | func |
		// cannot use doIfFileExists here because it causes infinite loops with other calls
		if (path.pathMatch.size == 0) { ^postf("file not found: %\n", path); };
		if (func.isNil) { ^this };
		if (server.serverRunning.not) {
			^server.waitForBoot({ this loadBufferAndDo: func });
		};
		if (buffer.notNil and: { buffer.numFrames == SoundFile.use(path, _.numFrames) }) {
			~synth = synth = func.(buffer, this);
		}{
			if (buffer.notNil) { buffer.free };	// free RAM of server from old buffer
			this loadBufferAndDo: func;
		}
	}

	loadBufferAndDo { | func |
		buffer = Buffer.read(server, path, action: { | b | ~synth = synth = func.(b, this); }); 
	}

	*postInfo { this.default.postInfo }
	postInfo {
		var data;
		this doIfFileExists: { 
			this loadBufferIfNeededAndDo: {
				data = [buffer.path, buffer.bufnum, buffer.numChannels, 
					buffer.numFrames, buffer.sampleRate, buffer.numFrames / buffer.sampleRate];
				postln("========= BUFFER INFO : =========");
				postf("path: %\nbufnum: %\nnumChannels: %\nnumFrames: %\nsampleRate: %
duration: % minutes and % seconds\n",
					buffer.path, buffer.bufnum, buffer.numChannels, 
					buffer.numFrames, buffer.sampleRate, 
					(data.last / 60) round: 1, data.last % 60);
			}
		}
		^data;
	}

	doIfFileExists { | func |
		if (path.pathMatch.size == 0) { ^postf("file not found: %\n", path); };
		func.value;
	}

	*spectrogram { | funcOrSynthDef |
		^this.default spectrogram: funcOrSynthDef;
	}

	spectrogram { | funcOrSynthDef |
		^this play: {
			{ Spectrogram2(bounds: Rect(0, 0, 1000, 200)).start; }.defer;
			this play: funcOrSynthDef;
		};
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
	
	printOn { | stream |
		stream << this.class.name << "(";
		buffer.printOn(stream);
		stream << ")";
	}	
}


ProcessSoundFile {
	var synthdef, inpath, outpath, numInputChannels, numOutputChannels;
	var basepath;

	*new { | synthdef, inpath, outpath, numInputChannels = 1, numOutputChannels = 1 |
		^this.newCopyArgs(
			synthdef = synthdef ?? { this.defaultSynthDef }, 
			inpath = inpath ?? { this.defaultInPath }, 
			outpath = outpath,
			numInputChannels = numInputChannels ?? { this.defaultNumInputChannels },
			numOutputChannels = numOutputChannels ?? { this.defaultNumOutputChannels }
		).init;
	}

	*defaultSynthDef {
		^SynthDef("io", { | in = 1 | Out.ar(0, In.ar(in)); });
	}

	*defaultInPath {
		^"sounds/a11wlk01-44_1.aiff";
	}

	*defaultNumInputChannels { ^1 }
	*defaultNumOutputChannels { ^1 }

	init {
		synthdef.writeDefFile;
	}

	score {
		var soundfile, duration;
		soundfile = SoundFile.collect(inpath).first;
		duration = soundfile.numFrames / soundfile.sampleRate;
		^[
			[0.0, [\s_new, synthdef.name, 1000, 0, 0, \in, numOutputChannels]],
			[0.01, [\s_new, \endtrigger, 1001, 0, 0]],
			[duration, [\n_free, 1000]],
			[duration + 0.001, [\c_set, 0, 0]] // finish
		]
	}
	
	process {
		var options;
		options = ServerOptions.new;
		options.numOutputBusChannels = numOutputChannels;
		options.numInputBusChannels = numInputChannels;
		Score.recordNRT(
			this.score,
			this.scorepath,
			this.outpath, // Date.getDate.stamp ++ ".aiff",
			inputFilePath: inpath,
			options: options
		);	
	}
	
	scorepath { ^this.basepath; }
	outpath { ^this.basepath ++ ".aiff" }
	
	basepath {
		// if outpath not provided by user, create base path from inpath.
		// in that case, always add a new timestamp 
		if (outpath.isNil) {
			basepath = format("%_%", PathName(inpath).withoutExtension, Date.getDate.stamp);
		}{
		// if outpath is provided, get basepath from it, without time stamp
			basepath = PathName(outpath).withoutExtension;
		};
		^basepath;
	}

	openOutputFileWithAudacity {
		this doIfOutputExists: {
			format("open -a Audacity.app %", basepath ++ ".aiff").unixCmd;
		};
	}
	
	openOutputFolder {
		this doIfOutputExists: {
			format("open %", basepath.dirname.asCompileString).unixCmd;
		}
	}
	
	doIfOutputExists { | func |
		if (basepath.isNil) { ^"output file does not exist. Run method 'process' to create file".postln; };
		func.value;		
	}
}


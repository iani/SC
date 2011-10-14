LilypondEdit222223222222 {
	classvar <notes, <alterations;
	var <path, <source, <header;
	var <file;

	*initClass {
		notes = [$c, $c, $d, $d, $e, $f, $f, $g, $g, $a, $a, $b];
		alterations = (
			es: -1,
			eses: -2,
			is: 1,
			isis: 2
		);	
	}

	*new { | path, source, header |
		^super.newCopyArgs(path, source, header).init;	
	}
	
	init {
		path = path ?? { UserPath("test.ly") };
		file = File(path, "w");
		this.writeHeader;
		this.writeScore;
		this.close;
		postf("wrote score at: %", path);
	}
	
	writeHeader {
		header = header ?? { this.makeHeader };
		file.putString("\\header {" /* } */ );
		header keysValuesDo: { | key, value |
			file.putString(format("\n\t% = %", key, value.asCompileString));
		};
		file.putString(/* { */ "\n}\n");
	}

	makeHeader {
		^(title: PathName(path).fileNameWithoutExtension);
	}

	writeScore {
		this.openScore;
		this.writeScoreEvents;
		this.closeScore;
	}
	
	openScore {
		file.putString("\n\\absolute {\n\t" /* } */)
	}
	
	writeScoreEvents { | maxEvents = 1000 |
		var stream, event, count = 0;
		this.initSource;
		stream = source.asStream;
		while { 
			(event = stream.next).notNil and: {
				count < maxEvents
			};
		}{
			file putString: event.lilyPond;
			file putString: " ";
			count = count + 1;
		};	
	}
	
	initSource {
		var notes = [\a, \b, \c, \d, \e, \f, \g];
		source = source ?? { 
			Pseq([Pser(notes.scramble, 1), Prand(notes, 10)], 5); 
		};	
	}
	
	closeScore {
		file.putString(/* { */"\n}\n" )	
	}
	
	close {
		file.close;
	}
}
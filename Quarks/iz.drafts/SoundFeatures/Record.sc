/* IZ 20120418 

Record onto disk for a specific amount of time, executing a given sound-generating function. 

//:--
Record(
	{ var a = { WhiteNoise.ar(0.1) }.play; 2.wait; a.free }, 
	3,
	RecPath.stamp("~/Desktop/SCRECTEST")
);

RecPath.showFolder;

//:--
RecPath.defaultPath = "~/Desktop/adsfflucturtrasdf";
RecPath.showFolder;
Record(
	{ var a = { WhiteNoise.ar(0.1) }.play; 2.wait; a.free }, 
	3,
	RecPath.stamp;
);
RecPath.showFolder;

//:--
*/

RecPath {
	/* Provide a recording path with optional timestamp */
	classvar <>defaultPath;
	*new { | path, stamp = false |
		path = path ?? { this.getPath +/+ "SC" };
		if (stamp) { ^path ++ this.makeStamp }{ ^path };
	}
	
	*getPath { ^(defaultPath ?? { thisProcess.platform.recordingsDir }).absolutePath }
	
	*makeStamp { // from Server:prepareForRecord
		// temporary kludge to fix Date's brokenness on windows
		if(thisProcess.platform.name == \windows) {
			^"_" ++ Main.elapsedTime.round(0.01) ++ "." ++ Server.default.recHeaderFormat;

		}{
			^"_" ++ Date.localtime.stamp ++ "." ++ Server.default.recHeaderFormat;
		};
	}
	*stamp { | path | ^this.new(path, true); } // shortcut
	*showFolder {
		if ( File.exists( this.getPath ).not ) {
			^postf("Directory not found: %\n", this.getPath);
		};
		format("open '%'", this.getPath).postln.unixCmd;
	}
}

Record {
	var <>func, <>dur = 10, <>path, <>numchans = 1;
	var <isRunning = false;

	*new { | func, dur = 10, path, numchans = 1 |
		^this.newCopyArgs(func, dur, path, numchans).init;
	}
	
	init {
		{
			isRunning = true;
			if ( File.exists( path.dirname ).not ) { path.dirname.mkdir };
			Server.default.recChannels = numchans;
			Server.default.record(path);
			0.1.wait; // can we get to know when the server actually starts recording?
			func.value;
			dur.wait;
			Server.default.stopRecording;
		}.fork;
	}	
}
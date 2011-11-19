/* IZ 20111119 

Utility for configuring 'Community Core Vision.app' by changing entries in the config file: 'data/config.xml'. 
 
To start with, just set the path of the file for reading the video on which to perform blob detection. 

Usage: 

At the moment the only user methods are: 

ConfigCCV.setConfigPath; // choose the path of config.xml where the video path is stored for ccv
ConfigCCV.setVideoPath;		// choose the path of the video file to be used by ccv

Intermediate checking methods (for development) are: 
ConfigCCV.getVideoPath;


This opens a dialog file for selecting the path for the video file to be used by Community Core Vision.  If ConfigCCV does not know the location of the config file of Community Core Vision, it will first open another dialog to get the path of the config.xml file from the user. 

TODO:
Add method checkPaths; // ??? needed ???
// ConfigCCV.postPaths;

*/

ConfigCCV {
	classvar <configPath, <configString, <videoPath;
	
	*initClass {
		StartUp add: {
			this.loadConfig(Archive.global at: this.name.asSymbol);
		}	
	}

	*loadConfig { | path |
		var file, videoPathMatch;
		if (File.exists(path.asString).not) {
			postf("ConfigCCV warning!\nThe path:\n%", path).postln;
			postln("Evaluate the next line to select config.xml for ccv:");
			postln("\tConfigCCV.setConfigPath;");
			^false;
		};
		configPath = path;
		file = File(path, "r");
		configString = file.readAllString;
		file.close;
		videoPathMatch = configString
			.findRegexp("<VIDEO>[:space:]*<FILENAME>(.*)</FILENAME>[:space:]*</VIDEO>")[1];
		if (videoPathMatch.size == 2) {
			videoPath = videoPathMatch[1];
		}{
			configString.postln;
			"============================================================".postln;
			"The above configuration string contains no specification of a video path".postln;
		};
		^true;
	}

	*setVideoPath {
		if (this.loadConfig(configPath)) {
			Dialog.getPaths({ | paths | this prChangeVideoPath: paths.first });
		};
	}
	
	*prChangeVideoPath { | newPath |
		var file;
//		configString.postln;
		configString = configString.replace(videoPath.asString, newPath); // .postln;
		file = File(configPath, "w");
		file.write(configString);
		file.close;
	}
	
	*setConfigPath {
		Dialog.getPaths({ | paths |
			configPath = paths.first;
			this.loadConfig(configPath);
			Archive.global.put(this.name.asSymbol, configPath);
		});
	}
}


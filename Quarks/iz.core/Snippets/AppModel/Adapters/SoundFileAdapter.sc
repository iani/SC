/* iz Fri 21 September 2012 11:52 PM EEST
Adapter for interacting with SoundFileView in AppModel

Note: Interface to various actions will be added incrementally. 
*/

SoundFileAdapter {
	var <>container, <soundFile;
	
	*new { | container | ^this.newCopyArgs(container); }
	
	soundFile_ { | path, startFrame = 0, frames |
		soundFile !? { soundFile.close }; // close previous soundfile
		soundFile = SoundFile();
		if (File.exists(path)) {
			soundFile.openRead(path);
			container.changed(\read, this, startFrame, frames);
		}{
			"SOUND FILE NOT FOUND!:".postln;
			path.postln;
		}
	}

	updateMessage { ^\refresh }	
}
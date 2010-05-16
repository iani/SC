/* iz 080818

Holds the path of a folder + the names of the files contained in it, holding a set of picture files (jpg or other) that can be played back in various ways.  

*/

PictureFrame {
	var <folder_name;
	var <file_name;
	
	*new { | folder_name, file_name |
		^this.newCopyArgs(folder_name, file_name);
	}
	vvvv_string {
		// construct string for sending to vvvv! 
		^folder_name ++ "\\" ++ file_name; 
	}
	storeArgs { ^[folder_name, file_name] }
	printOn { | stream | this.storeOn(stream); }
}

PictureCollection {
	var <path;
	var <picture_file_names;
	var <folder_name;
	var <spec;	// for indexing by normalized numeric index in range 0-1;

	*new { | path, picture_file_names |
		^this.newCopyArgs(path, picture_file_names).init;
	}
	
	init {
		folder_name = path.asString.split($/).last;
		spec = ControlSpec(0, picture_file_names.size - 1, \linear, 1, 0);
	}
	
	atNormal { | frame_index = 0 |
		^PictureFrame(folder_name, picture_file_names[spec.map(frame_index)]); // .postln;
	}
	
}

ClipArray {
	var <clips;
	var <spec;	// for indexing by normalized numeric index in range 0-1;
	*fromDict { | dict |
		^this.newCopyArgs(
			dict.keys.asArray.sort collect: { | folder_name |
				PictureCollection(folder_name.asString, dict[folder_name] collect: _.value)
			}
		).init;
	}

	init {
		spec = ControlSpec(0, clips.size - 1, \linear, 1, 0);
	}
	
	atNormal { | clip_index = 0, frame_index = 0 |
//		thisMethod.report(clip_index, frame_index, spec, spec.map(clip_index), clips.size);
		^clips.at(spec.map(clip_index)).atNormal(frame_index);		
	}
}


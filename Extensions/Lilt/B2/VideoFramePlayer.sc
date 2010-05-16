/* iz Sunday; September 21, 2008: 4:36 PM

Class for video_play script. 

For implementation of video player driving the video_player patch in vvvv via OSC. 

Operates directly with the play and loop pins of FileStream. 
Replaces VideoFrameScroller because playback with positioning frame-by-frame does not work smoothly. 

*/
VideoFramePlayer : Model {
	var script, conductor, vvvv_address;
	var <paths = #[], filename;
	var duration_responder;
	var 	base_msg, play_msg, loop_msg, loop_start_msg, loop_end_msg,
		do_seek_msg, seek_position_msg, speed_msg, filename_msg;
	var <duration, <position;

	*new { | script, conductor, vvvv_address |
		^this.newCopyArgs(nil, script, conductor,
			vvvv_address = vvvv_address ?? { NetAddr("192.168.5.66", 9001) }
		).init;
	}
	init {
		this.makeMsgNames;
		duration_responder = OSCresponder(nil, "/" ++ script.name ++ "__", { | t, r, m |
			this.positionAndDuration_(*m[1..]);
		}).add;
		script onClose: { duration_responder.remove };
	}
	makeMsgNames {
		base_msg = "/" ++ script.name ++ "/";
		play_msg = base_msg ++ "play";
		loop_msg = base_msg ++ "loop";
		do_seek_msg = base_msg ++ "do_seek";
		seek_position_msg = base_msg ++ "seek_position";
		filename_msg = base_msg ++ "filename";
	}
	sendMsg { | ... msgAndArgs | vvvv_address.sendBundle(0, msgAndArgs) }
	positionAndDuration_ { | pos, dur |
		if (pos == position and: pos >= dur) {
			this.ended;
		};
		position = pos;
		this.duration = dur; 
	}
	ended { this.changed(\ended) }
	duration_ { | dur |
		thisMethod.report(dur);
		if (duration != dur) {
			duration = dur;
			this.changed(\duration, dur);
		};
	}
	play { this.sendMsg(play_msg, 1) }
	pause { this.sendMsg(play_msg, 0) }
	loopOn {this.sendMsg(loop_msg, 1)  }
	loopOff {this.sendMsg(loop_msg, 0)  }
	setLoop { | loop_start, loop_end |
		this.sendMsg(loop_start_msg, loop_start);
		{ this.sendMsg(loop_start_msg, loop_end); }.defer(0.1);
	}
	speed_ { | speed = 1 | this.sendMsg(speed_msg max: 0, 0)   }
	scroll { | pos |
		this.sendMsg(seek_position_msg, pos);
	}
	loadFile { | path |
		this.sendMsg(filename_msg, path);
	}
}


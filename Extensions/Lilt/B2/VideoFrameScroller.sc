/* iz Sunday; September 21, 2008: 4:36 PM

Class for video_scroll script. 

For implementation of video player driving the video_looper patch in vvvv via OSC. 
Operates with the Do Seek and Seek Position pins of FileStream to enable single frame movement as well as forward and backward looping. 

Uses classes Positioner, FramePlayerAction, ActivePlayer, PausedPlayer to encapsulate positioning and playing algorithms. 

Abandoned on Tuesday; September 23, 2008: 9:05 PM. 
Scrolling video with seek position frame by frame gets jerky after a while on a long video. 
Therefore going back to using native video playback of vvvv. (Native video playback cannot play backwards, so this is lost!). 

Work with the native video playback is now on: video_play.scd, VideoFramePlayer

Interface: 

Main pane lets one set: 
	- loop start
	- loop end (if loop end is smaller than loop start, then playback is in the opposite direction!)
	- speed (if speed is negative, then playback is in the opposite than the above!)
Conductor pane lets one set: 
	- Button run / pause: runs or pauses the single shot or loop process. 
	- Button loop is on / single shot is on: changes between loop mode and single shot mode

The next two act independently of whether the playback process is on or not.
If the playback process is on, then the position is constrained to loop start - loop end;

	- Button randomize pos: Jumps to a random position
	- slider scrub: scrubs to an arbitrary position selected by the slider 

*/

VideoFrameScroller {
	var script, vvvv_address;
	var player;		// encapsulates the current playing mode and its play mechanisms
	var positioner;	// encapsulates the mechanism for calculating position limits
	var <paths = #[], filename;
	var duration_responder;
	var base_msg, play_msg, loop_msg, do_seek_msg, seek_position_msg, filename_msg;

	*new { | script, framerate, vvvv_address |
		^this.newCopyArgs(script,
			vvvv_address = vvvv_address ?? { NetAddr("192.168.5.66", 9001) }
		).init(framerate);
	}
	init { | framerate |
		positioner = Positioner(framerate);
		player = FramePlayerAction.new;
		this.makeMsgNames;
		duration_responder = OSCresponder(nil, this.makeResponderMsgName, { | t, r, m |
			this.duration = m[1];
		}).add;
		script onStart: { this.initValues };
		script onClose: { duration_responder.remove };
		script.onSet(\loop_start, { | val | positioner.loop_start = val });
		script.onSet(\loop_end, { | val | positioner.loop_end = val });
		script.onSet(\speed, { | val | positioner.speed = val });
	}
	makeMsgNames {
		base_msg = "/" ++ script.name ++ "/";
		play_msg = base_msg ++ "play";
		loop_msg = base_msg ++ "loop";
		do_seek_msg = base_msg ++ "do_seek";
		seek_position_msg = base_msg ++ "seek_position";
		filename_msg = base_msg ++ "filename";
	}
	makeResponderMsgName {
		^"/" ++ script.name ++ "__"
	}	
	initValues {
		this.getDuration;
		positioner.loop_start = script.envir[\loop_start] ? 0;
		positioner.loop_end = script.envir[\loop_end] ? 1;
		positioner.speed = script.envir[\speed] ? 1;
	}
	getDuration {
		var get_duration_msg;
		get_duration_msg = base_msg ++ "get_duration";
		this.sendMsg(get_duration_msg, 1000.0.xrand2); // make sure it is different...
		{
			0.1.wait;
			this.sendMsg(play_msg, 0);
			0.1.wait;
			this.sendMsg(loop_msg, 0);
			0.1.wait;
			this.sendMsg(do_seek_msg, 1);
		}.fork;
	}
	sendMsg { | ... msgAndArgs | vvvv_address.sendBundle(0, msgAndArgs) }
	duration_ { | dur | positioner.dur = dur;
		thisMethod.report(dur);
		this.changed(\duration, dur);
	}
	duration { ^positioner.dur }
	run {
		// start playing with the player, in loop mode or single-shot mode
		player.run(this);
		this.changed(\started);
		thisMethod.report;
	}
	pause {
		player.pause;
		this.changed(\paused);
		thisMethod.report;
	}
	loop_ { | loopP = true | player.loop = loopP }
	speed_ { | speed = 1 | positioner.speed = speed }
	randomize_position { player.random(this) }
	scroll { | pos | player.scroll(this, pos) }
	// play is called by the player when running or looping
	play { player.play(this) }
	isInBounds { ^positioner.isInBounds }
	jumpToOtherEnd {
		vvvv_address.sendBundle(0, [seek_position_msg, positioner.jumpToOtherEnd])
	}
	goRandomFull { this.sendFrame(positioner.goRandomFull) }
	goRandomInMarkers { this.sendFrame(positioner.goRandomInMarkers) }
	scrollConstrainedFull { | pos |
		this.sendFrame(positioner.scrollConstrainedFull(pos))
	}
	scrollConstrainedInMarkers { | pos |
		this.sendFrame(positioner.scrollConstrainedInMarkers(pos))
	}
	sendFrameAndSchedule {
		this.sendFrame(positioner.position);
		positioner.advanceAndSchedule(this);
	}
	sendFrame { | pos | thisMethod.report(pos);
		vvvv_address.sendBundle(0, [seek_position_msg, pos]);
//		vvvv_address.sendBundle(0, [do_seek_msg, 1]);
		 }
	resetPosition { positioner.resetPosition }
	loadPathsDialog {
		GUI.dialog.getPaths({ | path | this.loadPaths(path.first) });
	}
	loadPaths { | path |
		paths = path.load;
		this.changed(\paths, paths);
	}
	selectFile { | index = 0 |
		var path;
		path = paths[index];
		if (path.notNil) {
//			thisMethod.report(index, "SENDING", path);
			this.sendMsg(filename_msg.postln, path);
//			{ this.getDuration; }.defer(0.1); // do not need to send this - vvvv updates anyway
		};
	}
}

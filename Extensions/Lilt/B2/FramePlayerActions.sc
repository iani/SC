/* iz Monday; September 22, 2008: 8:17 PM
Help classes for VideoFrameScroller
*/

// encapsulates the mechanism for calculating position limits: 
Positioner {
	var <framerate = 0.04, <speed = 1;
	var <position = 0;
	var <loop_start = 0, <loop_end = 1, <min = 0, <max = 1, <dur = 1, <direction = 1;
	var <increment;
	*new { | framePlayer, framerate | ^this.newCopyArgs(framerate ?? { 25.reciprocal}) }
	loop_start_ { | pos |
		loop_start = position;
		this.recalc;
	}
	loop_end_ { | pos |
//		thisMethod.report("before pos", pos, "dur", dur, "loop_end", loop_end);
		loop_end = pos;
//		thisMethod.report("after pos", pos, "dur", dur, "loop_end", loop_end);
		this.recalc;
//		thisMethod.report("after after pos", pos, "dur", dur, "loop_end", loop_end);
	}
	dur_ { | pos | dur = pos; this.recalc }
	speed_ { | speed_ | speed = speed_; this.recalc }
	framerate_ { | framerate_ | framerate = framerate_; this.recalc }
	recalc {
//		thisMethod.report("BEFORE loop start", loop_start, "loop end", loop_end);
		#loop_start, loop_end = [loop_start, loop_end] min: dur;
//		thisMethod.report("AFTER: loop start", loop_start, "loop end", loop_end);
//		thisMethod.report("BEFORE min", min, "max", max);
		if (loop_start < loop_end) {
			#min, max = [loop_start, loop_end] * dur;
			direction = 1;
		}{
			#max, min = [loop_start, loop_end] * dur;
			direction = -1;			
		};
//		thisMethod.report("AFTER min", min, "max", max);
		increment = framerate * speed * direction;
		thisMethod.report(format(
			"dur: %, framerate: %, speed: %, min: %, max: %, direction: %, increment: %\n",
			dur, framerate, speed, min, max, direction, increment));
	}
	isInBounds {
//		thisMethod.report("min", min, "max", max, "pos", position,
//			position >= min and: { position <= max }
//		);
		^position >= min and: { position <= max }
	}
	resetPosition { | doNotResetIfInBounds = true |
		if (doNotResetIfInBounds) {
			if (position > 0 and: { position < dur }) { ^this };
		};
		if (direction > 0) {
			position = 0
		} {
			position = dur
		}
	}
	jumpToOtherEnd {
//		thisMethod.report("position", position, "min", min, "max", max);
		if (position < min) { position = max } { position = min };
//		thisMethod.report("position after the jump", position, "min", min, "max", max);
	}
	goRandomFull { position = dur.rand }
	goRandomInMarkers { position = min rrand: max }
	scrollConstrainedFull { | argPos | ^position = argPos.clip(0, dur) }
	scrollConstrainedInMarkers { | argPos | ^position = argPos.clip(min, max) }
	advanceAndSchedule { | scroller |
		{
		thisMethod.report(format("dur: %, framerate: %, speed: %, direction: %, increment: %\n",
			dur, framerate, speed, direction, increment, position));
			position = position + increment;
			scroller.play;
		}.defer(framerate);
	}
}

/* Following classes implement the playback functionality of the VideoFrameScroller: */

FramePlayerAction {
	var action, runAction;
	*new { ^super.new.init }
	init {
		runAction = ActivePlayer.new;
		action = PausedPlayer;
	}
	// ------------------------- action config: 
	run { | scroller |
		action = runAction;
		action.resetPosition(scroller);
		this.play(scroller);
	}
	pause { action = PausedPlayer }
	loop_ { | loopP = false | runAction.loop = loopP }
	// ------------------------- playing and scrolling: 
	play { | scroller | action.play(scroller) }
	random { | scroller | action.play(scroller) }
	scroll { | scroller, position | action.scroll(scroller, position) }
}

ActivePlayer {
	var playbackAction;
	// ------------------------- action config: 
	loop_ { | loopP = false |
		if (loopP) { playbackAction = LoopingPlayer }
		{ playbackAction = OneShotPlayer }
	}
	resetPosition { | scroller |
		playbackAction.resetPosition(scroller)
	}
	// ------------------------- playing and scrolling: 
	play { | scroller | playbackAction.play(scroller) }
	random { | scroller | scroller.goRandomInMarkers }
	scroll { | scroller, position | scroller.scrollConstrainedInMarkers(position) }
}

OneShotPlayer {
	*play { | scroller |
		if(scroller.isInBounds) { scroller.sendFrameAndSchedule }
		{ scroller.changed(\paused) }
	}
	*resetPosition { | scroller | scroller.resetPosition }
}

LoopingPlayer {
	*play { | scroller |
		if(scroller.isInBounds) {
			scroller.sendFrameAndSchedule;		
		}{
			scroller.jumpToOtherEnd;
			scroller.sendFrameAndSchedule;
		}	
	}
	*resetPosition { }
}

PausedPlayer {
	*play { /* do not send or schedule since you are paused */ }
	// but do respond to random and scroll buttons: 
	*random { | scroller | scroller.goRandomFull }
	*scroll { | scroller, position |
		scroller.scrollConstrainedFull(position);
	}
}

Frames : List {

	add { | frameInfo |
		// store the frame info together with a time stamp
		^super add: [Process.elapsedTime, frameInfo];
	}
	
	timerange { | earliest = 0, latest = 1 |
		var index, latestIndex;
		index = this.size - 1;
		while {
			this[index][0] > latest and: { index >= 0 }
		}{
			index = index -1
		};
		latestIndex = index;
		while {			
			this[index][0] > earliest and: { index >= 0 }
		}{
			index = index - 1;
		};
		
		^this[index + 1 .. latestIndex];
	}
	
	timerangeDo { | func, earliest = 1, latest |
		var index, frame;
		index = this.size - 1;
		latest = latest ?? { this.last[0] };
		while {
			this[index][0] > latest and: { index >= 0 }
		}{
			index = index -1
		};
		while {
			frame = this[index];
			frame[0] > earliest and: { index >= 0 }
		}{
			func.(frame, index, earliest, latest, this);
			index = index - 1;
		};
	}
	
	plot { | func, view, earliest = 0, latest |
		// view can be a view or an Image
		this.timerangeDo({ | frame, index, earliest, latest, framelist |
			func.(view, frame, index, earliest, latest, framelist);
		}, earliest, latest);
	}
	
}


/* 

This is the first version. But one not need to push in reverse order because one is accessing the values by index anyway, using while

*/
ReverseFrames : List {
	
	addNow {
		this.addFirst(Process.elapsedTime);	
	}

	displayable { | earliest = 0, latest = 1 |
		^[
			this.indexOf(this detect: { | e | e <= earliest }) - 1,
			this.indexOf(this detect: { | e | e <= latest })
		]
	}
	
	rangeDo { | greatest = 1, smallest = 0 |
		var index = 0;
		while {
			this[index.post] > greatest;
		}{
			postf(": % was after the latest frame: % therefore doing nothing\n", this[index], greatest);
			index = index + 1;
		};
		while {
			this[index.post] > smallest;
		}{
			postf(": % was after the earliest frame %, therefore doing something\n", this[index], smallest);
			index = index + 1;			
		};
		"THE END".postln;
	}
}

/*
f
f.displayable(141034);
*/
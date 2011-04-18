/*

Record some data frames, timestamping each added frame.
Then select the data frames that were recorded within a given time interval and plot them with a given function.

*/

Frames : List {

	add { | frameInfo |
		// store the frame info together with a time stamp
		^super add: [Process.elapsedTime, frameInfo];
	}

	plot { | func, view, earliest = 0, latest |
		// view can be a view or an Image
		this.timerangeDo({ | frame, index, earliest, latest, framelist |
			func.(view, frame, index, earliest, latest, framelist);
		}, earliest, latest);
	}

	// iterate a function over the frames that lie within a given time range
	timerangeDo { | func, earliest = 1, latest |
		var index, frame;
		index = this.size - 1;
		latest = latest ?? { this.last[0] };
		while {
			index >= 0 and: { this[index][0] > latest } 
		}{
			index = index -1
		};
		while {
			frame = this[index];
			index >= 0 and: { frame[0] > earliest }
		}{
			func.(frame, index, earliest, latest, this);
			index = index - 1;
		};
	}	

	//  return the frames within a given time range
	timerange { | earliest = 0, latest |
		var index, latestIndex;
		index = this.size - 1;
		latest = latest ?? { this.last[0] };
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

}

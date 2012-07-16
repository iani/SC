/* IZ 2012 07 16 

A History that plays in its own ProxySpace. This makes it possible to play copies of the same History in parallel (concurrently) while keeping each copy in its own ProxySpace.

*/

ParallelHistory : History {

	var proxySpace;	

	*play {
		// clone a copy from current History and play it in its own ProxySpace;
		^this.new(this.current.lines).play;
	}

	init { | lines |
		super.init(lines);
		this.initPlayer;
	}
	
	initPlayer {
		
		player = TaskProxy.new.source_({ |e|
			var linesSize, lineIndices, lastTimePlayed;
			linesSize = lines.size;
			proxySpace = ProxySpace.new;
			if (linesSize > 0) {	// reverse indexing
				lineIndices = (e.endLine.min(linesSize) .. e.startLine.min(linesSize));

				lineIndices.do { |index|
					var time, id, code, waittime;
					#time, id, code = lines[index];

					waittime = time - (lastTimePlayed ? time);
					lastTimePlayed = time;
					waittime.wait;
					if (e.verbose) { code.postln };
					proxySpace.push;
					code.compile.value;	// so it does not change cmdLine.
					proxySpace.pop;
				};
			};
			0.5.wait;
			"history is over.".postln;
		}).set(\startLine, 0, \endLine, 0);
	}
	

/*	// Debugging
	play { |start=0, end, verbose=true|	// line numbers;
									// starting from past 0 may not work.
		postf("start: %, end: %\n", start, end);
		lines.
		start = start.clip(0, lines.lastIndex);
		end = (end ? lines.lastIndex).clip(0, lines.lastIndex);

		player.set(\startLine, start, \endLine, end, \verbose, verbose);
		player.play;
	}
*/
}
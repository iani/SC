/* IZ 2012 07 16 


A History that plays in its own ProxySpace. This makes it possible to play copies of the same History in parallel (concurrently) while keeping each copy in its own ProxySpace.


ParallelHistory.play(optional: proxySpace, history);

Make a copy of the history's lines, and play it in its own proxySpace. 
arguments: 
	proxySpzce If proxySpace argument is provided, then it is used, otherwise new ProxySpace is made
	history: if provided, its lines are used, otherwise History.current is used. 

( // problem??????????????????
{
	0.1.wait; // removing this may result in skipping first ParallelHistory?
	ParallelHistory.play(p);
	0.5.wait;
	ParallelHistory.play(q);
}.fork
)

=============== Example ================

// Construct a history to play back:
(
History.current = History.new;
h = History.current;

h.addLine(0, \me, "~out = { SinOsc.ar(~freq.kr, 0, LFPulse.kr(~rate.kr, 0.2)) * 0.1 };");
h.addLine(0.1, \me, "~out.play;");
h.addLine(2, \me, "~out.play;");
h.addLine(2.4, \me, "~out.stop;");
h.addLine(4.4, \me, "~out.play;");
h.addLine(4.9, \me, "~out.stop;");
h.addLine(8, \me, "~out.play;");
h.addLine(10.5, \me, "~out.stop;");
)
// Construct two different proxy spaces to play the history in: 
(
p = ProxySpace.new;
p[\freq] = NodeProxy.new.source = 500;
p[\rate] = NodeProxy.new.source = 5;

q = ProxySpace.new;
q[\freq] = 550;
q[\rate] = 5.5;
)
// Play two copies of History in parallel, using the different proxy spaces above:
(
{
	1.wait;
	ParallelHistory.play(p);
	3.5.wait;
	ParallelHistory.play(q);
}.fork
)

(
{
	ParallelHistory.play(p);
	3.5.wait;
	ParallelHistory.play(q);
}.fork
)

( // problem??????????????????
{
	ParallelHistory.play(p);
	0.5.wait;
	ParallelHistory.play(q);
}.fork
)

*/

ParallelHistory : History {

	var <>proxySpace;	

	*play { | proxySpace, history |
		// clone a copy from provided or current History and play it in its own ProxySpace;
		^this.new((history ?? { this.current }).lines)
			.proxySpace_(proxySpace ?? { ProxySpace.new })
			.play;
	}
	
	init { | inLines |
		super.init(inLines);
		this.initPlayer;	
	}

	initPlayer {

		player = TaskProxy.new.source_({ | e |
			var linesSize, lineIndices, lastTimePlayed;
			linesSize = lines.size;
			if (linesSize > 0) {	// reverse indexing
				lineIndices = (e.endLine.min(linesSize) .. e.startLine.min(linesSize));

				lineIndices.do { | index |
					var time, id, code, waittime;
					#time, id, code = lines[index];

					waittime = time - (lastTimePlayed ? time);
					lastTimePlayed = time;
					waittime.wait;
					if (e.verbose) { code.postln };
					proxySpace.push;
					code.compile.value; // so it does not change cmdLine.
					proxySpace.pop;
				};
			};
			0.5.wait;
			"history is over.".postln;
		}).set(\startLine, 0, \endLine, 0);
	}
}
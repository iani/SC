//:
  
\default.play;


//:-
a = FFTpollSynth.new.addListener(\test, \synthStarted, { 
	"STARTED".postln;
	\test.play({ WhiteNoise.ar(0.01) });
});

//:-
Server.default.queryAllNodes;


\asdf onClose: { "closed".postln };

\asdf.objectClosed;

UniquePlay

\default.play;

a.free;

UniqueSynth(\default);
UniquePlay({ WhiteNoise.ar(0.1) });
\xxx.play({ WhiteNoise.ar(0.1) });

ServerPrep.new.addAction({ { WhiteNoise.ar(0.1) }.play });

UniqueSynth
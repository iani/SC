/* 
Broadcasting the whole fibonacci structure to everyone over the network. 

Stasis.addHost; // do this if you want to send to localhost as well
// or do it directly in the start message: 
Stasis.start(local: true);



l = Fib.ascending(15).flat.size;		// The length of a Fibonacci tree of depth 15 is 1597
(l / 1.30) / 60; // The duration is 20.474358974359 minutes


l = Fib.ascending(17).flat.size;		// The length of a Fibonacci tree of depth 17 is 4181
(l / 3.5) / 60; // The duration is 20.474358974359 minutes

(
d = FibDisplay.new;
d.show;

t = { c = TempoClock(4); };
a = SyncSender(clockFunc: t);
a.pattern = Pfib(Fib.ascending(17)).asPbind(a);
a.start;
)
(
{ 
	loop {	
		{ 0.5 * WhiteNoise.ar(EnvGen.kr(Env.perc(0.1, 0.4), doneAction: 2)) }.play;
		3.5.reciprocal.wait;
	}
}.fork
)
Stasi_Aris.new
Stasis.addHost; // do this if you want to send to localhost as well
Stasis.start;
Stasis.start("BB") // do this if you want to start from a specific part in the piece;
// 1. Kafeneio: "AAA"
~showPiece.(~descending, ["AAAAAA", "AAAAAB", "AAAAB", "AAABA", "AAABB"]);

// 2. Eisodos: "AAB"
~showPiece.(~descending, ["AABAAA", "AABAAB", "AABAB", "AABBA", "AABBB"]);

// 3. Choros: "AB"
~showPiece.(~descending, ["ABAAA", "ABAAB", "ABAB", "ABBA", "ABBB"]);

// 4. Syngrousi: "BA"
~showPiece.(~descending, ["BAAAA", "BAAAB", "BAAB", "BABA", "BABB"]);

// 5. Epistrofi: "BB"
~showPiece.(~descending, ["BBAAA", "BBAAB", "BBAB", "BBBA", "BBBB"]);


SyncAction("s_", { 

*/



Stasis {
	classvar <>receivers;
	classvar <sender;
	/* 	l = Fib.ascending(17).flat.size;		// The length of a Fibonacci tree of depth 15 is 1597
		(l / 3.5) / 60; // The duration is 20.474358974359 minutes
	*/
	classvar <levels = 17; /* 
		l = Fib.ascending(17).flat.size;		
		// The length of a Fibonacci tree of depth 15 is 1597 */
	classvar <numBeats;
	classvar <tempo = 3.5; /*
		(l / 3.5) / 60; // The duration of the piece at 3.5 beats per second is 20.474358974359 minutes
		*/
	classvar <ascendingFib;
	classvar <descendingFib;
	classvar <pattern;
	classvar <conductStream;	// the stream that plays the fibonacci tree process
//	classvar <tempo = 1.3308;
	classvar <scLangPort = 57120;

	*init {
	// Initialize all receivers
		receivers = (
			iani: NetAddr("192.168.1.10", scLangPort),
			graphics: NetAddr("192.168.1.10", scLangPort),			manolis: NetAddr("192.168.1.12", scLangPort), 
			aris: NetAddr("192.168.1.13", scLangPort), 
			arisOf: NetAddr("192.168.1.10", 12345), 
			arisLocalhost: NetAddr("127.0.0.1", 12345),
			dakis: NetAddr("192.168.1.14", scLangPort), 
			omer: NetAddr("192.168.1.15", scLangPort)
			
		);
	}

	*addHost { | name = 'local', netAddr |
		this.init;
		if (name == 'local') { netAddr = NetAddr.localAddr };
		receivers[name] = netAddr;
	}
	*start { | startPhrase, local = false |	// phrase to start from
		if (sender.notNil) { this.stop };
		if (receivers.isNil) { this.init };
		if (local) { this.addHost };
		this.makeSender(startPhrase);
		sender.start;
	}
	
	*makeSender { |startPhrase |
		sender = SyncSender(nil, receivers.values.asArray, { TempoClock(tempo) });
		pattern = Pfib(Fib.descending(levels), startPhrase).asPbind(sender, "l");
		sender.pattern = pattern;
	}

	*stop {
		sender.stop;
		sender = nil;
	}
	
	*tempo_ { | tempo |
		sender.clock.tempo = tempo;
	}

}

/* 

~ascending = Fib.ascending(17);
~descending = Fib.descending(17);

~showBranchSize = { | fib, label = "A", tempo = 3.5 |
	var branch;
	label = List.new.addAll(label) collect: { | char | char.ascii - 65 };	branch = fib;
	label do: { | index | 
		branch = branch[index]
	};
	[branch.flat.size, branch.flat.size / tempo / 60];
};

~showPiece = { | fib, labels |
	var beats, duration;
 	labels do: { | label | 
		#beats, duration = ~showBranchSize.(fib, label);
	format("% : beats: % duration: %", label, beats, duration).postln;
	};
};


~showPiece.(~descending, ["AAA", "AAB", "AB", "BA", "BB"]);

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



======= Note: earlier subdivision tries: 

// 1. Kafeneio: "AAA"
~showPiece.(~descending, ["AAAAA", "AAAAB", "AAAB"]);
~showPiece.(~descending, ["AAAAA", "AAAAB", "AAABA", "AAABB"]);
~showPiece.(~descending, ["AAAAAA", "AAAAAB", "AAAAB", "AAABA", "AAABB"]);


*/


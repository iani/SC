/*
a = Plily("<c8 d e f16> g a b c'3|32 b a g e d c");
b = a.asStream;
b.next;

~  				tie
(  ) 			slur
\(		\)		phrasing slur
\pp, etc			dynamics
\<		\!		crescendo
\>		\!		decrescemdp

-^+>-|._			articulations

dynamic state
articulation state

phrase 
dynamic phrase
*/
Plily : FilterPattern {
	classvar <>noteNames, <>relativeIntervals;
	var <>relativeMode = false;
	
	*initClass {
		noteNames =  ($c: 0, $d: 2, $e: 4, $f: 5, $g: 7, $a: 9, $b: 11, $r: \r);
		relativeIntervals = [0,1,2,3,4,5,6,-5,-4,-3,-2,-1];
	}
	
	
	pattern_ { | string |
		if (string.isKindOf(String) ) {
			pattern = Proutine({ 			// streams whitespace separated clumps of Chars
				var word ="";
				string.do { | char | 
					if ( char.ascii < 33) { 
						if(word.size >0) { word.yield; word = "";  };
					} { 
						word = word ++ char
					} 
				};
				if (word.size != 0) { word.yield }
			});
		} {
			pattern = string
		};
	}

	embedInStream { | event |
		var stream, word, notes;
		var curDur = Ref(1/4), curNote = Ref(0);
		var durAdjust = 1;
		stream = pattern.asStream;
		loop {
			word = stream.next(event) ?? {^event};
			if (word[0] != $<) { 
				event = [this.parseNote(word, curDur, curNote), curDur.value].yield 
			} {
				word = word[1..];
				notes = [ this.parseNote(word, curDur, curNote) ];
				while {
					word = stream.next(event) ?? {^event};
					notes = notes.add(this.parseNote(word, curDur, curNote) );
					word.last != $>
				};
				event = [notes, curDur.value].yield;   // patterns support only one duration for a chord
			}
		}
	}
				
	parseNote { | word, curDur, curNote |
		var note, num, den, curN, curOct, char, endBraceDetected = false;
		var durAdjust = 1;
		note = noteNames[word[0]];			// find pitch Class value
		
		// implement relative pitch
		if (relativeMode) {
			curN = curNote.value.mod(12);		// find current pitch Class
			curOct = curNote.value.trunc(12);	// find currentOctave
			curNote.value = note = relativeIntervals[(note - curN).mod(12)] + curN + curOct;
		};
		word[1..].do { | char |
					case 
					{ char == $> }		{ endBraceDetected = true }
					{ char == $| }		{ num = den; den = [] }
					{ char == $s } 		{ note = note + 1}
					{ char == $f } 		{ note = note - 1}
					{ char == $, } 		{ note = note - 12}
					{ char == $' } 		{ note = note + 12}
					{ char.isDecDigit} 	{ den = den.add(char.digit) }
					{ char == $. }		{ durAdjust = durAdjust * 1.5 }
		};
		if (den.notNil) {
				if (num.isNil) { num = 1 } { num = num.reverse.collect({ | n, i | 10 ** i * n }).sum };
				den = den.reverse.collect({ | n, i | 10 ** i * n }).sum; 
				curDur.value = durAdjust * num/den;
		};
		if (endBraceDetected) {word[word.size - 1] = $> };
		^note;
	}
}

	
/*

	Pbind(*[
		#[note, dur], Plily("c8 d e f8. g a b c4. b a g e d <c e g c e g c e g>")
	]).play
	
	Pbind(*[
		#[note, dur], Plily("c8 f d g e a f b g c a d b e c f").relativeMode_(true)
	]).play
	
To do:
	~ for ties
	,\ff  dynamics
	,\p
	,\mp
	,\mf
	,\times fraction  for tuplets
	\ key signature, from root?
	handling non monophonic textures
	
	articulations:
     [ \accent             ,\marcato            ,\staccatissimo 	,\espressivo
        ,\staccato           ,\tenuto             ,\portato
        ,\upbow              ,\downbow            ,\flageolet
        ,\thumb              ^,\lheel             ,\rheel
        ^,\ltoe              ,\rtoe               ,\open
        ,\stopped            ,\turn               ,\reverseturn
        ,\trill              ,\prall              ,\mordent
        ,\prallprall         ,\prallmordent       ,\upprall
        ,\downprall          ,\upmordent          ,\downmordent
        ,\pralldown          ,\prallup            ,\lineprall
        ,\signumcongruentiae ,\shortfermata       ,\fermata
        ,\longfermata        ,\verylongfermata    ,\segno
        ,\coda               ,\varcoda]
 
 
 */

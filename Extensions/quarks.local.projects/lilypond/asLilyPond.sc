
+ Symbol {

	lilyPond { ^this.asString }

	lpmidi {
		var str, note, rest, alteration, octave = 0;
		str = this.asString;
		note = Lilypond.notes.indexOf(str[0]);
		rest = str[1..];
		alteration = rest.findRegexp("[:alpha:]+");
		if (alteration.size > 0) {
			alteration = Lilypond.alterations.at(alteration[0][1].asSymbol);		}{
			alteration = 0;
		};
		octave = octave + rest.findRegexp("[']+").first.asArray.last.size;
		octave = octave - rest.findRegexp("[,]+").first.asArray.last.size;
		^octave * 12 + 48 + note + alteration;
	}

	lpcps { ^this.lpmidi.midicps }
}

+ Integer {
	lilyPond { 
		^[\c, \cis, \d, \es, \e, \f, \fis, \g, \as, \a, \bes, \b].wrapAt(this) 
		++
		[",,,,", ",,,", ",,", ",,", "", "'", "''", "'''", "''''", "'''''", "''''''"]
			.at((this / 12).floor.asInteger)
	}

	
}

+ Float {
	lilyPond {
		^this.cpsmidi.round.asInteger.lilyPond;
	}
}
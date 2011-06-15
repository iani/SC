
+ Symbol {
	lilyPond { ^this.asString }
}

+ Integer {
	lilyPond { 
		^[\c, \cis, \d, \es, \e, \f, \fis, \g, \as, \a, \bes, \b].wrapAt(this) 
		++
		[",,,,", ",,,", ",,", ",,", "", "'", "''", "'''", "''''", "'''''"]
			.at((this / 12).floor.asInteger)
	}

	
}

+ Float {
	lilyPond {
		^this.midicps.round.asInteger.lilyPond;
	}
}
KeyCode {
	classvar keys;
	*initClass { keys = this.getkeyCodes }
	*new{|sym| ^keys[sym] }
	*getkeyCodes {
		^(
			d: 2,
			f: 3,
			h: 4,
			p: 35,
			z: 16,
			
			\0: 29,
			\1: 18,
			\2: 19,
			\3: 20,
			\4: 21,
			\5: 23,
			\6: 22,
			\7: 26,
			\8: 28,
			\9: 25
		)
	}
}

KC : KeyCode { }
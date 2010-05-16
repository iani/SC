/* (IZ 2005-08-23) {
} */

+ MIDIResponder {
	install {
		/* { install: Macro for safe adding of MIDIResponders.
			Remove responder before adding it and thus prevent adding 
		 	the same responder multiple times to the listening list. 
		} */
		this.remove;	// remove myself just in case I already registered
		this.class.add(this); // add again
	}
	add {	// synonym to install. added Monday, April 14, 2008 for compatibility
			// with OSCresponder when used by Snippet
			this.install;
	}
}

/* {
	

+ MIDIResponder {
	install {
		this.class.add(this);
		this.removeDuplicates;
	}
}

+ NoteOnResponder {
	removeDuplicates { nonr = nonr.asSet.asArray }
}

+ NoteOffResponder {
	removeDuplicates { noffr = noffr.asSet.asArray }
}

+ CCResponder {
	removeDuplicates {
		ccr = ccr.asSet.asArray;
		ccnumr.do {|r, i| if (r.notNil) { ccnumr[i] = r.asSet.asArray }}
	}
}

+ TouchResponder {
	removeDuplicates { touchr = touchr.asSet.asArray }
}

+ BendResponder {
	removeDuplicates { bendr = bendr.asSet.asArray }
}

} */
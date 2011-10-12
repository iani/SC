RCTuning : Tuning {

	// var <tuning, <octaveRatio, <>name in superclass Tuning
	var <isR, <isCyclic; // these vars are kept consistent
	
	
	*new { | tuning, octaveRatio = 2.0, name = "Unknown Tuning", isR=false, isCyclic=true |
		^super.newCopyArgs(tuning, octaveRatio, name, isR, isCyclic).init;
	}
/*
	*newFromKey { | key |
		^TuningInfo.at(key)
	}

	*default { | pitchesPerOctave |
		^this.et(pitchesPerOctave);
	}

	*et { |pitchesPerOctave = 12 |
		^this.new(this.calcET(pitchesPerOctave), 2.0, this.etName(pitchesPerOctave));
	}

	*calcET { | pitchesPerOctave |
		^(0..(pitchesPerOctave - 1)) * (12/pitchesPerOctave)
	}

	*etName { |pitchesPerOctave|
		^"ET" ++ pitchesPerOctave.asString
	}

	*choose { |size = 12|
		^TuningInfo.choose({ |x| x.size == size })
	}
*/
	tuning{ ^this.semitones } // compatibility to Tuning
	asTuning {
		var semitones = this.semitones;
		if (isCyclic) {
			^Tuning.new(semitones, octaveRatio, name)
		}{
			if (isR && ( octaveRatio == tuning.last )) { semitones.pop };
			if (isR.not && ( octaveRatio == tuning.last.centratio )) { semitones.pop };
			if (octaveRatio < 1) {
				^Tuning.new(semitones.reverse, 1 / octaveRatio, name)
			}{
				^Tuning.new(semitones, octaveRatio, name)
			}
		}
	}
	
	init{ name = name.asSymbol }
	name_{|inName| name = inName.asSymbol }
	isRational { ^isR }
	isRational_{|inRational|   
		if (inRational != isR) { 
			isR = inRational;
			if (isR) {
				tuning = tuning.collect{|step| step.centratio.asRatio}
			}{
				tuning = tuning.collect{|step| step.ratiocent }
			}
		}
	}

	asRCTuning {|inCyclic| 
		if (inCyclic.isNil) { ^this } {this.isCyclic_(inCyclic) } 
	}
	isCyclic_{|inCyclic|
		if ( inCyclic.notNil && (inCyclic != isCyclic) ) {
			if (isCyclic) {	// convert cyclic to non-cyclic  
				if (isR) { 
					tuning.add( tuning.first * octaveRatio.asRatio) 
				}{
					tuning.add( tuning.first + octaveRatio.ratiocent )
				}
			}{				
				if (isR) { // convert non-cyclic to cyclic
					if (octaveRatio == (tuning.last / tuning.first)) { tuning.pop }
				}{
					if (octaveRatio.ratiocent == (tuning.last - tuning.first)) { tuning.pop }
				}
			};
			isCyclic = inCyclic
		}
	}
// these are actually indices NOT degrees ?!!!
	getiR{|degrees| //get intervals as Rationals from list of degrees
		^case 
		{isCyclic && isR} { this.prGetiRc(degrees) }
		{isCyclic && isR.not} { this.prGetiCc(degrees).collect{|i| i !? {i.centratio.asRatio} } }
		{isCyclic.not && isR} { this.prGetiRnc(degrees) }
		{ this.prGetiCnc(degrees).collect{|i| i !? {i.centratio.asRatio} } }
	}
// getiC -> these are midinotes !!
	getiC{|degrees| //get intervals as cents from list of degrees
		^case 
		{isCyclic && isR} { this.prGetiRc(degrees).collect{|i| i !? {i.ratiocent} } }
		{isCyclic && isR.not} { this.prGetiCc(degrees) }
		{isCyclic.not && isR} { this.prGetiRnc(degrees).collect{|i| i !? {i.ratiocent} } }
		{ this.prGetiCnc(degrees) }
	}
// geti wrong output
	geti{|degrees| //get intervals as midinotes from list of degrees
		^this.getiC(degrees).collect{|i| i !? {i/100} }
	}
			/*
			SYS.testSystema.tuning.getiR([2, 44, 0, 1, 2, -3, 45])
			SYS.testSystema.tuning.getiC([2, 44, 0, 1, 2, -3, 45])
			SYS.ionian.tuning.getiR([0,0,3,7,0,12,0, 24, -12])
			SYS.ionian.tuning.getiC([0,0,3,7,0,12,0, 24, -12])
			*/
	prGetiRc {|degrees| 
		var res=List.new; 
			degrees.doAdjacentPairs{|a, b|
				try { res.add(this.prDegToR(b) / this.prDegToR(a) ) }{ res.add(nil) } };�
		^res
	}
	prGetiCc{|degrees| 
		var res=List.new;
			degrees.doAdjacentPairs{|a, b| 
				try {  res.add(this.prDegToC(b) - this.prDegToC(a) ) }{ res.add(nil) } }
		^res
	}
	prGetiRnc {|degrees| 
		var res=List.new;
			degrees.doAdjacentPairs{|a, b| 
				try { res.add(tuning[b] / tuning[a]) }{ res.add(nil) } };
		^res
	}
	prGetiCnc {|degrees| 
		var res=List.new; 
			degrees.doAdjacentPairs{|a, b| 
				try { res.add(tuning[b] - tuning[a]) }{ res.add(nil) } };
		^res
	}
	
	degToR{|deg|
		^case 
		{deg.isNil} { nil }
		{isCyclic && isR} { this.prDegToR(deg) }
		{isCyclic && isR.not} { this.prDegToC(deg).centratio.asRatio}
		{isCyclic.not && isR} { try { tuning.at(deg) } {nil} }
		{ try {tuning.at(deg).centratio.asRatio} {nil} }
	}
	degToC{|deg|
		^case
		{deg.isNil} { nil }
		{isCyclic && isR} { this.prDegToR(deg).ratiocent }
		{isCyclic && isR.not} { this.prDegToC(deg) }
		{isCyclic.not && isR} {  try { tuning.at(deg).ratiocent } {nil} }
		{ try { tuning.at(deg) } {nil} }
	}
	prDegToR {|deg| ^((octaveRatio ** (deg div: tuning.size)) * tuning.wrapAt(deg)).asRatio }
	prDegToC {|deg| ^(octaveRatio.ratiocent * (deg div: tuning.size)) + tuning.wrapAt(deg) }
	
	ambitR { ^case
	 	{isCyclic && isR} { octaveRatio / tuning.first }
	 	{isCyclic && isR.not} { octaveRatio / tuning.first.centratio.asRatio }
	 	{isCyclic.not && isR } { tuning.last / tuning.first }
	 	{ (tuning.last - tuning.first).centratio.asRatio }
	}
	ambitC { ^case
		{isCyclic && isR} { (octaveRatio / tuning.first).ratiocent }
	 	{isCyclic && isR.not} { octaveRatio.ratiocent - tuning.first }
	 	{isCyclic.not && isR } { (tuning.last / tuning.first).ratiocent }
	 	{ tuning.last - tuning.first }
	}
	
	
	ratios {
		if (isR) { ^tuning.deepCopy } { ^tuning.centratio.asRatio }
	}
	semitones {
		if (isR) { ^tuning.ratiomidi } { ^tuning / 100 }
	}

	cents {
		if (isR) { ^tuning.ratiocent } { ^tuning.copy }
	}
/*
	as { |class|
		^this.semitones.as(class)
	}

	size {
		^tuning.size
	}
*/
	
	at		{|i| ^try { if(isR) {tuning.at(i).ratiomidi} {tuning.at(i) / 100} } {nil} }
	wrapAt	{|i| ^try { if(isR) {tuning.wrapAt(i).ratiomidi} {tuning.wrapAt(i) / 100} } {nil} }
	atR		{|i| ^try { if(isR) {tuning.at(i) } {(tuning.at(i) / 100).midiratio } } {nil} }
	wrapAtR	{|i| ^try { if(isR) {tuning.wrapAt(i) } {(tuning.wrapAt(i) / 100).midiratio } } {nil} }

	== { |argT| //equals
		^case 
		{ argT.isKindOf(Tuning).not } { false }
		{ argT.isMemberOf(RCTuning) } { 
			(if (isR) { tuning == argT.ratios } { tuning == argT.cents })
			&& (octaveRatio == argT.octaveRatio) }
		{ argT.isMemberOf(Tuning) } { 
			(tuning.semitones == argT.tuning) && (octaveRatio == argT.octaveRatio) }
	}
	|=| {|argT| //equals reverse
		^case
		{ argT.isKindOf(Tuning).not } { false }
		{ argT.isMemberOf(RCTuning) } { 
			(if (isR) { tuning == argT.ratios.reverse } { tuning == argT.cents.revese })
			&& (octaveRatio == 1 / argT.octaveRatio) }
		{ argT.isMemberOf(Tuning) } { 
			(tuning.semitones == argT.tuning.revese) && (octaveRatio == 1 / argT.octaveRatio) }
	}
	
	findRootIndex { 
		^if (isR) { tuning.indexOfEqual(1) } { tuning.indexOfEqual(0) } 
	}
	item2Degree {|deg, offset=0 ...codes|
		^case
		{deg.isKindOf(Integer)} { deg = deg + offset;
			if (( deg< 0) || (deg > (tuning.size-1))) {nil}{deg} }
		{deg.isKindOf(String)} {this.code2Degrees(deg, *codes)}
		{deg.isKindOf(Ratio)} { this.ratio2Degree(deg) }
		{deg.isKindOf(Float)} { this.cent2Degree(deg) }
		{deg.isKindOf(Collection)} { 
			^deg.collect{|item| this.item2Degree(item, offset, *codes)} } //recursive!
		{nil}
		/*
			SYS.testSystema.tuning.item2Degree(25)
			SYS.testSystema.tuning.item2Degree("𝍜")
			SYS.testSystema.tuning.item2Degree(16/|15)
			SYS.testSystema.tuning.item2Degree(111.7)
			SYS.testSystema.tuning.item2Degree([25, 16/|15, 111.7, "Ͻ𝍷𝈬"])
		*/
	}
	items2Degrees {|items, offset ...codes| 
		^items.collect{|i| this.item2Degree(i, offset, *codes) } 
	}
	ratio2Degree{|ratio| 
		^if (isR) { tuning.indexOfEqual(ratio) }{ tuning.indexOfEqual(ratio.ratiocent) }
	}
	cent2Degree{|cent| 
		^this.cents.round(Systema.centPresc).indexOfEqual(cent.round(Systema.centPresc))
	}
	code2Degrees{|str ...codes|
		var degs=List.new;
		var sys = Systema.all[name];
		if (sys.isNil) {^degs};
		codes = codes.flat.asList;
		if (codes.isEmpty) { codes.addAll(sys.globDict[\stepCodes] ? Set[]) };
		if (codes.isEmpty) { "no stepCodes specified".warn; ^degs};
		str.takeUTF8.do{|utf8| codes.do{|code|
			var index = (sys.stepDicts ? #[]).detectIndex{|dict| dict[code] == utf8};
			if (index.notNil) { degs.add(index) } //multi trigger possible -> use for chords?
		}};
		^degs
	}
	
/*
	*doesNotUnderstand { |selector, args|
		var tuning = this.newFromKey(selector, args);
		^tuning ?? { super.doesNotUnderstand(selector, args) }
	}

	*directory {
		^TuningInfo.directory
	}

	stepsPerOctave { 
		^octaveRatio.log2 * 12.0	 //	! nothing else but ( octaveRatio.ratiomidi ) !
	}
*/

	copy {|argName|
		^this.class.new(tuning.deepCopy, octaveRatio.copy, argName ?? { name.asString ++"Cpy" },
			isR, isCyclic)
	}
	deepCopy {|argName| ^this.copy(argName) }
	storeOn { |stream|
		stream << this.class.name << "(" << tuning.asCompileString << ", " 
			<< octaveRatio.asCompileString << ", ";
		name.storeOn(stream);
		stream << ", " << isR << ", " << isCyclic << " )"
	}

	printOn { |stream|
		stream << "a " << this.class.name << " <";
		name.printOn(stream);
		if (isCyclic) { stream << " cyclic " } { stream << " non-cyclic "};
		stream << this.size << " /\\ " << octaveRatio; //" octaveRatio.asString 
		if (isR) { stream << " R" } { stream << " C"};
		stream << "> "
	}
}
/*
+SequenceableCollection {
	revAt { |index| ^this.at(this.size-1-index) }
	revWrapAt {|index| ^this.at(this.size-1-(index % this.size)) }
}
*/

+Symbol {
	asRCTuning {|isCyclic| 
		^(Systema.all[this] !? {Systema.all[this].tuning.isCyclic_(isCyclic)}) 
			? (this.asTuning !? {this.asTuning.asRCTuning(isCyclic)}) 
	}
}

+String {
	asRCTuning {|isCyclic| ^this.asSymbol.asRCTuning(isCyclic) }
}

+Tuning {
	asRCTuning {|isCyclic| ^RCTuning(tuning * 100, octaveRatio, name).isCyclic_(isCyclic) }
}
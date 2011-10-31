
LiltQuarks : RepQuarks {
	// Set the quark path so that this file can be inside a subfolder of the Repository:
	*getQuarkPath {
		^filenameSymbol.asString.dirname.dirname +/+ "/";
	} 	
}
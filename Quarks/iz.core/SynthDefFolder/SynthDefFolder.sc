


SynthDefFolder {

	*initClass {
		StartUp add: {
			(this.filenameSymbol.asString.dirname +/+ "*.scd").pathMatch do: _.load;
		}	
	}
	
}
/* iz Sunday; September 28, 2008: 2:20 PM
Communicates between a single value and objects that can change it or can receive updates from it for display or other purposes. 

Similar to CV in Conductor Quark but does not use bidirectional 'sync'

Used by Config. 

*/

ValueAdapter : Model {
	var <value;
	value_ { | val |
		value = val;
		this.changed(\value, value);
	}
	
}

/*
InterfaceAdapter : ValueAdapter {
	var config, key;
	*new { | config, key, specs |
		thisNewCopyArgs(nil, nil, config, key).init(specs);
	}
	init {
		config.event.put(key, this);
		specs do: _.makeInterface(this);
	}
}
*/
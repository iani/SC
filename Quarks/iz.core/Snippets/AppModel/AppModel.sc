/* IZ Wed 15 August 2012  9:14 PM EEST
Trying whole different approach to the whole widget / value notification thing. 
*/

AppModel {
	
	var <values;  // IdentityDictionary
	var <widgets; // IdentityDictionary
	
	*new {
		^this.newCopyArgs(IdentityDictionary.new, IdentityDictionary.new);
	}

	addWidget { | name, widget |
		
	}

	getWidget { | name |
		
	}
	
	setWidgetValue { | name, value |
		
	}
	
	getWidgetValue { | name, value |
		
	}


}
/* IZ 100517
Improving class method new in SimpleController to provide shortcut for initializing the list of message and action pairs it listens to. 
*/

+ SimpleController {
	*new { arg model ... messageActionPairs;
		^super.newCopyArgs(model).init(messageActionPairs);
	}
	init { | messageActionPairs |
		model.addDependant(this);
		messageActionPairs pairsDo: { | message, action | 
			this.put(message, action);
		};
		// a tad kludgey: prevent errors when no actions have been added
		if (actions.isNil, {
			actions = IdentityDictionary.new(4);
		});
	}
}
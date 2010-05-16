/* IZ 080430
A more efficient scheme for adding dependants that trigger a function only when a specific symbol is matched. The following:  
'hello'.addSymbolAction('sunrise', { "good morning".postln; }); 
Will trigger the function { "good morning".postln; } when this is evaluated: 'hello'.changed('sunrise');
Multiple actions can be set to be triggered when one symbol is matched. 
Any number of symbols can be added. 
To remove all actions matched to symbol 'foo', do: 
anObject.removeSymbolAction('foo')
To remove a specific function, the same function must be sent together with the symbol: 

e = { "good evening".postln; };
n = { "... and good night".postln; };

'hello'.addSymbolAction('sunset', e);
'hello'.changed('sunset');
'hello'.addSymbolAction('sunset', n);
'hello'.changed('sunset');
'hello'.removeSymbolAction('sunset', e);
'hello'.changed('sunset');
'hello'.removeSymbolAction('sunset', n);
'hello'.changed('sunset');

This scheme is more efficient than adding multiple functions as dependants, where each action checks separately whether the message matches. 

More examples: 
'hello'.addSymbolAction('sunrise', { "good morning".postln; });
'hello'.addSymbolAction('sunset', { "good evening".postln; });
'hello'.addSymbolAction('sunset', { "... and good night".postln; });
'hello'.changed(\sunrise);
'hello'.changed(\sunset);
'hello'.removeSymbolAction('sunrise');
'hello'.removeSymbolAction('sunset');


*/

SymbolAction : Event {
	*getSymbolActionsFor { | object |
		^object.dependants detect: _.isKindOf(this);
	}
	*add { | object, symbol, action |
		var actions;
		actions = this.getSymbolActionsFor(object);
		if (actions.isNil) { object.addDependant(actions = this.new) };
		actions[symbol] = actions[symbol].add(action);
	}
	*remove { | object, symbol, action |
		var actions, actionArray;
		actions = this.getSymbolActionsFor(object);
		if (actions.isNil) { ^this };
		actionArray = actions[symbol];
		if (actionArray.isNil) { ^this };
		if (action.isNil) { actionArray = nil } { actionArray.remove(action) };
		if (actionArray.size == 0) {
			actions[symbol] = nil;
			if (actions.size == 0) { object.removeDependant(actions) }
		};
	}
	
	update { | sender, message ... args |
		this[message] do: _.(*args);
	}
}


+ Object {
	addSymbolAction { | symbol, action | SymbolAction.add(this, symbol, action) }
	removeSymbolAction { | symbol, action | SymbolAction.remove(this, symbol, action) }
}
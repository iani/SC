/* iz 080905

Controller: SimpleController with settable actions: Actions is made writeable, so that controllers for the same type of node can share one prefabricated event as dictionary of actions instead of having to create their own from scratch. 
Also the syntax of creating the controller is more condensed because one can write the actions as an event: (key1: { ... }, key2: { .... }, ...)

Adapter: 
Extends Controller through variable 'target' which can be used as target for all actions. Target is passed as argument to each action in the update method. 

Possible variants: the change method may pass to the action as arguments any of the following combinations: 
update(model, target, what, args)	// fast access to both target and model
							// this is what is used for adapter. 
update(this, what, args)		// slower to access target, but allows access to 
							// model and actions also. Used for DynamicAdapter, which
							// can change the actions ... 
update(target, what, args)			// fast access to target


Possible variants: MultiController, MultiAdapter: adding multiple actions under the same key. For example for redoing Node:onEnd, to add multiple independent actions to do when a Node ends. 

*/

Controller : SimpleController {
	*new { | ... args |
		^this.newCopyArgs(*args).init;
	}
	*newNotActive { | ... args |
		^this.new(*args).remove;
	}
	init {
		super.init;
		actions = actions ?? ();
	}
	model { ^model }
	actions { ^actions }
	add { this.init } // synonym for semantic compatibility with Responder classes
	activate {
		// add self and also activate model 
		this.add;
		this.activateModel;
	}
	activateModel {
		if (model respondsTo: \activate) { model.activate }
	}
	removeOn { | message, argModel |
		argModel = argModel ? model;
		argModel addDependant: { | who, what |
			if (what === message) {
				this.remove;
				argModel removeDependant: thisFunction;
			}
		}
	}
}

Adapter : Controller {
	var <target;
	update { arg theChanger, what ... moreArgs;
		var action;
		action = actions.at(what);
		if (action.notNil, {
			action.valueArray(theChanger, target, what, moreArgs);
		});
	}
	// free target safely if it is a node. 
	// safely = free only if it is still playing
	// Used by NodeArrayController
	free { if (target.isPlaying) { target.free } }
/*	add {
//		thisMethod.report("before super add:", model, actions, model.dependants);
		super.add;
//		thisMethod.report("after super add:", model, actions, model.dependants);
	}
*/
}

DynamicAdapter : Adapter {
	update { arg theChanger, what ... moreArgs;
		var action;
		action = actions.at(what);
		if (action.notNil, {
			action.valueArray(this, what, moreArgs);
		});
	}
}

/*
SimpleAdapter {
	// replace one message by another for the receiver, when the receiver receives 
	// the message 'perform'
	// Used by OSCresponderManager 

	var model, dependant, messageReceived, message2perform;
	
	*new { | model, dependant, messageReceived, message2perform |
		var class = this;
		if (message2perform.isKindOf(Function)) {
			class = FunctionAdapter
		};
		^class.newCopyArgs(model, dependant, messageReceived, message2perform).add;
	}
	add {
		model.addDependant(this);
		DynamicAdapter(dependant)
			.put(\deactivate, { | adapter, change, message |
				if (message === messageReceived) {
					adapter.remove;
					this.remove;
				}
			});
	}
	remove {
		model.removeDependant(this);
	}
	perform { | message ... args |
		dependant.perform(message2perform, *args);
	}
}

FunctionAdapter : SimpleAdapter {
	perform { | message ... args |
		message2perform.value(dependant, *args);
	}
}
*/
/*
a = SimpleAdapter(1, 200, \something, \postln);
a.perform(\something)
*/

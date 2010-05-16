/* iz 080909

So that classes (and instances) can add OSCresponders and activate / deactivate their listening to OSC easily.

ModelWithControllerTestOSC.activate;
OSCmodel.getInstance.changed(\slider);
ModelWithControllerTestOSC.deactivate;
OSCmodel.getInstance.changed('/contours');

NetAddr.localAddr.sendMsg('/contours', *Array.rand(5, 1, 5));
ModelWithControllerTestOSC.getInstance.actions.actions;

The parameters passed to each action function of the OSCcontroller from the OSCresponder made by the OSCmodel are: 

1. the model, 
2. the controller, 
3. the message, 
4. time, 
5. responder, 
6. msg+data (array), 
7. the NetAddr of the sender

*/
OSCmodel : SingletonModel {
	var <responders;
	*defaultArgs { ^nil }
	init {
		responders = IdentityDictionary.new;
	}
	addDependant { | dependant |
		this.makeResponders(dependant);
		super.addDependant(dependant);
	}
	makeResponders { | dependant |
		dependant.actions keysDo: this.makeResponderFor(_)
	}
	makeResponderFor { | key |
		var responder;
		responder = responders[key];
		if (responder.isNil) {
			responder = OSCresponder(nil, key, { | time, responder, message, addr |
				this.changed(key, time, responder, message, addr);
			});
			responders[key] = responder;
		}
	}
	activateKeys { | ... keys |
		var resp;
		keys do: { | k |
			resp = responders[k];
			if (resp.notNil) { resp.add }
		}
	}
	deactivate {
		responders do: _.remove;
		super.deactivate;
	}
}

OSCcontroller : ModelWithController {
	*model { ^OSCmodel.getInstance }
	activate {
		super.activate;
		actions.model.activateKeys(*actions.actions.keys.asArray);
	}
}

OSCcontrollerExample : OSCcontroller {
	*actions {
		^[['/contours',  { | ... args |
				args.postln;
		}], [\slider, { | ... args |
				args.postln;
		}]]
	}
}

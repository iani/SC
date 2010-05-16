/* iz 080908
A Model that creates OSCresponders and listens to them. 
A concise way for making instances of classes that respond to various OSCresponder messages.

Uses OSCresponderManager and SimpleAdapter (q.v.)

ModelWithController: A model that adds itself as dependant to another model, using a Controller. Methods activate / deactivate are used to add / remove the controller from the model. 

a = ModelWithController(1, (one: { | ... args | args.postln }, two: { | ... args | args.postln }));
a.activate;
1.changed(\one);
1.changed(\two);
1.changed(\three);

// experimental: when deactivating one should inform dependant models that may want to know their deactivated state.
  Done by making the interested controllers add a dependant/adapter to their controller's model 
  so that they are informed when that model goes changed(\deactivated)
  see: ModelWithController:init
  Tested on scripts. 
*/

SingletonModel : Model {
	// a model class that provides a single default instance to operate on.
	classvar <instances;
	var <>envir;	// optionally share a script's or other environment
	*getInstance {
		var instance;
		if (instances.isNil) { instances = IdentityDictionary.new };
		instance = instances[this];
		if (instance.isNil) {
			instance = this.new(*this.defaultArgs);
			// (*this.defaultArgs); // .init(*this.defaultArgs);
			instances[this] = instance;
		};
		^instance;
	}
	*new { | ... args |
		^super.new.init(*args).envir_(currentEnvironment); // .init(*args);
	}
	*defaultArgs { ^[this.model] }
	init { this.subclassResponsibility(thisMethod); }
	*activate { this.getInstance.activate }
	activate {
		this.changed(\activated);
	}
	*deactivate { this.getInstance.deactivate }
	deactivate {
		this.changed(\deactivated);
	}
	*changed { | ... args |
		this.getInstance.changed(*args);
	}
	*addScript { | script |
// cannot create new instance as this breaks the links between 
// instances in other scripts to which this instance may depend! 
//		this.new.init(*this.defaultArgs).addScript(script)
		this.getInstance.addScript(script)
	}
	addScript { | script |
		var starter, stopper;
		envir = script.envir;
		starter = { | argScript, message |
			if (message === \started) {
				argScript.removeDependant(starter);
				argScript.addDependant(stopper);
				this.activate;
			}
		};
		stopper = { | argScript, message |
			if (message === \stopped) {
				argScript.removeDependant(stopper);
				argScript.addDependant(starter);
				this.deactivate;
			}
		};
		if (script.isRunning) {
			script.addDependant(stopper)
		}{
			script.addDependant(starter);
		};
		Controller(this,
			(	activated: { script.started },
				deactivated: { script.stopped }
			)
		).add.removeOn(\closed, script);
		if (script.envir[\start].isNil) {
			script.envir[\start] = { postf("script starting: %\n", script.name) }
		}
	}
}

MessagePerformer : SingletonModel {
	var <model;
	*model { this.subclassResponsibility(thisMethod) }
	init { | argModel |
		model = argModel;
	}
	activate {
		super.activate;
		this.add;
		if (model respondsTo: \activate) { model.activate };
	}
	add { model.addDependant(this); }
	remove { model.removeDependant(this); }
	deactivate {
		super.deactivate;
		model.removeDependant(this);
	}
	update { | theModel, theMessage ... args |
		envir use: { this.perform(theMessage, theModel, *args); }
	}
	// messages received from model or script: 
	activated {
		this.changed(\activated);
	}
	deactivated {
		this.changed(\deactivated);
		model.removeDependant(this);
	}
/*
	onActivate { | action |
		this.addDependant( { | who, what | if (what === \activated) { action.(this) } } )
	}
	onDeactivate { | action |
		this.addDependant( { | who, what | if (what === \deactivated) { action.(this) } } )
	}
*/
}

ModelWithController : SingletonModel {
	var <actions;
	*defaultArgs { ^[this.actions, this.model] }
	*new { | argActions, argModel |
		^super.new(argModel ?? { this.model }, argActions ?? { this.actions })
	} 
	init { | model, argActions |
		var messages2, actions2;
//		thisMethod.report(argActions, model);
		argActions = argActions ?? { this.class.actions };
		if (argActions.isKindOf(Array)) {
			#messages2, actions2 = argActions.flop;
			actions2 = actions2.asArray collect: { | x | 
				if (x.isKindOf(Function)) { x }
				{ { | ... args | this.perform(x, *args); } }
			};
			argActions = ();
			messages2.asArray do: { | m, i |
				argActions.put(m.asSymbol, actions2[i])
			};
		};
		// notify dependants that your model has deactivated: 
		argActions.put(\deactivated, { this.changed(\deactivated) });
		actions = Adapter.newNotActive(model ?? { this.class.model }, argActions, this);
	}
	*model { this.subclassResponsibility(thisMethod) }
	*actions { this.subclassResponsibility(thisMethod) }
	activate {
		actions.activate; // also activates model of actions!
		super.activate;
	}
	deactivate {
		actions.remove;
		super.deactivate;
	}
}

/*
ModelWithControllerTest.activate;
ModelWithControllerTest.deactivate;
*/
ModelWithControllerTest : ModelWithController {
	*model {
		var window;
		window = GUI.window.new("test", Rect(400, 400, 200, 200));
		GUI.slider.new(window, Rect(3, 3, 150, 20))
			.action = { | me | window.changed(\slider, me.value) }
		^window.front;
	}
	*actions { 
		^(
			slider: { | window, self, message, args |
				[window, self, message, args].postln;
			}
		) 
	}
}

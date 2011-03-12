// result is a dict of (argname:value) which can be passed to newFromParamDict
BMControllerConfigGUI : BMAbstractGUI {
	var class, parent, okayFunc, existingParams, params, widgets;
	
	*new {|class, parent, okayFunc, existingParams|
		^super.new.init(class, parent, okayFunc, existingParams).makeWindow;
	}
	
	init {|argclass, argparent, argokayFunc, argexistingParams|
		class = argclass;
		parent = argparent;
		okayFunc = argokayFunc;
		params = class.parameterList;
		existingParams = argexistingParams ? ();
		widgets = Array.new(params.size);
	}
	
	makeWindow {
		var result, textFields;
		result = ();
		textFields = List.new;
		window = SCModalSheet(parent, Rect(30, 30, 300, params.size + 1 * 24 + 44));
		window.addFlowLayout;
		StaticText(window, Rect(10, 10, 280, 20)).font_(Font("Helvetica-Bold", 12))
			.string_("Configure" + class.humanName);
		params.keys.asArray.sort.do({|argName|
			var vals, widget, paramclass, lastValidInput;
			var keys, string;
			var existingPort;
			vals = params[argName]; // argname->[class, spec, humanName];
			paramclass = vals[0];

			case(
				{paramclass == Integer || (paramclass == Float)}, {
					widget = EZNumber(window, 292@20, vals[2], vals[1], 
						initVal: existingParams[argName], // maybe nil
						labelWidth: 100);
					widget.numberView.background_(Color.white.alpha_(0.3));
				}, 
				{paramclass == BMMIDIPort }, {
					keys = BMMIDIPort.ports.keys.asArray.sort;
					existingPort = existingParams[argName];
					SCStaticText(window, 100@20).string_(vals[2]).align_(\right);
					widget = SCPopUpMenu(window, 188@20)
						.background_(Color.white.alpha_(0.3))
						.items_(keys) 
						.value_(
							existingPort.notNil.if({
								keys.indexOf(existingPort.name.asSymbol) ? 0; // maybe nil
							}, {0});
						)
				},
				{paramclass != String && paramclass.superclasses.includes(RawArray)}, {
					SCStaticText(window, 100@20).string_(vals[2]).align_(\right);
					existingParams[argName].do({|item|
						if(string.size > 0, { string = string ++ ", "});
						string = string ++ item.asString;
					});
					widget = SCTextField(window, 188@20)
						.background_(Color.white.alpha_(0.3))
						.string_(string ?? {vals[1].value});
					textFields.add(widget);
				},
				// default
				{
					SCStaticText(window, 100@20).string_(vals[2]).align_(\right);
					widget = SCTextField(window, 188@20)
						.string_(existingParams[argName] ?? {vals[1].value})
						.background_(Color.white.alpha_(0.3));
					textFields.add(widget);
				}
			);
			
			if(paramclass != String && paramclass.superclasses.includes(RawArray), {
				lastValidInput = "";
				widget.action_({
					var interpretedInput;
					try {
						interpretedInput = 
							(paramclass.asString ++ "[" ++ widget.value ++ "]").interpret;
						result[argName] = lastValidInput = interpretedInput;
					} {|error| 
						("Invalid input for array parameter" + vals[2] ++ ". Please re-enter.").error;
						widget.string = lastValidInput;
					};
				});
			}, {
				widget.action_({
					var res;
					res = widget.value;
					if(paramclass == Symbol, { res = res.asSymbol });
					if(paramclass == BMMIDIPort , {
						 res = BMMIDIPort.ports[widget.item]; 
					});
					result[argName] = res;
				});
			});
			widget.doAction;
		});
		
		window.view.decorator.nextLine.nextLine;
		window.view.decorator.shift(window.bounds.width - 242, 0);
		
		RoundButton(window, 115 @ 20)
			.extrude_(false).canFocus_(false) 
			.states_([[ "Cancel", Color.black, Color.white.alpha_(0.8) ]])
			.action_({ window.close });
			   
		RoundButton(window, 115 @ 20)
			.extrude_(false).canFocus_(false)
			.states_([[ "OK", Color.black, Color.new255(51, 111, 203, 255 * 0.95) ]])
			.action_({ 
				textFields.do({|tf| tf.doAction});
				window.close;
				okayFunc.value(result);
				onClose.value(this);
			});
		
		
	}
}
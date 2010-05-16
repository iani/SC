/* IZ Sunday, May 11, 2008 (+080429)
Output Parameter that updates elements bound to it whenever triggered from within a Tr-output script.

Implements linking between scripts via actions (functions?) performed from a routine inside the writing script, as opposed to via ar or kr busses.

towards an example: 
	var script;
	script = ~script;
	~controlSpecs = [
		['t_out'] // 't_out' as name creates a TrOutput parameter
	];
	~script.routine {
		loop {
			0.25.wait;
			'broadcasting'.postln;
			script.set('t_out', 1.0.rand);	// set value, update gui, broadcast to linked readers
		}
	}
*/

TrOutput : AbstractOutputParameter {
	addReader { | reader |
//		thisMethod.report(reader);
		output = output add: reader;
		reader.addTrWriter(this);
		script.changed(\readers);
//		reader.script.changed(\writers);
	}
	removeReader { | reader |
		output.remove(reader);
		reader.removeTrWriter(this);
		script.changed(\readers);
	}
	makeDefaultAction { | envir |
		// set all your outputs to your value
		^{ | val ... args |
			envir[name] = val;
			output do: _.set(val, *args);
			script.changed(name, val, *args);
		};
	}
	setScriptAttributes {	// set flag for script label color
		script.hasTriggerOutput = true;
	}
	outputRate { ^\trigger; }
	outBoxBackground { ^Color.black.alpha_(0.1) }
	labelBackground { ^Color.white.alpha_(0.9) }
	getReaderParameters { ^output ? [] }
}
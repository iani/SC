/* iz Wednesday; September 10, 2008: 12:26 PM

Redoing simple sines from contours using NodeController as ModelWithController

See also newer approach in SimpleContourSound -> subclass of ContourController

*/

ContourSines1 : NodeController {
	var <freqSpec;
	*model {
		^ContourCluster.getInstance;
	}
	*actions {
		^[
			['contour_born', { | cluster, self, message, contour |
				self.addNode(
					Synth("variable_sin", [\freq, self.freqSpec.value(contour.x)]),
					contour
				)
			}],
			['zero_contours',  { | cluster, self | self.freeAll }],
		]
	}
	init { | argModel, argActions, argNodeActions, argFreqSpec |
		argNodeActions = argNodeActions ?? {(
			moved: { | contour, synth |
				synth.set(\freq, freqSpec.value(contour.x));
			},
			died: { | contour, synth |
				if (synth.isRunning) { synth.free; }
			}
		)};
		super.init(argModel, argActions, argNodeActions);
		freqSpec = argFreqSpec ?? { BiMap(-1, 1, \linear, 0, 100, 5000, \exponential); }
	}
}


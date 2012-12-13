/* iz Mon 10 December 2012  1:03 AM EET

Variant of GraphBuilder that adds amplitude control to the envelope.
Used in Function:eplay
*/

AmpGraphBuilder : GraphBuilder {
	//used to create an out ugen automatically and a fade envelope

	*makeFadeEnv { arg fadeTime = (0.02);
		var dt = NamedControl.kr(\fadeTime, fadeTime);
		var gate = NamedControl.kr(\gate, 1.0);
		var vol = NamedControl.kr(\vol, 1, 0.2);
		var startVal = (dt <= 0);
		^EnvGen.kr(Env.new([startVal, 1, 0], #[1, 1], \lin, 1), gate, 1, 0.0, dt, 2) * vol;
	}



}


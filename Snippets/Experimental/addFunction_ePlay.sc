/* iz Sun 09 December 2012  2:12 PM EET

Variant of Function:play with following modifications:

- Add Control 'vol' to the levelScale parameter of the envelope for volume control
- Use NodeWatcher instead of OscNodeResponder, so that SynthDef is removed also at CmdPeriod.
- Store SynthDef in SynthDescLib.global for access to auto-build gui etc., and remove it when Synth is freed.

*/

+ Function {

	eplay { | target, outbus = 0, fadeTime = 0.02, addAction=\addToHead, args |
		var def, synth, server, bytes, synthMsg, oscFunc, cmdPeriodFunc;
		target = target.asTarget;
		server = target.server;
		if (server.serverRunning.not) {
			("server '" ++ server.name ++ "' not running.").warn; ^nil
		};
		def = this.asAmpSynthDef(
			fadeTime:fadeTime,
			name: SystemSynthDefs.generateTempName
		);
		synth = Synth.basicNew(def.name, server);
		if (server.notified) {
			NodeWatcher.register(synth);
			this.addNotifierOneShot(synth, \n_end, {
				server.sendMsg(\d_free, def.name);
				SynthDef.removeAt(def.name);
			});
		};
		synthMsg = synth.newMsg(target, [\i_out, outbus, \out, outbus] ++ args, addAction);
		def.add(completionMsg: synthMsg);
		^synth
	}

	asAmpSynthDef { arg rates, prependArgs, outClass=\Out, fadeTime, name;
		^AmpGraphBuilder.wrapOut(name ?? { this.identityHash.abs.asString },
			this, rates, prependArgs, outClass, fadeTime
		);
	}
}


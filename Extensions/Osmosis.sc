/* Setup for piece: Buffers etc.

IZ 2011 0316


O@\x


(a: 1)@\a


O.startup;
O@\swallowse

O.openScoreFiles;

*/

Osmosis {
	classvar <o;
	var <>buffers;
//	var <>players;
	
	*initClass { o = this.new }
	
	*new { 
		^super.new.init;	
	}
	
	init { buffers = IdentityDictionary.new }
	
	startup { // setup the piece: configure and restart server, load synthdefs, load buffers.
		{
			this.quitServer;
			0.5.wait;
			this.setupServer;
			this.bootServer;
		}.fork(AppClock);
	}

	openScoreFiles {
		(Platform.userAppSupportDir ++ "/OSMOSIS/*.scd").pathMatch do: Document.open(_);
	}

	quitServer {
		if (Server.default.serverRunning) { Server.default.quit };
	}
	
	setupServer {
		Server.default = Server.local;
		Server.default.options.numAudioBusChannels = 256;
		Server.default.options.numOutputBusChannels = 43;
		Server.default.options.numInputBusChannels = 2;
	}
	
	bootServer {
		Server.default.waitForBoot({
			this.loadSynthDefs;
			this.loadBuffers;
		});
	}

	loadSynthDefs {
		"======== LOADING SYNTHDEFS ==========".postln;
		(Platform.userAppSupportDir ++ "/synthdef_code/*.scd").pathMatch do: { | path |
			postf("loading: % -- ", path.basename);
			path.load.postln;
		};
//		{ SynthDescLib.global.browse }.defer(1);		
	}
	
	loadBuffers {
		var bufname;
		"======== LOADING BUFFERS ==========".postln;
		(Platform.userAppSupportDir ++ "/sounds/*").pathMatch do: { | folder |
			postf("--------- loading folder: % -- \n", folder.basename);
			(folder ++ "/*.aiff").pathMatch do: { | path |
				bufname = path.basename.splitext.first.asSymbol;				
				postf("* loading buffer : %\n", bufname);
				buffers[bufname] = Buffer.read(Server.default, path);
			}
		}
	}
	
	*doesNotUnderstand { | message ... args | ^o.perform(message, *args) }
	
	@ { | bufname | ^buffers[bufname].bufnum }
	
	bplay { | bufname | ^buffers[bufname].play }
	
//	add { | playername | } 
//	> { | playername | ^players[playername] }
	
	startSynths {
		~swallowsa 		= KDpan([\bphasor, \bufnum, O@\swallowsa, \vol, 0.5, \rate, 1]);
		~weddella			= KDpan([\bphasor, \bufnum, O@\weddella, \vol, 0.5, \rate, 1]);
		~weddellb			= KDpan([\bphasor, \bufnum, O@\weddellb, \vol, 0.5, \rate, 1]);
		~weddellc			= KDpan([\bphasor, \bufnum, O@\weddellc, \vol, 0.5, \rate, 1]);
		~weddelld			= KDpan([\bphasor, \bufnum, O@\weddelld, \vol, 0.5, \rate, 1]);
		~weddelle			= KDpan([\bphasor, \bufnum, O@\weddelle, \vol, 0.5, \rate, 1]);
		
		~chimes			= KDpan([\bphasor, \bufnum, O@\chimes, \vol, 0.5, \rate, 1]);
		~dashes			= KDpan([\bphasor, \bufnum, O@\dashes, \vol, 0.5, \rate, 1]);
		~dfd				= KDpan([\bphasor, \bufnum, O@\dfd, \vol, 0.5, \rate, 1]);
		~newstar			= KDpan([\bphasor, \bufnum, O@\newstar, \vol, 0.5, \rate, 1]);
		~strich			= KDpan([\bphasor, \bufnum, O@\strich, \vol, 0.5, \rate, 1]);
		
		~allsw			= NodeArray({ | i | Synth(\bphasor, [\out, i, \bufnum, O@\swallowsa, \rate, 1, \vol, 0]); });
		~allswd			= KDpanvol(~allsw);
		
		~allwe			= NodeArray({ | i | Synth(\blfn3, [\out, i, \bufnum, O@\weddelle, \vol, 0]); });
		~allwed			= KDpanvol(~allwe);
		
		~alldf			= NodeArray({ | i | Synth(\bphasor, [\out, i, \bufnum, O@\dfd, \vol, 0]); });
		~alldfd			= KDpanvol(~alldf);		

		~allch			= NodeArray({ | i | Synth(\bphasor, [\out, i, \bufnum, O@\chimes, \vol, 0]); });
		~allchd			= KDpanvol(~allch);		

		~allda			= NodeArray({ | i | Synth(\bphasor, [\out, i, \bufnum, O@\dashes, \vol, 0]); });
		~alldad			= KDpanvol(~allda);		

		~allne			= NodeArray({ | i | Synth(\bphasor, [\out, i, \bufnum, O@\newstar, \vol, 0]); });
		~allned			= KDpanvol(~allne);		

		~allst			= NodeArray({ | i | Synth(\bphasor, [\out, i, \bufnum, O@\strich, \vol, 0]); });
		~allstd			= KDpanvol(~allst);		
	}
}

O : Osmosis { } // Define O as shortcut for Osmosis



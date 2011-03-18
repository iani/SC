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
	var <buffers;
	
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
	
}

O : Osmosis { } // Define O as shortcut for Osmosis
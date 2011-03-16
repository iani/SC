

if (Server.default.serverRunning) { Server.default.quit };

Server.default = s = Server.local;

Server.default.options.numAudioBusChannels = 256;
Server.default.options.numOutputBusChannels = 43;
Server.default.options.numInputBusChannels = 2;

Server.default.waitForBoot({
	"============= LOADING SYNTHDEFS ===============".postln;
	(Platform.userAppSupportDir ++ "/synthdef_code/*.scd").pathMatch do: { | path |
		postf("loading: % -- ", path.basename);
		path.load.postln;
	};
	"============= LOADING BUFFERS ===============".postln;
	(Platform.userAppSupportDir ++ "/sounds/*").pathMatch do: { | path |
		postf("loading buffer folder: % -- ", path.basename);
		
	};
	
});

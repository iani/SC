
CocoaMenuItem.addToMenu("Utils", "Boot/Quit default server", ["B", true, false], { 
	if (Server.default.serverRunning) { Server.default.quit } { Server.default.boot };
});

CocoaMenuItem.addToMenu("Utils", "Edit startup file", ["S", true, true], { 
	Document.open(UserPath("startup.scd"))
});

CocoaMenuItem.addToMenu("Utils", "Open User Directory", ["o", true, true], { 
	"open ~/Library/Application\\ Support/SuperCollider".unixCmd;
});

CocoaMenuItem.addToMenu("Utils", "Toggle OSC input posting", ["O", true, true], { 
	if (thisProcess.recvOSCfunc.isNil) {
		thisProcess.recvOSCfunc = { | time, addr, msg |
			if (msg[0].asString.contains("status.reply").not) {			postf("time: % sender: % message: %\n", time, addr, msg)
			};
		};
		"OSC Posting is ON".postln;
	}{
		thisProcess.recvOSCfunc = nil;
		"OSC Posting is OFF".postln;
	}
});

CocoaMenuItem.addToMenu("Utils", "Start OSC input test", ["I", true, true], { 
	{
		var a;
		a = NetAddr.localAddr;
		loop { a.sendMsg(\test); 0.25.wait };
	}.fork
});

CocoaMenuItem.addToMenu("Utils", "Open log file in orgmode", ["l", true, true], { 
	{
		var logname;
		logname = Platform.userAppSupportDir ++ "/sclog.org";
		if (logname.pathMatch.size == 0) {
			format("touch %", logname.asCompileString).unixCmd;
			0.1.wait;
		};
		format("open -a Emacs.app %", logname.asCompileString).unixCmd;
	}.fork
});

//:rest
Panes.activate;

{ Document.listener.front }.defer(0.5);

//:developer specials - should not be on git because they create files that interfere with pull: 

(Platform.userAppSupportDir ++ "/dev_only/*.scd").pathMatch do: _.load;


//:OSC Responders
SpectroTouchOSC.makeResponders;
//: My Synths
MySynths.sendToServer;
//: My Buffers
//MyBuffers.osmoFull;
//MyBuffers.seals;
//MyBuffers.sketch;
//BufferResource.postNames;
//:SendSpectrogramData
//SendSpectrogramData.new;
//SendSpectrogramData('fft').free;
//s.boot;


(Platform.userAppSupportDir ++ "/startupOC/*").pathMatch do: _.load;


CocoaMenuItem.addToMenu("User Menu", "Edit startup file", ["S", true, true], { 
	Document.open(UserPath("startup.scd"))
});

CocoaMenuItem.addToMenu("User Menu", "Open User Directory", ["o", true, true], { 
	"open ~/Library/Application\\ Support/SuperCollider".unixCmd;
});

CocoaMenuItem.addToMenu("User Menu", "Toggle OSC input posting", ["O", true, true], { 
	if (thisProcess.recvOSCfunc.isNil) {
		thisProcess.recvOSCfunc = { | time, addr, msg |
			if (msg[0].asString.contains("status.reply").not) {			postf("time: % sender: % message: %\n", time, addr, msg)
			};
		};
	}{
		thisProcess.recvOSCfunc= nil;
	}
});

CocoaMenuItem.addToMenu("User Menu", "Start OSC input test", ["I", true, true], { 
	{
		var a;
		a = NetAddr.localAddr;
		loop { a.sendMsg(\test); 0.25.wait };
	}.fork
});

CocoaMenuItem.addToMenu("User Menu", "Open log file in orgmode", ["l", true, true], { 
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

// DocListWindow.start;

/*
Document.themes[\pinkString] = Document.themes[\default].copy;
Document.themes[\pinkString][\stringColor] = Color(0.8, 0.0, 0.7);
Document.setTheme(\pinkString);
*/

Document.themes[\pinkString] = Document.themes[\default].copy;
// Document.themes[\pinkString][\textColor] = Color(0.4, 0.4, 0.5);
Document.themes[\pinkString][\textColor] = Color(0.5, 0.4, 0.4);
// Document.themes[\pinkString][\stringColor] = Color(0.8, 0.0, 0.7);
Document.themes[\pinkString][\stringColor] = Color(0.9, 0.1, 0.6);
Document.themes[\pinkString][\numberColor] = Color(0.7, 0.2, 0, 1);
Document.themes[\pinkString][\classColor] = Color(0.1, 0.6, 0.9);
Document.themes[\pinkString][\backColor] = Color.grey;
Document.themes[\pinkString][\background] = Color.grey;
Document.themes[\pinkString][\backgroundColor] = Color.grey;
Document.setTheme(\pinkString);

Document.postColor = Color(0.4, 0.4, 0.5);
Panes.activate;

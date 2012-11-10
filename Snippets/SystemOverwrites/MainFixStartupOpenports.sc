
+ Main {

	startup {
	    var didWarnOverwrite = false;
		// setup the platform first so that class initializers can call platform methods.
		// create the platform, then intialize it so that initPlatform can call methods
		// that depend on thisProcess.platform methods.
		platform = this.platformClass.new;
		platform.initPlatform;

		super.startup;

		// set the 's' interpreter variable to the default server.
		interpreter.s = Server.default;
		GUI.fromID( this.platform.defaultGUIScheme );
		GeneralHID.fromID( this.platform.defaultHIDScheme );
		this.platform.startup;
		// Enable creating OSCFunc instances with custom ports at startup
		// iz Sat 10 November 2012  3:10 PM EET
		openPorts = Set[NetAddr.langPort];
		StartUp.run;

		("Welcome to SuperCollider %.".format(Main.version)
			+ (Platform.ideName.switch(
				"scvim", {"For help type :SChelp."},
				"scel",  {"For help type C-c C-y."},
				"sced",  {"For help type ctrl-U."},
				"scapp", {"For help type cmd-d."},
				"scqt", {"For help press %.".format(if(this.platform.name==\osx,"Cmd-D","Ctrl-D"))}
			) ?? {
				(
					osx: "For help type cmd-d.",
					linux: "For help type ctrl-c ctrl-h (Emacs) or :SChelp (vim) or ctrl-U (sced/gedit).",
				 	windows: "For help press F1.",
					iphone: ""
				 ).at(platform.name);

			})
		).postln;

		Main.overwriteMsg.split(Char.nl).drop(-1).collect(_.split(Char.tab)).do {|x|
			if(x[2].beginsWith(Platform.classLibraryDir) and: {x[1].contains(""+/+"SystemOverwrites"+/+"").not}
			) {
				warn("Extension in '%' overwrites % in main class library.".format(x[1],x[0]));
				didWarnOverwrite = true;
			}
		};
		if(didWarnOverwrite) {
			inform("\nIntentional overwrites must be put in a 'SystemOverwrites' subfolder.")
		}
	}
}
UserPrefs {
	classvar prefsDir="UserPrefs";
	*initClass { 
		//[File, UnixPlatform].do{|class| Class.initClassTree(class) }; 
		this.checkPrefs;
		//StartUp.add({this.checkPrefs}); //too late if used with *initClass by other Classes
	}
	*checkPrefs{
		prefsDir = Platform.userAppSupportDir +/+ prefsDir;
		if (File.exists(prefsDir).not) {
			if (thisProcess.platform.isKindOf(UnixPlatform).not) {
				("¥" ++ this ++ ": does not know how to create" + prefsDir + "on this platform").warn
			}{ this.logln("mkdir:" + ("mkdir -pv" + prefsDir.escapeChar($ )).unixCmdGetStdOut) }
		}
	}
	*load{|relPath, defaultPrefsEvent|
		var path = prefsDir +/+ relPath;
		^if (File.exists(path)) { path.load 
		}{ this.save(relPath, defaultPrefsEvent); defaultPrefsEvent }
	}
	*save{|relPath, prefsEvent|
		var path = prefsDir +/+ relPath;
		var str = prefsEvent.asCompileString;
		if (thisProcess.platform.isKindOf(UnixPlatform).not) {
				("¥" ++ this ++ ": does not know how to create prefs file" + path 
				+ "on this platform\n -> save following post manually:\n" + str).warn
		}{
			this.logln("mkdir:" + ("mkdir -pv" + path.dirname.escapeChar($ )).unixCmdGetStdOut);
			this.prWriteFile(path, str)
		}
	}
	*prWriteFile{|fullPath, str|
		var res, file;
		file = File(fullPath, "w"); 
		if (file.isOpen) { 
			res = file.write(str);
			file.close;
		}; // this.logln("saved:" + [fullPath, res]);
		^res;
	}
}

/*
TemplateTestClass {
	classvar prefs, subFolderList; // =#["put", "in", "all", "these"];
	classvar prefsFile="TemplateTestPrefs.scd";
	*initClass {
		var relPath = if (subFolderList.isNil) { prefsFile 
			}{ "" +/+ subFolderList.reduce('+/+') +/+ prefsFile };
		prefs = ();
		Class.initClassTree(UserPrefs);
		prefs.putAll(UserPrefs.load(relPath, this.getDefaults));
		this.logln("myPrefs:\n" + prefs, lfB:1, lfE:1);
	}
	*getDefaults {
		^( myDefault1: 1, myDefault2: "zwei")
	}	
}
*/
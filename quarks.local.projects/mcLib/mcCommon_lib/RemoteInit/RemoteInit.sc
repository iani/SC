RemoteInit { // supports 2 delay stages 
	
	classvar <classes, <callers, <defaultDly=2;
	
	var <>iClass, <>who, <dir, <relativeDir, <configFile, extraArgs;
	var <preClass, <pstClass, <>preDly, <>pstDly, <>selfNotification=false;
	
	*initClass { 
		Class.initClassTree(StartUp);
		classes = IdentityDictionary.new;
		callers = IdentityDictionary.new; //for debug mainly
	}
	*isInited {|clss| 
		if (classes[clss.asClass].isNil) {^false}
			{ if (classes[clss.asClass][\postLoad]) {^true} {^false} }
	}
	*new{|clss, who, path, relativePath, file, pre, pst ...args|
		^super.new.iClass_(clss).who_(who ? clss).init(path, relativePath, file, pre, pst, args);
	}
	init {|path, relativePath, file, pre, pst, args| var iDic;
		if (classes[iClass].isNil) {
			iDic = IdentityDictionary.new;
			iDic.put(\preLoad, false).put(\postLoad, false).put(\callers, List[who]);
			classes.put(iClass, iDic);
		}{
			classes[iClass][\callers].add(who);
		};
		if (callers[who].isNil) {
			callers.put(who, List[iClass]);
		}{
			callers[who].add(iClass);
		};
		dir = (path ?? {PathName(iClass.class.filenameSymbol.asString).pathOnly.drop(-1)})
			+/+ (relativePath ?? {iClass.relativeDir} );
		configFile = dir +/+ (file ? iClass.configFile);
		extraArgs = args;
		case 
			{ pre.isKindOf(Class) }	{ preDly = 0; preClass = pre}
			{ pre.isKindOf(SequenceableCollection) }{ 
				preDly=pre[1] ? 0; preClass = pre[0] !? {pre[0].asClass } }
			{ preDly = pre ? 0};
		case 
			{ pst.isKindOf(Class) }	{ pstDly = 0; pstClass = pst}
			{ pst.isKindOf(SequenceableCollection) }{ 
				pstDly=pst[1] ? 0; pstClass = pst[0] !? {pst[0].asClass } }
			{ pstDly = pst ? 0};
		// if class needs the AppClock immediately, it can still add more stuff to StartUp.add
		if (this.inited.not) { AppClock.sched(preDly, { this.takeInit }); }
	}
	inited {	
		if (classes[iClass][\postLoad]) { 
			// ("*¥" + iClass + "already inited; attempt failed by" + who).warn;
			if (iClass != who) { who.tryPerform( \updRemoteInit, iClass, 1) };
			^true } {^false}
	}
	inniting {
		if (classes[iClass][\preLoad]) { 
				// ("*¥" + iClass + "already stage1 ("++who+"failed)").warn;
			 who.tryPerform( \updRemoteInit, iClass, 2); //failed for the caller
			^true } {^false}
	}
	takeInit{ //stage1
		if (this.inited.not) { if (this.inniting.not) { 
			classes[iClass][\preLoad] = true;
			if (preClass.notNil) {
				NotificationCenter.registerOneShot(preClass, \inited, iClass,
					{ this.preLoadInit }); // wait for preClass then preLoadInit 
			}{
				this.preLoadInit 
			}
		}}
	}
	preLoadInit {
//this.logln("preLoadInit:" + [who, extraArgs]);
		iClass.tryPerform(\preLoadInit, this, who, *extraArgs);
		if (File.exists(dir).not) {
			this.createRelDirs;
		}{ 
			if (File.exists(configFile)) { 
				configFile.load;
				classes[iClass][\postLoad] = true;
				if (pstClass.notNil) {
					NotificationCenter.registerOneShot(pstClass, \inited, iClass,
						{ AppClock.sched(pstDly,{ this.postLoadInit }) });				}{
					AppClock.sched(pstDly,{ this.postLoadInit });
				}
			}{	
				("*¥" + configFile + "does not exist").warn;
			 	iClass.tryPerform(\failLoadInit, dir);
			 	if (iClass != who) {who.tryPerform(\updRemoteInit, iClass, 2)};
			}
		}
	}
	createRelDirs {
		if (thisProcess.platform.isKindOf(UnixPlatform).not) {
			("*¥do not know how to create a new folder on this platform").warn
		} {
this.logln("createRelDirs" + iClass);
			("mkdir -p -v" + dir.escapeChar($ )).unixCmd({|res|
				if (res != 0) { ("*¥ could not create directory:" + dir).warn; 
					iClass.tryPerform(\failLoadInit, nil);
				}{
					iClass.tryPerform(\failLoadInit, dir);
				};
				if (iClass != who) {who.tryPerform(\updRemoteInit, iClass, 2)};
			}, true);
		}
	}
	postLoadInit { //stage2
		iClass.tryPerform(\postLoadInit, dir);
		classes[iClass][\postLoad] = true;
		classes[iClass][\preLoad] = false;
		if (iClass != who) {who.tryPerform(\updRemoteInit, iClass, 0)};
		if (selfNotification.not) { NotificationCenter.notify(iClass, \inited) };
	}
}
/*
	RemoteInit.classes[RemoteIntiClass]
*/
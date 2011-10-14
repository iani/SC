Systema : Scale {
	/*
	support pitchEvent: 	
How to self-address an event from inside the event? simply 'this' does not work?
	eventExtras as Event -> then this.putAll(): (root: midiRoot and ... logOn, noMatchWarn; 	noMatchBeep;
	
	Systema archive format is an Event that 'superposes' Scale and Tuning definitions 
		with extras like notation and annotations --> from this information a Notation class 
		will be derived that can analyse a sequence of intervals by a patternmatching
		according to certain initial conditions and specified rules.
	Systemas can easily be created and extended from other Systemas or Scales or Tunings
		*fromScale	or 	aScale.asSystema  
		*fromTuning 	or 	aTuning.asSystema
		*newCopy
	add varios compare, transform, retune and display methods, text and finally gui (.plot ...)

How does notation, and/or key interact here? --> think of all 70 AGM notes as a tuning...

	'unwrapping the wrappings of the wrappers' <-> vs. 'recursion' that deletes history? 
	wrappings pile up by inheritance and the source is still the good old schicksal of 	all wrappers wrappings.
	*/
	/*@
	shortDesc: enables convenient handling of 'rational' and 'non-cyclic' scales
	longDesc: Scale representaion and repository with an interface to the <b>SCL format</b>. 	<br><b>Systema</b>, in contrast to <b>Scale</b> handles 'non-cyclic' or 'non-octave-indentical' 	scales. It supports 'rational' scale definitions by instances of class <b>Ratio</b>. 	<br><br>Compatible with the <b>Scale</b> + <b>Tuning</b> couple, Systema provides a series of 	additional scale creation methods e.g. parasemantic, sturmian words, maximally even, MOS etc.) 	and offers some useful transformation and modulation methods for them. Various annotations to 	each step of a scale can be specified and stored on disk.
	seeAlso: Scale, RCTuning, Ratio 						<br>Examples --> Archytas_Stoichos, Archytas_Stoichos_Systema, Systema_posts 
	issues: <b>Systema extends the default pitch event.</b>
	
	all: IdentityDictionary with all instances stored by its name symbol.
	systemDirs: Set of system-wide dirs to look for systemata or SCL files. Default dir is 		SYSTEMATA in [Platform.systemAppSupportDir].
	userDirs:  Set of user-owned dirs to look for systemata or SCL files. Default dir is SYSTEMATA 		in [Platform.systemAppSupportDir].
	defaultFolder: String. Any folder containing string within sytemDirs or userDirs will be loaded 		after compile.
	defaultCodes: Set of default encodings known to CodePage.
	copySymbols: keys to those entries in globDict that will be copied by copy methods.
	equalityPrecision: round off value (0.001) used to test for equality of scales.
	centPrec: round off value to identify steps of a scale.
	verbose: (boolean) controls post behaviour. 
	isLdPost: (boolean) whether to post loaded files, or not.
	
	instDesc: Instance Methods
	longInstDesc: See also instance vars of Scale.
	rootIndex: Offset to the list of degrees. All 'degrees' of the systema are relative to this 	offset, enabling for negative degrees. The rootIndex maybe directly set by an Integer, or 	indirectly by the method 'rootDegree_' (see below).
	tonalCenter: An offset possibly diffrent from root of scale (== degree 0). TonalCenter need not 	be contained in degrees, but must be an index matching a step of tuning. TonalCenter maybe set 	directly by an Integer, or by 'tcDegree_'. Options of arguments are those of 'rootDegree_'.	(see below).
	midiRoot: default absolut pitch of rootIndex specified by a midiNote - Default is 60.0.
	midiOffset: default relative pitch offset of rootIndex specified by a midiNote - Default is 0.0.
	globDict: global Event for all sorts of meta-data, including post-options and 'stepCodes' known 	to CodePage.
	stepDicts: List of Events with meta-data for each step of a Systema, including UTF8 code 	strings known by CodePage.<br><br>Also refer to example file 'testSystema.systema'.
	@*/
/*
ACH(Systema)
*/
	
// Prefs
	classvar <>centPresc= 0.1; // <>equalityPrecision= 0.001; //test for equality of scales
	classvar systemaFolderName="SYSTEMATA", <fileEnding="systema", uniquePefix="V0";
	classvar defaultFolder="DEFAULT", <>defaultCodes=#[\agmIns, \agmVoc], <>isLdPost;
	classvar <>copySymbols = #[\prtLegend, \prtFormat, \stepCodes], <>unixPst=false;

	classvar <all, <count=0, <systemDirs, <userDirs;
	
	// var <degrees, <pitchesPerOctave, <tuning, <>name; => instance vars from Scale
	var <rootIndex=0, <tonalCenter, <midiRoot, <midiOffset, <>stepDicts, <>globDict, <>fullPath;
// Defaults
	var <>noMatchWarn = false, <>noMatchBeep = true; // <>logOn = false;

	*initClass { 
		Class.initClassTree(Event);  // Class.initClassTree(CodePage);
		this.overwriteDefaultEvent; // in extra File: Systema_pitchEvent.sc
		all = IdentityDictionary.new;
		defaultCodes = defaultCodes.asSet;
		systemDirs = Set.new; userDirs = Set.new;
		StartUp.add({ isLdPost = this.isLog(2); this.initFileDirs; 
			SystemaGui.new; // SystemaTableauGui.new; SYSTabGui.new
		});
		NotificationCenter.registerOneShot(CodePage, \ready, this, {this.loadDir(defaultFolder);
			this.addMenueLibrary;
		});
	}
	*addMenueLibrary {
		Platform.case(\osx, {
			SCMenuSeparator(CocoaMenuItem.default);
			SCMenuItem(CocoaMenuItem.default, "Systema.Gui").action_({ SystemaGui.new })
		})
	}
	*doesNotUnderstand { |selector ...args| //recall or creation of a Scale with conversion
		if (all[selector].isNil) { 
			^super.doesNotUnderstand(selector, *args).asSystema;
		}{ 
			^if (args[0].isNil) { all[selector] }{ all[selector].tuning_(*args) }
		}
	}
	deepCopy{|argName| 
		^this.class.new(degrees.copy, 
			if(tuning.isCyclic) {nil} {0}, tuning.copy(argName), argName ? name, rootIndex,
		 	tonalCenter, midiRoot.copy, midiOffset.copy, nil, globDict.deepCopy, stepDicts.deepCopy);
	}
	copy{|argName| ^this.prCopy(argName) 
		/*@ ex:
			Systema.ionian.copy("hey").postli.play
			Systema.testSystema.copy.postld.play
			<br>
			Systema.testSystema.copy("kiekste").postld
			Systema.testSystema.deepCopy.postld
			Systema.testSystema.deepCopy("kiekste").postld
			<br>
			Systema.testSystema.copy.asCompileString
		@*/
	}
	prCopy{|argName, indices|
		indices !? { indices = this.checkIndices(indices) };
		^this.class.new(indices ?? { degrees.copy }, 
			if(tuning.isCyclic) {nil} {0}, tuning, argName ? name, 
			if (indices.notNil) { indices.indexOf(rootIndex) ? 0 }{ rootIndex },
		 	tonalCenter, midiRoot.copy, midiOffset.copy, nil, 
		 	if (indices.notNil) {nil} {globDict.deepCopy}, 
		 	if (indices.notNil) {nil} {stepDicts.deepCopy} )
	}
	copyByIndices{|indices, argName| ^this.prCopy(argName, indices) 
		/*@ ex:
			SYS.testSystema.copyByIndices([13,23,33], "cpByIndices").postld // new rootIndex 1
			SYS.testSystema.copyByIndices([13,24,33], "cpByIndices").postld // root does not match
		@*/
	}
	copyByDegrees{|degrees, argName| ^this.copyBy(degrees, argName) //@ desc: same as copyBy
	}
	copyBy{|items, argName| 
			// if (items.every{|i| i.isKindOf(Integer)}) { items = items + rootIndex};
			^this.prCopy(argName, this.items2Degrees(items).flat.collect{|i| i !? {i+rootIndex} })
		/*@ ex:
			SYS.testSystema.copyBy([-10,0,10], "cpByDegs").postld // new rootIndex 1
			SYS.testSystema.copyBy([-10,1,10], "cpByDegs").postld // rootIndex does not match -> 0
				
			SYS.testSystema.copyBy([ 112/|81, 1/|1, 32/|45], "cpByRatios").postld
			SYS.testSystema.copyBy([561.0, 0.0, -590.2], "cpByFloats").postld
			SYS.testSystema.copyBy([ "ùç¥", "ùçú", "ùçû" ], "cpByCodes").postld
			SYS.testSystema.copyBy([ "ùç¥ùçúùçû" ], "cpByCodes").postld
			SYS.testSystema.copyBy([ "ùàî œπ ¢å∑" ], "cpByCodes").postld
		@*/
	}
	checkIndices{|indices|
		^indices.select{|index, i| 
			if (index.isKindOf(Integer)) {
				if ((index > 0) && (index < degrees.size)) { true
				}{("index" + index + "at" + i +"is out of range, -> ignoring").warn; false}
			}{
				("index at" + i + "is not an Integer, --> ignoring").warn; false
			}
		}
	}
	storeArgs { 
		^[degrees, if(tuning.isCyclic) {nil} {0}, tuning, name, rootIndex, tonalCenter, midiRoot,
			midiOffset, nil, globDict, stepDicts] 
	}
	storeOn {|stream| //must overload Scale here
		stream << this.class.name;
		this.storeParamsOn(stream);
	}
	== { |sys|
//		var test;
		if (sys.isNil) { ^false
		}{ 
		//this.logln("midiRoot"+ [midiRoot, midiRoot.class, sys.midiRoot, sys.midiRoot.class]);
//			test = (this.degrees == sys.degrees) && ( tuning == sys.tuning ) 
//			&& (tonalCenter == sys.tonalCenter) && (midiRoot == sys.midiRoot);
//			if (test.not) { this.logln("••••••••••••••• false") };
//			^test 
			^name == sys.name // instead of hash
		}
	}
	*new {|steps, pitchesPerOctave, tuning, name, root, tc, //rootIndex, tonalCenter as name does
			midiRoot, midiOffset, octaveRatio, globDict, stepDicts ...stepCodes|
		if (steps.isKindOf(Symbol)) { if (all[steps].notNil) { // quick access syntax surgar
			if(pitchesPerOctave.isKindOf(Symbol) || pitchesPerOctave.isKindOf(Tuning)) { 
				^all[steps].tuning_(pitchesPerOctave, tuning, name) }{ ^all[steps] }
		}{ ("could not retrieve Systema of name:" + steps).warn; ^nil }}; 
		^super.new.midiRoot_(midiRoot ? 60.0).midiOffset_(midiOffset ? 0.0)
			.globDict_(globDict ?? {Event.new})
			.stepDicts_(stepDicts) // wrong: {Array.fill(steps.size, {Event.new}) maybe a String
			.initSystema(steps, pitchesPerOctave, tuning, name, root, tc,
				octaveRatio, stepCodes)
		/*@
		steps: 5 casses of usage depending on whether steps is <br>		(1) a List of Integers -- interpreted as the degree of a scale like in Scale.<br>		(2) a List of SimpleNumber that contains a Ratio => rational-mode, degrees are derived.		<br>(3) a List of SimpleNumber that contains Floats => cents-mode, degrees are derived.		<br>In this 3 cases also UTF8 codes known to CodePage are allowed which will be look up		in the stepDicts, or the stepDicts of the Systema with the same name as the given Tuning.		<br>(4) a String of UTF8 codes. More convenient than to split code strings by commas like 		in the cases 1-3.<br>								(5) a Symbol -- syntactic sugar to retrieve a Systema by name (similar to a pseudo 		method). In this case the 3 follwing arguments take the function of those of method 		'tuning_'.
		pitchesPerOctave: an Integer -- specifying the pitches per octave in a scale. If tuning is 		nil and values of the steps argument are degees (i.e. case 1), an equal tempered scale 		with the number of pitches per octave is computed as default tuning. In all other cases 		the number of pitchesPerOctave is derived and the argument is ignored. However if 		0, a systema's scale is considered to be non-cyclic.
		tuning: a Tuning or Symobl specifying a tunings name. Tunings are looked up first in all		known Systemata, then in TuningInfo.
		name: a String or Symbol. Name may entail a discription separated by a colon.
		root: Integer offset of rootIndex. If nil, the degree of tuning 0 cents or 1/1 (if 		rational)	is searched to determine the instance var 'rootIndex' (see below). 		Alternativeley a Ratio, Float or string may be used like in 'rootDegree_' (see below).
		tc: Integer offset of tonalCenter. If nil, it is set to the rootIndex, and thus refers to 		the rootDegree (always 0). Alternativeley a Ratio, Float or string may be used like in 		'rootDegree_' (see below).
		midiRoot: see Instance Variables - Default is 60.0.
		midiOffset: see Instance Variables - Default is 0.0.
		octaveRatio: a SimpleNumber to overwrite the octaveRatio of a Tuning. If nil, it will be		derived either from steps or tuning.
		globDict: see Instance Variables and example file testSystema.systema
		stepDicts: see Instance Variables and example file testSystema.systema
		stepCode: a Symbol, Symbols, or a Collection of Symbols specifing the UTF8 codes to be		used in order to replace the Strings given in the steps List. If nil, all availlable		encodings are used as given by (1st) the stepDicts or (2nd) the stepDicts of the tuning of		a Systema referenced	by name as Sybol in the tuning argument.
		
		ex: 
		Systema([1,2], name: \SimpleName) //note: leading capitals will be converted to minuscule.
		Systema([0,14],name: "Any Name: with a global discription of this systema")
			<br>
		Systema.ionian
		Systema.ionian(\just) // quickly apply a Tuning
		Systema.unknown // throws an error -> not nice, and no suitable way to create a new Systema
			<br>
		Systema(\ionian) // just another quick access method
		Systema(\ionian, \et19) // and also quickly change the tuning
		Systema(\unknown)
			<br>
		Systema([1,2/|1], 0, \et24).postl		// non-cyclic -- last degree matches tuning
		Systema([1,2/|1], tuning: \et24).postl	// cyclic -- last degree does not match tuning
		Systema([0.0, 600], nil, \et24).postli.play(0.4, (1..6)) //octaveRatio has only effect when 		cycling
		Systema([0.0, 600 ], nil, \et24, octaveRatio: 0.5).postli.play(0.4, (1..6)) // thus, also on 		direction 
			<br>
		Systema("ùàâ œπ ùàö ùçöùç∑ùçúùà¨", 0, \testSystema, 			"new SYS: from code string").postlid.play
		Systema(["ùàâ", 2, "œπ", "ùàö", 1], 0, \testSystema).postlid.play // specify degree by		degrees
		Systema(["ùàâ", 2, "œπ", "ùàö", 1/|1], 0, \testSystema).postlid.play // by Ratios
		Systema(["ùàâ", 1200, "œπ", "ùàö", 0.0], 0, \testSystema).postlid.play //by cents
		@*/
	}
	*fromEvent{|ev| // any preProcessing, checking here?
		^this.new( *[\steps, \pitchesPerOctave, \tuning, \name, \rootIndex, \tonalCenter,
			\midiRoot, \midiOffset, \octaveRatio, \globDict, \stepDicts, \stepCodes]
				.collect{|key| ev[key]})
	}
	/* ...>>> better take as Systema as instantance var of ScaleLoop that sits at the background
		as the unchanging prototype of a scaleLoop whoose deviations are reflected as imag values
	*fromSL {|scaleLoop, octaveRatio, rootIndex, tonalCenter, midiRoot=60.0, midiOffset=0.0|
		//@ scaleLoop: ... this will import a subclass of Loop called ScaleLoop
		// check if scaleLoop is Symbol or a ScaleLoop --> to be able to write: S.fromSL(\loop)
	}
	*fromL {|scaleLoop, octaveRatio, rootIndex, tonalCenter, midiRoot=60.0, midiOffset=0.0|
		//@ scaleLoop: ... this will import a Loop and create a ScaleLoop in between
		// a S(\anyScale).asSL will export a ScaleLoop
	}
	*/
	*at { |name| //@ name: a Symbol to retrieve an instance from classvar all by name.
		^all.at(name) 
	}
	*remove {|name| //@ name: a name symbol to remove an instance from classvar all.
		var instance = all.removeAt(name);
		if (instance.notNil) { this.changed(\destroy, name)
		}{ ("could not remove:" + name).warn };
		^instance
	}
	remove { this.class.remove(name) }
	
	*removeAll { //@ desc: remove all instances from classvar all.
		//all = IdentityDictionary.new; 
		all.keys.do{|key| this.remove(key) }
	}
	
	*posta { //@ desc: 'post all' present Systemata with name, tuning name and description.
		all.do{|item| if (item.globDict[\desc].notNil)
			{(item.asString ++ "\n\t" ++ item.globDict[\desc] ++ "\n").postln} 
		 	{item.asString.postln};
		}
	}
	//	*plota {}, etc..
	
	// ------------------------------- class load/save methods ----------------------------------
	*initFileDirs {
		//this.addSystemDir(Platform.systemAppSupportDir +/+ systemaFolderName);
		this.addUserDir(Platform.userAppSupportDir +/+ systemaFolderName);
		//this.createFileDir(Platform.systemAppSupportDir +/+ systemaFolderName +/+ defaultFolder);
		this.createFileDir(Platform.userAppSupportDir +/+ systemaFolderName +/+ defaultFolder);
	}
	*addSystemDir {|dir| 
		if(dir.notNil) { systemDirs.add(dir); this.checkSystemDirs }
		/*@ 	desc: add a system-wide dir to look for .systema or .scl files 
			dir: full path as String (need not to be escaped) -@*/
	}
	*addUserDir {|dir| 
		if(dir.notNil) { userDirs.add(dir); this.checkUserDirs }
		/*@ 	desc: add a user-owned dir to look for .systema or .scl files 
			dir: full path as String (need not to be escaped) -@*/
 	}
	*checkSystemDirs {
		// systemDirs.copy.do{|dir| if (File.exists(dir).not) {systemDirs.remove(dir)} };
	}	
	*checkUserDirs {
		var callback = {|ok, dir| if (ok.not) {userDirs.remove(dir)}; };
		userDirs.copy.do{|dir| if (File.exists(dir).not) {this.createFileDir(dir, callback)} };
	}
	*createFileDir {|dir, cb|
		if (thisProcess.platform.isKindOf(UnixPlatform).not) {
			("*•do not know how to create a new folder on this platform").warn;
			this.callback(cb, false, dir);
		}{
			("mkdir -p -v" + dir.escapeChar($ )).unixCmd({|res|
				if (res != 0) { ("*• could not create directory:" + dir).warn; 
					this.callback(cb, false, dir); } { this.callback(cb, true, dir) }
			}, unixPst)
		}
	}
	*callback {|callback ...args| //callback must be either a Symbol or Function
		if (callback.isKindOf(Symbol)) {this.perform(callback, *args)} {callback.value(*args)} 
	}
	*load {|path, post, warn=true|
		post = post ? isLdPost;
		if (path.notNil) { ^this.prLoad(PathName(path.asString),fileEnding, post, warn )
		}{ File.openDialog(nil, {|path| ^this.prLoad(PathName(path), fileEnding, post, warn)}) }
		/*@ path: a file. If path is nil a file dialog is invoked -
		post: a Boolean -- whether paths should be posted
		ex:
		Systema.load("TEST/testSystema.systema") 
		// searches for testSystema.systeama in folder TEST of *userDirs and *systemDirs
		// only one parent folder maybe specified, otherwise all subdirs of SYSTEMATA is searched. 
		// if you drop the file extension, all files containing the file name string are found:
		Systema.load("TEST/testSystema") 
		Systema.load("test") 	// all systema files containing the string test are loaded
		@*/
	}
	*loadDir {|dir, post, warn=true|
		post = post ? isLdPost;
		if (dir.notNil) { ^this.prLoadDir(PathName(dir.asString), fileEnding, post, warn)
		}{ File.openDialog(nil, {|dir| ^this.prLoadDir(PathName(dir), fileEnding, post, warn)}) }
		/*@ dir: a directory. If dir is nil a file dialog is invoked -
		post: a Boolean -- whether paths should be posted
		ex:
		Systema.loadDir("TEST") // loads subdirs of *userDirs and *systemDirs incl. String TEST
			// eg: 
		Systema.loadDir("/Users/admin2/Library/Application Support/SuperCollider/SYSTEMATA/TEST") 
		@*/
	}
	*prLoad {|pathN, ext, post, warn| 
		if (pathN.isFile) 
			{^this.prLoadFile(pathN, ext, post, warn) }{^this.prSearchFile(pathN, ext, post, warn) } 
	}
	*prLoadDir {|pathN, ext, post, warn| 
		if (pathN.isFolder) 
			{^this.prLoadFolder(pathN, ext, post, warn) }{^this.prSearchDir(pathN, ext, post, warn)} 
	}
	*prSearchFile {|pathN, ext, post, warn| 
		var folder, files=List.new, return=List.new;
		if(pathN.isFile) { ^this.prLoadFile(pathN, ext, post, warn) } {
			folder = PathName(""+/+pathN.fullPath).allFolders.last;
			if (folder.notNil) { 
				(userDirs.asList ++ systemDirs.asList).do{|dir| 
					PathName(dir).filesDo{|pathName| 
						if ( (pathName.allFolders.last == folder) 
						&& (pathName.fileName.contains(pathN.fileName)) ) {files.add(pathName)}
					}
				}
			}{
				(userDirs.asList ++ systemDirs.asList).do{|dir| 
					PathName(dir).filesDo{|pathName| 
						if (pathName.fileName.contains(pathN.fileName)) {files.add(pathName)}
					}
				}
			};
			files = files.select{|pathName| pathName.extensionAt(ext.findAll(".").size+1)==ext};
			if (files.isEmpty) { ("file not found:" + pathN.fileName).warn 
			}{ 
				files.do{|path| return.add( this.prLoadFile(path, ext, post, warn) )};
				^return
			}
		}
	}
	*prLoadFile {|pathN, ext, post, warn| 
		var file, res, fullPath = pathN.fullPath, sym = pathN.fileNameWithoutExtension.asSymbol;
		if (Systema.at(sym).notNil) { if (warn) { ("already loaded: Systema." ++ sym).warn;
			("load path attempted:" + fullPath + "\n").postln }; ^Systema.at(sym) 
		}{
			if (ext == fileEnding) {
				res = fullPath.load;
				if (post) {this.prPstL(fullPath, ext)};
				if (res.isKindOf(Event)) { res = this.fromEvent(res) };
				^res.fullPath_(fullPath)
			}{ 
				file = File(fullPath,"r");
				res = file.readAllString;
				file.close;
				if (post) {this.prPstL(fullPath, ext)};
				^this.prInterpret(res, pathN)
			}
		}
	}
	*prPstL {|fullPath, ext| ("*" + ext +"* loaded from:" + fullPath).postln }
	*prSearchDir{|pathN, ext, post, warn| var files=List.new, return=List.new;
		var folder=pathN.fileName;
		(userDirs.asList ++ systemDirs.asList).do{|dir| 
			PathName(dir).filesDo{|pathName| 
				if (pathName.extensionAt(fileEnding.findAll(".").size+1) == ext) {
					if (pathName.allFolders.any{|f| f.contains(folder)}) {files.add(pathName)}};
			};
		};
		if (files.isEmpty) { ("no files found in dir or folder:" + folder).warn 
		}{ 
			files.do{|path| return.add(this.prLoadFile(path, ext, post, warn))};
			^return
		}
	}
	*prLoadFolder{|pathN, ext, post, warn|
		var return = List.new;
		pathN.filesDo{|pathName| 
			if (pathName.extensionAt(fileEnding.findAll(".").size+1) == ext) {
				return.add(this.prLoadFile(pathName, ext, post, warn))
			}
		}
		^return.flat
	}
	*loadAll {|post, warn=true|
		var return=List.new;
		post = post ? isLdPost;
		(systemDirs | userDirs).do{|dir| 
			return.add( this.prLoadFolder(PathName(dir), fileEnding, post, warn) )};
		^return.flat
		/*@ desc: load all Systemata in *systemDirs and *userDirs
		post: a Boolean -- whether paths should be posted
		ex:
		Systema.loadAll(false) //load all don't post
		@*/
	}
	*importSCL {|path, post|
		post = post ? isLdPost;
		if (path.notNil) { ^this.prLoad(PathName(path.asString),"scl", post )
		}{ File.openDialog(nil, {|path| ^this.prLoad(PathName(path), "scl", post)}) }
		/*@ path: a file. If path is nil a file dialog is invoked -
		post: a Boolean -- whether paths should be posted
		ex:
		Systema.importSCL("TEST/testSCL.scl") 
		// searches for arch_enh.scl in folder SCL of *userDirs and *systemDirs
		// only one parent folder maybe specified, otherwise all subdirs of SYSTEMATA is searched. 
		// if you drop the file extension, all files containing the file name string are found:
		Systema.importSCL("TEST/testSCL")
		Systema.importSCL("testSCL") // all files which contain the string arch_enh are loaded
		@*/
	}
	*importSCLdir {|dir, post|
		post = post ? isLdPost;
		if (dir.notNil) { ^this.prLoadDir(PathName(dir.asString), "scl", post)
		}{ File.openDialog(nil, {|dir| ^this.prLoadDir(PathName(dir), "scl", post)}) }
		/*@ dir: a directory. If dir is nil a file dialog is invoked -
		post: a Boolean -- whether paths should be posted
		ex:
		Systema.importSCLdir("SCL") // loads subdirs of *userDirs and *systemDirs containing "SCL"
		@*/
	}
	*importSCLall {|post|
		var return=List.new;
		post = post ? isLdPost;
		(systemDirs | userDirs).do{|dir| return.add(this.prLoadFolder(PathName(dir), "scl", post))}
		^return.flat
		/*@ desc: import all SCL files in *systemDirs and *userDirs
		post: a Boolean -- whether paths should be posted
		ex:
		Systema.importSCLall
		@*/
	}
	*prInterpret {|str, pathN|
		var pPO, oR, steps=List[0.0], notes=List.new;
		var lines = str.split($\n).reject{|line| line[0] == $!}; //.do{|line, i| [i, line]};
		var name=lines[0].copyRange(0, lines[0].indexOf($:) ?? {(lines[0].size-1)});
		var desc = lines[0].copyRange(name.size+1, lines[0].size-1);
		var isRational, null;
		name = this.stripSpaceR(name.reject{|char| #[$\f,$\t,$\r,$:].any{|c| char == c}});
		try {
			if ("[A-Za-z]".matchRegexp(name).not && "[0-9]".matchRegexp(name)) { 
				pPO = name.interpret.asInteger +1;
				name = desc = nil;
				lines = lines.drop(1)
			}{
				pPO = lines[1].interpret.asInteger + 1;
				if (desc[0] == $ ) { desc = desc.drop(1) };
				desc = this.tryExpand2Dict(desc, \desc);
				lines = lines.drop(2)
			};
			lines.do{|line, i| 
				var step, splitPos = line.findRegexp("[^ 0-9./]").first ?? {(List[line.size])};
				step = line.copyRange(0, splitPos[0]-1);
				if ("[0-9]".matchRegexp(step)) {
					if (step.includes($.)) { 
						steps.add(step.interpret)
					}{ 
						step = step.split($/); 
						steps.add( Ratio(step[0].asInteger, (step[1] ? 1).asInteger))
					};
					notes.add( this.tryExpand2Dict(line.copyRange(splitPos[0], line.size-1)) );
				}
			};
		}{|error| ("coud not interpret SCL file, ignoring:\n"++pathN.fullPath++"\n").warn;
error.throw;
			^nil};
		if (steps.size != pPO) {
			("inconsistent number of steps ("++steps.size++") in expected scale size ("
				++pPO++"), ignoring:\n"++pathN.fullPath++"\n").warn; ^nil
		}{ 
			if (desc[\dropFirst] == true) { steps = steps.drop(1); pPO = pPO-1};
			if (isRational = steps.any{|s| s.isKindOf(Ratio)}) {
				steps = steps.collect{|s| if (s.isKindOf(Ratio).not) {s.centratio.asRatio} {s} }; 
				oR = steps.last / steps.first
			}{
				oR = (steps.last - steps.first).centratio
			};
			if (desc[\isCyclic] == false) {null = 0} {
				steps.pop; pPO = pPO-1; notes.addFirst(notes.pop)};
			if (name.isNil || name == "") { name = pathN.fileNameWithoutExtension };
			^this.new(Array.series(pPO,0,1), null, RCTuning(steps, oR, name,isRational,null==nil), 
				name, desc[\rootIndex], desc[\tonalCenter], desc[\midiRoot], desc[\midiOffset],
				desc[\octaveRatio], desc, notes, desc[\stepCodes] ? #[]).fullPath_(pathN.fullPath)
		}
	}
	*stripSpaceR{|str|
		var n=0;
		str.reverseDo{|c| if (c == $ ) {n = n+1} };
		^str.copyRange(0, str.size-n)
	}
	*tryExpand2Dict{|str, default=\raw|
		var dict = Event.new;
		try {
			str.replace("\t", "").replace(": ", ":").replace("\r ", "\r")
				.split($\f).collect{|pair| pair.split($:)}.do{|l| 
					dict.put(l[0].replace(" ", "").asSymbol, l[1].interpret )
			};			// e.g. "name:" still causes "Open ended String..." warning 
		}{ ("could not expand SCL comment:" + str).warn; ^Event.new.put(default, str) };
		if (dict.isEmpty && str.every{|c| c.isPrint.not || c.isSpace}.not) { 
			^Event.new.put(default, str) } {^dict}
	}
	encodeDict{|dict|
		var str="";
		dict.keysValuesDo{|key, val| str = str++"\f\t\t"++key++":\t\t"++val.asCompileString};
		^str
	}
	*saveAll {|dir="SAVED"|
		this.prSaveAll(dir, \prSaveCS)
		/*@ dir: any absolute or relative dir to [Platform.userAppSupportDir +/+ "SYSTEMATA"] -
		ex:
		Systema.saveAll
		Systema.loadDir("SAVED")
		Systema.saveAll("Tests/SaveTest")
		Systema.loadDir("SaveTest")
		@*/
	}
	*exportSCLall {|dir="SAVED_SCL"|
		this.prSaveAll(dir, \prSaveSCL)
		/*@ dir: any absolute or relative dir to [Platform.userAppSupportDir +/+ "SYSTEMATA"] -
		ex:
		Systema.exportSCLall
		Systema.importSCLdir("SAVED_SCL")
		Systema.exportSCLall("Tests/SaveTest SCL")
		Systema.importSCLdir("SaveTest SCL")
		@*/
	}
	*prSaveAll{|dir, m|
		var folder = PathName(""+/+dir+/+"");
		if (folder.isFolder.not) {
			dir = Platform.userAppSupportDir +/+ systemaFolderName +/+ folder.fullPath;
			this.createFileDir(dir, {|ok, dir| if (ok) { all.values.do{|s| s.prSave(dir,m)}}} )};
	}
	*makePathTree {
		var addON = List.new;
		var pathTree = MultiLevelIdentityDictionary.new;
		(userDirs.asList ++ systemDirs.asList).do{|dir| 
			var pn = PathName(dir);
			var size = pn.allFolders.size;
			pn.deepFiles.select{|path| path.extension == fileEnding }.do{|path|
				pathTree.put( *path.allFolders.drop(size).add(path.fileName)
					.collect{|str| str.asSymbol}.add( () ) )
			}
		};
		pathTree.treeDo({|branch| if (branch.isEmpty.not 
			&& (branch.last.asString.split($.).last != fileEnding)) {
				addON.add(branch) };
		});
		addON.do{|list| pathTree.put(*list.addAll(['All:', ()])) };
		^pathTree;
	}
	// ------------------------------ instance load/save methods ----------------------------------
	save{|dir="SAVED"|
		if (fullPath.notNil) { this.prWriteFile(fullPath, this.asCompileString)
		}{ this.prSave(dir, \prSaveCS) }
		/*@ dir: any absolute or relative dir to [Platform.userAppSupportDir +/+ "SYSTEMATA"] -
		ex:
		Systema.load("TEST/testSystema.systema")
		Systema(\testSystema).save
		Systema.load("SAVED/testSystema.systema")
		<br>
		Systema(\testSystema).save("Tests/SaveTest2")
		Systema.load("SaveTest2/testSystema")
		<br>
		Systema.importSCL("TEST/testSCL")
		Systema(\testSCL).save("Tests/SaveTest3")
		Systema.load("SaveTest3/testSCL")
		@*/
	}
	exportSCL{|dir="SAVED_SCL"| 
		this.prSave(dir, \prSaveSCL)
		/*@ dir: any absolute or relative dir to [Platform.userAppSupportDir +/+ "SYSTEMATA"] -
		ex:
		Systema.importSCL("TEST/testSCL.scl")
		Systema(\testSCL).exportSCL
		Systema.importSCL("SAVED_SCL/testSCL.scl")
		<br>
		Systema(\testSCL).exportSCL("Tests/SaveTest2")
		Systema.importSCL("SaveTest2/testSCL")
		<br>
		Systema.load("TEST/testSystema.systema")
		Systema(\testSystema).exportSCL("Tests/SaveTest3")
		Systema.importSCL("SaveTest3/testSystema")
		@*/
	}
	prSave{|dir, method|
		var folder = PathName(""+/+dir+/+"");
		if (folder.isFolder.not) {
			dir = Platform.userAppSupportDir +/+ systemaFolderName +/+ folder.fullPath;
			this.class.createFileDir(dir, {|ok, dir| if (ok) { this.perform(method, dir) } } ) 
		}{ this.perform(method, dir) }
	}
	prSaveCS{|dir| this.prWriteSYS(dir, fileEnding, this.asCompileString) }
	prSaveSCL{|dir|this.prWriteSYS(dir, "scl", this.makeSCLstr) }
	prWriteSYS{|dir, ext, str|
		^this.prWriteFile(dir +/+ name ++ "." ++ ext, str)
	}
	prWriteFile{|fullPath, str|
		var res, file;
		file = File(fullPath, "w"); 
		if (file.isOpen) { 
			res = file.write(str);
			file.close;
		}; // this.logln("saved:" + [fullPath, res]);
		^res;
	}
	
	makeSCLstr {
		var steps, str, df=0;
		if (tuning.isRational) {
			steps = this.scaleIndices.collect{|deg| tuning.degToR(deg).asString.replace("|","") };
			if (steps.first == "1/1") {
				steps=steps.drop(1); df=1 } { globDict.put(\dropFirst,true) }
		}{
			steps = this.scaleIndices.collect{|deg| tuning.degToC(deg).asCompileString };
			if (steps.first == "0.000000") {
				steps = steps.drop(1); df=1 } { globDict.put(\dropFirst,true) }
		};
		#[\rootIndex, \tonalCenter, \midiRoot, \midiOffset, \octaveRatio].do{|key| 
			globDict.put(key, this.perform(key)) };
		str = "!" + name ++ ".scl\n!\n" ++ name + ":\tisCyclic:" + tuning.isCyclic 
			++ this.encodeDict(globDict) ++ "\n!\n" + (this.scaleIndices.size-df) ++ "\n!\n";
		steps.do{|step, i| str = str ++ step + this.encodeDict(stepDicts.wrapAt(i+df)) ++ "\n" };
		^str
	}
	//------------------------------------ instance methods ---------------------------------------
	init { //overload init because it is called with wrong args by *new of super class Scale)
	}
	initSystema{|inSteps, inPPO, inTuning, inName, inRoot, inTC, inOctRatio, codes|
		var desc, steps, isCyclic=true;

		if (inSteps.isNil) { "Cannot create Systema if steps are nil".warn; ^nil};
		if (inName.isNil || (inName == "")) { inName = "untitled" };
		inName = inName.asString;
		name = inName.copyRange(0, inName.indexOf($:) ?? {(inName.size-1)}).replace(":","");
		globDict[\name] ?? {globDict[\name]=name}; // keep original name as String
		desc = inName.copyRange(name.size+1, inName.size-1);
		globDict[\desc] ?? {globDict[\desc]= if (desc[0] == $ ) {desc.drop(1)} {desc} };
		name = this.makeUniqueName(name); //all.put(name, this); count = count + 1; //not yet!
		globDict[\stepCodes] ?? { globDict.put(\stepCodes, Set.with(*codes)) };
//this.logln("globDict:\n" ++ globDict);
		if (inPPO == 0) { isCyclic = false; inPPO = nil }; // is cyclic?
		tuning = inTuning !? { inTuning.asRCTuning(isCyclic) };
		tuning !? { inTuning = tuning.name }; //keep original name of tuning;
//this.logln("tuning:" + [isCyclic, tuning] );
		if (inSteps.isKindOf(String)) { inSteps = inSteps.takeUTF8 };
		if (tuning.notNil && inSteps.any{|step| step.isKindOf(String)}) { // convert steps
			#degrees, steps = this.code2DegreesSteps(inSteps, *codes) 
		}{
			steps = inSteps; 
		};
		case // 1st case (all degree) is the only case where a tuning cannot be derived from steps
		{ inSteps.select{|step| step.isKindOf(String).not}.every{|step| step.isKindOf(Integer)} }{
			this.prConvSteps2Degrees(steps, inOctRatio, inPPO, isCyclic) }
		{ steps.any{|step| step.isKindOf(Ratio)} }{ 
			this.prConvSteps2Ratio(steps, inOctRatio, isCyclic) }
		{ this.prConvSteps2Cents(steps, inOctRatio, isCyclic) };
		
		stepDicts = stepDicts ?? { Array.fill(degrees.size, {Event.new}) };
		if (all[inTuning].notNil) {
//this.logln("degrees, all[inTuning]" + degrees + "\n" + all[inTuning].stepDicts);
			degrees.do{|deg, i| all[inTuning].stepDicts[deg].keysValuesDo{|key, val|
				stepDicts[i][key] = stepDicts[i][key] ?? { val.deepCopy } } };// not overwrite !! 
			copySymbols.do{|key| // automatic copy of some entries --> see classvar
				globDict[key] = globDict[key] ?? {List.new}; //stepCodes is Set and exits
				globDict[key].addAll( all[inTuning].globDict[key] ) }
		};
		all.put(name, this); count = count + 1;
//this.logln("degrees:\n" + degrees + "\n");
		if (inRoot.isKindOf(Integer)) { this.rootIndex_(inRoot)
		}{ this.rootDegree_(inRoot ?? { degrees.indexOf(tuning.findRootIndex) } ? 0 ) };
		if (inTC.isKindOf(Integer)) { this.tonalCenter_(inTC)
		}{ this.tcDegree_(inTC ? 0) };
		this.class.changed(\new, name);
	}
	makeUniqueName{|key| var str, i;
		key = key.asString;
		#[" ", "'", "/", "\t", "\f", "\r", "\n"].do{|str| key = key.replace(str, "")}; // "_" allowed
		if (key.first.isUpper) { key[0] = key.first.toLower };
		key = key.asSymbol;
		if(all.at(key).isNil && this.class.methods.any{|m| m.name == key}.not) 
			{ ^key } {
				str = key.asString;
				i = str.findBackwards(uniquePefix);
				if (i.notNil) { 
					i=i+uniquePefix.size; 
					^this.makeUniqueName((str.keep(i) ++ (str.drop(i).asInt+1)).asSymbol)
				}{ 	^this.makeUniqueName((str ++ uniquePefix ++ 1).asSymbol) }
			}
	}
	code2DegreesSteps{|inSteps ...codes|
		var tuningStepDicts, deg;
		codes = codes.flat.asList;
		if (codes.isEmpty) { codes.addAll(all[tuning.name].globDict[\stepCodes] ?? {Set[]}) };
		if (codes.isEmpty) { "no stepCodes specified".warn; ^[deg, inSteps]};
		tuningStepDicts = all[tuning.name] !? all[tuning.name].stepDicts;
		deg = List.newClear(inSteps.size); //works for both: a list or one single string!
		inSteps = inSteps.collect{|step, i| var res, index;
			if (step.isKindOf(String)) {
				step.takeUTF8.do{|utf8| // this.logln("utf8:" + utf8);
					codes.do{|code| // 1st pass in local tuning
						index = (stepDicts ? #[]).detectIndex{|dict| dict[code] == utf8};
						if (index.notNil) { deg[i] = index; res = tuning[index] ? res}
					};
					if (res.isNil && tuningStepDicts.notNil) { codes.do{|code| // 2nd pass 
						index = tuningStepDicts.detectIndex{|dict| dict[code] == utf8};
						if (index.notNil) {  deg[i] = index;
							res = try{tuning[index]}{all[tuning.name].tuning[index]} ? res;
							// globDict[\stepCodes].add(code); //if wanted only the used one
						}
					}}
				};
				res
			}{ step }
		};	
		^[deg, inSteps]
	}
	prConvSteps2Degrees {|steps, inOctRatio, inPPO, isCyclic|
		if (degrees.isNil) { 
			degrees = steps
			//if (steps.notEmpty) {degrees = steps} {degrees = Array[0]} //degrees had no codes
		}{ 
			degrees = degrees.collect{|deg,i| deg ?? {steps[i]}} // superimpose converted degrees
		};
		tuning = tuning ?? {Tuning.default(inPPO??{this.guessPPO}).asRCTuning.isCyclic_(isCyclic)};
		pitchesPerOctave = tuning.size;
		this.tuning_(tuning, inOctRatio);
	}
	prConvSteps2Ratio {|steps, inOctRatio, isCyclic|
		steps = steps.collect{|step, i| case 
			{ step.isKindOf(Ratio) } { step } 
			{ step.isKindOf(Integer) } { step.asRatio }
			{ try { step.centratio.asRatio 
			  }{|error| "could not convert step"+i+"to Ratio -> filling in: 24/|1".warn; 24/|1} }
		};
		if (tuning.isNil) {
			if (inOctRatio.isNil) { inOctRatio = steps.last / steps.first }; //still a Ratio
			tuning = RCTuning.new(steps.deepCopy, inOctRatio, name, true, isCyclic);
			degrees = Array.series(steps.size, 0, 1);
		}{
			degrees = degrees.collect{|deg, i| 
				deg ?? { tuning.ratios.detectIndex{|val| val==steps[i]} } }
				?? { degrees=steps.collect{|step| tuning.ratios.detectIndex{|val| val==step }} }
		};
		pitchesPerOctave = tuning.size;
		this.tuning_(tuning, inOctRatio)
	}
	prConvSteps2Cents {|steps, inOctRatio, isCyclic|
		steps = steps.collect{|step, i| case 
			{ step.isKindOf(Float) } { step } 
			{ step.isKindOf(Integer) } { step.asFloat }
			{ try { step.ratiocent 
			  }{|error| "could not convert step"+i+"to cents -> filling in: 4302".warn; 4302} }
		};
		if (tuning.isNil) {
			if (inOctRatio.isNil) { inOctRatio = (steps.last - steps.first).centratio };
			tuning = RCTuning.new(steps, inOctRatio, name, false, isCyclic);
			degrees = Array.series(steps.size, 0, 1);
		}{
			degrees = degrees.collect{|deg, i| deg ?? {tuning.cents.detectIndex{|val| 
				val==steps[i]}} }
				?? { degrees=steps.collect{|step| tuning.cents.detectIndex{|val| val==step }} }
		};
		pitchesPerOctave = tuning.size;		
		this.tuning_(tuning, inOctRatio)
	}
	
	ratio2Degree{|ratio| ^try { this.ratios.indexOfEqual(ratio) - rootIndex }{ nil } }
	cent2Degree{|cent| 
		^try { this.cents.round(centPresc).indexOfEqual(cent.round(centPresc)) - rootIndex 
		}{ nil } 
	}
	code2Degrees{|str ...codes|
		var degs=List.new;
		codes = codes.flat.asList;
		if (codes.isEmpty) { codes.addAll(globDict[\stepCodes] ?? {Set[]}) };
		if (codes.isEmpty) { "no stepCodes specified".warn; ^degs};
		str.takeUTF8.do{|utf8| codes.do{|code|
			var index = (stepDicts ? #[]).detectIndex{|dict| dict[code] == utf8};
			if (index.notNil) { degs.add(index) } //multi trigger possible -> use for chords?
		}};
		^degs-rootIndex
	}
	rootIndex_{|index|
		if (index.isKindOf(Integer)) {
			if ((index.isPositive ) && (index < degrees.size)) { rootIndex = index
			}{ ("rootIndex of "+index+" out of range --> keeping:" + rootIndex).warn }
		}{  ("rootIndex "+index+" not an Integer --> keeping:" + rootIndex).warn }
	}
	rootDegree { ^0 }
	rootDegree_ {|deg|
		deg !? { deg = this.item2Degree(deg) };
		if (deg.isKindOf(Collection)) { deg = deg.first };
		if (deg.isKindOf(SimpleNumber).not) { 
			("could not determine rootIndex --> keeping:" + rootIndex).warn; ^this};
		this.rootIndex_(deg + rootIndex)
		/*@ desc: set rootIndex indirectly by !! degree !!. The argument can be either (1) an 		Interger of a valid degree present in degrees, or can be looked up in tuning 		via (2) a Float representing cents, (3) a Ratio representing a ratio, or (4) a String,		giving a UTF8 sequence known to CodePage.<br>(Also see sample file testSysteama.systeama.)
			ex:
			Systema.testSystema.rootDegree = -23
			Systema.testSystema.postld
			Systema.testSystema.rootDegree = 1/|1
			Systema.testSystema.postld
			Systema.testSystema.rootDegree = -1200.0
			Systema.testSystema.postld
			Systema.testSystema.rootDegree = "ùçú"
			Systema.testSystema.postld
		@*/
	}
	tonalCenter_{|index|
		if (index.isKindOf(Integer)) {
			if ((index.isPositive ) && (index < tuning.size)) { tonalCenter = index
			}{ ("tonalCenter of "+index+" out of range --> keeping:" + tonalCenter).warn }
		}{  ("tonalCenter "+index+" not an Integer --> keeping:" + tonalCenter).warn }
	}
	tcDegree{ ^tonalCenter - rootIndex //@: desc: TonalCenter as degree, possibly not in degreees.
	}
	tcDegree_ {|deg|
		deg !? { deg = tuning.item2Degree(deg, rootIndex) };
		if (deg.isKindOf(Collection)) { deg = deg.first };
		if (deg.isKindOf(SimpleNumber).not) { 
			tonalCenter = tonalCenter ? rootIndex;
			("could not determine tonalCenter --> keeping:" + tonalCenter).warn; ^this};
		this.tonalCenter_(deg);
		/*@ desc: set rootIndex by degree or CodePage code known by Set at globDict[\stepCodes]
			ex:
			Systema.testSystema.tcDegree = -23
			Systema.testSystema.postld
			Systema.testSystema.tcDegree = 1/|1
			Systema.testSystema.postld
			Systema.testSystema.tcDegree = -1200.0
			Systema.testSystema.postld
			Systema.testSystema.tcDegree = "blaŒ±Œ¥œÜœπùàïdada"
			Systema.testSystema.postld
		@*/
	}
	isCyclic { tuning.isCyclic }
	isCyclic_{|boolean| 
		var size = tuning.size; 
		if (tuning.isCyclic) { 
			tuning.isCyclic_(boolean); if (tuning.size > size) {degrees.add(size)} 
		}{										//what to do with stepDicts ???
			tuning.isCyclic_(boolean); if (tuning.size < size) {degrees.pop}
		}
		/*@ ex:
			SYS.ionian.postl
			SYS.ionian.isCyclic_(false).postl
			SYS.ionian.isCyclic_(true).postl
			<br>
			SYS.testSystema.postld
			SYS.testSystema.isCyclic_(true).postld
			SYS.testSystema.isCyclic_(false).postld
		@*/
	}
	tuning_{|inTuning, inOctRatio, isRational, warn=true|
		if ((inTuning == tuning.name) && inOctRatio.isNil && isRational.isNil) {^this};
		inTuning = inTuning !? {inTuning.asRCTuning};
//this.logln("changeTuning" + [inTuning, inOctRatio, isRational] );
		if (inTuning.isNil) {
			("could not find Tuning -> ignoring...").warn 
		}{
			inOctRatio = inOctRatio ?? { inTuning.octaveRatio };
//			if (inOctRatio.notNil) {
				if ((inTuning.octaveRatio -= inOctRatio).not && (inTuning.name == tuning.name)) {
this.logln("oR, inoR" + [inTuning.octaveRatio,inOctRatio ]);
					tuning = RCTuning.new(
						if (inTuning.isRational) {inTuning.ratios} {inTuning.cents}, 
						inOctRatio, name, inTuning.isRational, inTuning.isCyclic)
					// changing the octaveRatio is a matter of identity -> alter tuning name
				}{
					if(this.checkTuningForMismatch(inTuning).not) { 
						if (warn) { ("Pitches per octave (" ++ pitchesPerOctave
							++ ") do not match tuning size (" ++ inTuning.size
							++ ") -> still changing!").warn }
					};
					tuning = inTuning; // still change it -> diffrent than in Scale!
					pitchesPerOctave = tuning.size; // check sense of this later...
				};
//			};
			if (isRational.notNil) {tuning.isRational_(isRational)}
					//changing the data format is just a matter of representation -> alter flag
		}
	}
	scaleIndices { 
		^if (tuning.isCyclic) {degrees ++ tuning.size} {degrees}
	}
	scaleDegrees { //@ desc: like 'degrees', but plus the first cyclic degree
		^this.scaleIndices - rootIndex
	}
	reduceTuning {|inCyclic, inRational| //@ desc: reduces the tuning to the degrees of this Systema
		this.reduce(inCyclic, inRational)
	}
	reduce {|inCyclic, inRational| //@ desc: identical with method reduceTuning 
		var steps, octRatio;
		if (tuning.isRational) { steps = this.ratios; octRatio = steps.last / steps.first
		}{ steps = this.cents; octRatio = (steps.last - steps.first).centratio };
		degrees = Array.series(steps.size, 0, 1);
		this.tuning_( RCTuning.new(steps, octRatio, name, tuning.isRational, tuning.isCyclic)
				.isRational_(inRational ?? { tuning.isRational })
			, warn: false);
		this.isCyclic_(inCyclic ?? { tuning.isCyclic })
	}
	// ------------------------------- important print features  ----------------------------------
	printOn {|stream|	
		if (tuning.isNil) { (this.class.name ++ "." ++ name).printOn(stream)
		}{ (this.class.name ++ "." ++ name ++ "('" ++ tuning.name ++ "')" ).printOn(stream) }
	}
	post { this.asString.post }
	postln{ this.asString.postln }
	
	posti { this.postIntervals 
		/*@ desc: 'post intervals'
		ex:
			SYS.ionian.posti
			SYS.ionian.postri
			SYS.ionian.postci
			SYS.ionian.postid
			SYS.ionian.postrid
			SYS.ionian.postcid
			<br>
			SYS.testSystema.posti
			SYS.testSystema.postri
			SYS.testSystema.postci
			SYS.testSystema.postid
			SYS.testSystema.postrid
			SYS.testSystema.postcid
			SYS([1,12.rand,12.rand], 14, name:"test").posti 
		@*/
	}
	postid { 		//@ desc: 'post intervals descending'
		this.postIntervals(desc: true) }
	postri {		//@ desc: 'post ratio intervals'
		this.postIntervals(true)}
	postrid { 	//@ desc: 'post ratio intervals descending'
		this.postIntervals(true, true) }
	postci {		//@ desc: 'post cent intervals'
		this.postIntervals(false) }
	postcid {		//@ desc: 'post rational intervals descending'
		this.postIntervals(false, true) }
	
	postIntervals {|inR, desc=false| 				// post intervals
		var rev, interv, updown, isR = inR ?? { tuning.isRational };
		interv = if (isR) { tuning.getiR(this.scaleIndices) } { tuning.getiC(this.scaleIndices) };
		updown = this.getUpDowns(interv, isR);
		"".postln; 
		if (desc.not) { interv = interv.reverse; rev=interv.size-1 };
		interv.do{|item, i| if(desc.not) {i=rev-i};
				(this.prPosti(item, i+1, i+2, updown[i], inR) ).post }
	}
	getUpDowns{| interv, isR| 
		^if (isR) { interv.collect{|i| case {i.isNil}{"••"}{i>1}{" /"}{i<1}{" \\"}{" -"} }
		}{          interv.collect{|i| case {i.isNil}{"••"}{i>0}{" /"}{i<0}{" \\"}{" -"} } }
	}
	prPosti {|interv, i, j, updown, isR| 
		^"i(" ++ i.mcFormat(2) ++ " - " ++ j.mcFormat(2) ++ "): " ++ updown ++ "  " 
			++ if (interv.isNil) { "•contains nil•" }{
				isR.switch(
				true, { interv.mcFormat(10, \c) },
				false, { interv.round(0.01).mcFormat(7, \r, plus:true) },
				{ if (interv.isKindOf(Ratio)) {interv.info} {interv.centratio.asRatio.info } })
			} ++ "\n";
	}
	postl	{|prtLegend, prtFormat| this.postlong(prtLegend, prtFormat)
			/*@ desc: 'post long'
			prtLegend: a List of Strings -- keys as Strings to values stored in the stepDicts.				Surrounding spaces give the width of the print column.
			prtFormat: a List of Symbols -- one of \l, \c, or \r is possible, defining the 				column's allignment.
			ex: 
				SYS.ionian.postl
				SYS.ionian.postrl
				SYS.ionian.postcl
				SYS.ionian.postld
				SYS.ionian.postrld
				SYS.ionian.postcld
				<br>
				SYS.testSystema.postld
				SYS.testSystema.postrld
				SYS.testSystema.postcld
				SYS.testSystema.postrld(["  name  ", "agmVoc"], [\c, \c])
			@*/
	}
	postld	{|prtLegend, prtFormat| //@ desc: 'post long descending'
		this.postlong(prtLegend, prtFormat, false, desc: true) }
	postrl 	{|prtLegend, prtFormat| //@ desc: 'post ratios long'
		this.postlong(prtLegend, prtFormat, false, true) }
	postrld	{|prtLegend, prtFormat| //@ desc: 'post ratios long descending'
		this.postlong(prtLegend, prtFormat, false, true, true) }
	postcl 	{|prtLegend, prtFormat| //@ desc: 'post cents long'
		this.postlong(prtLegend, prtFormat, false, false) }
	postcld	{|prtLegend, prtFormat| //@ desc: 'post cents long descending'
		this.postlong(prtLegend, prtFormat, false, false, true) }

	postli	{|prtLegend, prtFormat| this.postlong(prtLegend, prtFormat, true) 
			/*@ desc: 'post long (with) intervals'
			prtLegend: a List of Strings -- keys as Strings to values stored in the stepDicts.				Surrounding spaces give the width of the print column.
			prtFormat: a List of Symbols -- one of \l, \c, or \r is possible, defining the 				column's allignment.
			ex: 
				SYS.ionian.postli
				SYS.ionian.postrli
				SYS.ionian.postcli
				<br>
				SYS.testSystema.postli
				SYS.testSystema.postlid
				SYS.testSystema.postrli
				SYS.testSystema.postrlid
				SYS.testSystema.postcli
				SYS.testSystema.postclid
				SYS.testSystema.postclid(["  name  ", "agmVoc"], [\c, \c])
			@*/
	}
	postlid 	{|prtLegend, prtFormat| //@ desc: 'post long intervals descending'
		this.postlong(prtLegend, prtFormat, true, desc:true) } 
	postrli	{|prtLegend, prtFormat| //@ desc: 'post ratio long intervals'
		this.postlong(prtLegend, prtFormat, true, true) }
	postrlid	{|prtLegend, prtFormat| //@ desc: 'post ratio long intervals descending'
		this.postlong(prtLegend, prtFormat, true, true, true) }
	postcli	{|prtLegend, prtFormat| //@ desc: 'post cents long intervals'
		this.postlong(prtLegend, prtFormat, true, false) }
	postclid	{|prtLegend, prtFormat| //@ desc: 'post cents long intervals descending'
		this.postlong(prtLegend, prtFormat, true, false, true) }
	
	postlong { |prtLegend, prtFormat, isPrtInts=false, inR, desc=false|
		var isR = inR ?? { tuning.isRational };
		var title = this.asString + " ::" + "midiRoot:" + midiRoot.round(0.01) 
			+ "midiOffset:" + midiOffset.round(0.01) + ":: "
			+ (if (tuning.isCyclic) { " cyclic " } { " non-cyclic "}) 
			+ "<" ++ tuning.size + "/\\" + tuning.octaveRatio + (if (isR) { "R" } {"C"}) ++ ">";
		var head = ["rD,tC", "degree", "step"], block = "", info= "", strg;
		var headSize, infoSize=0;
		var sDegs = this.scaleIndices;
		var rev, interv, intervN, updown, deg2i;
		if (isPrtInts) {
			interv = if (isR) { tuning.getiR(sDegs) } { tuning.getiC(sDegs) };
			intervN = interv.size;
			updown = this.getUpDowns(interv, isR)
		};
		strg = title ++ "\n".catList(Array.fill(title.size,$-)) ++ "\n";
		globDict[\name] !? { 
			if(globDict[\name].notEmpty && (globDict[\name] != name.asString)) {
				strg=strg++globDict[\name]++"\n"} 
		};
		globDict[\desc] !? { 
			if(globDict[\desc].notEmpty) { 
				strg = strg ++ "\t" ++ globDict[\desc].raggedLeftFormat(title.size)++"\n"} 
		};
		
		prtLegend = prtLegend ?? { globDict[\prtLegend] } ?? {List.new};
		if (prtLegend.notEmpty) { 
			prtFormat = prtFormat ?? { globDict[\prtFormat] } ?? {List.fill(prtLegend.size, \c)};
			head = prtLegend ++ head
		};
		((head.size)-1).do{|i| block = block ++ head[i] ++ " | "};
		headSize = block.size+1; head = block ++ " " ++ head.last.asString; block ="";
		
		if (desc.not) { sDegs = sDegs.reverse; rev=sDegs.size-1 };
		sDegs.do{|deg, i| 
			var j, line, marker = "";
			if(desc.not) {i=rev-i};
			if (deg.isNil) { block = block ++ "\•degree" +i.mcFormat(3,\r,true)+"•is nil•\n";
			}{
				marker = if (i == rootIndex) { marker ++ " * "} {marker ++ "   "};
				marker = if (i == (tonalCenter)) { marker ++ " T"} {marker ++ "  "};
				if (prtLegend.notEmpty) {
					line = "".catList(prtLegend.collect{|keyS, j| 
						var key = (keyS.asString.reject{|c| c.isSpace}).asSymbol;
						(stepDicts.wrapAt(i).at(key) ? "-?-") // name octaves once...!
							.encFormat(keyS.size, prtFormat.at(j), bias:-1) 
							++ "   " })
				};
				line = line ++ marker ++ "  " ++ (i-rootIndex).mcFormat(4,\r,true) ++ "       ";
				info = try { 
					inR.switch(
						true, { tuning.degToR(deg).mcFormat(10, \c) },
						false, { tuning.degToC(deg).round(0.01).mcFormat(7, \r) },
						{ tuning.degToR(deg).info })
				}{ "•is nil•" };
				infoSize = infoSize max: info.size;
				block = block ++ line ++ info ++ "\n";
				if (isPrtInts) {
					if (desc.not) {j=i-1} {j=i};
					if ( (j < intervN) && j.isNegative.not) {
						block = block.catList(Array.fill(headSize-18,$ ))
						++ this.prPosti(interv[j], j+1, j+2, updown[j], inR)
					}
				}
			}
		};
		
		headSize = headSize + infoSize;
		strg = strg.catList(Array.fill(headSize,$_)) ++ "\n";
		strg = strg ++ head ++ "\n" ++ block;
		strg = strg.catList(Array.fill(headSize max: title.size,$=)) ++ "\n";
		("\n" ++ strg).postln;
	}
	
	
	// ********************************************************************************************
	// ---------------------------- overloadings of Scale behaviour -------------------------------
	semitones {	 //@ desc: like in Scale
		^this.cents.collect{|c| c !? {c/100} } 
	}
	cents { 		//@ desc: like in Scale
		^degrees.collect{|deg| tuning.degToC(deg)} 
	}
	ratios {		//@ desc: like in Scale
		^degrees.collect{|deg| tuning.degToR(deg)}
	}
	degreeToRatio {|degree, octave = 0|
		var ratio = tuning.degToR(degree + rootIndex);
		^ratio !? { ratio * (tuning.octaveRatio ** octave) }
		/*@ desc: like in Scale
			ex:
			SYS.testSystema.degrees.do{|deg| SYS.testSystema.degreeToRatio(deg).postln }
			SYS.testSystema.degrees.do{|deg| SYS.testSystema.degreeToRatio(deg, -1).postln }
		@*/
	}
	degreeToFreq { |degree, rootFreq, octave=0|
		var ratio = tuning.degToR(degree + rootIndex);
		^ratio !? {(ratio * (tuning.octaveRatio ** octave) 
			* (rootFreq ?? { (midiRoot + midiOffset).midicps })).value}
		/*@ desc: like in Scale
		rootFreq: if nil, midiRoot + midiOffset is used as default.
		ex:
			SYS.testSystema.degreeToFreq(0, 54.midicps)
			SYS.testSystema.degreeToFreq(0)
			SYS.testSystema.degrees.collect{|deg| SYS.testSystema.degreeToFreq(deg) }
		@*/
	}
	
	*choose { |size = 7, pitchesPerOctave = 12|
		var systemata = all.select{|sys| 
			(sys.size == size) && (sys.pitchesPerOctave == pitchesPerOctave) 
		};
		systemata.isEmpty.if({ 
			("No known Systemata with size " ++ size.asString ++ 
				" and pitchesPerOctave " ++ pitchesPerOctave.asString + "in memory").warn;
			^nil
		});
		^systemata.choose
	}
	
	/* 
		replace with an archiving scheme by files...
		
	*directory {
		^ScaleInfo.directory
	}
	
	storedKey {
		// can be optimised later
		var stored = ScaleInfo.scales.detect(_ == this);
		^stored !? { ScaleInfo.scales.findKeyForValue(stored) }
	}

	*/
	
	// ---------------------------- main inerface to the pitchEvent ------------------------------
	at { |index|
		if (tuning.isCyclic) {
			^tuning.at(degrees.wrapAt(index + rootIndex))
		}{
			^tuning.at(degrees.at(index + rootIndex))
		}
		/*@ desc: get midinote/semitone of a degree
		ex:
			SYS.ionian.at(-1)
			SYS.ionian.at(0)
			SYS.ionian.at(7)
			SYS.ionian.at(8)
			<br>
			SYS.testSystema.at(-24)
			SYS.testSystema.at(-23)
			SYS.testSystema.at(-21)
			SYS.testSystema.at(0)
			SYS.testSystema.at(21)
			SYS.testSystema.at(22)
		@*/
	}
	wrapAt { |index|
		if (tuning.isCyclic) {
			^tuning.wrapAt(degrees.wrapAt(index + rootIndex))
		}{
			^tuning.wrapAt(degrees.at(index + rootIndex))
		}
		/*@ desc: get midinote/semitone of a degree wraped at the tuning 		<br>(though this makes little sense for non-cyclic systemata)
		ex:
			SYS.ionian.wrapAt(-1)
			SYS.ionian.wrapAt(0)
			SYS.ionian.wrapAt(7)
			SYS.ionian.wrapAt(8)
			<br>
			SYS.testSystema.wrapAt(-24)
			SYS.testSystema.wrapAt(-23)
			SYS.testSystema.wrapAt(-21)
			SYS.testSystema.wrapAt(0)
			SYS.testSystema.wrapAt(21)
			SYS.testSystema.wrapAt(22)
		@*/
	}
	degrees { 
		^degrees.collect{|deg| try {deg - rootIndex} {nil} }
		/*@ desc: get midinote/semitone of a degree
		ex:
			SYS.ionian.degrees
			SYS.testSystema.degrees
			<br>
			SYS.testSystema.rootIndex
			SYS.testSystema.degrees.at(SYS.testSystema.rootIndex)
		@*/
		 
	}
	item2Degree {|item ...codes| //callback from pitch event
		var res = case
		{item.isKindOf(Integer)} {item} // is Integer means: item is already a proper degree
		{item.isKindOf(String)} { this.code2Degrees(item, *codes) } 
		{item.isKindOf(Ratio)} { this.ratio2Degree(item) }
		{item.isKindOf(Float)} { this.cent2Degree(item) }
		{item.isKindOf(Collection)} { 
			^item.collect{|i| this.item2Degree(i, *codes)} } //recursive!
		{nil};
		^if (res.isKindOf(Collection).not) { res } {
			if (res.notEmpty) {res} {nil}
		}
		/*
			SYS.testSystema.item2Degree(-2)
			SYS.testSystema.item2Degree("ùçú")
			SYS.testSystema.item2Degree(16/|15)
			SYS.testSystema.item2Degree(111.7)
			SYS.testSystema.item2Degree([-2, 16/|15, 111.7, "œΩùç∑ùà¨"])
		*/
	}
	items2Degrees {|items ...codes| 
		if (items.isKindOf(String)) {items = items.takeUTF8};
		^items.collect{|i| this.item2Degree(i, *codes) } 
	}
	
	/* not working ... how to address one's event from inside?
	//answer: use the value method which returns the event: anEvent.value
	//also check use of proto to overwrite parent keys ...
	eventExtras { 
		^Event.new.put(\logOn, logOn).put(\noMatchWarn, noMatchWarn)
			.put(\noMatchBeep, noMatchBeep)
	}
	*/
	
	isInAmbit {|deg| 
		if (tuning.isCyclic) {^true};
		deg = deg + rootIndex;
		 ^if (deg.isNegative || (deg > (tuning.size-1))) {^false} {^true}
	}
	
	/* like in Scale + Tuning -- no need to overload:
	octaveRatio { ^tuning.octaveRatio }
	stepsPerOctave { ^tuning.stepsPerOctave  // ! nothing else but ( octaveRatio.ratiomidi ) !
		/*	stepsPerOctave only applies to equal temprament that can be computed right away
		without the use of any tuning lists ... --> so if there is a tuning this is meaningless.		Hence, stepsPerOctave and octaveRatio counterbalance each other and behave absoulutely
		nutral while computing  '~midinote' of the pitch event:
			~octaveRatio = 2.0
			~octaveRatio = 0.25
			~octaveRatio = 0.2344
			~stepsPerOctave = ~octaveRatio.log2 * 12.0
			~stepsPerOctave = ~octaveRatio.ratiomidi

			~midinote = {|note| (note / ~stepsPerOctave) * (12 * ~octaveRatio.log2) + 60 }
			(-12..25).do{|i| ~midinote.value(i).postln}
		*/
	}
	*/
	performDegreeToKey {|degree, stepsPerOctave, accidental = 0|
		var baseKey; //accidental = Vorzeichen only in ET! from -5 to +4 e.g. 3.5 -> pDTK(4,12,-5)
		//if (degree.isNil) {^nil};
		stepsPerOctave = stepsPerOctave ?? {tuning.stepsPerOctave};
		if (tuning.isCyclic) {
			baseKey = stepsPerOctave * ((degree + rootIndex) div: this.size) +this.wrapAt(degree)
		}{
			try { baseKey = this.at(degree) } {^nil}
		};
		^if(accidental == 0) { baseKey } { baseKey + (accidental * (stepsPerOctave / 12.0)) }
		/* 
		first thought of diffrent idea to realonably interpolate between scale degrees...
		
		if(accidental == 0) { ^baseKey } { 
			if (tuning.isCyclic) {
				nextKey = stepsPerOctave * ((degree + rootIndex +1) div: this.size) 
					+this.wrapAt(degree+1)
			}{
				nextKey =	this.at(degree +1) ? baseKey
			};
			^baseKey + (accidental * (nextKey - baseKey) * 0.1)
		}
		*/
			/*@ desc: returns a midinote based on current tuning.
			ex:
				SYS.ionian.performDegreeToKey(-1)
				SYS.ionian.performDegreeToKey(0)
				SYS.ionian.performDegreeToKey(7)
				SYS.ionian.performDegreeToKey(8)
				<br>
				SYS.testSystema.performDegreeToKey(-24)
				SYS.testSystema.performDegreeToKey(-23)
				SYS.testSystema.performDegreeToKey(-21)
				SYS.testSystema.performDegreeToKey(0)
				SYS.testSystema.performDegreeToKey(21)
				SYS.testSystema.performDegreeToKey(22)
			@*/
	}
	performKeyToDegree {|inKey| 
		var n, key, offset, index, semitones = tuning.semitones;
		if (inKey.isNil) {^nil};
		inKey = inKey.round(centPresc / 100);
		if (tuning.isCyclic) {
			offset = if (tuning.stepsPerOctave.isNegative) 
				{ semitones.maxItem.abs.neg }{ semitones.minItem.abs };
			inKey = inKey + offset;
			n = inKey div: tuning.stepsPerOctave * degrees.size;
			key = inKey % tuning.stepsPerOctave - offset;
		}{
			if ((inKey < semitones.minItem) || (inKey > semitones.maxItem)) {^nil}; 
			key = inKey; n=0;
		};
		index = semitones.order[semitones.copy.sort.indexInBetween(key).round];
		/*
			a = [ 1, 2, 4, 3, 5, 6 ]
			a = a.reverse
			b = a.order
			c = a.copy.sort
			d = c.indexInBetween(1)
			e = d.round
			f = b[e]
			g = a[f]
		*/	
//this.logln("n, key, index" + [n, key, index]);
		^degrees.indexInBetween(index) + n - rootIndex;
		
			/*@	desc: map midinotes back into scale degrees
			ex:
			Scale.ionian.degrees
			SYS.ionian.semitones.collect{|key| SYS.ionian.performKeyToDegree(key) }
				
				// <br> // shift rootIndex
			SYS.ionian.copy("root2").rootIndex_(2).degrees
			r = (0..(SYS.root2.size-1)).collect{|deg| SYS.root2.performDegreeToKey(deg) }
			r.collect{|key| SYS.root2.performKeyToDegree(key) }
			
				// <br> //  descending systema
			Systema(Systema.testSystema.ratios.drop(2).drop(-1), name: \testCyc0,				octaveRatio: 0.25).rootIndex_(0).postld
			r = (0..(SYS.testCyc0.size-1)).collect{|deg| SYS.testCyc0.performDegreeToKey(deg) }
			r.collect{|key| SYS.testCyc0.performKeyToDegree(key) }
				
				// <br> // descending systema with shifted rootIndex
			Systema(Systema.testSystema.ratios.drop(2).drop(-1), name: \testCyc21,	 			octaveRatio: 0.25).postld
			r = (0..(SYS.testCyc21.size-1)).collect{|deg| SYS.testCyc21.performDegreeToKey(deg) }
			r.collect{|key| SYS.testCyc21.performKeyToDegree(key) }
			r.collect{|key| SYS.testCyc21.degrees.at(SYS.testCyc21.performKeyToDegree(key)) }
				
				// <br> // non-cycric testSystema descending with shifted rootIndex
			r = (0..(SYS.testSystema.size-1)).collect{|deg| 				SYS.testSystema.performDegreeToKey(deg) }
			r.collect{|key| SYS.testSystema.performKeyToDegree(key) }
			r.collect{|key| SYS.testSystema.degrees.at(SYS.testSystema.performKeyToDegree(key)?0)}
			
				
				// <br> // non-cycric testSystema descending with shifted rootIndex
			r = SYS.testSystema.degrees.collect{|deg| SYS.testSystema.performDegreeToKey(deg) }
			r.collect{|key| SYS.testSystema.performKeyToDegree(key) }
			@*/
	}
	performNearestInList { |degree| 
		^degrees.at(this.degrees.indexIn(degree))
			/*@ ex: 
				(-4..14).collect{|i| SYS.ionian.performNearestInList(i) }
				SYS.ionian.rootIndex_(2).degrees
				(-4..14).collect{|i| SYS.ionian.rootIndex_(2).performNearestInList(i) }
				<br>
				(-4..14).collect{|i| SYS.testSystema.performNearestInList(i) }
				
				[-24, -23, -22, -20, -0.3, 0, 0.3, 21, 22, 23].collect{|i| 					SYS.testSystema.performNearestInList(i) }
				
				SYS([13, 23, 33], 0, \testSystema, \extractIndices, 1).postld.degrees;
				[-24, -23, -22, -20, -0.3, 0, 0.3, 21, 22, 23].collect{|i| 					SYS.extractIndices.performNearestInList(i) }
				
				SYS.testSystema.copyBy([-10, 0, 10], \extractDegrees).postld.degrees;
				[-24, -23, -22, -20, -0.3, 0, 0.3, 21, 22, 23].collect{|i| 					SYS.extractDegrees.performNearestInList(i) }
			@*/
	}
	performNearestInScale { |degree| var root, key;
			if (tuning.isCyclic.not) { ^this.performNearestInList(degree) - rootIndex };
			degree =	degree + rootIndex;
			root = 	degree trunc: tuning.size; // not stepsPerOctave
			key = 	degree % 		tuning.size; // not stepsPerOctave
			^key.nearestInList(degrees) + root -rootIndex;
			/*@ ex: 
				(-4..14).collect{|i| SYS.ionian.performNearestInScale(i) }
				SYS.ionian.rootIndex_(2).degrees
				(-4..14).collect{|i| SYS.ionian.rootIndex_(2).performNearestInScale(i) }
				<br>
				SYS.testSystema.postld
				(-4..14).collect{|i| SYS.testSystema.performNearestInScale(i) }
				SYS.testSystema.deepCopy(\testCyclic).isCyclic_(true).postld				(-4..14).collect{|i| SYS.testCyclic.performNearestInScale(i) }
				
				[-24, -23, -22, -20, -0.3, 0, 0.3, 21, 22, 23].collect{|i| 					SYS.testSystema.performNearestInScale(i) }
				[-24, -23, -22, -20, -0.3, 0, 0.3, 21, 22, 23].collect{|i| 					SYS.testCyclic.performNearestInScale(i) }
			@*/
	}
	// -------------------------------- 'backward' compatibility --------------------------------
	
	asScale {|argDegrees, argName|
		^Scale.new( argDegrees ? degrees, tuning.size, tuning.asTuning, argName ? name) }	
	// *******************************************************************************************
	midiRoot_ {|midiNote|
		midiRoot = midiNote;
		this.changed(\midiRoot, midiRoot)
	}
	midiOffset_ {|midiNote|
		midiOffset = midiNote;
		this.changed(\midiOffset, midiOffset)
	}
	ratioToDegree {|ratio| 
		^this.performKeyToDegree(ratio.ratiomidi)
		/*@: same like performKeyToDegree with a Ratio as argument
			ex:
			r = SYS.testSystema.degrees.collect{|deg| SYS.testSystema.degreeToRatio(deg) }
			r.collect{|ratio| SYS.testSystema.ratioToDegree(ratio) }
		@*/
	}
	freqToDegree {|freq, rootFreq, octave=0|
		rootFreq = (rootFreq ?? { (midiRoot + midiOffset).midicps }) * (this.octaveRatio ** octave);
		^this.ratioToDegree(freq / rootFreq)
		/*@: same like performKeyToDegree but with frequency as argument
			ex:
			r = SYS.testSystema.degrees.collect{|deg| SYS.testSystema.degreeToFreq(deg) }
			r.collect{|freq| SYS.testSystema.freqToDegree(freq) }
			r.collect{|freq| SYS.testSystema.freqToDegree(freq +1) } //still a bit academic ;-)
		@*/
	}
	
	play {|dur=0.3, degs, amp|
		var degPat = 	if (degs.notNil) { Pseq( this.items2Degrees(degs) ) 
				  	}{ 	if(tuning.isCyclic) {	Pseries(0-rootIndex, 1, degrees.size +1)
				  		}{				  	Pseries(0-rootIndex, 1, degrees.size) } };
		if (amp.isNil) { Pbind(\scale, this, \degree, degPat, \dur, dur).play
		}{ Pbind(\scale, this, \degree, degPat, \dur, dur, \amp, amp).play }		/*@ desc: play it
		dur: duration per note
		degs: list of degrees or code string. Same argument policy as explained for 'rootDegree_'
		ex:
		Systema.ionian.play
		Systema.ionian.playR	
		Systema.testSystema.play(0.2)
		Systema.testSystema.playR(0.2)
	
		Systema.testSystema.play(0.2, (-2..0))
		Systema.testSystema.play(0.2, [16/|15, 28/|27, 1/|1])
		Systema.testSystema.play(0.2, [111.7, 63.0, 0.0])
		Systema.testSystema.play(0.2, "œΩùà±ùçú")
		Systema.testSystema.playR(0.2, "¢åæ¢åøœπ")
		Systema.testSystema.play(0.8, ["¢åæ¢åøœπ", "ùç∑ùàØùç∑", "ùàÆùà≠ùà¨"])
		// <br>
		// Patterns are supported. Actually 'play' uses a Pbind like this
		// <br>
		Pbind(\dur, 0.2, \scale, Systema.testSystema, \degree, [16/|15, 28/|27, 1/|1]).play
		(
		Pbind(\dur, 0.8, \scale, Systema.testSystema, \codes, [\agmIns, \agmVoc], 
			\degree, Pseq(["¢åæ¢åøœπ", "ùç∑ùàØùç∑", "ùàÆùà≠ùà¨"]) ).play
		)
		@*/
	}
	playR {|dur=0.3, degs, amp| //@ desc: 'play reverse' (see play)
		var degPat =	if (degs.notNil) { Pseq( this.items2Degrees(degs).reverse ) 
					}{ Pseries(degrees.size-1-rootIndex, -1, degrees.size) };
		if (amp.isNil) { Pbind(\scale, this, \degree, degPat, \dur, dur).play
		}{ Pbind(\scale, this, \degree, degPat, \dur, dur, \amp, amp).play }
	}
	gui {
		SystemaGui(this);
	}
	/*
	normalize {
		isRational.if { 
			tuning = tuning / rootIndex; rootIndex = 1.asRatio 
		} { 
			tuning = tuning - rootIndex; rootIndex = 0
		};
	}
	*/
	
	
	// *******************************************************************************************
	// *******************************************************************************************
	
	/* 
	Systema.testSystema.postld
	Systema.testSystema.octaveRatio.info
	Pbind(\scale, Systema.testSystema, \degree, Pseq(Systema.testSystema.degrees), \dur, 0.2, 		\i, Pseries(0,1, Systema.testSystema.size)).trace([\degree, \note, \freq, \i]).play
	Pbind(\scale, Systema.testSystema, \degree, Pseq([-21, 0, 21]), \dur, 0.2, 		\i, Pseries(0,1, Systema.testSystema.size)).trace([\degree, \note, \freq, \i]).play
	Pbind(\scale, Systema.testSystema, \degree, Pseq([-42, 0, 23]), \dur, 0.2, 		\i, Pseries(0,1, Systema.testSystema.size)).trace([\degree, \note, \freq, \i]).play
	
	Systema.testSystema.noMatchWarn = true
	
	Systema(Systema.testSystema.ratios.drop(2).drop(-1), name: \testSystemaCyclic, octaveRatio: 0.25)
	Systema.testSystemaCyclic.postld
	Systema.testSystemaCyclic.at(-21)
	Systema.testSystemaCyclic.at(21)
	Systema.testSystemaCyclic.size
	Systema.testSystemaCyclic.tuning.size
	Systema.testSystemaCyclic.stepsPerOctave
	
	Pbind(\scale, Systema.testSystemaCyclic, \degree, Pseq([-21, 0, 21]), \dur, 0.2, 		\i, Pseries(0,1, Systema.testSystemaCyclic.size)).trace([\degree, \note, \freq, \i]).play
	Pbind(\scale, Systema.testSystemaCyclic, \degree, Pseq([-42, -21, 0, 21, 42]), \dur, 0.2, 		\i, Pseries(0,1, Systema.testSystemaCyclic.size)).trace([\degree, \note, \freq, \i]).play
	Pbind(\scale, Systema.testSystemaCyclic, \degree, Pseries(-42,1,85), \dur, 0.1, 		\i, Pseries(0,1)).trace([\degree, \note, \freq, \i]).play
	
	Pbind(\octave, 6, \scale, Systema.testSystemaCyclic, \degree, Pseq([-42, -21, 0, 21, 42]), \dur, 0.2, \i, Pseries(0,1, Systema.testSystemaCyclic.size)).trace([\degree, \note, \freq, \i]).play
		
	
	Systema.ionian.midiRoot = 70
	
	Pbind(\scale, Systema.ionian, \degree, Pseq(Systema.ionian.degrees), \dur, 0.1, 		\i, Pseries(0,1)).trace([\degree, \note, \freq, \i]).play
	Pbind(\scale, Systema.ionian, \degree, Pseq(Systema.ionian.scaleIndices), \dur, 0.1, 		\i, Pseries(0,1)).trace([\degree, \note, \freq, \i]).play
	Pbind(\scale, Systema.ionian, \degree, Pseries(-12,1,25), \dur, 0.1, 		\i, Pseries(0,1)).trace([\degree, \note, \freq, \i]).play
	
	
	*/	
}

SYS : Systema {
	makeUniqueName{|key| var str, i;
		key = key.asString;
		#[" ", "'", "/", "\t", "\f", "\r", "\n"].do{|str| key = key.replace(str, "")}; // "_" allowed
		if (key.first.isUpper) { key[0] = key.first.toLower };
		key = key.asSymbol;
		if(all.at(key).isNil && this.class.methods.any{|m| m.name == key}.not) 
			{ ^key } {
				str = key.asString;
				i = str.findBackwards(uniquePefix);
				if (i.notNil) { 
					i=i+uniquePefix.size; 
					^this.makeUniqueName((str.keep(i) ++ (str.drop(i).asInt+1)).asSymbol)
				}{ 	^this.makeUniqueName((str ++ uniquePefix ++ 1).asSymbol) }
			}
	}
}

SystemaAD : Systema {
	var <>descScale;
	*new {|stepsUp, stepsDown, pitchesPerOctave, tuning, name, rootIndex, tonalCenter,
			midiRoot, midiOffset, octaveRatio, globDict, stepDicts ...stepCodes|
		^super.new(stepsUp, pitchesPerOctave, tuning, name, rootIndex, tonalCenter,
			midiRoot, midiOffset, octaveRatio, globDict, stepDicts, *stepCodes)
			.descScale_(Systema(stepsDown, pitchesPerOctave, tuning, name, rootIndex, tonalCenter,
				midiRoot, midiOffset, octaveRatio, globDict, stepDicts, *stepCodes)
			)
	}
	asStream { ^ScaleStream(this, 0) }
	embedInStream { ScaleStream(this).yield }

}

+Scale {
	asSystema{ ^Systema.new(degrees, pitchesPerOctave, tuning.asRCTuning, name: name) }
}

+Nil {
	degreeToKey { ^nil }	
}
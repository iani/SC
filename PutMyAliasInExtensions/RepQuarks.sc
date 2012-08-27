/* MC / IZ 201109-10
RepQuarks is a code management mechanism for SuperCollider based on Quarks. RepQuarks makes it possible to combine different quark projects which exist independently in different folders, without having to copy these in the global Quarks directory. This gives additional freedom in working with multiple projects while keeping their files separate, and is especially valuable for sharing code with other people independently from the global Quarks. 

Please see README_RepQuarks.rtfd for documentation. 

2DO: Implement automatic dependency resolution? Probably too expensive as one would have to scan for *all* class names? Some thoughts though: 
 
Before installing a Quark from a RepQuarks repository, look at all its class definitions by performing a grep for class definition code in the class definition files of the quark. Collect all declared superclasses in those class definitions. Find if those superclasses are already defined. If a superclass is not already defined, then find if it is defined in one of the known RepQuark repositories. If yes, find the Quark in that repository and install it. Print a message about the automatic dependency resolution (successful or not). 

To prepare this: Look at Quarks classvars: global, allInstances, known, repos, local.
Examine which of these variables should be used to store the list of known/installed repquarks with their paths. These can be used to search all existing RepQuarks in order to resolve dependencies. 

*/

RepQuarks : Quarks {
	classvar <menu, <menuName = "Quarks";
//	classvar <repQuarks;	// dict of all RepQuarks instances, i.e. groups of local repository quarks.

	*initClass {
		StartUp add: {
//			repQuarks = IdentityDictionary.new;
			Platform.case(\osx, {
				this.makeMainQuarksMenu;
				this.subclasses do: _.makeMenu;
			});
		};
	}

	*makeMainQuarksMenu {
		menu = CocoaMenuItem.topLevelItems detect: { | m | m.name == menuName };
		if (menu.isNil) {
			menu = SCMenuGroup(nil, menuName, 10);
		};
		SCMenuItem(menu, "quarks").action = { Quarks.gui };
		SCMenuItem(menu, "sc3-plugins").action = {
			Quarks(localPath: (Platform.userAppSupportDir +/+ "sc3-plugins")).gui
		};
	}

	*addToMenu { | itemName, path |
//		postf("THIS WILL ENTER LIBRARY: %\n", itemName);
//		path.postln;
//		pathMatch(path +/+ "DIRECTORY/*").postln;
//		repQuarks[itemName.asSymbol] = this.new(localPath: path);
		SCMenuItem(menu, itemName).action = { this.new(localPath: path).gui; };
	}

	*makeMenu {
		var path, pathMatch, quarkspecs;
		path = this.getQuarkPath;	// subclasses give their own paths
		SCMenuSeparator(menu, menu.children.size);
		quarkspecs = this.getQuarksDirectories(path);
		if (quarkspecs.size == 0) {
			postf("FOUND NO QUARKS FOR: %\n", path);
		}{
			quarkspecs do: { | spec | this.addToMenu(spec[0], spec[1]); }
		}
	}

	*getQuarksDirectories { | path |
		/* 	Find out which quark groups are contained in the folder given by the 
			quarkpath and create a menu for them.
			Search in 4 ways: 
			1. Look for a folder named Quarks. If found then if it contains
			a DIRECTORY folder, read the quark definitions from the DIRECTORY folder. 
			2. If no DIRECTORY folder was found in the Quarks folder, then search for 
		   	DIRECTORY folders in each of the subdirectories of the Quarks folder. 
		   	3. If none of the above, then, 
			if the quarkpath folder contains a folder named DIRECTORY, then read the quarks
			from DIRECTORY and add one single quark group named after the quarkpath folder.
			4. Otherwise look at each subfolder in the quarkpath folder and add a quark group
			for each subfolder that contains a DIRECTORY folder.
		*/
		var pathplus, quarksDirectories;
		pathplus = path +/+ "Quarks/DIRECTORY";
		quarksDirectories = pathplus.pathMatch;
		if (quarksDirectories.size > 0) { ^[[path.basename, quarksDirectories.first.dirname]] };
		pathplus = path +/+ "Quarks/*";
		quarksDirectories = pathplus.pathMatch.select { | p |
			(p +/+ "DIRECTORY").pathMatch.size > 0;
		};
		if (quarksDirectories.size > 0) {
			^[quarksDirectories collect: _.basename, quarksDirectories].flop;
		};
		pathplus = path +/+ "DIRECTORY";
		postf("RepQuark Directory not found. Looking for: %\n", pathplus);
		postf("The contents of above path are: %\n", pathplus.pathMatch);
		if ((quarksDirectories = pathplus.pathMatch).size > 0) {
			^[[path.basename, pathplus.dirname]];
		};
		pathplus = path +/+ "*/DIRECTORY";
		^quarksDirectories = pathplus.pathMatch.collect({ | p | [p.dirname.basename, p.dirname] });
	}

	*isQuarkFolder { | path |
		^(path +/+ "DIRECTORY").pathMatch.size > 0; 	
	}

	*getQuarkPath {
		/* RepQuarks can be included in the compile path of SuperCollider by creating 
		a subclass of RepQuarks, putting it in the top level of your quarks folder, and then
		putting an alias of the subclass file inside the Extensions folder of SuperCollider
		(Platform.userExtensionDir;). 
	
		Otherwise, if you use a symlink (ln -s) to include your quark directory in the 
		Extensions folder, then you should provide a class method quarkPath to define your 
		custom path.  This may also be a class variable with getter: 
			classvar <quarkPath = "/path/to/my/quark/folder... ";
			
		The getQuarkPath method first checks if the file of this subclass is in the Extensions folder.
		If yes, it assumes that this is a symlink, and tries to get your custom quarkPath. 
		If no, it uses the path of the file of this subclass to get your quarks. 
		This means that your quarks can be used independently of your quarkPath method,
		as long as an *alias* (MacOS X) your RepQuarks subclass definition file is placed 
		in the Extensions folder.  	
		
		 */
		var path;
		path = this.filenameSymbol.asString.dirname;
//		path = this.filenameSymbol.asString;
		if (path == Platform.userExtensionDir) {
			 postf("% is a symlink. Will use custom path to find quarks\n", this.name);
			 path = this.quarkPath;
			 postf("Custom quarks path is: %\n", path);
		}
		^path;
	}

	*quarkPath {
		/* This is the default quarks path. Subclasses may overwrite it.
		It is only used if the subclass definition file of a quark folder is placed in 
		the Extensions folder, _not_ as an alias, but as a symlink.
		*/
		^PathName("~/Quarks/").fullPath;
	}

	// of quarks in local, select those also present in userExtensionDir
	installed {
		^local.quarks select: { | q | this.installPath(q).pathMatch.notEmpty }
	}

	// IZ: user extension dir + local quark containing dir
	installDir { ^Platform.userExtensionDir +/+ local.path.basename }
	
	// IZ: user extension dir + local quark containing dir + quark name
	installPath { | q | ^this.installDir +/+ q.name; }
	
	// a gui for Quarks. 2007 by LFSaw.de
	// Mod of window height to fit all quarks list by IZ 201108
	gui {
		var	window, caption, explanation, views, resetButton, saveButton, warning,
			scrollview, scrB, flowLayout, /* quarksflow, */ height, maxPerPage, nextButton, prevButton;
		var	quarks;
		var pageStart = 0, fillPage;

		// note, this doesn't actually contact svn
		// it only reads the DIRECTORY entries you've already checked out
//		postf("RepQuarks gui - repos: %\n", repos.postln.quarks);
//		postf("RepQuarks gui - local: %\n", local.postln.quarks);
		quarks = this.repos.quarks.copy
			.sort({ |a, b| a.name < b.name });
		
		fillPage = { | start |
			scrollview.visible = false;
			if (views.notNil) {
				views.do({ |view| view.remove });
			};
			scrollview.decorator.reset;
			views = quarks collect: { | quark |
				var qView = QuarkView.new(scrollview, 500@20, quark,
					this.installed.detect{ | it | it == quark }.notNil
				);
				scrollview.decorator.nextLine;
				qView;
			};
			scrollview.visible = true;
			views
		};

		scrB = GUI.window.screenBounds;
//		height = min(quarks.size * 25 + 120, scrB.height - 60);
//		IZ mod: add more vertical space to fit all quarks in the pane
		height = min(quarks.size * 25 + 170, scrB.height - 60);
		window = GUI.window.new(this.name, Rect.aboutPoint( scrB.center, 250, height.div( 2 )));
		flowLayout = FlowLayout( window.view.bounds );
		window.view.decorator = flowLayout;

		caption = GUI.staticText.new(window, Rect(20,15,400,30));
		caption.font_( GUI.font.new( GUI.font.defaultSansFace, 24 ));
		caption.string = this.name;
		window.view.decorator.nextLine;

		if ( quarks.size == 0 ){
			GUI.button.new(window, Rect(0, 0, 229, 20))
			.states_([["checkout Quarks DIRECTORY", Color.black, Color.gray(0.5)]])
			.action_({ this.checkoutDirectory; });
		}{
			GUI.button.new(window, Rect(0, 0, 229, 20))
			.states_([["update Quarks DIRECTORY", Color.black, Color.gray(0.5)]])
			.action_({ this.updateDirectory;});
		};

		GUI.button.new(window, Rect(0, 0, 200, 20))
		.states_([["refresh Quarks listing", Color.black, Color.gray(0.5)]])
		.action_({
			window.close;
			this.gui;
		});

		window.view.decorator.nextLine;

		GUI.button.new(window, Rect(0, 0, 150, 20))
			.states_([["browse all help", Color.black, Color.gray(0.5)]])
			.action_({
				var path;
				path = this.local.path;
				Help(path[0..path.size-2]).gui
			});

		// add open directory button (open is only implemented in OS X)
		if (thisProcess.platform.name == \osx) {
			GUI.button.new(
				window, 
				Rect(15,15,150,20)
			).states_([["open quark directory", Color.black, Color.gray(0.5)]]
			).action = {
				"open %".format(this.local.path.escapeChar($ )).unixCmd;
			};
		};

		resetButton = GUI.button.new(window, Rect(15,15,75,20));
		resetButton.states = [
			["reset", Color.black, Color.gray(0.5)]
		];
		resetButton.action = { views.do(_.reset); };

		saveButton = GUI.button.new(window, Rect(15,15,75,20));
		saveButton.states = [
			["save", Color.black, Color.blue(1, 0.5)]
		];
		saveButton.action = { arg butt;
			Task {
				warning.string = "Applying changes, please wait";
				warning.background_(Color(0.7, 0.0, 0.0));
				0.1.wait;
				views.do{ | qView |
					qView.toBeInstalled.if({
						this.install(qView.quark.name);
						qView.flush
					});
					qView.toBeDeinstalled.if({
						this.uninstall(qView.quark.name);
						qView.flush;
					})
				};
				warning.string = "Done. You should now recompile sclang";
				warning.background_(Color(0, 0.7, 0));
			}.play(AppClock);
		};

		window.view.decorator.nextLine;
		explanation = GUI.staticText.new(window, Rect(20,15,500,20));
		explanation.string = 
			"\"+\" -> installed, \"-\" -> not installed, \"*\" -> marked to install, \"x\" -> marked to uninstall";
		explanation.font = Font("Helvetica-Bold", 12); // Fit QT Gui default
		window.view.decorator.nextLine;

		warning = GUI.staticText.new(window, Rect(20,15,400,30));
		warning.font_( GUI.font.new( GUI.font.defaultSansFace, 18 ));

		window.view.decorator.nextLine;
		GUI.staticText.new( window, 492 @ 1 ).background_( Color.grey );		window.view.decorator.nextLine;

		flowLayout.margin_( 0 @0 ).gap_( 0@0 );
		scrollview = GUI.scrollView.new(window, 500 @ (height - 165))
			.resize_( 5 )
			.autohidesScrollers_(true);
		scrollview.decorator = FlowLayout( Rect( 0, 0, 500, quarks.size * 25 + 20 ));

		window.front;
		fillPage.(pageStart);
		^window;
	}
	
	install { | name, includeDependencies = true, checkoutIfNeeded = true |
		var q, deps, installed, dirname, quarksForDep;
		// rewritten as instance method:
//		var extendedDirname;	// IZ: includes base directory, for better structure of Extensions dir

		if (this.isInstalled(name)) {
			(name + "already installed").inform;
			^this
		};

		q = local.findQuark(name);
		if (q.isNil) {
			if (checkoutIfNeeded) {
				(name.asString + " not found in local quarks; checking out from remote ...").postln;
				this.checkout(name, sync: true);
				q = local.reread.findQuark(name);
				if (q.isNil) {
					Error("Quark" + name + "install: checkout failed.").throw;
				};
			}
			{
				Error(name.asString + "not found in local quarks.  Not yet downloaded from the repository ?").throw;
			};
		};

		if (q.isCompatible.not) {
			(q.name + " reports that it is not compatible with your current class library.  See the help file for further information.").inform;
			^this
		};

		// create /quarks/ directory if needed
		if (this.repos.checkDir.not) { this.checkoutDirectory };

		// Now ensure that the dependencies are installed 
		// (if available given the current active reposses)
		if (includeDependencies) {
			q.dependencies(true).do({ |dep|
				quarksForDep = if(dep.repos.isNil, {this}, {Quarks.forUrl(dep.repos)});
				if(quarksForDep.isNil, {
					("Quarks:install - unable to find repository for dependency '" ++ dep.name
						++ "' - you may need to satisfy this dependency manually. No repository detected locally with URL " ++ dep.repos).warn;
				}, {
					if(quarksForDep.isInstalled(dep.name).not, {
						try({
							quarksForDep.install(dep.name, false, checkoutIfNeeded)
						}, {
							("Unable to satisfy dependency of '"++name++"' on '"++dep.name
								++"' - you may need to install '"++dep.name++"' manually.").warn;
						});
					});
				});
			});
		};

		// Ensure the correct folder-hierarchy exists first
		
		// IZ Adding enclosing dir for grouping: 
		// dirname = (Platform.userExtensionDir +/+ local.name +/+ q.path).dirname;
		
		if (File.exists(this.installDir).not) {
			systemCmd("mkdir -p " + this.installDir.escapeChar($ ));
		};

		// install via symlink to Extensions/<quark-group-dir>/<quarks-dir>
		systemCmd("ln -s " 
			+ (local.path +/+ q.path).escapeChar($ ) 
			+ this.installPath(q).escapeChar($ ));
		inform(q.name + "installed");
	}

	uninstall { | name |
		var q, deps, installed;
		name = name.asString;
		if(this.isInstalled(name).not,{
			^this
		});

		q = local.findQuark(name);
		if(q.isNil,{
			Error(
				name +
				"is not found in Local quarks in order to look up its relative path.  You may remove the symlink manually."
			).throw;
		});

		// remove symlink file
		systemCmd("rm " + this.installPath(q).escapeChar($ ));
		(q.name + "uninstalled").inform;
	}

}

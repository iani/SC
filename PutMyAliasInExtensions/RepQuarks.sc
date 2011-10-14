/*

MC / IZ 201109-10

Repository Quarks: Manage repository quarks, that is quarks residing in local directories anywhere in the system. 
These quarks may be parts of Git repositories, or may just be in any local folder. 

RepQuarks builds a menu that has one item for each rep-quark directory found in the system. Each item in the menu opens a GUI window for installing or un-installing any quarks in the rep-quark directory. A rep-quark repository is a folder containing quarks, and uses exactly the same format as a regular Quarks repository (a "DIRECTORY" folder which lists and gives info for the quarks included, see also Quarks help file).

There are two alternative ways to add a quark-directory to the RepQuarks menu: 

1. 	_Either:_ Place the quark directory inside the user application support directory
 	(~/Library/Application Support/SuperCollider),
 	- at the root level, 
 	- and name it quarks.<anything>, 
 		where <anything> is a name of your choice. 
 	The name of your choice will appear in the menu. 

2. 	_Or:_ Define an empty subclass of RepQuarks and use it in the following way: 
	2.1 	Place he definition file of the subclass inside the directory of the quarks that you want 
		to include. The definition file should be at the top level of your quarks directory. 
	2.2. Make an alias of the definition file and place it inside the Extensions folder in the
		user application support directory. This should be a regular MacOS X alias made in the Finder,
		not a symlink. If you use symlinks, then you should provide the path to the quark folder
		through the class method *quarkPath of your subclass of RepQuarks.

*/

RepQuarks : Quarks {
	classvar <menu, <menuName = "Quarks";

	*initClass {
		StartUp add: {
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
		this.addToMenu("quarks", { Quarks.gui });
		this.addToMenu("sc3-plugins", {
			Quarks(localPath: (Platform.userAppSupportDir +/+ "sc3-plugins")).gui
		});
	}

	*addToMenu { | itemName, action |
		SCMenuItem(menu, itemName).action = action;
	}

	*makeMenu {
		var path, pathMatch;
		path = this.getQuarkPath;
		SCMenuSeparator(menu, menu.children.size);
		pathMatch = (path ++ "quarks.*").pathMatch;
		if (pathMatch.size > 0) {
			^pathMatch do: { | p |
				this.addToMenu(p.basename[7..], { this.new(localPath: p).gui; });
			}
		};
		pathMatch = (path ++ "*").pathMatch select: { | p |
			this.isQuarkFolder(p +/+ "/");	
		};
		if (pathMatch.size > 0) {
			^pathMatch do: { | p |
				this.addToMenu(p.basename, { this.new(localPath: p).gui; });
			}
		};
		if (this.isQuarkFolder(path)) {
			^this.addToMenu(this.name.asString, { this.new(localPath: path).gui; });
		};
		path = path ++ "Quarks/";
		if (this.isQuarkFolder(path)) {
			^this.addToMenu(this.name.asString, { this.new(localPath: path).gui; });
		};
		(path ++ "*").pathMatch do: { | p |
			this.addToMenu(p.basename, { this.new(localPath: p).gui; });
		};
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
		path = this.filenameSymbol.asString.pathOnly;
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

	// a gui for Quarks. 2007 by LFSaw.de
	// Mod of window height to fit all quarks list by IZ 201108
	gui {
		var	window, caption, explanation, views, resetButton, saveButton, warning,
			scrollview, scrB, flowLayout, /* quarksflow, */ height, maxPerPage, nextButton, prevButton;
		var	quarks;
		var pageStart = 0, fillPage;
		
		fillPage = { | start |
			scrollview.visible = false;
			views.notNil.if({
				views.do({ |view| view.remove });
			});
			scrollview.decorator.reset;
			views = quarks collect: { | quark |
				var qView = QuarkView.new(scrollview, 500@20, quark,
					this.installed.detect{|it| it == quark}.notNil);
				scrollview.decorator.nextLine;
				qView;
			};
			scrollview.visible = true;
			views
		};

		// note, this doesn't actually contact svn
		// it only reads the DIRECTORY entries you've already checked out
//		postf("RepQuarks gui - repos: %\n", repos.postln.quarks);
//		postf("RepQuarks gui - local: %\n", local.postln.quarks);
		quarks = this.repos.quarks.copy
			.sort({ |a, b| a.name < b.name });

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
			.action_({ Help(this.local.path).gui });

		// add open directory button (open is only implemented in OS X)
		(thisProcess.platform.name == \osx).if{
			GUI.button.new(window, Rect(15,15,150,20)).states_([["open quark directory", 
				Color.black, Color.gray(0.5)]]).action_{ arg butt;
				"open %".format(this.local.path.escapeChar($ )).unixCmd;
			};
		};

		resetButton = GUI.button.new(window, Rect(15,15,75,20));
		resetButton.states = [
			["reset", Color.black, Color.gray(0.5)]
		];
		resetButton.action = { arg butt;
			views.do(_.reset);
		};

		saveButton = GUI.button.new(window, Rect(15,15,75,20));
		saveButton.states = [
			["save", Color.black, Color.blue(1, 0.5)]
		];
		saveButton.action = { arg butt;
			Task{
				warning.string = "Applying changes, please wait";
				warning.background_(Color(1.0, 1.0, 0.9));
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
				warning.background_(Color(0.9, 1.0, 0.9));
			}.play(AppClock);
		};

		window.view.decorator.nextLine;
		explanation = GUI.staticText.new(window, Rect(20,15,500,20));
		explanation.string = 
			"\"+\" -> installed, \"-\" -> not installed, \"*\" -> marked to install, \"x\" -> marked to uninstall";
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
}

/* iz Thu 04 October 2012  5:33 PM EEST

Keep a history of n recent paths for an object. Create a dialog pane and dialog panels for opening / saving an objects data in one of the paths chosen from the

The history is stored in the default archive (Platform.userAppSupportDir +/+ "archive.sctxar"), in a dictionary under a symbol which is the objectID.

The objectID is used to identify the object that is using the path history, keeping the id immutable between compile sessions. The simplest case is to use the Class itself as ID and let RecentPaths turn the class name into a symbol which server as objectID.

See also: Prefs.

RecentPaths.open(\test, { | thePath | thePath.postln; });

Example using RecentPaths: ScriptLib;

RecentPaths are not stored in the global archive but in a separate file, because they would be lost if the library is loaded without the RecentPaths class installed.

TODO: Add delete button.

*/

RecentPaths {
	classvar <all; // all RecentPaths instances, by objectID
	// root path at Library for instances stored by their paths:
	classvar <libPath = 'RecentPathsLoadedInstances';

	var <objectID, <>numHistoryItems = 20, <paths, <default;

	*open { | objectID, openAction, createAction |
		^this.new(objectID.asSymbol).open(openAction, createAction);
	}

	*save { | objectID, action |
		^this.new(objectID.asSymbol).save(action);
	}

	*new { | objectID, numHistoryItems = 20 |
		var instance;
		all ?? { all = Object.readArchive(this.archiveFilePath); };
		all ?? { all = IdentityDictionary(); };
		instance = all[objectID];
		instance ?? {
			instance = this.newCopyArgs(objectID, numHistoryItems).init;
			all[objectID] = instance;
			this.saveAll;
		};
		^instance;
	}

	init { paths = List.new; }

	default_ { | argDefault |
		default = argDefault;
		postf("Setting default path for: %\nPath is: %\n", objectID, argDefault);
		this.class.saveAll;
	}

	*saveAll {
		all.writeArchive(this.archiveFilePath);
	}

	*archiveFilePath { ^Platform.userAppSupportDir +/+ "RecentPaths.sctxar" }

	open { | openAction, createAction |
		var buttons;
		buttons add: StaticText().string_("... or choose a path from the recent paths below:");
		AppModel().window({ | window, app |
			window.bounds = window.bounds.left_(250).width_(800);
			window.name = format("Select path for: %", objectID);
			buttons = Array(3);
			// Button for creating new instance, without selecting any path
			createAction !? {
				buttons add: Button().states_([["Create new"]])
				.action_({ createAction.(this); window.close; });
			};
			// Button for loading an instance from path selected by user via open panel dialog
			buttons add: Button().states_([["Open from disc ... "]]).action_({
				this.openPanel(openAction, window);
			});
			buttons add: StaticText().string_("... or select from list below:");
			window.layout = VLayout(
				HLayout(*buttons),
				// List of recent paths.
				app.listView(\paths).items_(paths).view,
				HLayout(
					// Button for loading instance from path selected from recent paths list
					app.button(\paths).action_({ | me |
						this.selectExistingOrOpen(me.item, openAction);
						window.close;
					}).view.states_([["Accept"]]),
					app.button(\paths).action_({ window.close }).view.states_([["Cancel"]]),
				)
			)
		})
	}

	selectExistingOrOpen { | path, openAction |
		var existing;
		existing = this.getInstanceAtPath(path);
		if (existing.notNil) { ^existing };
		this.addInstanceAtPath(path, openAction.(path));
	}

	getInstanceAtPath { | path |
		^Library.at(libPath, path.asSymbol);
	}

	addInstanceAtPath { | path, instance |
		// retrospective correction of already saved instances from older version:
		[this, thisMethod.name, instance].postln;
		if (paths.isKindOf(List).not) { paths = List.newUsing(paths); };
		paths.remove(paths detect: { | p | p == path });
		paths add: path;
		if (paths.size > numHistoryItems) { paths.pop };
		Library.put(libPath, path.asSymbol, instance);
		this.addNotifier(instance, \objectClosed, { Library.put(libPath, path.asSymbol, nil); [instance, "closed"].postln; });
		this.class.saveAll;
		^instance;
	}

	openPanel { | action, window |
		Dialog.openPanel({ | path |
			this.selectExistingOrOpen(path, action);
			window.close;
		})
	}

	save { | action |
		Dialog.savePanel({ | path |
			action.(path);
			this addPath: path;
		});
	}
}

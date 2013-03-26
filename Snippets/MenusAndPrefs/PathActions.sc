/* iz Tue 26 March 2013 12:04 AM EET

A simpler to use version after RecentPaths.

Keep a history of n recent paths for a symbol, with actions for performing on a selected path.

The history is stored in the default archive (Platform.userAppSupportDir +/+ "PathLists.sctxar"), in a dictionary under a symbol which is the objectID.

The objectID is used to identify the object that is using the path history, keeping the id immutable between compile sessions.

PathList(\test2).openDialog;

Example using RecentPaths: ScriptLib;

See also: Prefs, RecentPaths.

*/

PathList {
	classvar <all; // all PathList instances, by objectID
	// root path at Library for objects stored by their paths:
	classvar <libPath = 'PathLists';

	var <objectID, <>numHistoryItems = 20, <paths, <default;

	// Find or create an instance holding the recent paths for objectID
	*new { | objectID, numHistoryItems = 20 |
		var instance;
		objectID = objectID.asSymbol;
		all ?? { all = Object.readArchive(this.archiveFilePath); };
		all ?? { all = IdentityDictionary(); };
		instance = all[objectID];
		instance ?? {
			instance = this.newCopyArgs(objectID, numHistoryItems, List.new);
			all[objectID] = instance;
			this.saveAll;
		};
		^instance;
	}

	*archiveFilePath { ^Platform.userAppSupportDir +/+ libPath ++ ".sctxar" }

	*saveAll { all.writeArchive(this.archiveFilePath);  /* "Recent paths saved".postln; */ }

	openDialog { | action |
		var buttons;
		action = action ?? { { | path | path.postln; } };
		AppModel().window({ | window, app |
			window.bounds = window.bounds.left_(250).width_(800);
			window.name = format("Select path for: %", objectID);
			buttons = Array(2);
			// Button for loading an instance from path selected by user via open panel dialog
			buttons add: Button().states_([["Open from disc ... "]]).action_({
				Dialog.openPanel({ | path |
					this.addPath(path);
					this.doActionAndClose(path, action, window);
				})
			});
			buttons add: StaticText().string_("... or select from list below:");
			window.layout = VLayout(
				HLayout(*buttons),
				// List of recent paths.
				app.listView(\paths).items_(paths).view,
				HLayout(
					// Button for loading instance from path selected from recent paths list
					app.button(\paths).action_({ | me |
						this.doActionAndClose(me.item, action, window);
					}).view.states_([["Accept"]]),
					app.button(\paths).action_({ window.close }).view.states_([["Cancel"]]),
				)
			)
		})
	}

	addPath { | path |
		paths add: path;
		if (paths.size > numHistoryItems) { paths.remove(paths@0) };
		this.class.saveAll;
	}

	doActionAndClose { | path, action, window |
		action.(path);
		window.close;
	}

	default_ { | argDefault |
		default = argDefault;
		postf("Setting default path for: %\nPath is: %\n", objectID, argDefault);
		this.class.saveAll;
	}
}

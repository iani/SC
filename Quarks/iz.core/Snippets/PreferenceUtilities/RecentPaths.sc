/* iz Thu 04 October 2012  5:33 PM EEST

Keep a history of n recent paths for an object. Create a dialog pane and dialog panels for opening / saving an objects data in one of the paths chosen from the

The history is stored in the default archive (Platform.userAppSupportDir +/+ "archive.sctxar"), in a dictionary under a symbol which is the objectID.

The objectID is used to identify the object that is using the path history, keeping the id immutable between compile sessions. The simplest case is to use the Class itself as ID and let RecentPaths turn the class name into a symbol which server as objectID.

See also: Prefs.

RecentPaths.open(\test, { | tehPath | tehPath.postln; });

Example using RecentPaths: ScriptLib;

RecentPaths are not stored in the global archive but in a separate file, because they would be lost if the library is loaded without the RecentPaths class installed.

*/

RecentPaths {
	classvar <all;

	var <objectID, <>numHistoryItems = 20, <paths;

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
			instance = this.newCopyArgs(objectID, numHistoryItems);
			all[objectID] = instance;
			this.saveAll;
		};
		^instance;
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
			createAction !? {
				buttons add: Button().states_([["Create new"]])
				.action_({ createAction.(this); window.close; });
			};
			buttons add: Button().states_([["Open from disc ... "]]).action_({
				this.openPanel(openAction, window);
			});
			buttons add: StaticText().string_("... or select from list below:");
			window.layout = VLayout(
				HLayout(*buttons),
				app.listView(\paths).items_(paths).view,
				HLayout(
					app.button(\paths).action_({ | me |
						openAction.(me.item);
						window.close;
					}).view.states_([["Accept"]]),
					app.button(\paths).action_({ window.close }).view.states_([["Cancel"]]),
				)
			)
		})
	}

	openPanel { | action, window |
		Dialog.openPanel({ | path |
			var preexisting;
			this addPath: path;
			action.(path);
			window.close;
		})
	}

	addPath { | path |
		paths = paths ?? { [] };
		paths.remove(paths detect: { | p | p == path });
		paths add: path;
		if (paths.size > numHistoryItems) { paths.pop };
		this.class.saveAll;
	}

	save { | action |
		Dialog.savePanel({ | path |
			action.(path);
			this addPath: path;
		});
	}
}

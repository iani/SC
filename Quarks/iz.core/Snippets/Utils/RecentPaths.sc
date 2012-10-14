/* iz Thu 04 October 2012  5:33 PM EEST

Keep a history of n recent paths for an object. Create a dialog pane and dialog panels for opening / saving an objects data in one of the paths chosen from the 

The history is stored in the default archive (Platform.userAppSupportDir +/+ "archive.sctxar"), in a dictionary under a symbol which is the objectID. 

The objectID is used to identify the object that is using the path history, keeping the id immutable between compile sessions. The simplest case is to use the Class itself as ID and let RecentPaths turn the class name into a symbol which server as objectID. 

See also: Prefs. 


RecentPaths.open(\test, { | tehPath | tehPath.postln; });

Example using RecentPaths: ScriptLib;

*/

RecentPaths {
	classvar <all;

	var <objectID, <>numHistoryItems = 20, <paths;

	*open { | objectID, action |
		^this.new(objectID.asSymbol).open(action);
	}

	*save { | objectID, action |
		^this.new(objectID.asSymbol).save(action);
	}

	*new { | objectID, numHistoryItems = 20 |
		var instance;
		all = Archive.global.at('RecentPaths');
		all ?? {
			all = IdentityDictionary();
			Archive.global.put('RecentPaths', all);
		};
		instance = all[objectID];
		instance ?? {
			instance = this.newCopyArgs(objectID, numHistoryItems);
			all[objectID] = instance;
			Archive.write;
		};
		^instance;
	}
	
	open { | action |
		AppModel().window({ | window, app |
			window.bounds = window.bounds.left_(250).width_(800);
			window.name = format("Select path for: %", objectID);
			window.layout = VLayout(
				Button().states_([["Click to select new path ... "]]).action_({
					this.openPanel(action, window);
				}),
				StaticText().string_("... or select a path from the recent paths below:"),
				app.listView(\paths).items_(paths).view,
				HLayout(
					app.button(\paths).action_({ | me |
						action.(me.item);
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
		Archive.write;		
	}

	save { | action |
		Dialog.savePanel({ | path |
			action.(path);
			this addPath: path;
		});
	}
}

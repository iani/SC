/* iz Thu 04 October 2012  8:09 AM EEST

Facilitate the saving of data between sessions.

Stores selected preferences in Archive.global, using the object that they belong to, and the symbol that names each preference as path.  

If a preference or a set of preferences have not been found, provides a dialog window for setting it, and then performs an action with the object and the preferences 

Example: 

/* Open path dialog the first time. After that, return already saved path */
Prefs.getPath(Object, \path, { | path | path.postln; }); 


Prefs.getPathList(\test); // One can store preferences for Classes or Symbols

Prefs.dialog(\test, [\path, nil, _.postln], [\x, 100, _.postln], [\y, 200, _.postln])

*/

Prefs : AppModel {
	
	classvar <allPrefs, <>prefPath;

	var <object, <prefSpecs, <myPrefs;

	*initClass {
		StartUp.add { this.loadPrefsFromArchive };
		ShutDown.add { this.savePrefsToArchive };
	}

	*loadPrefsFromArchive {
		if (File.exists(this.archiveFilePath)) {
			allPrefs = Object.readArchive(this.archiveFilePath);
		}{
			allPrefs = IdentityDictionary();
			this.savePrefsToArchive;
		};
	}

	*archiveFilePath { ^prefPath ?? { Platform.userAppSupportDir +/+ "Prefs.sctxar" } }

	*savePrefsToArchive { allPrefs.writeTextArchive(this.archiveFilePath); }

	*openPanel { | object, pathPrefName = \path, action, multipleSelection = false |
		var pref, path;
		pref = this.new(object, [[pathPrefName, nil, action]]);
		if ((path = pref.getPref(pathPrefName)).isNil) {
			Dialog.openPanel({ | argPaths |
				pref.storePrefInObject(pathPrefName, argPaths, action);
				this.savePrefsToArchive;
			}, multipleSelection)
		}{
			pref.storePrefInObject(pathPrefName, path, action);
		};
	}

	getPref { | prefName | ^myPrefs[prefName] }

	*getPrefs { | object ... prefSpecs |
		// get the allPrefs specified by prefSpecs and put them in object without a dialog. 
		// Pref values are read from Archive.global, or from defaults in prefSpecs, if present
		^this.new(object, prefSpecs).setPrefs;
	}

	*dialog { | object ... prefSpecs |
		// Open a dialog to set or modify allPrefs
		^this.new(object, prefSpecs).dialog;
	}

	*new { | object, prefSpecs | ^super.new(object, prefSpecs).getPrefs; }

	getPrefs {
		// get preferences from archive, use defaults if needed and provided
		var prefName, default;
		myPrefs = allPrefs.at(object.asSymbol);
		if (myPrefs.isNil) {
			myPrefs = IdentityDictionary();
			allPrefs[object.asSymbol] = myPrefs;
		};
		prefSpecs do: { | pSpec |
			#prefName, default = pSpec.asArray;
			myPrefs[prefName] = myPrefs[prefName] ? default;
		};
	}

	dialog {
		this.window({ | window |
			var statusMessage;
			window.bounds = Rect(300, 300, 600, 400);
			window.layout = VLayout(
				VLayout(*(prefSpecs collect: this.prefGui(_))),
				HLayout(
					Button().states_([["OK"]]).action_({
						this.setPrefs;
						statusMessage.string = "Preferences saved. Close window to store on disc.";
					}),
					Button().states_([["REVERT"]]).action_({
						this.revert;
						statusMessage.string = "Preferences restored from disc.";
					}),
					Button().states_([["CANCEL"]]).action_({ window.close; }),
				),
				statusMessage = StaticText(),
			);
			this.windowClosed(window, { this.savePrefs });
		})
	}

	prefGui { | prefSpec |
		var prefName, prefNameString, rest;
		#prefName ... rest = prefSpec.asArray;
		prefNameString = prefName.asString;
		^HLayout(
			StaticText().string_(prefNameString),
			this.makeOpenPanelButtonIfAppropriate(prefNameString, prefName),
			this.textField(prefName).makeStringGetter.string_(myPrefs[prefName].asCompileString)
				.view
		);
	}

	makeOpenPanelButtonIfAppropriate { | prefNameString, prefName |
		if (prefNameString[prefNameString.size - 5..].postln == "path") {
			^Button().states_([["choose..."]]).action_({ | view |
				Dialog.openPanel({ | path |
					this.getValue(prefName).string_(path.asCompileString);
				})
			});
		}{
			^StaticText();	// minimizes the width taken in HLayout. Better than nil.
		}
	}

	setPrefs {
		// store allPrefs in object and archive. 
		// is called after prefs have been set from archive or dialog.
		var prefName, default, action;
		prefSpecs do: { | pSpec |
			#prefName, default, action = pSpec.asArray;
			this.storePrefInObject(prefName, this.getValue(prefName).getString.interpret, action);
		};
	}

	storePrefInObject { | prefName, val, action |
		myPrefs[prefName] = val;
		(action ?? { object.perform(prefName.asSetter, val) }).(val, object);
	}

	savePrefs {
		this.class.savePrefsToArchive;
		postf("Preferences saved for %\n", object);
	}

	revert {
		this.class.loadPrefsFromArchive;
		this.getPrefs;
		myPrefs keysValuesDo: { | key, value |
			this.getValue(key).string = value.asCompileString;
		};
	}

}
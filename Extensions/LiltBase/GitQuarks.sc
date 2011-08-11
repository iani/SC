
GitQuarks : Quarks {
	// install so that it can be distributed through git: 
	// copy code instead of creating a link. 
	// For uninstall use rm -rf to remove directory instead of simple rm which works for alias
	
	*initClass {
		Class.initClassTree(MenuHandler);	
		this.addMenu;	
	}
	
	
	*menuItems {
		^[
			CocoaMenuItem.addToMenu("Code", "Configure core quarks", nil, {
				GitQuarks(localPath: Platform.userAppSupportDir +/+ "quarks.local.core").gui;
			}),
			CocoaMenuItem.addToMenu("Code", "Configure project quarks", nil, {
				GitQuarks(localPath: Platform.userAppSupportDir +/+ "quarks.local.projects").gui;
			}),
			CocoaMenuItem.addToMenu("Code", "Configure draft quarks", nil, {
				GitQuarks(localPath: Platform.userAppSupportDir +/+ "quarks.local.drafts").gui;
			}),
			CocoaMenuItem.addToMenu("Code", "Save local quarks", nil, {
				this.saveLocalQuarks;
			})
		]
	}
	
	install { | name , includeDependencies=true, checkoutIfNeeded=true |
		var q, deps, installed, dirname, quarksForDep;

		if(this.isInstalled(name),{
			(name + "already installed").inform;
			^this
		});

		q = local.findQuark(name);
		if(q.isNil,{
			if(checkoutIfNeeded) {
				(name.asString + " not found in local quarks; checking out from remote ...").postln;
				this.checkout(name, sync: true);
				q = local.reread.findQuark(name);
				if(q.isNil, {
					Error("Quark" + name + "install: checkout failed.").throw;
				});
			}
			{
				Error(name.asString + "not found in local quarks.  Not yet downloaded from the repository ?").throw;
			};
		});

		if(q.isCompatible.not,{
			(q.name + " reports that it is not compatible with your current class library.  See the help file for further information.").inform;
			^this
		});

		// create /quarks/ directory if needed
		if(this.repos.checkDir.not){this.checkoutDirectory};

		// Now ensure that the dependencies are installed (if available given the current active reposses)
		if(includeDependencies, {
			q.dependencies(true).do({ |dep|
				quarksForDep = if(dep.repos.isNil, {this}, {Quarks.forUrl(dep.repos)});
				if(quarksForDep.isNil, {
					("Quarks:install - unable to find repository for dependency '" ++ dep.name
						++ "' - you may need to satisfy this dependency manually. No repository detected locally with URL "++dep.repos).warn;
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
		});

		// Ensure the correct folder-hierarchy exists first
		dirname = (Platform.userExtensionDir +/+  local.name +/+ q.path).dirname;
		if(File.exists(dirname).not, {
			("mkdir -p " + dirname.escapeChar($ )).systemCmd;
		});

		// install via copy -r to Extensions/<quarks-dir>
		("cp -r " +  (local.path +/+ q.path).escapeChar($ ) +  (Platform.userExtensionDir +/+ local.name +/+ q.path).escapeChar($ )).systemCmd;
		(q.name + "installed").inform;
//		{ thisProcess.recompile }.defer(3);
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

		// Uninstall by removing the folder recursively.
		("rm -rf " +  (Platform.userExtensionDir +/+ local.name +/+ q.path).escapeChar($ )).systemCmd;
		(q.name + "uninstalled").inform;
	}

	*saveLocalQuarks {
		var script = "echo 'copying quarks from Extensions to archive ...' ", path;
		var quarkgroups = #["core", "projects", "drafts"];
		quarkgroups do: { | group |
			path = format("%/quarks.local.%/*", Platform.userExtensionDir, group).escapeChar($ );
			if (path.pathMatch.size > 0) {
				script = script + format("&& cp -r % %", path, 
					format(format("%/quarks.local.%/", Platform.userAppSupportDir, 
						group)).escapeChar($ );
				);
			};
		};
		script = script ++ " && echo '... quark copy DONE'";
		script.unixCmd;
	}
}

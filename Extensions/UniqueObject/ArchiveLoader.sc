/*
Load some objects selecting from a dictionary of sessions saved on the archive at Platform.userAppSupportDir ++ "/archive.sctxar",



*/

ArchiveLoader : ListWindow {	
	*new { | archiveRootKey = 'sessions', loadFunc |
		^super.new(archiveRootKey, nil, 
			{
				var dict, items;
				dict = Archive.global.at(archiveRootKey);
				if (dict.isNil) {
					["---"-> {}]
				}{
					dict sortedKeysValuesDo: { | key, value |
						items = items add: key->{ loadFunc.(value) };
					};
					items;
				}
			}
		);	
	}
}
